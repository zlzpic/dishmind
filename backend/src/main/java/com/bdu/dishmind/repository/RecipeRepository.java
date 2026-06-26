package com.bdu.dishmind.repository;

import com.bdu.dishmind.entity.Recipe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

    Page<Recipe> findByStatus(Integer status, Pageable pageable);

    Page<Recipe> findByAuthorId(Long authorId, Pageable pageable);

    Page<Recipe> findByStatusAndTitleContaining(Integer status, String keyword, Pageable pageable);

    @Query(value = "SELECT r.* FROM recipe r " +
            "JOIN recipe_tag rt ON r.id = rt.recipe_id " +
            "WHERE r.status = 2 AND rt.tag_id = ?1 " +
            "ORDER BY r.view_count DESC " +
            "LIMIT ?2, ?3", nativeQuery = true)
    List<Recipe> findByTagId(Integer tagId, Integer offset, Integer limit);

    @Query(value = "SELECT COUNT(DISTINCT r.id) FROM recipe r " +
            "JOIN recipe_tag rt ON r.id = rt.recipe_id " +
            "WHERE r.status = 2 AND rt.tag_id = ?1", nativeQuery = true)
    Long countByTagId(Integer tagId);

    @Query(value = "SELECT r.*, COUNT(rt2.tag_id) as common_tags " +
            "FROM recipe r JOIN recipe_tag rt2 ON r.id = rt2.recipe_id " +
            "WHERE rt2.tag_id IN (SELECT tag_id FROM recipe_tag WHERE recipe_id = ?1) " +
            "AND r.id != ?1 AND r.status = 2 " +
            "GROUP BY r.id ORDER BY common_tags DESC LIMIT ?2", nativeQuery = true)
    List<Recipe> findSimilarByTags(Long recipeId, Integer limit);

    @Query(value = "SELECT r.* FROM recipe r " +
            "WHERE r.status = 2 AND r.id NOT IN (?1) " +
            "ORDER BY r.view_count DESC LIMIT ?2", nativeQuery = true)
    List<Recipe> findHotExcludeIds(List<Long> excludeIds, Integer limit);

    @Query(value = "SELECT r.* FROM recipe r " +
            "JOIN recipe_tag rt ON r.id = rt.recipe_id " +
            "WHERE r.status = 2 AND rt.tag_id IN (?1) AND r.id NOT IN (?2) " +
            "GROUP BY r.id ORDER BY COUNT(rt.tag_id) DESC LIMIT ?3", nativeQuery = true)
    List<Recipe> findByTagIdsAndExcludeIds(List<Integer> tagIds, List<Long> excludeIds, Integer limit);
    /**
     * 多标签筛选（且关系：必须同时包含所有标签）
     * @param tagIds 标签ID列表
     * @param tagCount 标签数量（用于HAVING COUNT）
     * @param offset 分页偏移
     * @param limit 每页条数
     */
    @Query(value = "SELECT r.* FROM recipe r " +
            "WHERE r.status = 2 " +
            "AND r.id IN (" +
            "  SELECT rt.recipe_id FROM recipe_tag rt " +
            "  WHERE rt.tag_id IN (?1) " +  // 去掉 rt.status = 0
            "  GROUP BY rt.recipe_id " +
            "  HAVING COUNT(DISTINCT rt.tag_id) = ?2" +
            ") " +
            "ORDER BY r.view_count DESC " +
            "LIMIT ?3, ?4", nativeQuery = true)
    List<Recipe> findByTagIdsAllMatch(List<Integer> tagIds, int tagCount, int offset, int limit);

    @Query(value = "SELECT COUNT(DISTINCT r.id) FROM recipe r " +
            "WHERE r.status = 2 " +
            "AND r.id IN (" +
            "  SELECT rt.recipe_id FROM recipe_tag rt " +
            "  WHERE rt.tag_id IN (?1) " +  // 去掉 rt.status = 0
            "  GROUP BY rt.recipe_id " +
            "  HAVING COUNT(DISTINCT rt.tag_id) = ?2" +
            ")", nativeQuery = true)
    Long countByTagIdsAllMatch(List<Integer> tagIds, int tagCount);

    int countByAuthorId(Long authorId);
}
