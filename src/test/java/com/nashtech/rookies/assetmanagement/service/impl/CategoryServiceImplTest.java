package com.nashtech.rookies.assetmanagement.service.impl;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import com.nashtech.rookies.assetmanagement.dto.request.Asset.CategoryRequest;
import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.mapper.CategoryMapper;
import com.nashtech.rookies.assetmanagement.repository.CategoryRepository;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;

@ExtendWith(MockitoExtension.class)
class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    private CategoryMapper categoryMapper = Mappers.getMapper(CategoryMapper.class);

    private CategoryServiceImpl categoryServiceImpl;

    private CategoryRequest categoryRequest;
    private Category category1;
    private Category category2;
    private CategoryResponse categoryResponse1;
    private CategoryResponse categoryResponse2;

    @BeforeEach
    void setUp() {
        categoryServiceImpl = new CategoryServiceImpl(categoryRepository, categoryMapper);
        categoryRequest = new CategoryRequest();
        categoryRequest.setName("Category Name");

        category1 = new Category();
        category1.setId(1);
        category1.setPrefix("LA");
        category1.setName("Laptop");
        category1.setStatus(StatusConstant.ACTIVE);

        category2 = new Category();
        category2.setId(2);
        category2.setPrefix("MO");
        category2.setName("Monitor");
        category2.setStatus(StatusConstant.ACTIVE);

        categoryResponse1 = new CategoryResponse();
        categoryResponse1.setId(1);
        categoryResponse1.setPrefix("LA");
        categoryResponse1.setName("Laptop");

        categoryResponse2 = new CategoryResponse();
        categoryResponse2.setId(2);
        categoryResponse2.setPrefix("MO");
        categoryResponse2.setName("Monitor");
    }

    @Test
    void testSaveCategory_WhenPrefixAndNameNotExists_thenSuccess() {
        Integer existPrefixCount = 2;
        when(categoryRepository.existsByName(categoryRequest.getName())).thenReturn(false);
        when(categoryRepository.countByPrefixStartsWith(any(String.class))).thenReturn(existPrefixCount);
        ArgumentCaptor<Category> categoryCaptor = ArgumentCaptor.forClass(Category.class);
        when(categoryRepository.saveAndFlush(categoryCaptor.capture())).thenReturn(category1);

        var response = categoryServiceImpl.saveCategory(categoryRequest);

        Category presavedCategory = categoryCaptor.getValue();
        assertThat(presavedCategory).isNotNull();
        assertThat(presavedCategory.getPrefix()).isEqualTo("CN" + existPrefixCount);
        assertThat(presavedCategory.getName()).isEqualTo(categoryRequest.getName());
        assertThat(presavedCategory.getStatus()).isEqualTo(StatusConstant.ACTIVE);

        assertNotNull(response);
        assertEquals("Create Category successfully.", response.getMessage());
        assertEquals(categoryResponse1.getName(), response.getData().getName());
        assertEquals(categoryResponse1.getPrefix(), response.getData().getPrefix());

        verify(categoryRepository).existsByName(categoryRequest.getName());
        verify(categoryRepository).saveAndFlush(any(Category.class));
    }

    @Test
    void testSaveCategory_WhenNameExists_ThenThrowResourceAlreadyExistException() {
        when(categoryRepository.existsByName(categoryRequest.getName())).thenReturn(true);

        ResourceAlreadyExistException exception = assertThrows(ResourceAlreadyExistException.class, () ->
                categoryServiceImpl.saveCategory(categoryRequest));

        assertEquals("Category is already existed. Please enter a different category", exception.getMessage());

        verify(categoryRepository).existsByName(categoryRequest.getName());
        verify(categoryRepository, never()).saveAndFlush(any(Category.class));
    }

    @Test
    void testGetAllCategory_WhenSuccess_ThenReturnListCategory() {
        List<Category> categoryList = Arrays.asList(category1, category2);
        Sort sort = Sort.by(Sort.Direction.ASC, "auditMetadata.createdOn");

        when(categoryRepository.findAll(sort)).thenReturn(categoryList);

        ResponseDto<List<CategoryResponse>> response = categoryServiceImpl.getAllCategory();

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals("LA", response.getData().get(0).getPrefix());
        assertEquals("MO", response.getData().get(1).getPrefix());

        verify(categoryRepository, times(1)).findAll(any(Sort.class));
    }
}
