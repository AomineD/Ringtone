package com.colvengames.downloadmp3.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.entity.Tag;
import com.colvengames.downloadmp3.ui.SearchActivity;

import java.util.List;

/**
 * Created by hsn on 05/04/2018.
 */

public class TagAdapter extends  RecyclerView.Adapter<TagAdapter.TagHolder>{
    private List<Tag> tagList;
    private Activity activity;
    private Boolean tags = false;
    public TagAdapter(List<Tag> tagList, Activity activity) {
        this.tagList = tagList;
        this.activity = activity;
    }

    public TagAdapter(List<Tag> tagList, Activity activity, Boolean tags) {
        this.tagList = tagList;
        this.activity = activity;
        this.tags = tags;
    }

    @Override
    public TagHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_tag, null);
        TagHolder mh = new TagHolder(v);
        return mh;
    }

    @Override
    public void onBindViewHolder(TagHolder holder,final int position) {
        holder.text_view_item_tag_item.setText(tagList.get(position).getName());
        if (!tags) {
            String[] colorsTxt = activity.getResources().getStringArray(R.array.colors);

            int step = 1;
            int final_step = 1;
            for (int i = 1; i < position + 1; i++) {
                if (i == position + 1) {
                    final_step = step;
                }
                step++;
                if (step > 5) {
                    step = 1;
                }
            }

            holder.card_view_tag_item_global.setCardBackgroundColor(Color.parseColor(colorsTxt[step-1]));

            holder.card_view_tag_item_global.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Intent intent_video  =  new Intent(activity, SearchActivity.class);
                        intent_video.putExtra("query",tagList.get(position).getName());
                        activity.startActivity(intent_video);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }catch (IndexOutOfBoundsException e){

                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return tagList.size();
    }

    public class TagHolder extends RecyclerView.ViewHolder {
        public TextView text_view_item_tag_item ;
        public CardView card_view_tag_item_global ;

        public TagHolder(View itemView) {
            super(itemView);
            this.card_view_tag_item_global=(CardView) itemView.findViewById(R.id.card_view_tag_item_global);
            this.text_view_item_tag_item=(TextView) itemView.findViewById(R.id.text_view_item_tag_item);
        }
    }
}
