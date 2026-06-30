package com.macro.mall.portal.repository.impl;

import com.macro.mall.portal.domain.MemberReadHistory;
import com.macro.mall.portal.repository.MemberReadHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 会员商品浏览历史Repository PostgreSQL实现
 */
@Repository
public class MemberReadHistoryRepositoryImpl implements MemberReadHistoryRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<MemberReadHistory> rowMapper = new RowMapper<MemberReadHistory>() {
        @Override
        public MemberReadHistory mapRow(ResultSet rs, int rowNum) throws SQLException {
            MemberReadHistory item = new MemberReadHistory();
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
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS ums_member_read_history (" +
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
                "CONSTRAINT uk_member_read_history UNIQUE(member_id, product_id)" +
                ")");
    }

    @Override
    public int save(MemberReadHistory memberReadHistory) {
        return jdbcTemplate.update(
                "INSERT INTO ums_member_read_history " +
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
                memberReadHistory.getMemberId(),
                memberReadHistory.getMemberNickname(),
                memberReadHistory.getMemberIcon(),
                memberReadHistory.getProductId(),
                memberReadHistory.getProductName(),
                memberReadHistory.getProductPic(),
                memberReadHistory.getProductSubTitle(),
                memberReadHistory.getProductPrice()
        );
    }

    @Override
    public Page<MemberReadHistory> findByMemberIdOrderByCreateTimeDesc(Long memberId, Pageable pageable) {
        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ums_member_read_history WHERE member_id = ?",
                Long.class,
                memberId
        );
        List<MemberReadHistory> list = total == null || total == 0
                ? Collections.emptyList()
                : jdbcTemplate.query(
                        "SELECT * FROM ums_member_read_history WHERE member_id = ? ORDER BY create_time DESC, id DESC LIMIT ? OFFSET ?",
                        rowMapper,
                        memberId,
                        pageable.getPageSize(),
                        pageable.getOffset()
                );
        return new PageImpl<>(list, pageable, total == null ? 0 : total);
    }

    @Override
    public int deleteByMemberIdAndIds(Long memberId, List<String> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }

        List<Long> numericIds = ids.stream()
                .map(this::parseId)
                .filter(id -> id != null)
                .collect(Collectors.toList());

        if (numericIds.isEmpty()) {
            return 0;
        }

        String placeholders = numericIds.stream().map(id -> "?").collect(Collectors.joining(","));
        Object[] args = new Object[numericIds.size() + 1];
        args[0] = memberId;
        for (int i = 0; i < numericIds.size(); i++) {
            args[i + 1] = numericIds.get(i);
        }

        return jdbcTemplate.update(
                "DELETE FROM ums_member_read_history WHERE member_id = ? AND id IN (" + placeholders + ")",
                args
        );
    }

    @Override
    public void deleteAllByMemberId(Long memberId) {
        jdbcTemplate.update("DELETE FROM ums_member_read_history WHERE member_id = ?", memberId);
    }

    private Long parseId(String value) {
        try {
            return Long.valueOf(value);
        } catch (NumberFormatException e) {
            return null;
        }
    }
}
