package com.projeto.trainninggo.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.projeto.domain.API.UserService;
import com.projeto.domain.Model.Grupo;
import com.projeto.domain.Model.Grupos;
import com.projeto.domain.ServiceGenerator;
import com.projeto.trainninggo.Activities.GrupoActivity;
import com.projeto.trainninggo.Adapter.AdapterGroups;
import com.projeto.trainninggo.Interfaces.RecyclerViewOnClickListenerHack;
import com.projeto.trainninggo.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class GroupsFragment extends Fragment implements RecyclerViewOnClickListenerHack{

    private static final String TAG = GroupsFragment.class.getSimpleName();
    private static final String KEY_GRUPO = "GRUPO";

    @BindView(R.id.SwipeRefreshLayout) SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.RecyclerViewGrupos) RecyclerView mRecyclerView;

    private List<Grupo> mListGrupo;
    private AdapterGroups mAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.mListGrupo = new ArrayList<>();
        this.mAdapter = new AdapterGroups(getContext(),mListGrupo);
        this.mAdapter.setOnClickListenerHack(this);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_groups, container, false);
        ButterKnife.bind(this, view);

        this.mRecyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        this.mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestGroupService();
            }
        });

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.requestGroupService();
    }

    @Override
    public void onClickListener(View view, int position) {
        Intent it = new Intent(getContext(),GrupoActivity.class);
        it.putExtra(KEY_GRUPO,mListGrupo.get(position));

        this.startActivity(it);
    }

    @Override
    public void onLongClickListener(View view, int position) {
        Toast.makeText(getContext(),mListGrupo.get(position).getDescription(),Toast.LENGTH_LONG).show();
    }

    public void requestGroupService(){
        mSwipeRefreshLayout.setRefreshing(true);

        UserService service = ServiceGenerator.createServiceAnonymous(UserService.class);
        Call<Grupos> call = service.getGrupos();

        call.enqueue(new Callback<Grupos>() {
            @Override
            public void onResponse(Call<Grupos> call, Response<Grupos> response) {
                Log.e(TAG,"Message: "+response.code());
                if (response.isSuccessful()){
                    if(mListGrupo.size() > 0)
                        mListGrupo.clear();
                    mListGrupo.addAll(response.body().getGrupos());
                    mAdapter.notifyDataSetChanged();
                }
                mSwipeRefreshLayout.setRefreshing(false);
            }

            @Override
            public void onFailure(Call<Grupos> call, Throwable t) {
                Log.e(TAG,t.toString());
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });

    }

}
