package com.bdu.dishmind.dto.request;

import lombok.Data;

@Data
public class FavoriteFolderRenameRequest {
    private Long userId;
    private String oldFolderName;  // 原文件夹名
    private String newFolderName;  // 新文件夹名
}
