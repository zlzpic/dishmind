/*
 Navicat Premium Data Transfer

 Source Server         : mysql50
 Source Server Type    : MySQL
 Source Server Version : 50738
 Source Host           : localhost:3306
 Source Schema         : dishmind

 Target Server Type    : MySQL
 Target Server Version : 50738
 File Encoding         : 65001

 Date: 20/02/2026 14:07:43
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for recipe
-- ----------------------------
DROP TABLE IF EXISTS `recipe`;
CREATE TABLE `recipe`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '菜谱名称',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '简介',
  `cover_image` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '封面图',
  `author_id` bigint(20) NULL DEFAULT NULL COMMENT '作者ID',
  `difficulty` tinyint(4) NULL DEFAULT NULL COMMENT '难度 1-5',
  `cook_time` int(11) NULL DEFAULT NULL COMMENT '烹饪时间（分钟）',
  `rating` decimal(2, 1) NULL DEFAULT 5.0 COMMENT '评分 0-5',
  `rating_count` int(11) NULL DEFAULT 0 COMMENT '评分人数',
  `view_count` int(11) NULL DEFAULT 0 COMMENT '浏览量',
  `collect_count` int(11) NULL DEFAULT 0 COMMENT '收藏数',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '0-草稿 1-审核中 2-正常显示 3-下架',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_author`(`author_id`) USING BTREE,
  INDEX `idx_status`(`status`) USING BTREE,
  INDEX `idx_rating`(`rating`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recipe
-- ----------------------------
INSERT INTO `recipe` VALUES (1, '麻婆豆腐', '经典川菜，麻辣鲜香，下饭神器', 'cover_1', 1, 2, 20, 4.8, 156, 3200, 580, 2, '2026-02-16 16:31:53', '2026-02-18 15:44:02');
INSERT INTO `recipe` VALUES (2, '番茄炒蛋', '国民家常菜，酸甜可口', 'cover_1', 1, 1, 10, 4.5, 230, 5100, 921, 2, '2026-02-16 16:31:53', '2026-02-19 13:40:34');
INSERT INTO `recipe` VALUES (3, '宫保鸡丁', '川菜经典，辣中带甜，花生酥脆', 'cover_1', 1, 3, 25, 4.6, 189, 2800, 451, 2, '2026-02-16 16:31:53', '2026-02-18 21:38:33');
INSERT INTO `recipe` VALUES (4, '清蒸鲈鱼', '粤菜代表，清淡鲜美，宴客首选', 'cover_1', 2, 2, 15, 4.7, 98, 1500, 320, 2, '2026-02-16 16:31:53', '2026-02-18 15:44:11');
INSERT INTO `recipe` VALUES (5, '红烧肉', '肥而不腻，入口即化，家常硬菜', 'cover_1', 1, 3, 60, 4.9, 312, 4500, 891, 2, '2026-02-16 16:31:53', '2026-02-19 13:40:55');
INSERT INTO `recipe` VALUES (6, '酸辣土豆丝', '快手素菜，酸辣开胃', 'cover_1', 2, 1, 10, 4.3, 145, 2100, 381, 2, '2026-02-16 16:31:53', '2026-02-19 13:55:13');
INSERT INTO `recipe` VALUES (7, '可乐鸡翅', '甜口爱好者必学，简单零失败', 'cover_1', 2, 1, 25, 4.4, 178, 3600, 621, 2, '2026-02-16 16:31:53', '2026-02-19 13:41:11');
INSERT INTO `recipe` VALUES (8, '水煮牛肉', '麻辣过瘾，牛肉嫩滑，川菜招牌', 'cover_1', 1, 4, 35, 4.7, 134, 1900, 410, 2, '2026-02-16 16:31:53', '2026-02-18 15:44:17');
INSERT INTO `recipe` VALUES (9, '白灼虾', '粤菜经典，原汁原味，5分钟搞定', 'cover_1', 2, 1, 5, 4.6, 167, 2400, 540, 2, '2026-02-16 16:31:53', '2026-02-18 15:44:19');
INSERT INTO `recipe` VALUES (10, '地三鲜', '东北名菜，咸鲜下饭，素食也香', 'cover_1', 1, 2, 20, 4.5, 112, 1800, 290, 2, '2026-02-16 16:31:53', '2026-02-18 15:44:20');
INSERT INTO `recipe` VALUES (11, '待审核菜谱', '这是一个测试审核流程【驳回原因：图片不清晰，请补充步骤图】', 'cover_1', 2, 2, 30, 0.0, 0, 0, 0, 1, '2026-02-16 16:31:53', '2026-02-19 19:38:19');
INSERT INTO `recipe` VALUES (12, '已下架菜谱', '这个菜谱已下架', 'cover_1', 1, 2, 20, 4.0, 50, 800, 120, 3, '2026-02-16 16:31:53', '2026-02-19 12:38:33');
INSERT INTO `recipe` VALUES (13, '测试新菜谱-宫保鸡丁家庭版', '简化版的宫保鸡丁，适合新手', 'https://example.com/test-cover.jpg', 1, 2, 20, 5.0, 0, 0, 0, 1, '2026-02-18 20:59:15', '2026-02-19 19:54:40');
INSERT INTO `recipe` VALUES (14, '已下架菜谱2', '这个菜谱已下架2', 'cover_12', 1, 2, 20, 4.0, 50, 800, 120, 3, '2026-02-16 16:31:53', '2026-02-19 12:33:29');

-- ----------------------------
-- Table structure for recipe_step
-- ----------------------------
DROP TABLE IF EXISTS `recipe_step`;
CREATE TABLE `recipe_step`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `recipe_id` bigint(20) NOT NULL COMMENT '所属菜谱ID',
  `step_number` int(11) NOT NULL COMMENT '步骤序号',
  `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '步骤描述',
  `image_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '步骤配图',
  `tip` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '小贴士',
  `duration_seconds` int(11) NULL DEFAULT NULL COMMENT '预计耗时（秒）',
  `status` tinyint(4) NULL DEFAULT 0 COMMENT '0-正常 1-已删除（软删除）',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_recipe_step`(`recipe_id`, `step_number`) USING BTREE,
  INDEX `idx_recipe`(`recipe_id`) USING BTREE,
  CONSTRAINT `recipe_step_ibfk_1` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 51 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recipe_step
-- ----------------------------
INSERT INTO `recipe_step` VALUES (1, 1, 1, '【更新】豆腐切成2厘米见方的小块，淡盐水浸泡15分钟', 'http://example.com/step1_new.jpg', '浸泡时间延长至15分钟更入味', 900, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (2, 1, 2, '热锅凉油，下入牛肉末中小火煸炒至酥香', 'https://example.com/mapo/step2.jpg', '牛肉要煸干才香，油温不要太高', 300, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (3, 1, 3, '加入豆瓣酱炒出红油，下入姜蒜末爆香', 'https://example.com/mapo/step3.jpg', '豆瓣酱要炒出红油才香', 120, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (4, 1, 4, '加入适量高汤或清水，调入生抽、老抽、糖，煮沸', 'https://example.com/mapo/step4.jpg', '汤底要咸鲜适口', 180, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (5, 1, 5, '滑入豆腐块，中小火煮3分钟入味', 'https://example.com/mapo/step5.jpg', '不要用铲子翻动，轻轻推匀', 180, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (6, 1, 6, '分三次淋入水淀粉勾芡，推匀', 'https://example.com/mapo/step6.jpg', '芡汁要薄而亮', 60, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (7, 1, 7, '撒上花椒粉和青蒜末，出锅装盘', 'https://example.com/mapo/step7.jpg', '花椒粉最后撒才有麻香味', 30, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (8, 2, 1, '番茄顶部划十字，开水烫30秒去皮', 'https://example.com/fanqie/step1.jpg', '去皮口感更好，也可省略', 120, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (9, 2, 2, '番茄切块，鸡蛋加少许盐打散', 'https://example.com/fanqie/step2.jpg', '鸡蛋要充分打散', 180, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (10, 2, 3, '热锅热油，倒入蛋液炒散盛出', 'https://example.com/fanqie/step3.jpg', '油温要高，鸡蛋才蓬松', 90, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (11, 2, 4, '锅中余油炒番茄至出汁', 'https://example.com/fanqie/step4.jpg', '中火慢炒出汁水', 240, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (12, 2, 5, '倒入鸡蛋翻炒均匀，加糖盐调味', 'https://example.com/fanqie/step5.jpg', '加糖能提鲜中和酸味', 60, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (13, 3, 1, '鸡胸肉切丁，加料酒、生抽、淀粉腌制15分钟', 'https://example.com/gongbao/step1.jpg', '淀粉锁住水分更嫩滑', 900, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (14, 3, 2, '花生米炸酥放凉备用', 'https://example.com/gongbao/step2.jpg', '小火慢炸，放凉才脆', 300, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (15, 3, 3, '调好宫保汁：糖、醋、生抽、淀粉、水调匀', 'https://example.com/gongbao/step3.jpg', '糖醋比例约1:1', 120, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (16, 3, 4, '热锅凉油滑炒鸡丁至变色盛出', 'https://example.com/gongbao/step4.jpg', '变色即出锅，不要炒老', 180, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (17, 3, 5, '爆香干辣椒花椒，下葱姜蒜炒香', 'https://example.com/gongbao/step5.jpg', '小火慢炸出麻辣味', 120, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (18, 3, 6, '下鸡丁翻炒，倒入宫保汁快速炒匀', 'https://example.com/gongbao/step6.jpg', '大火快炒，汁要包裹食材', 90, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (19, 3, 7, '最后加入花生米翻匀出锅', 'https://example.com/gongbao/step7.jpg', '花生米最后放保持酥脆', 30, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (20, 4, 1, '鲈鱼处理干净，两面划刀，抹少许盐腌制10分钟', 'https://example.com/qingzheng/step1.jpg', '划刀利于蒸熟均匀', 600, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (21, 4, 2, '盘底铺姜片葱段，放上鱼，鱼肚塞入葱姜', 'https://example.com/qingzheng/step2.jpg', '去腥增香', 180, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (22, 4, 3, '水开上锅蒸8分钟，关火虚蒸2分钟', 'https://example.com/qingzheng/step3.jpg', '虚蒸让鱼肉更嫩', 600, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (23, 4, 4, '倒掉蒸出的汤汁，铺上葱丝红椒丝', 'https://example.com/qingzheng/step4.jpg', '汤汁较腥必须倒掉', 120, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (24, 4, 5, '淋上蒸鱼豉油，浇上热油激香', 'https://example.com/qingzheng/step5.jpg', '油温要高才能激发出香味', 60, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (25, 5, 1, '五花肉切3厘米方块，冷水下锅焯水去血沫', 'https://example.com/hongshao/step1.jpg', '冷水下锅才能去净血水', 300, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (26, 5, 2, '锅中少油，煸炒肉块至表面微黄出油', 'https://example.com/hongshao/step2.jpg', '煸出油脂才不腻', 600, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (27, 5, 3, '加入冰糖炒出糖色，翻炒肉块上色', 'https://example.com/hongshao/step3.jpg', '小火慢炒糖色，别炒糊', 300, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (28, 5, 4, '加入八角桂皮香叶、葱姜、生抽老抽炒匀', 'https://example.com/hongshao/step4.jpg', '香料不要太多，抢味', 180, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (29, 5, 5, '加入开水没过肉块，大火烧开转小火炖1小时', 'https://example.com/hongshao/step5.jpg', '必须加开水，肉质不柴', 3600, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (30, 5, 6, '开大火收汁，至汤汁浓稠红亮', 'https://example.com/hongshao/step6.jpg', '不停翻炒防糊底', 300, 0, '2026-02-18 19:44:16');
INSERT INTO `recipe_step` VALUES (48, 13, 1, '鸡肉切丁，加料酒淀粉腌制', 'https://example.com/step1.jpg', '腌制10分钟更嫩', 600, 0, '2026-02-19 15:51:16');
INSERT INTO `recipe_step` VALUES (49, 13, 2, '热锅凉油滑炒鸡丁', '', '变色即出锅', 180, 0, '2026-02-19 15:51:16');
INSERT INTO `recipe_step` VALUES (50, 13, 3, '测试步骤3', '1', 'test', 1, 0, '2026-02-19 15:51:16');

-- ----------------------------
-- Table structure for recipe_tag
-- ----------------------------
DROP TABLE IF EXISTS `recipe_tag`;
CREATE TABLE `recipe_tag`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `recipe_id` bigint(20) NOT NULL,
  `tag_id` int(11) NOT NULL,
  `weight` decimal(3, 2) NULL DEFAULT 1.00 COMMENT '该菜谱此标签的权重',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_recipe_tag`(`recipe_id`, `tag_id`) USING BTREE,
  INDEX `idx_tag_recipe`(`tag_id`, `recipe_id`) USING BTREE,
  CONSTRAINT `recipe_tag_ibfk_1` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `recipe_tag_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 56 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of recipe_tag
-- ----------------------------
INSERT INTO `recipe_tag` VALUES (1, 1, 1, 1.00);
INSERT INTO `recipe_tag` VALUES (2, 1, 6, 0.90);
INSERT INTO `recipe_tag` VALUES (3, 1, 8, 1.00);
INSERT INTO `recipe_tag` VALUES (4, 1, 19, 1.00);
INSERT INTO `recipe_tag` VALUES (5, 1, 26, 0.90);
INSERT INTO `recipe_tag` VALUES (6, 1, 31, 0.80);
INSERT INTO `recipe_tag` VALUES (7, 2, 4, 1.00);
INSERT INTO `recipe_tag` VALUES (8, 2, 11, 1.00);
INSERT INTO `recipe_tag` VALUES (9, 2, 18, 1.00);
INSERT INTO `recipe_tag` VALUES (10, 2, 25, 0.90);
INSERT INTO `recipe_tag` VALUES (11, 2, 30, 0.90);
INSERT INTO `recipe_tag` VALUES (12, 3, 2, 0.90);
INSERT INTO `recipe_tag` VALUES (13, 3, 3, 0.60);
INSERT INTO `recipe_tag` VALUES (14, 3, 8, 1.00);
INSERT INTO `recipe_tag` VALUES (15, 3, 15, 1.00);
INSERT INTO `recipe_tag` VALUES (16, 3, 27, 0.70);
INSERT INTO `recipe_tag` VALUES (17, 4, 7, 1.00);
INSERT INTO `recipe_tag` VALUES (18, 4, 9, 1.00);
INSERT INTO `recipe_tag` VALUES (19, 4, 22, 1.00);
INSERT INTO `recipe_tag` VALUES (20, 4, 27, 1.00);
INSERT INTO `recipe_tag` VALUES (21, 4, 31, 0.80);
INSERT INTO `recipe_tag` VALUES (22, 5, 5, 1.00);
INSERT INTO `recipe_tag` VALUES (23, 5, 11, 0.90);
INSERT INTO `recipe_tag` VALUES (24, 5, 16, 1.00);
INSERT INTO `recipe_tag` VALUES (25, 5, 26, 1.00);
INSERT INTO `recipe_tag` VALUES (26, 5, 32, 0.80);
INSERT INTO `recipe_tag` VALUES (27, 6, 4, 1.00);
INSERT INTO `recipe_tag` VALUES (28, 6, 1, 0.80);
INSERT INTO `recipe_tag` VALUES (29, 6, 11, 0.90);
INSERT INTO `recipe_tag` VALUES (30, 6, 20, 1.00);
INSERT INTO `recipe_tag` VALUES (31, 6, 25, 1.00);
INSERT INTO `recipe_tag` VALUES (32, 6, 30, 0.90);
INSERT INTO `recipe_tag` VALUES (33, 7, 3, 1.00);
INSERT INTO `recipe_tag` VALUES (34, 7, 11, 0.90);
INSERT INTO `recipe_tag` VALUES (35, 7, 15, 1.00);
INSERT INTO `recipe_tag` VALUES (36, 7, 30, 1.00);
INSERT INTO `recipe_tag` VALUES (37, 7, 25, 0.80);
INSERT INTO `recipe_tag` VALUES (38, 8, 1, 1.00);
INSERT INTO `recipe_tag` VALUES (39, 8, 6, 0.90);
INSERT INTO `recipe_tag` VALUES (40, 8, 8, 1.00);
INSERT INTO `recipe_tag` VALUES (41, 8, 17, 1.00);
INSERT INTO `recipe_tag` VALUES (42, 8, 33, 0.80);
INSERT INTO `recipe_tag` VALUES (43, 9, 7, 1.00);
INSERT INTO `recipe_tag` VALUES (44, 9, 9, 1.00);
INSERT INTO `recipe_tag` VALUES (45, 9, 23, 1.00);
INSERT INTO `recipe_tag` VALUES (46, 9, 25, 1.00);
INSERT INTO `recipe_tag` VALUES (47, 9, 30, 0.90);
INSERT INTO `recipe_tag` VALUES (48, 10, 5, 0.90);
INSERT INTO `recipe_tag` VALUES (49, 10, 13, 1.00);
INSERT INTO `recipe_tag` VALUES (50, 10, 20, 0.80);
INSERT INTO `recipe_tag` VALUES (51, 10, 21, 0.80);
INSERT INTO `recipe_tag` VALUES (52, 10, 26, 0.90);
INSERT INTO `recipe_tag` VALUES (53, 13, 1, 1.00);
INSERT INTO `recipe_tag` VALUES (54, 13, 15, 1.00);
INSERT INTO `recipe_tag` VALUES (55, 13, 25, 1.00);

-- ----------------------------
-- Table structure for tag
-- ----------------------------
DROP TABLE IF EXISTS `tag`;
CREATE TABLE `tag`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `category_id` int(11) NOT NULL COMMENT '类型ID，外键',
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '标签名：辣/川菜/鸡肉',
  `usage_count` int(11) NULL DEFAULT 0 COMMENT '使用次数',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_category_name`(`category_id`, `name`) USING BTREE,
  INDEX `idx_name`(`name`) USING BTREE,
  CONSTRAINT `tag_ibfk_1` FOREIGN KEY (`category_id`) REFERENCES `tag_category` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 35 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag
-- ----------------------------
INSERT INTO `tag` VALUES (1, 1, '辣', 4);
INSERT INTO `tag` VALUES (2, 1, '微辣', 1);
INSERT INTO `tag` VALUES (3, 1, '甜', 2);
INSERT INTO `tag` VALUES (4, 1, '酸甜', 2);
INSERT INTO `tag` VALUES (5, 1, '咸鲜', 2);
INSERT INTO `tag` VALUES (6, 1, '麻', 2);
INSERT INTO `tag` VALUES (7, 1, '清淡', 2);
INSERT INTO `tag` VALUES (8, 2, '川菜', 3);
INSERT INTO `tag` VALUES (9, 2, '粤菜', 2);
INSERT INTO `tag` VALUES (10, 2, '湘菜', 0);
INSERT INTO `tag` VALUES (11, 2, '家常菜', 4);
INSERT INTO `tag` VALUES (12, 2, '西餐', 0);
INSERT INTO `tag` VALUES (13, 2, '东北菜', 1);
INSERT INTO `tag` VALUES (14, 2, '江浙菜', 0);
INSERT INTO `tag` VALUES (15, 3, '鸡肉', 3);
INSERT INTO `tag` VALUES (16, 3, '猪肉', 1);
INSERT INTO `tag` VALUES (17, 3, '牛肉', 1);
INSERT INTO `tag` VALUES (18, 3, '鸡蛋', 1);
INSERT INTO `tag` VALUES (19, 3, '豆腐', 1);
INSERT INTO `tag` VALUES (20, 3, '土豆', 2);
INSERT INTO `tag` VALUES (21, 3, '茄子', 1);
INSERT INTO `tag` VALUES (22, 3, '鱼', 1);
INSERT INTO `tag` VALUES (23, 3, '虾', 1);
INSERT INTO `tag` VALUES (24, 4, '早餐', 0);
INSERT INTO `tag` VALUES (25, 4, '快手菜', 5);
INSERT INTO `tag` VALUES (26, 4, '下饭菜', 3);
INSERT INTO `tag` VALUES (27, 4, '宴客', 2);
INSERT INTO `tag` VALUES (28, 4, '夜宵', 0);
INSERT INTO `tag` VALUES (29, 4, '便当', 0);
INSERT INTO `tag` VALUES (30, 5, '新手', 4);
INSERT INTO `tag` VALUES (31, 5, '简单', 2);
INSERT INTO `tag` VALUES (32, 5, '中等', 1);
INSERT INTO `tag` VALUES (33, 5, '挑战', 1);

-- ----------------------------
-- Table structure for tag_category
-- ----------------------------
DROP TABLE IF EXISTS `tag_category`;
CREATE TABLE `tag_category`  (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `name` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '类型名：口味/菜系/食材/场景',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `name`(`name`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of tag_category
-- ----------------------------
INSERT INTO `tag_category` VALUES (6, '功效');
INSERT INTO `tag_category` VALUES (1, '口味');
INSERT INTO `tag_category` VALUES (4, '场景');
INSERT INTO `tag_category` VALUES (2, '菜系');
INSERT INTO `tag_category` VALUES (5, '难度');
INSERT INTO `tag_category` VALUES (3, '食材');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '登录名',
  `password` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '显示昵称',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `avatar_url` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL,
  `user_type` tinyint(4) NULL DEFAULT 0 COMMENT '0-普通用户 1-管理员',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `status` tinyint(4) NULL DEFAULT 1 COMMENT '0-禁用 1-正常',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, '1', '1', '美食探索家', '13800138001', 'avatar_1', 0, '2026-02-16 16:31:52', 1);
INSERT INTO `user` VALUES (2, '2', '2', '厨房小白', '13800138002', 'avatar_2', 0, '2026-02-16 16:31:52', 1);
INSERT INTO `user` VALUES (3, 'admin', 'admin', '系统管理员', '13800138003', 'avatar_3', 1, '2026-02-16 16:31:52', 1);
INSERT INTO `user` VALUES (4, 'wangwu', '123456', '美食家小王', '13800138004', 'avatar_4', 0, '2026-02-16 23:36:34', 1);
INSERT INTO `user` VALUES (5, 'qianliu', '123456', '更新后的昵称', '13800138006', 'avatar_5', 0, '2026-02-16 23:42:04', 1);
INSERT INTO `user` VALUES (6, 'gun', '222222', '222222', '15522222222', NULL, 0, '2026-02-19 16:14:40', 0);

-- ----------------------------
-- Table structure for user_behavior
-- ----------------------------
DROP TABLE IF EXISTS `user_behavior`;
CREATE TABLE `user_behavior`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `recipe_id` bigint(20) NOT NULL,
  `behavior_type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'VIEW/CLICK/COLLECT/SHARE',
  `duration_seconds` int(11) NULL DEFAULT NULL COMMENT '停留时长（秒）',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_user_time`(`user_id`, `created_at`) USING BTREE,
  INDEX `idx_recipe`(`recipe_id`) USING BTREE,
  CONSTRAINT `user_behavior_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_behavior_ibfk_2` FOREIGN KEY (`recipe_id`) REFERENCES `recipe` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 293 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_behavior
-- ----------------------------
INSERT INTO `user_behavior` VALUES (1, 1, 1, 'VIEW', 45, '2026-02-14 16:31:53');
INSERT INTO `user_behavior` VALUES (2, 1, 1, 'COLLECT', NULL, '2026-02-14 16:31:53');
INSERT INTO `user_behavior` VALUES (3, 1, 3, 'VIEW', 60, '2026-02-15 16:31:53');
INSERT INTO `user_behavior` VALUES (4, 1, 3, 'CLICK', 120, '2026-02-15 16:31:53');
INSERT INTO `user_behavior` VALUES (5, 1, 5, 'VIEW', 30, '2026-02-13 16:31:53');
INSERT INTO `user_behavior` VALUES (7, 1, 8, 'VIEW', 90, '2026-02-16 16:31:53');
INSERT INTO `user_behavior` VALUES (8, 1, 8, 'CLICK', 180, '2026-02-16 16:31:53');
INSERT INTO `user_behavior` VALUES (9, 2, 2, 'VIEW', 40, '2026-02-14 16:31:53');
INSERT INTO `user_behavior` VALUES (10, 2, 2, 'COLLECT', NULL, '2026-02-14 16:31:53');
INSERT INTO `user_behavior` VALUES (11, 2, 4, 'VIEW', 35, '2026-02-15 16:31:53');
INSERT INTO `user_behavior` VALUES (12, 2, 6, 'VIEW', 25, '2026-02-15 16:31:53');
INSERT INTO `user_behavior` VALUES (13, 2, 6, 'CLICK', 60, '2026-02-15 16:31:53');
INSERT INTO `user_behavior` VALUES (14, 2, 9, 'VIEW', 20, '2026-02-16 16:31:53');
INSERT INTO `user_behavior` VALUES (15, 2, 9, 'COLLECT', NULL, '2026-02-16 16:31:53');
INSERT INTO `user_behavior` VALUES (16, 1, 1, 'VIEW', 0, '2026-02-17 20:23:09');
INSERT INTO `user_behavior` VALUES (17, 1, 2, 'COLLECT', 0, '2026-02-17 21:05:52');
INSERT INTO `user_behavior` VALUES (18, 1, 3, 'VIEW', 30, '2026-02-17 21:25:05');
INSERT INTO `user_behavior` VALUES (19, 1, 3, 'CLICK', 0, '2026-02-17 21:26:49');
INSERT INTO `user_behavior` VALUES (21, 1, 1, 'VIEW', 0, '2026-02-18 15:49:07');
INSERT INTO `user_behavior` VALUES (22, 1, 5, 'COLLECT', 0, '2026-02-18 16:14:15');
INSERT INTO `user_behavior` VALUES (23, 1, 2, 'UNCOLLECT', 0, '2026-02-18 16:19:41');
INSERT INTO `user_behavior` VALUES (24, 1, 2, 'COLLECT', 0, '2026-02-18 16:21:02');
INSERT INTO `user_behavior` VALUES (25, 1, 2, 'UNCOLLECT', 0, '2026-02-18 16:24:08');
INSERT INTO `user_behavior` VALUES (26, 1, 2, 'COLLECT', 0, '2026-02-18 16:24:31');
INSERT INTO `user_behavior` VALUES (27, 1, 2, 'DISLIKE', 0, '2026-02-18 16:47:51');
INSERT INTO `user_behavior` VALUES (28, 1, 2, 'UNCOLLECT', 0, '2026-02-18 16:55:45');
INSERT INTO `user_behavior` VALUES (29, 1, 2, 'COLLECT', 0, '2026-02-18 16:56:02');
INSERT INTO `user_behavior` VALUES (30, 1, 2, 'DISLIKE', 0, '2026-02-18 16:56:32');
INSERT INTO `user_behavior` VALUES (31, 1, 13, 'VIEW', 0, '2026-02-18 21:03:45');
INSERT INTO `user_behavior` VALUES (32, 1, 13, 'VIEW', 0, '2026-02-18 21:26:37');
INSERT INTO `user_behavior` VALUES (33, 1, 3, 'COLLECT', 0, '2026-02-18 21:38:33');
INSERT INTO `user_behavior` VALUES (34, 1, 2, 'VIEW', 0, '2026-02-19 11:55:02');
INSERT INTO `user_behavior` VALUES (35, 1, 2, 'VIEW', 0, '2026-02-19 11:55:02');
INSERT INTO `user_behavior` VALUES (36, 1, 2, 'VIEW', 45, '2026-02-19 11:55:44');
INSERT INTO `user_behavior` VALUES (37, 1, 3, 'VIEW', 0, '2026-02-19 11:56:53');
INSERT INTO `user_behavior` VALUES (38, 1, 3, 'VIEW', 0, '2026-02-19 11:56:53');
INSERT INTO `user_behavior` VALUES (39, 1, 3, 'VIEW', 10, '2026-02-19 11:56:59');
INSERT INTO `user_behavior` VALUES (40, 1, 13, 'VIEW', 0, '2026-02-19 12:09:10');
INSERT INTO `user_behavior` VALUES (41, 1, 12, 'VIEW', 0, '2026-02-19 12:11:25');
INSERT INTO `user_behavior` VALUES (42, 1, 7, 'VIEW', 0, '2026-02-19 12:55:55');
INSERT INTO `user_behavior` VALUES (43, 1, 7, 'VIEW', 0, '2026-02-19 12:55:55');
INSERT INTO `user_behavior` VALUES (44, 1, 7, 'VIEW', 160, '2026-02-19 12:58:35');
INSERT INTO `user_behavior` VALUES (45, 1, 3, 'VIEW', 0, '2026-02-19 12:58:39');
INSERT INTO `user_behavior` VALUES (46, 1, 3, 'VIEW', 0, '2026-02-19 12:58:39');
INSERT INTO `user_behavior` VALUES (47, 1, 3, 'VIEW', 10, '2026-02-19 12:58:47');
INSERT INTO `user_behavior` VALUES (48, 1, 10, 'VIEW', 0, '2026-02-19 12:58:56');
INSERT INTO `user_behavior` VALUES (49, 1, 10, 'VIEW', 0, '2026-02-19 12:58:56');
INSERT INTO `user_behavior` VALUES (50, 1, 10, 'VIEW', 5, '2026-02-19 12:58:59');
INSERT INTO `user_behavior` VALUES (51, 1, 10, 'VIEW', 0, '2026-02-19 12:59:01');
INSERT INTO `user_behavior` VALUES (52, 1, 10, 'VIEW', 0, '2026-02-19 12:59:01');
INSERT INTO `user_behavior` VALUES (53, 1, 10, 'VIEW', 10, '2026-02-19 12:59:09');
INSERT INTO `user_behavior` VALUES (54, 1, 10, 'VIEW', 0, '2026-02-19 13:00:36');
INSERT INTO `user_behavior` VALUES (55, 1, 10, 'VIEW', 0, '2026-02-19 13:00:36');
INSERT INTO `user_behavior` VALUES (56, 1, 10, 'VIEW', 10, '2026-02-19 13:00:43');
INSERT INTO `user_behavior` VALUES (57, 1, 2, 'VIEW', 0, '2026-02-19 13:08:18');
INSERT INTO `user_behavior` VALUES (58, 1, 2, 'VIEW', 0, '2026-02-19 13:08:18');
INSERT INTO `user_behavior` VALUES (59, 1, 2, 'VIEW', 5, '2026-02-19 13:08:23');
INSERT INTO `user_behavior` VALUES (60, 1, 3, 'VIEW', 0, '2026-02-19 13:08:29');
INSERT INTO `user_behavior` VALUES (61, 1, 3, 'VIEW', 0, '2026-02-19 13:08:29');
INSERT INTO `user_behavior` VALUES (62, 1, 3, 'VIEW', 10, '2026-02-19 13:08:37');
INSERT INTO `user_behavior` VALUES (63, 1, 3, 'VIEW', 0, '2026-02-19 13:10:05');
INSERT INTO `user_behavior` VALUES (64, 1, 3, 'VIEW', 0, '2026-02-19 13:10:05');
INSERT INTO `user_behavior` VALUES (65, 1, 3, 'VIEW', 0, '2026-02-19 13:10:11');
INSERT INTO `user_behavior` VALUES (66, 1, 3, 'VIEW', 0, '2026-02-19 13:10:26');
INSERT INTO `user_behavior` VALUES (67, 1, 3, 'VIEW', 45, '2026-02-19 13:10:45');
INSERT INTO `user_behavior` VALUES (68, 1, 7, 'VIEW', 0, '2026-02-19 13:10:48');
INSERT INTO `user_behavior` VALUES (69, 1, 7, 'VIEW', 0, '2026-02-19 13:10:48');
INSERT INTO `user_behavior` VALUES (70, 1, 7, 'COLLECT', 0, '2026-02-19 13:10:50');
INSERT INTO `user_behavior` VALUES (71, 1, 7, 'COLLECT', 0, '2026-02-19 13:10:50');
INSERT INTO `user_behavior` VALUES (72, 1, 7, 'VIEW', 15, '2026-02-19 13:11:00');
INSERT INTO `user_behavior` VALUES (73, 1, 7, 'UNCOLLECT', 0, '2026-02-19 13:23:12');
INSERT INTO `user_behavior` VALUES (74, 1, 5, 'UNCOLLECT', 0, '2026-02-19 13:24:11');
INSERT INTO `user_behavior` VALUES (75, 1, 2, 'UNCOLLECT', 0, '2026-02-19 13:24:23');
INSERT INTO `user_behavior` VALUES (76, 1, 2, 'VIEW', 0, '2026-02-19 13:40:31');
INSERT INTO `user_behavior` VALUES (77, 1, 2, 'VIEW', 0, '2026-02-19 13:40:31');
INSERT INTO `user_behavior` VALUES (78, 1, 2, 'COLLECT', 0, '2026-02-19 13:40:34');
INSERT INTO `user_behavior` VALUES (79, 1, 2, 'COLLECT', 0, '2026-02-19 13:40:34');
INSERT INTO `user_behavior` VALUES (80, 1, 2, 'VIEW', 5, '2026-02-19 13:40:35');
INSERT INTO `user_behavior` VALUES (81, 1, 5, 'VIEW', 0, '2026-02-19 13:40:53');
INSERT INTO `user_behavior` VALUES (82, 1, 5, 'VIEW', 0, '2026-02-19 13:40:53');
INSERT INTO `user_behavior` VALUES (83, 1, 5, 'COLLECT', 0, '2026-02-19 13:40:55');
INSERT INTO `user_behavior` VALUES (84, 1, 5, 'COLLECT', 0, '2026-02-19 13:40:55');
INSERT INTO `user_behavior` VALUES (85, 1, 5, 'VIEW', 5, '2026-02-19 13:40:56');
INSERT INTO `user_behavior` VALUES (86, 1, 7, 'VIEW', 0, '2026-02-19 13:41:10');
INSERT INTO `user_behavior` VALUES (87, 1, 7, 'VIEW', 0, '2026-02-19 13:41:10');
INSERT INTO `user_behavior` VALUES (88, 1, 7, 'COLLECT', 0, '2026-02-19 13:41:11');
INSERT INTO `user_behavior` VALUES (89, 1, 7, 'COLLECT', 0, '2026-02-19 13:41:11');
INSERT INTO `user_behavior` VALUES (90, 1, 7, 'VIEW', 10, '2026-02-19 13:41:15');
INSERT INTO `user_behavior` VALUES (91, 1, 2, 'VIEW', 0, '2026-02-19 13:44:58');
INSERT INTO `user_behavior` VALUES (92, 1, 2, 'VIEW', 0, '2026-02-19 13:44:58');
INSERT INTO `user_behavior` VALUES (93, 1, 2, 'VIEW', 30, '2026-02-19 13:45:24');
INSERT INTO `user_behavior` VALUES (94, 1, 7, 'VIEW', 0, '2026-02-19 13:54:56');
INSERT INTO `user_behavior` VALUES (95, 1, 7, 'VIEW', 0, '2026-02-19 13:54:56');
INSERT INTO `user_behavior` VALUES (96, 1, 7, 'VIEW', 5, '2026-02-19 13:54:58');
INSERT INTO `user_behavior` VALUES (97, 1, 3, 'VIEW', 0, '2026-02-19 13:54:59');
INSERT INTO `user_behavior` VALUES (98, 1, 3, 'VIEW', 0, '2026-02-19 13:54:59');
INSERT INTO `user_behavior` VALUES (99, 1, 3, 'VIEW', 5, '2026-02-19 13:55:02');
INSERT INTO `user_behavior` VALUES (100, 1, 6, 'VIEW', 0, '2026-02-19 13:55:03');
INSERT INTO `user_behavior` VALUES (101, 1, 6, 'VIEW', 0, '2026-02-19 13:55:03');
INSERT INTO `user_behavior` VALUES (102, 1, 6, 'COLLECT', 0, '2026-02-19 13:55:13');
INSERT INTO `user_behavior` VALUES (103, 1, 6, 'COLLECT', 0, '2026-02-19 13:55:13');
INSERT INTO `user_behavior` VALUES (104, 1, 6, 'VIEW', 15, '2026-02-19 13:55:15');
INSERT INTO `user_behavior` VALUES (105, 1, 13, 'VIEW', 0, '2026-02-19 13:57:53');
INSERT INTO `user_behavior` VALUES (106, 1, 2, 'VIEW', 0, '2026-02-19 14:35:50');
INSERT INTO `user_behavior` VALUES (107, 1, 2, 'VIEW', 0, '2026-02-19 14:35:50');
INSERT INTO `user_behavior` VALUES (108, 1, 2, 'VIEW', 160, '2026-02-19 14:38:25');
INSERT INTO `user_behavior` VALUES (109, 1, 2, 'VIEW', 0, '2026-02-19 14:38:27');
INSERT INTO `user_behavior` VALUES (110, 1, 2, 'VIEW', 0, '2026-02-19 14:38:27');
INSERT INTO `user_behavior` VALUES (111, 1, 2, 'VIEW', 250, '2026-02-19 14:42:33');
INSERT INTO `user_behavior` VALUES (112, 1, 2, 'VIEW', 0, '2026-02-19 14:42:36');
INSERT INTO `user_behavior` VALUES (113, 1, 2, 'VIEW', 0, '2026-02-19 14:42:36');
INSERT INTO `user_behavior` VALUES (114, 1, 2, 'VIEW', 205, '2026-02-19 14:45:59');
INSERT INTO `user_behavior` VALUES (115, 1, 2, 'VIEW', 0, '2026-02-19 14:52:42');
INSERT INTO `user_behavior` VALUES (116, 1, 2, 'VIEW', 0, '2026-02-19 14:52:42');
INSERT INTO `user_behavior` VALUES (117, 1, 2, 'VIEW', 5, '2026-02-19 14:52:45');
INSERT INTO `user_behavior` VALUES (118, 1, 5, 'VIEW', 0, '2026-02-19 14:52:46');
INSERT INTO `user_behavior` VALUES (119, 1, 5, 'VIEW', 0, '2026-02-19 14:52:46');
INSERT INTO `user_behavior` VALUES (120, 1, 5, 'VIEW', 5, '2026-02-19 14:52:48');
INSERT INTO `user_behavior` VALUES (121, 1, 7, 'VIEW', 0, '2026-02-19 14:52:50');
INSERT INTO `user_behavior` VALUES (122, 1, 7, 'VIEW', 0, '2026-02-19 14:52:50');
INSERT INTO `user_behavior` VALUES (123, 1, 7, 'VIEW', 5, '2026-02-19 14:52:55');
INSERT INTO `user_behavior` VALUES (124, 1, 13, 'VIEW', 0, '2026-02-19 14:57:08');
INSERT INTO `user_behavior` VALUES (125, 1, 13, 'VIEW', 0, '2026-02-19 14:57:08');
INSERT INTO `user_behavior` VALUES (126, 1, 13, 'VIEW', 35, '2026-02-19 14:57:40');
INSERT INTO `user_behavior` VALUES (127, 1, 7, 'VIEW', 0, '2026-02-19 15:00:33');
INSERT INTO `user_behavior` VALUES (128, 1, 7, 'VIEW', 0, '2026-02-19 15:00:33');
INSERT INTO `user_behavior` VALUES (129, 1, 7, 'VIEW', 5, '2026-02-19 15:00:36');
INSERT INTO `user_behavior` VALUES (130, 1, 13, 'VIEW', 0, '2026-02-19 15:02:55');
INSERT INTO `user_behavior` VALUES (131, 1, 13, 'VIEW', 0, '2026-02-19 15:02:55');
INSERT INTO `user_behavior` VALUES (132, 1, 13, 'VIEW', 0, '2026-02-19 15:03:00');
INSERT INTO `user_behavior` VALUES (133, 1, 2, 'VIEW', 0, '2026-02-19 15:10:04');
INSERT INTO `user_behavior` VALUES (134, 1, 2, 'VIEW', 0, '2026-02-19 15:10:04');
INSERT INTO `user_behavior` VALUES (135, 1, 2, 'VIEW', 10, '2026-02-19 15:10:12');
INSERT INTO `user_behavior` VALUES (136, 1, 13, 'VIEW', 0, '2026-02-19 15:10:17');
INSERT INTO `user_behavior` VALUES (137, 1, 13, 'VIEW', 0, '2026-02-19 15:10:17');
INSERT INTO `user_behavior` VALUES (138, 1, 13, 'VIEW', 10, '2026-02-19 15:10:22');
INSERT INTO `user_behavior` VALUES (139, 1, 14, 'VIEW', 0, '2026-02-19 15:10:26');
INSERT INTO `user_behavior` VALUES (140, 1, 14, 'VIEW', 0, '2026-02-19 15:10:26');
INSERT INTO `user_behavior` VALUES (141, 1, 14, 'VIEW', 5, '2026-02-19 15:10:28');
INSERT INTO `user_behavior` VALUES (142, 1, 7, 'VIEW', 0, '2026-02-19 15:10:32');
INSERT INTO `user_behavior` VALUES (143, 1, 7, 'VIEW', 0, '2026-02-19 15:10:32');
INSERT INTO `user_behavior` VALUES (144, 1, 7, 'VIEW', 5, '2026-02-19 15:10:35');
INSERT INTO `user_behavior` VALUES (145, 1, 2, 'VIEW', 0, '2026-02-19 15:10:50');
INSERT INTO `user_behavior` VALUES (146, 1, 2, 'VIEW', 0, '2026-02-19 15:10:50');
INSERT INTO `user_behavior` VALUES (147, 1, 2, 'VIEW', 110, '2026-02-19 15:12:38');
INSERT INTO `user_behavior` VALUES (148, 1, 5, 'VIEW', 0, '2026-02-19 15:12:40');
INSERT INTO `user_behavior` VALUES (149, 1, 5, 'VIEW', 0, '2026-02-19 15:12:40');
INSERT INTO `user_behavior` VALUES (150, 1, 5, 'VIEW', 5, '2026-02-19 15:12:45');
INSERT INTO `user_behavior` VALUES (151, 1, 5, 'VIEW', 0, '2026-02-19 15:12:47');
INSERT INTO `user_behavior` VALUES (152, 1, 5, 'VIEW', 0, '2026-02-19 15:12:47');
INSERT INTO `user_behavior` VALUES (153, 1, 5, 'VIEW', 15, '2026-02-19 15:12:57');
INSERT INTO `user_behavior` VALUES (154, 1, 2, 'VIEW', 0, '2026-02-19 15:14:17');
INSERT INTO `user_behavior` VALUES (155, 1, 2, 'VIEW', 0, '2026-02-19 15:14:17');
INSERT INTO `user_behavior` VALUES (156, 1, 2, 'VIEW', 10, '2026-02-19 15:14:24');
INSERT INTO `user_behavior` VALUES (157, 1, 7, 'VIEW', 0, '2026-02-19 15:14:27');
INSERT INTO `user_behavior` VALUES (158, 1, 7, 'VIEW', 0, '2026-02-19 15:14:27');
INSERT INTO `user_behavior` VALUES (159, 1, 7, 'VIEW', 5, '2026-02-19 15:14:30');
INSERT INTO `user_behavior` VALUES (160, 1, 2, 'VIEW', 0, '2026-02-19 15:15:14');
INSERT INTO `user_behavior` VALUES (161, 1, 2, 'VIEW', 0, '2026-02-19 15:15:14');
INSERT INTO `user_behavior` VALUES (162, 1, 2, 'VIEW', 125, '2026-02-19 15:17:17');
INSERT INTO `user_behavior` VALUES (163, 1, 7, 'VIEW', 0, '2026-02-19 15:17:23');
INSERT INTO `user_behavior` VALUES (164, 1, 7, 'VIEW', 0, '2026-02-19 15:17:23');
INSERT INTO `user_behavior` VALUES (165, 1, 7, 'VIEW', 5, '2026-02-19 15:17:28');
INSERT INTO `user_behavior` VALUES (166, 1, 5, 'VIEW', 0, '2026-02-19 15:18:13');
INSERT INTO `user_behavior` VALUES (167, 1, 5, 'VIEW', 0, '2026-02-19 15:18:13');
INSERT INTO `user_behavior` VALUES (168, 1, 5, 'VIEW', 5, '2026-02-19 15:18:17');
INSERT INTO `user_behavior` VALUES (169, 1, 5, 'VIEW', 0, '2026-02-19 15:18:37');
INSERT INTO `user_behavior` VALUES (170, 1, 5, 'VIEW', 0, '2026-02-19 15:18:37');
INSERT INTO `user_behavior` VALUES (171, 1, 5, 'VIEW', 10, '2026-02-19 15:18:47');
INSERT INTO `user_behavior` VALUES (172, 1, 7, 'VIEW', 0, '2026-02-19 15:24:01');
INSERT INTO `user_behavior` VALUES (173, 1, 7, 'VIEW', 0, '2026-02-19 15:24:01');
INSERT INTO `user_behavior` VALUES (174, 1, 7, 'VIEW', 10, '2026-02-19 15:24:07');
INSERT INTO `user_behavior` VALUES (175, 1, 1, 'VIEW', 0, '2026-02-19 15:24:08');
INSERT INTO `user_behavior` VALUES (176, 1, 1, 'VIEW', 0, '2026-02-19 15:24:08');
INSERT INTO `user_behavior` VALUES (177, 1, 1, 'VIEW', 5, '2026-02-19 15:24:12');
INSERT INTO `user_behavior` VALUES (178, 1, 4, 'VIEW', 0, '2026-02-19 15:24:12');
INSERT INTO `user_behavior` VALUES (179, 1, 4, 'VIEW', 0, '2026-02-19 15:24:12');
INSERT INTO `user_behavior` VALUES (180, 1, 4, 'VIEW', 10, '2026-02-19 15:24:19');
INSERT INTO `user_behavior` VALUES (181, 1, 2, 'VIEW', 0, '2026-02-19 15:24:47');
INSERT INTO `user_behavior` VALUES (182, 1, 2, 'VIEW', 0, '2026-02-19 15:24:47');
INSERT INTO `user_behavior` VALUES (183, 1, 2, 'VIEW', 110, '2026-02-19 15:26:35');
INSERT INTO `user_behavior` VALUES (184, 1, 5, 'VIEW', 0, '2026-02-19 15:26:42');
INSERT INTO `user_behavior` VALUES (185, 1, 5, 'VIEW', 0, '2026-02-19 15:26:42');
INSERT INTO `user_behavior` VALUES (186, 1, 5, 'VIEW', 15, '2026-02-19 15:26:53');
INSERT INTO `user_behavior` VALUES (187, 1, 7, 'VIEW', 0, '2026-02-19 15:26:53');
INSERT INTO `user_behavior` VALUES (188, 1, 7, 'VIEW', 0, '2026-02-19 15:26:53');
INSERT INTO `user_behavior` VALUES (189, 1, 7, 'VIEW', 5, '2026-02-19 15:26:57');
INSERT INTO `user_behavior` VALUES (190, 1, 13, 'VIEW', 0, '2026-02-19 15:27:35');
INSERT INTO `user_behavior` VALUES (191, 1, 13, 'VIEW', 0, '2026-02-19 15:27:35');
INSERT INTO `user_behavior` VALUES (192, 1, 13, 'VIEW', 10, '2026-02-19 15:27:42');
INSERT INTO `user_behavior` VALUES (193, 1, 13, 'VIEW', 0, '2026-02-19 15:27:55');
INSERT INTO `user_behavior` VALUES (194, 1, 13, 'VIEW', 0, '2026-02-19 15:27:55');
INSERT INTO `user_behavior` VALUES (195, 1, 13, 'VIEW', 0, '2026-02-19 15:28:00');
INSERT INTO `user_behavior` VALUES (196, 1, 13, 'VIEW', 0, '2026-02-19 15:28:08');
INSERT INTO `user_behavior` VALUES (197, 1, 13, 'VIEW', 0, '2026-02-19 15:31:01');
INSERT INTO `user_behavior` VALUES (198, 1, 13, 'VIEW', 235, '2026-02-19 15:31:47');
INSERT INTO `user_behavior` VALUES (199, 1, 13, 'VIEW', 0, '2026-02-19 15:31:49');
INSERT INTO `user_behavior` VALUES (200, 1, 13, 'VIEW', 0, '2026-02-19 15:31:49');
INSERT INTO `user_behavior` VALUES (201, 1, 13, 'VIEW', 0, '2026-02-19 15:31:56');
INSERT INTO `user_behavior` VALUES (202, 1, 13, 'VIEW', 0, '2026-02-19 15:32:35');
INSERT INTO `user_behavior` VALUES (203, 1, 13, 'VIEW', 70, '2026-02-19 15:32:58');
INSERT INTO `user_behavior` VALUES (204, 1, 13, 'VIEW', 0, '2026-02-19 15:33:03');
INSERT INTO `user_behavior` VALUES (205, 1, 13, 'VIEW', 0, '2026-02-19 15:33:03');
INSERT INTO `user_behavior` VALUES (206, 1, 13, 'VIEW', 75, '2026-02-19 15:34:17');
INSERT INTO `user_behavior` VALUES (207, 1, 13, 'VIEW', 0, '2026-02-19 15:34:19');
INSERT INTO `user_behavior` VALUES (208, 1, 13, 'VIEW', 0, '2026-02-19 15:34:19');
INSERT INTO `user_behavior` VALUES (209, 1, 13, 'VIEW', 0, '2026-02-19 15:34:43');
INSERT INTO `user_behavior` VALUES (210, 1, 13, 'VIEW', 0, '2026-02-19 15:39:34');
INSERT INTO `user_behavior` VALUES (211, 1, 13, 'VIEW', 325, '2026-02-19 15:39:41');
INSERT INTO `user_behavior` VALUES (212, 1, 13, 'VIEW', 0, '2026-02-19 15:39:44');
INSERT INTO `user_behavior` VALUES (213, 1, 13, 'VIEW', 0, '2026-02-19 15:39:44');
INSERT INTO `user_behavior` VALUES (214, 1, 13, 'VIEW', 0, '2026-02-19 15:39:50');
INSERT INTO `user_behavior` VALUES (215, 1, 13, 'VIEW', 0, '2026-02-19 15:40:01');
INSERT INTO `user_behavior` VALUES (216, 1, 13, 'VIEW', 0, '2026-02-19 15:43:36');
INSERT INTO `user_behavior` VALUES (217, 1, 13, 'VIEW', 0, '2026-02-19 15:43:36');
INSERT INTO `user_behavior` VALUES (218, 1, 13, 'VIEW', 0, '2026-02-19 15:43:42');
INSERT INTO `user_behavior` VALUES (219, 1, 13, 'VIEW', 0, '2026-02-19 15:50:44');
INSERT INTO `user_behavior` VALUES (220, 1, 13, 'VIEW', 0, '2026-02-19 15:50:44');
INSERT INTO `user_behavior` VALUES (221, 1, 13, 'VIEW', 0, '2026-02-19 15:50:46');
INSERT INTO `user_behavior` VALUES (222, 1, 13, 'VIEW', 0, '2026-02-19 15:50:57');
INSERT INTO `user_behavior` VALUES (223, 1, 13, 'VIEW', 40, '2026-02-19 15:51:20');
INSERT INTO `user_behavior` VALUES (224, 1, 13, 'VIEW', 0, '2026-02-19 15:51:21');
INSERT INTO `user_behavior` VALUES (225, 1, 13, 'VIEW', 0, '2026-02-19 15:51:21');
INSERT INTO `user_behavior` VALUES (226, 1, 13, 'VIEW', 20, '2026-02-19 15:51:38');
INSERT INTO `user_behavior` VALUES (227, 1, 7, 'VIEW', 0, '2026-02-19 15:52:49');
INSERT INTO `user_behavior` VALUES (228, 1, 7, 'VIEW', 0, '2026-02-19 15:52:49');
INSERT INTO `user_behavior` VALUES (229, 1, 7, 'VIEW', 5, '2026-02-19 15:52:53');
INSERT INTO `user_behavior` VALUES (230, 1, 2, 'VIEW', 0, '2026-02-19 15:53:19');
INSERT INTO `user_behavior` VALUES (231, 1, 2, 'VIEW', 0, '2026-02-19 15:53:19');
INSERT INTO `user_behavior` VALUES (232, 1, 2, 'VIEW', 35, '2026-02-19 15:53:50');
INSERT INTO `user_behavior` VALUES (233, 1, 4, 'VIEW', 0, '2026-02-19 15:54:12');
INSERT INTO `user_behavior` VALUES (234, 1, 4, 'VIEW', 0, '2026-02-19 15:54:12');
INSERT INTO `user_behavior` VALUES (235, 1, 4, 'VIEW', 0, '2026-02-19 15:54:16');
INSERT INTO `user_behavior` VALUES (236, 1, 4, 'VIEW', 0, '2026-02-19 15:54:58');
INSERT INTO `user_behavior` VALUES (237, 1, 4, 'VIEW', 120, '2026-02-19 15:56:07');
INSERT INTO `user_behavior` VALUES (238, 1, 5, 'VIEW', 0, '2026-02-19 15:56:34');
INSERT INTO `user_behavior` VALUES (239, 1, 5, 'VIEW', 0, '2026-02-19 15:56:34');
INSERT INTO `user_behavior` VALUES (240, 1, 5, 'VIEW', 0, '2026-02-19 15:56:37');
INSERT INTO `user_behavior` VALUES (241, 1, 5, 'VIEW', 0, '2026-02-19 15:57:24');
INSERT INTO `user_behavior` VALUES (242, 1, 5, 'VIEW', 0, '2026-02-19 15:57:24');
INSERT INTO `user_behavior` VALUES (243, 1, 5, 'VIEW', 0, '2026-02-19 15:57:27');
INSERT INTO `user_behavior` VALUES (244, 1, 5, 'VIEW', 0, '2026-02-19 15:58:14');
INSERT INTO `user_behavior` VALUES (245, 1, 5, 'VIEW', 0, '2026-02-19 15:58:14');
INSERT INTO `user_behavior` VALUES (246, 1, 5, 'VIEW', 0, '2026-02-19 15:58:17');
INSERT INTO `user_behavior` VALUES (247, 1, 5, 'VIEW', 15, '2026-02-19 15:58:29');
INSERT INTO `user_behavior` VALUES (248, 6, 10, 'VIEW', 0, '2026-02-19 16:21:13');
INSERT INTO `user_behavior` VALUES (249, 6, 10, 'VIEW', 0, '2026-02-19 16:21:13');
INSERT INTO `user_behavior` VALUES (250, 6, 10, 'VIEW', 10, '2026-02-19 16:21:20');
INSERT INTO `user_behavior` VALUES (251, 1, 2, 'VIEW', 0, '2026-02-19 16:24:34');
INSERT INTO `user_behavior` VALUES (252, 1, 2, 'VIEW', 0, '2026-02-19 16:24:34');
INSERT INTO `user_behavior` VALUES (253, 1, 2, 'VIEW', 10, '2026-02-19 16:24:42');
INSERT INTO `user_behavior` VALUES (254, 1, 2, 'VIEW', 0, '2026-02-19 16:30:28');
INSERT INTO `user_behavior` VALUES (255, 1, 2, 'VIEW', 0, '2026-02-19 16:30:28');
INSERT INTO `user_behavior` VALUES (256, 1, 2, 'VIEW', 130, '2026-02-19 16:32:33');
INSERT INTO `user_behavior` VALUES (257, 1, 2, 'VIEW', 0, '2026-02-19 16:32:38');
INSERT INTO `user_behavior` VALUES (258, 1, 2, 'VIEW', 0, '2026-02-19 16:32:38');
INSERT INTO `user_behavior` VALUES (259, 1, 2, 'VIEW', 0, '2026-02-19 16:33:04');
INSERT INTO `user_behavior` VALUES (260, 1, 2, 'VIEW', 400, '2026-02-19 16:39:14');
INSERT INTO `user_behavior` VALUES (261, 1, 2, 'VIEW', 0, '2026-02-19 16:39:34');
INSERT INTO `user_behavior` VALUES (262, 1, 2, 'VIEW', 0, '2026-02-19 16:39:34');
INSERT INTO `user_behavior` VALUES (263, 1, 2, 'VIEW', 0, '2026-02-19 16:39:41');
INSERT INTO `user_behavior` VALUES (264, 1, 2, 'VIEW', 0, '2026-02-19 16:44:39');
INSERT INTO `user_behavior` VALUES (265, 1, 2, 'VIEW', 0, '2026-02-19 16:44:40');
INSERT INTO `user_behavior` VALUES (266, 1, 2, 'VIEW', 0, '2026-02-19 16:44:45');
INSERT INTO `user_behavior` VALUES (267, 1, 2, 'VIEW', 0, '2026-02-19 16:44:45');
INSERT INTO `user_behavior` VALUES (268, 1, 2, 'VIEW', 5, '2026-02-19 16:44:48');
INSERT INTO `user_behavior` VALUES (269, 1, 2, 'VIEW', 0, '2026-02-19 16:44:49');
INSERT INTO `user_behavior` VALUES (270, 1, 2, 'VIEW', 0, '2026-02-19 16:44:49');
INSERT INTO `user_behavior` VALUES (271, 1, 2, 'VIEW', 0, '2026-02-19 16:48:21');
INSERT INTO `user_behavior` VALUES (272, 1, 2, 'VIEW', 0, '2026-02-19 16:48:21');
INSERT INTO `user_behavior` VALUES (273, 1, 2, 'VIEW', 20, '2026-02-19 16:48:36');
INSERT INTO `user_behavior` VALUES (274, 1, 5, 'VIEW', 0, '2026-02-19 16:48:40');
INSERT INTO `user_behavior` VALUES (275, 1, 5, 'VIEW', 0, '2026-02-19 16:48:40');
INSERT INTO `user_behavior` VALUES (276, 1, 5, 'VIEW', 5, '2026-02-19 16:48:42');
INSERT INTO `user_behavior` VALUES (277, 1, 2, 'VIEW', 0, '2026-02-19 16:51:02');
INSERT INTO `user_behavior` VALUES (278, 1, 2, 'VIEW', 0, '2026-02-19 16:51:02');
INSERT INTO `user_behavior` VALUES (279, 1, 2, 'VIEW', 145, '2026-02-19 16:53:24');
INSERT INTO `user_behavior` VALUES (280, 1, 2, 'VIEW', 0, '2026-02-19 16:53:24');
INSERT INTO `user_behavior` VALUES (281, 1, 2, 'VIEW', 0, '2026-02-19 16:53:24');
INSERT INTO `user_behavior` VALUES (282, 1, 2, 'VIEW', 15, '2026-02-19 16:53:37');
INSERT INTO `user_behavior` VALUES (283, 1, 1, 'VIEW', 0, '2026-02-19 16:59:17');
INSERT INTO `user_behavior` VALUES (284, 1, 1, 'VIEW', 0, '2026-02-19 16:59:17');
INSERT INTO `user_behavior` VALUES (285, 1, 1, 'VIEW', 10, '2026-02-19 16:59:23');
INSERT INTO `user_behavior` VALUES (286, 3, 11, 'VIEW', 0, '2026-02-19 19:49:28');
INSERT INTO `user_behavior` VALUES (287, 3, 11, 'VIEW', 0, '2026-02-19 19:49:39');
INSERT INTO `user_behavior` VALUES (288, 3, 11, 'VIEW', 0, '2026-02-19 19:51:45');
INSERT INTO `user_behavior` VALUES (289, 3, 11, 'VIEW', 0, '2026-02-19 19:51:49');
INSERT INTO `user_behavior` VALUES (290, 3, 11, 'VIEW', 0, '2026-02-19 19:51:55');
INSERT INTO `user_behavior` VALUES (291, 3, 11, 'VIEW', 0, '2026-02-19 19:53:30');
INSERT INTO `user_behavior` VALUES (292, 3, 13, 'VIEW', 0, '2026-02-19 19:54:46');

-- ----------------------------
-- Table structure for user_favorite
-- ----------------------------
DROP TABLE IF EXISTS `user_favorite`;
CREATE TABLE `user_favorite`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `recipe_id` bigint(20) NOT NULL,
  `folder_name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT '默认收藏夹',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  `status` int(11) NULL DEFAULT NULL COMMENT '0-正常 1-软删',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_recipe`(`user_id`, `recipe_id`) USING BTREE,
  INDEX `idx_user`(`user_id`, `created_at`) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_favorite
-- ----------------------------
INSERT INTO `user_favorite` VALUES (1, 1, 2, '默认收藏夹', '2026-02-17 21:05:52', '2026-02-19 13:40:34', 0);
INSERT INTO `user_favorite` VALUES (2, 1, 5, '默认收藏夹', '2026-02-18 16:14:15', '2026-02-19 13:40:55', 0);
INSERT INTO `user_favorite` VALUES (3, 1, 3, '默认收藏夹', '2026-02-18 21:38:33', '2026-02-19 13:42:02', 0);
INSERT INTO `user_favorite` VALUES (4, 1, 7, '默认收藏夹', '2026-02-19 13:10:50', '2026-02-19 13:41:11', 0);
INSERT INTO `user_favorite` VALUES (5, 1, 6, '111', '2026-02-19 13:55:13', '2026-02-19 13:55:13', 0);

-- ----------------------------
-- Table structure for user_tag_preference
-- ----------------------------
DROP TABLE IF EXISTS `user_tag_preference`;
CREATE TABLE `user_tag_preference`  (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `user_id` bigint(20) NOT NULL,
  `tag_id` int(11) NOT NULL,
  `score` decimal(6, 4) NULL DEFAULT 0.0000 COMMENT '累计权重分数',
  `positive_count` int(11) NULL DEFAULT 0 COMMENT '正向行为次数',
  `negative_count` int(11) NULL DEFAULT 0 COMMENT '负向行为次数',
  `last_interaction` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后交互时间',
  `created_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `updated_at` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_tag`(`user_id`, `tag_id`) USING BTREE,
  INDEX `idx_user_score`(`user_id`, `score`) USING BTREE,
  INDEX `tag_id`(`tag_id`) USING BTREE,
  CONSTRAINT `user_tag_preference_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `user_tag_preference_ibfk_2` FOREIGN KEY (`tag_id`) REFERENCES `tag` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 37 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_general_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_tag_preference
-- ----------------------------
INSERT INTO `user_tag_preference` VALUES (1, 1, 1, 4.0500, 63, 0, '2026-02-19 08:59:23', '2026-02-17 20:23:09', '2026-02-19 16:59:23');
INSERT INTO `user_tag_preference` VALUES (2, 1, 6, 0.4000, 8, 0, '2026-02-19 08:59:23', '2026-02-17 20:23:09', '2026-02-19 16:59:23');
INSERT INTO `user_tag_preference` VALUES (3, 1, 8, 2.0000, 28, 0, '2026-02-19 08:59:23', '2026-02-17 20:23:09', '2026-02-19 16:59:23');
INSERT INTO `user_tag_preference` VALUES (4, 1, 19, 0.4000, 8, 0, '2026-02-19 08:59:23', '2026-02-17 20:23:09', '2026-02-19 16:59:23');
INSERT INTO `user_tag_preference` VALUES (5, 1, 26, 4.2500, 55, 1, '2026-02-19 08:59:23', '2026-02-17 20:23:09', '2026-02-19 16:59:23');
INSERT INTO `user_tag_preference` VALUES (6, 1, 31, 0.8000, 16, 0, '2026-02-19 08:59:23', '2026-02-17 20:23:09', '2026-02-19 16:59:23');
INSERT INTO `user_tag_preference` VALUES (8, 1, 11, 13.1000, 158, 0, '2026-02-19 08:53:37', '2026-02-17 21:05:52', '2026-02-19 16:53:37');
INSERT INTO `user_tag_preference` VALUES (12, 1, 2, 1.6000, 20, 0, '2026-02-19 05:55:02', '2026-02-17 21:25:05', '2026-02-19 13:55:02');
INSERT INTO `user_tag_preference` VALUES (13, 1, 3, 5.0500, 59, 0, '2026-02-19 07:52:54', '2026-02-17 21:25:05', '2026-02-19 15:52:53');
INSERT INTO `user_tag_preference` VALUES (14, 1, 15, 7.5500, 109, 0, '2026-02-19 07:52:54', '2026-02-17 21:25:05', '2026-02-19 15:52:53');
INSERT INTO `user_tag_preference` VALUES (15, 1, 27, 2.0000, 28, 0, '2026-02-19 07:56:08', '2026-02-17 21:25:05', '2026-02-19 15:56:07');
INSERT INTO `user_tag_preference` VALUES (16, 1, 5, 3.8500, 47, 0, '2026-02-19 08:48:42', '2026-02-17 21:28:40', '2026-02-19 16:48:42');
INSERT INTO `user_tag_preference` VALUES (17, 1, 16, 3.4000, 38, 0, '2026-02-19 08:48:42', '2026-02-17 21:28:40', '2026-02-19 16:48:42');
INSERT INTO `user_tag_preference` VALUES (18, 1, 32, 3.4000, 38, 0, '2026-02-19 08:48:42', '2026-02-17 21:28:40', '2026-02-19 16:48:42');
INSERT INTO `user_tag_preference` VALUES (19, 1, 4, 6.2500, 78, 0, '2026-02-19 08:53:37', '2026-02-18 16:56:02', '2026-02-19 16:53:37');
INSERT INTO `user_tag_preference` VALUES (20, 1, 18, 5.1000, 73, 0, '2026-02-19 08:53:37', '2026-02-18 16:56:02', '2026-02-19 16:53:37');
INSERT INTO `user_tag_preference` VALUES (21, 1, 25, 12.2000, 167, 0, '2026-02-19 08:53:37', '2026-02-18 16:56:02', '2026-02-19 16:53:37');
INSERT INTO `user_tag_preference` VALUES (22, 1, 30, 9.7000, 117, 0, '2026-02-19 08:53:37', '2026-02-18 16:56:02', '2026-02-19 16:53:37');
INSERT INTO `user_tag_preference` VALUES (23, 1, 13, 0.4500, 9, 0, '2026-02-19 05:00:44', '2026-02-19 12:58:56', '2026-02-19 13:00:43');
INSERT INTO `user_tag_preference` VALUES (24, 1, 20, 1.6000, 14, 0, '2026-02-19 05:55:15', '2026-02-19 12:58:56', '2026-02-19 13:55:15');
INSERT INTO `user_tag_preference` VALUES (25, 1, 21, 0.4500, 9, 0, '2026-02-19 05:00:44', '2026-02-19 12:58:56', '2026-02-19 13:00:43');
INSERT INTO `user_tag_preference` VALUES (26, 1, 7, 0.4000, 8, 0, '2026-02-19 07:56:08', '2026-02-19 15:24:12', '2026-02-19 15:56:07');
INSERT INTO `user_tag_preference` VALUES (27, 1, 9, 0.4000, 8, 0, '2026-02-19 07:56:08', '2026-02-19 15:24:12', '2026-02-19 15:56:07');
INSERT INTO `user_tag_preference` VALUES (28, 1, 22, 0.4000, 8, 0, '2026-02-19 07:56:08', '2026-02-19 15:24:12', '2026-02-19 15:56:07');
INSERT INTO `user_tag_preference` VALUES (29, 6, 5, 0.1500, 3, 0, '2026-02-19 08:21:21', '2026-02-19 16:21:13', '2026-02-19 16:21:20');
INSERT INTO `user_tag_preference` VALUES (30, 6, 13, 0.1500, 3, 0, '2026-02-19 08:21:21', '2026-02-19 16:21:13', '2026-02-19 16:21:20');
INSERT INTO `user_tag_preference` VALUES (31, 6, 20, 0.1500, 3, 0, '2026-02-19 08:21:21', '2026-02-19 16:21:13', '2026-02-19 16:21:20');
INSERT INTO `user_tag_preference` VALUES (32, 6, 21, 0.1500, 3, 0, '2026-02-19 08:21:21', '2026-02-19 16:21:13', '2026-02-19 16:21:20');
INSERT INTO `user_tag_preference` VALUES (33, 6, 26, 0.1500, 3, 0, '2026-02-19 08:21:21', '2026-02-19 16:21:13', '2026-02-19 16:21:20');
INSERT INTO `user_tag_preference` VALUES (34, 3, 1, 0.0500, 1, 0, '2026-02-19 11:54:46', '2026-02-19 19:54:46', '2026-02-19 19:54:46');
INSERT INTO `user_tag_preference` VALUES (35, 3, 15, 0.0500, 1, 0, '2026-02-19 11:54:46', '2026-02-19 19:54:46', '2026-02-19 19:54:46');
INSERT INTO `user_tag_preference` VALUES (36, 3, 25, 0.0500, 1, 0, '2026-02-19 11:54:46', '2026-02-19 19:54:46', '2026-02-19 19:54:46');

SET FOREIGN_KEY_CHECKS = 1;
