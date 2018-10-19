package com.colvengames.downloadmp3.entity;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created by hsn on 27/11/2017.
 */

public class Ringtone {



    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("size")
    @Expose
    private String size;
    @SerializedName("review")
    @Expose
    private Boolean review;
    @SerializedName("downloads")
    @Expose
    private Integer downloads;
    @SerializedName("user")
    @Expose
    private String user;
    @SerializedName("userid")
    @Expose
    private Integer userid;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("tags")
    @Expose
    private String tags;
    @SerializedName("rating")
    @Expose
    private String rating;
    @SerializedName("duration")
    @Expose
    private int duration;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("extension")
    @Expose
    private String extension;
    @SerializedName("ringtone")
    @Expose
    private String ringtone;
    @SerializedName("userimage")
    @Expose
    private String userimage;
    @SerializedName("created")
    @Expose
    private String created;

    @SerializedName("premium")
    @Expose
    private Boolean premium;

    private Boolean playing =  false;
    private Boolean preparing =  false;


    private int viewType = 1;

    @SerializedName("trusted")
    @Expose
    private Boolean trusted;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Boolean getReview() {
        return review;
    }

    public void setReview(Boolean review) {
        this.review = review;
    }

    public Integer getDownloads() {
        return downloads;
    }

    public void setDownloads(Integer downloads) {
        this.downloads = downloads;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getExtension() {
        return extension;
    }

    public void setExtension(String extension) {
        this.extension = extension;
    }

    public String getRingtone() {
        return ringtone;
    }

    public void setRingtone(String ringtone) {
        this.ringtone = ringtone;
    }

    public String getUserimage() {
        return userimage;
    }

    public void setUserimage(String userimage) {
        this.userimage = userimage;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void setPlaying(Boolean playing) {
        this.playing = playing;
    }

    public void setPreparing(Boolean preparing) {
        this.preparing = preparing;
    }

    public Ringtone setViewType(int viewType) {
        this.viewType = viewType;
        return this;
    }

    public Boolean getPlaying() {
        return playing;
    }

    public Boolean getPreparing() {
        return preparing;
    }

    public int getViewType() {
        return viewType;
    }

    public void setPremium(Boolean premium) {
        this.premium = premium;
    }

    public Boolean getPremium() {
        return premium;
    }

    public void setTrusted(Boolean trusted) {
        this.trusted = trusted;
    }

    public Boolean getTrusted() {
        return trusted;
    }
}
