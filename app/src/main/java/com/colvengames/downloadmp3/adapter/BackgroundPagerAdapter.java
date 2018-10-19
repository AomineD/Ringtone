package com.colvengames.downloadmp3.adapter;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.entity.Ringtone;

import java.util.List;

/**
 * Created by hsn on 10/03/2018.
 */

public class BackgroundPagerAdapter  extends PagerAdapter{

    private final List<Ringtone> ringtoneList;
    private final Context context;
    private LayoutInflater layoutInflater;

    @Override
    public int getCount() {
        return ringtoneList.size();
    }
    public BackgroundPagerAdapter(List<Ringtone> imageList, Context context){
        this.ringtoneList=imageList;
        this.context=context;
        this.layoutInflater = (LayoutInflater) this.context.getSystemService(this.context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((View)object);
    }
    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = this.layoutInflater.inflate(R.layout.item_background, container, false);

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

        ImageView imgBackground=(ImageView) view.findViewById(R.id.imgBackground);


        switch (step){
            case 1:
                imgBackground.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_1));
                break;
            case 2:
                imgBackground.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_2));
                break;
            case 3:
                imgBackground.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_3));
                break;
            case 4:
                imgBackground.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_4));
                break;
            case 5:
                imgBackground.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_5));
                break;
            case 6:
                imgBackground.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_6));
                break;
            case 7:
                imgBackground.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.bg_progress_item_7));
                break;
        }

        container.addView(view);
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

    @Override
    public Parcelable saveState() {
        return null;
    }
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
