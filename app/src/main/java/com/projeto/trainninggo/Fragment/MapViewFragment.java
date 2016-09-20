package com.projeto.trainninggo.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.projeto.trainninggo.Base.BaseFragment;
import com.projeto.trainninggo.R;
import com.projeto.domain.Model.ChargingLocation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapViewFragment extends BaseFragment implements OnMapReadyCallback{

    private static final String TAG = MapViewFragment.class.getSimpleName();
    private static final String LOCATION_IS_UPDATE = "LOCATION_IS_UPDATE";
    private static final String LOCATION_KEY = "LOCATION_KEY";
    private static final String LAST_UPDATED_TIME = "LAST_UPDATED_TIME";

    @BindView(R.id.tvStatusAtt) TextView tvStatus;

    private GoogleMap mGoogleMap;
    private Location mCurrentLocation;
    private String mLastUpdateTime;
    private boolean mRequestingLocationUpdates;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.viewMap);
        mapFragment.getMapAsync(this);

        this.updateValuesFromBundle(savedInstanceState);
        this.updateLocationMap();

        return view;
    }


    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(LOCATION_IS_UPDATE, mRequestingLocationUpdates);
        savedInstanceState.putString(LAST_UPDATED_TIME, mLastUpdateTime);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        super.onSaveInstanceState(savedInstanceState);
    }

    protected void updateValuesFromBundle(Bundle savedInstanceState){
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

        }

    }

    protected void updateLocationMap(){
        ChargingLocation mChargingLocation = new ChargingLocation();
        mChargingLocation.setUpdated(false);

        Log.e(TAG, "onEvent: updateLocationMap : MapViewFragment");
        EventBus.getDefault().post(mChargingLocation);

        if(mCurrentLocation!= null){
            LatLng latLngCurrent = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            if(mGoogleMap != null){
                mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(latLngCurrent,14));
                mGoogleMap.addMarker(new MarkerOptions().position(latLngCurrent));
            }

            mLastUpdateTime = getString(R.string.map_view_fragment_label_ultima_att)+(DateFormat.getTimeInstance().format(new Date()));
            tvStatus.setText(mLastUpdateTime);
        }
    }

    @OnClick(R.id.fabMyLocation)
    public void btnUpdateLocation(){
        this.updateLocationMap();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChargingLocation chargingLocation){
        if(!chargingLocation.isUpdated())
            return;

        Log.e(TAG, "onEvent: MapViewFragment");
        this.mCurrentLocation = chargingLocation.getLocation();
        LatLng latLngCurrent = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

        if(mGoogleMap != null){
            mGoogleMap.clear();
            mGoogleMap.addMarker(new MarkerOptions().position(latLngCurrent));
            mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(latLngCurrent,14));
        }
    }

}