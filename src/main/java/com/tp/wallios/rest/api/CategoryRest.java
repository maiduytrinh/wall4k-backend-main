package com.tp.wallios.rest.api;

import com.tp.wallios.ServerVerticle;
import com.tp.wallios.entity.Category;
import com.tp.wallios.model.common.DataItem;
import com.tp.wallios.rest.common.Response;
import com.tp.wallios.rest.writers.MyResponseWriter;
import com.tp.wallios.service.CategoryService;
import com.tp.wallios.service.filter.DataFilter;
import com.tp.wallios.utils.AppUtils;
import com.tp.wallios.utils.CountryUtils;
import com.zandero.rest.annotation.ResponseWriter;
import io.swagger.annotations.*;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

@Api(value = ServerVerticle.API_ROOT, tags = "phase 1 - category")
@Path(ServerVerticle.API_ROOT + "/category")
@Produces(MediaType.APPLICATION_JSON)
@ResponseWriter(MyResponseWriter.class)
public class CategoryRest extends BaseRest {

	private static final Logger LOG = LoggerFactory.getLogger(CategoryRest.class);

	@Inject
	private CategoryService categoryService;

	@ApiOperation(value = "API-0201 | Return list of category", notes = "pageNumber default value is 1 ", response = DataItem.class, responseContainer = "List")
	@ApiResponses(value = { @ApiResponse(code = 200, message = "Return Data list is OK."), @ApiResponse(code = 400, message = "Failed to return the data list.") })
	@GET
	@Path("/")
	public Response<List<Category>> getCategoriesByCountry(
			@ApiParam(value = "The language value", defaultValue = "vi", required = true) @HeaderParam("language") String language,
			@ApiParam(value = "The country value", defaultValue = "VN", required = true) @HeaderParam("country") String country,
			@ApiParam(value = "The mobileid value", defaultValue = "167e5a47e70ee6c3_sdk29_tg28", required = true) @HeaderParam("mobileid") String mobileId,
			@ApiParam(value = "The token value", defaultValue = "testtoken", required = true) @HeaderParam("token") String token,
			@ApiParam(value = "The appid value", defaultValue = "testappid", required = true) @QueryParam("appid") String appId,
			@Context RoutingContext context) {
		LOG.info("API-0201::getCategories()-----------------------------------------------------------------------------");
		LOG.info("country = {}", country);
		handle(context);

		final String newCountry = CountryUtils.getDBCountry(country);

		DataFilter filter = new DataFilter(AppUtils.getLocale(newCountry, language));

		List<Category> resultContext = categoryService.getCategoriesByCountry(filter);

		return Response.ok(resultContext);
	}

}
