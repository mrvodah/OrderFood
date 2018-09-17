package com.example.vietvan.orderfood;

import android.content.Intent;
import android.hardware.fingerprint.FingerprintManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.multidots.fingerprintauth.AuthErrorCodes;
import com.multidots.fingerprintauth.FingerPrintAuthCallback;
import com.multidots.fingerprintauth.FingerPrintAuthHelper;

public class LogInFingerPrint extends AppCompatActivity implements FingerPrintAuthCallback {

    private static final String TAG = "TAG";
    private FingerPrintAuthHelper mFingerPrintAuthHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_finger_print);

        mFingerPrintAuthHelper = FingerPrintAuthHelper.getHelper(this, this);

    }

    @Override
    protected void onResume() {
        super.onResume();
        //start finger print authentication
        mFingerPrintAuthHelper.startAuth();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFingerPrintAuthHelper.stopAuth();
    }

    @Override
    public void onNoFingerPrintHardwareFound() {
        //Device does not have finger print scanner.
        Toast.makeText(this, "Your device does not have finger print scanner", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onNoFingerPrintRegistered() {
        //There are no finger prints registered on this device.
        Toast.makeText(this, "There are no finger prints registered on this device." +
                " Please register your finger from settings", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(android.provider.Settings.ACTION_SECURITY_SETTINGS));
    }

    @Override
    public void onBelowMarshmallow() {
        //Device running below API 23 version of android that does not support finger print authentication.
        Toast.makeText(this, "You are running older version of android that does not support finger print authentication", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onAuthSuccess(FingerprintManager.CryptoObject cryptoObject) {
        //Authentication sucessful.
        Toast.makeText(this, "Successful!", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onAuthSuccess: " + cryptoObject.toString());
        Log.d(TAG, "onAuthSuccess: " + cryptoObject.getCipher().toString() +
                "/" + cryptoObject.getMac().toString() +
                "/" + cryptoObject.getSignature().toString());
    }

    @Override
    public void onAuthFailed(int errorCode, String errorMessage) {
        switch (errorCode) {    //Parse the error code for recoverable/non recoverable error.
            case AuthErrorCodes.CANNOT_RECOGNIZE_ERROR:
                //Cannot recognize the fingerprint scanned.
                Toast.makeText(this, "Cannot recognize your finger print. Please try again", Toast.LENGTH_SHORT).show();
                break;
            case AuthErrorCodes.NON_RECOVERABLE_ERROR:
                //This is not recoverable error. Try other options for user authentication. like pin, password.
                Toast.makeText(this, "Cannot initialize finger print authentication", Toast.LENGTH_SHORT).show();
                break;
            case AuthErrorCodes.RECOVERABLE_ERROR:
                //Any recoverable error. Display message to the user.
                Toast.makeText(this, "" + errorMessage, Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
