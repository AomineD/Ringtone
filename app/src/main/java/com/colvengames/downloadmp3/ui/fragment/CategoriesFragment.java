package com.colvengames.downloadmp3.ui.fragment;


import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.colvengames.downloadmp3.R;
import com.colvengames.downloadmp3.adapter.SectionAdapter;
import com.colvengames.downloadmp3.api.apiClient;
import com.colvengames.downloadmp3.api.apiRest;
import com.colvengames.downloadmp3.entity.Category;
import com.colvengames.downloadmp3.entity.Section;
import com.colvengames.downloadmp3.entity.Tag;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


/**
 * A simple {@link Fragment} subclass.
 */
public class CategoriesFragment extends Fragment {

    private Boolean subCategories = false;

    private View view;
    private boolean loaded;
    private RelativeLayout relative_layout_categories_fragment;
    private SwipeRefreshLayout swipe_refreshl_categories_fragment;
    private ImageView image_view_empty;
    private RecyclerView recycle_view_categories_fragment;
    private RelativeLayout relative_layout_load_more;
    private LinearLayout linear_layout_page_error;
    private Button button_try_again;
    private GridLayoutManager gridLayoutManager;
    private SectionAdapter sectionAdapter;

    private List<Section> sectionList=new ArrayList<>();
    private List<Category> categories=new ArrayList<>();
    private List<Tag> tagList=new ArrayList<>();

    public CategoriesFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        this.view =   inflater.inflate(R.layout.fragment_categories, container, false);
        initView();
        initAction();
        return view;
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser){
            if (!loaded){
                loadData();
            }
        }
    }
    private void loadData() {

            sectionList.clear();

            recycle_view_categories_fragment.setVisibility(View.VISIBLE);
            image_view_empty.setVisibility(View.GONE);
            linear_layout_page_error.setVisibility(View.GONE);
            swipe_refreshl_categories_fragment.setRefreshing(true);

        loadColor();
    }

    private void LoadSections() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Section>> call = service.SectionList();
        call.enqueue(new Callback<List<Section>>() {
            @Override
            public void onResponse(Call<List<Section>> call, Response<List<Section>> response) {
                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        Log.e("MAINY", "onResponse algo: "+response.body().get(0).getImage());
                        for (int i=0;i<response.body().size();i++){
                            sectionList.add(response.body().get(i));
                        }
                        sectionAdapter.notifyDataSetChanged();
                        recycle_view_categories_fragment.setVisibility(View.VISIBLE);
                        image_view_empty.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }else {
                        Log.e("MAINY", "onResponse Vacio: "+response.body().size());
                        recycle_view_categories_fragment.setVisibility(View.GONE);
                        image_view_empty.setVisibility(View.VISIBLE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }
                    loaded= true;
                }else{
                    recycle_view_categories_fragment.setVisibility(View.GONE);
                    image_view_empty.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
                swipe_refreshl_categories_fragment.setRefreshing(false);

            }
            @Override
            public void onFailure(Call<List<Section>> call, Throwable t) {
                recycle_view_categories_fragment.setVisibility(View.GONE);
                image_view_empty.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_categories_fragment.setRefreshing(false);

            }
        });
    }

    private void initAction() {
        swipe_refreshl_categories_fragment.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadData();
            }
        });
        this.button_try_again.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadData();
            }
        });
    }

    private void initView() {
        this.relative_layout_categories_fragment=(RelativeLayout) view.findViewById(R.id.relative_layout_categories_fragment);
        this.swipe_refreshl_categories_fragment=(SwipeRefreshLayout) view.findViewById(R.id.swipe_refreshl_categories_fragment);
        this.image_view_empty=(ImageView) view.findViewById(R.id.image_view_empty);
        this.recycle_view_categories_fragment=(RecyclerView) view.findViewById(R.id.recycle_view_categories_fragment);
        this.relative_layout_load_more=(RelativeLayout) view.findViewById(R.id.relative_layout_load_more);
        this.linear_layout_page_error=(LinearLayout) view.findViewById(R.id.linear_layout_page_error);
        this.button_try_again=(Button) view.findViewById(R.id.button_try_again);

        this.gridLayoutManager=  new GridLayoutManager(getActivity().getApplicationContext(),1,GridLayoutManager.VERTICAL,false);
        this.sectionAdapter =new SectionAdapter(sectionList,tagList,getActivity());
        recycle_view_categories_fragment.setHasFixedSize(true);
        recycle_view_categories_fragment.setAdapter(sectionAdapter);
        recycle_view_categories_fragment.setLayoutManager(gridLayoutManager);
    }
    private void loadColor() {

        tagList.clear();
        sectionList.clear();
        sectionAdapter.notifyDataSetChanged();

        recycle_view_categories_fragment.setVisibility(View.VISIBLE);
        image_view_empty.setVisibility(View.GONE);
        linear_layout_page_error.setVisibility(View.GONE);
        swipe_refreshl_categories_fragment.setRefreshing(true);

        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Tag>> call = service.TagList();
        call.enqueue(new Callback<List<Tag>>() {
            @Override
            public void onResponse(Call<List<Tag>> call, Response<List<Tag>> response) {
                apiClient.FormatData(getActivity(),response);
                if(response.isSuccessful()){
                    if (response.body().size()!=0){
                        for (int i=0;i<response.body().size();i++){
                            tagList.add(response.body().get(i));
                        }
                        sectionList.add(new Section().setViewType(2));
                    }
                    sectionAdapter.notifyDataSetChanged();
                    if (subCategories){
                        LoadSections();
                    }else{
                        LoadCategories();
                    }
                }else{
                    recycle_view_categories_fragment.setVisibility(View.GONE);
                    image_view_empty.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                    swipe_refreshl_categories_fragment.setRefreshing(false);
                }

            }
            @Override
            public void onFailure(Call<List<Tag>> call, Throwable t) {
                recycle_view_categories_fragment.setVisibility(View.GONE);
                image_view_empty.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_categories_fragment.setRefreshing(false);
            }
        });

        ImageView viewimga = view.findViewById(R.id.backhome);

        int w = getResources().getDisplayMetrics().widthPixels;
        int h = getResources().getDisplayMetrics().heightPixels;

        Picasso.with(getContext()).load(Uri.parse(HomeFragment.urlHome)).resize(w, h).into(viewimga);



    }

    private void LoadCategories() {
        Retrofit retrofit = apiClient.getClient();
        apiRest service = retrofit.create(apiRest.class);
        Call<List<Category>> call = service.categoryAll();
        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(Call<List<Category>> call, Response<List<Category>> response) {
                if(response.isSuccessful()){
                    if (response.body().size()!=0){

                        for (int i=0;i<response.body().size();i++){
                            Section section= new Section();
                            section.setId(response.body().get(i).getId());
                            section.setImage(response.body().get(i).getImage());
                            section.setTitle(response.body().get(i).getTitle());
                            section.setViewType(3);
                            sectionList.add(section);
                        }
                        sectionAdapter.notifyDataSetChanged();
                        recycle_view_categories_fragment.setVisibility(View.VISIBLE);
                        image_view_empty.setVisibility(View.GONE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }else {
                        recycle_view_categories_fragment.setVisibility(View.GONE);
                        image_view_empty.setVisibility(View.VISIBLE);
                        linear_layout_page_error.setVisibility(View.GONE);
                    }
                    loaded= true;
                }else{
                    recycle_view_categories_fragment.setVisibility(View.GONE);
                    image_view_empty.setVisibility(View.GONE);
                    linear_layout_page_error.setVisibility(View.VISIBLE);
                }
                swipe_refreshl_categories_fragment.setRefreshing(false);

            }
            @Override
            public void onFailure(Call<List<Category>> call, Throwable t) {
                recycle_view_categories_fragment.setVisibility(View.GONE);
                image_view_empty.setVisibility(View.GONE);
                linear_layout_page_error.setVisibility(View.VISIBLE);
                swipe_refreshl_categories_fragment.setRefreshing(false);

            }
        });
    }
}
