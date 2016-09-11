package com.projeto.domain.Model;

import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Session {

    @SerializedName("user")
    @Expose
    private Usuario user;

    /**
     *
     * @return
     * The user
     */
    public Usuario getUsuario() {
        return user;
    }

    /**
     *
     * @param user
     * The user
     */
    public void setUsuario(Usuario user) {
        this.user = user;
    }

}