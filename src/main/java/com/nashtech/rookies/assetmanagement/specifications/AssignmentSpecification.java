package com.nashtech.rookies.assetmanagement.specifications;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nashtech.rookies.assetmanagement.dto.UserDetailsDto;
import com.nashtech.rookies.assetmanagement.dto.request.Assignment.AssignmentGetRequest;
import com.nashtech.rookies.assetmanagement.entity.Assignment;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;

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
            return builder.like(
                builder.lower(root.join("assignee").get("username")),
                "%" + username.toLowerCase() + "%"
            );
            // return root.<Assignment>get("user").get("username").in(username);
            // return root.<Assignment>get("assignee").get("username").in(username);
        };
    }

    public static Specification<Assignment> withLocation(LocationConstant location) {
        return (root, query, builder) -> {
            if (location == null) {
                return builder.conjunction();
            }

            return root.<Assignment>get("auditMetadata").get("createdBy").get("location").in(location);
        };
    }

    public static Specification<Assignment> ownSpecs(UserDetailsDto requestUser) {
        return Specification.where(withUsername(requestUser.getUsername()).and(withState(List.of("ACCEPTED","WAITING_FOR_ACCEPTANCE"))));
    }

    public static Specification<Assignment> filterSpecs(AssignmentGetRequest request, LocationConstant location) {
        return Specification.where(withAssigneeUsername(request.getSearch()).or(withAssetCode(request.getSearch())).or(withAssetName(request.getSearch())).and(withState(request.getStatus())).and(withAssignedDate(request.getAssignedDate())).and(withLocation(location)));
    }
}
