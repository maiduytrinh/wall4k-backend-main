package com.tp.wallios.service.impl;

import javax.inject.Inject;

import com.tp.wallios.common.DataResult;
import com.tp.wallios.common.config.AppConfiguration;
import com.tp.wallios.entity.Category;
import com.tp.wallios.entity.Data;
import com.tp.wallios.entity.reference.CategoryType;
import com.tp.wallios.entity.reference.DataType;
import com.tp.wallios.model.ImageItem;
import com.tp.wallios.model.VideoItem;
import com.tp.wallios.model.common.CategoryItem;
import com.tp.wallios.model.common.DataItem;
import com.tp.wallios.service.CategoryService;
import com.tp.wallios.service.DataService;
import com.tp.wallios.service.ImageService;
import com.tp.wallios.service.VideoService;
import com.tp.wallios.service.filter.DataFilter;
import com.tp.wallios.service.handler.DataHandler;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataServiceImpl implements DataService {
	private static final Logger LOG = LoggerFactory.getLogger(DataServiceImpl.class);
	@Inject
	private ImageService imageService;
	@Inject
	private VideoService videoService;
	@Inject
	private CategoryService categoryService;
	private final Integer SIZE_FOR_YOU_HOMEPAGE = 10;
	private final Integer SIZE_CATEGORY_HOMEPAGE = 10;
	private final Integer SIZE_DEPTH_EFFECT_HOMEPAGE = 10;
	private final Integer SIZE_LIVE_WALL_HOMEPAGE = 8;
	private final Integer LAST_PAGE = -1;
	Vertx vertx = Vertx.vertx();

	@Override
	public DataResult<CategoryItem> getDataHomePage(DataFilter filter, String country) {
		List<CategoryItem> result = new ArrayList<>();
		try {
			filter.setFullPage(true);
			// get data for you
			filter.setForYou(true);
			filter.setPageNumber(filter.getFirstOpen() ? 0 : 1);
			filter.setSizeConfig(SIZE_FOR_YOU_HOMEPAGE);
			List<DataItem> dataForYou =  this.getData(filter).getResult();
			if (!dataForYou.isEmpty()) {
				result.add(new CategoryItem(new Category().setNameHomeScreen(CategoryType.FOR_YOU.name).toJson(), dataForYou, CategoryType.FOR_YOU.type));
			}

			// get data live
			filter.setSizeConfig(SIZE_LIVE_WALL_HOMEPAGE);
			List<DataItem> dataLive =  this.getDataLive(filter).getResult();
			if (!dataLive.isEmpty()) {
				result.add(new CategoryItem(new Category().setNameHomeScreen(CategoryType.LIVE.name).toJson(), dataLive, CategoryType.LIVE.type));
			}

			// get data depth effect
			if (filter.getDepthSupport()) {
				filter.setSizeConfig(SIZE_DEPTH_EFFECT_HOMEPAGE);
				List<DataItem> dataDepthEffect =  this.getDataDepthEffect(filter).getResult();
				if (!dataDepthEffect.isEmpty()) {
					result.add(new CategoryItem(new Category().setNameHomeScreen(CategoryType.DEPTH_EFFECT.name).toJson(), dataDepthEffect, CategoryType.DEPTH_EFFECT.type));
				}
			}

			// get data categories
			List<Category> listCategories = categoryService.getCategoriesByCountry(filter);
			filter.setSizeConfig(SIZE_CATEGORY_HOMEPAGE);
			int offsetDaily = AppConfiguration.getInteger(AppConfiguration.WALL_HOME_DAILY_TAB_OFFSET, "2");
			int i = 0;
			for (Category category : listCategories) {
				// sau 2 cate hien thi tab Daily
				if (i == offsetDaily) {
					if (!dataForYou.isEmpty()) {
						result.add(new CategoryItem(new Category().setNameHomeScreen(CategoryType.DAILY_PICK.name).toJson(), dataForYou, CategoryType.DAILY_PICK.type));
					}
				}
				filter.setCategories(category.getId().toString());
				List<DataItem> dataCate =  this.getDataByCategory(filter.setPageNumber(1)).getResult();
				if (!dataCate.isEmpty()) {
					category.processName(country);
					result.add(new CategoryItem(category.toJson(), dataCate, CategoryType.CATEGORY.type));
				}
				i++;
			}
		} catch (Exception ex) {
			LOG.error("getDataHomePage error: {}", ex.getMessage());
		}
		return new DataResult<>(result,null);
	}

	@Override
	public DataResult<DataItem> getData(DataFilter filter) {
		return !(filter.getPageNumber() == 0) ? getDataNormal(filter) : getDataSpecial(filter);
	}

	public DataResult<DataItem> getDataNormal (DataFilter filter) {
		Promise<DataResult<Data>> promiseDataImage = Promise.promise();
		int limit = filter.getSizeConfig();
		int page = filter.getPageNumber();
		int limitVideo = limit / (DataService.IMAGE_RATIO + DataService.VIDEO_RATIO);
		int limitImage = limit - limitVideo;
		int offsetImage = (page - 1) * limitImage;
		int offsetVideo = (page - 1) * limitVideo;
		Integer typeImage = filter.getDepthSupport() ? null : DataType.IMAGE.contentType;

		// get data image
		vertx.executeBlocking(promiseBlocking -> {
			List<Data> resultImage = imageService.getDataImage(filter.getLocale(), offsetImage, limitImage, filter.getFirstOpen(), typeImage, filter.getIpad());
			if (resultImage.isEmpty() || resultImage.size() < limitImage) {
				promiseBlocking.complete(new DataResult<>(resultImage, LAST_PAGE));
			} else {
				promiseBlocking.complete(new DataResult<>(resultImage, page + 1));
			}
		}, promiseDataImage);

		// get data video
		Promise<DataResult<Data>> promiseDataVideo = Promise.promise();
		vertx.executeBlocking(promiseBlocking -> {
			List<Data> resultVideo = videoService.getDataLive(filter.getLocale(), offsetVideo, limitVideo, filter.getForYou(), filter.getFirstOpen(), filter.getIpad());
			if (resultVideo.isEmpty() || resultVideo.size() < limitVideo) {
				promiseBlocking.complete(new DataResult<>(resultVideo, LAST_PAGE));
			} else {
				promiseBlocking.complete(new DataResult<>(resultVideo, page + 1));
			}
		}, promiseDataVideo);

		Future<DataResult<Data>> futureImage = promiseDataImage.future();
		Future<DataResult<Data>> futureVideo = promiseDataVideo.future();
		try {
			CompositeFuture compositeFuture = CompositeFuture.join(futureImage, futureVideo).toCompletionStage().toCompletableFuture().get();

			DataResult<Data> dataResultImage = compositeFuture.resultAt(0);
			DataResult<Data> dataResultVideo = compositeFuture.resultAt(1);

			List<Data> imageList = dataResultImage.getResult();
			List<Data> videoList = dataResultVideo.getResult();

			// add them cho du limit
			if (filter.getFullPage() != null && filter.getFullPage()) {
				int sizeImage = imageList.size();
				int sizeVideo = videoList.size();
				if (sizeImage < limitImage && sizeVideo == limitVideo) {
					videoList.addAll(videoService.getDataLive(filter.getLocale(), offsetVideo + sizeVideo, limit-(sizeVideo+sizeImage), false, false, filter.getIpad()));
				} else if (sizeVideo < limitVideo && sizeImage == limitImage) {
					imageList.addAll(imageService.getDataImage(filter.getLocale(), offsetImage + sizeImage, limit-(sizeVideo+sizeImage), filter.getFirstOpen(), typeImage, filter.getIpad()));
				}
			}

			List<DataItem> data = DataHandler.mergeData(imageList, videoList);

			return new DataResult<>(data, Math.max(dataResultImage.getNextPageNumber(), dataResultVideo.getNextPageNumber()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return  new DataResult<>(new ArrayList<>(), filter.getPageNumber());
	}

	public DataResult<DataItem> getDataSpecial (DataFilter filter) {
		Promise<DataResult<Data>> promiseDataImage = Promise.promise();
		int limit = filter.getSizeConfig();
		int page = filter.getPageNumber();
		String country = filter.getLocale();
		int limitVideo = limit / (DataService.IMAGE_RATIO + DataService.VIDEO_RATIO);
		int limitImage = limit - limitVideo;

		// get data image
		vertx.executeBlocking(promiseBlocking -> {
			List<Data> resultImage = imageService.getDataImageSpecial(country, limitImage, DataType.IMAGE.specialType, filter.getIpad());
			if (resultImage.isEmpty()) {
				promiseBlocking.complete(new DataResult<>(new ArrayList<>(), page));
			} else {
				promiseBlocking.complete(new DataResult<>(resultImage, page + 1));
			}
		}, promiseDataImage);

		// get data video
		String contentType = filter.getForYou() ? DataType.VIDEO.specialType :  DataType.LIVE.specialType;
		Promise<DataResult<Data>> promiseDataVideo = Promise.promise();
		vertx.executeBlocking(promiseBlocking -> {
			List<Data> resultVideo = videoService.getDataLiveSpecial(country, limitVideo, contentType, filter.getIpad());
			if (resultVideo.isEmpty()) {
				promiseBlocking.complete(new DataResult<>(new ArrayList<>(), page));
			} else {
				promiseBlocking.complete(new DataResult<>(resultVideo, page + 1));
			}
		}, promiseDataVideo);

		Future<DataResult<Data>> futureImage = promiseDataImage.future();
		Future<DataResult<Data>> futureVideo = promiseDataVideo.future();
		try {
			CompositeFuture compositeFuture = CompositeFuture.join(futureImage, futureVideo).toCompletionStage().toCompletableFuture().get();

			DataResult<Data> dataResultImage = compositeFuture.resultAt(0);
			DataResult<Data> dataResultVideo = compositeFuture.resultAt(1);

			List<Data> imageList = dataResultImage.getResult();
			List<Data> videoList = dataResultVideo.getResult();

			List<DataItem> data = new ArrayList<>();
			int nextPage = 0;
			if (videoList.isEmpty() && imageList.isEmpty()) {
				filter.setPageNumber(1);
				DataResult<DataItem> dataNormal = getDataNormal(filter);
				data = dataNormal.getResult();
				nextPage = dataNormal.getNextPageNumber();
			} else {
				data = DataHandler.mergeData(imageList, videoList);
				nextPage = dataResultImage.getNextPageNumber();
			}

			return new DataResult<>(data, nextPage);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return  new DataResult<>(new ArrayList<>(), filter.getPageNumber());
	}

	@Override
	public DataResult<DataItem> getDataLive(DataFilter filter) {
		return !(filter.getPageNumber() == 0) ? getDataLiveNormal(filter) : getDataLiveSpecial(filter);
	}

	public DataResult<DataItem> getDataLiveNormal (DataFilter filter) {
		int size = filter.getSizeConfig();
		int page = filter.getPageNumber();
		int offset = (page - 1) * size;
		List<Data> dataVideos = videoService.getDataLive(filter.getLocale(), offset, size, filter.getForYou(), filter.getFirstOpen(), filter.getIpad());
		List<DataItem> result = dataVideos.stream().map(VideoItem::new).collect(Collectors.toList());
		if (result.isEmpty() || result.size() < size) {
			return new DataResult<>(result, LAST_PAGE);
		} else {
			return new DataResult<>(result, page + 1);
		}
	}

	public DataResult<DataItem> getDataLiveSpecial (DataFilter filter) {
		List<Data> dataVideos = videoService.getDataLiveSpecial(filter.getLocale(), filter.getSizeConfig(), DataType.LIVE.specialType, filter.getIpad());
		int nextPage = 1;
		List<DataItem> result = new ArrayList<>();
		if (dataVideos.isEmpty()) {
			filter.setPageNumber(1);
			DataResult<DataItem> dataLiveNormal = getDataLiveNormal(filter);
			result = dataLiveNormal.getResult();
			nextPage = dataLiveNormal.getNextPageNumber();
		} else {
			result = dataVideos.stream().map(VideoItem::new).collect(Collectors.toList());
		}
		return new DataResult<>(result, nextPage);
	}

	@Override
	public DataResult<DataItem> getDataByCategory(DataFilter filter) {
		Promise<DataResult<Data>> promiseDataImage = Promise.promise();
		int limit = filter.getSizeConfig();
		int page = filter.getPageNumber();
		int limitVideo = limit / (DataService.IMAGE_RATIO + DataService.VIDEO_RATIO);
		int limitImage = limit - limitVideo;
		int offsetImage = (page - 1) * limitImage;
		int offsetVideo = (page - 1) * limitVideo;
		Integer typeImage = filter.getDepthSupport() ? null : DataType.IMAGE.contentType;

		// get data image by categories
		vertx.executeBlocking(promiseBlocking -> {
			List<Data> resultContextPro = imageService.getDataByCategories(filter.getCategories(), offsetImage, limitImage, typeImage, filter.getIpad()).result();
			if (resultContextPro.isEmpty() || resultContextPro.size() < limitImage) {
				promiseBlocking.complete(new DataResult<>(resultContextPro, LAST_PAGE));
			} else {
				promiseBlocking.complete(new DataResult<>(resultContextPro, page + 1));
			}
		}, promiseDataImage);

		// get data video by categories
		Promise<DataResult<Data>> promiseDataVideo = Promise.promise();
		vertx.executeBlocking(promiseBlocking -> {
			List<Data> resultContextVideo = videoService.getDataByCategories(filter.getCategories(), offsetVideo, limitVideo, null, filter.getIpad()).result();
			if (resultContextVideo.isEmpty() || resultContextVideo.size() < limitVideo) {
				promiseBlocking.complete(new DataResult<>(resultContextVideo, LAST_PAGE));
			} else {
				promiseBlocking.complete(new DataResult<>(resultContextVideo, page + 1));
			}
		}, promiseDataVideo);

		Future<DataResult<Data>> futureImage = promiseDataImage.future();
		Future<DataResult<Data>> futureVideo = promiseDataVideo.future();
		try {
			CompositeFuture compositeFuture = CompositeFuture.join(futureImage, futureVideo).toCompletionStage().toCompletableFuture().get();

			DataResult<Data> dataResultImage = compositeFuture.resultAt(0);
			DataResult<Data> dataResultVideo = compositeFuture.resultAt(1);

			List<Data> imageList = dataResultImage.getResult();
			List<Data> videoList = dataResultVideo.getResult();

			// add them cho du limit
			if (filter.getFullPage() != null && filter.getFullPage()) {
				int sizeImage = imageList.size();
				int sizeVideo = videoList.size();
				if (sizeImage < limitImage && sizeVideo == limitVideo) {
					videoList.addAll(videoService.getDataByCategories(filter.getCategories(), offsetVideo + sizeVideo, limit-(sizeVideo+sizeImage), null, filter.getIpad()).result());
				} else if (sizeVideo < limitVideo && sizeImage == limitImage) {
					imageList.addAll(imageService.getDataByCategories(filter.getCategories(), offsetImage + sizeImage, limit-(sizeVideo+sizeImage), typeImage, filter.getIpad()).result());
				}
			}

			List<DataItem> data = DataHandler.mergeData(imageList, videoList);

			return new DataResult<>(data, Math.max(dataResultImage.getNextPageNumber(), dataResultVideo.getNextPageNumber()));
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		return  new DataResult<>(new ArrayList<>(), filter.getPageNumber());
	}

	@Override
	public DataResult<DataItem> getTopTrending(DataFilter filter) {
		List<Data> imageTrending = imageService.getDataTrending(filter);
		List<DataItem> result = imageTrending.stream().map(ImageItem::new).collect(Collectors.toList());
		if (result.isEmpty() || result.size() < filter.getSizeConfig()) {
            return new DataResult<>(result, LAST_PAGE);
        } else {
            return new DataResult<>(result, filter.getPageNumber() + 1);
        }
	}

	@Override
	public DataResult<DataItem> getTopDown(DataFilter filter) {
		List<Data> imageTrending = imageService.getDataTopDown(filter);
		List<DataItem> result = imageTrending.stream().map(ImageItem::new).collect(Collectors.toList());
		if (result.isEmpty() || result.size() < filter.getSizeConfig()) {
			return new DataResult<>(result, LAST_PAGE);
		} else {
			return new DataResult<>(result, filter.getPageNumber() + 1);
		}
	}

	@Override
	public DataResult<DataItem> getDataDepthEffect(DataFilter filter) {
		return !(filter.getPageNumber() == 0) ? getDataDepthEffectNormal(filter) : getDataDepthEffectSpecial(filter);
	}

	public DataResult<DataItem> getDataDepthEffectNormal (DataFilter filter) {
		int size = filter.getSizeConfig();
		int page = filter.getPageNumber();
		int offset = (page - 1) * size;
		List<Data> dataDepthEffects = imageService.getDataImage(filter.getLocale(), offset, size, filter.getFirstOpen(), DataType.DEPTH_EFFECT.contentType, filter.getIpad());
		List<DataItem> result = dataDepthEffects.stream().map(ImageItem::new).collect(Collectors.toList());
		if (result.isEmpty() || result.size() < size) {
			return new DataResult<>(result, LAST_PAGE);
		} else {
			return new DataResult<>(result, page + 1);
		}
	}

	public DataResult<DataItem> getDataDepthEffectSpecial (DataFilter filter) {
		List<Data> dataDepthEffects = imageService.getDataImageSpecial(filter.getLocale(), filter.getSizeConfig(), DataType.DEPTH_EFFECT.specialType, filter.getIpad());
		int nextPage = 1;
		List<DataItem> result = new ArrayList<>();
		if (dataDepthEffects.isEmpty()) {
			filter.setPageNumber(1);
			DataResult<DataItem> dataDepthEffectNormal = getDataDepthEffectNormal(filter);
			result = dataDepthEffectNormal.getResult();
			nextPage = dataDepthEffectNormal.getNextPageNumber();
		} else {
			result = dataDepthEffects.stream().map(ImageItem::new).collect(Collectors.toList());
		}
		return new DataResult<>(result, nextPage);
	}
}
