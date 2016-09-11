package com.projeto.domain.API;

import com.projeto.domain.Model.Esportes;
import com.projeto.domain.Model.Grupo;
import com.projeto.domain.Model.Grupos;
import com.projeto.domain.Model.Membros;
import com.projeto.domain.Model.Session;
import com.projeto.domain.Model.Usuario;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface UserService {

    //PARA FAZER LOGIN
    @GET("login")
    Call<Session> createLogin();

    //PARA RETORNAR TODOS OS GRUPOS
    @GET("groups")
    Call<Grupos> getGrupos();

    //PARA RETORNAR UM USUARIO
    @GET("users/{id}")
    Call<Session> getUser(@Path("id") int id);

    //CRIA UM NOVO GROUP
    @POST("groups")
    Call<ResponseBody> createGroup(@Body Grupo grupo);

    //RETORNA APENAS UM GRUPO
    @GET("groups/{id}")
    Call<Grupo> getGroup(@Path("id") int id);

    //RETORNAR MEMBROS  DO GRUPO
    @GET("groups/{id}/members")
    Call<Membros> getGroupMembers(@Path("id") int id);

    //RETORNA TODOS OS GRUPOS
    //QUE O USUÁRIO PARTICIPA
    @GET("groups/my")
    Call<Grupos> getMyGroups();

    //PARA JUNTAR-SE A UM GRUPO
    @GET("groups/{id}/join")
    Call<ResponseBody> joinGroup(@Path("id") int id);

    //PARA DEIXAR UM GRUPO
    @GET("groups/{id}/unjoin")
    Call<ResponseBody> unjoin(@Path("id") int id);

    //PARA DELETAR UM GRUPO
    @DELETE("groups/{id}")
    Call<ResponseBody> deletGroup(@Path("id") int id);

    //ATUALIZAR GRUPO
    @PUT("groups/{id}")
    Call<ResponseBody> editGroup(@Path("id") int id, @Body Grupo grupo);

    //RETORNA OS SPORTS
    @GET("sports")
    Call<Esportes> getSports();
//
//    //PARA CRIAR ATIVIDADE
//    @POST("groups/{id}/activity")
//    Call<ResponseBody> creatActivity(@Path("id") int id, @Body Atividade atividade);
//
//    //PARA ATUALIZAR ATIVIDADE
//    @PUT("groups/{id}/activity")
//    Call<ResponseBody> editActivity(@Path("id") int id, @Body Atividade atividade);
//
//    //PARA MARCAR PRESENÇA
//    //ATIVIDADE
//    @GET("groups/{id}/activity/join")
//    Call<ResponseBody> joinActivity(@Path("id") int id);
//
//    // PARA DESMARCAR PRESENÇA
//    // ATIVIDADE
//    @GET("groups/{id}/activity/unjoin")
//    Call<ResponseBody> unJoiActivity(@Path("id") int id);
//
//    // PARA PEGAR OS PATICIPATES
//    // ATIVIDADE
//    @GET("groups/{id}/activity/participants")
//    Call<ArrayList<Usuario>> getParticipantsActivity(@Path("id") int id);
//
//    // SEGUIR USUÁRIO
//    @POST("follow/{id}")
//    Call<ResponseBody> follow(@Path("id") int id);

}
