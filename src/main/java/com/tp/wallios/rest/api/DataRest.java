package com.tp.wallios.rest.api;

import com.mysql.cj.util.StringUtils;
import com.tp.wallios.ServerVerticle;
import com.tp.wallios.common.DataResult;
import com.tp.wallios.model.common.CategoryItem;
import com.tp.wallios.model.common.DataItem;
import com.tp.wallios.rest.common.Response;
import com.tp.wallios.rest.writers.MyResponseWriter;
import com.tp.wallios.service.DataService;
import com.tp.wallios.service.filter.DataFilter;
import com.tp.wallios.utils.AppUtils;
import com.tp.wallios.utils.CountryUtils;
import com.zandero.rest.annotation.ResponseWriter;
import io.swagger.annotations.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Api(value = ServerVerticle.API_ROOT, tags = "phase 1 - data")
@Path(ServerVerticle.API_ROOT + "/data")
@Produces(MediaType.APPLICATION_JSON)
@ResponseWriter(MyResponseWriter.class)
public class DataRest extends BaseRest {

	private static final Logger LOG = LoggerFactory.getLogger(DataRest.class);
	private final String DEFAULT_SIZE_CONFIG = "40";
	@Inject
	private DataService dataService;

	@GET
	@Path("/")
	@ApiOperation(value = "API-0101 | Return list of data", notes = "pageNumber default value is 1 ", response = DataItem.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Return Data list is OK."), @ApiResponse(code = 400, message = "Failed to return the data list.") })
	public Response<JsonObject> getData(
			@ApiParam(value = "The language value", defaultValue = "en", required = true) @HeaderParam("language") String language,
			@ApiParam(value = "The country value", defaultValue = "US", required = true) @HeaderParam("country") String country,
			@ApiParam(value = "The mobileid value", defaultValue = "167e5a47e70ee6c3_sdk29_tg28", required = true) @HeaderParam("mobileid") String mobileId,
			@ApiParam(value = "The token value", defaultValue = "testtoken", required = true) @HeaderParam("token") String token,
			@ApiParam(value = "The appid value", defaultValue = "testappid", required = true) @QueryParam("appid") String appId,
			@ApiParam(value = "Page number value", defaultValue = "0", required = true) @QueryParam("pagenumber") Integer pageNumber,
			@ApiParam(value = "Size config value", defaultValue = DEFAULT_SIZE_CONFIG) @QueryParam("sizeconfig") @DefaultValue(DEFAULT_SIZE_CONFIG) Integer sizeConfig,
			@ApiParam(value = "First open true/false ", defaultValue = "false") @QueryParam("firstopen") @DefaultValue("false") Boolean isFirst,
			@ApiParam(value = "Support depth effect true/false ", defaultValue = "false") @QueryParam("depthsupport") @DefaultValue("false") Boolean depthSupport,
			@ApiParam(value = "Support ipad true/false ", defaultValue = "false") @QueryParam("isipad") @DefaultValue("false") Boolean isIpad,
			@Context RoutingContext context) {
		LOG.info("API-0101::getDataForYou()-----------------------------------------------------------------------------");
		LOG.info("country = {}, pageNumber = {}, size = {}, depthSupport = {}, isIpad = {}", country, pageNumber, sizeConfig, depthSupport, isIpad);
		handle(context);

		final String newCountry = CountryUtils.getDBCountry(country);

		DataFilter filter = new DataFilter(AppUtils.getLocale(newCountry, language));
		filter.setSizeConfig(sizeConfig);
		filter.setPageNumber(pageNumber);
		filter.setForYou(true);
		filter.setFirstOpen(isFirst);
		filter.setDepthSupport(depthSupport);
		filter.setIpad(isIpad);

		if (pageNumber==null || pageNumber < 0) {
			return Response.fail("Page number must be greater than 0.");
		}

		DataResult<DataItem> resultContext = dataService.getData(filter);

		final JsonObject resultJson = new JsonObject();
		resultJson.put("wallpapers", resultContext.getResult());
		resultJson.put("nextPageNumber", resultContext.getNextPageNumber());
		resultJson.put("currentPageNumber", pageNumber);

		return Response.ok(resultJson);
	}

	@ApiOperation(value = "API-0103 | Return list of data by categories", notes = "pageNumber default value is 1 ", response = DataItem.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Return Data list is OK."), @ApiResponse(code = 400, message = "Failed to return the data list.") })
	@GET
	@Path("/category")
	public Response<JsonObject> getDateByCategory(
			@ApiParam(value = "The language value", defaultValue = "en", required = true) @HeaderParam("language") String language,
			@ApiParam(value = "The country value", defaultValue = "US", required = true) @HeaderParam("country") String country,
			@ApiParam(value = "The mobileid value", defaultValue = "167e5a47e70ee6c3_sdk29_tg28", required = true) @HeaderParam("mobileid") String mobileId,
			@ApiParam(value = "The token value", defaultValue = "testtoken", required = true) @HeaderParam("token") String token,
			@ApiParam(value = "The appid value", defaultValue = "testappid", required = true) @QueryParam("appid") String appId,
			@ApiParam(value = "List Id categories", defaultValue = "", required = true) @QueryParam("category") @DefaultValue("0") String category,
			@ApiParam(value = "Page number value", defaultValue = "1", required = true) @QueryParam("pagenumber") @DefaultValue("1") Integer pageNumber,
			@ApiParam(value = "Size config value", defaultValue = DEFAULT_SIZE_CONFIG) @QueryParam("sizeconfig") @DefaultValue(DEFAULT_SIZE_CONFIG) Integer sizeConfig,
			@ApiParam(value = "Support depth effect true/false ", defaultValue = "false") @QueryParam("depthsupport") @DefaultValue("false") Boolean depthSupport,
			@ApiParam(value = "Support ipad true/false ", defaultValue = "false") @QueryParam("isipad") @DefaultValue("false") Boolean isIpad,
			@QueryParam("type") @DefaultValue("wallpaper") String type,
			@Context RoutingContext context) {
		LOG.info("API-0103::getDataByCategories()-----------------------------------------------------------------------------");
		LOG.info("categories = {}, pageNumber = {}, size = {}, depthSupport = {}, isIpad = {}, type = {}", category, pageNumber, sizeConfig, depthSupport, isIpad, type);
		handle(context);

		final JsonObject resultJson = new JsonObject();

		final String newCountry = CountryUtils.getDBCountry(country);

		DataFilter filter = new DataFilter(AppUtils.getLocale(newCountry, language));
		filter.setCategories(category);
		filter.setPageNumber(pageNumber);
		filter.setSizeConfig(sizeConfig);
		filter.setDepthSupport(depthSupport);
		filter.setIpad(isIpad);

		DataResult<DataItem> resultContext;

		if (type.equalsIgnoreCase("toptrend")) {
			resultContext = dataService.getTopTrending(filter);
		} else if (type.equalsIgnoreCase("topdown")) {
			resultContext = dataService.getTopDown(filter);
		} else {
			resultContext = dataService.getDataByCategory(filter);
		}

		resultJson.put("wallpapers", resultContext.getResult());
		resultJson.put("nextPageNumber", resultContext.getNextPageNumber());
		resultJson.put("currentPageNumber", pageNumber);

		return Response.ok(resultJson);
	}

	@ApiOperation(value = "API-0103 | Return list of data by categories", notes = "pageNumber default value is 1 ", response = DataItem.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Return Data list is OK."), @ApiResponse(code = 400, message = "Failed to return the data list.") })
	@GET
	@Path("/topWall")
	public Response<JsonObject> getDataTop(
			@ApiParam(value = "The language value", defaultValue = "en", required = true) @HeaderParam("language") String language,
			@ApiParam(value = "The country value", defaultValue = "US", required = true) @HeaderParam("country") String country,
			@ApiParam(value = "The mobileid value", defaultValue = "167e5a47e70ee6c3_sdk29_tg28", required = true) @HeaderParam("mobileid") String mobileId,
			@ApiParam(value = "The token value", defaultValue = "testtoken", required = true) @HeaderParam("token") String token,
			@ApiParam(value = "The appid value", defaultValue = "testappid", required = true) @QueryParam("appid") String appId,
			@ApiParam(value = "List Id categories", defaultValue = "", required = true) @QueryParam("type") @DefaultValue("down") String category,
			@ApiParam(value = "Page number value", defaultValue = "1", required = true) @QueryParam("pagenumber") @DefaultValue("1") Integer pageNumber,
			@ApiParam(value = "Size config value", defaultValue = DEFAULT_SIZE_CONFIG) @QueryParam("sizeconfig") @DefaultValue(DEFAULT_SIZE_CONFIG) Integer sizeConfig,
			@QueryParam("type") @DefaultValue("wallpaper") String type,
			@Context RoutingContext context) {
		LOG.info("API-0103::getDataByCategories()-----------------------------------------------------------------------------");
		handle(context);

		final JsonObject resultJson = new JsonObject();

		final String newCountry = CountryUtils.getDBCountry(country);

		DataFilter filter = new DataFilter(AppUtils.getLocale(newCountry, language));
		filter.setCategories(category);
		filter.setPageNumber(pageNumber);
		filter.setSizeConfig(sizeConfig);

		DataResult<DataItem> resultContext;

		if (type.equalsIgnoreCase("toptrend")) {
			resultContext = dataService.getTopTrending(filter);
		} else if (type.equalsIgnoreCase("topdown")) {
			resultContext = dataService.getTopDown(filter);
		} else {
			resultContext = dataService.getDataByCategory(filter);
		}

		resultJson.put("wallpapers", resultContext.getResult());
		resultJson.put("nextPageNumber", resultContext.getNextPageNumber());
		resultJson.put("currentPageNumber", pageNumber);

		return Response.ok(resultJson);
	}
}
