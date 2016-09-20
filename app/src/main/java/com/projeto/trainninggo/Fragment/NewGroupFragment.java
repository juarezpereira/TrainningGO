package com.projeto.trainninggo.Fragment;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.projeto.domain.API.UserService;
import com.projeto.domain.Model.Atividade;
import com.projeto.domain.Model.Esporte;
import com.projeto.domain.Model.Esportes;
import com.projeto.domain.Model.Grupo;
import com.projeto.domain.ServiceGenerator;
import com.projeto.trainninggo.Adapter.SpinnerAdapter;
import com.projeto.trainninggo.Base.BaseFragment;
import com.projeto.trainninggo.R;
import com.projeto.trainninggo.TrainningGoApplication;
import com.projeto.domain.Model.ChargingLocation;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NewGroupFragment extends BaseFragment implements OnMapReadyCallback{

    private static final String TAG = NewGroupFragment.class.getSimpleName();

    @BindView(R.id.edtName) EditText edtName;
    @BindView(R.id.edtDesc) EditText edtDesc;
    @BindView(R.id.SpinnerEsporte) Spinner mSpinner;
    @BindView(R.id.SwitchAtividade) Switch mSwitchAtv;

    @BindView(R.id.ViewAtividade) ViewGroup viewAtividade;
    @BindView(R.id.edtLocationActivity) EditText edtLocal;
    @BindView(R.id.edtDateActivity) EditText edtDate;

    private TrainningGoApplication application;
    private ArrayAdapter mSpinnerAdapter;
    private List<Esporte> mListEsportes;

    private GoogleMap mGoogleMap;
    private Location mCurrentLocation;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.application = ((TrainningGoApplication)getActivity().getApplication());

        this.mListEsportes = new ArrayList<>();
        this.mListEsportes.add(new Esporte("Esportes"));
        this.mSpinnerAdapter = new SpinnerAdapter(getContext(),mListEsportes);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_new_group, container, false);
        ButterKnife.bind(this,view);

        SupportMapFragment mapFragment = (SupportMapFragment)getChildFragmentManager().findFragmentById(R.id.viewMap);
        mapFragment.getMapAsync(this);
        this.mSpinner.setAdapter(mSpinnerAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.requestSports();
    }

    @Override
    public void onStart() {
        EventBus.getDefault().register(this);
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick(R.id.SwitchAtividade)
    public void onClickSwitch(){
        if (mSwitchAtv.isChecked()){
            viewAtividade.setVisibility(View.VISIBLE);
            this.onLocationUpdate();
        }else{
            viewAtividade.setVisibility(View.GONE);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mGoogleMap = googleMap;
    }

    @OnClick(R.id.btnNewGroup)
    public void onClickRegister(){
        Grupo newGrupo = new Grupo();
        newGrupo.setName(edtName.getText().toString());
        newGrupo.setDescription(edtDesc.getText().toString());

        Esporte esporte = (Esporte)mSpinner.getSelectedItem();
        newGrupo.setEsporte(esporte);

        if(mSwitchAtv.isChecked()){
            Atividade atividade = new Atividade();
            atividade.setAddress(edtLocal.getText().toString());
            atividade.setDate(edtDate.getText().toString());
            newGrupo.setAtividade(atividade);
        }

        UserService service = ServiceGenerator.createService(UserService.class, null, application.getCurrentUser().getApiKey());
        Call<ResponseBody> call = service.createGroup(newGrupo);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                Log.e(TAG,"Message: "+response.code());
                Log.e(TAG,"Error: "+ response.message());
                Log.e(TAG,"Error: "+response.errorBody());
                if(response.isSuccessful()){
                    Toast.makeText(getContext(),"Grupo criado",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG,t.getLocalizedMessage());
            }
        });

    }

    public void requestSports(){

        UserService service = ServiceGenerator.createServiceAnonymous(UserService.class);
        Call<Esportes> call = service.getSports();

        call.enqueue(new Callback<Esportes>() {
            @Override
            public void onResponse(Call<Esportes> call, Response<Esportes> response) {
                Log.e(TAG,"Message: "+response.code());
                if(response.isSuccessful()){
                    mListEsportes.addAll(response.body().getEsportes());
                    mSpinnerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Esportes> call, Throwable t) {
                Log.e(TAG,"Message: "+t.toString());
            }
        });

    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ChargingLocation chargingLocation){
        if(!chargingLocation.isUpdated())
            return;

        Log.e(TAG, "onEvent: NewGroupFragment");
        this.mCurrentLocation = chargingLocation.getLocation();
    }

    public void onLocationUpdate(){
        ChargingLocation chargingLocation = new ChargingLocation();
        chargingLocation.setUpdated(false);

        Log.e(TAG, "onEvent: NewGroupFragment : onLocationUpdate");
        EventBus.getDefault().post(chargingLocation);

        if(mCurrentLocation != null){
            LatLng latLngCurrent = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());

            mGoogleMap.animateCamera( CameraUpdateFactory.newLatLngZoom(latLngCurrent,14));
            mGoogleMap.addMarker(new MarkerOptions().position(latLngCurrent));
        }
    }

}
