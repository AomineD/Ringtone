package com.colvengames.downloadmp3.manager;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.colvengames.downloadmp3.entity.Ringtone;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by hsn on 30/11/2017.
 */



public class FavoritesStorage {
    private final String STORAGE = "MY_FAVORITE";
    private SharedPreferences preferences;
    private Context context;
    public FavoritesStorage(Context context) {
        this.context = context;
    }
    public void storeAudio(ArrayList<Ringtone> arrayList) {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(arrayList);
        editor.putString("favoritesList", json);
        editor.apply();
    }

    public ArrayList<Ringtone> loadFavorites() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferences.getString("favoritesList", null);
        Type type = new TypeToken<ArrayList<Ringtone>>() {
        }.getType();
        return gson.fromJson(json, type);
    }
    public void clearCachedAudioPlaylist() {
        preferences = context.getSharedPreferences(STORAGE, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

}

