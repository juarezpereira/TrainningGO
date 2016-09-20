package com.projeto.trainninggo.Base;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.projeto.trainninggo.R;
import com.projeto.domain.Model.ChargingLocation;
import com.projeto.trainninggo.Utils.PlayServiceUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindString;
import butterknife.BindView;
import butterknife.ButterKnife;

public abstract class BaseActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener{

    @Nullable
    @BindView(R.id.Toolbar)
    Toolbar mToolbar;

    @BindString(R.string.baseActivity_permission_location)
    String titlePermission;
    @BindString(R.string.baseActivity_permission_text)
    String messagePermission;
    @BindString(R.string.baseActivity_permission_text1)
    String messagePermission1;
    @BindString(R.string.baseActivity_action_label_ativar)
    String actionAtivar;
    @BindString(R.string.baseActivity_action_label_cancelar)
    String actionCancelar;

    public static final int CLOSE_APP = 1;
    protected static final int REQUEST_LOCATION = 2000;
    protected static final int REQUEST_CHECK_SETTINGS = 1000;
    protected static final String TAG = BaseActivity.class.getSimpleName();

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mCurrentLocation;
    protected ChargingLocation mChargingLocation;

    protected AlertDialog alertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PlayServiceUtil.checkGooglePlayService(this);

        EventBus.getDefault().register(this);

        if(mGoogleApiClient == null){
            this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        this.onCreateLocationRequest();

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        this.prepareViews();
    }

    protected void setContentViewWithoutInject(@LayoutRes int layoutResID){
        super.setContentView(layoutResID);
    }

    protected void prepareViews(){
        ButterKnife.bind(this);
        this.setupToolbar();
    }

    protected void setupToolbar(){
        if(mToolbar != null){
            setSupportActionBar(mToolbar);
            if(getSupportActionBar() != null){
                getSupportActionBar().setDisplayShowTitleEnabled(false);
            }
        }
    }

    public Toolbar getToolbar(){
        return mToolbar != null ? mToolbar : null;
    }

    public void setTitle(String titleToolbar) {
        if (mToolbar != null) {
            TextView txtTitle = (TextView) mToolbar.findViewById(R.id.titleToolbar);
            txtTitle.setText(titleToolbar);
        }
    }

    @Override
    protected void onResume() {
        Log.e(TAG,"onResume");
        super.onResume();
        if (mGoogleApiClient.isConnected())
            this.startLocationUpdate();
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        Log.e(TAG,"onStart");
        super.onStart();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        this.stopLocationUpdate();
        mGoogleApiClient.disconnect();
        Log.e(TAG,"onStop");
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG, "onDestroy");
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.e(TAG,"onConnected");
        this.startLocationUpdate();
        this.getLastLocationUpdated();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    /*      Method OnCreateLocationRequest
    *
    *   1.  Configuração da requisição de Localização.
    *   2.  Adicionando configuração ao objeto LocationSettingsRequest.
    *   3.  Verificando se as configuração foi adicionada.
    *   4.  Verifica se a configuração de Localização foi satisfeita.
    *       4.1 LocationSettingsStatusCodes.SUCCESS
    *           Se a configuração de localização for satisfeita.
    *       4.2 LocationSettingsStatusCodes.RESOLUTION_REQUIRED
    *           se a configuração de localização não for satisfeita mas pode ser corrigida.
    *       4.3 LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE
    *           se a configuração de localização não for satisfeita e não pode ser corrigida.
    *
    * */

    public void onCreateLocationRequest(){
        this.mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(3000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationSettingsRequest.Builder requestSettings = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult>   resultPendingResult = LocationServices.SettingsApi
                .checkLocationSettings(mGoogleApiClient,requestSettings.build());

        resultPendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        Log.e(TAG, "SETTINGS LOCATION SATISFIED");
                        getLastLocationUpdated();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        Log.e(TAG, "SETTINGS LOCATION NO SATISFIED, BUT CAN BE FIXED");
                        try {
                            status.startResolutionForResult(BaseActivity.this, REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG, e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "SETTINGS LOCATION NO SATISFIED");
                        break;
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK){
                this.getLastLocationUpdated();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if ( requestCode == REQUEST_LOCATION){
            if ( grantResults[0] == PackageManager.PERMISSION_GRANTED){
                Log.e(TAG, "PERMISSION_GRANTED");
                this.getLastLocationUpdated();
            }else{
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(titlePermission);
                builder.setMessage(messagePermission1);
                builder.setPositiveButton(actionAtivar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
                    }
                });

                alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    public void getLastLocationUpdated(){
        Log.e(TAG, "getLastLocationUpdated");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            this.mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            Log.e(TAG, "PERMISSION_GRANTED");
        }else{
            Log.e(TAG, "PERMISSION_DANIED");
            if(ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(titlePermission);
                builder.setMessage(messagePermission);
                builder.setPositiveButton(actionAtivar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M )
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
                    }
                });

                alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    protected void startLocationUpdate(){
        Log.e(TAG,"startLocationUpdate");
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            Log.e(TAG, "PERMISSION_GRANTED");
        }else {
            Log.e(TAG, "PERMISSION_DANIED");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle(titlePermission);
                builder.setMessage(messagePermission);
                builder.setPositiveButton(actionAtivar, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
                    }
                });

                alertDialog = builder.create();
                alertDialog.show();
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.e(TAG, "onLocationChanged: MainActivity");
        this.mCurrentLocation = location;

        mChargingLocation = new ChargingLocation();
        mChargingLocation.setLocation(mCurrentLocation);
        mChargingLocation.setUpdated(true);

        EventBus.getDefault().postSticky(mChargingLocation);
    }

    protected void stopLocationUpdate(){
        Log.e(TAG, "stopLocationUpdate: BaseActivity");
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient,this);
    }

    @Subscribe(priority = 1)
    public void onEvent(ChargingLocation chargingLocation) {

        if(chargingLocation.isUpdated()){
            return;
        }

        Log.e(TAG,"onEvent: BaseActvity");

        this.getLastLocationUpdated();
        mChargingLocation = chargingLocation;
        mChargingLocation.setUpdated(true);
        mChargingLocation.setLocation(mCurrentLocation);

        EventBus.getDefault().post(mChargingLocation);

    }

}