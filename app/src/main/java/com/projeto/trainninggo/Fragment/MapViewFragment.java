package com.projeto.trainninggo.Fragment;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.projeto.trainninggo.R;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapViewFragment extends Fragment implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final int REQUEST_CHECK_SETTINGS = 1001;
    private static final String LOCATION_IS_UPDATE = "LOCATION_IS_UPDATE";
    private static final String LOCATION_KEY = "LOCATION_KEY";
    private static final String LAST_UPDATED_TIME = "LAST_UPDATED_TIME";
    private static final String TAG = MapViewFragment.class.getSimpleName();

    @BindView(R.id.tvStatusAtt) TextView tvStatus;

    private GoogleMap mGoogleMap;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;
    private Location mCurrentLocation;
    private String mLastUpdateTime;

    private boolean mRequestingLocationUpdates;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.viewMap);
        mapFragment.getMapAsync(this);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        this.creatLocationResult();
        this.updateValuesFromBundle(savedInstanceState);

        return view;
    }

    @Override
    public void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    public void onPause() {
        super.onPause();
        this.stopLocationUpdates();
    }

    @Override
    public void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        this.startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public void creatLocationResult() {

        // MUDANDO CONFIGURAÇÕES DE LOCALIZAÇÃO
        // CONFIGURANDO PRIORIDADE DE FONTE DE LOCALIZAÇÃO
        // CONFIGURANDO INTERVELO DE TEMPO ENTRE AS REQUISIÇOES DE LOCALIZAÇÃO
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(50000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        // ADICIONANDO A CONFIGURAÇÃO DE LOCALIZACAO
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());

        // VERIFICA SE AS CONFIGURAÇÕES DE LOCALIZAÇÃO FORAM SATISFEITAS
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
//                final LocationSettingsStates locationSettingsStates = locationSettingsResult.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // TODAS AS CONFIGURAÇÕES DE LOCALIZAÇAO ESTIVREM SATISFEITAS
                        // O CLIENTE PODE INICIAR A SOLICITAÇÃO DE LOCALIZAÇÃO AQUI.
                        Log.e(TAG, "SETTINGS LOCATION SATISFIED");
                        requestLocationCurrent();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // CONFIGURAÇÕES DE LOCALIZAÇÕES NÃO ESTÃO SATISFEITAS MAS ISSO PDOE
                        // SER CORRIGIDO MOSTRANDO UM DIALOGO PARA O CLIENTE
                        Log.e(TAG, "SETTINGS LOCATION NO SATISFIED, BUT CAN BE FIXED");
                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            Log.e(TAG,e.toString());
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // CONFIGURAÇÕES DE LOCALIZAÇÕES NÃO ESTAO SATISFEITAS E NÃO PODEM SER CORRIGIDAS
                        Log.e(TAG, "SETTINGS LOCATION NO SATISFIED");
                        break;
                }

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            if (resultCode == Activity.RESULT_OK) {
                this.requestLocationCurrent();
            }
        }
    }

    // TODA VEZ QUE A LOCATION FOI ALTERADA
    // MAPA DO CLIENTE SERA ATUALIZADO
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        this.updateUI();
    }

    protected void startLocationUpdates() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    protected void requestLocationCurrent(){
        if(ContextCompat.checkSelfPermission(getContext(),Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            mCurrentLocation  = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            this.updateUI();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(LOCATION_IS_UPDATE, mRequestingLocationUpdates);
        savedInstanceState.putString(LOCATION_IS_UPDATE, mLastUpdateTime);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    public void updateValuesFromBundle(Bundle savedInstanceState){
        if(savedInstanceState != null){

            if(savedInstanceState.keySet().contains(LOCATION_IS_UPDATE)){
                mRequestingLocationUpdates = savedInstanceState.getBoolean(LOCATION_IS_UPDATE);
            }

            if(savedInstanceState.keySet().contains(LOCATION_KEY)){
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            if(savedInstanceState.keySet().contains(LAST_UPDATED_TIME)){
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME);
            }

            this.updateUI();
        }

    }

    public void updateUI(){
        if(mCurrentLocation!= null){
            LatLng latLngCurrent = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(latLngCurrent,14));
            mGoogleMap.addMarker(new MarkerOptions().position(latLngCurrent));

            mLastUpdateTime = getString(R.string.map_view_fragment_label_ultima_att)+(DateFormat.getTimeInstance().format(new Date()));
            tvStatus.setText(mLastUpdateTime);
        }
    }

    @OnClick(R.id.fabMyLocation)
    public void btnUpdateLocation(){
        this.requestLocationCurrent();
    }

}