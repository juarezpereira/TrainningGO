package com.projeto.trainninggo.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.projeto.domain.API.UserService;
import com.projeto.domain.Model.Grupo;
import com.projeto.domain.Model.Membros;
import com.projeto.domain.Model.Usuario;
import com.projeto.domain.ServiceGenerator;
import com.projeto.trainninggo.Adapter.RecyclerAdapterUsuario;
import com.projeto.trainninggo.Interfaces.RecyclerViewOnClickListenerHack;
import com.projeto.trainninggo.R;
import com.projeto.trainninggo.Interfaces.InterfaceGrupo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MembersFragment extends Fragment implements InterfaceGrupo {

    private static final String TAG = MembersFragment.class.getSimpleName();

    @BindView(R.id.RecyclerViewMembers) RecyclerView mRecyclerView;

    private List<Usuario> mList;
    private RecyclerAdapterUsuario mRecyclerAdapter;

    private Grupo currentGrupo;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mList = new ArrayList<>();
        this.mRecyclerAdapter = new RecyclerAdapterUsuario(getContext(),mList);
        this.mRecyclerAdapter.setOnClickListenerHack(new RecyclerViewOnClickListenerHack() {
            @Override
            public void onClickListener(View view, int position) {

            }

            @Override
            public void onLongClickListener(View view, int position) {

            }
        });

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_members, container, false);
        ButterKnife.bind(this,view);

        this.mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mRecyclerAdapter);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.requestMembersGroupService();
    }

    public void requestMembersGroupService(){
        UserService service = ServiceGenerator.createServiceAnonymous(UserService.class);
        Call<Membros> call = service.getGroupMembers(currentGrupo.getId());

        call.enqueue(new Callback<Membros>() {
            @Override
            public void onResponse(Call<Membros> call, Response<Membros> response) {
                Log.e(TAG,"Message: "+response.code());
                if(response.isSuccessful()){
                    if (mList.size() > 0)
                        mList.clear();
                    mList.addAll(response.body().getUsuarios());
                    mRecyclerAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Call<Membros> call, Throwable t) {
                Log.e(TAG,"Message: "+t.toString());
            }
        });

    }

    @Override
    public void currentGrupo(Grupo current) {
        this.currentGrupo = current;
    }

}
