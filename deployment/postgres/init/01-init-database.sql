/*
 Mall template database initialization script.
 Keeps the core catalog, cart, checkout, payment, refund, admin, and email tables
 with neutral demo data for local template validation.
 创建时间: 2025-07-16
*/


-- ----------------------------
-- 基础权限和用户表
-- ----------------------------

-- 后台用户表
DROP TABLE IF EXISTS ums_admin CASCADE;
CREATE TABLE ums_admin (
  id BIGSERIAL NOT NULL,
  username varchar(64) DEFAULT NULL,
  password varchar(64) DEFAULT NULL,
  icon varchar(500) DEFAULT NULL,
  email varchar(100) DEFAULT NULL,
  nick_name varchar(200) DEFAULT NULL,
  note varchar(500) DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  login_time TIMESTAMP DEFAULT NULL,
  status INTEGER DEFAULT '1',
  PRIMARY KEY (id)
);

-- 默认后台账号：admin / 123456；只读展示账号：demo_viewer / 123456
INSERT INTO ums_admin (id, username, password, icon, email, nick_name, note, create_time, login_time, status) VALUES
(1, 'admin', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', '/template-assets/avatar-demo.svg', 'admin@mall-template.test', 'Admin', 'Template administrator', '2026-06-30 00:00:00', NULL, 1),
(2, 'demo_viewer', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', '/template-assets/avatar-demo.svg', 'demo-viewer@mall-template.test', 'Demo Viewer', 'Read-only public demo account', '2026-06-30 00:00:00', NULL, 1);

-- 后台用户登录日志表
DROP TABLE IF EXISTS ums_admin_login_log CASCADE;
CREATE TABLE ums_admin_login_log (
  id BIGSERIAL NOT NULL,
  admin_id BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  ip varchar(64) DEFAULT NULL,
  address varchar(100) DEFAULT NULL,
  user_agent varchar(100) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 后台用户和权限关系表
DROP TABLE IF EXISTS ums_admin_permission_relation CASCADE;
CREATE TABLE ums_admin_permission_relation (
  id BIGSERIAL NOT NULL,
  admin_id BIGINT DEFAULT NULL,
  permission_id BIGINT DEFAULT NULL,
  type INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 后台用户和角色关系表
DROP TABLE IF EXISTS ums_admin_role_relation CASCADE;
CREATE TABLE ums_admin_role_relation (
  id BIGSERIAL NOT NULL,
  admin_id BIGINT DEFAULT NULL,
  role_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 为 admin 分配超级管理员角色，为 demo_viewer 分配只读展示角色
INSERT INTO ums_admin_role_relation VALUES (1, 1, 5), (2, 2, 8);

-- 前台用户表
DROP TABLE IF EXISTS ums_member CASCADE;
CREATE TABLE ums_member (
  id BIGSERIAL NOT NULL,
  member_level_id BIGINT DEFAULT NULL,
  username varchar(64) DEFAULT NULL,
  password varchar(64) DEFAULT NULL,
  nickname varchar(64) DEFAULT NULL,
  phone varchar(64) DEFAULT NULL,
  status INTEGER DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  icon varchar(500) DEFAULT NULL,
  gender INTEGER DEFAULT NULL,
  birthday date DEFAULT NULL,
  city varchar(64) DEFAULT NULL,
  job varchar(100) DEFAULT NULL,
  personalized_signature varchar(200) DEFAULT NULL,
  source_type INTEGER DEFAULT NULL,
  integration INTEGER DEFAULT NULL,
  growth INTEGER DEFAULT NULL,
  luckey_count INTEGER DEFAULT NULL,
  history_integration INTEGER DEFAULT NULL,
  email varchar(100) DEFAULT NULL,
  email_verified INTEGER DEFAULT '0',
  email_verification_token varchar(255) DEFAULT NULL,
  email_verification_expires TIMESTAMP DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT idx_username UNIQUE (username),
  CONSTRAINT idx_phone UNIQUE (phone)
);

-- 会员等级兼容表（模板默认不启用）
DROP TABLE IF EXISTS ums_member_level CASCADE;
CREATE TABLE ums_member_level (
  id BIGSERIAL NOT NULL,
  name varchar(100) DEFAULT NULL,
  growth_point INTEGER DEFAULT NULL,
  default_status INTEGER DEFAULT NULL,
  free_freight_point decimal(10,2) DEFAULT NULL,
  comment_growth_point INTEGER DEFAULT NULL,
  priviledge_free_freight INTEGER DEFAULT NULL,
  priviledge_sign_in INTEGER DEFAULT NULL,
  priviledge_comment INTEGER DEFAULT NULL,
  priviledge_promotion INTEGER DEFAULT NULL,
  priviledge_member_price INTEGER DEFAULT NULL,
  priviledge_birthday INTEGER DEFAULT NULL,
  note varchar(200) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 等级/积分相关业务在轻量模板中不作为默认功能启用，因此不写入等级种子数据。

-- 会员收货地址表
DROP TABLE IF EXISTS ums_member_receive_address CASCADE;
CREATE TABLE ums_member_receive_address (
  id BIGSERIAL NOT NULL,
  member_id BIGINT DEFAULT NULL,
  name varchar(100) DEFAULT NULL,
  phone_number varchar(64) DEFAULT NULL,
  default_status INTEGER DEFAULT NULL,
  post_code varchar(100) DEFAULT NULL,
  province varchar(100) DEFAULT NULL,
  city varchar(100) DEFAULT NULL,
  region varchar(100) DEFAULT NULL,
  detail_address varchar(128) DEFAULT NULL,
  country varchar(10) DEFAULT 'CN',
  country_code varchar(10) DEFAULT '+86',
  prefix_address varchar(255) DEFAULT '',
  PRIMARY KEY (id)
);

-- 默认前台账号：demo_user / 123456
INSERT INTO ums_member (
  id, member_level_id, username, password, nickname, phone, status, create_time,
  icon, gender, birthday, city, job, personalized_signature, source_type,
  integration, growth, luckey_count, history_integration, email, email_verified
) VALUES
(1, NULL, 'demo_user', '$2a$10$NZ5o7r2E.ayT2ZoxgjlI.eJ6OEYqjH7INR/F.mXDbjZJi9HF0YCVG', 'Demo User', '+15550100001', 1, '2026-06-30 00:00:00', '/template-assets/avatar-demo.svg', 0, NULL, 'San Francisco', 'Template evaluator', 'Default storefront demo account', 0, 0, 0, 0, 0, 'demo-user@mall-template.test', 1);

INSERT INTO ums_member_receive_address (
  id, member_id, name, phone_number, default_status, post_code, province, city,
  region, detail_address, country, country_code, prefix_address
) VALUES
(1, 1, 'Demo User', '5550100001', 1, '94107', 'California', 'San Francisco', 'SOMA', '100 Market Street', 'US', '+1', '');

-- 后台菜单表
DROP TABLE IF EXISTS ums_menu CASCADE;
CREATE TABLE ums_menu (
  id BIGSERIAL NOT NULL,
  parent_id BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  title varchar(100) DEFAULT NULL,
  level INTEGER DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  name varchar(100) DEFAULT NULL,
  icon varchar(200) DEFAULT NULL,
  hidden INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO ums_menu VALUES (1,0,'2020-02-02 14:50:36','商品',0,0,'pms','product',0),(2,1,'2020-02-02 14:51:50','商品列表',1,0,'product','product-list',0),(3,1,'2020-02-02 14:52:44','添加商品',1,0,'addProduct','product-add',0),(4,1,'2020-02-02 14:53:51','商品分类',1,0,'productCate','product-cate',0),(5,1,'2020-02-02 14:54:51','商品类型',1,0,'productAttr','product-attr',0),(6,1,'2020-02-02 14:56:29','品牌管理',1,0,'brand','product-brand',0),(7,0,'2020-02-02 16:54:07','订单',0,0,'oms','order',0),(8,7,'2020-02-02 16:55:18','订单列表',1,0,'order','product-list',0),(9,7,'2020-02-02 16:56:46','订单设置',1,0,'orderSetting','order-setting',0),(10,7,'2020-02-02 16:57:39','退货申请处理',1,0,'returnApply','order-return',0),(11,7,'2020-02-02 16:59:40','退货原因设置',1,0,'returnReason','order-return-reason',0),(12,0,'2020-02-04 16:18:00','营销',0,0,'sms','sms',0),(13,12,'2020-02-04 16:19:22','秒杀活动列表',1,0,'flash','sms-flash',0),(14,12,'2020-02-04 16:20:16','优惠券列表',1,0,'coupon','sms-coupon',0),(16,12,'2020-02-07 16:22:38','品牌推荐',1,0,'homeBrand','product-brand',0),(17,12,'2020-02-07 16:23:14','新品推荐',1,0,'homeNew','sms-new',0),(18,12,'2020-02-07 16:26:38','人气推荐',1,0,'homeHot','sms-hot',0),(19,12,'2020-02-07 16:28:16','专题推荐',1,0,'homeSubject','sms-subject',0),(20,12,'2020-02-07 16:28:42','广告列表',1,0,'homeAdvertise','sms-ad',0),(21,0,'2020-02-07 16:29:13','权限',0,0,'ums','ums',0),(22,21,'2020-02-07 16:29:51','用户列表',1,0,'admin','ums-admin',0),(23,21,'2020-02-07 16:30:13','角色列表',1,0,'role','ums-role',0),(24,21,'2020-02-07 16:30:53','菜单列表',1,0,'menu','ums-menu',0),(25,21,'2020-02-07 16:31:13','资源列表',1,0,'resource','ums-resource',0),(26,21,'2025-06-22 05:53:19','商城用户管理',1,0,'mallUser','user-list',0);

-- 后台用户权限表
DROP TABLE IF EXISTS ums_permission CASCADE;
CREATE TABLE ums_permission (
  id BIGSERIAL NOT NULL,
  pid BIGINT DEFAULT NULL,
  name varchar(100) DEFAULT NULL,
  value varchar(200) DEFAULT NULL,
  icon varchar(500) DEFAULT NULL,
  type INTEGER DEFAULT NULL,
  uri varchar(200) DEFAULT NULL,
  status INTEGER DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO ums_permission VALUES (1,0,'商品',NULL,NULL,0,NULL,1,'2018-09-29 16:15:14',0),(2,1,'商品列表','pms:product:read',NULL,1,'/pms/product/index',1,'2018-09-29 16:17:01',0),(3,1,'添加商品','pms:product:create',NULL,1,'/pms/product/add',1,'2018-09-29 16:18:51',0),(4,1,'商品分类','pms:productCategory:read',NULL,1,'/pms/productCate/index',1,'2018-09-29 16:23:07',0),(5,1,'商品类型','pms:productAttribute:read',NULL,1,'/pms/productAttr/index',1,'2018-09-29 16:24:43',0),(6,1,'品牌管理','pms:brand:read',NULL,1,'/pms/brand/index',1,'2018-09-29 16:25:45',0),(7,2,'编辑商品','pms:product:update',NULL,2,'/pms/product/updateProduct',1,'2018-09-29 16:34:23',0),(8,2,'删除商品','pms:product:delete',NULL,2,'/pms/product/delete',1,'2018-09-29 16:38:33',0),(9,4,'添加商品分类','pms:productCategory:create',NULL,2,'/pms/productCate/create',1,'2018-09-29 16:43:23',0),(10,4,'修改商品分类','pms:productCategory:update',NULL,2,'/pms/productCate/update',1,'2018-09-29 16:43:55',0),(11,4,'删除商品分类','pms:productCategory:delete',NULL,2,'/pms/productAttr/delete',1,'2018-09-29 16:44:38',0),(12,5,'添加商品类型','pms:productAttribute:create',NULL,2,'/pms/productAttr/create',1,'2018-09-29 16:45:25',0),(13,5,'修改商品类型','pms:productAttribute:update',NULL,2,'/pms/productAttr/update',1,'2018-09-29 16:48:08',0),(14,5,'删除商品类型','pms:productAttribute:delete',NULL,2,'/pms/productAttr/delete',1,'2018-09-29 16:48:44',0),(15,6,'添加品牌','pms:brand:create',NULL,2,'/pms/brand/add',1,'2018-09-29 16:49:34',0),(16,6,'修改品牌','pms:brand:update',NULL,2,'/pms/brand/update',1,'2018-09-29 16:50:55',0),(17,6,'删除品牌','pms:brand:delete',NULL,2,'/pms/brand/delete',1,'2018-09-29 16:50:59',0),(18,0,'首页',NULL,NULL,0,NULL,1,'2018-09-29 16:51:57',0),(19,0,'商城用户管理',NULL,NULL,0,NULL,1,'2025-06-22 05:53:19',0),(20,19,'用户列表查看','mall:user:read',NULL,1,'/mallUser/list',1,'2025-06-22 05:53:19',0),(21,19,'用户详情查看','mall:user:detail',NULL,2,'/mallUser/detail',1,'2025-06-22 05:53:19',0),(22,19,'用户信息更新','mall:user:update',NULL,2,'/mallUser/update',1,'2025-06-22 05:53:19',0),(23,19,'用户状态管理','mall:user:status',NULL,2,'/mallUser/updateStatus',1,'2025-06-22 05:53:19',0),(24,19,'用户删除','mall:user:delete',NULL,2,'/mallUser/delete',1,'2025-06-22 05:53:19',0),(25,19,'用户批量冻结','mall:user:batch_freeze',NULL,2,'/mallUser/batchFreeze',1,'2025-06-22 05:53:19',0),(26,19,'用户角色查看','mall:user:role_read',NULL,2,'/mallUser/roles',1,'2025-06-22 05:53:19',0);

-- 后台资源表
DROP TABLE IF EXISTS ums_resource CASCADE;
CREATE TABLE ums_resource (
  id BIGSERIAL NOT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  name varchar(200) DEFAULT NULL,
  url varchar(200) DEFAULT NULL,
  description varchar(500) DEFAULT NULL,
  category_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO ums_resource VALUES
(1,'2020-02-04 17:04:55','商品品牌管理','/brand/**',NULL,1),
(2,'2020-02-04 17:05:35','商品属性分类管理','/productAttribute/category/**',NULL,1),
(3,'2020-02-04 17:06:13','商品属性管理','/productAttribute/**',NULL,1),
(4,'2020-02-04 17:07:15','商品分类管理','/productCategory/**',NULL,1),
(5,'2020-02-04 17:09:16','商品管理','/product/**',NULL,1),
(6,'2020-02-04 17:09:53','商品库存管理','/sku/**',NULL,1),
(8,'2020-02-05 14:43:37','订单管理','/order/**','',2),
(9,'2020-02-05 14:44:22','订单退货申请管理','/returnApply/**','',2),
(10,'2020-02-05 14:45:08','退货原因管理','/returnReason/**','',2),
(11,'2020-02-05 14:45:43','订单设置管理','/orderSetting/**','',2),
(12,'2020-02-05 14:46:23','收货地址管理','/companyAddress/**','',2),
(13,'2020-02-07 16:37:22','优惠券管理','/coupon/**','',3),
(14,'2020-02-07 16:37:59','优惠券领取记录管理','/couponHistory/**','',3),
(15,'2020-02-07 16:38:28','限时购活动管理','/flash/**','',3),
(16,'2020-02-07 16:38:59','限时购商品关系管理','/flashProductRelation/**','',3),
(17,'2020-02-07 16:39:22','限时购场次管理','/flashSession/**','',3),
(18,'2020-02-07 16:40:07','首页轮播广告管理','/home/advertise/**','',3),
(19,'2020-02-07 16:40:34','首页品牌管理','/home/brand/**','',3),
(20,'2020-02-07 16:41:06','首页新品管理','/home/newProduct/**','',3),
(21,'2020-02-07 16:42:16','首页人气推荐管理','/home/recommendProduct/**','',3),
(22,'2020-02-07 16:42:48','首页专题推荐管理','/home/recommendSubject/**','',3),
(23,'2020-02-07 16:44:56','商品优选管理','/prefrenceArea/**','',5),
(24,'2020-02-07 16:45:39','商品专题管理','/subject/**','',5),
(25,'2020-02-07 16:47:34','后台用户管理','/admin/**','',4),
(26,'2020-02-07 16:48:24','后台用户角色管理','/role/**','',4),
(27,'2020-02-07 16:48:48','后台菜单管理','/menu/**','',4),
(28,'2020-02-07 16:49:18','后台资源分类管理','/resourceCategory/**','',4),
(29,'2020-02-07 16:49:45','后台资源管理','/resource/**','',4),
(30,'2020-09-19 15:47:57','Legacy level interface','/memberLevel/**','Compatibility endpoint; disabled in the default template UI',7),
(31,'2020-09-19 15:51:29','获取登录用户信息','/admin/info','用户登录必配',4),
(32,'2020-09-19 15:53:34','用户登出','/admin/logout','用户登出必配',4),
(33,'2025-06-22 05:53:19','商城用户管理','/mallUser/**','商城用户增删改查、状态管理、角色分配等功能',8),
(34,'2026-06-30 00:00:00','只读商品列表','/product/list','Demo read-only catalog list',1),
(35,'2026-06-30 00:00:00','只读商品简表','/product/simpleList','Demo read-only product options',1),
(36,'2026-06-30 00:00:00','只读商品详情','/product/updateInfo/*','Demo read-only product detail',1),
(37,'2026-06-30 00:00:00','只读商品图片','/product/image/*','Demo read-only product image list',1),
(38,'2026-06-30 00:00:00','只读品牌列表','/brand/list','Demo read-only brand list',1),
(39,'2026-06-30 00:00:00','只读品牌选项','/brand/listAll','Demo read-only brand options',1),
(40,'2026-06-30 00:00:00','只读品牌详情','/brand/*','Demo read-only brand detail',1),
(41,'2026-06-30 00:00:00','只读分类列表','/productCategory/list/*','Demo read-only category list',1),
(42,'2026-06-30 00:00:00','只读分类详情','/productCategory/*','Demo read-only category detail',1),
(43,'2026-06-30 00:00:00','只读分类树','/productCategory/list/withChildren','Demo read-only category tree',1),
(44,'2026-06-30 00:00:00','只读属性分类','/productAttribute/category/list/withAttr','Demo read-only attribute categories',1),
(45,'2026-06-30 00:00:00','只读分类属性','/productAttribute/attrInfo/*','Demo read-only category attributes',1),
(46,'2026-06-30 00:00:00','只读轮播列表','/banner/list','Demo read-only banners',3),
(47,'2026-06-30 00:00:00','只读轮播详情','/banner/*','Demo read-only banner detail',3),
(48,'2026-06-30 00:00:00','只读设置列表','/sysSetting/list','Demo read-only settings list',4),
(49,'2026-06-30 00:00:00','只读设置详情','/sysSetting/*','Demo read-only setting detail',4),
(50,'2026-06-30 00:00:00','只读全部设置','/sysSetting/getAllSettings','Demo read-only enabled settings',4),
(51,'2026-06-30 00:00:00','只读设置值','/sysSetting/getValue/*','Demo read-only setting value',4),
(52,'2026-06-30 00:00:00','只读邮件模板列表','/smsEmailTemplate/list','Demo read-only email templates',4),
(53,'2026-06-30 00:00:00','只读邮件模板详情','/smsEmailTemplate/*','Demo read-only email template detail',4),
(54,'2026-06-30 00:00:00','只读工作台','/admin/dashboard','Demo read-only dashboard',4);

-- 后台用户角色表
DROP TABLE IF EXISTS ums_role CASCADE;
CREATE TABLE ums_role (
  id BIGSERIAL NOT NULL,
  name varchar(100) DEFAULT NULL,
  description varchar(500) DEFAULT NULL,
  admin_count INTEGER DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  status INTEGER DEFAULT '1',
  sort INTEGER DEFAULT '0',
  PRIMARY KEY (id)
);

INSERT INTO ums_role VALUES
(1,'Catalog Manager','Can manage catalog resources',0,'2025-06-22 05:53:19',0,0),
(2,'Order Manager','Can manage orders',1,'2025-06-22 05:53:19',1,0),
(5,'Super Admin','Full template administration access',1,'2025-06-22 05:53:19',1,0),
(6,'Member Viewer','Can view member information only',1,'2025-06-22 05:53:19',1,0),
(7,'Member Admin','Full member management access',1,'2025-06-22 05:53:19',1,0),
(8,'Demo Viewer','Read-only catalog and configuration demo role; no member, order, or admin-user access',1,'2026-06-30 00:00:00',1,0);

-- 后台角色菜单关系表
DROP TABLE IF EXISTS ums_role_menu_relation CASCADE;
CREATE TABLE ums_role_menu_relation (
  id BIGSERIAL NOT NULL,
  role_id BIGINT DEFAULT NULL,
  menu_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO ums_role_menu_relation VALUES (53,2,7),(54,2,8),(55,2,9),(56,2,10),(57,2,11),(72,5,1),(73,5,2),(74,5,3),(75,5,4),(76,5,5),(77,5,6),(78,5,7),(79,5,8),(80,5,9),(81,5,10),(82,5,11),(83,5,12),(84,5,13),(85,5,14),(86,5,16),(87,5,17),(88,5,18),(89,5,19),(90,5,20),(91,5,21),(92,5,22),(93,5,23),(94,5,24),(95,5,25),(121,1,1),(122,1,2),(123,1,3),(124,1,4),(125,1,5),(126,1,6),(127,6,21),(128,6,26),(129,7,21),(130,7,26),(131,5,26);

-- 后台用户角色和权限关系表
DROP TABLE IF EXISTS ums_role_permission_relation CASCADE;
CREATE TABLE ums_role_permission_relation (
  id BIGSERIAL NOT NULL,
  role_id BIGINT DEFAULT NULL,
  permission_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO ums_role_permission_relation VALUES (1,1,1),(2,1,2),(3,1,3),(4,1,7),(5,1,8),(6,2,4),(7,2,9),(8,2,10),(9,2,11),(10,3,5),(11,3,12),(12,3,13),(13,3,14),(14,4,6),(15,4,15),(16,4,16),(17,4,17),(18,6,20),(19,6,21),(20,6,26),(21,7,19),(22,7,20),(23,7,21),(24,7,22),(25,7,23),(26,7,24),(27,7,25),(28,7,26),(29,5,19),(30,5,20),(31,5,21),(32,5,22),(33,5,23),(34,5,24),(35,5,25),(36,5,26);

-- 后台角色资源关系表
DROP TABLE IF EXISTS ums_role_resource_relation CASCADE;
CREATE TABLE ums_role_resource_relation (
  id BIGSERIAL NOT NULL,
  role_id BIGINT DEFAULT NULL,
  resource_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO ums_role_resource_relation VALUES (194,5,1),(195,5,2),(196,5,3),(197,5,4),(198,5,5),(199,5,6),(200,5,8),(201,5,9),(202,5,10),(203,5,11),(204,5,12),(205,5,13),(206,5,14),(207,5,15),(208,5,16),(209,5,17),(210,5,18),(211,5,19),(212,5,20),(213,5,21),(214,5,22),(215,5,23),(216,5,24),(217,5,25),(218,5,26),(219,5,27),(220,5,28),(221,5,29),(222,5,30),(232,2,8),(233,2,9),(234,2,10),(235,2,11),(236,2,12),(237,2,31),(238,2,32),(239,1,1),(240,1,2),(241,1,3),(242,1,4),(243,1,5),(244,1,6),(245,1,23),(246,1,24),(247,1,31),(248,1,32),(250,6,25),(251,6,31),(252,6,32),(253,7,33),(254,7,25),(255,7,31),(256,7,32),(257,5,33),(258,8,31),(259,8,32),(260,8,34),(261,8,35),(262,8,36),(263,8,37),(264,8,38),(265,8,39),(266,8,40),(267,8,41),(268,8,42),(269,8,43),(270,8,44),(271,8,45),(272,8,46),(273,8,47),(274,8,48),(275,8,49),(276,8,50),(277,8,51),(278,8,52),(279,8,53),(280,8,54),(281,5,31),(282,5,32),(283,5,34),(284,5,35),(285,5,36),(286,5,37),(287,5,38),(288,5,39),(289,5,40),(290,5,41),(291,5,42),(292,5,43),(293,5,44),(294,5,45),(295,5,46),(296,5,47),(297,5,48),(298,5,49),(299,5,50),(300,5,51),(301,5,52),(302,5,53),(303,5,54);

-- ----------------------------
-- 产品相关表
-- ----------------------------

-- 品牌表
DROP TABLE IF EXISTS pms_brand CASCADE;
CREATE TABLE pms_brand (
  id BIGSERIAL NOT NULL,
  name varchar(64) DEFAULT NULL,
  sub_title varchar(255) DEFAULT NULL,
  first_letter varchar(8) DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  factory_status INTEGER DEFAULT NULL,
  show_status INTEGER DEFAULT NULL,
  product_count INTEGER DEFAULT NULL,
  product_comment_count INTEGER DEFAULT NULL,
  logo varchar(255) DEFAULT NULL,
  big_pic varchar(255) DEFAULT NULL,
  brand_story text,
  PRIMARY KEY (id)
);

-- 通用演示品牌
INSERT INTO pms_brand VALUES 
(1,'Nova Supply','Desk gear and daily electronics','N',1,1,1,1,0,'/template-assets/brand-1.svg','/template-assets/brand-1.svg','Nova Supply curates practical electronics for modern desks and everyday work.'),
(2,'Urban Nest','Compact home and travel essentials','U',2,1,1,1,0,'/template-assets/brand-2.svg','/template-assets/brand-2.svg','Urban Nest focuses on compact goods for small homes, travel, and flexible routines.'),
(3,'Luma Works','Lighting, bags, and work accessories','L',3,1,1,1,0,'/template-assets/brand-3.svg','/template-assets/brand-3.svg','Luma Works builds simple accessories for commuting, work, and organized living.');

-- 商品评价表
DROP TABLE IF EXISTS pms_comment CASCADE;
CREATE TABLE pms_comment (
  id BIGSERIAL NOT NULL,
  product_id BIGINT DEFAULT NULL,
  member_nick_name varchar(255) DEFAULT NULL,
  product_name varchar(255) DEFAULT NULL,
  star INTEGER DEFAULT NULL,
  member_ip varchar(64) DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  show_status INTEGER DEFAULT NULL,
  product_attribute varchar(255) DEFAULT NULL,
  collect_couont INTEGER DEFAULT NULL,
  read_count INTEGER DEFAULT NULL,
  content text,
  pics varchar(1000) DEFAULT NULL,
  member_icon varchar(255) DEFAULT NULL,
  replay_count INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 产品评价回复表
DROP TABLE IF EXISTS pms_comment_replay CASCADE;
CREATE TABLE pms_comment_replay (
  id BIGSERIAL NOT NULL,
  comment_id BIGINT DEFAULT NULL,
  member_nick_name varchar(255) DEFAULT NULL,
  member_icon varchar(255) DEFAULT NULL,
  content varchar(1000) DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  type INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 运费模版表
DROP TABLE IF EXISTS pms_feight_template CASCADE;
CREATE TABLE pms_feight_template (
  id BIGSERIAL NOT NULL,
  name varchar(64) DEFAULT NULL,
  charge_type INTEGER DEFAULT NULL,
  first_weight decimal(10,2) DEFAULT NULL,
  first_fee decimal(10,2) DEFAULT NULL,
  continue_weight decimal(10,2) DEFAULT NULL,
  continme_fee decimal(10,2) DEFAULT NULL,
  dest varchar(255) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 产品分类表
DROP TABLE IF EXISTS pms_product_category CASCADE;
CREATE TABLE pms_product_category (
  id BIGSERIAL NOT NULL,
  parent_id BIGINT DEFAULT NULL,
  name varchar(64) DEFAULT NULL,
  sub_title varchar(255) DEFAULT NULL,
  level INTEGER DEFAULT NULL,
  product_count INTEGER DEFAULT NULL,
  product_unit varchar(64) DEFAULT NULL,
  nav_status INTEGER DEFAULT NULL,
  show_status INTEGER DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  icon varchar(255) DEFAULT NULL,
  keywords varchar(255) DEFAULT NULL,
  description text,
  PRIMARY KEY (id)
);

-- 通用演示分类
INSERT INTO pms_product_category VALUES 
(1, 0, 'Electronics', 'Everyday tech essentials', 0, 2, 'item', 1, 1, 100, '/template-assets/category-1.svg', 'electronics,desk,device', 'Keyboards, speakers, chargers, and compact devices'),
(2, 0, 'Home Goods', 'Useful products for compact living', 0, 0, 'item', 1, 1, 90, '/template-assets/category-2.svg', 'home,desk,living', 'Small home goods and desk upgrades'),
(3, 0, 'Everyday Carry', 'Bags and portable essentials', 0, 1, 'item', 1, 1, 80, '/template-assets/category-3.svg', 'bag,travel,carry', 'Daily bags and travel accessories');

-- 产品属性分类表
DROP TABLE IF EXISTS pms_product_attribute_category CASCADE;
CREATE TABLE pms_product_attribute_category (
  id BIGSERIAL NOT NULL,
  name varchar(64) DEFAULT NULL,
  attribute_count INTEGER DEFAULT '0',
  param_count INTEGER DEFAULT '0',
  PRIMARY KEY (id)
);

-- 通用演示属性分类
INSERT INTO pms_product_attribute_category VALUES (1, 'General Specs', 0, 4);

-- 产品属性表
DROP TABLE IF EXISTS pms_product_attribute CASCADE;
CREATE TABLE pms_product_attribute (
  id BIGSERIAL NOT NULL,
  product_attribute_category_id BIGINT DEFAULT NULL,
  name varchar(64) DEFAULT NULL,
  select_type INTEGER DEFAULT NULL,
  input_type INTEGER DEFAULT NULL,
  input_list varchar(255) DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  filter_type INTEGER DEFAULT NULL,
  search_type INTEGER DEFAULT NULL,
  related_status INTEGER DEFAULT NULL,
  hand_add_status INTEGER DEFAULT NULL,
  type INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 通用演示属性
INSERT INTO pms_product_attribute VALUES (1, 1, 'Color', 0, 0, '', 4, 0, 2, 0, 1, 1);
INSERT INTO pms_product_attribute VALUES (2, 1, 'Material', 0, 0, '', 3, 0, 1, 0, 1, 1);
INSERT INTO pms_product_attribute VALUES (3, 1, 'Power', 0, 0, '', 2, 0, 2, 0, 1, 1);
INSERT INTO pms_product_attribute VALUES (4, 1, 'Warranty', 0, 0, '', 1, 0, 2, 0, 1, 1);

-- 产品分类和属性关系表
DROP TABLE IF EXISTS pms_product_category_attribute_relation CASCADE;
CREATE TABLE pms_product_category_attribute_relation (
  id BIGSERIAL NOT NULL,
  product_category_id BIGINT DEFAULT NULL,
  product_attribute_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 插入分类属性关系
INSERT INTO pms_product_category_attribute_relation VALUES (1, 1, 1);
INSERT INTO pms_product_category_attribute_relation VALUES (2, 1, 2);
INSERT INTO pms_product_category_attribute_relation VALUES (3, 1, 3);
INSERT INTO pms_product_category_attribute_relation VALUES (4, 1, 4);
INSERT INTO pms_product_category_attribute_relation VALUES (5, 2, 1);
INSERT INTO pms_product_category_attribute_relation VALUES (6, 2, 2);
INSERT INTO pms_product_category_attribute_relation VALUES (7, 3, 1);
INSERT INTO pms_product_category_attribute_relation VALUES (8, 3, 2);

-- 产品表
DROP TABLE IF EXISTS pms_product CASCADE;
CREATE TABLE pms_product (
  id BIGSERIAL NOT NULL,
  brand_id BIGINT DEFAULT NULL,
  product_category_id BIGINT DEFAULT NULL,
  feight_template_id BIGINT DEFAULT NULL,
  product_attribute_category_id BIGINT DEFAULT NULL,
  name varchar(200) NOT NULL,
  pic varchar(255) DEFAULT NULL,
  product_sn varchar(64) NOT NULL,
  delete_status INTEGER DEFAULT NULL,
  publish_status INTEGER DEFAULT NULL,
  new_status INTEGER DEFAULT NULL,
  recommand_status INTEGER DEFAULT NULL,
  verify_status INTEGER DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  sale INTEGER DEFAULT NULL,
  price decimal(10,2) DEFAULT NULL,
  promotion_price decimal(10,2) DEFAULT NULL,
  gift_growth INTEGER DEFAULT '0',
  gift_point INTEGER DEFAULT '0',
  use_point_limit INTEGER DEFAULT NULL,
  sub_title varchar(255) DEFAULT NULL,
  description text,
  original_price decimal(10,2) DEFAULT NULL,
  stock INTEGER DEFAULT NULL,
  low_stock INTEGER DEFAULT NULL,
  unit varchar(16) DEFAULT NULL,
  weight decimal(10,2) DEFAULT NULL,
  preview_status INTEGER DEFAULT NULL,
  service_ids varchar(64) DEFAULT NULL,
  keywords varchar(255) DEFAULT NULL,
  note varchar(255) DEFAULT NULL,
  album_pics varchar(255) DEFAULT NULL,
  detail_title varchar(255) DEFAULT NULL,
  detail_desc text,
  detail_html text,
  detail_mobile_html text,
  promotion_start_time TIMESTAMP DEFAULT NULL,
  promotion_end_time TIMESTAMP DEFAULT NULL,
  promotion_per_limit INTEGER DEFAULT NULL,
  promotion_type INTEGER DEFAULT NULL,
  brand_name varchar(255) DEFAULT NULL,
  product_category_name varchar(255) DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

-- 通用演示商品
INSERT INTO pms_product (id, brand_id, product_category_id, feight_template_id, product_attribute_category_id, name, pic, product_sn, delete_status, publish_status, new_status, recommand_status, verify_status, sort, sale, price, promotion_price, gift_growth, gift_point, use_point_limit, sub_title, description, original_price, stock, low_stock, unit, weight, preview_status, service_ids, keywords, note, album_pics, detail_title, detail_desc, detail_html, detail_mobile_html, promotion_start_time, promotion_end_time, promotion_per_limit, promotion_type, brand_name, product_category_name) VALUES 
(1, 1, 1, NULL, 1, 'Nova Keys Wireless Keyboard', '/template-assets/product-1.svg', 'NV-KEYS-001', 0, 1, 1, 1, 1, 100, 42, 79.00, NULL, 0, 0, 0, 'Hot-swappable keys for work and gaming desks', 'A compact wireless keyboard with quiet switches and multi-device pairing.', 99.00, 120, 12, 'piece', 0.85, 0, '1,2,3', 'keyboard wireless office desk', 'Template demo product', '/template-assets/product-1.svg', 'Nova Keys Wireless Keyboard', 'Compact desk keyboard with replaceable switches.', '<p>Compact layout, wireless pairing, and replaceable keys for everyday productivity.</p>', '<p>Compact layout, wireless pairing, and replaceable keys for everyday productivity.</p>', NULL, NULL, NULL, 0, 'Nova Supply', 'Electronics'),
(2, 2, 1, NULL, 1, 'Urban Sound Mini Speaker', '/template-assets/product-2.svg', 'UN-SPK-001', 0, 1, 1, 1, 1, 90, 35, 49.00, NULL, 0, 0, 0, 'Portable Bluetooth audio for rooms and trips', 'A small speaker with long battery life and clean tabletop styling.', 69.00, 96, 10, 'piece', 0.55, 0, '1,2,3', 'speaker bluetooth portable audio', 'Template demo product', '/template-assets/product-2.svg', 'Urban Sound Mini Speaker', 'Small speaker with full-room sound.', '<p>Bluetooth pairing, durable shell, and a compact footprint for home or travel.</p>', '<p>Bluetooth pairing, durable shell, and a compact footprint for home or travel.</p>', NULL, NULL, NULL, 0, 'Urban Nest', 'Electronics'),
(3, 3, 3, NULL, 1, 'Luma Commuter Backpack', '/template-assets/product-3.svg', 'LW-BAG-001', 0, 1, 1, 1, 1, 80, 28, 89.00, NULL, 0, 0, 0, 'Water-resistant bag with laptop organization', 'A daily backpack with a protected laptop sleeve and quick-access pockets.', 119.00, 64, 8, 'piece', 0.95, 0, '1,2,3', 'backpack commuter laptop travel', 'Template demo product', '/template-assets/product-3.svg', 'Luma Commuter Backpack', 'Everyday backpack for work and short trips.', '<p>Water-resistant fabric, laptop storage, and balanced shoulder straps.</p>', '<p>Water-resistant fabric, laptop storage, and balanced shoulder straps.</p>', NULL, NULL, NULL, 0, 'Luma Works', 'Everyday Carry');

-- 会员商品收藏表
DROP TABLE IF EXISTS ums_member_product_collection CASCADE;
CREATE TABLE ums_member_product_collection (
  id BIGSERIAL NOT NULL,
  member_id BIGINT NOT NULL,
  member_nickname varchar(64) DEFAULT NULL,
  member_icon varchar(500) DEFAULT NULL,
  product_id BIGINT NOT NULL,
  product_name varchar(200) DEFAULT NULL,
  product_pic varchar(500) DEFAULT NULL,
  product_sub_title varchar(255) DEFAULT NULL,
  product_price varchar(64) DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uk_member_product_collection UNIQUE (member_id, product_id)
);

-- 会员品牌关注表
DROP TABLE IF EXISTS ums_member_brand_attention CASCADE;
CREATE TABLE ums_member_brand_attention (
  id BIGSERIAL NOT NULL,
  member_id BIGINT NOT NULL,
  member_nickname varchar(64) DEFAULT NULL,
  member_icon varchar(500) DEFAULT NULL,
  brand_id BIGINT NOT NULL,
  brand_name varchar(200) DEFAULT NULL,
  brand_logo varchar(500) DEFAULT NULL,
  brand_city varchar(255) DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uk_member_brand_attention UNIQUE (member_id, brand_id)
);

-- 会员商品浏览历史表
DROP TABLE IF EXISTS ums_member_read_history CASCADE;
CREATE TABLE ums_member_read_history (
  id BIGSERIAL NOT NULL,
  member_id BIGINT NOT NULL,
  member_nickname varchar(64) DEFAULT NULL,
  member_icon varchar(500) DEFAULT NULL,
  product_id BIGINT NOT NULL,
  product_name varchar(200) DEFAULT NULL,
  product_pic varchar(500) DEFAULT NULL,
  product_sub_title varchar(255) DEFAULT NULL,
  product_price varchar(64) DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uk_member_read_history UNIQUE (member_id, product_id)
);

-- 产品属性值表
DROP TABLE IF EXISTS pms_product_attribute_value CASCADE;
CREATE TABLE pms_product_attribute_value (
  id BIGSERIAL NOT NULL,
  product_id BIGINT DEFAULT NULL,
  product_attribute_id BIGINT DEFAULT NULL,
  value varchar(64) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 插入产品属性值
INSERT INTO pms_product_attribute_value VALUES (1, 1, 1, 'Slate');
INSERT INTO pms_product_attribute_value VALUES (2, 1, 2, 'Aluminum and PBT');
INSERT INTO pms_product_attribute_value VALUES (3, 1, 3, 'USB-C / Bluetooth');
INSERT INTO pms_product_attribute_value VALUES (4, 1, 4, '12 months');

INSERT INTO pms_product_attribute_value VALUES (5, 2, 1, 'Teal');
INSERT INTO pms_product_attribute_value VALUES (6, 2, 2, 'Recycled polymer');
INSERT INTO pms_product_attribute_value VALUES (7, 2, 3, '18-hour battery');
INSERT INTO pms_product_attribute_value VALUES (8, 2, 4, '12 months');

INSERT INTO pms_product_attribute_value VALUES (9, 3, 1, 'Clay Orange');
INSERT INTO pms_product_attribute_value VALUES (10, 3, 2, 'Water-resistant nylon');
INSERT INTO pms_product_attribute_value VALUES (11, 3, 3, 'No battery');
INSERT INTO pms_product_attribute_value VALUES (12, 3, 4, '24 months');

-- SKU表
DROP TABLE IF EXISTS pms_sku_stock CASCADE;
CREATE TABLE pms_sku_stock (
  id BIGSERIAL NOT NULL,
  product_id BIGINT DEFAULT NULL,
  sku_code varchar(64) NOT NULL,
  price decimal(10,2) DEFAULT NULL,
  stock INTEGER DEFAULT '0',
  low_stock INTEGER DEFAULT NULL,
  pic varchar(255) DEFAULT NULL,
  sale INTEGER DEFAULT NULL,
  promotion_price decimal(10,2) DEFAULT NULL,
  lock_stock INTEGER DEFAULT '0',
  sp_data varchar(500) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 插入SKU库存
INSERT INTO pms_sku_stock VALUES (1, 1, 'NV-KEYS-001-STD', 79.00, 120, 12, '/template-assets/product-1.svg', 0, NULL, 0, NULL);
INSERT INTO pms_sku_stock VALUES (2, 2, 'UN-SPK-001-STD', 49.00, 96, 10, '/template-assets/product-2.svg', 0, NULL, 0, NULL);
INSERT INTO pms_sku_stock VALUES (3, 3, 'LW-BAG-001-STD', 89.00, 64, 8, '/template-assets/product-3.svg', 0, NULL, 0, NULL);

-- 产品阶梯价格表
DROP TABLE IF EXISTS pms_product_ladder CASCADE;
CREATE TABLE pms_product_ladder (
  id BIGSERIAL NOT NULL,
  product_id BIGINT DEFAULT NULL,
  count INTEGER DEFAULT NULL,
  discount decimal(10,2) DEFAULT NULL,
  price decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 产品满减表(只针对同商品)
DROP TABLE IF EXISTS pms_product_full_reduction CASCADE;
CREATE TABLE pms_product_full_reduction (
  id BIGSERIAL NOT NULL,
  product_id BIGINT DEFAULT NULL,
  full_price decimal(10,2) DEFAULT NULL,
  reduce_price decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 商品会员价格表
DROP TABLE IF EXISTS pms_member_price CASCADE;
CREATE TABLE pms_member_price (
  id BIGSERIAL NOT NULL,
  product_id BIGINT DEFAULT NULL,
  member_level_id BIGINT DEFAULT NULL,
  member_price decimal(10,2) DEFAULT NULL,
  member_level_name varchar(100) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 专题商品关系表
DROP TABLE IF EXISTS cms_subject_product_relation CASCADE;
CREATE TABLE cms_subject_product_relation (
  id BIGSERIAL NOT NULL,
  subject_id BIGINT DEFAULT NULL,
  product_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 优选专区表
DROP TABLE IF EXISTS cms_prefrence_area_product_relation CASCADE;
CREATE TABLE cms_prefrence_area_product_relation (
  id BIGSERIAL NOT NULL,
  prefrence_area_id BIGINT DEFAULT NULL,
  product_id BIGINT DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 专题表
DROP TABLE IF EXISTS cms_subject CASCADE;
CREATE TABLE cms_subject (
  id BIGSERIAL NOT NULL,
  category_id BIGINT DEFAULT NULL,
  title varchar(100) DEFAULT NULL,
  pic varchar(500) DEFAULT NULL,
  product_count INTEGER DEFAULT NULL,
  recommend_status INTEGER DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  collect_count INTEGER DEFAULT NULL,
  read_count INTEGER DEFAULT NULL,
  comment_count INTEGER DEFAULT NULL,
  album_pics varchar(1000) DEFAULT NULL,
  description varchar(1000) DEFAULT NULL,
  show_status INTEGER DEFAULT NULL,
  forward_count INTEGER DEFAULT NULL,
  category_name varchar(200) DEFAULT NULL,
  content text,
  PRIMARY KEY (id)
);

INSERT INTO cms_subject (id, category_id, title, pic, product_count, recommend_status, create_time, collect_count, read_count, comment_count, album_pics, description, show_status, forward_count, category_name, content) VALUES
(1, 1, 'Starter Catalog Picks', '/template-assets/banner.svg', 3, 1, NOW(), 0, 0, 0, NULL, 'A compact product set for validating the storefront template.', 1, 0, 'Featured', 'Template starter catalog content');

-- 首页轮播广告表
DROP TABLE IF EXISTS sms_home_advertise CASCADE;
CREATE TABLE sms_home_advertise (
  id BIGSERIAL NOT NULL,
  name varchar(100) DEFAULT NULL,
  type INTEGER DEFAULT NULL,
  pic varchar(500) DEFAULT NULL,
  start_time TIMESTAMP DEFAULT NULL,
  end_time TIMESTAMP DEFAULT NULL,
  status INTEGER DEFAULT NULL,
  click_count INTEGER DEFAULT NULL,
  order_count INTEGER DEFAULT NULL,
  url varchar(500) DEFAULT NULL,
  note varchar(500) DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO sms_home_advertise (id, name, type, pic, start_time, end_time, status, click_count, order_count, url, note, sort) VALUES
(1, 'Starter Storefront Banner', 1, '/template-assets/banner.svg', NOW() - INTERVAL '1 day', NOW() + INTERVAL '365 day', 1, 0, 0, '/products/new', 'Default template storefront banner', 100);

-- 首页推荐品牌表
DROP TABLE IF EXISTS sms_home_brand CASCADE;
CREATE TABLE sms_home_brand (
  id BIGSERIAL NOT NULL,
  brand_id BIGINT DEFAULT NULL,
  brand_name varchar(100) DEFAULT NULL,
  recommend_status INTEGER DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO sms_home_brand (id, brand_id, brand_name, recommend_status, sort) VALUES
(1, 1, 'Nova Supply', 1, 100),
(2, 2, 'Urban Nest', 1, 90),
(3, 3, 'Luma Works', 1, 80);

-- 首页新品推荐表
DROP TABLE IF EXISTS sms_home_new_product CASCADE;
CREATE TABLE sms_home_new_product (
  id BIGSERIAL NOT NULL,
  product_id BIGINT DEFAULT NULL,
  product_name varchar(500) DEFAULT NULL,
  recommend_status INTEGER DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO sms_home_new_product (id, product_id, product_name, recommend_status, sort) VALUES
(1, 1, 'Nova Keys Wireless Keyboard', 1, 100),
(2, 2, 'Urban Sound Mini Speaker', 1, 90),
(3, 3, 'Luma Commuter Backpack', 1, 80);

-- 首页人气推荐表
DROP TABLE IF EXISTS sms_home_recommend_product CASCADE;
CREATE TABLE sms_home_recommend_product (
  id BIGSERIAL NOT NULL,
  product_id BIGINT DEFAULT NULL,
  product_name varchar(500) DEFAULT NULL,
  recommend_status INTEGER DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO sms_home_recommend_product (id, product_id, product_name, recommend_status, sort) VALUES
(1, 1, 'Nova Keys Wireless Keyboard', 1, 100),
(2, 2, 'Urban Sound Mini Speaker', 1, 90),
(3, 3, 'Luma Commuter Backpack', 1, 80);

-- 首页专题推荐表
DROP TABLE IF EXISTS sms_home_recommend_subject CASCADE;
CREATE TABLE sms_home_recommend_subject (
  id BIGSERIAL NOT NULL,
  subject_id BIGINT DEFAULT NULL,
  subject_name varchar(100) DEFAULT NULL,
  recommend_status INTEGER DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO sms_home_recommend_subject (id, subject_id, subject_name, recommend_status, sort) VALUES
(1, 1, 'Starter Catalog Picks', 1, 100);

-- 秒杀活动表（模板默认空数据，避免首页强制进入秒杀流程）
DROP TABLE IF EXISTS sms_flash_promotion CASCADE;
CREATE TABLE sms_flash_promotion (
  id BIGSERIAL NOT NULL,
  title varchar(200) DEFAULT NULL,
  start_date date DEFAULT NULL,
  end_date date DEFAULT NULL,
  status INTEGER DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sms_flash_promotion_session CASCADE;
CREATE TABLE sms_flash_promotion_session (
  id BIGSERIAL NOT NULL,
  name varchar(200) DEFAULT NULL,
  start_time time DEFAULT NULL,
  end_time time DEFAULT NULL,
  status INTEGER DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  PRIMARY KEY (id)
);

DROP TABLE IF EXISTS sms_flash_promotion_product_relation CASCADE;
CREATE TABLE sms_flash_promotion_product_relation (
  id BIGSERIAL NOT NULL,
  flash_promotion_id BIGINT DEFAULT NULL,
  flash_promotion_session_id BIGINT DEFAULT NULL,
  product_id BIGINT DEFAULT NULL,
  flash_promotion_price decimal(10,2) DEFAULT NULL,
  flash_promotion_count INTEGER DEFAULT NULL,
  flash_promotion_limit INTEGER DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 优惠券表
DROP TABLE IF EXISTS sms_coupon CASCADE;
CREATE TABLE sms_coupon (
  id BIGSERIAL NOT NULL,
  type INTEGER DEFAULT NULL,
  name varchar(100) DEFAULT NULL,
  platform INTEGER DEFAULT NULL,
  count INTEGER DEFAULT NULL,
  amount decimal(10,2) DEFAULT NULL,
  per_limit INTEGER DEFAULT NULL,
  min_point decimal(10,2) DEFAULT NULL,
  start_time TIMESTAMP DEFAULT NULL,
  end_time TIMESTAMP DEFAULT NULL,
  use_type INTEGER DEFAULT NULL,
  note varchar(200) DEFAULT NULL,
  publish_count INTEGER DEFAULT NULL,
  use_count INTEGER DEFAULT NULL,
  receive_count INTEGER DEFAULT NULL,
  enable_time TIMESTAMP DEFAULT NULL,
  code varchar(64) DEFAULT NULL,
  member_level INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 优惠券和产品分类关系表
DROP TABLE IF EXISTS sms_coupon_product_category_relation CASCADE;
CREATE TABLE sms_coupon_product_category_relation (
  id BIGSERIAL NOT NULL,
  coupon_id BIGINT DEFAULT NULL,
  product_category_id BIGINT DEFAULT NULL,
  product_category_name varchar(200) DEFAULT NULL,
  parent_category_name varchar(200) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 优惠券和产品关系表
DROP TABLE IF EXISTS sms_coupon_product_relation CASCADE;
CREATE TABLE sms_coupon_product_relation (
  id BIGSERIAL NOT NULL,
  coupon_id BIGINT DEFAULT NULL,
  product_id BIGINT DEFAULT NULL,
  product_name varchar(500) DEFAULT NULL,
  product_sn varchar(200) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 优惠券使用、领取历史表
DROP TABLE IF EXISTS sms_coupon_history CASCADE;
CREATE TABLE sms_coupon_history (
  id BIGSERIAL NOT NULL,
  coupon_id BIGINT DEFAULT NULL,
  member_id BIGINT DEFAULT NULL,
  coupon_code varchar(64) DEFAULT NULL,
  member_nickname varchar(64) DEFAULT NULL,
  get_type INTEGER DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  use_status INTEGER DEFAULT NULL,
  use_time TIMESTAMP DEFAULT NULL,
  order_id BIGINT DEFAULT NULL,
  order_sn varchar(100) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 积分变化历史兼容表（模板默认不启用）
DROP TABLE IF EXISTS ums_integration_change_history CASCADE;
CREATE TABLE ums_integration_change_history (
  id BIGSERIAL NOT NULL,
  member_id BIGINT DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  change_type INTEGER DEFAULT NULL,
  change_count INTEGER DEFAULT NULL,
  operate_man varchar(100) DEFAULT NULL,
  operate_note varchar(200) DEFAULT NULL,
  source_type INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 积分消费设置兼容表（模板默认不启用）
DROP TABLE IF EXISTS ums_integration_consume_setting CASCADE;
CREATE TABLE ums_integration_consume_setting (
  id BIGSERIAL NOT NULL,
  deduction_per_amount INTEGER DEFAULT NULL,
  max_percent_per_order INTEGER DEFAULT NULL,
  use_unit INTEGER DEFAULT NULL,
  coupon_status INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

-- ----------------------------
-- 订单相关表
-- ----------------------------

-- 订单表
DROP TABLE IF EXISTS oms_order CASCADE;
CREATE TABLE oms_order (
  id BIGSERIAL NOT NULL,
  member_id BIGINT NOT NULL,
  coupon_id BIGINT DEFAULT NULL,
  order_sn varchar(64) DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  member_username varchar(64) DEFAULT NULL,
  total_amount decimal(10,2) DEFAULT NULL,
  pay_amount decimal(10,2) DEFAULT NULL,
  freight_amount decimal(10,2) DEFAULT NULL,
  promotion_amount decimal(10,2) DEFAULT NULL,
  integration_amount decimal(10,2) DEFAULT NULL,
  coupon_amount decimal(10,2) DEFAULT NULL,
  discount_amount decimal(10,2) DEFAULT NULL,
  pay_type INTEGER DEFAULT NULL,
  source_type INTEGER DEFAULT NULL,
  status INTEGER DEFAULT NULL,
  order_type INTEGER DEFAULT NULL,
  delivery_company varchar(64) DEFAULT NULL,
  delivery_sn varchar(64) DEFAULT NULL,
  auto_confirm_day INTEGER DEFAULT NULL,
  integration INTEGER DEFAULT NULL,
  growth INTEGER DEFAULT NULL,
  promotion_info varchar(100) DEFAULT NULL,
  bill_type INTEGER DEFAULT NULL,
  bill_header varchar(200) DEFAULT NULL,
  bill_content varchar(200) DEFAULT NULL,
  bill_receiver_phone varchar(32) DEFAULT NULL,
  bill_receiver_email varchar(64) DEFAULT NULL,
  receiver_name varchar(100) NOT NULL,
  receiver_phone varchar(32) NOT NULL,
  receiver_country varchar(10) NOT NULL,
  receiver_country_code varchar(10) NOT NULL,
  receiver_post_code varchar(32) DEFAULT NULL,
  receiver_province varchar(32) DEFAULT NULL,
  receiver_city varchar(32) DEFAULT NULL,
  receiver_region varchar(32) DEFAULT NULL,
  receiver_detail_address varchar(200) DEFAULT NULL,
  note varchar(500) DEFAULT NULL,
  confirm_status INTEGER DEFAULT NULL,
  delete_status INTEGER NOT NULL DEFAULT '0',
  use_integration INTEGER DEFAULT NULL,
  payment_time TIMESTAMP DEFAULT NULL,
  delivery_time TIMESTAMP DEFAULT NULL,
  receive_time TIMESTAMP DEFAULT NULL,
  comment_time TIMESTAMP DEFAULT NULL,
  modify_time TIMESTAMP DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 订单商品信息表
DROP TABLE IF EXISTS oms_order_item CASCADE;
CREATE TABLE oms_order_item (
  id BIGSERIAL NOT NULL,
  order_id BIGINT DEFAULT NULL,
  order_sn varchar(64) DEFAULT NULL,
  product_id BIGINT DEFAULT NULL,
  product_pic varchar(500) DEFAULT NULL,
  product_name varchar(200) DEFAULT NULL,
  product_brand varchar(200) DEFAULT NULL,
  product_sn varchar(64) DEFAULT NULL,
  product_price decimal(10,2) DEFAULT NULL,
  product_quantity INTEGER DEFAULT NULL,
  product_sku_id BIGINT DEFAULT NULL,
  product_sku_code varchar(50) DEFAULT NULL,
  product_category_id BIGINT DEFAULT NULL,
  sp_data varchar(500) DEFAULT NULL,
  promotion_name varchar(200) DEFAULT NULL,
  promotion_amount decimal(10,2) DEFAULT NULL,
  coupon_amount decimal(10,2) DEFAULT NULL,
  integration_amount decimal(10,2) DEFAULT NULL,
  real_amount decimal(10,2) DEFAULT NULL,
  gift_integration INTEGER DEFAULT NULL,
  gift_growth INTEGER DEFAULT NULL,
  product_attr varchar(500) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 购物车表
DROP TABLE IF EXISTS oms_cart_item CASCADE;
CREATE TABLE oms_cart_item (
  id BIGSERIAL NOT NULL,
  product_id BIGINT DEFAULT NULL,
  product_sku_id BIGINT DEFAULT NULL,
  member_id BIGINT DEFAULT NULL,
  quantity INTEGER DEFAULT NULL,
  price decimal(10,2) DEFAULT NULL,
  product_pic varchar(1000) DEFAULT NULL,
  product_name varchar(500) DEFAULT NULL,
  product_sub_title varchar(500) DEFAULT NULL,
  product_sku_code varchar(200) DEFAULT NULL,
  member_nickname varchar(500) DEFAULT NULL,
  create_date TIMESTAMP DEFAULT NULL,
  modify_date TIMESTAMP DEFAULT NULL,
  delete_status INTEGER DEFAULT '0',
  product_category_id BIGINT DEFAULT NULL,
  product_brand varchar(200) DEFAULT NULL,
  product_sn varchar(200) DEFAULT NULL,
  product_attr varchar(500) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 订单操作历史记录表
DROP TABLE IF EXISTS oms_order_operate_history CASCADE;
CREATE TABLE oms_order_operate_history (
  id BIGSERIAL NOT NULL,
  order_id BIGINT DEFAULT NULL,
  operate_man varchar(100) DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  order_status INTEGER DEFAULT NULL,
  note varchar(500) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 订单退货申请表
DROP TABLE IF EXISTS oms_order_return_apply CASCADE;
CREATE TABLE oms_order_return_apply (
  id BIGSERIAL NOT NULL,
  order_id BIGINT DEFAULT NULL,
  company_address_id BIGINT DEFAULT NULL,
  product_id BIGINT DEFAULT NULL,
  order_sn varchar(64) DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  member_username varchar(64) DEFAULT NULL,
  return_amount decimal(10,2) DEFAULT NULL,
  return_name varchar(100) DEFAULT NULL,
  return_phone varchar(100) DEFAULT NULL,
  status INTEGER DEFAULT NULL,
  handle_time TIMESTAMP DEFAULT NULL,
  product_pic varchar(500) DEFAULT NULL,
  product_name varchar(200) DEFAULT NULL,
  product_brand varchar(200) DEFAULT NULL,
  product_attr varchar(500) DEFAULT NULL,
  product_count INTEGER DEFAULT NULL,
  product_price decimal(10,2) DEFAULT NULL,
  product_real_price decimal(10,2) DEFAULT NULL,
  reason varchar(200) DEFAULT NULL,
  description varchar(500) DEFAULT NULL,
  proof_pics varchar(1000) DEFAULT NULL,
  handle_note varchar(500) DEFAULT NULL,
  handle_man varchar(100) DEFAULT NULL,
  receive_man varchar(100) DEFAULT NULL,
  receive_time TIMESTAMP DEFAULT NULL,
  receive_note varchar(500) DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 退货原因表
DROP TABLE IF EXISTS oms_order_return_reason CASCADE;
CREATE TABLE oms_order_return_reason (
  id BIGSERIAL NOT NULL,
  name varchar(100) DEFAULT NULL,
  sort INTEGER DEFAULT NULL,
  status INTEGER DEFAULT NULL,
  create_time TIMESTAMP DEFAULT NULL,
  PRIMARY KEY (id)
);

-- 订单设置表
DROP TABLE IF EXISTS oms_order_setting CASCADE;
CREATE TABLE oms_order_setting (
  id BIGSERIAL NOT NULL,
  flash_order_overtime INTEGER DEFAULT NULL,
  normal_order_overtime INTEGER DEFAULT NULL,
  confirm_overtime INTEGER DEFAULT NULL,
  finish_overtime INTEGER DEFAULT NULL,
  comment_overtime INTEGER DEFAULT NULL,
  PRIMARY KEY (id)
);

INSERT INTO oms_order_setting VALUES (1,60,1440,15,7,7);

-- H5支付系统数据库表结构
-- 创建时间：2025-07-26

-- 支付方式表
DROP TABLE IF EXISTS oms_payment_method CASCADE;
CREATE TABLE IF NOT EXISTS oms_payment_method (
  id BIGSERIAL NOT NULL,
  method_code varchar(50) NOT NULL,
  method_name varchar(100) NOT NULL,
  channel varchar(50) NOT NULL,
  type varchar(20) NOT NULL,
  icon_url varchar(255) DEFAULT NULL,
  status INTEGER NOT NULL DEFAULT '1',
  min_amount decimal(10,2) DEFAULT '0.01',
  max_amount decimal(10,2) DEFAULT '10000.00',
  fee_rate decimal(5,4) DEFAULT '0.0000',
  fixed_fee decimal(10,2) DEFAULT '0.00',
  sort INTEGER DEFAULT '0',
  supported_currency varchar(100) DEFAULT 'JPY,CNY',
  config text,
  description varchar(255) DEFAULT NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uk_method_code UNIQUE (method_code)
);

-- 初始化支付方式数据
INSERT INTO oms_payment_method (method_code, method_name, channel, type, icon_url, status, min_amount, max_amount, fee_rate, fixed_fee, sort, supported_currency, description) VALUES
('STRIPE_CHECKOUT', 'Stripe Checkout', 'Stripe', 'H5', '/images/payment/stripe.png', 1, 0.01, 50000.00, 0.0290, 0.30, 1, 'JPY,USD,EUR,CNY', 'Stripe Checkout integration slot; local template confirms through LOCAL_SELF by default'),
('CREDIT_CARD', 'Credit Card', 'GlobePay', 'H5', '/images/payment/creditcard.png', 1, 0.01, 50000.00, 0.0350, 0.00, 2, 'JPY,USD,EUR,CNY', 'Optional card payment provider slot'),
('ALIPAY_H5', 'Alipay H5', 'Alipay', 'H5', '/images/payment/alipay.png', 1, 0.01, 10000.00, 0.0060, 0.00, 3, 'USD,CNY', 'Optional Alipay mobile web payment integration slot'),
('WECHAT_H5', 'WeChat H5', 'Wechat', 'H5', '/images/payment/wechat.png', 1, 0.01, 10000.00, 0.0060, 0.00, 4, 'USD,CNY', 'Optional WeChat mobile web payment integration slot');

-- 支付记录表
DROP TABLE IF EXISTS oms_payment_record CASCADE;
CREATE TABLE IF NOT EXISTS oms_payment_record (
  id BIGSERIAL NOT NULL,
  order_id BIGINT NOT NULL,
  order_sn varchar(64) NOT NULL,
  payment_channel varchar(50) NOT NULL,
  payment_method varchar(100) NOT NULL,
  payment_amount decimal(10,2) NOT NULL,
  currency varchar(10) NOT NULL DEFAULT 'JPY',
  payment_status varchar(20) NOT NULL DEFAULT 'PENDING',
  third_party_order_id varchar(100) DEFAULT NULL,
  third_party_trade_no varchar(100) DEFAULT NULL,
  payment_url varchar(500) DEFAULT NULL,
  qr_code_url varchar(500) DEFAULT NULL,
  request_params text,
  payment_response text,
  notify_response text,
  failure_reason varchar(255) DEFAULT NULL,
  payment_time timestamp NULL DEFAULT NULL,
  notify_time timestamp NULL DEFAULT NULL,
  expire_time timestamp NULL DEFAULT NULL,
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

-- 支付通知日志表
DROP TABLE IF EXISTS oms_payment_notify_log CASCADE;
CREATE TABLE IF NOT EXISTS oms_payment_notify_log (
  id BIGSERIAL NOT NULL,
  order_sn varchar(64) NOT NULL,
  payment_channel varchar(50) NOT NULL,
  third_party_order_id varchar(100) DEFAULT NULL,
  third_party_trade_no varchar(100) DEFAULT NULL,
  notify_type varchar(50) NOT NULL,
  notify_status varchar(20) NOT NULL,
  notify_content text,
  process_result varchar(50) DEFAULT NULL,
  error_message varchar(255) DEFAULT NULL,
  notify_ip varchar(50) DEFAULT NULL,
  retry_count INTEGER DEFAULT '0',
  create_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

-- 退款申请表
DROP TABLE IF EXISTS refund_request CASCADE;
CREATE TABLE refund_request (
  id BIGSERIAL NOT NULL,
  refund_sn varchar(64) NOT NULL,
  order_id BIGINT NOT NULL,
  order_sn varchar(64) NOT NULL,
  payment_record_id BIGINT DEFAULT NULL,
  member_id BIGINT NOT NULL,
  member_username varchar(64) DEFAULT NULL,
  refund_amount decimal(10,2) NOT NULL,
  refund_reason varchar(500) DEFAULT NULL,
  status varchar(20) NOT NULL DEFAULT 'PENDING',
  apply_time TIMESTAMP NOT NULL,
  audit_time TIMESTAMP DEFAULT NULL,
  auditor_id BIGINT DEFAULT NULL,
  auditor_name varchar(64) DEFAULT NULL,
  audit_note varchar(500) DEFAULT NULL,
  third_party_refund_id varchar(64) DEFAULT NULL,
  refund_time TIMESTAMP DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uk_refund_sn UNIQUE (refund_sn)
);

-- 退款处理记录表
DROP TABLE IF EXISTS refund_process_log CASCADE;
CREATE TABLE refund_process_log (
  id BIGSERIAL NOT NULL,
  refund_request_id BIGINT NOT NULL,
  refund_sn varchar(64) NOT NULL,
  operation_type varchar(20) NOT NULL,
  operation_status varchar(20) NOT NULL,
  operator_id BIGINT DEFAULT NULL,
  operator_name varchar(64) DEFAULT NULL,
  operation_note varchar(500) DEFAULT NULL,
  request_params text,
  response_data text,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);


-- ----------------------------
-- 邮件相关表
-- ----------------------------

-- 邮件模板表
DROP TABLE IF EXISTS sms_email_template CASCADE;
CREATE TABLE sms_email_template (
  id BIGSERIAL NOT NULL,
  template_name varchar(100) NOT NULL,
  trigger_scene INTEGER NOT NULL DEFAULT '0',
  subject varchar(200) NOT NULL,
  content text,
  status INTEGER NOT NULL DEFAULT '1',
  note varchar(500) DEFAULT NULL,
  create_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

-- 插入邮件模板
INSERT INTO sms_email_template (id, template_name, trigger_scene, subject, content, status, create_time, update_time) VALUES
  (1,'Order Confirmation',0,'[Mall Template] Order confirmation','Hi [[${userName}]],\r\n\r\nThanks for your order. Here are the order details:\r\n\r\nOrder ID: [[${orderId}]]\r\nOrder time: [[${orderDate}]]\r\nItems: [[${orderList}]]\r\nTotal: [[${totalAmount}]]\r\n\r\nThis template email is ready for local validation. Replace the support signature and fulfillment copy before production use.\r\n\r\nRegards,\r\nMall Template Support\r\nsupport@mall-template.test',1,'2026-06-30 00:00:00','2026-06-30 00:00:00'),
  (2,'Shipment Update',1,'[Mall Template] Shipment update','Hi [[${userName}]],\r\n\r\nOrder [[${orderId}]] has a shipment update:\r\n\r\nCarrier: [[${carrier}]]\r\nTracking number: [[${trackingNumber}]]\r\nTracking URL: [[${trackingUrl}]]\r\n\r\nUse this template as a placeholder until a real logistics provider is connected.\r\n\r\nRegards,\r\nMall Template Support\r\nsupport@mall-template.test',1,'2026-06-30 00:00:00','2026-06-30 00:00:00'),
  (3,'Registration Confirmation',2,'[Mall Template] Confirm your email','Hi [[${userName}]],\r\n\r\nThanks for creating an account. Confirm your email with this link:\r\n\r\n[[${confirmationLink}]]\r\n\r\nThe link is valid for 24 hours. You can ignore this message if you did not create the account.\r\n\r\nRegards,\r\nMall Template Support\r\nsupport@mall-template.test',1,'2026-06-30 00:00:00','2026-06-30 00:00:00'),
  (4,'Order Edit Reminder',3,'[Mall Template] Order edit reminder','Hi [[${userName}]],\r\n\r\nOrder [[${orderId}]] can be edited before fulfillment begins.\r\n\r\nCurrent address:\r\n[[${nowAddress}]]\r\n\r\nPlease finish any changes before [[${modifyDeadline}]].\r\n\r\nRegards,\r\nMall Template Support\r\nsupport@mall-template.test',1,'2026-06-30 00:00:00','2026-06-30 00:00:00'),
  (5,'Password Reset',4,'[Mall Template] Reset your password','Hi [[${userName}]],\r\n\r\nUse this link to reset your password:\r\n\r\n[[${resetLink}]]\r\n\r\nThe link is valid for 30 minutes. You can ignore this message if you did not request a password reset.\r\n\r\nRegards,\r\nMall Template Support\r\nsupport@mall-template.test',1,'2026-06-30 00:00:00','2026-06-30 00:00:00');


  -- 每日数据统计表
DROP TABLE IF EXISTS daily_statistics CASCADE;
CREATE TABLE IF NOT EXISTS daily_statistics (
  id BIGSERIAL NOT NULL,
  statistics_date date NOT NULL,
  daily_sales decimal(15,2) DEFAULT '0.00',
  daily_order_count BIGINT DEFAULT '0',
  daily_new_users BIGINT DEFAULT '0',
  daily_completed_orders BIGINT DEFAULT '0',
  daily_cancelled_orders BIGINT DEFAULT '0',
  daily_avg_order_amount decimal(15,2) DEFAULT '0.00',
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id),
  CONSTRAINT uk_statistics_date UNIQUE (statistics_date)
);

-- 订单状态更新记录表（用于记录需要发送通知的订单状态变更）
DROP TABLE IF EXISTS order_status_notification_log CASCADE;
CREATE TABLE IF NOT EXISTS order_status_notification_log (
  id BIGSERIAL NOT NULL,
  order_id BIGINT NOT NULL,
  order_sn varchar(64) DEFAULT NULL,
  member_id BIGINT NOT NULL,
  old_status INTEGER DEFAULT NULL,
  new_status INTEGER NOT NULL,
  notification_type varchar(50) NOT NULL,
  notification_status INTEGER DEFAULT '0',
  retry_count INTEGER DEFAULT '0',
  error_message text,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

-- 支付回调重试记录表
DROP TABLE IF EXISTS payment_callback_retry_log CASCADE;
CREATE TABLE IF NOT EXISTS payment_callback_retry_log (
  id BIGSERIAL NOT NULL,
  order_id BIGINT NOT NULL,
  order_sn varchar(64) DEFAULT NULL,
  payment_method varchar(50) NOT NULL,
  callback_url varchar(500) NOT NULL,
  callback_data text,
  retry_count INTEGER DEFAULT '0',
  max_retry_count INTEGER DEFAULT '3',
  status INTEGER DEFAULT '0',
  error_message text,
  next_retry_time TIMESTAMP DEFAULT NULL,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);

-- ----------------------------
-- 基础设置表
-- ----------------------------

-- 系统设置表
DROP TABLE IF EXISTS sys_setting CASCADE;
CREATE TABLE sys_setting (
  id BIGSERIAL NOT NULL,
  setting_key varchar(100) NOT NULL,
  setting_value text,
  setting_name varchar(200) DEFAULT NULL,
  description varchar(500) DEFAULT NULL,
  type INTEGER DEFAULT '1',
  status INTEGER DEFAULT '1',
  create_time TIMESTAMP DEFAULT NULL,
  update_time TIMESTAMP DEFAULT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_setting_key UNIQUE (setting_key)
);

-- 系统设置数据
INSERT INTO sys_setting (id, setting_key, setting_value, setting_name, description, type, status, create_time, update_time) VALUES
(1, 'customer_support_email', 'support@mall-template.test', 'Support Email', 'Default storefront support mailbox', 2, 1, '2026-06-30 00:00:00', '2026-06-30 00:00:00'),
(2, 'customer_service_name', 'Template Support', 'Support Name', 'Default customer support display name', 2, 1, '2026-06-30 00:00:00', '2026-06-30 00:00:00'),
(3, 'customer_qrcode_url', '/template-assets/avatar-demo.svg', 'Support Image URL', 'Replace with a real QR code or support image for production', 2, 1, '2026-06-30 00:00:00', '2026-06-30 00:00:00');

-- ----------------------------
-- 退款系统相关表
-- ----------------------------

-- 退款申请表
DROP TABLE IF EXISTS refund_request CASCADE;
CREATE TABLE refund_request (
  id BIGSERIAL NOT NULL,
  refund_sn varchar(64) NOT NULL,
  order_id BIGINT NOT NULL,
  order_sn varchar(64) NOT NULL,
  payment_record_id BIGINT DEFAULT NULL,
  member_id BIGINT NOT NULL,
  member_username varchar(64) NOT NULL,
  refund_amount decimal(10,2) NOT NULL,
  refund_reason varchar(500) NOT NULL,
  status varchar(50) NOT NULL DEFAULT 'PENDING_AUDIT',
  apply_time TIMESTAMP NOT NULL,
  audit_time TIMESTAMP DEFAULT NULL,
  auditor_id BIGINT DEFAULT NULL,
  auditor_name varchar(64) DEFAULT NULL,
  audit_note varchar(500) DEFAULT NULL,
  third_party_refund_id varchar(128) DEFAULT NULL,
  refund_time TIMESTAMP DEFAULT NULL,
  create_time TIMESTAMP NOT NULL,
  update_time TIMESTAMP NOT NULL,
  PRIMARY KEY (id),
  CONSTRAINT uk_refund_sn UNIQUE (refund_sn)
);

-- 退款处理日志表
DROP TABLE IF EXISTS refund_process_log CASCADE;
CREATE TABLE refund_process_log (
  id BIGSERIAL NOT NULL,
  refund_request_id BIGINT NOT NULL,
  refund_sn varchar(64) NOT NULL,
  operation_type varchar(50) NOT NULL,
  operation_content varchar(500) DEFAULT NULL,
  operation_status varchar(50) DEFAULT NULL,
  request_data text,
  response_data text,
  error_message varchar(1000) DEFAULT NULL,
  operator_id BIGINT DEFAULT NULL,
  operator_name varchar(64) DEFAULT NULL,
  operation_time TIMESTAMP NOT NULL,
  create_time TIMESTAMP NOT NULL,
  PRIMARY KEY (id)
);

-- ----------------------------
-- 轮播图表（最小化版本）
-- ----------------------------
DROP TABLE IF EXISTS cms_banner CASCADE;
CREATE TABLE cms_banner (
  id BIGSERIAL NOT NULL,
  title varchar(100) NOT NULL,
  image_url varchar(500) NOT NULL,
  link_url varchar(500) DEFAULT NULL,
  sort INTEGER DEFAULT 0,
  status SMALLINT DEFAULT 1,
  create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  update_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (id)
);
CREATE INDEX idx_cms_banner_status_sort ON cms_banner (status, sort DESC);

-- ----------------------------
-- 插入示例数据
-- ----------------------------
INSERT INTO cms_banner (title, image_url, link_url, sort, status) VALUES
('New Starter Products', '/template-assets/banner.svg', '/products/new', 100, 1),
('Daily Tech Picks', '/template-assets/category-1.svg', '/products?categoryId=1', 90, 1),
('Carry Essentials', '/template-assets/category-3.svg', '/products?categoryId=3', 80, 1);


-- 执行完成提示
SELECT 'Mall template database initialization complete.' AS message;


-- Reset sequences after explicit seed IDs.
SELECT setval(pg_get_serial_sequence('cms_banner', 'id'), COALESCE((SELECT MAX(id) FROM cms_banner), 1), true);
SELECT setval(pg_get_serial_sequence('cms_subject', 'id'), COALESCE((SELECT MAX(id) FROM cms_subject), 1), true);
SELECT setval(pg_get_serial_sequence('cms_prefrence_area_product_relation', 'id'), COALESCE((SELECT MAX(id) FROM cms_prefrence_area_product_relation), 1), true);
SELECT setval(pg_get_serial_sequence('cms_subject_product_relation', 'id'), COALESCE((SELECT MAX(id) FROM cms_subject_product_relation), 1), true);
SELECT setval(pg_get_serial_sequence('oms_cart_item', 'id'), COALESCE((SELECT MAX(id) FROM oms_cart_item), 1), true);
SELECT setval(pg_get_serial_sequence('oms_order', 'id'), COALESCE((SELECT MAX(id) FROM oms_order), 1), true);
SELECT setval(pg_get_serial_sequence('oms_order_item', 'id'), COALESCE((SELECT MAX(id) FROM oms_order_item), 1), true);
SELECT setval(pg_get_serial_sequence('oms_order_operate_history', 'id'), COALESCE((SELECT MAX(id) FROM oms_order_operate_history), 1), true);
SELECT setval(pg_get_serial_sequence('oms_order_return_apply', 'id'), COALESCE((SELECT MAX(id) FROM oms_order_return_apply), 1), true);
SELECT setval(pg_get_serial_sequence('oms_order_return_reason', 'id'), COALESCE((SELECT MAX(id) FROM oms_order_return_reason), 1), true);
SELECT setval(pg_get_serial_sequence('oms_order_setting', 'id'), COALESCE((SELECT MAX(id) FROM oms_order_setting), 1), true);
SELECT setval(pg_get_serial_sequence('pms_brand', 'id'), COALESCE((SELECT MAX(id) FROM pms_brand), 1), true);
SELECT setval(pg_get_serial_sequence('pms_comment', 'id'), COALESCE((SELECT MAX(id) FROM pms_comment), 1), true);
SELECT setval(pg_get_serial_sequence('pms_comment_replay', 'id'), COALESCE((SELECT MAX(id) FROM pms_comment_replay), 1), true);
SELECT setval(pg_get_serial_sequence('pms_feight_template', 'id'), COALESCE((SELECT MAX(id) FROM pms_feight_template), 1), true);
SELECT setval(pg_get_serial_sequence('pms_member_price', 'id'), COALESCE((SELECT MAX(id) FROM pms_member_price), 1), true);
SELECT setval(pg_get_serial_sequence('pms_product', 'id'), COALESCE((SELECT MAX(id) FROM pms_product), 1), true);
SELECT setval(pg_get_serial_sequence('pms_product_attribute', 'id'), COALESCE((SELECT MAX(id) FROM pms_product_attribute), 1), true);
SELECT setval(pg_get_serial_sequence('pms_product_attribute_category', 'id'), COALESCE((SELECT MAX(id) FROM pms_product_attribute_category), 1), true);
SELECT setval(pg_get_serial_sequence('pms_product_attribute_value', 'id'), COALESCE((SELECT MAX(id) FROM pms_product_attribute_value), 1), true);
SELECT setval(pg_get_serial_sequence('pms_product_category', 'id'), COALESCE((SELECT MAX(id) FROM pms_product_category), 1), true);
SELECT setval(pg_get_serial_sequence('pms_product_category_attribute_relation', 'id'), COALESCE((SELECT MAX(id) FROM pms_product_category_attribute_relation), 1), true);
SELECT setval(pg_get_serial_sequence('pms_product_full_reduction', 'id'), COALESCE((SELECT MAX(id) FROM pms_product_full_reduction), 1), true);
SELECT setval(pg_get_serial_sequence('pms_product_ladder', 'id'), COALESCE((SELECT MAX(id) FROM pms_product_ladder), 1), true);
SELECT setval(pg_get_serial_sequence('pms_sku_stock', 'id'), COALESCE((SELECT MAX(id) FROM pms_sku_stock), 1), true);
SELECT setval(pg_get_serial_sequence('refund_process_log', 'id'), COALESCE((SELECT MAX(id) FROM refund_process_log), 1), true);
SELECT setval(pg_get_serial_sequence('refund_request', 'id'), COALESCE((SELECT MAX(id) FROM refund_request), 1), true);
SELECT setval(pg_get_serial_sequence('sms_coupon', 'id'), COALESCE((SELECT MAX(id) FROM sms_coupon), 1), true);
SELECT setval(pg_get_serial_sequence('sms_coupon_history', 'id'), COALESCE((SELECT MAX(id) FROM sms_coupon_history), 1), true);
SELECT setval(pg_get_serial_sequence('sms_coupon_product_category_relation', 'id'), COALESCE((SELECT MAX(id) FROM sms_coupon_product_category_relation), 1), true);
SELECT setval(pg_get_serial_sequence('sms_coupon_product_relation', 'id'), COALESCE((SELECT MAX(id) FROM sms_coupon_product_relation), 1), true);
SELECT setval(pg_get_serial_sequence('sms_email_template', 'id'), COALESCE((SELECT MAX(id) FROM sms_email_template), 1), true);
SELECT setval(pg_get_serial_sequence('sms_flash_promotion', 'id'), COALESCE((SELECT MAX(id) FROM sms_flash_promotion), 1), true);
SELECT setval(pg_get_serial_sequence('sms_flash_promotion_product_relation', 'id'), COALESCE((SELECT MAX(id) FROM sms_flash_promotion_product_relation), 1), true);
SELECT setval(pg_get_serial_sequence('sms_flash_promotion_session', 'id'), COALESCE((SELECT MAX(id) FROM sms_flash_promotion_session), 1), true);
SELECT setval(pg_get_serial_sequence('sms_home_advertise', 'id'), COALESCE((SELECT MAX(id) FROM sms_home_advertise), 1), true);
SELECT setval(pg_get_serial_sequence('sms_home_brand', 'id'), COALESCE((SELECT MAX(id) FROM sms_home_brand), 1), true);
SELECT setval(pg_get_serial_sequence('sms_home_new_product', 'id'), COALESCE((SELECT MAX(id) FROM sms_home_new_product), 1), true);
SELECT setval(pg_get_serial_sequence('sms_home_recommend_product', 'id'), COALESCE((SELECT MAX(id) FROM sms_home_recommend_product), 1), true);
SELECT setval(pg_get_serial_sequence('sms_home_recommend_subject', 'id'), COALESCE((SELECT MAX(id) FROM sms_home_recommend_subject), 1), true);
SELECT setval(pg_get_serial_sequence('sys_setting', 'id'), COALESCE((SELECT MAX(id) FROM sys_setting), 1), true);
SELECT setval(pg_get_serial_sequence('ums_admin', 'id'), COALESCE((SELECT MAX(id) FROM ums_admin), 1), true);
SELECT setval(pg_get_serial_sequence('ums_admin_login_log', 'id'), COALESCE((SELECT MAX(id) FROM ums_admin_login_log), 1), true);
SELECT setval(pg_get_serial_sequence('ums_admin_permission_relation', 'id'), COALESCE((SELECT MAX(id) FROM ums_admin_permission_relation), 1), true);
SELECT setval(pg_get_serial_sequence('ums_admin_role_relation', 'id'), COALESCE((SELECT MAX(id) FROM ums_admin_role_relation), 1), true);
SELECT setval(pg_get_serial_sequence('ums_integration_change_history', 'id'), COALESCE((SELECT MAX(id) FROM ums_integration_change_history), 1), true);
SELECT setval(pg_get_serial_sequence('ums_integration_consume_setting', 'id'), COALESCE((SELECT MAX(id) FROM ums_integration_consume_setting), 1), true);
SELECT setval(pg_get_serial_sequence('ums_member', 'id'), COALESCE((SELECT MAX(id) FROM ums_member), 1), true);
SELECT setval(pg_get_serial_sequence('ums_member_level', 'id'), COALESCE((SELECT MAX(id) FROM ums_member_level), 1), true);
SELECT setval(pg_get_serial_sequence('ums_member_receive_address', 'id'), COALESCE((SELECT MAX(id) FROM ums_member_receive_address), 1), true);
SELECT setval(pg_get_serial_sequence('ums_menu', 'id'), COALESCE((SELECT MAX(id) FROM ums_menu), 1), true);
SELECT setval(pg_get_serial_sequence('ums_permission', 'id'), COALESCE((SELECT MAX(id) FROM ums_permission), 1), true);
SELECT setval(pg_get_serial_sequence('ums_resource', 'id'), COALESCE((SELECT MAX(id) FROM ums_resource), 1), true);
SELECT setval(pg_get_serial_sequence('ums_role', 'id'), COALESCE((SELECT MAX(id) FROM ums_role), 1), true);
SELECT setval(pg_get_serial_sequence('ums_role_menu_relation', 'id'), COALESCE((SELECT MAX(id) FROM ums_role_menu_relation), 1), true);
SELECT setval(pg_get_serial_sequence('ums_role_permission_relation', 'id'), COALESCE((SELECT MAX(id) FROM ums_role_permission_relation), 1), true);
SELECT setval(pg_get_serial_sequence('ums_role_resource_relation', 'id'), COALESCE((SELECT MAX(id) FROM ums_role_resource_relation), 1), true);
