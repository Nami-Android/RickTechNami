package com.app.ricktech.models;

import java.io.Serializable;
import java.util.List;

public class CategoryBuildingDataModel extends StatusResponse implements Serializable {
    private List<CategoryModel> data;

    public List<CategoryModel> getData() {
        return data;
    }
}
