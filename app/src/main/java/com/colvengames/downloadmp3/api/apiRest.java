package com.colvengames.downloadmp3.api;

import com.colvengames.downloadmp3.config.Config;
import com.colvengames.downloadmp3.entity.ApiResponse;
import com.colvengames.downloadmp3.entity.Category;
import com.colvengames.downloadmp3.entity.Section;
import com.colvengames.downloadmp3.entity.Slide;
import com.colvengames.downloadmp3.entity.Tag;
import com.colvengames.downloadmp3.entity.User;
import com.colvengames.downloadmp3.entity.Ringtone;

import java.util.List;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by hsn on 27/11/2017.
 */

public interface apiRest {
    @FormUrlEncoded
    @POST("user/register/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> register(@Field("name") String name, @Field("username") String username, @Field("password") String password, @Field("type") String type, @Field("image") String image);

    @GET("version/check/{code}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> check(@Path("code") Integer code);

    @GET("device/{tkn}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addDevice(@Path("tkn")  String tkn);

    @GET("ringtone/all/{order}/{page}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Ringtone>> ringtoneAll(@Path("order") String order, @Path("page") Integer page);

    @GET("ringtone/related/{id}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Ringtone>> ringtoneRelated(@Path("id") Integer id);


    @GET("ringtone/category/{page}/{category}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Ringtone>> ringtoneByCategory(@Path("page") Integer page, @Path("category") Integer category);

    @GET("ringtone/get/{id}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<Ringtone> ringtoneById(@Path("id")  Integer id);

    @GET("ringtone/query/{page}/{query}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Ringtone>> ringtoneBysearch(@Path("page") Integer page, @Path("query") String query);

    @GET("ringtone/user/{page}/{user}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Ringtone>> ringtoneByUser(@Path("page") Integer page, @Path("user") Integer user);

    @GET("ringtone/me/{page}/{user}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Ringtone>> ringtonesByMe(@Path("page") Integer page, @Path("user") Integer user);

    @GET("slide/all/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Slide>> slideAll();

    @GET("section/list/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Section>> SectionList();

    @GET("category/list/{id}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Category>> categoryList(@Path("id") Integer id);

    @GET("category/all/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Category>> categoryAll();



    @GET("rate/add/{user}/{ringtone}/{value}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addRate(@Path("user")  String user,@Path("ringtone") Integer ringtone,@Path("value") float value);

    @GET("rate/get/{user}/{ringtone}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> getRate(@Path("user")  String user,@Path("ringtone") Integer ringtone);

    @FormUrlEncoded
    @POST("report/add/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addReport(@Field("ringtone") Integer ringtone, @Field("message") String message);

    @FormUrlEncoded
    @POST("support/add/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addSupport(@Field("email") String email, @Field("name") String name , @Field("message") String message);

    @GET("install/add/{id}/"+ Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> addInstall(@Path("id") String id);


    @FormUrlEncoded
    @POST("ringtone/add/download/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<Integer> addDownload(@Field("id")  Integer id);

    @Multipart
    @POST("ringtone/upload/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> uploadRingtone(@Part MultipartBody.Part file, @Part("duration") long duration, @Part("id") String id, @Part("key") String key, @Part("title") String title,  @Part("categories") String categories);

    @GET("user/get/{user}/{me}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> getUser(@Path("user") Integer user,@Path("me") Integer me);

    @FormUrlEncoded
    @POST("user/token/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> editToken(@Field("user") Integer user,@Field("key") String key,@Field("token_f") String token_f);

    @GET("category/by/{id}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Category>> CategoriesBy(@Path("id") Integer id);


    @GET("user/follow/{user}/{follower}/{key}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<ApiResponse> follow(@Path("user") Integer user,@Path("follower") Integer follower,@Path("key") String key);


    @GET("user/followers/{user}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<User>> getFollowers(@Path("user") Integer user);

    @GET("user/followings/{user}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<User>> getFollowing(@Path("user") Integer user);

    @GET("tags/all/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Tag>> TagList();


    @GET("user/followingstop/{user}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<User>> getFollowingTop(@Path("user") Integer user);


    @GET("ringtone/by/follow/{page}/{user}/"+Config.TOKEN_APP+"/"+Config.ITEM_PURCHASE_CODE+"/")
    Call<List<Ringtone>> followRingtone(@Path("page") Integer page, @Path("user") Integer user);


}

