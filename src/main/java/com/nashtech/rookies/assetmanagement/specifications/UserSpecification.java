package com.nashtech.rookies.assetmanagement.specifications;

import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.entity.User_;
import com.nashtech.rookies.assetmanagement.util.LocationConstant;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;

public class UserSpecification {
    public static Specification<User> hasLocation (LocationConstant location) {
        return (root, query, builder) -> builder.equal(root.get(User_.LOCATION), location);
    }

    public static Specification<User> likeName (String name) {
        return (root, query, builder) -> builder.like(
            builder.lower(
                builder.concat(
                    builder.concat(root.get(User_.FIRST_NAME), " " ),
                    root.get(User_.LAST_NAME)
                )
            ),
            "%" + name.toLowerCase() + "%"
        );
    }

    public static Specification<User> likeStaffCode (String staffCode) {
        return (root, query, builder) -> builder.like(
            builder.lower(root.get(User_.STAFF_CODE))
            , "%" + staffCode.toLowerCase() + "%"
        );
    }

    public static Specification<User> hasRole (List<Integer> roleIds) {
        return (root, query, builder) -> root.get(User_.ROLE).get("id").in(roleIds);
    }

    public static Specification<User> hasStatus (StatusConstant status) {
        return (root, query, builder) -> builder.equal(root.get(User_.STATUS), status);
    }

    public static Specification<User> notHasUserId (Integer excludeUserId) {
        return (root, query, builder) -> builder.notEqual(root.get(User_.ID), excludeUserId);
    }

    public static Specification<User> userListFilter (String search, List<Integer> roleIds, LocationConstant location, Integer excludeUserId) {
        Specification<User> spec = Specification.where(hasStatus(StatusConstant.ACTIVE).and(notHasUserId(excludeUserId)));
        if (search != null) {
            spec = spec.and(likeName(search).or(likeStaffCode(search)));
        }
        if (roleIds != null) {
            spec = spec.and(hasRole(roleIds));
        }
        if (location != null) {
            spec = spec.and(hasLocation(location));
        }
        return spec;
    }
}
