/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.specifications;

import com.nashtech.rookies.assetmanagement.entity.Asset;
import com.nashtech.rookies.assetmanagement.entity.Asset_;
import com.nashtech.rookies.assetmanagement.entity.Category;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import jakarta.persistence.criteria.Path;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author HP
 * @author Tamina
 */
public class AssetSpecification {

    public static Specification<Asset> withCategoryIn(List<Long> category_id) {
        return (root, query, builder) -> {
            if (category_id == null) {
                return builder.conjunction();
            }

            return root.<Category>get("category").get("id").in(category_id);
        };
    }

    public static Specification<Asset> withStatesIn(List<String> states) {
        return (root, query, builder) -> {
            if (states == null) {
                return builder.conjunction();
            }
            return root.<StatusConstant>get(Asset_.STATUS).in(states);
        };
    }

    public static Specification<Asset> nameLike(String name) {
        return (root, query, builder) -> {
            if (name == null) {
                return builder.conjunction();
            }
            return builder.like(root.get(Asset_.NAME), "%" + name + "%");
        };
    }

    public static Specification<Asset> assetCodeLike(String code) {
        return (root, query, builder) -> {
            if (code == null) {
                return builder.conjunction();
            }
            return builder.like(root.get(Asset_.ASSET_CODE), "%" + code + "%");
        };
    }

    public static Specification<Asset> locationAt(LocationConstant location) {
        return (root, query, builder) -> {
            return builder.equal(root.get(Asset_.LOCATION), location);
        };
    }

    public static Specification<Asset> filterSpecs(LocationConstant location, List<Long> category_id, String search, List<String> states) {
        return Specification.where(locationAt(location)).and(nameLike(search).or(assetCodeLike(search))).and(withCategoryIn(category_id)).and(withStatesIn(states));
    }
}
