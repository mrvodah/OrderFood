package com.example.vietvan.orderfood.Common;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.vietvan.orderfood.Model.User;

/**
 * Created by VietVan on 29/05/2018.
 */

public class Common {
    public static User currentUser;

    public static String DELETE = "DELETE";
    public static String USER_KEY = "USER";
    public static String PW_KEY = "PASSWORD";

    public static String convertCodeToStatus(String code){
        if(code.equals("0"))
            return "Placed";
        else if(code.equals("1"))
            return "On my way";
        else
            return "Shipped";
    }

    public static boolean isConnectedToInternet(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if(connectivityManager != null){
            NetworkInfo[] infos = connectivityManager.getAllNetworkInfo();
            if(infos != null){
                for(int i=0;i<infos.length;i++){
                    if(infos[i].getState() == NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }

        return false;
    }
}
