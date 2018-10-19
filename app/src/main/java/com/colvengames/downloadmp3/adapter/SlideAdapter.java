package com.colvengames.downloadmp3.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.entity.Slide;
import com.colvengames.downloadmp3.ui.CategoryActivity;
import com.colvengames.downloadmp3.ui.RingtoneActivity;
import com.squareup.picasso.Picasso;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by hsn on 28/11/2017.
 */

public class SlideAdapter extends PagerAdapter {
    private List<Slide> slideList =new ArrayList<Slide>();
    private Activity activity;

    public SlideAdapter( Activity activity,List<Slide> stringList) {
        this.slideList = stringList;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return slideList.size();
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        LayoutInflater layoutInflater = (LayoutInflater)this.activity.getSystemService(this.activity.LAYOUT_INFLATER_SERVICE);

        View view = layoutInflater.inflate(R.layout.item_slide_one, container, false);

        TextView text_view_item_slide_one_title =  (TextView)  view.findViewById(R.id.text_view_item_slide_one_title);
        ImageView image_view_item_slide_one =  (ImageView)  view.findViewById(R.id.image_view_item_slide_one);

        Typeface face = Typeface.createFromAsset(activity.getAssets(), "Pattaya-Regular.ttf");
        text_view_item_slide_one_title.setTypeface(face);

        byte[] data = android.util.Base64.decode(slideList.get(position).getTitle(), android.util.Base64.DEFAULT);
        final String final_text = new String(data, Charset.forName("UTF-8"));

        text_view_item_slide_one_title.setText(final_text);

        CardView card_view_item_slide_one = (CardView) view.findViewById(R.id.card_view_item_slide_one);
        card_view_item_slide_one.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (slideList.get(position).getType().equals("3") && slideList.get(position).getRingtone()!=null){
                    Intent intent = new Intent(activity, RingtoneActivity.class);
                    intent.putExtra("id", slideList.get(position).getRingtone().getId());
                    intent.putExtra("title", slideList.get(position).getRingtone().getTitle());
                    intent.putExtra("premium", slideList.get(position).getRingtone().getPremium());
                    intent.putExtra("trusted", slideList.get(position).getRingtone().getTrusted());
                    intent.putExtra("userid", slideList.get(position).getRingtone().getUserid());
                    intent.putExtra("size", slideList.get(position).getRingtone().getSize());
                    intent.putExtra("user", slideList.get(position).getRingtone().getUser());
                    intent.putExtra("userimage", slideList.get(position).getRingtone().getUserimage());
                    intent.putExtra("type", slideList.get(position).getRingtone().getType());
                    intent.putExtra("duration", slideList.get(position).getRingtone().getDuration());
                    intent.putExtra("ringtone", slideList.get(position).getRingtone().getRingtone());
                    intent.putExtra("extension",slideList.get(position).getRingtone().getExtension());
                    intent.putExtra("downloads", slideList.get(position).getRingtone().getDownloads());
                    intent.putExtra("created",slideList.get(position).getRingtone().getCreated());
                    intent.putExtra("tags", slideList.get(position).getRingtone().getTags());
                    intent.putExtra("description", slideList.get(position).getRingtone().getDescription());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                }
                if (slideList.get(position).getType().equals("1") && slideList.get(position).getCategory()!=null){
                    Intent intent  =  new Intent(activity.getApplicationContext(), CategoryActivity.class);
                    intent.putExtra("id",slideList.get(position).getCategory().getId());
                    intent.putExtra("title",slideList.get(position).getCategory().getTitle());
                    activity.startActivity(intent);
                    activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                }
                if (slideList.get(position).getType().equals("2") && slideList.get(position).getUrl()!=null){
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(slideList.get(position).getUrl()));
                    activity.startActivity(browserIntent);
                }
            }
        });

         Picasso.with(activity).load(slideList.get(position).getImage()).placeholder(R.drawable.placeholder).into(image_view_item_slide_one);

        container.addView(view);
        return view;
    }
    @Override
    public void destroyItem(ViewGroup collection, int position, Object view) {
        collection.removeView((View) view);
    }



    @Override
    public float getPageWidth (int position) {
        return 1f;
    }

    @Override
    public void destroyItem(View arg0, int arg1, Object arg2) {
        ((ViewPager) arg0).removeView((View) arg2);

    }
    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == ((View) arg1);

    }
    @Override
    public Parcelable saveState() {
        return null;
    }
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
