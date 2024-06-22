package com.tp.wallios.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tp.wallios.utils.AppUtils;
import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;
import org.apache.commons.lang3.StringUtils;

@DataObject(generateConverter = true)
public class Category {

	private Integer id;
	private String name, types, owner, countries, isPublic, url;
	@JsonIgnore
	private String displayByLang;
	@JsonIgnore
	private String nameHomeScreen;
	private Integer count, order;
	private Long createdDate;

	public Category() {
	}

	public static Category valueOf(String json) {
		return AppUtils.fromJson(json, Category.class);
	}

	public static void merge(JsonObject json, Category obj) {
		CategoryConverter.fromJson(json, obj);
	}

	public JsonObject toJson() {
		JsonObject json = new JsonObject();
		CategoryConverter.toJson(this, json);
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

	public Category setName(String name) {
		this.name = name;
		return this;
	}

	public String getDisplayByLang() {
		return displayByLang;
	}

	public void setDisplayByLang(String displayByLang) {
		this.displayByLang = displayByLang;
	}

	public String getTypes() {
		return types;
	}

	public void setTypes(String types) {
		this.types = types;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCountries() {
		return countries;
	}

	public void setCountries(String countries) {
		this.countries = countries;
	}

	public String getIsPublic() {
		return isPublic;
	}

	public void setIsPublic(String isPublic) {
		this.isPublic = isPublic;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public Integer getOrder() {
		return order;
	}

	public void setOrder(Integer order) {
		this.order = order;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

    public String getNameHomeScreen() {
        return nameHomeScreen;
    }

    public Category setNameHomeScreen(String nameHomeScreen) {
        this.nameHomeScreen = nameHomeScreen;
		return this;
    }

    public Long getCreatedDate() {
		return createdDate;
	}

	public Category setCreatedDate(Long createdDate) {
		this.createdDate = createdDate;
		return this;
	}

	public Category processName(String country) {
		if (!StringUtils.isEmpty(displayByLang)) {
			String[] names = displayByLang.split(";");
			String usName = this.nameHomeScreen;
			for (String myName : names) {
				if (myName.startsWith(country)) {
					this.nameHomeScreen = myName.replace(country + ":", "");
					break;
				}
				if (myName.startsWith("US")) {
					usName = myName.replace("US:", "");
				}
			}
			if (this.nameHomeScreen == null || this.nameHomeScreen.isEmpty()) {
				this.nameHomeScreen = usName;
			}
		}
		return this;
	}
}
