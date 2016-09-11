package com.projeto.trainninggo;

import android.app.Application;
import android.content.SharedPreferences;

import com.projeto.domain.Model.Usuario;

public class TrainningGoApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

    }

    public void setCurrentUser(Usuario currentUser){
        SharedPreferences.Editor editor = getSharedPreferences().edit();

        editor.putBoolean(getString(R.string.currentUser_isOn),     true);
        editor.putString (getString(R.string.currentUser_name),     currentUser.getName());
        editor.putString (getString(R.string.currentUser_token),    currentUser.getAccessToken());
        editor.putString (getString(R.string.currentUser_apikey),   currentUser.getApiKey());
        editor.putString (getString(R.string.currentUser_email),    currentUser.getEmail());
        editor.putInt    (getString(R.string.currentUser_id),       currentUser.getId());
        editor.putString (getString(R.string.currentUser_provider), currentUser.getProvider());
        editor.putString (getString(R.string.currentUser_uid),      currentUser.getUid());

        editor.commit();
    }

    public Usuario getCurrentUser(){
        SharedPreferences pref = getSharedPreferences();
        boolean isOn = pref.getBoolean(getString(R.string.currentUser_isOn), false);

        if (isOn){
            Usuario currentUser = new Usuario();

            currentUser.setName(pref.getString(getString(R.string.currentUser_name),""));
            currentUser.setAccessToken(pref.getString(getString(R.string.currentUser_token),""));
            currentUser.setApiKey(pref.getString(getString(R.string.currentUser_apikey),""));
            currentUser.setEmail(pref.getString(getString(R.string.currentUser_email),""));
            currentUser.setId(pref.getInt(getString(R.string.currentUser_id),0));
            currentUser.setProvider(pref.getString(getString(R.string.currentUser_provider),""));
            currentUser.setUid(pref.getString(getString(R.string.currentUser_uid),""));

            return currentUser;
        }

        return null;
    }

    public void logOutApplication(){
        SharedPreferences.Editor editor = getSharedPreferences().edit();
        editor.clear();
        editor.commit();
    }

    private SharedPreferences getSharedPreferences(){
        return getSharedPreferences(getString(R.string.sharedpreferences_key),MODE_PRIVATE);
    }

}
