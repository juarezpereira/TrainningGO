package com.projeto.trainninggo;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v7.app.ActionBarDrawerToggle;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.projeto.trainninggo.Activities.LoginActivity;
import com.projeto.trainninggo.Base.BaseActivityDrawer;
import com.projeto.trainninggo.Fragment.DashBoardFragment;
import com.projeto.trainninggo.Fragment.MyGroupsFragment;
import com.projeto.trainninggo.Fragment.NewGroupFragment;

public class MainActivity extends BaseActivityDrawer {

    private TrainningGoApplication application;
    private LoginManager loginManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        setTitle(getString(R.string.app_name));

        this.application = ((TrainningGoApplication)getApplication());
        this.loginManager = LoginManager.getInstance();

        //USUÁRIO LOGADO NA APLICAÇÃO
        //PEDIR PARA VERIFICAR A PERMISSÃO DE LOCALIZAÇÃO
        if(application.getCurrentUser()!= null){
            this.showFragment(new DashBoardFragment());
            this.setProfileName(application.getCurrentUser().getName());
            this.setProfileEmail(application.getCurrentUser().getEmail());
        }else{
            startActivity(new Intent(getBaseContext(), LoginActivity.class));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menu_newgroup:
                showFragment(new NewGroupFragment());
                Toast.makeText(getBaseContext(),item.getTitle(),Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void setupNavigation() {
        mNavigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                mDrawerLayout.closeDrawers();

                switch (item.getItemId()){
                    case R.id.nav_item_home:
                        showFragment(new DashBoardFragment());
                        Toast.makeText(getApplicationContext(),item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_item_mygroups:
                        showFragment(new MyGroupsFragment());
                        Toast.makeText(getApplicationContext(),item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_item_achievement:
                        Toast.makeText(getApplicationContext(),item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_item_settings:
                        Toast.makeText(getApplicationContext(),item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_item_help:
                        Toast.makeText(getApplicationContext(),item.getTitle(),Toast.LENGTH_SHORT).show();
                        return true;
                    case R.id.nav_item_logout:
                        loginManager.logOut();
                        application.logOutApplication();
                        startActivityForResult(new Intent(getApplicationContext(),LoginActivity.class),CLOSE_APP);
                        return true;
                }

                return false;
            }
        });

        ActionBarDrawerToggle drawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, getToolbar(),R.string.app_name,R.string.app_name);
        mDrawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();

    }

    @Override
    protected void setupHeader() {
        View viewHeader = mNavigationView.getHeaderView(0);

        tvProfileName   = (TextView)viewHeader.findViewById(R.id.tvProfileName);
        tvProfileEmail  = (TextView)viewHeader.findViewById(R.id.tvProfileEmail);
        ivProfileImage  = (ImageView)viewHeader.findViewById(R.id.ivProfileImage);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CLOSE_APP)
            finish();
    }

}
