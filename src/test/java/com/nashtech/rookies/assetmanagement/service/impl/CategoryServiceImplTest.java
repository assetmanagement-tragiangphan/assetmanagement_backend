package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Asset.CategoryRequest;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.mapper.CategoryMapper;
import com.nashtech.rookies.assetmanagement.mapper.UserMapper;
import com.nashtech.rookies.assetmanagement.repository.CategoryRepository;
import com.nashtech.rookies.assetmanagement.repository.UserRepository;
import com.nashtech.rookies.assetmanagement.service.CategoryService;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.RoleConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {
    @Mock
    private CategoryService categoryService;
    @Mock
    private CategoryRepository categoryRepository;
    @Mock
    private CategoryMapper categoryMapper;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    private CategoryRequest categoryRequest;
    private CategoryResponse categoryResponse;
    private UserDetailsDto userDetailsDto;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        categoryService = new CategoryServiceImpl(categoryRepository, categoryMapper, userRepository, userMapper);
        Category category1 = Category.builder()
                .id(1)
                .prefix("LA")
                .name("Laptop")
                .status(StatusConstant.ACTIVE)
                .build();
        Category category2 = Category.builder()
                .id(2)
                .prefix("MO")
                .name("Monitor")
                .status(StatusConstant.ACTIVE)
                .build();
        categories = List.of(category1, category2);
        userDetailsDto = UserDetailsDto.builder()
                .id(1)
                .username("username")
                .location(LocationConstant.HCM)
                .roleName(RoleConstant.ADMIN)
                .build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void testSaveCategory_WhenCategoryExists_ThenThrowResourceAlreadyExistException() {

    }

    @Test
    void testSaveCategory_WhenCategoryDoesNotExists_ThenReturnNewCategory() {

    }

    @Test
    void testGetAllCategory_WhenNotExistAnyCategory_ThenThrowResourceNotFoundException() {
    }

    @Test
    void testGetAllCategory_WhenExistCategories_ThenReturnListOfCategory() {
    }
}