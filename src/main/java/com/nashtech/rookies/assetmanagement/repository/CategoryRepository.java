package com.nashtech.rookies.assetmanagement.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer>, JpaSpecificationExecutor<Category> {
    boolean existsByPrefix(String prefix);
    boolean existsByName(String name);
    Category findCategoryByName(String name);
    Integer countByPrefixStartsWith(String prefix);

    List<CategoryResponse> findAllProjectedBy(Sort sort);
}
