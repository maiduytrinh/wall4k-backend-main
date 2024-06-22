package com.tp.wallios.service.impl;

import com.tp.wallios.entity.Category;
import com.tp.wallios.service.CategoryService;
import com.tp.wallios.service.filter.DataFilter;
import com.tp.wallios.storage.CategoryStorage;

import javax.inject.Inject;
import java.util.List;

public class CategoryServiceImpl implements CategoryService {

	@Inject
	private CategoryStorage categoryStorage;

	@Override
	public List<Category> getCategoriesByCountry(DataFilter filter) {
		return categoryStorage.getCategoriesByCountry(filter);
	}
}
