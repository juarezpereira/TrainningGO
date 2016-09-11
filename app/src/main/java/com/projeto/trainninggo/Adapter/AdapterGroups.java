package com.projeto.trainninggo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projeto.domain.Model.Grupo;
import com.projeto.trainninggo.Interfaces.RecyclerViewOnClickListenerHack;
import com.projeto.trainninggo.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AdapterGroups extends RecyclerView.Adapter<AdapterGroups.MyViewHolder> {

    private static final int CORRIDA = 1;
    private static final int CICLISMO = 2;
    private static final int FUTEBOL = 3;
    private static final int VOLEI = 4;
    private static final int MUSCULACAO = 5;

    private List<Grupo> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack onClickListenerHack;

    public AdapterGroups(Context context, List<Grupo> listGrupo){
        this.mList = listGrupo;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_card_group, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Grupo currentGrupo = mList.get(position);

        switch (holder.getItemViewType()){
            case CORRIDA:
                holder.ivGroupImage.setImageResource(R.drawable.ic_correr);
                break;
            case CICLISMO:
                holder.ivGroupImage.setImageResource(R.drawable.ic_ciclismo);
                break;
            case FUTEBOL:
                holder.ivGroupImage.setImageResource(R.drawable.ic_futebol);
                break;
            case VOLEI:
                holder.ivGroupImage.setImageResource(R.drawable.ic_volei);
                break;
            case MUSCULACAO:
                holder.ivGroupImage.setImageResource(R.drawable.ic_musculacao);
                break;
        }
        holder.tvGroupName.setText(currentGrupo.getName());
        holder.tvGroupSport.setText(currentGrupo.getEsporte().getName());

    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @Override
    public int getItemViewType(int position) {
        Grupo currentGroup = mList.get(position);

        switch (currentGroup.getEsporte().getId()){
            case CORRIDA:
                return CORRIDA;
            case CICLISMO:
                return CICLISMO;
            case FUTEBOL:
                return FUTEBOL;
            case VOLEI:
                return VOLEI;
            case MUSCULACAO:
                return MUSCULACAO;
            default:
                return super.getItemViewType(position);
        }
    }

    public void setOnClickListenerHack(RecyclerViewOnClickListenerHack onClickListenerHack){
        this.onClickListenerHack = onClickListenerHack;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,View.OnLongClickListener{

        @BindView(R.id.tvGroupName) TextView tvGroupName;
        @BindView(R.id.tvGroupSport) TextView tvGroupSport;
        @BindView(R.id.ivGroupImage) ImageView ivGroupImage;

        public MyViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(onClickListenerHack != null){
                onClickListenerHack.onClickListener(view,getAdapterPosition());
            }
        }

        @Override
        public boolean onLongClick(View view) {
            if(onClickListenerHack != null){
                onClickListenerHack.onLongClickListener(view,getAdapterPosition());
                return true;
            }
            return false;
        }
    }

}
