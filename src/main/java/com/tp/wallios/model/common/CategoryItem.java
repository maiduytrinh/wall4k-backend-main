package com.tp.wallios.model.common;

import com.tp.wallios.entity.reference.CategoryType;
import io.vertx.core.json.JsonObject;

import java.util.List;

import static com.tp.wallios.entity.reference.CategoryType.FOR_YOU;

public class CategoryItem extends  JsonObject{
    public CategoryItem() {

    }

    public <T> CategoryItem(JsonObject cateJson, List<T> data, String type) {
        this.put("id", cateJson.getLong("id") == null ? "" : cateJson.getLong("id"));
        this.put("name", cateJson.getString("nameHomeScreen") == null ? "" : cateJson.getString("nameHomeScreen"));
        this.put("shortName", cateJson.getString("name") == null ? cateJson.getString("nameHomeScreen") : cateJson.getString("name"));
        this.put("type", type);
        this.put("data", data);
    }
}
