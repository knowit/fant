import com.oracle.tools.packager.Log;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openssl.PEMReader;
import org.bouncycastle.openssl.PasswordFinder;
import org.eclipse.paho.client.mqttv3.*;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import javax.smartcardio.*;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.Security;
import java.security.cert.X509Certificate;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = terminalFactory.terminals().list();
        System.out.println("Terminal: " + terminals);
        CardTerminal terminal = terminals.get(0);

        IMqttClient publisher = connect();

        while (true) {
            if (terminal.isCardPresent()) {
                try {
                    Card card = terminal.connect("T=0");
                    byte[] b = Utils.hexStringToByteArray("00A4040007A0000002471001");
                    CommandAPDU commandAPDU = new CommandAPDU(b);
                    ResponseAPDU responseAPDU = card.getBasicChannel().transmit(commandAPDU);
                    card.disconnect(false);
                    terminal.waitForCardAbsent(10000L);
                    System.out.println(new String(responseAPDU.getBytes()));
                    if (responseAPDU.toString().equals("EXIT")) {
                        break;
                    }
                    unlockDoor(new String(responseAPDU.getBytes()), publisher);
                    card.disconnect(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.debug(e.getMessage());
                }
            }
        }
    }

    private static void unlockDoor(String key, IMqttClient client) throws MqttException {
        MqttMessage message = new MqttMessage(key.getBytes());
        client.publish("lock", message);
    }

    private static MqttClient connect() {
        String serverUrl = "ssl://a1wn77w8brnymu-ats.iot.eu-west-2.amazonaws.com:8883";
        String caFilePath = "/Users/didrikpemmeraalen/IdeaProjects/SmartCardReader/src/main/resources/AmazonRootCA1.pem";
        String clientCrtFilePath = "/Users/didrikpemmeraalen/IdeaProjects/SmartCardReader/src/main/resources/0fd094cc01-certificate.pem.crt";
        String clientKeyFilePath = "/Users/didrikpemmeraalen/IdeaProjects/SmartCardReader/src/main/resources/0fd094cc01-private.pem.key";
        MqttClient mqttClient = null;
        try {
            mqttClient = new MqttClient(serverUrl, "askeladden");
            MqttConnectOptions options = new MqttConnectOptions();
            options.setConnectionTimeout(60);
            options.setKeepAliveInterval(60);
            options.setMqttVersion(MqttConnectOptions.MQTT_VERSION_3_1);

            SSLSocketFactory socketFactory = getSocketFactory(caFilePath,
                    clientCrtFilePath, clientKeyFilePath, "");
            options.setSocketFactory(socketFactory);
            mqttClient.connect(options);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mqttClient;
    }

    private static SSLSocketFactory getSocketFactory(final String caCrtFile, final String crtFile, final String keyFile,
                                                     final String password) throws Exception {
        Security.addProvider(new BouncyCastleProvider());

        // load CA certificate
        PEMReader reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(caCrtFile)))));
        X509Certificate caCert = (X509Certificate) reader.readObject();
        reader.close();

        // load client certificate
        reader = new PEMReader(new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(crtFile)))));
        X509Certificate cert = (X509Certificate) reader.readObject();
        reader.close();

        // load client private key
        reader = new PEMReader(
                new InputStreamReader(new ByteArrayInputStream(Files.readAllBytes(Paths.get(keyFile)))),
                new PasswordFinder() {
                    @Override
                    public char[] getPassword() {
                        return password.toCharArray();
                    }
                }
        );
        KeyPair key = (KeyPair) reader.readObject();
        reader.close();

        // CA certificate is used to authenticate server
        KeyStore caKs = KeyStore.getInstance(KeyStore.getDefaultType());
        caKs.load(null, null);
        caKs.setCertificateEntry("ca-certificate", caCert);
        TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
        tmf.init(caKs);

        // client key and certificates are sent to server so it can authenticate us
        KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
        ks.load(null, null);
        ks.setCertificateEntry("certificate", cert);
        ks.setKeyEntry("private-key", key.getPrivate(), password.toCharArray(), new java.security.cert.Certificate[]{cert});
        KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
        kmf.init(ks, password.toCharArray());

        // finally, create SSL socket factory
        SSLContext context = SSLContext.getInstance("TLSv1.2");
        context.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

        return context.getSocketFactory();
    }


}
