package com.colvengames.downloadmp3.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.entity.Category;
import com.colvengames.downloadmp3.ui.CategoryActivity;

import java.util.List;

/**
 * Created by hsn on 05/12/2017.
 */


public class CategoryAdapterRingtone extends RecyclerView.Adapter<CategoryAdapterRingtone.CategoryHolder>{
    private List<Category> categoryList;
    private Activity activity;
    private Boolean tags = false;
    public CategoryAdapterRingtone(List<Category> categoryList, Activity activity) {
        this.categoryList = categoryList;
        this.activity = activity;
    }


    @Override
    public CategoryAdapterRingtone.CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_category_ringtone, null);
        CategoryAdapterRingtone.CategoryHolder mh = new CategoryAdapterRingtone.CategoryHolder(v);
        return mh;
    }


    @Override
    public void onBindViewHolder(CategoryAdapterRingtone.CategoryHolder holder, final int position) {
        holder.text_view_item_category_item.setText(categoryList.get(position).getTitle());
        holder.card_view_category_item_global.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity.getApplicationContext(), CategoryActivity.class);
                intent.putExtra("id", categoryList.get(position).getId());
                intent.putExtra("title", categoryList.get(position).getTitle());
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.enter, R.anim.exit);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categoryList.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder {
        public TextView text_view_item_category_item;
        public CardView card_view_category_item_global;

        public CategoryHolder(View itemView) {
            super(itemView);
            this.card_view_category_item_global=(CardView) itemView.findViewById(R.id.card_view_category_item_global);
            this.text_view_item_category_item=(TextView) itemView.findViewById(R.id.text_view_item_category_item);
        }
    }
}
