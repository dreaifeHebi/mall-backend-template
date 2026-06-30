package com.macro.mall.portal.repository.impl;

import com.macro.mall.portal.domain.MemberBrandAttention;
import com.macro.mall.portal.repository.MemberBrandAttentionRepository;
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

/**
 * 会员品牌关注Repository PostgreSQL实现
 */
@Repository
public class MemberBrandAttentionRepositoryImpl implements MemberBrandAttentionRepository {
    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<MemberBrandAttention> rowMapper = new RowMapper<MemberBrandAttention>() {
        @Override
        public MemberBrandAttention mapRow(ResultSet rs, int rowNum) throws SQLException {
            MemberBrandAttention item = new MemberBrandAttention();
            item.setId(String.valueOf(rs.getLong("id")));
            item.setMemberId(rs.getLong("member_id"));
            item.setMemberNickname(rs.getString("member_nickname"));
            item.setMemberIcon(rs.getString("member_icon"));
            item.setBrandId(rs.getLong("brand_id"));
            item.setBrandName(rs.getString("brand_name"));
            item.setBrandLogo(rs.getString("brand_logo"));
            item.setBrandCity(rs.getString("brand_city"));
            item.setCreateTime(rs.getTimestamp("create_time"));
            return item;
        }
    };

    @PostConstruct
    public void initTable() {
        jdbcTemplate.execute("CREATE TABLE IF NOT EXISTS ums_member_brand_attention (" +
                "id BIGSERIAL PRIMARY KEY, " +
                "member_id BIGINT NOT NULL, " +
                "member_nickname VARCHAR(64), " +
                "member_icon VARCHAR(500), " +
                "brand_id BIGINT NOT NULL, " +
                "brand_name VARCHAR(200), " +
                "brand_logo VARCHAR(500), " +
                "brand_city VARCHAR(255), " +
                "create_time TIMESTAMP DEFAULT CURRENT_TIMESTAMP, " +
                "CONSTRAINT uk_member_brand_attention UNIQUE(member_id, brand_id)" +
                ")");
    }

    @Override
    public int save(MemberBrandAttention brandAttention) {
        return jdbcTemplate.update(
                "INSERT INTO ums_member_brand_attention " +
                        "(member_id, member_nickname, member_icon, brand_id, brand_name, brand_logo, brand_city, create_time) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP) " +
                        "ON CONFLICT (member_id, brand_id) DO UPDATE SET " +
                        "member_nickname = EXCLUDED.member_nickname, " +
                        "member_icon = EXCLUDED.member_icon, " +
                        "brand_name = EXCLUDED.brand_name, " +
                        "brand_logo = EXCLUDED.brand_logo, " +
                        "brand_city = EXCLUDED.brand_city, " +
                        "create_time = CURRENT_TIMESTAMP",
                brandAttention.getMemberId(),
                brandAttention.getMemberNickname(),
                brandAttention.getMemberIcon(),
                brandAttention.getBrandId(),
                brandAttention.getBrandName(),
                brandAttention.getBrandLogo(),
                brandAttention.getBrandCity()
        );
    }

    @Override
    public MemberBrandAttention findByMemberIdAndBrandId(Long memberId, Long brandId) {
        List<MemberBrandAttention> list = jdbcTemplate.query(
                "SELECT * FROM ums_member_brand_attention WHERE member_id = ? AND brand_id = ? LIMIT 1",
                rowMapper,
                memberId,
                brandId
        );
        return list.isEmpty() ? null : list.get(0);
    }

    @Override
    public int deleteByMemberIdAndBrandId(Long memberId, Long brandId) {
        return jdbcTemplate.update(
                "DELETE FROM ums_member_brand_attention WHERE member_id = ? AND brand_id = ?",
                memberId,
                brandId
        );
    }

    @Override
    public Page<MemberBrandAttention> findByMemberId(Long memberId, Pageable pageable) {
        Long total = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM ums_member_brand_attention WHERE member_id = ?",
                Long.class,
                memberId
        );
        List<MemberBrandAttention> list = total == null || total == 0
                ? Collections.emptyList()
                : jdbcTemplate.query(
                        "SELECT * FROM ums_member_brand_attention WHERE member_id = ? ORDER BY create_time DESC, id DESC LIMIT ? OFFSET ?",
                        rowMapper,
                        memberId,
                        pageable.getPageSize(),
                        pageable.getOffset()
                );
        return new PageImpl<>(list, pageable, total == null ? 0 : total);
    }

    @Override
    public void deleteAllByMemberId(Long memberId) {
        jdbcTemplate.update("DELETE FROM ums_member_brand_attention WHERE member_id = ?", memberId);
    }
}
