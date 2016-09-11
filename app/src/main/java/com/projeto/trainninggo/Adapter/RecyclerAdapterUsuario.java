package com.projeto.trainninggo.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projeto.domain.Model.Usuario;
import com.projeto.trainninggo.Interfaces.RecyclerViewOnClickListenerHack;
import com.projeto.trainninggo.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecyclerAdapterUsuario extends RecyclerView.Adapter<RecyclerAdapterUsuario.MyViewHolder>{

    private List<Usuario> mList;
    private LayoutInflater mLayoutInflater;
    private RecyclerViewOnClickListenerHack mOnClickListenerHack;

    public RecyclerAdapterUsuario(Context context,List<Usuario> lst){
        this.mList = lst;
        this.mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = mLayoutInflater.inflate(R.layout.item_participante, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        holder.tvName.setText(mList.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnClickListenerHack(RecyclerViewOnClickListenerHack onClickListenerHack){
        this.mOnClickListenerHack = onClickListenerHack;
    }


    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        @BindView(R.id.ivProfileImage) ImageView ivProfileImage;
        @BindView(R.id.tvName) TextView tvName;

        public MyViewHolder(View itemView){
            super(itemView);
            ButterKnife.bind(this,itemView);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            if(mOnClickListenerHack!= null)
                mOnClickListenerHack.onClickListener(view,getAdapterPosition());
        }

    }

}
