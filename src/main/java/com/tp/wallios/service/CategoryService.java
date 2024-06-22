package com.tp.wallios.service;

import com.tp.wallios.common.ResultContext;
import com.tp.wallios.entity.Category;
import com.tp.wallios.service.filter.DataFilter;

import java.util.List;

public interface CategoryService {
    List<Category> getCategoriesByCountry(DataFilter filter);
}
