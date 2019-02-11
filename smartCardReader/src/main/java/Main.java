import com.oracle.tools.packager.Log;

import javax.net.ssl.HttpsURLConnection;
import javax.smartcardio.*;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.List;

public class Main {

    public static void main(String[] args) throws Exception {
        TerminalFactory terminalFactory = TerminalFactory.getDefault();
        List<CardTerminal> terminals = terminalFactory.terminals().list();
        CardTerminal terminal = terminals.get(0);

        String postURL = "https://5qulm7recj.execute-api.eu-west-2.amazonaws.com/dev/nfc";

        String body = "{ \"nfc\": { \"key\": \"replace\" }}";


        while (true) {
            if (terminal.isCardPresent()) {
                try {
                    Card card = terminal.connect("T=0");
                    byte[] b = Utils.hexStringToByteArray("00A4040007A0000002471001");
                    CommandAPDU commandAPDU = new CommandAPDU(b);
                    ResponseAPDU responseAPDU = card.getBasicChannel().transmit(commandAPDU);
                    card.disconnect(false);
                    terminal.waitForCardAbsent(10000L);
                    if (responseAPDU.toString().equals("EXIT")) {
                        break;
                    }
                    unlockDoor(new String(responseAPDU.getBytes()), postURL, body);
                    card.disconnect(false);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.debug(e.getMessage());
                }
            }
        }
    }

    private static void unlockDoor(String key, String url, String body) throws IOException {
        body = body.replace("replace", key);

        URL url1 = new URL(url);

        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url1.openConnection();
        httpsURLConnection.setRequestMethod("POST");
        httpsURLConnection.setDoOutput(true);

        OutputStream outputStream = httpsURLConnection.getOutputStream();

        outputStream.write(body.getBytes());

        outputStream.flush();
        outputStream.close();

        int responseCode = httpsURLConnection.getResponseCode();

    }
}
