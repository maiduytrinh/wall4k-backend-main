package com.tp.wallios.service.filter;

import javax.swing.text.StyledEditorKit;
import java.util.Locale;

public class DataFilter extends SimpleFilter {

	private String categories;
	private Integer pageNumber;
	private Integer sizeConfig;
	private Boolean fullPage = false;
	private Boolean isForYou = false;
	private Boolean firstOpen;
	private Boolean depthSupport;
	private Boolean isIpad;
	private String hashtags;

	public DataFilter() {
    }

	public DataFilter(Locale locale) {
		super(locale);
	}

	public DataFilter(Locale locale, String categories, Integer pageNumber, Integer sizeConfig) {
		super(locale);
		this.categories = categories;
		this.pageNumber = pageNumber;
		this.sizeConfig = sizeConfig;
	}

	public DataFilter(Locale locale, String categories, Integer pageNumber, Integer sizeConfig, Boolean isForYou) {
		super(locale);
		this.categories = categories;
		this.pageNumber = pageNumber;
		this.sizeConfig = sizeConfig;
		this.isForYou = isForYou;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public Integer getPageNumber() {
		return pageNumber;
	}

	public DataFilter setPageNumber(Integer pageNumber) {
		this.pageNumber = pageNumber;
		return this;
	}

	public Integer getSizeConfig() {
		return sizeConfig;
	}

	public void setSizeConfig(Integer sizeConfig) {
		this.sizeConfig = sizeConfig;
	}

	public Boolean getFullPage() {
		return fullPage;
	}

	public void setFullPage(Boolean fullPage) {
		this.fullPage = fullPage;
	}

	public Boolean getForYou() {
		return isForYou;
	}

	public void setForYou(Boolean forYou) {
		isForYou = forYou;
	}

	public Boolean getFirstOpen() {
		return firstOpen;
	}

	public void setFirstOpen(Boolean firstOpen) {
		this.firstOpen = firstOpen;
	}

	public Boolean getDepthSupport() {
		return depthSupport;
	}

	public void setDepthSupport(Boolean depthSupport) {
		this.depthSupport = depthSupport;
	}

	public Boolean getIpad() {
		return isIpad;
	}

	public void setIpad(Boolean ipad) {
		isIpad = ipad;
	}

	public String getHashtags() {
		return hashtags;
	}

	public void setHashtags(String hashtags) {
		this.hashtags = hashtags;
	}
}
