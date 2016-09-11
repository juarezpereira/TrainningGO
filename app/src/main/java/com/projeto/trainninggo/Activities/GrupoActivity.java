package com.projeto.trainninggo.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.graphics.Palette;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projeto.domain.Model.Atividade;
import com.projeto.domain.Model.Grupo;
import com.projeto.trainninggo.Base.BaseActivity;
import com.projeto.trainninggo.Fragment.FeedFragment;
import com.projeto.trainninggo.Fragment.MembersFragment;
import com.projeto.trainninggo.R;

import butterknife.BindView;

public class GrupoActivity extends BaseActivity {

    private static final String KEY_GRUPO = "GRUPO";

    @BindView(R.id.CollapsingToolbar) CollapsingToolbarLayout mCollapsingToolbar;
    @BindView(R.id.fabAtividade) FloatingActionButton mFab;
    @BindView(R.id.ivGroupImage) ImageView ivGrupo;
    @BindView(R.id.TabLayout) TabLayout mTabLayout;
    @BindView(R.id.ViewPager) ViewPager mViewPager;

    @BindView(R.id.ViewAtividade) ViewGroup mViewAtividade;
    @BindView(R.id.tvDate) TextView tvDate;
    @BindView(R.id.tvLocal) TextView tvLocal;

    private Grupo currentGrupo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grupo);

        if(getIntent().getExtras()!= null){
            this.currentGrupo = getIntent().getParcelableExtra(KEY_GRUPO);
            Atividade currentAtividade = currentGrupo.getAtividade();

            if (currentAtividade != null){
                tvDate.setText(currentAtividade.getDate());
                tvLocal.setText(currentAtividade.getAddress());
            }else{
                tvLocal.setText(R.string.item_atividade_notification_sem_atividade);
                tvDate.setText(R.string.item_atividade_notification_sem_atividade_2);
            }

        }

        if(getSupportActionBar()!= null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mCollapsingToolbar.setTitle(currentGrupo.getName());
        }

        this.loadImageGrupo();

        this.mViewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
        this.mTabLayout.post(new Runnable() {
            @Override
            public void run() {
                mTabLayout.setupWithViewPager(mViewPager);
            }
        });

        this.mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),AtividadeGrupoActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_grupo,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void loadImageGrupo(){
        String currentGrupoName = currentGrupo.getEsporte().getName();

        switch (currentGrupoName){
            case "corrida":
                ivGrupo.setImageResource(R.drawable.image_cor);
                loadPallete();
                break;
            case "ciclismo":
                ivGrupo.setImageResource(R.drawable.image_cic);
                loadPallete();
                break;
            case "futebol":
                ivGrupo.setImageResource(R.drawable.image_fut);
                loadPallete();
                break;
            case "vôlei":
                ivGrupo.setImageResource(R.drawable.image_vol);
                loadPallete();
                break;
            case "musculação":
                ivGrupo.setImageResource(R.drawable.image_mus);
                loadPallete();
                break;
        }
    }

    public void loadPallete(){
        BitmapDrawable drawable = (BitmapDrawable) ivGrupo.getDrawable();
        Bitmap bitmap = drawable.getBitmap();

        Palette.Builder builder = new Palette.Builder(bitmap);
        builder.generate(new Palette.PaletteAsyncListener() {
            @Override
            public void onGenerated(Palette palette) {
                Palette.Swatch vibrantDark = palette.getDarkVibrantSwatch();
                Palette.Swatch vibrantLight = palette.getLightVibrantSwatch();

                if(vibrantDark != null){
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                        getWindow().setStatusBarColor(vibrantDark.getRgb());
                    mCollapsingToolbar.setContentScrimColor(vibrantDark.getRgb());
                    mCollapsingToolbar.setStatusBarScrimColor(vibrantDark.getRgb());
                    mTabLayout.setBackgroundColor(vibrantDark.getRgb());
                    mTabLayout.setSelectedTabIndicatorColor(vibrantDark.getTitleTextColor());
                    mTabLayout.setTabTextColors(vibrantDark.getTitleTextColor(),vibrantDark.getBodyTextColor());

                    if(vibrantLight != null){
                        mViewAtividade.setBackgroundColor(vibrantLight.getRgb());
                    }

                }

            }
        });
    }

    protected class ViewPagerAdapter extends FragmentPagerAdapter{

        public ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position){
                case 0:return new FeedFragment();
                case 1:
                    MembersFragment fragment = new MembersFragment();
                    fragment.currentGrupo(currentGrupo);
                    return fragment;
                default:
                    return null;
            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position){
                case 0: return getString(R.string.fragment_feeds_title);
                case 1: return getString(R.string.fragment_members_title);
                default:return null;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

    }

}
