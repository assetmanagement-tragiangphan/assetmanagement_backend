/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nashtech.rookies.assetmanagement.specifications;

import com.nashtech.rookies.assetmanagement.entity.Asset_;
import com.nashtech.rookies.assetmanagement.entity.Assignment_;
import com.nashtech.rookies.assetmanagement.entity.AuditMetadata_;
import com.nashtech.rookies.assetmanagement.entity.ReturnRequest;
import com.nashtech.rookies.assetmanagement.entity.ReturnRequest_;
import com.nashtech.rookies.assetmanagement.entity.User_;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;
import java.time.LocalDate;
import java.util.List;
import org.springframework.data.jpa.domain.Specification;

/**
 *
 * @author HP
 * @author Tamina
 */
public class ReturnRequestSpecification {

    public static Specification<ReturnRequest> withStatesIn(List<StatusConstant> states) {
        return (root, query, builder) -> {
            if (states == null) {
                return builder.conjunction();
            }
            return root.<StatusConstant>get(ReturnRequest_.STATUS).in(states);
        };
    }

    public static Specification<ReturnRequest> nameLike(String name) {
        return (root, query, builder) -> {
            if (name == null) {
                return builder.conjunction();
            }
            return builder.like(builder.lower(root.get(ReturnRequest_.ASSIGNMENT).get(Assignment_.ASSET).get(Asset_.NAME)), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<ReturnRequest> assetCodeLike(String code) {
        return (root, query, builder) -> {
            if (code == null) {
                return builder.conjunction();
            }
            return builder.like(builder.lower(root.get(ReturnRequest_.ASSIGNMENT).get(Assignment_.ASSET).get(Asset_.ASSET_CODE)), "%" + code.toLowerCase() + "%");
        };
    }

    public static Specification<ReturnRequest> requesterUsernameLike(String name) {
        return (root, query, builder) -> {
            if (name == null) {
                return builder.conjunction();
            }
            return builder.like(builder.lower(root.get(ReturnRequest_.AUDIT_METADATA).get(AuditMetadata_.CREATED_BY).get(User_.USERNAME)), "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<ReturnRequest> locationAt(LocationConstant location) {
        return (root, query, builder) -> {
            return builder.equal(root.get(ReturnRequest_.AUDIT_METADATA).get(AuditMetadata_.CREATED_BY).get(User_.LOCATION), location);
        };
    }

    public static Specification<ReturnRequest> dateEquals(LocalDate date) {
        return (root, query, builder) -> {
            return builder.equal(root.get(ReturnRequest_.RETURNED_DATE), date);
        };
    }

    public static Specification<ReturnRequest> filterSpecs(LocationConstant location, String search, LocalDate date, List<StatusConstant> states) {
        return Specification.where(locationAt(location)).and(dateEquals(date)).and(withStatesIn(states)).and(nameLike(search).or(assetCodeLike(search)).or(requesterUsernameLike(search)));
    }
}
