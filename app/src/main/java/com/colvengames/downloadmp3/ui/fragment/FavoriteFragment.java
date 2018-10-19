package com.colvengames.downloadmp3.ui.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.colvengames.downloadmp3.entity.Ringtone;
import com.colvengames.downloadmp3.entity.Slide;
import com.colvengames.downloadmp3.manager.FavoritesStorage;
import com.colvengames.downloadmp3.manager.PrefManager;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteFragment extends Fragment {

    private Integer page = 0;
    private Boolean loaded=false;

    private View view;
    private RelativeLayout relative_layout_favorites_fragment;
    private SwipeRefreshLayout swipe_refreshl_favorites_fragment;
    private ImageView image_view_empty;
    private RecyclerView recycle_view_favorites_fragment;
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
    private boolean tabletSize;
    private Boolean native_ads_enabled = false ;

    public FavoriteFragment() {



    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        renderersFactory = new DefaultRenderersFactory(getActivity());
        bandwidthMeter = new DefaultBandwidthMeter();
        trackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        trackSelector = new DefaultTrackSelector(trackSelectionFactory);
        loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(renderersFactory, trackSelector, loadControl);

        dataSourceFactory = new DefaultDataSourceFactory(getActivity(), "ExoplayerDemo");
        extractorsFactory = new DefaultExtractorsFactory();
        mainHandler = new Handler();


        view= inflater.inflate(R.layout.fragment_favorite, container, false);
        initView();
        initAction();

        return view;
    }

    private void initAction() {
        swipe_refreshl_favorites_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadFavorites();
            }
        });
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
        this.relative_layout_favorites_fragment=(RelativeLayout) view.findViewById(R.id.relative_layout_favorites_fragment);
        this.swipe_refreshl_favorites_fragment=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_favorites_fragment);
        this.image_view_empty=(ImageView) view.findViewById(R.id.image_view_empty);
        this.recycle_view_favorites_fragment=(RecyclerView) view.findViewById(R.id.recycle_view_favorites_fragment);
        this.relative_layout_load_more=(RelativeLayout) view.findViewById(R.id.relative_layout_load_more);
        this.linear_layout_page_error=(LinearLayout) view.findViewById(R.id.linear_layout_page_error);
        this.button_try_again=(Button) view.findViewById(R.id.button_try_again);
        boolean tabletSize = getResources().getBoolean(R.bool.isTablet);
        if (tabletSize) {
            this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 2, GridLayoutManager.VERTICAL, false);
        }else{
            this.gridLayoutManager = new GridLayoutManager(getActivity().getApplicationContext(), 1, GridLayoutManager.VERTICAL, false);
        }
        this.ringtoneAdapter =new RingtoneAdapter(ringtoneList,slideList,getActivity(),player,mediaSource,trackSelectionFactory,dataSourceFactory,extractorsFactory,mainHandler,renderersFactory,bandwidthMeter,loadControl,trackSelector,playeditem,true);
        recycle_view_favorites_fragment.setHasFixedSize(true);
        recycle_view_favorites_fragment.setAdapter(ringtoneAdapter);
        recycle_view_favorites_fragment.setLayoutManager(gridLayoutManager);

        ImageView viewimga = view.findViewById(R.id.backhome);

        Picasso.with(getContext()).load(Uri.parse(HomeFragment.urlHome)).into(viewimga);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            if (!loaded)
                loadFavorites();
        }
        else{

        }
    }

    private void loadFavorites() {
        swipe_refreshl_favorites_fragment.setRefreshing(true);
        final FavoritesStorage storageFavorites= new FavoritesStorage(getActivity().getApplicationContext());
        List<Ringtone> ringtones = storageFavorites.loadFavorites();

        if (ringtones ==null){
            ringtones = new ArrayList<>();
        }
        if (ringtones.size()!=0){
            ringtoneList.clear();
            for (int i = 0; i< ringtones.size(); i++){
                Ringtone a= new Ringtone();
                a = ringtones.get(i) ;
                ringtoneList.add(a);
            }
            ringtoneAdapter.notifyDataSetChanged();
            image_view_empty.setVisibility(View.GONE);
            recycle_view_favorites_fragment.setVisibility(View.VISIBLE);
        }else{
            image_view_empty.setVisibility(View.VISIBLE);
            recycle_view_favorites_fragment.setVisibility(View.GONE);
        }

        swipe_refreshl_favorites_fragment.setRefreshing(false);

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
