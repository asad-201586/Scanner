package com.example.i_monitor;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    private static final int requestCode = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //FirstFragment fragment = new FirstFragment();
        SecondFragment fragment = new SecondFragment();
        fragmentTransaction.replace(R.id.fragment_container, fragment);
        fragmentTransaction.commit();

        //Camera permission need for QR Code Reading using Device Camera
        if(checkCameraPermission()){
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.CAMERA
            }, requestCode);
        }
    }

    private Boolean checkCameraPermission () {
         return (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED);
    }
}