package com.example.jdbc.ex05dao;

import com.example.jdbc.common.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * [교안 5.2] member 테이블의 CRUD를 한 클래스에 모은 DAO
 * - 메서드마다 커넥션을 얻고, 일이 끝나면 닫는다(try-with-resources).
 * - 검사 예외 SQLException은 실행 예외로 바꿔 던진다. 호출하는 쪽이 JDBC를 몰라도 되게 하려는 것이다.
 */
public class MemberDao {

    private final DataSource dataSource;

    public MemberDao(DataSource dataSource) {   // 접속 정보는 밖에서 받는다
        this.dataSource = dataSource;
    }

    /** 저장 — DB가 붙인 id를 받아 member에 넣고 돌려준다 */
    public Member save(Member member) {
        String sql = "INSERT INTO member (name, age) VALUES (?, ?)";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, member.getName());
            pstmt.setInt(2, member.getAge());
            pstmt.executeUpdate();
            try (ResultSet keys = pstmt.getGeneratedKeys()) {   // AUTO_INCREMENT로 만들어진 키
                keys.next();
                member.setId(keys.getLong(1));
            }
            return member;
        } catch (SQLException e) {
            throw new IllegalStateException("회원 저장 실패", e);
        }
    }

    /** id로 한 명 조회 */
    public Optional<Member> findById(Long id) {
        String sql = "SELECT id, name, age FROM member WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(toMember(rs));
                }
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new IllegalStateException("회원 조회 실패: id=" + id, e);
        }
    }

    /** 전체 조회 */
    public List<Member> findAll() {
        String sql = "SELECT id, name, age FROM member ORDER BY id";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            List<Member> result = new ArrayList<>();
            while (rs.next()) {
                result.add(toMember(rs));
            }
            return result;
        } catch (SQLException e) {
            throw new IllegalStateException("회원 목록 조회 실패", e);
        }
    }

    /** 나이 수정 — 수정된 행 수를 돌려준다(없는 id면 0) */
    public int updateAge(Long id, int age) {
        String sql = "UPDATE member SET age = ? WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, age);
            pstmt.setLong(2, id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("회원 수정 실패: id=" + id, e);
        }
    }

    /** 삭제 — 삭제된 행 수를 돌려준다(없는 id면 0) */
    public int delete(Long id) {
        String sql = "DELETE FROM member WHERE id = ?";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new IllegalStateException("회원 삭제 실패: id=" + id, e);
        }
    }

    /** 현재 행 → Member */
    private Member toMember(ResultSet rs) throws SQLException {
        return new Member(rs.getLong("id"), rs.getString("name"), rs.getInt("age"));
    }
}
