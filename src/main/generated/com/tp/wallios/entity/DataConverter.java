package com.tp.wallios.entity;

import io.vertx.core.json.JsonObject;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.impl.JsonUtil;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

/**
 * Converter and mapper for {@link com.tp.wallios.entity.Data}.
 * NOTE: This class has been automatically generated from the {@link com.tp.wallios.entity.Data} original class using Vert.x codegen.
 */
public class DataConverter {


  public static void fromJson(Iterable<java.util.Map.Entry<String, Object>> json, Data obj) {
    for (java.util.Map.Entry<String, Object> member : json) {
      switch (member.getKey()) {
        case "categories":
          if (member.getValue() instanceof String) {
            obj.setCategories((String)member.getValue());
          }
          break;
        case "countByCountry":
          if (member.getValue() instanceof String) {
            obj.setCountByCountry((String)member.getValue());
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
        case "defaultCountry":
          if (member.getValue() instanceof String) {
            obj.setDefaultCountry((String)member.getValue());
          }
          break;
        case "downCount":
          if (member.getValue() instanceof Number) {
            obj.setDownCount(((Number)member.getValue()).intValue());
          }
          break;
        case "favorite":
          if (member.getValue() instanceof Number) {
            obj.setFavorite(((Number)member.getValue()).longValue());
          }
          break;
        case "hashtag":
          if (member.getValue() instanceof String) {
            obj.setHashtag((String)member.getValue());
          }
          break;
        case "homeType":
          if (member.getValue() instanceof String) {
            obj.setHomeType((String)member.getValue());
          }
          break;
        case "id":
          if (member.getValue() instanceof Number) {
            obj.setId(((Number)member.getValue()).intValue());
          }
          break;
        case "imgSize":
          if (member.getValue() instanceof String) {
            obj.setImgSize((String)member.getValue());
          }
          break;
        case "index":
          if (member.getValue() instanceof Boolean) {
            obj.setIndex((Boolean)member.getValue());
          }
          break;
        case "isApproved":
          if (member.getValue() instanceof String) {
            obj.setIsApproved((String)member.getValue());
          }
          break;
        case "isMin":
          if (member.getValue() instanceof Boolean) {
            obj.setIsMin((Boolean)member.getValue());
          }
          break;
        case "lastPlayDate":
          if (member.getValue() instanceof Number) {
            obj.setLastPlayDate(((Number)member.getValue()).longValue());
          }
          break;
        case "level":
          if (member.getValue() instanceof Number) {
            obj.setLevel(((Number)member.getValue()).intValue());
          }
          break;
        case "name":
          if (member.getValue() instanceof String) {
            obj.setName((String)member.getValue());
          }
          break;
        case "owner":
          if (member.getValue() instanceof String) {
            obj.setOwner((String)member.getValue());
          }
          break;
        case "playCount":
          if (member.getValue() instanceof Number) {
            obj.setPlayCount(((Number)member.getValue()).longValue());
          }
          break;
        case "screenRatio":
          if (member.getValue() instanceof Number) {
            obj.setScreenRatio(((Number)member.getValue()).intValue());
          }
          break;
        case "searchName":
          if (member.getValue() instanceof String) {
            obj.setSearchName((String)member.getValue());
          }
          break;
        case "supportIpad":
          if (member.getValue() instanceof Number) {
            obj.setSupportIpad(((Number)member.getValue()).intValue());
          }
          break;
        case "type":
          if (member.getValue() instanceof Number) {
            obj.setType(((Number)member.getValue()).intValue());
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

  public static void toJson(Data obj, JsonObject json) {
    toJson(obj, json.getMap());
  }

  public static void toJson(Data obj, java.util.Map<String, Object> json) {
    if (obj.getCategories() != null) {
      json.put("categories", obj.getCategories());
    }
    if (obj.getCountByCountry() != null) {
      json.put("countByCountry", obj.getCountByCountry());
    }
    if (obj.getCountries() != null) {
      json.put("countries", obj.getCountries());
    }
    if (obj.getCreatedDate() != null) {
      json.put("createdDate", obj.getCreatedDate());
    }
    if (obj.getDefaultCountry() != null) {
      json.put("defaultCountry", obj.getDefaultCountry());
    }
    if (obj.getDownCount() != null) {
      json.put("downCount", obj.getDownCount());
    }
    if (obj.getFavorite() != null) {
      json.put("favorite", obj.getFavorite());
    }
    if (obj.getHashtag() != null) {
      json.put("hashtag", obj.getHashtag());
    }
    if (obj.getHomeType() != null) {
      json.put("homeType", obj.getHomeType());
    }
    if (obj.getId() != null) {
      json.put("id", obj.getId());
    }
    if (obj.getImgSize() != null) {
      json.put("imgSize", obj.getImgSize());
    }
    if (obj.getIndex() != null) {
      json.put("index", obj.getIndex());
    }
    if (obj.getIsApproved() != null) {
      json.put("isApproved", obj.getIsApproved());
    }
    if (obj.getIsMin() != null) {
      json.put("isMin", obj.getIsMin());
    }
    if (obj.getLastPlayDate() != null) {
      json.put("lastPlayDate", obj.getLastPlayDate());
    }
    if (obj.getLevel() != null) {
      json.put("level", obj.getLevel());
    }
    if (obj.getName() != null) {
      json.put("name", obj.getName());
    }
    if (obj.getOwner() != null) {
      json.put("owner", obj.getOwner());
    }
    if (obj.getPlayCount() != null) {
      json.put("playCount", obj.getPlayCount());
    }
    if (obj.getScreenRatio() != null) {
      json.put("screenRatio", obj.getScreenRatio());
    }
    if (obj.getSearchName() != null) {
      json.put("searchName", obj.getSearchName());
    }
    if (obj.getSupportIpad() != null) {
      json.put("supportIpad", obj.getSupportIpad());
    }
    if (obj.getType() != null) {
      json.put("type", obj.getType());
    }
    if (obj.getUrl() != null) {
      json.put("url", obj.getUrl());
    }
  }
}
