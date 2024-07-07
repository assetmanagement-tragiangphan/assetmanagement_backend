package com.nashtech.rookies.assetmanagement.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.nashtech.rookies.assetmanagement.dto.request.Asset.CategoryRequest;
import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.mapper.CategoryMapper;
import com.nashtech.rookies.assetmanagement.repository.CategoryRepository;
import com.nashtech.rookies.assetmanagement.service.CategoryService;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private String generatePrefixCategory(String categoryName){
        String prefix = List.of(categoryName.split(" ")).stream().reduce("",(acc, item) -> acc + item.charAt(0)).toUpperCase();
        Integer count = categoryRepository.countByPrefixStartsWith(prefix);
        prefix = count == 0 ? prefix : prefix + count;
        return prefix;
    }

    @Override
    public ResponseDto<CategoryResponse> saveCategory(CategoryRequest request) {
        if (categoryRepository.existsByName(request.getName())) {
            throw new ResourceAlreadyExistException("Category is already existed. Please enter a different category");
        } 

        Category category = new Category();
        category.setPrefix(this.generatePrefixCategory(request.getName()));
        category.setName(request.getName());
        category.setStatus(StatusConstant.ACTIVE);
        category = categoryRepository.saveAndFlush(category);
        return ResponseDto.<CategoryResponse>builder()
                .data(categoryMapper.entityToDto(category))
                .message("Create Category successfully.")
                .build();
    }

    @Override
    public ResponseDto<List<CategoryResponse>> getAllCategory() {
        Sort sort = Sort.by(Sort.Direction.ASC, "auditMetadata.createdOn");
        List<Category> categories = categoryRepository.findAll(sort);
        List<CategoryResponse> categoryResponses = new ArrayList<>();
        categories.forEach(category -> categoryResponses.add(categoryMapper.entityToDto(category)));
        return ResponseDto.<List<CategoryResponse>>builder()
                .data(categoryResponses)
                .build();
    }
}
