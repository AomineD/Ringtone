package com.colvengames.downloadmp3.adapter;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.colvengames.downloadmp3.services.DownloadForShare;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.facebook.ads.NativeBannerAd;
import com.github.vivchar.viewpagerindicator.ViewPagerIndicator;

import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.RenderersFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.entity.Ringtone;
import com.colvengames.downloadmp3.entity.Slide;
import com.colvengames.downloadmp3.entity.User;
import com.colvengames.downloadmp3.manager.FavoritesStorage;
import com.colvengames.downloadmp3.ui.RingtoneActivity;
import com.colvengames.downloadmp3.ui.view.ClickableViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

/**
 * Created by hsn on 27/11/2017.
 */

public class RingtoneAdapter extends  RecyclerView.Adapter<RecyclerView.ViewHolder>{


    private  List<User> userList = new ArrayList<>();
    private  Runnable runnable;
    private boolean favorites = false;
    private List<Ringtone> ringtoneList;
    private Activity activity;
    private List<Slide> slideList= new ArrayList<>();
    private SlideAdapter slide_adapter;
    private Handler mainHandler;
    private RenderersFactory renderersFactory;
    private BandwidthMeter bandwidthMeter;
    private LoadControl loadControl;
    private DataSource.Factory dataSourceFactory;
    private ExtractorsFactory extractorsFactory;
    private MediaSource mediaSource;
    private TrackSelection.Factory trackSelectionFactory;
    private SimpleExoPlayer player;
    private TrackSelector trackSelector;
    private LinearLayoutManager linearLayoutManager;
    private Integer playeditem = -1;
    private FollowAdapter followAdapter;

    // private Timer mTimer;


    public RingtoneAdapter(
            List<Ringtone> ringtoneList,
            List<Slide> slideList,
            Activity activity,
            SimpleExoPlayer player,
            MediaSource mediaSource,
            TrackSelection.Factory trackSelectionFactory,
            DataSource.Factory dataSourceFactory,
            ExtractorsFactory extractorsFactory,
            Handler mainHandler,
            RenderersFactory renderersFactory,
            BandwidthMeter bandwidthMeter,
            LoadControl loadControl,
            TrackSelector trackSelector,
            Integer playeditem,
            Boolean favorites
    ) {
        this.ringtoneList=ringtoneList;
        this.activity=activity;
        this.slideList = slideList;
        this.favorites=favorites;

        this.renderersFactory = renderersFactory;
        this.bandwidthMeter =bandwidthMeter;
        this.trackSelectionFactory =trackSelectionFactory;
        this.trackSelector = trackSelector;
        this.loadControl = loadControl;
        this.player = player;

        this.dataSourceFactory = dataSourceFactory;
        this.extractorsFactory = extractorsFactory;
        this.mainHandler = mainHandler;
        this.playeditem = playeditem;
    }

    public RingtoneAdapter(
            List<Ringtone> ringtoneList,
            List<Slide> slideList,
            Activity activity,
            SimpleExoPlayer player,
            MediaSource mediaSource,
            TrackSelection.Factory trackSelectionFactory,
            DataSource.Factory dataSourceFactory,
            ExtractorsFactory extractorsFactory,
            Handler mainHandler,
            RenderersFactory renderersFactory,
            BandwidthMeter bandwidthMeter,
            LoadControl loadControl,
            TrackSelector trackSelector,
            Integer playeditem,
            Boolean favorites,
            List<User> userList
    ) {
        this.ringtoneList=ringtoneList;
        this.activity=activity;
        this.slideList = slideList;
        this.favorites=favorites;

        this.renderersFactory = renderersFactory;
        this.bandwidthMeter =bandwidthMeter;
        this.trackSelectionFactory =trackSelectionFactory;
        this.trackSelector = trackSelector;
        this.loadControl = loadControl;
        this.player = player;

        this.dataSourceFactory = dataSourceFactory;
        this.extractorsFactory = extractorsFactory;
        this.mainHandler = mainHandler;
        this.playeditem = playeditem;
        this.userList = userList;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v1 = inflater.inflate(R.layout.item_ringtone, parent, false);
                viewHolder = new RingtoneHolder(v1);
               break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_slide, parent, false);
                viewHolder = new SlideHolder(v2);
                break;
            }
            case 3: {
                View v3 = inflater.inflate(R.layout.item_followings, parent, false);
                viewHolder = new FollowHolder(v3);
                break;
            }
            case 4:{
                View v5 = inflater.inflate(R.layout.item_facebook_banner_ads, parent, false);
                viewHolder = new FacebookNativeHolder(v5);
                break;
            }
        }
        return viewHolder;
    }
    @Override
    public int getItemViewType(int position) {
        if (ringtoneList.get(position) == null){
            return  1;
        }else{
            return ringtoneList.get(position).getViewType();
        }
    }
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder_parent, final int position) {

        switch (ringtoneList.get(position).getViewType()) {
            case 1:{

                final RingtoneHolder holder = (RingtoneHolder) holder_parent;

                final FavoritesStorage storageFavorites = new FavoritesStorage(activity.getApplicationContext());
                List<Ringtone> ringtones = storageFavorites.loadFavorites();
                Boolean exist = false;
                if (ringtones == null) {
                    ringtones = new ArrayList<>();
                }
                for (int i = 0; i < ringtones.size(); i++) {
                    if (ringtones.get(i).getId().equals(ringtoneList.get(position).getId())) {
                        exist = true;
                    }
                }
                if (exist == false) {
                    holder.like_button_fav_item_ringtone.setLiked(false);
                } else {
                    holder.like_button_fav_item_ringtone.setLiked(true);
                }



holder.share_btn.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        //activity.getString(R.string.share_url)+"https://play.google.com/store/apps/details?id="+activity.getPackageName()

        final DownloadForShare download = new DownloadForShare(activity.getApplicationContext());

        download.execute(ringtoneList.get(position).getRingtone());

        ProgressDialog dialog = new ProgressDialog(activity.getApplicationContext());
        dialog.setMessage("Loading");
        dialog.setIndeterminate(true);
        dialog.setCancelable(true);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                download.cancel(true);
            }
        });


        //Log.e("MAIN", "onClick: url "+ringtoneList.get(position).getRingtone());
        intent.putExtra(Intent.EXTRA_STREAM, Uri.parse(Environment.getExternalStorageDirectory().getAbsolutePath()+"/test.mp3"));
        intent.setType("audio/*");
       activity.startActivity(Intent.createChooser(intent, activity.getString(R.string.send_info)));
    }
});



                holder.like_button_fav_item_ringtone.setOnLikeListener(new OnLikeListener() {
                    @Override
                    public void liked(LikeButton likeButton) {
                        try {
                            List<Ringtone> favorites_list = storageFavorites.loadFavorites();
                            Boolean exist = false;
                            if (favorites_list == null) {
                                favorites_list = new ArrayList<>();
                            }

                            for (int i = 0; i < favorites_list.size(); i++) {
                                if (favorites_list.get(i).getId().equals(ringtoneList.get(position).getId())) {
                                    exist = true;
                                }
                            }

                            if (exist == false) {
                                ArrayList<Ringtone> audios = new ArrayList<Ringtone>();

                                for (int i = 0; i < favorites_list.size(); i++) {
                                    audios.add(favorites_list.get(i));
                                }
                                audios.add(ringtoneList.get(position));
                                storageFavorites.storeAudio(audios);
                                holder.like_button_fav_item_ringtone.setLiked(true);

                            } else {
                                ArrayList<Ringtone> new_favorites = new ArrayList<Ringtone>();
                                for (int i = 0; i < favorites_list.size(); i++) {
                                    if (!favorites_list.get(i).getId().equals(ringtoneList.get(position).getId())) {
                                        new_favorites.add(favorites_list.get(i));

                                    }
                                }
                                if (favorites) {

                                    ringtoneList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();

                                }
                                storageFavorites.storeAudio(new_favorites);
                                holder.like_button_fav_item_ringtone.setLiked(false);
                            }
                        } catch (IndexOutOfBoundsException e) {
                            try {
                                ringtoneList.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                            } catch (IndexOutOfBoundsException ex) {

                            }
                        }
                    }

                    @Override
                    public void unLiked(LikeButton likeButton) {
                        try {
                            List<Ringtone> favorites_list = storageFavorites.loadFavorites();
                            Boolean exist = false;
                            if (favorites_list == null) {
                                favorites_list = new ArrayList<>();
                            }

                            for (int i = 0; i < favorites_list.size(); i++) {
                                if (favorites_list.get(i).getId().equals(ringtoneList.get(position).getId())) {
                                    exist = true;
                                }
                            }

                            if (exist == false) {
                                ArrayList<Ringtone> audios = new ArrayList<Ringtone>();

                                for (int i = 0; i < favorites_list.size(); i++) {
                                    audios.add(favorites_list.get(i));
                                }
                                audios.add(ringtoneList.get(position));
                                storageFavorites.storeAudio(audios);
                                holder.like_button_fav_item_ringtone.setLiked(true);

                            } else {
                                ArrayList<Ringtone> new_favorites = new ArrayList<Ringtone>();
                                for (int i = 0; i < favorites_list.size(); i++) {
                                    if (!favorites_list.get(i).getId().equals(ringtoneList.get(position).getId())) {
                                        new_favorites.add(favorites_list.get(i));

                                    }
                                }
                                if (favorites) {

                                    ringtoneList.get(position).setPlaying(false);
                                    player.stop();
                                    player.seekToDefaultPosition();
                                    mainHandler.removeCallbacksAndMessages(null);
                                    notifyDataSetChanged();
                                    notifyDataSetChanged();


                                    ringtoneList.remove(position);
                                    notifyItemRemoved(position);
                                    notifyDataSetChanged();

                                }
                                storageFavorites.storeAudio(new_favorites);
                                holder.like_button_fav_item_ringtone.setLiked(false);
                            }
                        } catch (IndexOutOfBoundsException e) {
                            try {
                                ringtoneList.remove(position);
                                notifyItemRemoved(position);
                                notifyDataSetChanged();
                            } catch (IndexOutOfBoundsException ex) {

                            }
                        }
                    }
                });
                int step = 1;
                int final_step = 1;
                for (int i = 1; i < position + 1; i++) {
                    if (i == position + 1) {
                        final_step = step;
                    }
                    step++;
                    if (step > 7) {
                        step = 1;
                    }
                }
                holder.progress_bar_item_ringtone_play.getIndeterminateDrawable().setColorFilter(activity.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
                holder.text_view_item_ringtone_title.setText(ringtoneList.get(position).getTitle());
                if(ringtoneList.get(position).getTags().contains("iphone")){
                    //Log.e("MAIN", "Es Iphone!"+ringtoneList.get(position).getTags());
                }
                holder.text_view_item_ringtone_author.setText(ringtoneList.get(position).getUser());
                holder.text_view_item_ringtone_downloads.setText(format(ringtoneList.get(position).getDownloads()));
                holder.text_view_item_ringtone_duration.setText(secToTime(ringtoneList.get(position).getDuration()));
                if (ringtoneList.get(position).getPremium()){
                    holder.text_view_item_ringtone_premium.setVisibility(View.VISIBLE);
                }else{
                    holder.text_view_item_ringtone_premium.setVisibility(View.GONE);
                }
                if (ringtoneList.get(position).getPreparing()) {
                    holder.progress_bar_item_ringtone_play.setVisibility(View.VISIBLE);
                    holder.image_view_item_ringtone_play.setVisibility(View.GONE);
                    holder.image_view_item_ringtone_pause.setVisibility(View.GONE);
                } else {
                    if (ringtoneList.get(position).getPlaying()) {
                        holder.progress_bar_item_ringtone_play.setVisibility(View.GONE);
                        holder.image_view_item_ringtone_play.setVisibility(View.GONE);
                        holder.image_view_item_ringtone_pause.setVisibility(View.VISIBLE);
                    } else {
                        Log.v("PAUSE", "YES");
                        holder.progress_bar_item_ringtone_play.setVisibility(View.GONE);
                        holder.image_view_item_ringtone_play.setVisibility(View.VISIBLE);
                        holder.image_view_item_ringtone_pause.setVisibility(View.GONE);
                    }
                }
                if (position == playeditem) {
                    if (ringtoneList.get(position).getPlaying()) {
                        Log.v("v", "I4MHERE");
                        Integer initial = (int) ((player.getCurrentPosition() * 1000) / player.getDuration());
                        Log.v("v", "I4MHERE" + initial + "-" + player.getCurrentPosition());
                        ProgressBarAnimation anim = new ProgressBarAnimation(holder.progress_bar_item_ringtone_background, initial, 1000);
                        anim.setDuration(player.getDuration() - player.getCurrentPosition());
                        holder.progress_bar_item_ringtone_background.startAnimation(anim);
                    } else {
                        holder.progress_bar_item_ringtone_background.setProgress(0);
                    }
                } else {
                    holder.progress_bar_item_ringtone_background.setProgress(0);
                }

                holder.image_view_item_ringtone_play.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        for (int i = 0; i < ringtoneList.size(); i++) {
                            ringtoneList.get(i).setPlaying(false);
                            ringtoneList.get(i).setPreparing(false);
                        }
                        ringtoneList.get(position).setPreparing(true);
                        notifyDataSetChanged();
                        playeditem = position;
                        Log.v("url", ringtoneList.get(position).getRingtone());
                        mediaSource = new ExtractorMediaSource(Uri.parse(ringtoneList.get(position).getRingtone()),
                                dataSourceFactory,
                                extractorsFactory,
                                mainHandler,
                                null);
                        player.seekTo(0);
                        player.addListener(new Player.EventListener() {
                            @Override
                            public void onTimelineChanged(Timeline timeline, Object manifest) {
                                Log.v("v", "onTimelineChanged");
                            }

                            @Override
                            public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
                                Log.v("v", "onTracksChanged");

                            }

                            @Override
                            public void onLoadingChanged(boolean isLoading) {
                                Log.v("v", "onLoadingChanged");

                            }

                            @Override
                            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {

                                if (playbackState == ExoPlayer.STATE_READY) {
                                    if (playeditem == position) {
                                        ringtoneList.get(position).setPlaying(true);
                                        ringtoneList.get(position).setPreparing(false);
                                        Log.v("getDuration", "=" + player.getDuration());
                                    } else {
                                        ringtoneList.get(position).setPlaying(false);
                                        ringtoneList.get(position).setPreparing(false);
                                    }
                                    notifyDataSetChanged();
                                }
                                if (playbackState == ExoPlayer.STATE_ENDED) {
                                    ringtoneList.get(position).setPlaying(false);
                                    player.stop();
                                    player.seekToDefaultPosition();
                                    mainHandler.removeCallbacksAndMessages(null);
                                    notifyDataSetChanged();
                                    notifyDataSetChanged();

                                }

                                Log.v("v", "onRepeatModeChanged" + playbackState + "-" + ExoPlayer.STATE_READY);


                            }

                            @Override
                            public void onRepeatModeChanged(int repeatMode) {
                                Log.v("v", "onRepeatModeChanged");

                            }

                            @Override
                            public void onPlayerError(ExoPlaybackException error) {
                                Log.v("v", "onPlayerError");

                            }

                            @Override
                            public void onPositionDiscontinuity() {
                                Log.v("v", "onPositionDiscontinuity");

                            }

                            @Override
                            public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {
                                Log.v("v", "onPlaybackParametersChanged");

                            }

                        });
                        player.setPlayWhenReady(true);
                        player.prepare(mediaSource);

                    }
                });
                holder.image_view_item_ringtone_pause.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ringtoneList.get(position).setPlaying(false);
                        player.stop();
                        player.seekToDefaultPosition();
                        mainHandler.removeCallbacksAndMessages(null);
                        notifyDataSetChanged();
                        notifyDataSetChanged();
                    }
                });
                holder.progress_bar_item_ringtone_background.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(activity, RingtoneActivity.class);
                        intent.putExtra("id", ringtoneList.get(position).getId());
                        intent.putExtra("title", ringtoneList.get(position).getTitle());
                        intent.putExtra("premium", ringtoneList.get(position).getPremium());
                        intent.putExtra("trusted", ringtoneList.get(position).getTrusted());
                        intent.putExtra("userid", ringtoneList.get(position).getUserid());
                        intent.putExtra("size", ringtoneList.get(position).getSize());
                        intent.putExtra("user", ringtoneList.get(position).getUser());
                        intent.putExtra("userimage", ringtoneList.get(position).getUserimage());
                        intent.putExtra("type", ringtoneList.get(position).getType());
                        intent.putExtra("duration", ringtoneList.get(position).getDuration());
                        intent.putExtra("ringtone", ringtoneList.get(position).getRingtone());
                        intent.putExtra("extension", ringtoneList.get(position).getExtension());
                        intent.putExtra("downloads", ringtoneList.get(position).getDownloads());
                        intent.putExtra("created", ringtoneList.get(position).getCreated());
                        intent.putExtra("tags", ringtoneList.get(position).getTags());
                        intent.putExtra("description", ringtoneList.get(position).getDescription());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }
                });
                switch (step) {
                    case 1:
                        holder.progress_bar_item_ringtone_background.setProgressDrawable(activity.getResources().getDrawable(R.drawable.bg_progress_item_1));

                        break;
                    case 2:
                        holder.progress_bar_item_ringtone_background.setProgressDrawable(activity.getResources().getDrawable(R.drawable.bg_progress_item_2));
                        break;
                    case 3:
                        holder.progress_bar_item_ringtone_background.setProgressDrawable(activity.getResources().getDrawable(R.drawable.bg_progress_item_3));
                        break;
                    case 4:
                        holder.progress_bar_item_ringtone_background.setProgressDrawable(activity.getResources().getDrawable(R.drawable.bg_progress_item_4));
                        break;
                    case 5:
                        holder.progress_bar_item_ringtone_background.setProgressDrawable(activity.getResources().getDrawable(R.drawable.bg_progress_item_5));
                        break;
                    case 6:
                        holder.progress_bar_item_ringtone_background.setProgressDrawable(activity.getResources().getDrawable(R.drawable.bg_progress_item_6));
                        break;
                    case 7:
                        holder.progress_bar_item_ringtone_background.setProgressDrawable(activity.getResources().getDrawable(R.drawable.bg_progress_item_7));
                        break;
                }
            }
            break;
            case 2: {
                final SlideHolder holder = (SlideHolder) holder_parent;

                slide_adapter = new SlideAdapter(activity, slideList);
                holder.view_pager_slide.setAdapter(this.slide_adapter);
                holder.view_pager_slide.setOffscreenPageLimit(1);

                holder.view_pager_slide.setClipToPadding(false);
                holder.view_pager_slide.setPageMargin(0);
                holder.view_pager_indicator.setupWithViewPager(holder.view_pager_slide);

                holder.view_pager_slide.setCurrentItem(slideList.size() / 2);
            }
            break;
            case 3: {
                final RingtoneAdapter.FollowHolder holder = (RingtoneAdapter.FollowHolder) holder_parent;
                this.linearLayoutManager=  new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false);
                this.followAdapter =new FollowAdapter(userList,activity);
                holder.recycle_view_follow_items.setHasFixedSize(true);
                holder.recycle_view_follow_items.setAdapter(followAdapter);
                holder.recycle_view_follow_items.setLayoutManager(linearLayoutManager);
                followAdapter.notifyDataSetChanged();
                break;
            }
            case 4:{
                Log.e("MAIN", "onBindViewHolder: ");
            }
        }
    }
    private class SlideHolder extends RecyclerView.ViewHolder {
        private final ViewPagerIndicator view_pager_indicator;
        private final ClickableViewPager view_pager_slide;
        public SlideHolder(View itemView) {
            super(itemView);
            this.view_pager_indicator=(ViewPagerIndicator) itemView.findViewById(R.id.view_pager_indicator);
            this.view_pager_slide=(ClickableViewPager) itemView.findViewById(R.id.view_pager_slide);
        }

    }
    public static class RingtoneHolder extends RecyclerView.ViewHolder {

        public final ProgressBar progress_bar_item_ringtone_play;
        public final ProgressBar progress_bar_item_ringtone_background;
        public final ImageView image_view_item_ringtone_play;
        public final ImageView image_view_item_ringtone_pause;
        private final TextView text_view_item_ringtone_author;
        private final TextView text_view_item_ringtone_downloads;
        private final TextView text_view_item_ringtone_duration;
        private final TextView text_view_item_ringtone_title;
        private final LikeButton like_button_fav_item_ringtone;
        private final TextView text_view_item_ringtone_premium;
        Button share_btn;

        public RingtoneHolder(View itemView) {
            super(itemView);
            progress_bar_item_ringtone_background=(ProgressBar) itemView.findViewById(R.id.progress_bar_item_ringtone_background);
            progress_bar_item_ringtone_play=(ProgressBar) itemView.findViewById(R.id.progress_bar_item_ringtone_play);
            image_view_item_ringtone_play=(ImageView) itemView.findViewById(R.id.image_view_item_ringtone_play);
            image_view_item_ringtone_pause=(ImageView) itemView.findViewById(R.id.image_view_item_ringtone_pause);
            text_view_item_ringtone_author=(TextView) itemView.findViewById(R.id.text_view_item_ringtone_author);
            text_view_item_ringtone_downloads=(TextView) itemView.findViewById(R.id.text_view_item_ringtone_downloads);
            text_view_item_ringtone_duration=(TextView) itemView.findViewById(R.id.text_view_item_ringtone_duration);
            text_view_item_ringtone_premium=(TextView) itemView.findViewById(R.id.text_view_item_ringtone_premium);
            text_view_item_ringtone_title=(TextView) itemView.findViewById(R.id.text_view_item_ringtone_title);
            like_button_fav_item_ringtone=(LikeButton) itemView.findViewById(R.id.like_button_fav_item_ringtone);
            share_btn = itemView.findViewById(R.id.sharebtn);

         }
    }
    @Override
    public int getItemCount() {
        return ringtoneList.size();
    }


    public  class FacebookNativeHolder extends  RecyclerView.ViewHolder {
        private final String TAG = "WALLPAPERADAPTER";
        private RelativeLayout nativeBannerAdContainer;
        private LinearLayout adView;
        private NativeBannerAd nativeBannerAd;
        public FacebookNativeHolder(View view) {
            super(view);
            loadNativeAd(view);
        }

        private void loadNativeAd(final View view) {
            // Instantiate a NativeAd object.
            // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
            // now, while you are testing and replace it later when you have signed up.
            // While you are using this temporary code you will only get test ads and if you release
            // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
            nativeBannerAd = new NativeBannerAd(activity, activity.getString(R.string.FACEBOOK_ADS_NATIVE_BANNER_PLACEMENT_ID));
            nativeBannerAd.setAdListener(new NativeAdListener() {
                @Override
                public void onMediaDownloaded(Ad ad) {
                    // Native ad finished downloading all assets
                    Log.e("MAIN", "Native ad finished downloading all assets.");
                }

                @Override
                public void onError(Ad ad, AdError adError) {
                    // Native ad failed to load
                    Log.e("MAIN", "Native ad failed to load: " + adError.getErrorMessage());
                }

                @Override
                public void onAdLoaded(Ad ad) {
                    // Race condition, load() called again before last ad was displayed
                    if (nativeBannerAd == null || nativeBannerAd != ad) {
                        return;
                    }
                    // Inflate Native Banner Ad into Container
                    inflateAd(nativeBannerAd,view);
                    Log.d("MAIN", "Native ad is loaded and ready to be displayed!");
                }

                @Override
                public void onAdClicked(Ad ad) {
                    // Native ad clicked
                    Log.d("MAIN", "Native ad clicked!");
                }

                @Override
                public void onLoggingImpression(Ad ad) {
                    // Native ad impression
                    Log.d("MAIN", "Native ad impression logged!");
                }
            });
            // load the ad
            nativeBannerAd.loadAd();
        }

        private void inflateAd(NativeBannerAd nativeBannerAd,View view) {
            // Unregister last ad
            nativeBannerAd.unregisterView();

            // Add the Ad view into the ad container.
            nativeBannerAdContainer = view.findViewById(R.id.native_banner_ad_container);
            LayoutInflater inflater = LayoutInflater.from(activity);
            // Inflate the Ad view.  The layout referenced is the one you created in the last step.
            adView = (LinearLayout) inflater.inflate(R.layout.item_native_banner_ad_layout, nativeBannerAdContainer, false);
            nativeBannerAdContainer.addView(adView);

            // Add the AdChoices icon (NativeBannerAdActivity.this, nativeBannerAd, true);
            RelativeLayout adChoicesContainer = adView.findViewById(R.id.ad_choices_container);
            AdChoicesView adChoicesView = new AdChoicesView(activity.getApplicationContext(),nativeBannerAd,true);
            adChoicesContainer.addView(adChoicesView, 0);

            // Create native UI using the ad metadata.
            TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
            TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
            TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
            AdIconView nativeAdIconView = adView.findViewById(R.id.native_icon_view);
            Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

            // Set the Text.
            nativeAdCallToAction.setText(nativeBannerAd.getAdCallToAction());
            nativeAdCallToAction.setVisibility(
                    nativeBannerAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
            nativeAdTitle.setText(nativeBannerAd.getAdvertiserName());
            nativeAdSocialContext.setText(nativeBannerAd.getAdSocialContext());
            sponsoredLabel.setText(nativeBannerAd.getSponsoredTranslation());

            // Register the Title and CTA button to listen for clicks.
            List<View> clickableViews = new ArrayList<>();
            clickableViews.add(nativeAdTitle);
            clickableViews.add(nativeAdCallToAction);
            nativeBannerAd.registerViewForInteraction(adView, nativeAdIconView, clickableViews);
        }

    }




    public class ProgressBarAnimation extends Animation{
        private ProgressBar progressBar;
        private float from;
        private float  to;

        public ProgressBarAnimation(ProgressBar progressBar, float from, float to) {
            super();
            this.progressBar = progressBar;
            this.from = from;
            this.to = to;
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);
            float value = from + (to - from) * interpolatedTime;
             progressBar.setProgress((int) value);
        }

    }
    public static class FollowHolder extends  RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_follow_items;
        public FollowHolder(View view) {
            super(view);
            recycle_view_follow_items = (RecyclerView) itemView.findViewById(R.id.recycle_view_follow_items);
        }
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

    public static String secToTime(int sec) {
        int seconds = sec % 60;
        int minutes = sec / 60;

        if (minutes>0){
            return String.format("%01d m %02d s", minutes, seconds);
        }else{
            return String.format("%01d s", seconds);
        }
    }
}
