package com.app.ricktech.uis.suggestions_module.activity_suggestion_sub_building;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.app.ricktech.R;
import com.app.ricktech.adapters.SubBuildingAdapter;
import com.app.ricktech.databinding.ActivitySubBuildingBinding;
import com.app.ricktech.databinding.ActivitySuggestionSubBuildingBinding;
import com.app.ricktech.language.Language;
import com.app.ricktech.models.AddBuildModel;
import com.app.ricktech.models.CategoryBuildingDataModel;
import com.app.ricktech.models.CategoryModel;
import com.app.ricktech.models.ProductModel;
import com.app.ricktech.models.SuggestionModel;
import com.app.ricktech.models.SuggestionsDataModel;
import com.app.ricktech.models.UserModel;
import com.app.ricktech.preferences.Preferences;
import com.app.ricktech.remote.Api;
import com.app.ricktech.tags.Tags;
import com.app.ricktech.uis.pc_building_module.activity_building_products.ProductBuildingActivity;
import com.ethanhua.skeleton.SkeletonScreen;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.paperdb.Paper;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SuggestionSubBuildingActivity extends AppCompatActivity {

    private ActivitySuggestionSubBuildingBinding binding;
    private String lang;
    private SubBuildingAdapter adapter;
    private UserModel userModel;
    private Preferences preferences;
    private List<CategoryModel> list;
    private SuggestionModel addBuildModel;
    private String  parent_id;
    private ActivityResultLauncher<Intent> launcher;
    private int selectedPos = -1;
    private CategoryModel categoryModel;
    private int req;
    private boolean canNext = false;
    private boolean hasData = false;

    protected void attachBaseContext(Context newBase) {
        Paper.init(newBase);
        super.attachBaseContext(Language.updateResources(newBase, Paper.book().read("lang", "de")));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_suggestion_sub_building);
        getDataFromIntent();
        initView();
    }

    private void getDataFromIntent() {
        Intent intent = getIntent();
        parent_id = intent.getStringExtra("data");
        addBuildModel = (SuggestionModel) intent.getSerializableExtra("data2");
        if (addBuildModel.getSub_categoryModel().size()>0){
            hasData = true;
        }
    }

    private void initView() {
        preferences = Preferences.getInstance();
        userModel = preferences.getUserData(this);
        list = new ArrayList<>();
        Paper.init(this);
        lang = Paper.book().read("lang", "de");
        binding.setLang(lang);
        binding.recView.setLayoutManager(new GridLayoutManager(this,2));
        adapter = new SubBuildingAdapter(this, list);
        binding.recView.setAdapter(adapter);
        binding.recView.setItemAnimator(new DefaultItemAnimator());
        binding.llBack.setOnClickListener(v -> finish());


        binding.shimmer.startShimmer();
        getSubBuildings();

        launcher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                if (req == 100 && result.getResultCode() == RESULT_OK && result.getData() != null) {

                    ProductModel productModel = (ProductModel) result.getData().getSerializableExtra("data");
                    if (productModel != null) {
                        if (selectedPos != -1) {

                            List<ProductModel> productModelList = new ArrayList<>(list.get(selectedPos).getSelectedProduct());
                            productModelList.add(productModel);
                            CategoryModel categoryModel = list.get(selectedPos);
                            categoryModel.setSelectedProduct(productModelList);
                            list.set(selectedPos,categoryModel);
                            adapter.notifyItemChanged(selectedPos);

                            canNext = true;
                            binding.btnSave.setBackgroundResource(R.drawable.small_rounded_primary);

                        }
                    }





                }

            }
        });

        binding.btnSave.setOnClickListener(v -> {
            if (canNext){
                List<CategoryModel> data = new ArrayList<>(getSelectedSubCategory());

                Intent intent = getIntent();
                intent.putExtra("data", (Serializable) data);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void getSubBuildings() {
        Api.getService(Tags.base_url)
                .getSubCategoryBuilding(lang,parent_id)
                .enqueue(new Callback<CategoryBuildingDataModel>() {
                    @Override
                    public void onResponse(Call<CategoryBuildingDataModel> call, Response<CategoryBuildingDataModel> response) {
                        binding.shimmer.stopShimmer();
                        binding.shimmer.setVisibility(View.GONE);
                        binding.llData.setVisibility(View.VISIBLE);
                        if (response.isSuccessful() && response.body() != null && response.body().getStatus() == 200) {
                            if (response.body().getData().size() > 0) {
                                updateData(response.body().getData());
                                binding.tvNoData.setVisibility(View.GONE);

                            } else {
                                binding.tvNoData.setVisibility(View.VISIBLE);


                            }

                        } else {
                            binding.shimmer.stopShimmer();
                            binding.shimmer.setVisibility(View.GONE);
                        }


                    }

                    @Override
                    public void onFailure(Call<CategoryBuildingDataModel> call, Throwable t) {
                        try {
                            Log.e("error", t.getMessage() + "__");
                            binding.shimmer.stopShimmer();
                            binding.shimmer.setVisibility(View.GONE);

                        } catch (Exception e) {

                        }
                    }
                });
    }

    private void updateData(List<CategoryModel> data)
    {

        list.clear();
        List<CategoryModel> categoryModelList = new ArrayList<>();

        for (int index=0;index<data.size();index++){
            CategoryModel model = data.get(index);
            for (CategoryModel categoryModel:addBuildModel.getSub_categoryModel()){

                if (categoryModel.getId()==model.getId()){
                    model.setSelectedProduct(categoryModel.getSelectedProduct());
                    model.setDefaultSelectedProduct(new ArrayList<>(categoryModel.getDefaultSelectedProduct()));
                    model.setSub_categories(categoryModel.getSub_categories());


                }

            }




            categoryModelList.add(model);


        }



        if (categoryModelList.size()==0){
            categoryModelList.addAll(data);
        }

        list.addAll(categoryModelList);

        adapter.notifyDataSetChanged();

        if(isListHasData()){
            canNext = true;
            binding.btnSave.setBackgroundResource(R.drawable.small_rounded_primary);

        }else {
            canNext = false;
            binding.btnSave.setBackgroundResource(R.drawable.small_rounded_gray77);

        }

    }

    public void setItemData(int adapterPosition, CategoryModel categoryModel) {
        this.selectedPos = adapterPosition;
        this.categoryModel = categoryModel;
        if (categoryModel.getIs_final_level().equals("yes")) {
            req = 100;
            Intent intent = new Intent(this, ProductBuildingActivity.class);
            intent.putExtra("data", categoryModel.getId() + "");
            intent.putExtra("data2", (Serializable) categoryModel.getSelectedProduct());
            launcher.launch(intent);

        }


    }

    public void deleteItemData(int adapterPosition, CategoryModel categoryModel) {
        categoryModel.getSelectedProduct().clear();
        list.set(adapterPosition,categoryModel);
        adapter.notifyItemChanged(adapterPosition);


        if(isListHasData()){
            canNext = true;
            binding.btnSave.setBackgroundResource(R.drawable.small_rounded_primary);
        }else {
            if (hasData){
                canNext = true;
                binding.btnSave.setBackgroundResource(R.drawable.small_rounded_primary);
            }else{
                canNext = false;
                binding.btnSave.setBackgroundResource(R.drawable.small_rounded_gray77);

            }

        }

    }

    private boolean isListHasData(){
        boolean hasData = false;
        for (CategoryModel model : list) {

            if (model.getSelectedProduct().size()>0){
                hasData = true;
            }


        }
        return hasData;
    }

    private List<CategoryModel> getSelectedSubCategory(){
        List<CategoryModel> categoryModelList = new ArrayList<>();
        for (CategoryModel categoryModel:list){
            if (categoryModel.getSelectedProduct().size()>0){
                categoryModelList.add(categoryModel);
            }
        }
        return categoryModelList;
    }

}