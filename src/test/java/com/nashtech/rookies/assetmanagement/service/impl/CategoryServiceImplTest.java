package com.nashtech.rookies.assetmanagement.service.impl;

import com.nashtech.rookies.assetmanagement.dto.request.Asset.CategoryRequest;
import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.dto.response.ResponseDto;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.exception.ResourceAlreadyExistException;
import com.nashtech.rookies.assetmanagement.mapper.CategoryMapper;
import com.nashtech.rookies.assetmanagement.repository.CategoryRepository;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceImplTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryMapper categoryMapper;

    @InjectMocks
    private CategoryServiceImpl categoryServiceImpl;

    private CategoryRequest categoryRequest;
    private Category category1;
    private Category category2;
    private CategoryResponse categoryResponse1;
    private CategoryResponse categoryResponse2;

    @BeforeEach
    public void setUp() {
        categoryRequest = new CategoryRequest();
        categoryRequest.setPrefix("CA");
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
    public void testSaveCategory_WhenPrefixAndNameNotExists_thenSuccess() {
        when(categoryRepository.existsByPrefix(categoryRequest.getPrefix())).thenReturn(false);
        when(categoryRepository.existsByName(categoryRequest.getName())).thenReturn(false);
        when(categoryRepository.saveAndFlush(any(Category.class))).thenReturn(category1);
        when(categoryMapper.entityToDto(any(Category.class))).thenReturn(categoryResponse1);

        var response = categoryServiceImpl.saveCategory(categoryRequest);

        assertNotNull(response);
        assertEquals("Create Category successfully.", response.getMessage());
        assertEquals(categoryResponse1, response.getData());

        verify(categoryRepository).existsByPrefix(categoryRequest.getPrefix());
        verify(categoryRepository).existsByName(categoryRequest.getName());
        verify(categoryRepository).saveAndFlush(any(Category.class));
        verify(categoryMapper).entityToDto(any(Category.class));
    }

    @Test
    public void testSaveCategory_WhenPrefixExists_ThenThrowResourceAlreadyExistException() {
        when(categoryRepository.existsByPrefix(categoryRequest.getPrefix())).thenReturn(true);

        ResourceAlreadyExistException exception = assertThrows(ResourceAlreadyExistException.class, () ->
                categoryServiceImpl.saveCategory(categoryRequest));

        assertEquals("Prefix is already existed. Please enter a different prefix!", exception.getMessage());

        verify(categoryRepository).existsByPrefix(categoryRequest.getPrefix());
        verify(categoryRepository, never()).existsByName(categoryRequest.getName());
        verify(categoryRepository, never()).saveAndFlush(any(Category.class));
        verify(categoryMapper, never()).entityToDto(any(Category.class));
    }

    @Test
    public void testSaveCategory_WhenNameExists_ThenThrowResourceAlreadyExistException() {
        when(categoryRepository.existsByPrefix(categoryRequest.getPrefix())).thenReturn(false);
        when(categoryRepository.existsByName(categoryRequest.getName())).thenReturn(true);

        ResourceAlreadyExistException exception = assertThrows(ResourceAlreadyExistException.class, () ->
                categoryServiceImpl.saveCategory(categoryRequest));

        assertEquals("Category is already existed. Please enter a different category!", exception.getMessage());

        verify(categoryRepository).existsByPrefix(categoryRequest.getPrefix());
        verify(categoryRepository).existsByName(categoryRequest.getName());
        verify(categoryRepository, never()).saveAndFlush(any(Category.class));
        verify(categoryMapper, never()).entityToDto(any(Category.class));
    }

    @Test
    public void testGetAllCategory_WhenSuccess_ThenReturnListCategory() {
        List<Category> categoryList = Arrays.asList(category1, category2);
        Sort sort = Sort.by(Sort.Direction.ASC, "auditMetadata.createdOn");
        Pageable pageable = PageRequest.of(0, categoryList.size(), sort);
        Page<Category> categories = new PageImpl<>(categoryList, pageable, categoryList.size());

        when(categoryRepository.findAll()).thenReturn(categoryList);
        when(categoryRepository.findAll(pageable)).thenReturn(categories);
        when(categoryMapper.entityToDto(category1)).thenReturn(categoryResponse1);
        when(categoryMapper.entityToDto(category2)).thenReturn(categoryResponse2);

        ResponseDto<List<CategoryResponse>> response = categoryServiceImpl.getAllCategory();

        assertNotNull(response);
        assertNotNull(response.getData());
        assertEquals(2, response.getData().size());
        assertEquals("LA", response.getData().get(0).getPrefix());
        assertEquals("MO", response.getData().get(1).getPrefix());

        verify(categoryRepository).findAll();
        verify(categoryMapper).entityToDto(category1);
        verify(categoryMapper).entityToDto(category2);
    }
}
