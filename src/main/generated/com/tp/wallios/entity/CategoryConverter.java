package com.tp.wallios.entity;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter and mapper for {@link com.tp.wallios.entity.Category}.
 * NOTE: This class has been automatically generated from the {@link com.tp.wallios.entity.Category} original class using Vert.x codegen.
 */
public class CategoryConverter {


  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Category obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "count":
          if (member.getValue() instanceof Number) {
            obj.setCount(((Number)member.getValue()).intValue());
          }
          break;
        case "countries":
          if (member.getValue() instanceof String) {
            obj.setCountries((String)member.getValue());
          }
          break;
        case "createdDate":
          if (member.getValue() instanceof Number) {
            obj.setCreatedDate(((Number)member.getValue()).longValue());
          }
          break;
        case "displayByLang":
          if (member.getValue() instanceof String) {
            obj.setDisplayByLang((String)member.getValue());
          }
          break;
        case "id":
          if (member.getValue() instanceof Number) {
            obj.setId(((Number)member.getValue()).intValue());
          }
          break;
        case "isPublic":
          if (member.getValue() instanceof String) {
            obj.setIsPublic((String)member.getValue());
          }
          break;
        case "name":
          if (member.getValue() instanceof String) {
            obj.setName((String)member.getValue());
          }
          break;
        case "nameHomeScreen":
          if (member.getValue() instanceof String) {
            obj.setNameHomeScreen((String)member.getValue());
          }
          break;
        case "order":
          if (member.getValue() instanceof Number) {
            obj.setOrder(((Number)member.getValue()).intValue());
          }
          break;
        case "owner":
          if (member.getValue() instanceof String) {
            obj.setOwner((String)member.getValue());
          }
          break;
        case "types":
          if (member.getValue() instanceof String) {
            obj.setTypes((String)member.getValue());
          }
          break;
        case "url":
          if (member.getValue() instanceof String) {
            obj.setUrl((String)member.getValue());
          }
          break;
      }
    }
  }

  public static void toJson(Category obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Category obj, java.util.Map<String, Object> json) {
    if (obj.getCount() != null) {
      json.put("count", obj.getCount());
    }
    if (obj.getCountries() != null) {
      json.put("countries", obj.getCountries());
    }
    if (obj.getCreatedDate() != null) {
      json.put("createdDate", obj.getCreatedDate());
    }
    if (obj.getDisplayByLang() != null) {
      json.put("displayByLang", obj.getDisplayByLang());
    }
    if (obj.getId() != null) {
      json.put("id", obj.getId());
    }
    if (obj.getIsPublic() != null) {
      json.put("isPublic", obj.getIsPublic());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    if (obj.getNameHomeScreen() != null) {
      json.put("nameHomeScreen", obj.getNameHomeScreen());
    }
    if (obj.getOrder() != null) {
      json.put("order", obj.getOrder());
    }
    if (obj.getOwner() != null) {
      json.put("owner", obj.getOwner());
    }
    if (obj.getTypes() != null) {
      json.put("types", obj.getTypes());
    }
    if (obj.getUrl() != null) {
      json.put("url", obj.getUrl());
    }
  }
}
