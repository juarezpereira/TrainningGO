package com.projeto.domain.Model;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Grupos {

    @SerializedName("groups")
    @Expose
    private List<Grupo> grupos = new ArrayList<Grupo>();

    /**
     * @return The grupos
     */
    public List<Grupo> getGrupos() {
        return grupos;
    }

    /**
     * @param grupos The grupos
     */
    public void setGrupos(List<Grupo> grupos) {
        this.grupos = grupos;
    }

}