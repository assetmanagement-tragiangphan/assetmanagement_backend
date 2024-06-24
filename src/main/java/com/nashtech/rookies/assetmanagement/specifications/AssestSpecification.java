/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.specifications;

import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Asset_;
import com.nashtech.rookies.assetmanagement.entity.Category;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Path;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author HP
 * @author Tamina
 */
public class AssestSpecification {

    public static Specification<Asset> withCategoryIn(List<Long> category_id) {
        return (root, query, builder) -> {
            if (category_id.isEmpty()) {
                return builder.conjunction();
            }
            final Path<Category> group = root.<Category>get("category").get("category").get("id");
            return group.in(category_id);
        };
    }

    public static Specification<Asset> nameLike(String name) {
        return (root, query, builder) -> {
            return builder.like(root.get(Asset_.NAME), "%" + name + "%");
        };
    }

    public static Specification<Asset> assetCodeLike(String code) {
        return (root, query, builder) -> {
            return builder.like(root.get(Asset_.ASSET_CODE), "%" + code + "%");
        };
    }

    public static Specification<Asset> filterSpecs(List<Long> category_id, String search) {
        return Specification.where(nameLike(search)).and(withCategoryIn(category_id));
    }
}
