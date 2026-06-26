package com.bdu.dishmind.repository;

import com.bdu.dishmind.entity.UserBehavior;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserBehaviorRepository extends JpaRepository<UserBehavior, Long> {

    @Query(value = "SELECT DISTINCT recipe_id FROM user_behavior WHERE user_id = ?1", nativeQuery = true)
    List<Long> findRecipeIdsByUserId(Long userId);

    long countByUserId(Long userId);
}
