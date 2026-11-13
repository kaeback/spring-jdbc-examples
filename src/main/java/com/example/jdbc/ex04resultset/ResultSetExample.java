package com.example.jdbc.ex04resultset;

import com.example.jdbc.common.Database;
import com.example.jdbc.common.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * [교안 4.2] ResultSet의 행을 Member 객체로 옮긴다
 */
public class ResultSetExample {

    public static void main(String[] args) throws SQLException {
        DataSource dataSource = Database.dataSource();
        Database.init(dataSource);   // member: 홍길동, 김철수, 이영희

        try (Connection conn = dataSource.getConnection()) {
            System.out.println("== 여러 행 → List<Member> ==");
            List<Member> members = findOlderThan(conn, 21);
            members.forEach(System.out::println);

            System.out.println();
            System.out.println("== 한 행 → Optional<Member> ==");
            System.out.println("id=1  → " + findById(conn, 1L));
            System.out.println("id=99 → " + findById(conn, 99L));

            System.out.println();
            System.out.println("== 값 하나 → int ==");
            System.out.println("회원 수: " + count(conn));
        }
    }

    /** 여러 행: while (rs.next())로 끝까지 돌며 한 행마다 Member를 만든다 */
    static List<Member> findOlderThan(Connection conn, int age) throws SQLException {
        String sql = "SELECT id, name, age FROM member WHERE age > ? ORDER BY id";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, age);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Member> result = new ArrayList<>();
                while (rs.next()) {              // 다음 행으로 이동. 행이 없으면 false
                    result.add(toMember(rs));
                }
                return result;
            }
        }
    }

    /** 한 행: if (rs.next())로 있으면 Member, 없으면 빈 Optional */
    static Optional<Member> findById(Connection conn, Long id) throws SQLException {
        String sql = "SELECT id, name, age FROM member WHERE id = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(toMember(rs));
                }
                return Optional.empty();
            }
        }
    }

    /** 값 하나: 결과는 1행 1열이므로 rs.next() 한 번 후 열 번호(1)로 읽는다 */
    static int count(Connection conn) throws SQLException {
        try (PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM member");
             ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            return rs.getInt(1);   // 열 번호는 1부터 시작한다
        }
    }

    /** 현재 커서가 가리키는 행의 컬럼 값을 꺼내 Member를 만든다 */
    static Member toMember(ResultSet rs) throws SQLException {
        return new Member(
                rs.getLong("id"),        // BIGINT  → long
                rs.getString("name"),    // VARCHAR → String
                rs.getInt("age"));       // INT     → int
    }
}
