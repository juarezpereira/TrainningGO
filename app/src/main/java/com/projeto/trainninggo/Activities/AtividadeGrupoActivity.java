package com.projeto.trainninggo.Activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.RecyclerView;

import com.projeto.trainninggo.Base.BaseActivity;
import com.projeto.trainninggo.R;

import butterknife.BindView;

public class AtividadeGrupoActivity extends BaseActivity {

    @BindView(R.id.RecyclerViewMembers) RecyclerView mRecyclerView;
    @BindView(R.id.fabAtividade) FloatingActionButton mFabAtividade;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo_atividade);

    }


}
