package com.colvengames.downloadmp3.ui.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

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
import com.colvengames.downloadmp3.adapter.RingtoneAdapter;
import com.colvengames.downloadmp3.api.apiClient;
import com.colvengames.downloadmp3.api.apiRest;
import com.colvengames.downloadmp3.entity.Ringtone;
import com.colvengames.downloadmp3.entity.Slide;
import com.colvengames.downloadmp3.entity.User;
import com.colvengames.downloadmp3.manager.PrefManager;
import com.colvengames.downloadmp3.ui.LoginActivity;
import com.colvengames.downloadmp3.ui.MainActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FollowFragment extends Fragment {

    private Integer page = 0;
    private Boolean loaded=false;

    private View view;
    private RelativeLayout relative_layout_follow_fragment;
    private SwipeRefreshLayout swipe_refreshl_follow_fragment;
    private ImageView image_view_empty;
    private RecyclerView recycle_view_follow_fragment;
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_page_error;
    private Button button_try_again;
    private GridLayoutManager gridLayoutManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private RingtoneAdapter ringtoneAdapter;
    private List<Ringtone> ringtoneList =new ArrayList<>();
    private List<Slide> slideList=new ArrayList<>();
    private List<User> userList=new ArrayList<>();


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
    private LinearLayout linear_layout_follow_fragment_me;


    private Integer item = 0 ;
    private Integer lines_beetween_ads = 8 ;
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;
    private Integer id_user;
    private Button button_login_nav_follow_fragment;


    public FollowFragment() {




        // Required empty public constructor
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

        this.view =   inflater.inflate(R.layout.fragment_follow, container, false);
        initView();
        initAction();

        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            if (!loaded) {
                page = 0;
                item = 0;

                loading = true;
                ringtoneList.clear();
                userList.clear();
                loadFollowings();
            }
        }
        else{

        }
    }

    private void initView() {
        if (getResources().getString(R.string.FACEBOOK_ADS_ENABLED_NATIVE).equals("true")){
            native_ads_enabled=true;
            lines_beetween_ads=Integer.parseInt(getResources().getString(R.string.FACEBOOK_ADS_ITEM_BETWWEN_ADS));
        }
        PrefManager prefManager= new PrefManager(getActivity().getApplicationContext());
        if (prefManager.getString("SUBSCRIBED").equals("TRUE")) {
            native_ads_enabled=false;
        }
        this.linear_layout_follow_fragment_me=(LinearLayout) view.findViewById(R.id.linear_layout_follow_fragment_me);
        this.relative_layout_follow_fragment=(RelativeLayout) view.findViewById(R.id.relative_layout_follow_fragment);
        this.swipe_refreshl_follow_fragment=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_follow_fragment);
        this.image_view_empty=(ImageView) view.findViewById(R.id.image_view_empty);
        this.recycle_view_follow_fragment=(RecyclerView) view.findViewById(R.id.recycle_view_follow_fragment);
        this.relative_layout_load_more=(RelativeLayout) view.findViewById(R.id.relative_layout_load_more);
        this.linear_layout_page_error=(LinearLayout) view.findViewById(R.id.linear_layout_page_error);
        this.button_try_again=(Button) view.findViewById(R.id.button_try_again);
        this.button_login_nav_follow_fragment=(Button) view.findViewById(R.id.button_login_nav_follow_fragment);
        this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),2,GridLayoutManager.VERTICAL,false);


        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
        }else{
            this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        }

        this.ringtoneAdapter =new RingtoneAdapter(ringtoneList,slideList,getActivity(),player,mediaSource,trackSelectionFactory,dataSourceFactory,extractorsFactory,mainHandler,renderersFactory,bandwidthMeter,loadControl,trackSelector,playeditem,false,userList);
        recycle_view_follow_fragment.setHasFixedSize(true);
        recycle_view_follow_fragment.setAdapter(ringtoneAdapter);
        recycle_view_follow_fragment.setLayoutManager(gridLayoutManager);
        PrefManager prf= new PrefManager(getActivity().getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            this.id_user = Integer.parseInt(prf.getString("ID_USER"));
            linear_layout_follow_fragment_me.setVisibility(View.GONE);
            relative_layout_follow_fragment.setVisibility(View.VISIBLE);
        }else{
            linear_layout_follow_fragment_me.setVisibility(View.VISIBLE);
            relative_layout_follow_fragment.setVisibility(View.GONE);
        }
    }

    private void initAction() {

        recycle_view_follow_fragment.addOnScrollListener(new RecyclerView.OnScrollListener()
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
        this.swipe_refreshl_follow_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 0;
                item = 0;

                loading = true;
                ringtoneList.clear();
                userList.clear();
                loadFollowings();
            }
        });
        button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                page = 0;
                item = 0;

                loading = true;
                ringtoneList.clear();
                userList.clear();
                loadFollowings();
            }
        });
        this.button_login_nav_follow_fragment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((MainActivity) getActivity()).setFromLogin();
                Intent intent = new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
            }
        });

        ImageView viewimga = view.findViewById(R.id.backhome);

        Picasso.with(getContext()).load(Uri.parse(HomeFragment.urlHome)).into(viewimga);

    }


    private void loadNextRingtones() {

        relative_layout_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Ringtone>> call = service.followRingtone(page,id_user);
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

        recycle_view_follow_fragment.setVisibility(View.GONE);
        image_view_empty.setVisibility(View.GONE);
        linear_layout_page_error.setVisibility(View.GONE);
        swipe_refreshl_follow_fragment.setRefreshing(true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Ringtone>> call = service.followRingtone(page,id_user);
        call.enqueue(new Callback<List<Ringtone>>() {
            @Override
            public void onResponse(Call<List<Ringtone>> call, Response<List<Ringtone>> response) {
                apiClient.FormatData(getActivity(),response);
                if(response.isSuccessful()){
                    if (response.body().size()!=0){

                        for (int i=0;i<response.body().size();i++){
                            if(response.body().get(i).getTags().contains("iphone")){
                                Log.e("MAIN", "onResponse: "+response.body().get(i).getTitle());
                            ringtoneList.add(response.body().get(i));
                            }

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

                        recycle_view_follow_fragment.setVisibility(View.VISIBLE);
                        image_view_empty.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }
                }
                swipe_refreshl_follow_fragment.setRefreshing(false);

            }
            @Override
            public void onFailure(Call<List<Ringtone>> call, Throwable t) {

            }
        });

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
    public void loadFollowings(){
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<User>> call = service.getFollowingTop(id_user);
        call.enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {

                if (response.isSuccessful()){

                    if (response.body().size()>0){
                        ringtoneList.add(new Ringtone().setViewType(3));
                        for (int i=0;i<response.body().size();i++){
                            userList.add(response.body().get(i));
                        }
                        ringtoneAdapter.notifyDataSetChanged();
                    }
                    loadRingtones();
                }

            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                loadRingtones();
            }
        });

    }
    public void Resume() {
        try {
            PrefManager prf= new PrefManager(getActivity().getApplicationContext());

            if (prf.getString("LOGGED").toString().equals("TRUE")){
                relative_layout_follow_fragment.setVisibility(View.VISIBLE);
                linear_layout_follow_fragment_me.setVisibility(View.GONE);


                this.id_user = Integer.parseInt(prf.getString("ID_USER"));

                page = 0;
                item = 0;
                loading = true;
                ringtoneList.clear();

                loadFollowings();

            }else{
                relative_layout_follow_fragment.setVisibility(View.GONE);
                linear_layout_follow_fragment_me.setVisibility(View.VISIBLE);
            }
        }catch (java.lang.NullPointerException e){
            startActivity(new Intent(getContext(),MainActivity.class));
            getActivity().finish();
        }
    }

}
