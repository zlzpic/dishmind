package com.bdu.dishmind.service;

import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.request.FavoriteFolderRenameRequest;
import com.bdu.dishmind.dto.request.FavoriteRequest;
import com.bdu.dishmind.dto.response.RecipeResponse;

import java.util.List;

public interface FavoriteService {
    void toggleFavorite(FavoriteRequest request);
    Boolean checkFavorite(Long userId, Long recipeId);
    PageResult<RecipeResponse> listFavorites(Long userId, Integer page, Integer size);
    List<String> getFolders(Long userId);
    void renameFolder(FavoriteFolderRenameRequest request);
    void deleteFolder(Long userId, String folderName);
    PageResult<RecipeResponse> listFavoritesByFolder(Long userId, String folderName, Integer page, Integer size);
    void moveFavoriteToFolder(Long userId, Long recipeId, String targetFolderName);
}
