package com.nashtech.rookies.assetmanagement.mapper;

import com.nashtech.rookies.assetmanagement.dto.response.CategoryResponse;
import com.nashtech.rookies.assetmanagement.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper
public interface CategoryMapper {
    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "prefix", source = "entity.prefix")
    @Mapping(target = "name", source = "entity.name")
    CategoryResponse entityToDto(Category entity);
}
