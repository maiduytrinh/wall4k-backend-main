package com.tp.wallios.rest.api;

import com.google.inject.Inject;
import com.tp.wallios.model.common.DataItem;
import com.tp.wallios.rest.common.Response;
import com.tp.wallios.rest.writers.MyResponseWriter;
import com.tp.wallios.service.SearchService;
import com.tp.wallios.service.filter.DataFilter;
import com.tp.wallios.utils.AppUtils;
import com.tp.wallios.utils.CountryUtils;
import com.zandero.rest.annotation.ResponseWriter;
import io.swagger.annotations.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.util.List;

import static com.tp.wallios.ServerVerticle.API_ROOT;


@Api(value = API_ROOT, tags = "phase 1 - search")
@Path(API_ROOT + "/search")
@Produces(MediaType.APPLICATION_JSON)
@ResponseWriter(MyResponseWriter.class)
public class SearchRest extends BaseRest {
    private static final Logger LOG = LoggerFactory.getLogger(SearchRest.class);
    private final String DEFAULT_SIZE_CONFIG = "40";
    @Inject
    private SearchService searchService;


    @GET
    @Path("/hashtag")
    @ApiOperation(value = "API-0101 | Return list of data", notes = "pageNumber default value is 1 ", response = DataItem.class, responseContainer = "List")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Return Data list is OK."), @ApiResponse(code = 400, message = "Failed to return the data list.") })
    public Response<JsonObject> getData(
            @ApiParam(value = "The language value", defaultValue = "en", required = true) @HeaderParam("language") String language,
            @ApiParam(value = "The country value", defaultValue = "US", required = true) @HeaderParam("country") String country,
            @ApiParam(value = "The mobileid value", defaultValue = "167e5a47e70ee6c3_sdk29_tg28", required = true) @HeaderParam("mobileid") String mobileId,
            @ApiParam(value = "Page number value", defaultValue = "0", required = true) @QueryParam("pagenumber") Integer pageNumber,
            @ApiParam(value = "Size config value", defaultValue = DEFAULT_SIZE_CONFIG) @QueryParam("sizeconfig") @DefaultValue(DEFAULT_SIZE_CONFIG) Integer sizeConfig,
            @ApiParam(value = "The hashtags value", defaultValue = "" ,required = true) @QueryParam("hashtags") String hashtags,
            @Context RoutingContext context) {
        LOG.info("API-0301::SearchByHashtag()-----------------------------------------------------------------------------");
        LOG.info("country = {}, pageNumber = {}, size = {}, hashtag = {}", country, pageNumber, sizeConfig, hashtags);
        handle(context);

        final String newCountry = CountryUtils.getDBCountry(country);

        DataFilter filter = new DataFilter(AppUtils.getLocale(newCountry, language));
        filter.setSizeConfig(sizeConfig);
        filter.setPageNumber(pageNumber);
        filter.setHashtags(hashtags);

        if (pageNumber==null || pageNumber < 0) {
            return Response.fail("Page number must be greater than 0.");
        }

        if (hashtags==null || hashtags.isEmpty()) {
            return Response.fail("Hashtags cannot be empty.");
        }

        List<DataItem> searchResult = searchService.searchByHashtag(filter, newCountry);

        if (searchResult == null || searchResult.isEmpty()) {
            searchResult = searchService.searchByKeyword(filter);
        }

        // do ko co video len dungf limit cua moi image
        int limitImage = (int) (sizeConfig * 0.75);
        int nextPageNumber = searchResult == null || searchResult.size() < limitImage ? -1 : pageNumber + 1;

        final JsonObject resultJson = new JsonObject();
        resultJson.put("wallpapers", searchResult);
        resultJson.put("nextPageNumber", nextPageNumber);
        resultJson.put("currentPageNumber", pageNumber);

        return Response.ok(resultJson);
    }
}
