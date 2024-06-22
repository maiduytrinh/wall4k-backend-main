package com.tp.wallios.storage;

import com.tp.wallios.entity.Category;
import com.tp.wallios.service.filter.DataFilter;

import java.util.List;

public interface CategoryStorage {
    List<Category> getCategoriesByCountry (DataFilter filter);
}
