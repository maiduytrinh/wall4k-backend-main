package com.tp.wallios.rest.api;

import com.tp.wallios.ServerVerticle;
import com.tp.wallios.entity.Data;
import com.tp.wallios.entity.SpecialData;
import com.tp.wallios.index.AbstractIndex;
import com.tp.wallios.index.service.ImageIndexService;
import com.tp.wallios.index.service.SpecialDataIndexService;
import com.tp.wallios.index.service.VideoIndexService;
import com.tp.wallios.rest.common.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import io.vertx.core.json.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;


@Api(value = ServerVerticle.API_ROOT, tags = "phase 1 - indexing")
@Path(ServerVerticle.API_ROOT)
@Produces(MediaType.APPLICATION_JSON)
public class IndexRest extends BaseRest{
    private static final Logger LOG = LoggerFactory.getLogger(IndexRest.class);

    private final AbstractIndex<Data, Data> imageIndex = new ImageIndexService();
    private final AbstractIndex<Data, Data> videoIndex = new VideoIndexService();
    private final AbstractIndex<SpecialData, SpecialData> specialDataIndex = new SpecialDataIndexService();

    @ApiOperation(value = "API-0301 | Rebuild indexing for data image.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Number of deleted and inserted document is OK."), @ApiResponse(code = 400, message = "Failed to delete and insert document is OK.")  })
    @GET
    @Path("/reindex/images")
    public Response<JsonObject> rebuildDataImages() {
        LOG.info("API-0301::rebuildDataImage()-----------------------------------------------------------------------------");
        JsonObject result = imageIndex.rebuildIndexes();
        return Response.ok(result);
    }

    @ApiOperation(value = "API-0302 | Rebuild indexing for data video.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Number of deleted and inserted document is OK."), @ApiResponse(code = 400, message = "Failed to delete and insert document is OK.")  })
    @GET
    @Path("/reindex/videos")
    public Response<JsonObject> rebuildDataVideos() {
        LOG.info("API-0302::rebuildDataVideo()-----------------------------------------------------------------------------");
        JsonObject result = videoIndex.rebuildIndexes();
        return Response.ok(result);
    }

    @ApiOperation(value = "API-0303 | Rebuild indexing for data special.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Number of deleted and inserted document is OK."), @ApiResponse(code = 400, message = "Failed to delete and insert document is OK.")  })
    @GET
    @Path("/reindex/specialData")
    public Response<JsonObject> rebuildDataSpecial() {
        LOG.info("API-0302::rebuildDataSpecial()-----------------------------------------------------------------------------");
        JsonObject result = specialDataIndex.rebuildIndexes();
        return Response.ok(result);
    }
}
