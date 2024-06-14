package com.nashtech.rookies.assetmanagement.repository;

import com.nashtech.rookies.assetmanagement.entity.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TokenRepository extends JpaRepository<Token, Integer> {
    List<Token> findAllByUserId(Integer userId);
    Optional<Token> findByToken(String token);

}
