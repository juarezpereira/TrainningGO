package com.projeto.domain.Model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Grupo implements Parcelable {

    @SerializedName("id")
    @Expose
    private int id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("sport")
    @Expose
    private Esporte sport;
    @SerializedName("admin_ids")
    @Expose
    private List<Integer> adminIds = new ArrayList<Integer>();
    @SerializedName("activity")
    @Expose
    private Atividade activity;

    /**
     *
     * @return
     * The id
     */
    public int getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The description
     */
    public String getDescription() {
        return description;
    }

    /**
     *
     * @param description
     * The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     *
     * @return
     * The sport
     */
    public Esporte getEsporte() {
        return sport;
    }

    /**
     *
     * @param sport
     * The sport
     */
    public void setEsporte(Esporte sport) {
        this.sport = sport;
    }

    /**
     *
     * @return
     * The adminIds
     */
    public List<Integer> getAdminIds() {
        return adminIds;
    }

    /**
     *
     * @param adminIds
     * The admin_ids
     */
    public void setAdminIds(List<Integer> adminIds) {
        this.adminIds = adminIds;
    }

    /**
     *
     * @return
     * The activity
     */
    public Atividade getAtividade() {
        return activity;
    }

    /**
     *
     * @param activity
     * The activity
     */
    public void setAtividade(Atividade activity) {
        this.activity = activity;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeString(this.description);
        dest.writeParcelable(this.sport, flags);
        dest.writeList(this.adminIds);
        dest.writeParcelable(this.activity, flags);
    }

    public Grupo() {
    }

    protected Grupo(Parcel in) {
        this.id = in.readInt();
        this.name = in.readString();
        this.description = in.readString();
        this.sport = in.readParcelable(Esporte.class.getClassLoader());
        this.adminIds = new ArrayList<Integer>();
        in.readList(this.adminIds, Integer.class.getClassLoader());
        this.activity = in.readParcelable(Atividade.class.getClassLoader());
    }

    public static final Creator<Grupo> CREATOR = new Creator<Grupo>() {
        @Override
        public Grupo createFromParcel(Parcel source) {
            return new Grupo(source);
        }

        @Override
        public Grupo[] newArray(int size) {
            return new Grupo[size];
        }
    };

}