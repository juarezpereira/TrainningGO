package com.projeto.trainninggo.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.projeto.domain.API.UserService;
import com.projeto.domain.Model.Session;
import com.projeto.domain.Model.Usuario;
import com.projeto.domain.ServiceGenerator;
import com.projeto.trainninggo.Base.BaseActivity;
import com.projeto.trainninggo.MainActivity;
import com.projeto.trainninggo.R;
import com.projeto.trainninggo.TrainningGoApplication;

import java.util.Arrays;

import butterknife.BindView;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends BaseActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();
    private static final int REQUEST_LOCATION = 2000;

    @BindView(R.id.facebookLoginButton) protected LoginButton loginButtonFB;
    @BindView(R.id.btnLoginFace) Button btnLoginFace;
    @BindView(R.id.activity_login) RelativeLayout view;

    private CallbackManager callbackManager;
    private LoginManager loginManager;
    private TrainningGoApplication application;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                Log.e(MainActivity.class.getSimpleName(),"PERMISSION_GRANTED");
            }else{
                if(ActivityCompat.shouldShowRequestPermissionRationale(this,Manifest.permission.ACCESS_FINE_LOCATION)){
                    btnLoginFace.setVisibility(View.INVISIBLE);
                    Snackbar.make(view,"Essa permissão e essencial para o funcionamento da aplicaçao.", Snackbar.LENGTH_INDEFINITE)
                            .setAction("Ativar", new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
                                    btnLoginFace.setVisibility(View.VISIBLE);
                                }
                            })
                            .show();
                }else{
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
                }
            }
        }

        this.application = ((TrainningGoApplication) getApplication());
        this.callbackManager = CallbackManager.Factory.create();
        this.loginManager = LoginManager.getInstance();
        this.loginButtonFB.setReadPermissions(Arrays.asList("email","public_profile"));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if( requestCode == REQUEST_LOCATION){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Permissão")
                        .setMessage("Aplicação necessita da localização do usuário para identificar grupos de atividades ao seu redor.")
                        .setPositiveButton("Ativar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
                                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},REQUEST_LOCATION);
                            }
                        })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                finishActivity(CLOSE_APP);
                            }
                        });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
            Log.e(MainActivity.class.getSimpleName(),"PERMISSION_GRANTED");
        }else{
            super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.btnLoginFace)
    public void onClick(){
        loginButtonFB.performClick();
        loginButtonFB.setPressed(true);
        loginButtonFB.invalidate();
        loginButtonFB.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(final LoginResult loginResult) {
                UserService service = ServiceGenerator.createService(UserService.class, loginResult.getAccessToken().getToken(),null);
                Call<Session> call = service.createLogin();

                call.enqueue(new Callback<Session>() {
                    @Override
                    public void onResponse(Call<Session> call, Response<Session> response) {
                        Log.e(TAG,"Message: "+response.code());
                        if(response.isSuccessful()){
                            Usuario usuario = response.body().getUsuario();
                            usuario.setApiKey(response.headers().get("X-Api-Key"));

                            application.setCurrentUser(usuario);

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivityForResult(intent,CLOSE_APP);
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(Call<Session> call, Throwable t) {
                        Log.e(TAG,t.toString());
                        loginManager.logOut();
                    }
                });

            }

            @Override
            public void onCancel() {
                loginManager.logOut();
            }

            @Override
            public void onError(FacebookException error) {
                loginManager.logOut();
            }
        });
        loginButtonFB.setPressed(false);
        loginButtonFB.invalidate();
    }

}
