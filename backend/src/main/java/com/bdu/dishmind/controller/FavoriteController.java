package com.bdu.dishmind.controller;

import com.bdu.dishmind.dto.ApiResult;
import com.bdu.dishmind.dto.PageResult;
import com.bdu.dishmind.dto.request.FavoriteFolderRenameRequest;
import com.bdu.dishmind.dto.request.FavoriteRequest;
import com.bdu.dishmind.dto.response.RecipeResponse;
import com.bdu.dishmind.service.FavoriteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/favorite")
public class FavoriteController {

    @Autowired
    private FavoriteService favoriteService;

    @PostMapping("/toggle")
    public ApiResult<Void> toggleFavorite(@RequestBody FavoriteRequest request) {
        favoriteService.toggleFavorite(request);
        return ApiResult.success(null);
    }

    @GetMapping("/check")
    public ApiResult<Boolean> checkFavorite(@RequestParam Long userId, @RequestParam Long recipeId) {
        return ApiResult.success(favoriteService.checkFavorite(userId, recipeId));
    }

    @GetMapping("/list")
    public ApiResult<PageResult<RecipeResponse>> listFavorites(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResult.success(favoriteService.listFavorites(userId, page, size));
    }

    @GetMapping("/folders")
    public ApiResult<List<String>> getFolders(@RequestParam Long userId) {
        return ApiResult.success(favoriteService.getFolders(userId));
    }

    @PostMapping("/folder/rename")
    public ApiResult<Void> renameFolder(@RequestBody FavoriteFolderRenameRequest request) {
        favoriteService.renameFolder(request);
        return ApiResult.success(null);
    }

    @PostMapping("/folder/delete")
    public ApiResult<Void> deleteFolder(@RequestParam Long userId, @RequestParam String folderName) {
        favoriteService.deleteFolder(userId, folderName);
        return ApiResult.success(null);
    }

    @GetMapping("/list-by-folder")
    public ApiResult<PageResult<RecipeResponse>> listByFolder(
            @RequestParam Long userId,
            @RequestParam(required = false) String folderName,
            @RequestParam(defaultValue = "1") Integer page,
            @RequestParam(defaultValue = "10") Integer size) {
        return ApiResult.success(favoriteService.listFavoritesByFolder(userId, folderName, page, size));
    }

    @PostMapping("/foldermove")
    public ApiResult<Void> moveToFolder(@RequestParam Long userId,
                                        @RequestParam Long recipeId,
                                        @RequestParam String folderName) {
        favoriteService.moveFavoriteToFolder(userId, recipeId, folderName);
        return ApiResult.success(null);
    }
}
