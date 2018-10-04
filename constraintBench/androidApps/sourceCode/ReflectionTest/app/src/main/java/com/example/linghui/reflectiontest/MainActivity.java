package com.example.linghui.reflectiontest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.telephony.SmsManager;
import android.widget.TextView;

import java.lang.reflect.Field;

public class MainActivity extends AppCompatActivity {

    private String loco = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
            return;
        }
        final String device = Build.DEVICE;
        final Location lastKnownLocation;
        if (!device.equals("BLUB")){
            lastKnownLocation= locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        } else {
            lastKnownLocation =null;
        }
        final TextView hello = findViewById(R.id.hello);
        try {
            //smuggle location to sink via reflection
            final Field locoField = this.getClass().getDeclaredField("loco");
            locoField.setAccessible(true);
            locoField.set(this, lastKnownLocation.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            hello.setText(e.getMessage());
            e.printStackTrace();
        }

        if ( Build.VERSION.SDK_INT <=26)
        {
            SmsManager smsManager = SmsManager.getDefault();
            hello.setText(loco);
            if ( Build.VERSION.SDK_INT <26) {
                smsManager.sendTextMessage(null, null, loco,null, null);
            }
        }
    }
}
