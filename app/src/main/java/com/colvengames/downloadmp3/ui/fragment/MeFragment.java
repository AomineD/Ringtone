package com.colvengames.downloadmp3.ui.fragment;


import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.colvengames.downloadmp3.entity.Slide;
import com.colvengames.downloadmp3.entity.User;
import com.colvengames.downloadmp3.manager.PrefManager;
import com.colvengames.downloadmp3.ui.LoginActivity;
import com.colvengames.downloadmp3.ui.MainActivity;
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

/**
 * A simple {@link Fragment} subclass.
 */
public class MeFragment extends Fragment {

    private Integer page = 0;
    private Boolean loaded=false;

    private View view;
    private RelativeLayout relative_layout_me_fragment;
    private SwipeRefreshLayout swipe_refreshl_me_fragment;
    private ImageView image_view_empty;
    private RecyclerView recycle_view_me_fragment;
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_page_error;
    private Button button_try_again;
    private GridLayoutManager gridLayoutManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private RingtoneAdapter ringtoneAdapter;
    private List<Ringtone> ringtoneList =new ArrayList<>();
    private List<Slide> slideList=new ArrayList<>();
    private Button button_login_nav_me_fragment;
    private LinearLayout linear_layout_me_fragment_me;
    private Integer id_user;
    private TextView text_view_profile_me_fragment;
    private CircleImageView image_view_profile_me_fragment;
    private TextView text_view_following_count_fragmenet_me;
    private TextView text_view_ringtones_count_fragmenet_me;
    private TextView text_view_followers_count_fragmenet_me;

    private AlertDialog.Builder builderFollowing;
    private List<User> followings=new ArrayList<>();

    private AlertDialog.Builder builderFollowers;
    private List<User> followers=new ArrayList<>();
    private ProgressDialog loading_progress;
    private LinearLayout linear_layout_me_fragment_followings;
    private LinearLayout linear_layout_me_fragment_followers;




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
    private ImageView image_view_me_fragement_trusted;

    public MeFragment() {



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        renderersFactory = new DefaultRenderersFactory(getActivity());
        bandwidthMeter = new DefaultBandwidthMeter();
        trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        dataSourceFactory = new DefaultDataSourceFactory(getActivity(), "ExoplayerDemo");
        extractorsFactory = new DefaultExtractorsFactory();
        mainHandler = new Handler();



        view=  inflater.inflate(R.layout.fragment_me, container, false);
        initView();
        initAction();

        return  view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            if (!loaded) {
                item = 0;
                page = 0;
                loading = true;
                slideList.clear();
                ringtoneList.clear();
                loadRingtoness();
            }
        }
        else{

        }
    }

    private void initAction() {
        recycle_view_me_fragment.addOnScrollListener(new RecyclerView.OnScrollListener()
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
                            loadNextRingtoness();
                        }
                    }
                }else{

                }
            }
        });
        this.swipe_refreshl_me_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                item = 0;
                loading = true;
                slideList.clear();
                ringtoneList.clear();
                loadRingtoness();
            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                item = 0;
                page = 0;
                loading = true;
                slideList.clear();
                ringtoneList.clear();
                loadRingtoness();
            }
        });
        this.button_login_nav_me_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity)getActivity()).setFromLogin();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });
        this.linear_layout_me_fragment_followers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFollowers();
            }
        });
        this.linear_layout_me_fragment_followings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadFollowings();
            }
        });
    }
    public void initView(){
        if (getResources().getString(R.string.FACEBOOK_ADS_ENABLED_NATIVE).equals("true")){
            native_ads_enabled=true;
            lines_beetween_ads=Integer.parseInt(getResources().getString(R.string.FACEBOOK_ADS_ITEM_BETWWEN_ADS));
        }
        PrefManager prefManager= new PrefManager(getActivity().getApplicationContext());
        if (prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            native_ads_enabled=false;
        }
        PulsatorLayout pulsator = (PulsatorLayout) view.findViewById(R.id.pulsator);
        pulsator.start();
        this.image_view_me_fragement_trusted=(ImageView)  view.findViewById(R.id.image_view_me_fragement_trusted);
        this.linear_layout_me_fragment_followers= (LinearLayout) view.findViewById(R.id.linear_layout_me_fragment_followers);
        this.linear_layout_me_fragment_followings= (LinearLayout) view.findViewById(R.id.linear_layout_me_fragment_followings);
        this.text_view_followers_count_fragmenet_me=(TextView) view.findViewById(R.id.text_view_followers_count_fragmenet_me);
        this.text_view_following_count_fragmenet_me=(TextView) view.findViewById(R.id.text_view_following_count_fragmenet_me);
        this.text_view_ringtones_count_fragmenet_me=(TextView) view.findViewById(R.id.text_view_ringtones_count_fragmenet_me);
        this.text_view_profile_me_fragment=(TextView) view.findViewById(R.id.text_view_profile_me_fragment);
        this.image_view_profile_me_fragment=(CircleImageView) view.findViewById(R.id.image_view_profile_me_fragment);
        this.relative_layout_me_fragment=(RelativeLayout) view.findViewById(R.id.relative_layout_me_fragment);
        this.swipe_refreshl_me_fragment=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_me_fragment);
        this.image_view_empty=(ImageView) view.findViewById(R.id.image_view_empty);
        this.recycle_view_me_fragment=(RecyclerView) view.findViewById(R.id.recycle_view_me_fragment);
        this.relative_layout_load_more=(RelativeLayout) view.findViewById(R.id.relative_layout_load_more);
        this.linear_layout_me_fragment_me=(LinearLayout) view.findViewById(R.id.linear_layout_me_fragment_me);
        this.linear_layout_page_error=(LinearLayout) view.findViewById(R.id.linear_layout_page_error);
        this.button_try_again=(Button) view.findViewById(R.id.button_try_again);
        this.button_login_nav_me_fragment=(Button) view.findViewById(R.id.button_login_nav_me_fragment);


        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
        }else{
            this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        }


        this.ringtoneAdapter =new RingtoneAdapter(ringtoneList,slideList,getActivity(),player,mediaSource,trackSelectionFactory,dataSourceFactory,extractorsFactory,mainHandler,renderersFactory,bandwidthMeter,loadControl,trackSelector,playeditem,false);
        recycle_view_me_fragment.setHasFixedSize(true);
        recycle_view_me_fragment.setAdapter(ringtoneAdapter);
        recycle_view_me_fragment.setLayoutManager(gridLayoutManager);


        Typeface face = Typeface.createFromAsset(getActivity().getAssets(), "Pattaya-Regular.ttf");
        text_view_profile_me_fragment.setTypeface(face);

        PrefManager prf= new PrefManager(getActivity().getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            this.id_user = Integer.parseInt(prf.getString("ID_USER"));
            linear_layout_me_fragment_me.setVisibility(View.GONE);
            relative_layout_me_fragment.setVisibility(View.VISIBLE);

            text_view_profile_me_fragment.setText(prf.getString("NAME_USER").toString());
            Picasso.with(getActivity().getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(image_view_profile_me_fragment);

        }else{
            linear_layout_me_fragment_me.setVisibility(View.VISIBLE);
            relative_layout_me_fragment.setVisibility(View.GONE);
        }


        ImageView viewimga = view.findViewById(R.id.backhome);

        Picasso.with(getContext()).load(Uri.parse(HomeFragment.urlHome)).into(viewimga);
    }
    private void loadNextRingtoness() {
        relative_layout_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Ringtone>> call = service.ringtonesByMe(page,id_user);
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
    private void loadRingtoness() {

        recycle_view_me_fragment.setVisibility(View.GONE);
        image_view_empty.setVisibility(View.GONE);
        linear_layout_page_error.setVisibility(View.GONE);
        swipe_refreshl_me_fragment.setRefreshing(true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Ringtone>> call = service.ringtonesByMe(page,id_user);
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
                        loaded=true;

                        recycle_view_me_fragment.setVisibility(View.VISIBLE);
                        image_view_empty.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }else {
                        recycle_view_me_fragment.setVisibility(View.GONE);
                        image_view_empty.setVisibility(View.VISIBLE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }
                }else{
                    recycle_view_me_fragment.setVisibility(View.GONE);
                    image_view_empty.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
                swipe_refreshl_me_fragment.setRefreshing(false);
                getUser();
            }
            @Override
            public void onFailure(Call<List<Ringtone>> call, Throwable t) {
                recycle_view_me_fragment.setVisibility(View.GONE);
                image_view_empty.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_me_fragment.setRefreshing(false);

            }
        });
    }

    public void Resume() {
        try {
            PrefManager prf= new PrefManager(getActivity().getApplicationContext());

            if (prf.getString("LOGGED").toString().equals("TRUE")){
                relative_layout_me_fragment.setVisibility(View.VISIBLE);
                linear_layout_me_fragment_me.setVisibility(View.GONE);

                text_view_profile_me_fragment.setText(prf.getString("NAME_USER").toString());
                Picasso.with(getActivity().getApplicationContext()).load(prf.getString("IMAGE_USER").toString()).placeholder(R.drawable.profile).error(R.drawable.profile).resize(200,200).centerCrop().into(image_view_profile_me_fragment);

                this.id_user = Integer.parseInt(prf.getString("ID_USER"));

                item = 0;
                page = 0;
                loading = true;
                slideList.clear();
                ringtoneList.clear();

                loadRingtoness();

            }else{
                relative_layout_me_fragment.setVisibility(View.GONE);
                linear_layout_me_fragment_me.setVisibility(View.VISIBLE);
            }
        }catch (java.lang.NullPointerException e){
            startActivity(new Intent(getContext(),MainActivity.class));
            getActivity().finish();
        }
    }
    private void getUser() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.getUser(id_user,-1);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                apiClient.FormatData(getActivity(),response);
                if (response.isSuccessful()){

                    for (int i=0;i<response.body().getValues().size();i++){
                        if (response.body().getValues().get(i).getName().equals("followers")){
                            text_view_followers_count_fragmenet_me.setText(format(Integer.parseInt(response.body().getValues().get(i).getValue())));
                        }
                        if (response.body().getValues().get(i).getName().equals("followings")){
                            text_view_following_count_fragmenet_me.setText(format(Integer.parseInt(response.body().getValues().get(i).getValue())));
                        }
                        if (response.body().getValues().get(i).getName().equals("ringtones")){
                            text_view_ringtones_count_fragmenet_me.setText(format(Integer.parseInt(response.body().getValues().get(i).getValue())));
                        }
                        if (response.body().getValues().get(i).getName().equals("trusted")){
                            if (response.body().getValues().get(i).getValue().equals("true")){
                                image_view_me_fragement_trusted.setVisibility(View.VISIBLE);
                            }else {
                                image_view_me_fragement_trusted.setVisibility(View.GONE);
                            }
                        }
                    }
                }else{
                    Snackbar snackbar = Snackbar
                            .make(relative_layout_me_fragment, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
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

            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Snackbar snackbar = Snackbar
                        .make(relative_layout_me_fragment, getResources().getString(R.string.no_connexion), Snackbar.LENGTH_INDEFINITE)
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
        });
    }
    public void loadFollowers(){
        loading_progress= ProgressDialog.show(getActivity(), null,getResources().getString(R.string.operation_progress), true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<User>> call = service.getFollowers(id_user);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    for (int i = 0; i < followers.size(); i++) {
                        followers.add(response.body().get(i));
                    }
                    builderFollowers = new AlertDialog.Builder(getActivity());
                    builderFollowers.setIcon(R.drawable.ic_follow);
                    builderFollowers.setTitle("Followers");
                    View view = (View)  getLayoutInflater().inflate(R.layout.user_row,null);
                    ListView listView= (ListView) view.findViewById(R.id.user_rows);
                    listView.setAdapter(new UserAdapter(response.body(),getActivity()));
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
        loading_progress= ProgressDialog.show(getActivity(), null,getResources().getString(R.string.operation_progress), true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<User>> call = service.getFollowing(id_user);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                if (response.isSuccessful()){
                    builderFollowing = new AlertDialog.Builder(getActivity());
                    builderFollowing.setIcon(R.drawable.ic_follow);
                    builderFollowing.setTitle("Followings");
                    View view = (View)  getLayoutInflater().inflate(R.layout.user_row,null);
                    ListView listView= (ListView) view.findViewById(R.id.user_rows);
                    listView.setAdapter(new UserAdapter(response.body(),getActivity()));
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
    @Override
    public void onPause() {
        super.onPause();
        stop();

    }
    public void stop(){
        player.setPlayWhenReady(false);
        player.stop();
        for (int i = 0; i < ringtoneList.size(); i++) {
            ringtoneList.get(i).setPreparing(false);
            ringtoneList.get(i).setPlaying(false);
        }
        ringtoneAdapter.notifyDataSetChanged();
    }
}
