package com.nashtech.rookies.assetmanagement.repository;

import com.nashtech.rookies.assetmanagement.entity.User;
import com.nashtech.rookies.assetmanagement.util.StatusConstant;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Integer>, JpaSpecificationExecutor<User> {
    Optional<User> findByStaffCode(String staffCode);
    Optional<User> findByUsernameAndStatus(String username, StatusConstant status);
    Optional<User> findByIdAndStatus(Integer id, StatusConstant status);
    Optional<User> findByStaffCodeAndStatus(String staffCode, StatusConstant status);
    List<User> findByUsernameStartsWith(String username);
}
