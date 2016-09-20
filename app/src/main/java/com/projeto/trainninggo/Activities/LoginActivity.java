package com.projeto.trainninggo.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import butterknife.ButterKnife;
import butterknife.OnClick;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

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
        ButterKnife.bind(this);

        this.application = ((TrainningGoApplication) getApplication());
        this.callbackManager = CallbackManager.Factory.create();
        this.loginManager = LoginManager.getInstance();
        this.loginButtonFB.setReadPermissions(Arrays.asList("email","public_profile"));

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
                            startActivityForResult(intent,BaseActivity.CLOSE_APP);
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
