package com.bdu.dishmind.repository;

import com.bdu.dishmind.entity.RecipeTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RecipeTagRepository extends JpaRepository<RecipeTag, Long> {

    Optional<RecipeTag> findByRecipeIdAndTagId(Long recipeId, Integer tagId);

    List<RecipeTag> findByRecipeId(Long recipeId);

    @Query(value = "SELECT tag_id FROM recipe_tag WHERE recipe_id = ?1", nativeQuery = true)
    List<Integer> findTagIdsByRecipeId(Long recipeId);
    long countByTagId(Integer tagId); // 已在 RecipeTagRepository
}
