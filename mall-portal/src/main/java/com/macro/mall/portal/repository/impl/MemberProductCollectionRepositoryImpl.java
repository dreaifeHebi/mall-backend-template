package com.macro.mall.portal.repository.impl;

import com.macro.mall.portal.domain.MemberProductCollection;
import com.macro.mall.portal.repository.MemberProductCollectionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

/**
 * 会员商品收藏Repository PostgreSQL实现
 * Created by macro on 2025/07/20.
 */
@Repository
public class MemberProductCollectionRepositoryImpl implements MemberProductCollectionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<MemberProductCollection> rowMapper = new RowMapper<MemberProductCollection>() {
        @Override
        public MemberProductCollection mapRow(ResultSet rs, int rowNum) throws SQLException {
            MemberProductCollection item = new MemberProductCollection();
            item.setId(String.valueOf(rs.getLong("id")));
            item.setMemberId(rs.getLong("member_id"));
            item.setMemberNickname(rs.getString("member_nickname"));
            item.setMemberIcon(rs.getString("member_icon"));
            item.setProductId(rs.getLong("product_id"));
            item.setProductName(rs.getString("product_name"));
            item.setProductPic(rs.getString("product_pic"));
            item.setProductSubTitle(rs.getString("product_sub_title"));
            item.setProductPrice(rs.getString("product_price"));
            item.setCreateTime(rs.getTimestamp("create_time"));
            return item;
        }
    };

    @PostConstruct
    public void initTable() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS ums_member_product_collection (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "member_id BIGINT NOT NULL, " +
                "member_nickname VARCHAR(64), " +
                "member_icon VARCHAR(500), " +
                "product_id BIGINT NOT NULL, " +
                "product_name VARCHAR(200), " +
                "product_pic VARCHAR(500), " +
                "product_sub_title VARCHAR(255), " +
                "product_price VARCHAR(64), " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "CONSTRAINT uk_member_product_collection UNIQUE(member_id, product_id)" +
                ")");
    }

    @Override
    public int save(MemberProductCollection productCollection) {
        return jdbcTemplate.update(
                "INSERT INTO ums_member_product_collection " +
                        "(member_id, member_nickname, member_icon, product_id, product_name, product_pic, product_sub_title, product_price, create_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                        "ON CONFLICT (member_id, product_id) DO UPDATE SET " +
                        "member_nickname = EXCLUDED.member_nickname, " +
                        "member_icon = EXCLUDED.member_icon, " +
                        "product_name = EXCLUDED.product_name, " +
                        "product_pic = EXCLUDED.product_pic, " +
                        "product_sub_title = EXCLUDED.product_sub_title, " +
                        "product_price = EXCLUDED.product_price, " +
                        "create_time = CURRENT_TIMESTAMP",
                productCollection.getMemberId(),
                productCollection.getMemberNickname(),
                productCollection.getMemberIcon(),
                productCollection.getProductId(),
                productCollection.getProductName(),
                productCollection.getProductPic(),
                productCollection.getProductSubTitle(),
                productCollection.getProductPrice()
        );
    }

    @Override
    public MemberProductCollection findByMemberIdAndProductId(Long memberId, Long productId) {
        List<MemberProductCollection> list = jdbcTemplate.query(
                "SELECT * FROM ums_member_product_collection WHERE member_id = ? AND product_id = ? LIMIT 1",
                rowMapper,
                memberId,
                productId
        );
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int deleteByMemberIdAndProductId(Long memberId, Long productId) {
        return jdbcTemplate.update(
                "DELETE FROM ums_member_product_collection WHERE member_id = ? AND product_id = ?",
                memberId,
                productId
        );
    }

    @Override
    public Page<MemberProductCollection> findByMemberId(Long memberId, Pageable pageable) {
        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ums_member_product_collection WHERE member_id = ?",
                Long.class,
                memberId
        );
        List<MemberProductCollection> list = total == null || total == 0
                ? Collections.emptyList()
                : jdbcTemplate.query(
                        "SELECT * FROM ums_member_product_collection WHERE member_id = ? ORDER BY create_time DESC, id DESC LIMIT ? OFFSET ?",
                        rowMapper,
                        memberId,
                        pageable.getPageSize(),
                        pageable.getOffset()
                );
        return new PageImpl<>(list, pageable, total == null ? 0 : total);
    }

    @Override
    public void deleteAllByMemberId(Long memberId) {
        jdbcTemplate.update("DELETE FROM ums_member_product_collection WHERE member_id = ?", memberId);
    }

}
