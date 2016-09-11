package com.projeto.trainninggo.Base;

import android.support.annotation.LayoutRes;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.projeto.trainninggo.R;

import butterknife.BindView;

/**
 * Created by Juarez on 27/08/2016.
 */
public abstract class BaseActivityDrawer extends BaseActivity {

    @BindView(R.id.DrawerLayout)
    protected DrawerLayout mDrawerLayout;
    @BindView(R.id.NavigationView)
    protected NavigationView mNavigationView;

    protected TextView tvProfileName;
    protected TextView tvProfileEmail;
    protected ImageView ivProfileImage;

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentViewWithoutInject(R.layout.activity_drawer);

        ViewGroup viewGroup = (ViewGroup)findViewById(R.id.ContainerDrawerLayout);
        LayoutInflater.from(this).inflate(layoutResID, viewGroup, true);

        this.prepareViews();
        this.setupNavigation();
        this.setupHeader();
    }

    protected abstract void setupNavigation();

    protected abstract void setupHeader();

    public void showFragment(Fragment fragment){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
        ft.replace(R.id.frame_container, fragment);
        ft.addToBackStack("mainFrag");
        ft.commitAllowingStateLoss();
    }

    public void setProfileName(String name) {
        this.tvProfileName.setText(name);
    }

    public void setProfileEmail(String email) {
        this.tvProfileEmail.setText(email);
    }

    public void setProfileImage(int id) {
        this.ivProfileImage.setImageResource(id);
    }
}
