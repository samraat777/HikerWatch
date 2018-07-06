package com.example.sam.hikerwatch;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    LocationManager locationManager;
    LocationListener locationListener;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if(grantResults.length>0&&grantResults[0]==PackageManager.PERMISSION_GRANTED)
        {
         startListnening();
        }
    }
   public void startListnening()
   {
       if(ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED)
           locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
   }


    public void updateLocation(Location location)
    {
        Log.i("info",location.toString());

        TextView latText=(TextView)findViewById(R.id.latText);

        TextView lngText=(TextView)findViewById(R.id.lngText);

        TextView accuracyText=(TextView)findViewById(R.id.accuracyText);

        TextView altiText=(TextView)findViewById(R.id.altiText);

        DecimalFormat df=new DecimalFormat("#.####");
        df.setRoundingMode(RoundingMode.CEILING);


        latText.setText("Latitude: "+df.format(location.getLatitude()));

        lngText.setText("Longitude: "+df.format(location.getLongitude()));

        accuracyText.setText("Accuracy: "+location.getAccuracy());

        altiText.setText("Altitude: "+location.getAltitude());

        Geocoder geocoder=new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            String address="Could not find address";
            List<Address> listAddress=geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);

            if(listAddress!=null&&listAddress.size()>0)
            {
                address="Address: \n";

               if(listAddress.get(0).getSubThoroughfare()!=null)
                   address+=listAddress.get(0).getSubThoroughfare()+" ";
                if(listAddress.get(0).getThoroughfare()!=null)
                    address+=listAddress.get(0).getThoroughfare()+"\n";
                if(listAddress.get(0).getLocality()!=null)
                    address+=listAddress.get(0).getLocality()+"\n";
                if(listAddress.get(0).getPostalCode()!=null)
                    address+=listAddress.get(0).getPostalCode()+"\n";
                if(listAddress.get(0).getCountryName()!=null)
                    address+=listAddress.get(0).getCountryName();
            }

            TextView addressText=(TextView)findViewById(R.id.addressText);
            addressText.setText(address);
        }

        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationManager=(LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener=new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                updateLocation(location);

            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(Build.VERSION.SDK_INT<23)
        {
            startListnening();
        }
        else
        {

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

            else
            {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0,locationListener);
                Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(location!=null)
                {
                    updateLocation(location);
                }

            }
        }
    }
}
