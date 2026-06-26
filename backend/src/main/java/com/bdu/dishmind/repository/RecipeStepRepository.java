package com.bdu.dishmind.repository;

import com.bdu.dishmind.entity.RecipeStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeStepRepository extends JpaRepository<RecipeStep, Long> {

    // 添加按状态查询，默认查正常的（status=0）
    List<RecipeStep> findByRecipeIdAndStatusOrderByStepNumberAsc(Long recipeId, Integer status);

    // 保留原有方法，Service层手动过滤
    List<RecipeStep> findByRecipeIdOrderByStepNumberAsc(Long recipeId);

    @Modifying
    @Query(value = "DELETE FROM recipe_step WHERE recipe_id = ?1", nativeQuery = true)
    void deleteByRecipeId(Long recipeId);

    @Query(value = "SELECT MAX(step_number) FROM recipe_step WHERE recipe_id = ?1 AND status = 0", nativeQuery = true)
    Integer findMaxStepNumberByRecipeId(Long recipeId);
}
