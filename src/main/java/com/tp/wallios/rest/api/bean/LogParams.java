package com.tp.wallios.rest.api.bean;

import javax.ws.rs.HeaderParam;

public class LogParams {
	@HeaderParam("X-ViewId")
	private String viewId = null;
	
	@HeaderParam("X-DeviceId")
	private String deviceId;
	
	@HeaderParam("X-AppVersion")
	private String appVersionId;
	
	@HeaderParam("X-AppId")
	private String appId;

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getAppVersionId() {
		return appVersionId;
	}

	public void setAppVersionId(String appVersionId) {
		this.appVersionId = appVersionId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getViewId() {
		return viewId;
	}

	public void setViewId(String viewId) {
		this.viewId = viewId;
	}
	
	
	

}
