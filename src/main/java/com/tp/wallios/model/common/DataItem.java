package com.tp.wallios.model.common;

import com.tp.wallios.entity.reference.DataType;
import io.vertx.core.json.JsonObject;

public class DataItem extends JsonObject {
    public DataItem(JsonObject jsonObject, String type) {
        this.put("id", jsonObject.getLong("id"));
        this.put("name", jsonObject.getString("name"));
        this.put("hashtag", jsonObject.getString("hashtag"));
        this.put("countries", jsonObject.getString("countries"));
        this.put("categories", jsonObject.getString("categories"));
        this.put("url", jsonObject.getString("url"));
        this.put("contentType", jsonObject.getLong("type") == 1 ? DataType.DEPTH_EFFECT.value : type);
        this.put("createdDate", jsonObject.getLong("createdDate"));
    }


}
