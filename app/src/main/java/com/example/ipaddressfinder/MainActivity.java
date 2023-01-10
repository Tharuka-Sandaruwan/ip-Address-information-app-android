package com.example.ipaddressfinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;


import androidx.appcompat.app.AlertDialog;


import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.format.Formatter;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Collections;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private EditText editTextPvtIP;
    private EditText editTextPbIP;
    private EditText editTextCity;
    private EditText editTextRegion;
    private EditText editTextCountry;
    private EditText editTextTimeZone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editTextPvtIP = findViewById(R.id.editTextPvtIP);
        editTextPbIP = findViewById(R.id.editTextPbIP);
        editTextCity = findViewById(R.id.editTextCity);
        editTextRegion = findViewById(R.id.editTextRegion);
        editTextCountry = findViewById(R.id.editTextCountry);
        editTextTimeZone = findViewById(R.id.editTextTimeZone);
        editTextPvtIP.setText(getPrivateIP());

    }

    private String getPrivateIP() {
        String privateIP = "";
        final WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        if (wm.isWifiEnabled()) {
            WifiInfo wifiInf = wm.getConnectionInfo();
            privateIP = Formatter.formatIpAddress(wifiInf.getIpAddress());
        } else {
            final ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (networkInfo.isConnected()) {
                try {
                    List<NetworkInterface> networkInterfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
                    for (NetworkInterface networkInterface : networkInterfaces) {
                        List<InetAddress> inetAddresses = Collections.list(networkInterface.getInetAddresses());
                        for (InetAddress inetAddress : inetAddresses) {
                            if (!inetAddress.isLoopbackAddress()) {
                                privateIP = inetAddress.getHostAddress().toUpperCase();
                            }
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            else{
                privateIP="";
                new AlertDialog.Builder(this)
                        .setTitle("Require Internet")
                        .setMessage("This App Requires Internet Connectivity. Please connect to the Internet and press OK")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                if(wm.isWifiEnabled()||cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()){
                                    try {
                                        Thread.sleep(2000);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    editTextPvtIP.setText(getPrivateIP());
                                }
                                else {
                                    getPrivateIP();
                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no,new DialogInterface.OnClickListener(){
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setCancelable(false)
                        .show();

            }
        }
        return privateIP;
    }

    public void getPublicIP(View view) {
        new GetMyPublicIP().execute();
    }
    public void getIPDetails(View view) {
        String publicIP = editTextPbIP.getText().toString();
        if (publicIP != null || !publicIP.equals("")) {
            new GetIPDetails(publicIP).execute();
        }
    }
}