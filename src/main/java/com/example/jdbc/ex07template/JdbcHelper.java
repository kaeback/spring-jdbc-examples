package com.example.jdbc.ex07template;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * [교안 7.2] MemberDao에서 메서드마다 반복되던 코드를 한곳에 모은 도우미
 * 커넥션 얻기 → PreparedStatement 만들기 → ? 에 값 넣기 → 실행 → 자원 닫기 → 예외 바꾸기
 * (스프링의 JdbcTemplate이 같은 일을 한다)
 */
public class JdbcHelper {

    private final DataSource dataSource;

    public JdbcHelper(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    /** INSERT·UPDATE·DELETE — 영향을 받은 행 수를 돌려준다 */
    public int update(String sql, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            bind(pstmt, params);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("SQL 실행 실패: " + sql, e);
        }
    }

    /** SELECT — 행마다 rowMapper로 객체를 만들어 목록으로 돌려준다 */
    public <T> List<T> query(String sql, RowMapper<T> rowMapper, Object... params) {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            bind(pstmt, params);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(rowMapper.mapRow(rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new IllegalStateException("SQL 실행 실패: " + sql, e);
        }
    }

    /** SELECT — 결과가 한 행 이하일 때 */
    public <T> Optional<T> queryOne(String sql, RowMapper<T> rowMapper, Object... params) {
        return query(sql, rowMapper, params).stream().findFirst();
    }

    /** ? 자리에 순서대로 값을 넣는다(번호는 1부터) */
    private void bind(PreparedStatement pstmt, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            pstmt.setObject(i + 1, params[i]);
        }
    }
}
