package com.bdu.dishmind.repository;

import com.bdu.dishmind.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TagRepository extends JpaRepository<Tag, Integer> {

    List<Tag> findByCategoryId(Integer categoryId);

    @Query(value = "SELECT t.* FROM tag t " +
            "JOIN recipe_tag rt ON t.id = rt.tag_id " +
            "WHERE rt.recipe_id = ?1", nativeQuery = true)
    List<Tag> findByRecipeId(Long recipeId);

    boolean existsByNameAndCategoryId(String name, Integer categoryId);
}
