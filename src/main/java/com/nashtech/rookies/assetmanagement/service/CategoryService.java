package com.nashtech.rookies.assetmanagement.service;

import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CategoryRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface CategoryService extends UserDetailsService {
    ResponseDto<CategoryResponse> saveCategory(CategoryRequest request, UserDetailsDto requestUser);
    ResponseDto<List<CategoryResponse>> getAllCategory(UserDetailsDto requestUser);
}
