# 前台分类更新API文档

## 新增接口

### 前台更新分类信息（接收图片URL）

**接口地址:** `POST /admin/productCategory/frontend/update/{id}`

**功能描述:** 
- 供前台调用，用于更新商品分类的名称、子标题和图片URL
- 接收图片URL字符串，直接存储到数据库
- 图片上传和"限制一张"的逻辑由前台页面处理

**请求参数:**

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| id | Long | 是 | 分类ID（路径参数） |
| name | String | 否 | 分类名称 |
| subTitle | String | 否 | 分类子标题/副名称 |
| imageUrl | String | 否 | 图片URL地址 |

**请求示例:**

使用 `application/x-www-form-urlencoded` 格式：
```
POST /admin/productCategory/frontend/update/1
Content-Type: application/x-www-form-urlencoded

name=电子烟&subTitle=新一代电子烟产品&imageUrl=https://example.com/images/category1.jpg
```

或使用 `multipart/form-data` 格式：
```
POST /admin/productCategory/frontend/update/1
Content-Type: multipart/form-data

name: 电子烟
subTitle: 新一代电子烟产品
imageUrl: https://example.com/images/category1.jpg
```

**响应示例:**

成功响应:
```json
{
    "code": 200,
    "message": "操作成功",
    "data": "更新成功"
}
```

失败响应:
```json
{
    "code": 500,
    "message": "更新失败: [具体错误信息]",
    "data": null
}
```

**处理逻辑:**
1. 名称和子标题会自动去除首尾空格
2. 空值或空字符串的参数会被忽略（不更新对应字段）
3. 只更新提供的非空参数，其他字段保持不变
4. 图片URL直接存储到数据库的icon字段

**技术实现:**
- 简单的字符串参数处理，无文件上传逻辑
- 使用 `updateByPrimaryKeySelective` 只更新非空字段
- 支持事务处理，确保数据一致性
- 轻量级实现，性能优良

## 使用场景

该接口主要用于：
1. 前台用户更新分类基本信息
2. 前台通过其他接口上传图片后，传递图片URL更新分类
3. 管理系统的分类维护功能
4. 批量更新分类信息的场景

## 与其他接口配合

建议的前台使用流程：
1. 如需上传图片，先调用图片上传接口获取URL
2. 调用本接口，传递图片URL和其他信息
3. 前台控制"限制一张图片"的逻辑

## 优势

1. **简单高效**: 无复杂的文件处理逻辑
2. **职责分离**: 图片上传和分类更新分离
3. **灵活性强**: 可以更新任意组合的字段
4. **性能优良**: 轻量级实现，响应快速
5. **易于维护**: 代码简洁，易于理解和维护