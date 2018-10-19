package com.colvengames.downloadmp3.ui;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.adapter.UserAdapter;
import com.colvengames.downloadmp3.adapter.RingtoneAdapter;
import com.colvengames.downloadmp3.api.apiClient;
import com.colvengames.downloadmp3.api.apiRest;
import com.colvengames.downloadmp3.entity.ApiResponse;
import com.colvengames.downloadmp3.entity.Ringtone;
import com.colvengames.downloadmp3.entity.Section;
import com.colvengames.downloadmp3.entity.Slide;
import com.colvengames.downloadmp3.entity.User;
import com.colvengames.downloadmp3.manager.PrefManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;
import pl.bclogic.pulsator4droid.library.PulsatorLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class UserActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private int id;
    private String name;
    private String image;
    private CircleImageView image_view_profile_user_activity;
    private TextView text_view_profile_user_activity;
    private TextView text_view_followers_count_user_activity;
    private TextView text_view_ringtone_count_activity_user;
    private TextView text_view_following_count_activity_user;
    private SwipeRefreshLayout swipe_refreshl_user_activity;
    private LinearLayout linear_layout_page_error;
    private ImageView image_view_empty;
    private RecyclerView recycle_view_user_activity;
    private RelativeLayout relative_layout_load_more;
    private RelativeLayout relative_layout_user_activity;
    private Button button_follow_user_activity;
    private List<Ringtone> ringtoneList =new ArrayList<>();
    private List<Slide> slideList=new ArrayList<>();
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private RingtoneAdapter ringtoneAdapter;
    private Integer page = 0;
    private Boolean loaded=false;
    private GridLayoutManager gridLayoutManager;
    private LinearLayout linear_layout_followers;
    private LinearLayout linear_layout_following;

    private AlertDialog.Builder builderFollowing;
    private List<User> followings=new ArrayList<>();

    private AlertDialog.Builder builderFollowers;
    private List<User> followers=new ArrayList<>();
    private ProgressDialog loading_progress;
    private Button button_try_again;


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


    private Integer item = 0 ;
    private Integer lines_beetween_ads = 8 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private ImageView image_view_ringtone_activity_trusted;
    private boolean trusted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras() ;

        renderersFactory = new DefaultRenderersFactory(this);
        bandwidthMeter = new DefaultBandwidthMeter();
        trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        dataSourceFactory = new DefaultDataSourceFactory(this, "ExoplayerDemo");
        extractorsFactory = new DefaultExtractorsFactory();
        mainHandler = new Handler();



        PrefManager prf= new PrefManager(getApplicationContext());

        this.id =  bundle.getInt("id");
        this.name =  bundle.getString("name");
        this.image =  bundle.getString("image");
        this.trusted =  bundle.getBoolean("trusted");

        getSection();
        initView();
        initAction();
        initUser();


    }
    public void getSection(){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Section>> call = service.SectionList();
        call.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(Call<List<Section>> call, Response<List<Section>> response) {
            }
            @Override
            public void onFailure(Call<List<Section>> call, Throwable t) {
            }
        });
    }
    private void initUser() {
        text_view_profile_user_activity.setText(
                R.string.name_admin
        );
        if (!image.isEmpty()){
            Picasso.with(getApplicationContext()).load(image).error(R.drawable.logo_r).placeholder(R.drawable.profile).into(this.image_view_profile_user_activity);
        }else{
            Picasso.with(getApplicationContext()).load(R.drawable.logo_r).error(R.drawable.logo_r).placeholder(R.drawable.profile).into(this.image_view_profile_user_activity);
        }
        PrefManager prf= new PrefManager(getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            Integer me = Integer.parseInt(prf.getString("ID_USER"));
            if (id==me){
                button_follow_user_activity.setVisibility(View.GONE);
            }
        }
        if (trusted){
            image_view_ringtone_activity_trusted.setVisibility(View.VISIBLE);
        }else {
            image_view_ringtone_activity_trusted.setVisibility(View.GONE);

        }
        getUser();
    }

    private void initView() {
        if (getResources().getString(R.string.FACEBOOK_ADS_ENABLED_NATIVE).equals("true")){
            native_ads_enabled=true;
            lines_beetween_ads=Integer.parseInt(getResources().getString(R.string.FACEBOOK_ADS_ITEM_BETWWEN_ADS));
        }
        PrefManager prefManager= new PrefManager(getApplicationContext());
        if (prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            native_ads_enabled=false;
        }
        setContentView(R.layout.activity_user);
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));

        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.image_view_ringtone_activity_trusted=(ImageView) findViewById(R.id.image_view_ringtone_activity_trusted);
        this.button_try_again=(Button) findViewById(R.id.button_try_again);
        this.image_view_profile_user_activity=(CircleImageView) findViewById(R.id.image_view_profile_user_activity);
        this.text_view_profile_user_activity=(TextView) findViewById(R.id.text_view_profile_user_activity);
        this.text_view_followers_count_user_activity =(TextView) findViewById(R.id.text_view_followers_count_user_activity);
        this.text_view_ringtone_count_activity_user=(TextView) findViewById(R.id.text_view_ringtone_count_activity_user);
        this.text_view_following_count_activity_user=(TextView) findViewById(R.id.text_view_following_count_activity_user);
        this.swipe_refreshl_user_activity=(SwipeRefreshLayout) findViewById(R.id.swipe_refreshl_user_activity);
        this.linear_layout_page_error=(LinearLayout) findViewById(R.id.linear_layout_page_error);
        this.image_view_empty=(ImageView) findViewById(R.id.image_view_empty);
        this.recycle_view_user_activity=(RecyclerView) findViewById(R.id.recycle_view_user_activity);
        this.relative_layout_load_more=(RelativeLayout) findViewById(R.id.relative_layout_load_more);
        this.relative_layout_user_activity=(RelativeLayout) findViewById(R.id.relative_layout_user_activity);
        this.button_follow_user_activity=(Button) findViewById(R.id.button_follow_user_activity);
        this.linear_layout_followers=(LinearLayout) findViewById(R.id.linear_layout_followers);
        this.linear_layout_following=(LinearLayout) findViewById(R.id.linear_layout_following);
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            this.gridLayoutManager = new GridLayoutManager(getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
        }else{
            this.gridLayoutManager = new GridLayoutManager(getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        }

        this.ringtoneAdapter =new RingtoneAdapter(ringtoneList,slideList,this,player,mediaSource,trackSelectionFactory,dataSourceFactory,extractorsFactory,mainHandler,renderersFactory,bandwidthMeter,loadControl,trackSelector,playeditem,false);

        recycle_view_user_activity.setHasFixedSize(true);
        recycle_view_user_activity.setAdapter(ringtoneAdapter);
        recycle_view_user_activity.setLayoutManager(gridLayoutManager);


        Typeface face = Typeface.createFromAsset(getAssets(), "Pattaya-Regular.ttf");
        text_view_profile_user_activity.setTypeface(face);

        PulsatorLayout pulsator = (PulsatorLayout) findViewById(R.id.pulsator);
        pulsator.start();
    }
    private void initAction() {
        this.swipe_refreshl_user_activity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                item = 0;
                page = 0;
                loading = true;
                slideList.clear();
                ringtoneList.clear();

                getUser();
            }
        });
        this.button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item = 0;
                page = 0;
                loading = true;
                slideList.clear();
                ringtoneList.clear();

                getUser();
            }
        });
        this.linear_layout_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                builderFollowing = new AlertDialog.Builder(UserActivity.this);
                builderFollowing.setIcon(R.drawable.ic_follow);
                builderFollowing.setTitle("Following");

                View view = (View)  getLayoutInflater().inflate(R.layout.user_row,null);

                ListView listView= (ListView) view.findViewById(R.id.user_rows);
                listView.setAdapter(new UserAdapter(followings,UserActivity.this));
                builderFollowing.setView(view);
                builderFollowing.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builderFollowing.show();
            }
        });
        this.linear_layout_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFollowers();

            }
        });
        this.linear_layout_following.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFollowings();
            }
        });
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        this.button_follow_user_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                follow();
            }
        });
        recycle_view_user_activity.addOnScrollListener(new RecyclerView.OnScrollListener()
        {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy)
            {
                if(dy > 0) //check for scroll down
                {

                    visibleItemCount    = gridLayoutManager.getChildCount();
                    totalItemCount      = gridLayoutManager.getItemCount();
                    pastVisiblesItems   = gridLayoutManager.findFirstVisibleItemPosition();

                    if (loading)
                    {
                        if ( (visibleItemCount + pastVisiblesItems) >= totalItemCount)
                        {
                            loading = false;
                            loadNextRingtones();
                        }
                    }
                }else{

                }
            }
        });
    }
    public void loadFollowers(){
        loading_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<User>> call = service.getFollowers(id);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    for (int i = 0; i < followers.size(); i++) {
                        followers.add(response.body().get(i));
                    }
                    builderFollowers = new AlertDialog.Builder(UserActivity.this);
                    builderFollowers.setIcon(R.drawable.ic_follow);
                    builderFollowers.setTitle("Followers");
                    View view = (View)  getLayoutInflater().inflate(R.layout.user_row,null);
                    ListView listView= (ListView) view.findViewById(R.id.user_rows);
                    listView.setAdapter(new UserAdapter(response.body(),UserActivity.this));
                    builderFollowers.setView(view);
                    builderFollowers.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderFollowers.show();
                }
                loading_progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading_progress.dismiss();
            }
        });

    }
    public void loadFollowings(){
        loading_progress= ProgressDialog.show(this, null,getResources().getString(R.string.operation_progress), true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<User>> call = service.getFollowing(id);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    builderFollowing = new AlertDialog.Builder(UserActivity.this);
                    builderFollowing.setIcon(R.drawable.ic_follow);
                    builderFollowing.setTitle("Followings");
                    View view = (View)  getLayoutInflater().inflate(R.layout.user_row,null);
                    ListView listView= (ListView) view.findViewById(R.id.user_rows);
                    listView.setAdapter(new UserAdapter(response.body(),UserActivity.this));
                    builderFollowing.setView(view);
                    builderFollowing.setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builderFollowing.show();
                }
                loading_progress.dismiss();
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loading_progress.dismiss();
            }
        });

    }
    @Override
    public void onBackPressed(){
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_enter, R.anim.slide_exit);
        return;
    }
    public void follow(){

        PrefManager prf= new PrefManager(getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            button_follow_user_activity.setText(getResources().getString(R.string.loading));
            button_follow_user_activity.setEnabled(false);
            String follower = prf.getString("ID_USER");
            String key = prf.getString("TOKEN_USER");
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.follow(id, Integer.parseInt(follower), key);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    if (response.isSuccessful()) {
                        if (response.body().getCode().equals(200)){
                            button_follow_user_activity.setText("UnFollow");
                            text_view_followers_count_user_activity.setText((Integer.parseInt(text_view_followers_count_user_activity.getText().toString())+1)+"");
                        }else if (response.body().getCode().equals(202)) {
                            button_follow_user_activity.setText("Follow");
                            text_view_followers_count_user_activity.setText((Integer.parseInt(text_view_followers_count_user_activity.getText().toString())-1)+"");

                        }
                    }
                    button_follow_user_activity.setEnabled(true);

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    button_follow_user_activity.setEnabled(true);
                }
            });
        }else{
            Intent intent = new Intent(UserActivity.this,LoginActivity.class);
            startActivity(intent);
        }
    }
    private void getUser() {
        PrefManager prf= new PrefManager(getApplicationContext());
        Integer follower= -1;
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            button_follow_user_activity.setEnabled(false);
            follower = Integer.parseInt(prf.getString("ID_USER"));
        }
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.getUser(id,follower);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()){

                    for (int i=0;i<response.body().getValues().size();i++){
                        if (response.body().getValues().get(i).getName().equals("followers")){
                            text_view_followers_count_user_activity.setText(format(Integer.parseInt(response.body().getValues().get(i).getValue())));
                        }
                        if (response.body().getValues().get(i).getName().equals("followings")){
                            text_view_following_count_activity_user.setText(format(Integer.parseInt(response.body().getValues().get(i).getValue())));
                        }
                        if (response.body().getValues().get(i).getName().equals("ringtones")){
                            text_view_ringtone_count_activity_user.setText(format(Integer.parseInt(response.body().getValues().get(i).getValue())));
                        }
                        if (response.body().getValues().get(i).getName().equals("follow")){
                            if (response.body().getValues().get(i).getValue().equals("true"))
                                button_follow_user_activity.setText("UnFollow");
                            else
                                button_follow_user_activity.setText("Follow");
                        }
                    }

                }else{
                    Snackbar snackbar = Snackbar
                            .make(relative_layout_user_activity, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                            .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    getUser();
                                }
                            });
                    snackbar.setActionTextColor(Color.RED);
                    View sbView = snackbar.getView();
                    TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                    textView.setTextColor(Color.YELLOW);
                    snackbar.show();

                }
                button_follow_user_activity.setEnabled(true);
                loadRingtones();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                button_follow_user_activity.setEnabled(true);

                Snackbar snackbar = Snackbar
                        .make(relative_layout_user_activity, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
                        .setAction(getResources().getString(R.string.retry), new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getUser();
                            }
                        });
                snackbar.setActionTextColor(Color.RED);
                View sbView = snackbar.getView();
                TextView textView = (TextView) sbView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setTextColor(Color.YELLOW);
                snackbar.show();
                loadRingtones();

            }
        });
    }
    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();
    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "G");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }
    public static String format(long value) {
        //Long.MIN_VALUE == -Long.MIN_VALUE so we need an adjustment here
        if (value == Long.MIN_VALUE) return format(Long.MIN_VALUE + 1);
        if (value < 0) return "-" + format(-value);
        if (value < 1000) return Long.toString(value); //deal with easy case

        Map.Entry<Long, String> e = suffixes.floorEntry(value);
        Long divideBy = e.getKey();
        String suffix = e.getValue();

        long truncated = value / (divideBy / 10); //the number part of the output times 10
        boolean hasDecimal = truncated < 100 && (truncated / 10d) != (truncated / 10);
        return hasDecimal ? (truncated / 10d) + suffix : (truncated / 10) + suffix;
    }
    private void loadNextRingtones() {
        relative_layout_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Ringtone>> call = service.ringtoneByUser(page,id);
        call.enqueue(new Callback<List<Ringtone>>() {
            @Override
            public void onResponse(Call<List<Ringtone>> call, Response<List<Ringtone>> response) {
                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        for (int i=0;i<response.body().size();i++){
                            ringtoneList.add(response.body().get(i));
                            if (native_ads_enabled){
                                item++;
                                if (item == lines_beetween_ads ){
                                    item= 0;
                                    ringtoneList.add(new Ringtone().setViewType(4));
                                }
                            }
                        }
                        ringtoneAdapter.notifyDataSetChanged();
                        page++;
                        loading=true;
                    }
                }
                relative_layout_load_more.setVisibility(View.GONE);
            }
            @Override
            public void onFailure(Call<List<Ringtone>> call, Throwable t) {
                relative_layout_load_more.setVisibility(View.GONE);
            }
        });
    }

    private void loadRingtones() {

        recycle_view_user_activity.setVisibility(View.GONE);
        image_view_empty.setVisibility(View.GONE);
        linear_layout_page_error.setVisibility(View.GONE);
        swipe_refreshl_user_activity.setRefreshing(true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Ringtone>> call = service.ringtoneByUser(page,id);
        call.enqueue(new Callback<List<Ringtone>>() {
            @Override
            public void onResponse(Call<List<Ringtone>> call, Response<List<Ringtone>> response) {
                apiClient.FormatData(UserActivity.this,response);

                if(response.isSuccessful()){
                    ringtoneList.clear();
                    if (response.body().size()!=0){
                        for (int i=0;i<response.body().size();i++){
                            ringtoneList.add(response.body().get(i));
                            if (native_ads_enabled){
                                item++;
                                if (item == lines_beetween_ads ){
                                    item= 0;
                                    ringtoneList.add(new Ringtone().setViewType(4));
                                }
                            }
                        }
                        ringtoneAdapter.notifyDataSetChanged();
                        page++;
                        loaded=true;

                        recycle_view_user_activity.setVisibility(View.VISIBLE);
                        image_view_empty.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }else {
                        recycle_view_user_activity.setVisibility(View.GONE);
                        image_view_empty.setVisibility(View.VISIBLE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }
                }else{
                    recycle_view_user_activity.setVisibility(View.GONE);
                    image_view_empty.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
                swipe_refreshl_user_activity.setRefreshing(false);

            }
            @Override
            public void onFailure(Call<List<Ringtone>> call, Throwable t) {
                recycle_view_user_activity.setVisibility(View.GONE);
                image_view_empty.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_user_activity.setRefreshing(false);

            }
        });
    }
}
