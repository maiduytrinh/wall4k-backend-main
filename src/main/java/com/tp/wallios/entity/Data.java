package com.tp.wallios.entity;

import com.tp.wallios.utils.AppUtils;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject(generateConverter = true)
public class Data {
	private Integer id;
	private String name;
	private String hashtag;
	private String searchName;
	private String countries;
	private String categories;
	private String url;
	private Integer screenRatio;
	private String imgSize;
	private String owner;
	private Integer downCount;
	private String countByCountry;
	private Long playCount;
	private Long favorite;
	private Integer level;
	private String defaultCountry;
	private String isApproved;
	private Long lastPlayDate;
	private Boolean isMin;
	private Boolean isIndex;
	private Long createdDate;
	private Integer type;
	private Integer supportIpad;

	private String homeType = "";

	public Data() {
	}

	public Data(JsonObject json) {
		DataConverter.fromJson(json, this);
	}

	public static Data valueOf(String json) {
		return AppUtils.fromJson(json, Data.class);
	}

	public static void merge(JsonObject json, Data obj) {
		DataConverter.fromJson(json, obj);
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		DataConverter.toJson(this, json);
		return json;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getHashtag() {
		return hashtag;
	}

	public void setHashtag(String hashtag) {
		this.hashtag = hashtag;
	}

	public String getSearchName() {
		return searchName;
	}

	public void setSearchName(String searchName) {
		this.searchName = searchName;
	}

	public String getCountries() {
		return countries;
	}

	public void setCountries(String countries) {
		this.countries = countries;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getScreenRatio() {
		return screenRatio;
	}

	public void setScreenRatio(Integer screenRatio) {
		this.screenRatio = screenRatio;
	}

	public String getImgSize() {
		return imgSize;
	}

	public void setImgSize(String imgSize) {
		this.imgSize = imgSize;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public Integer getDownCount() {
		return downCount;
	}

	public void setDownCount(Integer downCount) {
		this.downCount = downCount;
	}

	public String getCountByCountry() {
		return countByCountry;
	}

	public void setCountByCountry(String countByCountry) {
		this.countByCountry = countByCountry;
	}

	public Long getPlayCount() {
		return playCount;
	}

	public void setPlayCount(Long playCount) {
		this.playCount = playCount;
	}

	public Long getFavorite() {
		return favorite;
	}

	public void setFavorite(Long favorite) {
		this.favorite = favorite;
	}

	public Integer getLevel() {
		return level;
	}

	public void setLevel(Integer level) {
		this.level = level;
	}

	public String getDefaultCountry() {
		return defaultCountry;
	}

	public void setDefaultCountry(String defaultCountry) {
		this.defaultCountry = defaultCountry;
	}

	public String getIsApproved() {
		return isApproved;
	}

	public void setIsApproved(String isApproved) {
		this.isApproved = isApproved;
	}

	public Long getLastPlayDate() {
		return lastPlayDate;
	}

	public void setLastPlayDate(Long lastPlayDate) {
		this.lastPlayDate = lastPlayDate;
	}

	public Boolean getIsMin() {
		return isMin;
	}

	public void setIsMin(Boolean min) {
		isMin = min;
	}

	public Boolean getIndex() {
		return isIndex;
	}

	public void setIndex(Boolean index) {
		isIndex = index;
	}

	public String getHomeType() {
		return homeType;
	}

	public void setHomeType(String homeType) {
		this.homeType = homeType;
	}

	public Long getCreatedDate() {
		return createdDate;
	}

	public Data setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}

	public Integer getSupportIpad() {
		return supportIpad;
	}

	public void setSupportIpad(Integer supportIpad) {
		this.supportIpad = supportIpad;
	}
}
