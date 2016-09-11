package com.projeto.trainninggo.Base;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.projeto.trainninggo.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseActivity extends AppCompatActivity {

    protected static final int CLOSE_APP = 1;

    @Nullable
    @BindView(R.id.Toolbar)
    Toolbar mToolbar;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        prepareViews();
    }

    public void setContentViewWithoutInject(int layoutResId) {
        super.setContentView(layoutResId);
    }

    protected void prepareViews() {
        injectViews();
        setupToolbar();
    }

    protected void injectViews() {
        ButterKnife.bind(this);
    }

    protected void setupToolbar() {
        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            if(getSupportActionBar()!= null)
                getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
    }

    public Toolbar getToolbar(){
        return mToolbar != null ? mToolbar : null;
    }

    public void setTitle(String titleToolbar){
        if(mToolbar != null){
            TextView txtTitle = (TextView)mToolbar.findViewById(R.id.titleToolbar);
            txtTitle.setText(titleToolbar);
        }
    }

}
