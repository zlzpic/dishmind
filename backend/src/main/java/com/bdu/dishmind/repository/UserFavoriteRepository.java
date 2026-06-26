package com.bdu.dishmind.repository;

import com.bdu.dishmind.entity.UserFavorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserFavoriteRepository extends JpaRepository<UserFavorite, Long> {

    Page<UserFavorite> findByUserIdAndFolderNameAndStatus(Long userId, String folderName, Integer status, Pageable pageable);
    Page<UserFavorite> findByUserIdAndStatus(Long userId, Integer status, Pageable pageable);

    Optional<UserFavorite> findByUserIdAndRecipeId(Long userId, Long recipeId);

    Optional<UserFavorite> findByUserIdAndRecipeIdAndStatus(Long userId, Long recipeId, Integer status);

    boolean existsByUserIdAndRecipeIdAndStatus(Long userId, Long recipeId, Integer status);

    @Query("SELECT DISTINCT f.folderName FROM UserFavorite f WHERE f.userId = ?1 AND f.status = 0")
    List<String> findDistinctFolderNamesByUserId(Long userId);

    @Modifying
    @Query("UPDATE UserFavorite f SET f.folderName = ?3 WHERE f.userId = ?1 AND f.folderName = ?2 AND f.status = 0")
    void updateFolderName(Long userId, String oldFolderName, String newFolderName);

    @Modifying
    @Query("UPDATE UserFavorite f SET f.folderName = '默认收藏夹' WHERE f.userId = ?1 AND f.folderName = ?2 AND f.status = 0")
    void moveToDefaultFolder(Long userId, String folderName);

    long countByUserIdAndStatus(Long userId, Integer status);
}
