package com.colvengames.downloadmp3.adapter;

import android.app.Activity;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Handler;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
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
import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.entity.Ringtone;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsn on 10/03/2018.
 */


public class RingtonePagerAdapter  extends PagerAdapter {
    private final List<Ringtone> ringtoneList;
    private final Activity context;
    private LayoutInflater layoutInflater;



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

    @Override
    public int getCount() {
        return ringtoneList.size();
    }
    public RingtonePagerAdapter(
            List<Ringtone> imageList,
            Activity context,
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
            Integer playeditem
    ) {
        this.ringtoneList=imageList;
        this.context=context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);


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

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View)object);
    }
    @Override
    public Object instantiateItem(ViewGroup container,final int position) {
        View view= null;
        if (ringtoneList.get(position).getViewType() == 1 ){


            view = this.layoutInflater.inflate(R.layout.item_ringtone_page, container, false);
            int step=1;
            int final_step=1;
            for (int i = 1; i < position+1; i++) {
                if (i==position+1){
                    final_step=step;
                }
                step++;
                if (step>7){
                    step=1;
                }
            }

            ProgressBar progress_bar_item_ringtone_page_background=(ProgressBar) view.findViewById(R.id.progress_bar_item_ringtone_page_background);
            ProgressBar progress_bar_item_ringtone_page_play=(ProgressBar) view.findViewById(R.id.progress_bar_item_ringtone_page_play);
            ImageView image_view_item_ringtone_page_play=(ImageView) view.findViewById(R.id.image_view_item_ringtone_page_play);
            ImageView image_view_item_ringtone_page_pause=(ImageView) view.findViewById(R.id.image_view_item_ringtone_page_pause);
            TextView text_view_item_ringtone_page_title=(TextView) view.findViewById(R.id.text_view_item_ringtone_page_title);
            TextView text_view_item_ringtone_page_duration=(TextView) view.findViewById(R.id.text_view_item_ringtone_page_duration);
            TextView text_view_item_ringtone_premium=(TextView) view.findViewById(R.id.text_view_item_ringtone_premium);

            if (ringtoneList.get(position).getPremium()){
                text_view_item_ringtone_premium.setVisibility(View.VISIBLE);
            }else{
                text_view_item_ringtone_premium.setVisibility(View.GONE);
            }

            text_view_item_ringtone_page_title.setText(ringtoneList.get(position).getTitle());
            text_view_item_ringtone_page_duration.setText(secToTime(ringtoneList.get(position).getDuration()));
            progress_bar_item_ringtone_page_play.getIndeterminateDrawable().setColorFilter(context.getResources().getColor(R.color.white), PorterDuff.Mode.SRC_IN);
            if (ringtoneList.get(position).getPreparing()){
                progress_bar_item_ringtone_page_play.setVisibility(View.VISIBLE);
                image_view_item_ringtone_page_play.setVisibility(View.GONE);
                image_view_item_ringtone_page_pause.setVisibility(View.GONE);
            }else{
                if(ringtoneList.get(position).getPlaying()){
                    progress_bar_item_ringtone_page_play.setVisibility(View.GONE);
                    image_view_item_ringtone_page_play.setVisibility(View.GONE);
                    image_view_item_ringtone_page_pause.setVisibility(View.VISIBLE);
                }else{
                    Log.v("PAUSE","YES");
                    progress_bar_item_ringtone_page_play.setVisibility(View.GONE);
                    image_view_item_ringtone_page_play.setVisibility(View.VISIBLE);
                    image_view_item_ringtone_page_pause.setVisibility(View.GONE);
                }
            }
            if (position==playeditem) {
                if(ringtoneList.get(position).getPlaying()) {
                    Log.v("v","I4MHERE");
                    Integer initial =  (int) ((player.getCurrentPosition()*1000)/player.getDuration());
                    Log.v("v","I4MHERE"+initial+"-"+player.getCurrentPosition());
                    ProgressBarAnimation anim = new ProgressBarAnimation(progress_bar_item_ringtone_page_background,initial , 1000);
                    anim.setDuration(player.getDuration()-player.getCurrentPosition());
                    progress_bar_item_ringtone_page_background.startAnimation(anim);
                }else{
                    progress_bar_item_ringtone_page_background.setProgress(0);
                }
            }else{
                progress_bar_item_ringtone_page_background.setProgress(0);
            }

            switch (step){
                case 1:
                    progress_bar_item_ringtone_page_background.setProgressDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_1));

                    break;
                case 2:
                    progress_bar_item_ringtone_page_background.setProgressDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_2));
                    break;
                case 3:
                    progress_bar_item_ringtone_page_background.setProgressDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_3));
                    break;
                case 4:
                    progress_bar_item_ringtone_page_background.setProgressDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_4));
                    break;
                case 5:
                    progress_bar_item_ringtone_page_background.setProgressDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_5));
                    break;
                case 6:
                    progress_bar_item_ringtone_page_background.setProgressDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_6));
                    break;
                case 7:
                    progress_bar_item_ringtone_page_background.setProgressDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_7));
                    break;
            }
            image_view_item_ringtone_page_play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    for (int i = 0; i < ringtoneList.size(); i++) {
                        ringtoneList.get(i).setPlaying(false);
                        ringtoneList.get(i).setPreparing(false);
                    }
                    ringtoneList.get(position).setPreparing(true);
                    notifyDataSetChanged();
                    playeditem = position;
                    Log.v("v",ringtoneList.get(position).getRingtone());

                    mediaSource = new ExtractorMediaSource(Uri.parse(ringtoneList.get(position).getRingtone()),
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
                                if (playeditem==position) {
                                    ringtoneList.get(position).setPlaying(true);
                                    ringtoneList.get(position).setPreparing(false);
                                    Log.v("getDuration", "="+player.getDuration());
                                }else{
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
            });
            image_view_item_ringtone_page_pause.setOnClickListener(new View.OnClickListener() {
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

            container.addView(view);
        }else if (ringtoneList.get(position).getViewType()==4){
            view = this.layoutInflater.inflate(R.layout.item_facebook_native_ads, container, false);

            container.addView(view);
        }
        return view;
    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        container.removeView((View) object);
    }

    @Override
    public float getPageWidth (int position) {
        return 1f;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
            ((ViewPager) arg0).removeView((View) arg2);

    }

    public int getItemPosition(Object object) {

            return POSITION_NONE;

    }


    public class ProgressBarAnimation extends Animation {
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
