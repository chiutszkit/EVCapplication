package com.example.tommyhui.evcapplication.util;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.tommyhui.evcapplication.R;

public class ConnectionDetector {

    private Context context;
    private boolean isShowGPS;
    private boolean isShowInternet;

    public ConnectionDetector(Context context){
        this.context = context;
        this.isShowGPS = false;
        this.isShowInternet = false;
    }

    /** To check whether there is Internet connection **/
    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        if(!isShowInternet) {
            showNoInternetDialog();
            isShowInternet = true;
        }
        return false;
    }

    /** To check whether there is GPS signal **/
    public boolean isConnectingToGPS(){
        final LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        if (manager.isProviderEnabled( LocationManager.GPS_PROVIDER ) ) {
            return true;
        }
        if(!isShowGPS) {
            showNoGPSDialog();
            isShowGPS = true;
        }
        return false;
    }

    /** Show alert dialog for no GPS Signal **/
    private void showNoGPSDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.alertDialog_no_gps_title);
        alertDialog.setMessage(R.string.alertDialog_no_gps_text);
        alertDialog.setNeutralButton(R.string.alertDialog_ok_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }

    /** Show alert dialog for no Internet connection **/
    private void showNoInternetDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context);
        alertDialog.setTitle(R.string.alertDialog_no_internet_title);
        alertDialog.setMessage(R.string.alertDialog_no_internet_text);
        alertDialog.setNeutralButton(R.string.alertDialog_ok_option, new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                // TODO Auto-generated method stub
            }
        });

        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}