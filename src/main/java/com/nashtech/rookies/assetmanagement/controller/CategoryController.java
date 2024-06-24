package com.nashtech.rookies.assetmanagement.controller;

import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CategoryRequest;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping()
    public ResponseEntity<ResponseDto<CategoryResponse>> createCategory(@RequestBody CategoryRequest request, Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto)authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.saveCategory(request, requestUser));
    }

    @GetMapping()
    public ResponseEntity<ResponseDto<List<CategoryResponse>>> getCategories(Authentication authentication) {
        UserDetailsDto requestUser = (UserDetailsDto)authentication.getPrincipal();
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAllCategory(requestUser));
    }
}
