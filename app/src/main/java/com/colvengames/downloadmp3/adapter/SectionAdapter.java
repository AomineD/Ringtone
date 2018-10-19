package com.colvengames.downloadmp3.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.entity.Section;
import com.colvengames.downloadmp3.entity.Tag;
import com.colvengames.downloadmp3.ui.CategoryActivity;
import com.colvengames.downloadmp3.ui.SectionActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by hsn on 02/12/2017.
 */


public class SectionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<Section> sectionList;
    private Activity activity;
    private LinearLayoutManager linearLayoutManager;
    private List<Tag> tagList;
    private TagAdapter tagAdapter;

    public SectionAdapter(List<Section> sectionList, List<Tag> tagList, Activity activity) {
        this.sectionList = sectionList;
        this.activity = activity;
        this.tagList=tagList;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        RecyclerView.ViewHolder viewHolder = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        switch (viewType) {
            case 1: {
                View v1 = inflater.inflate(R.layout.item_section, parent, false);
                viewHolder = new SectionAdapter.SectionHolder(v1);
                break;
            }
            case 2: {
                View v2 = inflater.inflate(R.layout.item_tags, parent, false);
                viewHolder = new SectionAdapter.TagsHolder(v2);
                break;
            }
            case 3: {
                View v3 = inflater.inflate(R.layout.item_category, parent, false);
                viewHolder = new SectionAdapter.CategoryHolder(v3);
                break;
            }
        }
        return viewHolder;


    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder_parent,final int position) {

        switch (getItemViewType(position)) {

            case 1: {
                final SectionAdapter.SectionHolder holder = (SectionAdapter.SectionHolder) holder_parent;
                Typeface face = Typeface.createFromAsset(activity.getAssets(), "Pattaya-Regular.ttf");
                holder.text_view_item_section_title.setTypeface(face);

                holder.getTextView().setText(sectionList.get(position).getTitle());


                Picasso.with(activity.getApplicationContext()).load(sectionList.get(position).getImage()).placeholder(R.drawable.placeholder).into(holder.image_view_item_section_image);
                holder.card_view_item_section.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                     try {
                        Intent intent  =  new Intent(activity.getApplicationContext(), SectionActivity.class);
                        intent.putExtra("id",sectionList.get(position).getId());
                        intent.putExtra("title",sectionList.get(position).getTitle());
                        activity.startActivity(intent);
                        activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                    }catch (IndexOutOfBoundsException e){

                    }
                    }
                });
                break;
            }
            case 2: {
                final SectionAdapter.TagsHolder holder = (SectionAdapter.TagsHolder) holder_parent;
                this.linearLayoutManager=  new LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false);
                this.tagAdapter =new TagAdapter(tagList,activity);
                holder.recycle_view_tags_items.setHasFixedSize(true);
                holder.recycle_view_tags_items.setAdapter(tagAdapter);
                holder.recycle_view_tags_items.setLayoutManager(linearLayoutManager);
                tagAdapter.notifyDataSetChanged();
                break;
            }
            case 3: {
                final SectionAdapter.CategoryHolder holder = (SectionAdapter.CategoryHolder) holder_parent;
                Typeface face = Typeface.createFromAsset(activity.getAssets(), "Pattaya-Regular.ttf");
                holder.text_view_item_category_title.setTypeface(face);

                holder.getTextView().setText(sectionList.get(position).getTitle());

                Picasso.with(activity.getApplicationContext()).load(sectionList.get(position).getImage()).placeholder(R.drawable.placeholder).into(holder.image_view_item_category_image);
                holder.card_view_item_category.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        try {
                            Intent intent  =  new Intent(activity.getApplicationContext(), CategoryActivity.class);
                            intent.putExtra("id",sectionList.get(position).getId());
                            intent.putExtra("title",sectionList.get(position).getTitle());
                            activity.startActivity(intent);
                            activity.overridePendingTransition(R.anim.enter, R.anim.exit);
                        }catch (IndexOutOfBoundsException e){

                        }

                    }
                });
                break;
            }
        }
    }
    @Override
    public int getItemCount() {
        return sectionList.size();
    }

    public static class SectionHolder extends RecyclerView.ViewHolder{
        private final ImageView image_view_item_section_image;
        private CardView card_view_item_section;
        private TextView text_view_item_section_title;
        public SectionHolder(View itemView) {
            super(itemView);
            this.card_view_item_section=(CardView) itemView.findViewById(R.id.card_view_item_section);
            this.text_view_item_section_title=(TextView) itemView.findViewById(R.id.text_view_item_section_title);
            this.image_view_item_section_image=(ImageView) itemView.findViewById(R.id.image_view_item_section_image);
        }

        public TextView getTextView() {
            return text_view_item_section_title;
        }
    }

    public static class TagsHolder extends RecyclerView.ViewHolder {
        private final RecyclerView recycle_view_tags_items;


        public TagsHolder(View view) {
            super(view);
            recycle_view_tags_items = (RecyclerView) itemView.findViewById(R.id.recycle_view_tags_items);
        }
    }

    public static class CategoryHolder extends RecyclerView.ViewHolder {
        private final ImageView image_view_item_category_image;
        private CardView card_view_item_category;
        private TextView text_view_item_category_title;
        public CategoryHolder(View itemView) {
            super(itemView);
            this.card_view_item_category=(CardView) itemView.findViewById(R.id.card_view_item_category);
            this.text_view_item_category_title=(TextView) itemView.findViewById(R.id.text_view_item_category_title);
            this.image_view_item_category_image=(ImageView) itemView.findViewById(R.id.image_view_item_category_image);
        }
        public TextView getTextView() {
            return text_view_item_category_title;
        }
    }
    @Override
    public int getItemViewType(int position) {
        return sectionList.get(position).getViewType();
    }
}
