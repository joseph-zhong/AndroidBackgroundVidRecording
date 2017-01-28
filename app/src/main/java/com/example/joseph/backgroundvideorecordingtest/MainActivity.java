package com.example.joseph.backgroundvideorecordingtest;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE = 1;
    private static final int REQUEST_CODE_GENERAL = 0;
    public static String MP4_FORMAT = ".mp4";
    public static File MOV_DIR = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES), "BackgroundVideoRecordingTest");

    public static File VID_DIR = new File(MOV_DIR, "test" + MP4_FORMAT);
    public static String VID_DIR_PATH = VID_DIR.getPath();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        /** check if we already  have permission to draw over other apps */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(getApplicationContext())) {
                /** if not construct intent to request permission */
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_CODE);
            }
            else {
                Log.v("VERBOSE", "Overlay permission is granted");
            }
        }

        if (checkForPermissions()) {
            start();
        }



//        stopService(new Intent(MainActivity.this, RecorderService.class));
    }

    public void start() {
        Intent intent = new Intent(MainActivity.this, RecorderService.class);
        intent.putExtra(RecorderService.INTENT_VIDEO_PATH, VID_DIR_PATH); //eg: "/video/camera/"
        startService(intent);
    }

    public boolean checkForPermissions() {
        ArrayList<String> permissionsToGrant = new ArrayList<>();

        // "Dangerous" Permissions:
        // http://stackoverflow.com/questions/36936914/list-of-android-permissions-normal-permissions-and-dangerous-permissions-in-api
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToGrant.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else {
                Log.v("VERBOSE", "External storage permission is granted");
            }
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissionsToGrant.add(Manifest.permission.CAMERA);
            }
            else {
                Log.v("VERBOSE", "Camera Permission granted");
            }
        }

        // permission is automatically granted on sdk<23 upon installation
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            permissionsToGrant.add(Manifest.permission.RECORD_AUDIO);
        }
        else {
            Log.v("VERBOSE", "Record Audio Permission granted");
        }

        if (permissionsToGrant.size() > 0) {
            ActivityCompat.requestPermissions(MainActivity.this, permissionsToGrant.toArray(new String[permissionsToGrant.size()]), 0);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.i("TESTING", "onRequestPermissions Called");
        Log.v("VERBOSE", "onRequestPermissions requestCode: " + requestCode);
        Log.v("VERBOSE", "onRequestPermissions permissions: " + Arrays.toString(permissions));
        Log.v("VERBOSE", "onRequestPermissions grantResults: " + Arrays.toString(grantResults));
        switch (requestCode) {
            case REQUEST_CODE_GENERAL: {
                Log.i("TESTING", "Permissions granted: Loading...");
                Toast.makeText(this, "Permissions granted: Loading...", Toast.LENGTH_SHORT).show();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && !Settings.canDrawOverlays(this)) {
                    Log.i("TESTING", "Cannot draw overlays");
                    return;
                }

                start();

                return;
            }
            default: {
                Log.e("ERROR", "Permissions denied: Cannot load UI");
                Toast.makeText(this, "Permissions denied: Cannot load UI", Toast.LENGTH_SHORT).show();
                return;
            }
        }
    }
}
