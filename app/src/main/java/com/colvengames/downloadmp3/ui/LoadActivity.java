package com.colvengames.downloadmp3.ui;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.api.apiClient;
import com.colvengames.downloadmp3.api.apiRest;
import com.colvengames.downloadmp3.entity.Ringtone;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoadActivity extends AppCompatActivity {


    private  Integer id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load);
        Uri data = this.getIntent().getData();
        if (data==null){
            Bundle bundle = getIntent().getExtras() ;
            this.id =  bundle.getInt("id");
        }else{
            this.id=Integer.parseInt(data.getPath().replace("/share/","").replace(".html",""));
        }
        getArticle();
    }
    public void getArticle(){

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Ringtone> call = service.ringtoneById(id);
        call.enqueue(new retrofit2.Callback<Ringtone>() {
            @Override
            public void onResponse(Call<Ringtone> call, Response<Ringtone> response) {
                if(response.isSuccessful()) {
                    Intent intent = new Intent(LoadActivity.this, RingtoneActivity.class);
                    intent.putExtra("id", response.body().getId());
                    intent.putExtra("title", response.body().getTitle());
                    intent.putExtra("premium", response.body().getPremium());
                    intent.putExtra("trusted", response.body().getTrusted());
                    intent.putExtra("userid", response.body().getUserid());
                    intent.putExtra("size", response.body().getSize());
                    intent.putExtra("user", response.body().getUser());
                    intent.putExtra("userimage", response.body().getUserimage());
                    intent.putExtra("type", response.body().getType());
                    intent.putExtra("duration", response.body().getDuration());
                    intent.putExtra("ringtone", response.body().getRingtone());
                    intent.putExtra("extension",response.body().getExtension());
                    intent.putExtra("downloads", response.body().getDownloads());
                    intent.putExtra("created",response.body().getCreated());
                    intent.putExtra("tags", response.body().getTags());
                    intent.putExtra("description", response.body().getDescription());
                    startActivity(intent);
                    overridePendingTransition(R.anim.enter, R.anim.exit);
                }
            }
            @Override
            public void onFailure(Call<Ringtone> call, Throwable t) {

            }
        });
    }
}
