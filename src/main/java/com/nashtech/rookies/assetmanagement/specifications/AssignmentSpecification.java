package com.nashtech.rookies.assetmanagement.specifications;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentGetRequest;
import com.nashtech.rookies.assetmanagement.entity.Assignment;

public class AssignmentSpecification {
    public static Specification<Assignment> withUsername(String username) {
        return (root, query, builder) -> {
            if (username == null) {
                return builder.conjunction();
            }

            return root.<Assignment>get("assignee").get("username").in(username);
        };
    }

    public static Specification<Assignment> withState(List<String> status) {
        return (root, query, builder) -> {
            if (status == null) {
                return builder.conjunction();
            }

            return root.<Assignment>get("status").in(status);
        };
    }

    public static Specification<Assignment> withAssignedDate(LocalDate assignedDate) {
        return (root, query, builder) -> {
            if (assignedDate == null) {
                return builder.conjunction();
            }

            return root.<Assignment>get("assignedDate").in(assignedDate);
        };
    }

    public static Specification<Assignment> withAssetCode(String assetCode) {
        return (root, query, builder) -> {
            if (assetCode == null) {
                return builder.conjunction();
            }
            return builder.like(
                builder.lower(root.join("asset").get("assetCode")),
                "%" + assetCode.toLowerCase() + "%"
            );
            // return root.<Assignment>get("asset").get("assetCode").in(assetName);
        };
    }

    public static Specification<Assignment> withAssetName(String assetName) {
        return (root, query, builder) -> {
            if (assetName == null) {
                return builder.conjunction();
            }
            return builder.like(
                builder.lower(root.join("asset").get("name")),
                "%" + assetName.toLowerCase() + "%"
            );
            // return root.<Assignment>get("asset").get("name").in(assetName);
        };
    }

    public static Specification<Assignment> withAssigneeUsername(String username) {
        return (root, query, builder) -> {
            if (username == null) {
                return builder.conjunction();
            }
            return root.<Assignment>get("user").get("username").in(username);
        };
    }

    public static Specification<Assignment> ownSpecs(UserDetailsDto requestUser) {
        return Specification.where(withUsername(requestUser.getUsername()));
    }

    public static Specification<Assignment> filterSpecs(AssignmentGetRequest request) {
        return Specification.where(withUsername(request.getSearchKey()).or(withAssetCode(request.getSearchKey())).or(withAssetName(request.getSearchKey())).and(withState(request.getStatus())).and(withAssignedDate(request.getAssignedDate())));
    }
}
