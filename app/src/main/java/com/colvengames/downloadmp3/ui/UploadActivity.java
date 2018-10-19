package com.colvengames.downloadmp3.ui;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;

import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.adapter.CategorySelectAdapter;
import com.colvengames.downloadmp3.adapter.SelectableViewHolder;
import com.colvengames.downloadmp3.api.ProgressRequestBody;
import com.colvengames.downloadmp3.api.apiClient;
import com.colvengames.downloadmp3.api.apiRest;
import com.colvengames.downloadmp3.entity.ApiResponse;
import com.colvengames.downloadmp3.entity.Category;
import com.colvengames.downloadmp3.manager.PrefManager;
import com.squareup.picasso.Picasso;


import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UploadActivity extends AppCompatActivity implements ProgressRequestBody.UploadCallbacks,SelectableViewHolder.OnItemSelectedListener  {

    private RelativeLayout relative_layout_upload;




    private int PICK_AUDIO = 1002;
    private Bitmap bitmap_wallpaper;
    private ProgressDialog register_progress;
    private FloatingActionButton fab_upload;
    private FloatingActionButton button_save_upload;
    private EditText edit_text_upload_title;
    private static final int CAMERA_REQUEST_IMAGE_1 = 3001;
    private String          file_url;
    private ArrayList<Category> categoriesListObj = new ArrayList<Category>();
    private CircleImageView circle_image_view_upload_user;
    private LinearLayoutManager gridLayoutManagerCategorySelect;
    private RecyclerView recycle_view_selected_category;
    private CategorySelectAdapter categorySelectAdapter;
    private ProgressBar progress_bar_progress_wallpaper_upload;
    private TextView text_view_progress_progress_wallpaper_upload;
    private RelativeLayout relative_layout_progress_wallpaper_upload
            ;
    private File file_final;
    private long file_duration;
    private ImageView image_view_item_ringtone_play;
    private ImageView image_view_item_ringtone_pause;


    private Integer playeditem = -1;
    private SimpleExoPlayer player;
    private MediaSource mediaSource;
    private TrackSelection.Factory trackSelectionFactory;
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private Handler mainHandler;
    private RenderersFactory renderersFactory;
    private BandwidthMeter bandwidthMeter;
    private LoadControl loadControl;
    private TrackSelector trackSelector;
    private RelativeLayout relative_layout_controllers;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        renderersFactory = new DefaultRenderersFactory(this);
        bandwidthMeter = new DefaultBandwidthMeter();
        trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        dataSourceFactory = new DefaultDataSourceFactory(this, "ExoplayerDemo");
        extractorsFactory = new DefaultExtractorsFactory();
        mainHandler = new Handler();


        initView();
        initAction();
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //  SelectWallpaper();
        getSupportActionBar().setTitle(getResources().getString(R.string.upload_ringtone));
    }

    private void initAction() {
        fab_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SelectWallpaper();
            }
        });
        button_save_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edit_text_upload_title.getText().toString().trim().length()<3){
                    Toasty.error(UploadActivity.this, getResources().getString(R.string.edit_text_upload_title_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (file_final==null){
                    Toasty.error(UploadActivity.this, getResources().getString(R.string.image_upload_error), Toast.LENGTH_SHORT).show();
                    return;
                }
                upload(CAMERA_REQUEST_IMAGE_1);
            }
        });
        this.image_view_item_ringtone_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                play();
                image_view_item_ringtone_play.setVisibility(View.GONE);
                image_view_item_ringtone_pause.setVisibility(View.VISIBLE);

            }
        });
        this.image_view_item_ringtone_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stop();
                image_view_item_ringtone_pause.setVisibility(View.GONE);
                image_view_item_ringtone_play.setVisibility(View.VISIBLE);
            }
        });
    }

    private void SelectWallpaper() {
        if (ContextCompat.checkSelfPermission(UploadActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(UploadActivity.this, new String[] { Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE }, 0);
        }else{


            final ArrayList<AudioObject> audioList = new ArrayList<>();

            String[] projection = {
                    MediaStore.Audio.Media._ID,
                    MediaStore.Audio.Media.ARTIST,
                    MediaStore.Audio.Media.TITLE,
                    MediaStore.Audio.Media.DATA,
                    MediaStore.Audio.Media.DISPLAY_NAME,
                    MediaStore.Audio.Media.DURATION
            };
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(UploadActivity.this, android.R.layout.select_dialog_singlechoice);

            Cursor audioCursor = getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, null, null, null);

            if(audioCursor != null){
                if(audioCursor.moveToFirst()){
                    do{
                        AudioObject audioObject= new AudioObject();
                        audioObject.setId(audioCursor.getString(0));
                        audioObject.setTitle(audioCursor.getString(2));
                        audioObject.setUrl(audioCursor.getString(3));
                        audioList.add(audioObject);
                        arrayAdapter.add(audioCursor.getString(2));
                    }while(audioCursor.moveToNext());
                }
            }
            audioCursor.close();


            AlertDialog.Builder builderSingle = new AlertDialog.Builder(UploadActivity.this);
            builderSingle.setIcon(R.drawable.ic_music_note);
            builderSingle.setTitle(getResources().getString(R.string.choose_your_ringtone));

            builderSingle.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builderSingle.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    edit_text_upload_title.setText(audioList.get(which).getTitle());
                    file_final= new File(audioList.get(which).getUrl());
                    file_url = audioList.get(which).getUrl();

                    Uri uri = Uri.parse(audioList.get(which).getUrl());
                    MediaMetadataRetriever mmr = new MediaMetadataRetriever();
                    mmr.setDataSource(getApplicationContext(),uri);
                    String durationStr = mmr.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
                    int millSecond = Integer.parseInt(durationStr);

                    file_duration =millSecond;
                    relative_layout_controllers.setVisibility(View.VISIBLE);
                }
            });
            builderSingle.show();

        }
    }

    private void initView() {

        this.relative_layout_controllers=(RelativeLayout) findViewById(R.id.relative_layout_controllers);
        this.image_view_item_ringtone_pause=(ImageView) findViewById(R.id.image_view_item_ringtone_pause);
        this.image_view_item_ringtone_play=(ImageView) findViewById(R.id.image_view_item_ringtone_play);
        this.circle_image_view_upload_user=(CircleImageView) findViewById(R.id.circle_image_view_upload_user);
        this.edit_text_upload_title=(EditText) findViewById(R.id.edit_text_upload_title);
        this.button_save_upload =(FloatingActionButton) findViewById(R.id.button_save_upload);
        button_save_upload.hide();

        this.fab_upload=(FloatingActionButton) findViewById(R.id.fab_upload);
        this.relative_layout_upload=(RelativeLayout) findViewById(R.id.relative_layout_upload);
        getCategory();



        PrefManager prf= new PrefManager(getApplicationContext());


        Picasso.with(getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(circle_image_view_upload_user);


        this.progress_bar_progress_wallpaper_upload=(ProgressBar) findViewById(R.id.progress_bar_progress_wallpaper_upload);
        this.text_view_progress_progress_wallpaper_upload=(TextView) findViewById(R.id.text_view_progress_progress_wallpaper_upload);
        this.relative_layout_progress_wallpaper_upload=(RelativeLayout) findViewById(R.id.relative_layout_progress_wallpaper_upload);

        gridLayoutManagerCategorySelect = new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);

        recycle_view_selected_category= (RecyclerView) findViewById(R.id.recycle_view_selected_category);

        hideProgress();
    }
    private void getCategory() {
        register_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Category>> call = service.categoryAll();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if(response.isSuccessful()){
                    categoriesListObj.clear();
                    for (int i = 0;i<response.body().size();i++){
                        categoriesListObj.add(response.body().get(i));
                    }
                    categorySelectAdapter = new CategorySelectAdapter(UploadActivity.this, categoriesListObj, true, UploadActivity.this);
                    recycle_view_selected_category.setHasFixedSize(true);
                    recycle_view_selected_category.setAdapter(categorySelectAdapter);
                    recycle_view_selected_category.setLayoutManager(gridLayoutManagerCategorySelect);
                    register_progress.dismiss();

                }else {
                    register_progress.dismiss();
                    Snackbar snackbar = Snackbar
                            .make(relative_layout_upload, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getCategory();
                                }
                            });
                    snackbar.setActionTextColor(android.graphics.Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(android.graphics.Color.YELLOW);
                    snackbar.show();
                }

            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                register_progress.dismiss();
                Snackbar snackbar = Snackbar
                        .make(relative_layout_upload, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getCategory();
                            }
                        });
                snackbar.setActionTextColor(android.graphics.Color.RED);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(android.graphics.Color.YELLOW);
                snackbar.show();
            }
        });
    }



    protected void onActivityResult(int requestCode, int resultCode, Intent data) {


    }

    public void upload(final int CODE){
        showProgress();
        PrefManager prf = new PrefManager(getApplicationContext());

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);

        //File creating from selected URL




        ProgressRequestBody requestFile = new ProgressRequestBody(file_final, UploadActivity.this);

        // create RequestBody instance from file
        // RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);

        // MultipartBody.Part is used to send also the actual file name
        MultipartBody.Part body =MultipartBody.Part.createFormData("uploaded_file",file_final.getName(), requestFile);
        String id_ser=  prf.getString("ID_USER");
        String key_ser=  prf.getString("TOKEN_USER");

        Call<ApiResponse> request = service.uploadRingtone(body,file_duration,id_ser,key_ser,edit_text_upload_title.getText().toString().trim(),getSelectedCategories());
        request.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                if (response.isSuccessful()){
                    Toasty.success(getApplication(),getResources().getString(R.string.ringtone_upload_success),Toast.LENGTH_LONG).show();
                    finish();
                }else{
                    Toasty.error(getApplication(),getResources().getString(R.string.no_connexion),Toast.LENGTH_LONG).show();

                }
                // file.delete();
                // getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
                hideProgress();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toasty.error(getApplication(),getResources().getString(R.string.no_connexion),Toast.LENGTH_LONG).show();
                hideProgress();
            }
        });


    }

    @Override
    public void onProgressUpdate(int percentage) {
        ProgressValue(percentage);
    }

    @Override
    public void onError() {
        hideProgress();
    }

    @Override
    public void onFinish() {
        hideProgress();
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        return;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                super.onBackPressed();
                overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onItemSelected(Category item) {

    }



    public String getSelectedCategories(){
        String categories = "";
        for (int i = 0; i < categorySelectAdapter.getSelectedItems().size(); i++) {
            categories+="_"+categorySelectAdapter.getSelectedItems().get(i).getId();
        }
        Log.v("categories",categories);

        return categories;
    }

    public void ProgressValue(Integer progress){
        progress_bar_progress_wallpaper_upload.setProgress(progress);
        text_view_progress_progress_wallpaper_upload.setText("Loading : " + progress + "%");
    }
    public void hideProgress() {

            Animation c= AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_down);
            c.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {
                    relative_layout_progress_wallpaper_upload.setVisibility(View.GONE);
                    button_save_upload.show();

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                }
                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            this.relative_layout_progress_wallpaper_upload.startAnimation(c);

    }
    public void showProgress(){
        button_save_upload.hide();
        Animation c= AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.slide_up);
        c.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                relative_layout_progress_wallpaper_upload.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        this.relative_layout_progress_wallpaper_upload.startAnimation(c);
    }
    public class AudioObject {
        private String id;
        private String title;
        private String url;

        public String getId() {
            return id;
        }

        public String getTitle() {
            return title;
        }

        public String getUrl() {
            return url;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
    @Override
    protected void onPause() {
        super.onPause();
        stop();
    }
    public void stop(){
        player.setPlayWhenReady(false);
        player.stop();

    }

    public  void play(){


        mediaSource = new ExtractorMediaSource(Uri.parse(file_url),
                dataSourceFactory,
                extractorsFactory,
                mainHandler,
                null);
        player.seekTo(0);
        player.addListener(new Player.EventListener() {
            @Override
            public void onTimelineChanged(Timeline timeline, Object manifest) {
                Log.v("v","onTimelineChanged");
            }

            @Override
            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                Log.v("v","onTracksChanged");

            }

            @Override
            public void onLoadingChanged(boolean isLoading) {
                Log.v("v","onLoadingChanged");

            }
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                if (playbackState == ExoPlayer.STATE_READY) {

                }
                if (playbackState == ExoPlayer.STATE_ENDED) {
                    player.stop();
                    player.seekToDefaultPosition();
                    mainHandler.removeCallbacksAndMessages(null);

                }

                Log.v("v", "onRepeatModeChanged" + playbackState + "-" + ExoPlayer.STATE_READY);


            }

            @Override
            public void onRepeatModeChanged(int repeatMode) {
                Log.v("v","onRepeatModeChanged");

            }

            @Override
            public void onPlayerError(ExoPlaybackException error) {
                Log.v("v","onPlayerError");

            }

            @Override
            public void onPositionDiscontinuity() {
                Log.v("v","onPositionDiscontinuity");

            }

            @Override
            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                Log.v("v","onPlaybackParametersChanged");

            }

        });
        player.setPlayWhenReady(true);
        player.prepare(mediaSource);
    }
}


