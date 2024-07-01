package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CategoryRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;

import java.util.List;

public interface CategoryService {
    ResponseDto<CategoryResponse> saveCategory(CategoryRequest request);
    ResponseDto<List<CategoryResponse>> getAllCategory();
}
