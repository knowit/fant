package no.miljohack.teammega.fant;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;


public class HostCardEmulatorService extends HostApduService {

    BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            assert action != null;
            if (action.equals("new_key")) {
                KEY = intent.getStringExtra("key");
            } else if (action.equals("open_complete")) {
                KEY = "Whatevs";
            }
        }
    };


    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter filter = new IntentFilter();
        filter.addAction("new_key");
        filter.addAction("open_complete");
        registerReceiver(mBroadcastReceiver, filter);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);

    }

    public final static String TAG = "Host Card Emulator";
    public final static String STATUS_SUCCESS = "9000";
    public final static String STATUS_FAILED = "6F00";
    public final static String CLA_NOT_SUPPORTED = "6E00";
    public final static String INS_NOT_SUPPORTED = "6D00";
    public final static String AID = "A0000002471001";
    public final static String SELECT_INS = "A4";
    public final static String DEFAULT_CLA = "00";
    public final static Integer MIN_APDU_LENGTH = 12;
    public static String KEY = "Whatevs";


    @Override
    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        if (commandApdu == null) {
            return Utils.hexStringToByteArray(STATUS_FAILED);
        }

        String hexCommandApdu = Utils.toHex(commandApdu);
        if (hexCommandApdu.length() < MIN_APDU_LENGTH) {
            return Utils.hexStringToByteArray(STATUS_FAILED);
        }

        if (!hexCommandApdu.substring(0, 2).equals(DEFAULT_CLA)) {
            return Utils.hexStringToByteArray(CLA_NOT_SUPPORTED);
        }

        if (!hexCommandApdu.substring(2, 4).equals(SELECT_INS)) {
            return Utils.hexStringToByteArray(INS_NOT_SUPPORTED);
        }

        if (hexCommandApdu.substring(10, 24).equals(AID)) {
            if (!KEY.equals("Whatevs")) {
                Intent intent = new Intent("open_complete");
                sendBroadcast(intent);

            } else {
                Intent intent = new Intent("no_code");
                sendBroadcast(intent);
            }
            return KEY.getBytes();
        } else {
            Intent intent = new Intent("no_access");
            sendBroadcast(intent);
            return Utils.hexStringToByteArray(STATUS_FAILED);
        }
    }

    @Override
    public void onDeactivated(int reason) {
        Log.d(TAG, "Deactivated: " + reason);
    }


}
