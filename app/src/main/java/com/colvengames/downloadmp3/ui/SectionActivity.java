package com.colvengames.downloadmp3.ui;

import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.adapter.CategoryAdapter;
import com.colvengames.downloadmp3.api.apiClient;
import com.colvengames.downloadmp3.api.apiRest;
import com.colvengames.downloadmp3.entity.Category;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SectionActivity extends AppCompatActivity {


    private boolean loaded;
    private SwipeRefreshLayout swipe_refreshl_section_activity;
    private ImageView image_view_empty;
    private RecyclerView recycle_view_section_activity;
    private LinearLayout linear_layout_page_error;
    private Button button_try_again;
    private GridLayoutManager gridLayoutManager;
    private CategoryAdapter categoryAdapter;
    private List<Category> categoryList=new ArrayList<>();

    private int id;
    private String title;
    private String image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_section);
        Bundle bundle = getIntent().getExtras() ;
        this.id =  bundle.getInt("id");
        this.title =  bundle.getString("title");
        this.image =  bundle.getString("image");
        if(getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(title);

        initView();
        initAction();
        getCategories();

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


    private void initAction() {
        this.button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCategories();
            }
        });
        this.swipe_refreshl_section_activity.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getCategories();
            }
        });
    }

    private void initView() {
        this.swipe_refreshl_section_activity=(SwipeRefreshLayout) findViewById(R.id.swipe_refreshl_section_activity);
        this.image_view_empty=(ImageView) findViewById(R.id.image_view_empty);
        this.recycle_view_section_activity=(RecyclerView) findViewById(R.id.recycle_view_section_activity);
        this.linear_layout_page_error=(LinearLayout) findViewById(R.id.linear_layout_page_error);
        this.button_try_again=(Button) findViewById(R.id.button_try_again);

        this.gridLayoutManager=  new GridLayoutManager(getApplicationContext(),1,GridLayoutManager.VERTICAL,false);
        this.categoryAdapter =new CategoryAdapter(categoryList,this);
        recycle_view_section_activity.setHasFixedSize(true);
        recycle_view_section_activity.setAdapter(categoryAdapter);
        recycle_view_section_activity.setLayoutManager(gridLayoutManager);
    }

    public void getCategories() {

        categoryList.clear();

        recycle_view_section_activity.setVisibility(View.VISIBLE);
        image_view_empty.setVisibility(View.GONE);
        linear_layout_page_error.setVisibility(View.GONE);
        swipe_refreshl_section_activity.setRefreshing(true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Category>> call = service.categoryList(id);
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                apiClient.FormatData(SectionActivity.this,response);
                if(response.isSuccessful()){
                    if (response.body().size()!=0){

                        for (int i=0;i<response.body().size();i++){
                            categoryList.add(response.body().get(i));
                        }
                        categoryAdapter.notifyDataSetChanged();
                        recycle_view_section_activity.setVisibility(View.VISIBLE);
                        image_view_empty.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }else {
                        recycle_view_section_activity.setVisibility(View.GONE);
                        image_view_empty.setVisibility(View.VISIBLE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }
                    loaded= true;
                }else{
                    recycle_view_section_activity.setVisibility(View.GONE);
                    image_view_empty.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
                swipe_refreshl_section_activity.setRefreshing(false);

            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                recycle_view_section_activity.setVisibility(View.GONE);
                image_view_empty.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_section_activity.setRefreshing(false);

            }
        });
    }
}
