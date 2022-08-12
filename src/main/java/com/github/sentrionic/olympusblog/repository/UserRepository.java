package com.github.sentrionic.olympusblog.repository;

import com.github.sentrionic.olympusblog.model.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    boolean existsByEmail(String email);
    boolean existsByUsername(String username);
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    @Query(value = "SELECT u FROM User u WHERE u.username LIKE %:search% OR u.bio LIKE %:search%")
    List<User> findProfiles(@Param("search") String search);

    @Query(nativeQuery = true, value = "SELECT count(user_id) FROM user_followings where followers_id = :id")
    int getFolloweeCount(@Param("id") Long id);
}
