package com.colvengames.downloadmp3.ui.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.colvengames.downloadmp3.manager.PrefManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {
    private Integer page = 0;
    private Boolean loaded=false;

    public static String urlHome = "http://besosdeamor.info/forallringtons/bk.jpg";

    private View view;
    private RelativeLayout relative_layout_home_fragment;
    private ImageView back;
    private SwipeRefreshLayout swipe_refreshl_home_fragment;
    private ImageView image_view_empty;
    private RecyclerView recycle_view_home_fragment;
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_page_error;
    private Button button_try_again;
    private GridLayoutManager gridLayoutManager;
    private int pastVisiblesItems, visibleItemCount, totalItemCount;
    private boolean loading = true;
    private RingtoneAdapter ringtoneAdapter;
    private List<Ringtone> ringtoneList =new ArrayList<>();
    private List<Slide> slideList=new ArrayList<>();


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
    private Boolean native_ads_enabled = false ;

    public HomeFragment() {





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



        this.view =   inflater.inflate(R.layout.fragment_home, container, false);
        initView();
        initAction();
        if (!loaded)
            loadSlide();
        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            if (!loaded){
                //loadData();
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
        this.relative_layout_home_fragment=(RelativeLayout) view.findViewById(R.id.relative_layout_home_fragment);
        this.swipe_refreshl_home_fragment=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_home_fragment);
        back = view.findViewById(R.id.backhome);
        this.image_view_empty=(ImageView) view.findViewById(R.id.image_view_empty);
        this.recycle_view_home_fragment=(RecyclerView) view.findViewById(R.id.recycle_view_home_fragment);
        this.relative_layout_load_more=(RelativeLayout) view.findViewById(R.id.relative_layout_load_more);
        this.linear_layout_page_error=(LinearLayout) view.findViewById(R.id.linear_layout_page_error);
        this.button_try_again=(Button) view.findViewById(R.id.button_try_again);
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0) ? 2 : 1;
                }
            });
        } else {
            this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
            gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    return (position == 0) ? 1 : 1;
                }
            });
        }
        this.ringtoneAdapter =new RingtoneAdapter(ringtoneList,slideList,getActivity(),player,mediaSource,trackSelectionFactory,dataSourceFactory,extractorsFactory,mainHandler,renderersFactory,bandwidthMeter,loadControl,trackSelector,playeditem,false);
        recycle_view_home_fragment.setHasFixedSize(true);
        recycle_view_home_fragment.setAdapter(ringtoneAdapter);
        recycle_view_home_fragment.setLayoutManager(gridLayoutManager);




if(!urlHome.isEmpty()) {

    Picasso.with(getContext()).load(Uri.parse(urlHome)).into(back);
}




    }

    private void initAction() {

        recycle_view_home_fragment.addOnScrollListener(new RecyclerView.OnScrollListener()
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
        this.swipe_refreshl_home_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                item = 0;
                page = 0;
                loading = true;
                slideList.clear();
                ringtoneList.clear();
                loadSlide();
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
                loadSlide();
            }
        });

    }

    private void loadSlide() {

        recycle_view_home_fragment.setVisibility(View.GONE);
        image_view_empty.setVisibility(View.GONE);
        linear_layout_page_error.setVisibility(View.GONE);
        swipe_refreshl_home_fragment.setRefreshing(true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Slide>> call = service.slideAll();
        call.enqueue(new Callback<List<Slide>>() {
            @Override
            public void onResponse(Call<List<Slide>> call, Response<List<Slide>> response) {
                apiClient.FormatData(getActivity(),response);
                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        for (int i=0;i<response.body().size();i++){
                            slideList.add(response.body().get(i));
                        }
                        ringtoneList.add(new Ringtone().setViewType(2));
                    }
                    loadRingtones();

                }else{
                    recycle_view_home_fragment.setVisibility(View.GONE);
                    image_view_empty.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                    swipe_refreshl_home_fragment.setRefreshing(false);
                }

            }
            @Override
            public void onFailure(Call<List<Slide>> call, Throwable t) {
                recycle_view_home_fragment.setVisibility(View.GONE);
                image_view_empty.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_home_fragment.setRefreshing(false);
            }
        });
    }
    private void loadNextRingtones() {
        relative_layout_load_more.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Ringtone>> call = service.ringtoneAll("created",page);
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
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Ringtone>> call = service.ringtoneAll("created",page);
        call.enqueue(new Callback<List<Ringtone>>() {
            @Override
            public void onResponse(Call<List<Ringtone>> call, Response<List<Ringtone>> response) {
                if(response.isSuccessful()){
                    if (response.body().size()!=0){

                        for (int i=0;i<response.body().size();i++){
                           // Log.e("MAIN", "onResponse: "+response.body().get(i).getTags());
                            if(response.body().get(i).getTags() != null && response.body().get(i).getTags().contains(getResources().getString(R.string.tagMain))) {
                                ringtoneList.add(response.body().get(i));


                                if (native_ads_enabled) {
                                    item++;
                                    if (item == lines_beetween_ads) {
                                        item = 0;
                                        ringtoneList.add(new Ringtone().setViewType(4));
                                    }
                                }
                            }
                        }
                        ringtoneAdapter.notifyDataSetChanged();
                        page++;
                        loaded=true;

                        recycle_view_home_fragment.setVisibility(View.VISIBLE);
                        image_view_empty.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }else {
                        recycle_view_home_fragment.setVisibility(View.GONE);
                        image_view_empty.setVisibility(View.VISIBLE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }
                }else{
                    recycle_view_home_fragment.setVisibility(View.GONE);
                    image_view_empty.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
                swipe_refreshl_home_fragment.setRefreshing(false);

            }
            @Override
            public void onFailure(Call<List<Ringtone>> call, Throwable t) {
                recycle_view_home_fragment.setVisibility(View.GONE);
                image_view_empty.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_home_fragment.setRefreshing(false);

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
}
