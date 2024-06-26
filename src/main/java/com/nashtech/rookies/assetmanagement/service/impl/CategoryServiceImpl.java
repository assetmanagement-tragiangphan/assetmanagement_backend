package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CategoryRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.mapper.CategoryMapper;
import com.nashtech.rookies.assetmanagement.repository.CategoryRepository;
import com.nashtech.rookies.assetmanagement.service.CategoryService;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public ResponseDto<CategoryResponse> saveCategory(CategoryRequest request) {
        if (categoryRepository.existsByPrefix(request.getPrefix())) {
            throw new ResourceAlreadyExistException("Prefix is already existed. Please enter a different prefix!");
        } else if (categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistException("Category is already existed. Please enter a different category!");
        } else {
            Category category = new Category();
            category.setPrefix(request.getPrefix());
            category.setName(request.getName());
            category.setStatus(StatusConstant.ACTIVE);
            category = categoryRepository.saveAndFlush(category);
            return ResponseDto.<CategoryResponse>builder()
                    .data(categoryMapper.entityToDto(category))
                    .message("Create Category successfully.")
                    .build();
        }
    }

    @Override
    public ResponseDto<List<CategoryResponse>> getAllCategory() {
        Sort sort = Sort.by(Sort.Direction.ASC, "auditMetadata.createdOn");
        Pageable pageable = PageRequest.of(0, categoryRepository.findAll().size(), sort);
        Page<Category> categories = categoryRepository.findAll(pageable);
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        categories.forEach(category -> categoryResponses.add(categoryMapper.entityToDto(category)));
        return ResponseDto.<List<CategoryResponse>>builder()
                .data(categoryResponses)
                .build();
    }
}
