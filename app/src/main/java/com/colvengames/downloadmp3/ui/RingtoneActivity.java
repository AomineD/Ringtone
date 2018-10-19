package com.colvengames.downloadmp3.ui;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaScannerConnection;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatRatingBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.vending.billing.IInAppBillingService;
import com.anjlab.android.iab.v3.BillingProcessor;
import com.anjlab.android.iab.v3.Constants;
import com.anjlab.android.iab.v3.TransactionDetails;
import com.facebook.ads.Ad;
import com.facebook.ads.AdChoicesView;
import com.facebook.ads.AdError;
import com.facebook.ads.AdIconView;
import com.facebook.ads.MediaView;
import com.facebook.ads.NativeAd;
import com.facebook.ads.NativeAdListener;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
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
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.like.LikeButton;
import com.like.OnLikeListener;
import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.adapter.BackgroundPagerAdapter;
import com.colvengames.downloadmp3.adapter.CategoryAdapterRingtone;
import com.colvengames.downloadmp3.adapter.RingtonePagerAdapter;
import com.colvengames.downloadmp3.api.apiClient;
import com.colvengames.downloadmp3.api.apiRest;
import com.colvengames.downloadmp3.config.Config;
import com.colvengames.downloadmp3.entity.ApiResponse;
import com.colvengames.downloadmp3.entity.Category;
import com.colvengames.downloadmp3.entity.Ringtone;
import com.colvengames.downloadmp3.manager.FavoritesStorage;
import com.colvengames.downloadmp3.manager.PrefManager;
import com.colvengames.downloadmp3.ui.view.FadeTransformer;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

import de.hdodenhof.circleimageview.CircleImageView;
import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RingtoneActivity extends AppCompatActivity {


    private static String file_downloaded_url="";
    private int index = 0;

    private ViewPager viewPager;
    private List<Ringtone> ringtoneList = new ArrayList<>();
    private List<Category> categoryList = new ArrayList<>();
    private CategoryAdapterRingtone categoryAdapterRingtone;

    private RingtonePagerAdapter ringtonePagerAdapter;
    private ViewPager viewPagerBackground;
    private BackgroundPagerAdapter adapterBackground;

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

    private FloatingActionMenu floating_action_menu_ringtone_acitivty;
    private RelativeLayout relative_layout_cancel;
    private RelativeLayout relative_layout_user;
    private FloatingActionButton floating_action_button_ringtone_activity;
    private FloatingActionButton floating_action_button_notification_ringtone_activity;
    private FloatingActionButton floating_action_button_alarm_ringtone_activity;
    private FloatingActionButton floating_action_button_contact_ringtone_activity;
    private FloatingActionButton floating_action_button_download_ringtone_activity;
    private CircleImageView circle_image_view_activity_ringtone_user;
    private TextView text_view_activity_ringtone_user_name;
    private TextView text_view_activity_ringtone_created;
    private TextView text_view_activity_ringtone_downloads;

    private  static  final String DOWNLOAD_TYPE="DOWNLOAD_TYPE";
    private  static  final String NOTIFICATION_TYPE="NOTIFICATION_TYPE";
    private  static  final String CONTACT_TYPE="CONTACT_TYPE";
    private  static  final String ALARM_TYPE="ALARM_TYPE";
    private  static  final String RINGTONE_TYPE="RINGTONE_TYPE";
    private int id;
    private String title;
    private int userid;
    private String size;
    private String user;
    private String userimage;
    private String type;
    private int duration;
    private String ringtone;
    private String extension;
    private int downloads;
    private String tags;
    private String description;
    private String created;
    private boolean premium;
    private boolean trusted;

    private ImageView image_view_activity_ringtone_details;
    private RelativeLayout relative_layout_details_activity_ringtone;
    private LinearLayout linear_layout_description_details_ringtone_acitivty;
    private LinearLayout linear_layout_tags_details_ringtone_acitivty;
    private ImageView image_view_close_details_ringtone_activity;
    private TextView text_view_details_extension_ringtone_acitivty;
    private TextView text_view_details_time_ringtone_acitivty;
    private TextView text_view_details_downloads_ringtone_acitivty;
    private TextView text_view_details_size_ringtone_acitivty;
    private TextView text_view_deatils_title_ringtone_activity;
    private CircleImageView circle_image_view_activity_ringtone_user_details;
    private TextView text_view_activity_ringtone_user_name_details;
    private TextView text_view_description_details_ringtone_activity;
    private TextView text_view_tags_details_ringtone_activity;
    private LinearLayout linear_layout_categories_details_ringtone_acitvity;
    private LinearLayout linear_layout_categories_details_data_ringtone_acitvity;
    private ProgressBar progress_bar_item_ringtone_categories_ringtone_acitvity;
    private RecyclerView recycler_view_categories_details_ringtone_activity;
    private LinearLayoutManager linearLayoutManagerCategory;


    private Integer item = 0 ;
    private Integer lines_beetween_ads = 8 ;
    private Boolean native_ads_enabled = false ;

    private ProgressDialog register_progress;


    private AppCompatRatingBar rating_bar_guide_main_ringtone_activity;
    private AppCompatRatingBar rating_bar_guide_value_ringtone_activity;
    private RatingBar rating_bar_guide_1_ringtone_activity;
    private RatingBar rating_bar_guide_2_ringtone_activity;
    private RatingBar rating_bar_guide_3_ringtone_activity;
    private RatingBar rating_bar_guide_4_ringtone_activity;
    private RatingBar rating_bar_guide_5_ringtone_activity;
    private TextView text_view_rate_1_ringtone_activity;
    private TextView text_view_rate_2_ringtone_activity;
    private TextView text_view_rate_3_ringtone_activity;
    private TextView text_view_rate_4_ringtone_activity;
    private TextView text_view_rate_5_ringtone_activity;
    private ProgressBar progress_bar_rate_1_ringtone_activity;
    private ProgressBar progress_bar_rate_2_ringtone_activity;
    private ProgressBar progress_bar_rate_3_ringtone_activity;
    private ProgressBar progress_bar_rate_4_ringtone_activity;
    private ProgressBar progress_bar_rate_5_ringtone_activity;
    private TextView text_view_rate_main_ringtone_activity;
    private LikeButton like_button_fav_ringtone_activity;
    private LikeButton like_button_share_ringtone_activity;
    private ImageView image_view_report_details_ringtone_activity;
    private LinearLayout nativeAdContainer;
    private InterstitialAd mInterstitialAdDownload;
    private int open_action;
    private LinearLayout linearLayout_controllers;
    private ImageView image_view_ringtone_activity_trusted;
    private ImageView image_view_ringtone_activity_trusted_two;

    IInAppBillingService mService;

    private static final String LOG_TAG = "iabv3";
    // put your Google merchant id here (as stated in public profile of your Payments Merchant Center)
    // if filled library will provide protection against Freedom alike Play Market simulators
    private static final String MERCHANT_ID=null;

    private BillingProcessor bp;
    private boolean readyToPurchase = false;

    ServiceConnection mServiceConn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            mService = null;
            // Toast.makeText(MainActivity.this, "set null", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onServiceConnected(ComponentName name,
                                       IBinder service) {
            mService = IInAppBillingService.Stub.asInterface(service);
            //Toast.makeText(MainActivity.this, "set Stub", Toast.LENGTH_SHORT).show();

        }
    };
    private Dialog dialog;
    private  Boolean DialogOpened = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ringtone);
        Bundle bundle = getIntent().getExtras() ;
        this.id =  bundle.getInt("id");
        this.title =  bundle.getString("title");
        this.userid =  bundle.getInt("userid");
        this.size =  bundle.getString("size");
        this.user =  bundle.getString("user");
        this.userimage =  bundle.getString("userimage");
        this.type =  bundle.getString("type");
        this.duration =  bundle.getInt("duration");
        this.ringtone =  bundle.getString("ringtone");
        this.extension =  bundle.getString("extension");
        this.downloads =  bundle.getInt("downloads");
        this.trusted =  bundle.getBoolean("trusted");
        this.premium =  bundle.getBoolean("premium");
        this.tags =  bundle.getString("tags");
        this.description =  bundle.getString("description");
        this.created =  bundle.getString("created");

        Log.v("downloads : ",downloads+"");
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
        showAdsBanner();
        initInterstitialAdPrepare();

        RelatedRingtone();
        loadNativeAd();
        initBuy();
    }
    private void initBuy() {
        Intent serviceIntent =
                new Intent("com.android.vending.billing.InAppBillingService.BIND");
        serviceIntent.setPackage("com.android.vending");
        bindService(serviceIntent, mServiceConn, Context.BIND_AUTO_CREATE);


        if(!BillingProcessor.isIabServiceAvailable(this)) {
            //  showToast("In-app billing service is unavailable, please upgrade Android Market/Play to version >= 3.9.16");
        }

        bp = new BillingProcessor(this, Config.LICENSE_KEY, MERCHANT_ID, new BillingProcessor.IBillingHandler() {
            @Override
            public void onProductPurchased(@NonNull String productId, @Nullable TransactionDetails details) {
                //  showToast("onProductPurchased: " + productId);
                Intent intent= new Intent(RingtoneActivity.this,IntroActivity.class);
                startActivity(intent);
                finish();
                updateTextViews();
            }
            @Override
            public void onBillingError(int errorCode, @Nullable Throwable error) {
                // showToast("onBillingError: " + Integer.toString(errorCode));
            }
            @Override
            public void onBillingInitialized() {
                //  showToast("onBillingInitialized");
                readyToPurchase = true;
                updateTextViews();
            }
            @Override
            public void onPurchaseHistoryRestored() {
                // showToast("onPurchaseHistoryRestored");
                for(String sku : bp.listOwnedProducts())
                    Log.d(LOG_TAG, "Owned Managed Product: " + sku);
                for(String sku : bp.listOwnedSubscriptions())
                    Log.d(LOG_TAG, "Owned Subscription: " + sku);
                updateTextViews();
            }
        });
        bp.loadOwnedPurchasesFromGoogle();
    }
    private void updateTextViews() {
        PrefManager prf= new PrefManager(getApplicationContext());
        bp.loadOwnedPurchasesFromGoogle();

    }
    public Bundle getPurchases(){
        if (!bp.isInitialized()) {


            //  Toast.makeText(this, "null", Toast.LENGTH_SHORT).show();
            return null;
        }
        try{
            // Toast.makeText(this, "good", Toast.LENGTH_SHORT).show();

            return  mService.getPurchases(Constants.GOOGLE_API_VERSION, getApplicationContext().getPackageName(), Constants.PRODUCT_TYPE_SUBSCRIPTION, null);
        }catch (Exception e) {
            //  Toast.makeText(this, "ex", Toast.LENGTH_SHORT).show();

            e.printStackTrace();
        }
        return null;
    }

    public boolean checkSUBSCRIBED(){
        PrefManager prefManager= new PrefManager(getApplicationContext());
        if (prefManager.getString("SUBSCRIBED").equals("FALSE")) {
            return false;
        }
        return true;
    }

    public boolean check(){
        PrefManager prf = new PrefManager(this);
        if (getString(R.string.AD_MOB_ENABLED_BANNER).equals("false")){
            return false;
        }
        if (!prf.getString("SUBSCRIBED").equals("FALSE")) {
            return false;
        }
        Calendar c = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String strDate = sdf.format(c.getTime());

        if (prf.getString("LAST_DATE_ADS").equals("")) {
            prf.setString("LAST_DATE_ADS", strDate);
        } else {
            String toyBornTime = prf.getString("LAST_DATE_ADS");
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

            try {
                Date oldDate = dateFormat.parse(toyBornTime);
                System.out.println(oldDate);

                Date currentDate = new Date();

                long diff = currentDate.getTime() - oldDate.getTime();
                long seconds = diff / 1000;

                if (seconds > Integer.parseInt(getResources().getString(R.string.AD_MOB_TIME))) {
                    prf.setString("LAST_DATE_ADS", strDate);
                    return  true;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return  false;
    }
    private void initAction() {
        this.image_view_report_details_ringtone_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Report();
            }
        });
        this.like_button_share_ringtone_activity.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                like_button_share_ringtone_activity.setLiked(true);
                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 1000;
                    } else {
                        share();
                    }
                }else{
                    share();
                }

            }

            @Override
            public void unLiked(LikeButton likeButton) {
                like_button_share_ringtone_activity.setLiked(true);
                if (mInterstitialAdDownload.isLoaded()) {
                    if (check()) {
                        mInterstitialAdDownload.show();
                        open_action = 1000;
                    } else {
                        share();
                    }
                }else{
                    share();
                }
            }
        });
        this.text_view_activity_ringtone_user_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  =  new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id",ringtoneList.get(index).getUserid());
                intent.putExtra("image",ringtoneList.get(index).getUserimage());
                intent.putExtra("name",R.string.name_admin);
                intent.putExtra("trusted",ringtoneList.get(index).getTrusted());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        this.text_view_activity_ringtone_user_name_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  =  new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id",ringtoneList.get(index).getUserid());
                intent.putExtra("image",ringtoneList.get(index).getUserimage());
                intent.putExtra("name",ringtoneList.get(index).getUser());
                intent.putExtra("trusted",ringtoneList.get(index).getTrusted());

                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        this.circle_image_view_activity_ringtone_user_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  =  new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id",ringtoneList.get(index).getUserid());
                intent.putExtra("image",ringtoneList.get(index).getUserimage());
                intent.putExtra("name",ringtoneList.get(index).getUser());
                intent.putExtra("trusted",ringtoneList.get(index).getTrusted());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        this.circle_image_view_activity_ringtone_user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  =  new Intent(getApplicationContext(), UserActivity.class);
                intent.putExtra("id",ringtoneList.get(index).getUserid());
                intent.putExtra("image",ringtoneList.get(index).getUserimage());
                intent.putExtra("name",ringtoneList.get(index).getUser());
                intent.putExtra("trusted",ringtoneList.get(index).getTrusted());
                startActivity(intent);
                overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
        this.like_button_fav_ringtone_activity.setOnLikeListener(new OnLikeListener() {
            @Override
            public void liked(LikeButton likeButton) {
                addFavorite(index);
            }

            @Override
            public void unLiked(LikeButton likeButton) {
                addFavorite(index);
            }
        });
        this.viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        this.floating_action_button_ringtone_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_action_menu_ringtone_acitivty.showMenu(true);
            }
        });
        this.relative_layout_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                floating_action_menu_ringtone_acitivty.toggle(true);
            }
        });
        this.floating_action_menu_ringtone_acitivty.setOnMenuToggleListener(new FloatingActionMenu.OnMenuToggleListener() {
            @Override
            public void onMenuToggle(boolean opened) {
                if (opened){
                    relative_layout_cancel.setVisibility(View.VISIBLE);
                    floating_action_menu_ringtone_acitivty.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_black_24dp));
                }else{
                    relative_layout_cancel.setVisibility(View.GONE);
                    floating_action_menu_ringtone_acitivty.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_set));
                }
            }
        });
        this.image_view_activity_ringtone_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDetails();
            }
        });
        this.floating_action_menu_ringtone_acitivty.setOnMenuButtonClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!floating_action_menu_ringtone_acitivty.isOpened()){
                    floating_action_menu_ringtone_acitivty.toggle(true);
                    relative_layout_cancel.setVisibility(View.VISIBLE);
                    floating_action_menu_ringtone_acitivty.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_volume_up_black_24dp));
                }else{
                    floating_action_menu_ringtone_acitivty.toggle(false);
                    relative_layout_cancel.setVisibility(View.GONE);
                    floating_action_menu_ringtone_acitivty.getMenuIconView().setImageDrawable(getResources().getDrawable(R.drawable.ic_set));

                    like_button_share_ringtone_activity.setLiked(true);
                    if (!ringtoneList.get(index).getPremium()){
                        if (mInterstitialAdDownload.isLoaded()) {
                            if (check()) {
                                mInterstitialAdDownload.show();
                                open_action = 1001;
                            } else {
                                new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),RINGTONE_TYPE);
                            }
                        }else{
                            new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),RINGTONE_TYPE);
                        }
                    }else {
                        if (checkSUBSCRIBED()) {
                            new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),RINGTONE_TYPE);
                        }else{
                            showDialog();
                        }
                    }
                }
                floating_action_menu_ringtone_acitivty.toggle(true);

            }
        });
        this.floating_action_button_notification_ringtone_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!ringtoneList.get(index).getPremium()){
                    if (mInterstitialAdDownload.isLoaded()) {
                        if (check()) {
                            mInterstitialAdDownload.show();
                            open_action = 1002;
                        } else {
                            new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),NOTIFICATION_TYPE);
                        }
                    }else{
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),NOTIFICATION_TYPE);
                    }
                }else {
                    if (checkSUBSCRIBED()) {
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),NOTIFICATION_TYPE);
                    }else{
                        showDialog();
                    }
                }


                floating_action_menu_ringtone_acitivty.toggle(true);

            }
        });
        this.floating_action_button_alarm_ringtone_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




                if (!ringtoneList.get(index).getPremium()){
                    if (mInterstitialAdDownload.isLoaded()) {
                        if (check()) {
                            mInterstitialAdDownload.show();
                            open_action = 1003;
                        } else {
                            new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),ALARM_TYPE);
                        }
                    }else{
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),ALARM_TYPE);
                    }
                }else {
                    if (checkSUBSCRIBED()) {
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),ALARM_TYPE);
                    }else{
                        showDialog();
                    }
                }

                floating_action_menu_ringtone_acitivty.toggle(true);

            }
        });
        this.floating_action_button_contact_ringtone_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {



                if (!ringtoneList.get(index).getPremium()){
                    if (mInterstitialAdDownload.isLoaded()) {
                        if (check()) {
                            mInterstitialAdDownload.show();
                            open_action = 1004;
                        } else {
                            new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),CONTACT_TYPE);
                        }
                    }else{
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),CONTACT_TYPE);
                    }
                }else {
                    if (checkSUBSCRIBED()) {
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),CONTACT_TYPE);
                    }else{
                        showDialog();
                    }
                }

                floating_action_menu_ringtone_acitivty.toggle(true);
            }
        });
        this.floating_action_button_download_ringtone_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!ringtoneList.get(index).getPremium()){
                    if (mInterstitialAdDownload.isLoaded()) {
                        if (check()) {
                            mInterstitialAdDownload.show();
                            open_action = 1005;
                        } else {
                            new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),DOWNLOAD_TYPE);
                        }
                    }else{
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),DOWNLOAD_TYPE);
                    }
                }else {
                    if (checkSUBSCRIBED()) {
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),DOWNLOAD_TYPE);
                    }else{
                        showDialog();
                    }
                }

                floating_action_menu_ringtone_acitivty.toggle(true);

            }
        });
        this.image_view_activity_ringtone_details.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (ringtoneList.get(index).getTags()==null){
                        linear_layout_tags_details_ringtone_acitivty.setVisibility(View.GONE);
                    }else{
                        linear_layout_tags_details_ringtone_acitivty.setVisibility(View.VISIBLE);
                        text_view_tags_details_ringtone_activity.setText(ringtoneList.get(index).getTags());
                    }
                    if (ringtoneList.get(index).getDescription()==null){
                        linear_layout_description_details_ringtone_acitivty.setVisibility(View.GONE);
                    }else{
                        linear_layout_description_details_ringtone_acitivty.setVisibility(View.VISIBLE);
                        text_view_description_details_ringtone_activity.setText(ringtoneList.get(index).getDescription());
                    }
                  relative_layout_details_activity_ringtone.setVisibility(View.VISIBLE);
                  text_view_details_extension_ringtone_acitivty.setText(ringtoneList.get(index).getExtension());
                  text_view_details_time_ringtone_acitivty.setText(ringtoneList.get(index).getCreated());
                  text_view_details_downloads_ringtone_acitivty.setText(format(ringtoneList.get(index).getDownloads()));
                  text_view_details_size_ringtone_acitivty.setText(ringtoneList.get(index).getSize());
                  text_view_deatils_title_ringtone_activity.setText(ringtoneList.get(index).getTitle());

                getRingtoneCategories(ringtoneList.get(index).getId());
                Picasso.with(getApplicationContext()).load(ringtoneList.get(index).getUserimage()).error(getResources().getDrawable(R.drawable.profile)).placeholder(getResources().getDrawable(R.drawable.profile)).into(circle_image_view_activity_ringtone_user_details);
                text_view_activity_ringtone_user_name_details.setText(ringtoneList.get(index).getUser());
                getRate(ringtoneList.get(index).getId());
                if (ringtoneList.get(index).getTrusted()){
                    image_view_ringtone_activity_trusted.setVisibility(View.VISIBLE);
                    image_view_ringtone_activity_trusted_two.setVisibility(View.VISIBLE);
                }else{
                    image_view_ringtone_activity_trusted.setVisibility(View.GONE);
                    image_view_ringtone_activity_trusted_two.setVisibility(View.GONE);
                }
            }
        });
        this.image_view_close_details_ringtone_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                relative_layout_details_activity_ringtone.setVisibility(View.GONE);
            }
        });


        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int position) {
                index = position;
                ringtonePagerAdapter.notifyDataSetChanged();
                if (ringtoneList.get(index).getViewType()==1){
                    nativeAdContainer.setVisibility(View.GONE);
                    relative_layout_user.setVisibility(View.VISIBLE);
                    linearLayout_controllers.setVisibility(View.VISIBLE);
                    floating_action_button_ringtone_activity.setVisibility(View.VISIBLE);
                    floating_action_menu_ringtone_acitivty.setVisibility(View.VISIBLE);

                }else{
                    nativeAdContainer.setVisibility(View.VISIBLE);
                    relative_layout_user.setVisibility(View.INVISIBLE);
                    linearLayout_controllers.setVisibility(View.INVISIBLE);
                    floating_action_button_ringtone_activity.setVisibility(View.INVISIBLE);
                    floating_action_menu_ringtone_acitivty.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                //  Toast.makeText(RingtoneActivity.this, "onPageScrolled", Toast.LENGTH_SHORT).show();
                viewPagerBackground.setCurrentItem(index);
                if (index%10==0){
                    if (mInterstitialAdDownload.isLoaded()) {
                        if (check()) {
                            mInterstitialAdDownload.show();
                        }
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

                if (state ==ViewPager.SCROLL_STATE_DRAGGING) {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(relative_layout_user,"alpha",0.2F );
                    anim.setDuration(500); // duration 3 seconds
                    anim.start();
                    image_view_activity_ringtone_details.setVisibility(View.GONE);
                    nativeAdContainer.setVisibility(View.INVISIBLE);
                }
                if (state == ViewPager.SCROLL_STATE_IDLE) {
                    ObjectAnimator anim = ObjectAnimator.ofFloat(relative_layout_user,"alpha",1.0F );
                    anim.setDuration(500); // duration 3 seconds
                    anim.start();
                    stop();
                    if (ringtoneList.get(index).getViewType()==1){
                        setDetails(index);
                        image_view_activity_ringtone_details.setVisibility(View.VISIBLE);
                    }
                }

            }
        });
        this.rating_bar_guide_main_ringtone_activity.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if (fromUser){
                    addRate(rating,ringtoneList.get(index).getId());
                }

            }
        });









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
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        this.nativeAdContainer = findViewById(R.id.native_ad_container);

        this.linearLayout_controllers=(LinearLayout) findViewById(R.id.linearLayout_controllers);
        this.image_view_report_details_ringtone_activity=(ImageView) findViewById(R.id.image_view_report_details_ringtone_activity);
        this.like_button_fav_ringtone_activity=(LikeButton) findViewById(R.id.like_button_fav_ringtone_activity);
        this.like_button_share_ringtone_activity=(LikeButton) findViewById(R.id.like_button_share_ringtone_activity);
        this.recycler_view_categories_details_ringtone_activity=(RecyclerView) findViewById(R.id.recycler_view_categories_details_ringtone_activity);
        this.progress_bar_item_ringtone_categories_ringtone_acitvity=(ProgressBar) findViewById(R.id.progress_bar_item_ringtone_categories_ringtone_acitvity);
        this.linear_layout_categories_details_ringtone_acitvity=(LinearLayout) findViewById(R.id.linear_layout_categories_details_ringtone_acitvity);
        this.linear_layout_categories_details_data_ringtone_acitvity=(LinearLayout) findViewById(R.id.linear_layout_categories_details_data_ringtone_acitvity);
        this.text_view_description_details_ringtone_activity=(TextView) findViewById(R.id.text_view_description_details_ringtone_activity);
        this.text_view_tags_details_ringtone_activity=(TextView) findViewById(R.id.text_view_tags_details_ringtone_activity);
        this.circle_image_view_activity_ringtone_user_details=(CircleImageView) findViewById(R.id.circle_image_view_activity_ringtone_user_details);
        this.text_view_activity_ringtone_user_name_details=(TextView) findViewById(R.id.text_view_activity_ringtone_user_name_details);
        this.text_view_deatils_title_ringtone_activity=(TextView) findViewById(R.id.text_view_deatils_title_ringtone_activity);
        this.text_view_details_time_ringtone_acitivty=(TextView) findViewById(R.id.text_view_details_time_ringtone_acitivty);
        this.text_view_details_downloads_ringtone_acitivty=(TextView) findViewById(R.id.text_view_details_downloads_ringtone_acitivty);
        this.text_view_details_size_ringtone_acitivty=(TextView) findViewById(R.id.text_view_details_size_ringtone_acitivty);
        this.text_view_details_extension_ringtone_acitivty=(TextView) findViewById(R.id.text_view_details_extension_ringtone_acitivty);
        this.image_view_close_details_ringtone_activity=(ImageView) findViewById(R.id.image_view_close_details_ringtone_activity);
        this.linear_layout_tags_details_ringtone_acitivty=(LinearLayout) findViewById(R.id.linear_layout_tags_details_ringtone_acitivty);
        this.linear_layout_description_details_ringtone_acitivty=(LinearLayout) findViewById(R.id.linear_layout_description_details_ringtone_acitivty);
        this.relative_layout_details_activity_ringtone=(RelativeLayout) findViewById(R.id.relative_layout_details_activity_ringtone);
        this.image_view_activity_ringtone_details=(ImageView) findViewById(R.id.image_view_activity_ringtone_details);
        this.text_view_activity_ringtone_user_name=(TextView) findViewById(R.id.text_view_activity_ringtone_user_name);
        this.text_view_activity_ringtone_created=(TextView) findViewById(R.id.text_view_activity_ringtone_created);
        this.text_view_activity_ringtone_downloads=(TextView) findViewById(R.id.text_view_activity_ringtone_downloads);
        this.circle_image_view_activity_ringtone_user=(CircleImageView) findViewById(R.id.circle_image_view_activity_ringtone_user);
        this.floating_action_button_ringtone_activity=(FloatingActionButton) findViewById(R.id.floating_action_button_ringtone_activity);
        this.floating_action_menu_ringtone_acitivty=(FloatingActionMenu) findViewById(R.id.floating_action_menu_ringtone_acitivty);
        this.floating_action_button_notification_ringtone_activity=(FloatingActionButton) findViewById(R.id.floating_action_button_notification_ringtone_activity);
        this.floating_action_button_alarm_ringtone_activity=(FloatingActionButton) findViewById(R.id.floating_action_button_alarm_ringtone_activity);
        this.floating_action_button_contact_ringtone_activity=(FloatingActionButton) findViewById(R.id.floating_action_button_contact_ringtone_activity);
        this.floating_action_button_download_ringtone_activity=(FloatingActionButton) findViewById(R.id.floating_action_button_download_ringtone_activity);
        this.floating_action_menu_ringtone_acitivty.setIconAnimated(false);
        this.relative_layout_cancel = (RelativeLayout) findViewById(R.id.relative_layout_cancel);
        this.relative_layout_user = (RelativeLayout) findViewById(R.id.relative_layout_user);

        this.image_view_ringtone_activity_trusted=(ImageView) findViewById(R.id.image_view_ringtone_activity_trusted);
        this.image_view_ringtone_activity_trusted_two=(ImageView) findViewById(R.id.image_view_ringtone_activity_trusted_two);
        this.rating_bar_guide_main_ringtone_activity=(AppCompatRatingBar) findViewById(R.id.rating_bar_guide_main_ringtone_activity);
        this.rating_bar_guide_value_ringtone_activity=(AppCompatRatingBar) findViewById(R.id.rating_bar_guide_value_ringtone_activity);
        this.rating_bar_guide_1_ringtone_activity=(RatingBar) findViewById(R.id.rating_bar_guide_1_ringtone_activity);
        this.rating_bar_guide_2_ringtone_activity=(RatingBar) findViewById(R.id.rating_bar_guide_2_ringtone_activity);
        this.rating_bar_guide_3_ringtone_activity=(RatingBar) findViewById(R.id.rating_bar_guide_3_ringtone_activity);
        this.rating_bar_guide_4_ringtone_activity=(RatingBar) findViewById(R.id.rating_bar_guide_4_ringtone_activity);
        this.rating_bar_guide_5_ringtone_activity=(RatingBar) findViewById(R.id.rating_bar_guide_5_ringtone_activity);

        this.text_view_rate_1_ringtone_activity=(TextView) findViewById(R.id.text_view_rate_1_ringtone_activity);
        this.text_view_rate_2_ringtone_activity=(TextView) findViewById(R.id.text_view_rate_2_ringtone_activity);
        this.text_view_rate_3_ringtone_activity=(TextView) findViewById(R.id.text_view_rate_3_ringtone_activity);
        this.text_view_rate_4_ringtone_activity=(TextView) findViewById(R.id.text_view_rate_4_ringtone_activity);
        this.text_view_rate_5_ringtone_activity=(TextView) findViewById(R.id.text_view_rate_5_ringtone_activity);
        this.text_view_rate_main_ringtone_activity=(TextView) findViewById(R.id.text_view_rate_main_ringtone_activity);
        this.progress_bar_rate_1_ringtone_activity=(ProgressBar) findViewById(R.id.progress_bar_rate_1_ringtone_activity);
        this.progress_bar_rate_2_ringtone_activity=(ProgressBar) findViewById(R.id.progress_bar_rate_2_ringtone_activity);
        this.progress_bar_rate_3_ringtone_activity=(ProgressBar) findViewById(R.id.progress_bar_rate_3_ringtone_activity);
        this.progress_bar_rate_4_ringtone_activity=(ProgressBar) findViewById(R.id.progress_bar_rate_4_ringtone_activity);
        this.progress_bar_rate_5_ringtone_activity=(ProgressBar) findViewById(R.id.progress_bar_rate_5_ringtone_activity);



        viewPager = (ViewPager) findViewById(R.id.viewpager);
        viewPagerBackground = (ViewPager) findViewById(R.id.viewPagerbackground);

        adapterBackground = new BackgroundPagerAdapter(ringtoneList,this);
        viewPagerBackground.setAdapter(adapterBackground);
        viewPagerBackground.setPageTransformer(true, new FadeTransformer());
        viewPagerBackground.setOffscreenPageLimit(0);

        ringtonePagerAdapter = new RingtonePagerAdapter(ringtoneList,RingtoneActivity.this,player,mediaSource,trackSelectionFactory,dataSourceFactory,extractorsFactory,mainHandler,renderersFactory,bandwidthMeter,loadControl,trackSelector,playeditem);
        viewPager.setAdapter(ringtonePagerAdapter);
        Ringtone ringtoneobj= new Ringtone();
        ringtoneobj.setId(id);
        ringtoneobj.setTitle(title);
        ringtoneobj.setCreated(created);
        ringtoneobj.setDescription(description);
        ringtoneobj.setDownloads(downloads);
        ringtoneobj.setTrusted(trusted);
        ringtoneobj.setPremium(premium);
        ringtoneobj.setRingtone(ringtone);
        ringtoneobj.setDuration(duration);
        ringtoneobj.setExtension(extension);
        ringtoneobj.setSize(size);
        ringtoneobj.setTags(tags);
        ringtoneobj.setUserimage(userimage);
        ringtoneobj.setUserid(userid);
        ringtoneobj.setUser(user);
        ringtoneList.add(ringtoneobj);
        ringtonePagerAdapter.notifyDataSetChanged();
        adapterBackground.notifyDataSetChanged();
        viewPager.setClipChildren(false);
        viewPager.setPageMargin(30);
        viewPager.setCurrentItem(0);
        viewPager.setOffscreenPageLimit(0);
        setDetails(0);
       // viewPager.setPageTransformer(false, new CarouselEffectTransformer(RingtoneActivity.this)); // Set transformer


        this.linearLayoutManagerCategory=  new LinearLayoutManager(this,LinearLayoutManager.HORIZONTAL,false);
        this.categoryAdapterRingtone =new CategoryAdapterRingtone(categoryList,this);
        recycler_view_categories_details_ringtone_activity.setHasFixedSize(true);
        recycler_view_categories_details_ringtone_activity.setAdapter(categoryAdapterRingtone);
        recycler_view_categories_details_ringtone_activity.setLayoutManager(linearLayoutManagerCategory);

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
    private void RelatedRingtone() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Ringtone>> call = service.ringtoneRelated(id);
        call.enqueue(new Callback<List<Ringtone>>() {
            @Override
            public void onResponse(Call<List<Ringtone>> call, Response<List<Ringtone>> response) {
                apiClient.FormatData(RingtoneActivity.this,response);
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

                        ringtonePagerAdapter.notifyDataSetChanged();
                        adapterBackground.notifyDataSetChanged();
                        //viewPager.setPageTransformer(false, new CarouselEffectTransformer(RingtoneActivity.this)); // Set transformer
                    }
                }
            }
            @Override
            public void onFailure(Call<List<Ringtone>> call, Throwable t) {

            }
        });
    }

    @Override
    protected void onPause() {
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
        ringtonePagerAdapter.notifyDataSetChanged();
    }
    public void share(){
        String shareBody =title+"\n\n"+getResources().getString(R.string.download_ringtone_from)+"\n"+ Config.BASE_URL.replace("api","share")+ringtoneList.get(index).getId()+".html";
        Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
        sharingIntent.setType("text/plain");
        sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
        sharingIntent.putExtra(Intent.EXTRA_SUBJECT,  getString(R.string.app_name));
        startActivity(Intent.createChooser(sharingIntent, getResources().getString(R.string.app_name)));
    }


    class DownloadFileFromURL extends AsyncTask<String, String, String> {

        private String file_url;
        private String file_title;
        private String file_extention;
        private String file_id;
        private String TYPE;

        /**
         * Before starting background thread
         * Show Progress Bar Dialog
         * */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
           // toggleProgress();

        }
        public boolean dir_exists(String dir_path)
        {
            boolean ret = false;
            File dir = new File(dir_path);
            if(dir.exists() && dir.isDirectory())
                ret = true;
            return ret;
        }
        /**
         * Downloading file in background thread
         * */
        @Override
        protected String doInBackground(String... data_string) {
            int count;

            this.file_title=data_string[1];
            this.file_extention=data_string[2];
            this.file_id=data_string[3];
            this.TYPE=data_string[4];

            Log.v("file_title",file_title);
            Log.v("file_extention",file_extention);
            Log.v("file_id",file_id);
            Log.v("TYPE",TYPE);

            try {
                URL url = new URL(data_string[0]);





                URLConnection conection = url.openConnection();
                conection.setRequestProperty("Accept-Encoding", "identity");
                conection.connect();
                // this will be useful so that you can show a tipical 0-100% progress bar
                int lenghtOfFile = conection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);

                String dir_path = Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.DownloadFolder);

                if (!dir_exists(dir_path)){
                    File directory = new File(dir_path);
                    if(directory.mkdirs()){
                        Log.v("dir","is created 1");
                    }else{
                        Log.v("dir","not created 1");

                    }
                    if(directory.mkdir()){
                        Log.v("dir","is created 2");
                    }else{
                        Log.v("dir","not created 2");

                    }
                }else{
                    Log.v("dir","is exist");
                }
                Log.v("data_string", data_string[0]);
                Log.v("data_string", dir_path+file_title.toString().replace("/","_")+"_"+id+"."+extension);


                // Output stream
                OutputStream output = new FileOutputStream(dir_path+file_title.toString().replace("/","_")+"_"+id+"."+extension);

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress(""+(int)((total*100)/lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();


                output.close();
                input.close();
                MediaScannerConnection.scanFile(getApplicationContext(), new String[] { dir_path+file_title.toString().replace("/","_")+"_"+id+"."+extension },
                        null,
                        new MediaScannerConnection.OnScanCompletedListener() {
                            @Override
                            public void onScanCompleted(String path, Uri uri) {

                            }
                        });
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    final Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    final Uri contentUri = Uri.fromFile(new File(dir_path+file_title.toString().replace("/","_")+"_"+id+"."+extension));
                    scanIntent.setData(contentUri);
                    sendBroadcast(scanIntent);
                } else {
                    final Intent intent = new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://" + Environment.getExternalStorageDirectory()));

                    sendBroadcast(intent);
                }
                this.file_url = dir_path +file_title.toString().replace("/","_")+"_"+id+"."+extension;
                Log.v("file_url",file_url);

            } catch (Exception e) {
                Log.v("ex",e.getMessage());
            }
            return null;
        }
        /**
         * Updating progress bar
         * */
        protected void onProgressUpdate(String... progress) {
            ProgressValue(Integer.parseInt(progress[0]));
        }
        /**
         * After completing background task
         * Dismiss the progress dialog
         * **/
        @Override
        protected void onPostExecute(String file_url) {
            if (this.file_url==null){
                Toasty.error(getApplicationContext(),getResources().getString(R.string.ringtone_download_error),Toast.LENGTH_LONG).show();
               // toggleProgress();
            }else{
               addDownload(Integer.parseInt(this.file_id));
               file_downloaded_url = this.file_url;
               switch (TYPE){
                    case DOWNLOAD_TYPE :{
                        Toasty.success(getApplicationContext(),getResources().getString(R.string.ringtone_download_success),Toast.LENGTH_LONG).show();
                        break;
                    }
                    case RINGTONE_TYPE :{
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkPermission()) {
                                if (Settings.System.canWrite(RingtoneActivity.this)) {
                                    setRingtone(this.file_url);
                                } else {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                                            .setData(Uri.parse("package:" + getPackageName()))
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }


                                Log.e("value", "Permission already Granted, Now you can save image.");
                            } else {
                                requestPermission();
                            }
                        } else {
                            setRingtone(this.file_url);
                            Log.e("value", "Not required for requesting runtime permission");
                        }
                        break;
                    }
                    case ALARM_TYPE :{
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkPermission()) {
                                if (Settings.System.canWrite(RingtoneActivity.this)) {
                                    setAlarm(this.file_url);
                                } else {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                                            .setData(Uri.parse("package:" + getPackageName()))
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }


                                Log.e("value", "Permission already Granted, Now you can save image.");
                            } else {
                                requestPermission();
                            }
                        } else {
                            setAlarm(this.file_url);
                            Log.e("value", "Not required for requesting runtime permission");
                        }
                        break;
                    }
                    case NOTIFICATION_TYPE :{
                        if (Build.VERSION.SDK_INT >= 23) {
                            if (checkPermission()) {
                                if (Settings.System.canWrite(RingtoneActivity.this)) {
                                    setNotification(this.file_url);
                                } else {
                                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                                            .setData(Uri.parse("package:" + getPackageName()))
                                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                }
                                Log.e("value", "Permission already Granted, Now you can save image.");
                            } else {
                                requestPermission();
                            }
                        } else {
                            setNotification(this.file_url);
                            Log.e("value", "Not required for requesting runtime permission");
                        }
                        break;
                    }
                    case CONTACT_TYPE :{

                        doit(this.file_url);
                        break;
                    }
                }
                ProgressValue(0);
            }



        }
    }

    private void ProgressValue(int value) {
        floating_action_button_ringtone_activity.setProgress(value,false);
        Log.v("V",value+"%");
    }
    public void setNotification(String pathString){
        // Create File object for the specified ring tone path
        File f=new File(pathString);
        // Insert the ring tone to the content provider
        ContentValues value=new ContentValues();
        value.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
        value.put(MediaStore.MediaColumns.TITLE, f.getName());
        value.put(MediaStore.MediaColumns.SIZE, f.length());
        value.put(MediaStore.MediaColumns.MIME_TYPE,"audio/mp3");
        value.put(MediaStore.Audio.Media.ARTIST, getString(R.string.app_name));
        value.put(MediaStore.Audio.Media.IS_ALARM, false);
        value.put(MediaStore.Audio.Media.IS_MUSIC, false);
        value.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
        value.put(MediaStore.Audio.Media.IS_RINGTONE, false);



        Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
        getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + f.getAbsolutePath() + "\"", null);

        //Ok now insert it
        Uri newUri = getContentResolver().insert(uri, value);

        //Ok now set the ringtone_app from the content manager's uri, NOT the file's uri
        RingtoneManager.setActualDefaultRingtoneUri(
                getApplicationContext(),
                RingtoneManager.TYPE_NOTIFICATION,
                newUri
        );
        Toasty.info(getApplicationContext(),getResources().getString(R.string.ringtone_notification_success),Toast.LENGTH_LONG).show();

        // Set default ring tone
    }

    public void setAlarm(String pathString){
        // Create File object for the specified ring tone path
        File f=new File(pathString);
        // Insert the ring tone to the content provider
        ContentValues value=new ContentValues();
        value.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
        value.put(MediaStore.MediaColumns.TITLE, f.getName());
        value.put(MediaStore.MediaColumns.SIZE, f.length());
        value.put(MediaStore.MediaColumns.MIME_TYPE,"audio/mp3");
        value.put(MediaStore.Audio.Media.ARTIST, getString(R.string.app_name));
        value.put(MediaStore.Audio.Media.IS_ALARM, true);
        value.put(MediaStore.Audio.Media.IS_MUSIC, false);
        value.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        value.put(MediaStore.Audio.Media.IS_RINGTONE, false);



        Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
        getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + f.getAbsolutePath() + "\"", null);

        //Ok now insert it
        Uri newUri = getContentResolver().insert(uri, value);

        //Ok now set the ringtone_app from the content manager's uri, NOT the file's uri
        RingtoneManager.setActualDefaultRingtoneUri(
                getApplicationContext(),
                RingtoneManager.TYPE_ALARM,
                newUri
        );
        Toasty.info(getApplicationContext(),getResources().getString(R.string.ringtone_alarm_success),Toast.LENGTH_LONG).show();

        // Set default ring tone
    }
    static  final int PICK_CONTACT=1;

    public void setRingtoneContact(String pathString){



        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT);


        // Set default ring tone
    }


    public void setRingtone(String pathString){
        // Create File object for the specified ring tone path
        File f=new File(pathString);

        // Insert the ring tone to the content provider
        ContentValues value=new ContentValues();
        value.put(MediaStore.MediaColumns.DATA, f.getAbsolutePath());
        value.put(MediaStore.MediaColumns.TITLE, f.getName());
        value.put(MediaStore.MediaColumns.SIZE, f.length());
        value.put(MediaStore.MediaColumns.MIME_TYPE,"audio/mp3");
        value.put(MediaStore.Audio.Media.ARTIST, getString(R.string.app_name));
        value.put(MediaStore.Audio.Media.IS_ALARM, false);
        value.put(MediaStore.Audio.Media.IS_MUSIC, false);
        value.put(MediaStore.Audio.Media.IS_NOTIFICATION, false);
        value.put(MediaStore.Audio.Media.IS_RINGTONE, true);



        Uri uri = MediaStore.Audio.Media.getContentUriForPath(f.getAbsolutePath());
        getContentResolver().delete(uri, MediaStore.MediaColumns.DATA + "=\"" + f.getAbsolutePath() + "\"", null);

        //Ok now insert it
        Uri newUri = getContentResolver().insert(uri, value);



        //Ok now set the ringtone_app from the content manager's uri, NOT the file's uri
        RingtoneManager.setActualDefaultRingtoneUri(
                getApplicationContext(),
                RingtoneManager.TYPE_RINGTONE,
                newUri
        );
        Toasty.info(getApplicationContext(),getResources().getString(R.string.ringtone_set_success),Toast.LENGTH_LONG).show();

        // Set default ring tone
    }

    public void doit(String pth){
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkPermission()) {
                if (Settings.System.canWrite(RingtoneActivity.this)) {
                    setRingtoneContact(pth);
                } else {
                    Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                            .setData(Uri.parse("package:" + getPackageName()))
                            .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                }


                Log.e("value", "Permission already Granted, Now you can save image.");
            } else {
                requestPermission();
            }
        } else {
            setRingtoneContact(pth);
            Log.e("value", "Not required for requesting runtime permission");
        }

    }

    private boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(RingtoneActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        if (result == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    private void requestPermission() {

        if (ActivityCompat.shouldShowRequestPermissionRationale(RingtoneActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            Toast.makeText(RingtoneActivity.this, "Write External Storage permission allows us to do store images. Please allow this permission in App Settings.", Toast.LENGTH_LONG).show();
        } else {
            ActivityCompat.requestPermissions(RingtoneActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("value", "Permission Granted, Now you can save image .");
                    if (Build.VERSION.SDK_INT >= 23) {
                        if (Settings.System.canWrite(RingtoneActivity.this)) {
                            Log.e("value", "3awd");

                        } else {
                            Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS)
                                    .setData(Uri.parse("package:" + getPackageName()))
                                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                        }
                    }
                } else {
                    Log.e("value", "Permission Denied, You cannot save image.");
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT) {
            String phoneNo = null;
            String name = null;

            Uri uri = data.getData();
            Cursor cursor = getContentResolver().query(uri, null, null, null, null);

            if (cursor.moveToFirst()) {
                int phoneIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                int nameIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME);

                phoneNo = cursor.getString(phoneIndex);
                name = cursor.getString(nameIndex);

                Log.e("onActivityResult()", phoneIndex + " " + phoneNo + " " + nameIndex + " " + name);

                setForContact(phoneNo);



            }
            cursor.close();
        }
    }
    public void setForContact(String contact){
        // The Uri used to look up a contact by phone number
        final Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, contact);
// The columns used for `Contacts.getLookupUri`
        final String[] projection = new String[] {
                ContactsContract.Contacts._ID, ContactsContract.Contacts.LOOKUP_KEY
        };
// Build your Cursor
        final Cursor data = getContentResolver().query(lookupUri, projection, null, null, null);
        data.moveToFirst();
        try {
            // Get the contact lookup Uri
            final long contactId = data.getLong(0);
            final String lookupKey = data.getString(1);
            final Uri contactUri = ContactsContract.Contacts.getLookupUri(contactId, lookupKey);
            if (contactUri == null) {
                // Invalid arguments
                return;
            }

            // Get the path of ringtone_app you'd like to use
            final String storage = Environment.getExternalStorageDirectory().getPath();
                Log.v("file_downloaded_url",file_downloaded_url);


            final File file = new File(file_downloaded_url);

            Uri uri = Uri.fromFile(file);
            Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
            sendBroadcast(intent);

            if (file.exists()){

                final String value = Uri.fromFile(file).toString();

                // Apply the custom ringtone_app
                final ContentValues values = new ContentValues();
                values.put(ContactsContract.Data.RAW_CONTACT_ID, contactId);
                values.put(ContactsContract.Contacts.CUSTOM_RINGTONE, value);
                getContentResolver().update(contactUri, values, null, null);

                Toasty.info(getApplicationContext(),getResources().getString(R.string.ringtone_contact_success),Toast.LENGTH_LONG).show();

            }else{
                Toast.makeText(this, "file not exist", Toast.LENGTH_SHORT).show();
            }
        } finally {
            // Don't forget to close your Cursor
            data.close();
        }
    }
    public void setDetails(int position){

        final FavoritesStorage storageFavorites= new FavoritesStorage(getApplicationContext());
        List<Ringtone> ringtones = storageFavorites.loadFavorites();
        Boolean exist = false;
        if (ringtones==null){
            ringtones= new ArrayList<>();
        }
        for (int i = 0; i <ringtones.size() ; i++) {
            if (ringtones.get(i).getId().equals(ringtoneList.get(index).getId())){
                exist = true;
            }
        }
        if (exist == false) {
            like_button_fav_ringtone_activity.setLiked(false);

        } else {
            like_button_fav_ringtone_activity.setLiked(true);
        }

        Picasso.with(this).load(ringtoneList.get(position).getUserimage()).error(getResources().getDrawable(R.drawable.profile)).placeholder(getResources().getDrawable(R.drawable.profile)).into(this.circle_image_view_activity_ringtone_user);
        //text_view_activity_ringtone_user_name.setText(ringtoneList.get(position).getUser());
        text_view_activity_ringtone_user_name.setText(R.string.name_admin);
        text_view_activity_ringtone_downloads.setText(format(ringtoneList.get(position).getDownloads()));
        text_view_activity_ringtone_created.setText(ringtoneList.get(position).getCreated());

        if (ringtoneList.get(position).getTrusted()){
            image_view_ringtone_activity_trusted.setVisibility(View.VISIBLE);
            image_view_ringtone_activity_trusted_two.setVisibility(View.VISIBLE);
        }else{
            image_view_ringtone_activity_trusted.setVisibility(View.GONE);
            image_view_ringtone_activity_trusted_two.setVisibility(View.GONE);
        }
    }

    private void showDetails() {

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
    public void getRingtoneCategories(Integer id){
        categoryList.clear();
        categoryAdapterRingtone.notifyDataSetChanged();
        linear_layout_categories_details_data_ringtone_acitvity.setVisibility(View.GONE);
        linear_layout_categories_details_ringtone_acitvity.setVisibility(View.VISIBLE);
        progress_bar_item_ringtone_categories_ringtone_acitvity.setVisibility(View.VISIBLE);
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Category>> call = service.CategoriesBy(id);
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                apiClient.FormatData(RingtoneActivity.this,response);
                progress_bar_item_ringtone_categories_ringtone_acitvity.setVisibility(View.GONE);
                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        for (int i = 0; i < response.body().size(); i++) {
                            categoryList.add(response.body().get(i));
                        }
                        categoryAdapterRingtone.notifyDataSetChanged();
                        linear_layout_categories_details_data_ringtone_acitvity.setVisibility(View.VISIBLE);
                        linear_layout_categories_details_ringtone_acitvity.setVisibility(View.VISIBLE);
                    }else{
                        linear_layout_categories_details_ringtone_acitvity.setVisibility(View.GONE);
                    }
                }else{
                    linear_layout_categories_details_ringtone_acitvity.setVisibility(View.GONE);
                }
            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                linear_layout_categories_details_ringtone_acitvity.setVisibility(View.GONE);
                progress_bar_item_ringtone_categories_ringtone_acitvity.setVisibility(View.GONE);
            }
        });
    }

    public void getRate(Integer id) {
        PrefManager prf = new PrefManager(getApplicationContext());
        String user_id = "0";
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            user_id=prf.getString("ID_USER").toString();
        }
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.getRate(user_id,id);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    if (response.body().getCode() == 200) {
                        rating_bar_guide_main_ringtone_activity.setRating(Integer.parseInt(response.body().getMessage()));
                    } else if (response.body().getCode() == 202){
                        rating_bar_guide_main_ringtone_activity.setRating(0);
                    }else{
                        rating_bar_guide_main_ringtone_activity.setRating(0);
                    }
                    if(response.body().getCode() != 500){
                        Integer rate_1=0;
                        Integer rate_2=0;
                        Integer rate_3=0;
                        Integer rate_4=0;
                        Integer rate_5=0;
                        float rate=0;



                        for (int i=0;i<response.body().getValues().size();i++){

                            if (response.body().getValues().get(i).getName().equals("1")){
                                rate_1=Integer.parseInt(response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("2")){
                                rate_2=Integer.parseInt(response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("3")){
                                rate_3=Integer.parseInt(response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("4")){
                                rate_4=Integer.parseInt(response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("5")){
                                rate_5=Integer.parseInt(response.body().getValues().get(i).getValue());
                            }
                            if (response.body().getValues().get(i).getName().equals("rate")){
                                rate=Float.parseFloat(response.body().getValues().get(i).getValue());
                            }
                        }
                        rating_bar_guide_value_ringtone_activity.setRating(rate);
                        String formattedString=rate + "";


                        text_view_rate_main_ringtone_activity.setText(formattedString);
                        text_view_rate_1_ringtone_activity.setText(rate_1+"");
                        text_view_rate_2_ringtone_activity.setText(rate_2+"");
                        text_view_rate_3_ringtone_activity.setText(rate_3+"");
                        text_view_rate_4_ringtone_activity.setText(rate_4+"");
                        text_view_rate_5_ringtone_activity.setText(rate_5+"");
                        Integer total= rate_1 + rate_2 + rate_3 + rate_4 + rate_5;
                        if(total==0) {
                            total = 1;
                        }
                        progress_bar_rate_1_ringtone_activity.setProgress((int) ((rate_1*100) / total)  );
                        progress_bar_rate_2_ringtone_activity.setProgress((int) ((rate_2*100) / total)  );
                        progress_bar_rate_3_ringtone_activity.setProgress((int) ((rate_3*100) / total)  );
                        progress_bar_rate_4_ringtone_activity.setProgress((int) ((rate_4*100) / total)  );
                        progress_bar_rate_5_ringtone_activity.setProgress((int) ((rate_5*100) / total)  );
                    }
                } else {

                }

            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {


            }
        });

    }

    public void addRate(final float value, final Integer id) {
        PrefManager prf = new PrefManager(getApplicationContext());
        if (prf.getString("LOGGED").toString().equals("TRUE")) {
            Retrofit retrofit = apiClient.getClient();
            apiRest service = retrofit.create(apiRest.class);
            Call<ApiResponse> call = service.addRate(prf.getString("ID_USER").toString(),id, value);
            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {

                    if (response.isSuccessful()) {
                        if (response.body().getCode() == 200) {
                            Toasty.success(RingtoneActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }else{
                            Toasty.success(RingtoneActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                        getRate(id);
                    } else {

                    }

                }

                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {


                }
            });
        } else {
            Intent intent = new Intent(RingtoneActivity.this, LoginActivity.class);
            startActivity(intent);
        }

    }


    public void addFavorite(Integer position){
        final FavoritesStorage storageFavorites= new FavoritesStorage(getApplicationContext());
        List<Ringtone> ringtones = storageFavorites.loadFavorites();
        List<Ringtone> favorites_list = storageFavorites.loadFavorites();
        Boolean exist = false;
        if (favorites_list==null){
            favorites_list= new ArrayList<>();
        }
        for (int i = 0; i <favorites_list.size() ; i++) {
            if (favorites_list.get(i).getId().equals(ringtoneList.get(position).getId())){
                exist = true;
            }
        }
        if (exist  == false) {
            ArrayList<Ringtone> audios= new ArrayList<Ringtone>();
            for (int i = 0; i < favorites_list.size(); i++) {
                audios.add(favorites_list.get(i));
            }

            audios.add(ringtoneList.get(position));
            storageFavorites.storeAudio(audios);

            like_button_fav_ringtone_activity.setLiked(true);
        }else{
            ArrayList<Ringtone> new_favorites= new ArrayList<Ringtone>();
            for (int i = 0; i < favorites_list.size(); i++) {
                if (!favorites_list.get(i).getId().equals(ringtoneList.get(index).getId())){
                    new_favorites.add(favorites_list.get(i));

                }
            }
            storageFavorites.storeAudio(new_favorites);
            like_button_fav_ringtone_activity.setLiked(false);
        }
    }
    public void addDownload(final Integer id_){

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<Integer> call = service.addDownload(id_);
        call.enqueue(new Callback<Integer>() {
            @Override
            public void onResponse(Call<Integer> call, Response<Integer> response) {
                if(response.isSuccessful()) {
                    for (int i = 0; i < ringtoneList.size(); i++) {
                        if (ringtoneList.get(i).getId()==id_){
                            ringtoneList.get(i).setDownloads(response.body());
                            ringtonePagerAdapter.notifyDataSetChanged();
                            setDetails(index);
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<Integer> call, Throwable t) {

            }
        });
    }
    private void Report() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.report_ringtone));

        final EditText input = new EditText(this);
        final TextView textView = new TextView(this);
        textView.setText("Message");
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

        LinearLayout linearLayout=  new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT));


        LinearLayout.LayoutParams llp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
        llp.setMargins(30, 30, 30, 30); // llp.setMargins(left, top, right, bottom);
        textView.setLayoutParams(llp);

        linearLayout.addView(textView);
        linearLayout.addView(input);

        builder.setView(linearLayout);

        builder.setPositiveButton("Report", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (input.getText().toString().length() > 3){
                    addReport(id,input.getText().toString());
                }else{
                    Toasty.error(RingtoneActivity.this,getResources().getString(R.string.error_short_value) , Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }
    private void addReport(int id, String message) {
        register_progress= ProgressDialog.show(this,null,getString(R.string.progress_login));
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<ApiResponse> call = service.addReport(id,message);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if(response.isSuccessful()){
                    Toasty.success(getApplicationContext(), getResources().getString(R.string.message_sended), Toast.LENGTH_SHORT).show();
                }else{
                    Toasty.error(getApplicationContext(), getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
                }
                register_progress.dismiss();
            }
            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                register_progress.dismiss();
                Toasty.error(getApplicationContext(), getString(R.string.no_connexion), Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showAdsBanner() {
        PrefManager prefManager= new PrefManager(getApplicationContext());
        if (getString(R.string.AD_MOB_ENABLED_BANNER).equals("true")) {

            if (prefManager.getString("SUBSCRIBED").equals("FALSE")) {
                final AdView mAdView = (AdView) findViewById(R.id.adView);
                AdRequest adRequest = new AdRequest.Builder()
                        .build();

                // Start loading the ad in the background.
                mAdView.loadAd(adRequest);

                mAdView.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        super.onAdLoaded();
                        mAdView.setVisibility(View.VISIBLE);
                    }
                });
            }
        }
    }

    private void initInterstitialAdPrepare() {
        mInterstitialAdDownload = new InterstitialAd(this);
        mInterstitialAdDownload.setAdUnitId(getResources().getString(R.string.ad_unit_id_interstitial));
        mInterstitialAdDownload.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                switch (open_action){
                    case 1000:
                        share();
                        break;
                    case 1001:
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),RINGTONE_TYPE);
                        break;
                    case 1002:
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),NOTIFICATION_TYPE);
                        break;
                    case 1003:
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),ALARM_TYPE);
                        break;
                    case 1004:
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),CONTACT_TYPE);
                        break;
                    case 1005:
                        new DownloadFileFromURL().execute(ringtoneList.get(index).getRingtone(),ringtoneList.get(index).getTitle(),ringtoneList.get(index).getExtension(),ringtoneList.get(index).getId().toString(),DOWNLOAD_TYPE);
                        break;
                }
                requestNewInterstitial();
            }
        });
        requestNewInterstitial();
    }
    private void requestNewInterstitial() {
        AdRequest adRequest = new AdRequest.Builder()
                .build();

        mInterstitialAdDownload.loadAd(adRequest);
    }
    public void showDialog(){
        this.dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setContentView(R.layout.dialog_subscribe);
        TextView text_view_go_pro=(TextView) dialog.findViewById(R.id.text_view_go_pro);
        text_view_go_pro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bp.subscribe(RingtoneActivity.this, Config.SUBSCRIPTION_ID);

            }
        });
        dialog.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface arg0, int keyCode,
                                 KeyEvent event) {
                // TODO Auto-generated method stub
                if (keyCode == KeyEvent.KEYCODE_BACK) {

                    dialog.dismiss();
                }
                return true;
            }
        });
        dialog.show();
        DialogOpened=true;
    }

    private void loadNativeAd() {
        final String TAG = "WALLPAPERADAPTER";


        final NativeAd nativeAd;

        // Instantiate a NativeAd object.
        // NOTE: the placement ID will eventually identify this as your App, you can ignore it for
        // now, while you are testing and replace it later when you have signed up.
        // While you are using this temporary code you will only get test ads and if you release
        // your code like this to the Google Play your users will not receive ads (you will get a no fill error).
        nativeAd = new NativeAd(this,this.getString(R.string.FACEBOOK_ADS_NATIVE_PLACEMENT_ID));
        nativeAd.setAdListener(new NativeAdListener() {
            @Override
            public void onMediaDownloaded(Ad ad) {
                // Native ad finished downloading all assets
                Log.e(TAG, "Native ad finished downloading all assets.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Native ad failed to load
                Log.e(TAG, "Native ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Native ad is loaded and ready to be displayed
                Log.d(TAG, "Native ad is loaded and ready to be displayed!");
                // Race condition, load() called again before last ad was displayed
                if (nativeAd == null || nativeAd != ad) {
                    return;
                }
                nativeAd.unregisterView();

                // Add the Ad view into the ad container.
                LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
                // Inflate the Ad view.  The layout referenced should be the one you created in the last step.
                LinearLayout adView;

                adView = (LinearLayout) inflater.inflate(R.layout.native_ad_layout_1, nativeAdContainer, false);
                nativeAdContainer.addView(adView);

                // Add the AdChoices icon
                LinearLayout adChoicesContainer = findViewById(R.id.ad_choices_container);
                AdChoicesView adChoicesView = new AdChoicesView(getApplicationContext(), nativeAd, true);
                adChoicesContainer.addView(adChoicesView, 0);

                // Create native UI using the ad metadata.
                AdIconView nativeAdIcon = adView.findViewById(R.id.native_ad_icon);
                TextView nativeAdTitle = adView.findViewById(R.id.native_ad_title);
                MediaView nativeAdMedia = adView.findViewById(R.id.native_ad_media);
                TextView nativeAdSocialContext = adView.findViewById(R.id.native_ad_social_context);
                TextView nativeAdBody = adView.findViewById(R.id.native_ad_body);
                TextView sponsoredLabel = adView.findViewById(R.id.native_ad_sponsored_label);
                Button nativeAdCallToAction = adView.findViewById(R.id.native_ad_call_to_action);

                // Set the Text.
                nativeAdTitle.setText(nativeAd.getAdvertiserName());
                nativeAdBody.setText(nativeAd.getAdBodyText());
                nativeAdSocialContext.setText(nativeAd.getAdSocialContext());
                nativeAdCallToAction.setVisibility(nativeAd.hasCallToAction() ? View.VISIBLE : View.INVISIBLE);
                nativeAdCallToAction.setText(nativeAd.getAdCallToAction());
                sponsoredLabel.setText(nativeAd.getSponsoredTranslation());

                // Create a list of clickable views
                List<View> clickableViews = new ArrayList<>();
                clickableViews.add(nativeAdTitle);
                clickableViews.add(nativeAdCallToAction);

                // Register the Title and CTA button to listen for clicks.
                nativeAd.registerViewForInteraction(
                        adView,
                        nativeAdMedia,
                        nativeAdIcon,
                        clickableViews);
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Native ad clicked
                Log.d(TAG, "Native ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Native ad impression
                Log.d(TAG, "Native ad impression logged!");
            }
        });

        // Request an ad
        nativeAd.loadAd();
    }

}
