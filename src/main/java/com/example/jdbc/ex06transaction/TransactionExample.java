package com.example.jdbc.ex06transaction;

import com.example.jdbc.common.Database;
import com.example.jdbc.common.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * [교안 6.3] 회원 여러 명을 한꺼번에 등록하다가 중간에 실패하면?
 * 1) 자동 커밋 — 실패 전까지 등록한 회원은 그대로 남는다
 * 2) 트랜잭션 — 하나라도 실패하면 rollback()으로 모두 취소한다
 */
public class TransactionExample {

    // 세 번째 회원은 나이가 -1이라 CHECK (age >= 0) 조건을 어긴다
    static final List<Member> NEW_MEMBERS = List.of(
            new Member("박민수", 28),
            new Member("최지은", 22),
            new Member("정하늘", -1));

    public static void main(String[] args) throws SQLException {
        DataSource dataSource = Database.dataSource();

        System.out.println("== 1. 자동 커밋 (기본값) ==");
        Database.init(dataSource);   // 회원 3명으로 시작
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("autoCommit = " + conn.getAutoCommit());
            insertAll(conn, NEW_MEMBERS);
        } catch (SQLException e) {
            System.out.println("실패: " + firstLine(e));
        }
        System.out.println("회원 수: " + count(dataSource));

        System.out.println();
        System.out.println("== 2. 트랜잭션 ==");
        Database.init(dataSource);   // 다시 3명으로 시작
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);         // 트랜잭션 시작: commit() 전까지는 확정되지 않는다
            System.out.println("autoCommit = " + conn.getAutoCommit());
            try {
                insertAll(conn, NEW_MEMBERS);
                conn.commit();                 // 모두 성공했을 때만 확정
                System.out.println("commit");
            } catch (SQLException e) {
                conn.rollback();               // 하나라도 실패하면 이 트랜잭션의 변경을 모두 취소
                System.out.println("실패: " + firstLine(e));
                System.out.println("rollback");
            }
        }
        System.out.println("회원 수: " + count(dataSource));
    }

    /** 같은 커넥션으로 한 명씩 INSERT 한다 */
    static void insertAll(Connection conn, List<Member> members) throws SQLException {
        String sql = "INSERT INTO member (name, age) VALUES (?, ?)";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Member m : members) {
                pstmt.setString(1, m.getName());
                pstmt.setInt(2, m.getAge());
                pstmt.executeUpdate();
                System.out.println("INSERT " + m.getName());
            }
        }
    }

    static int count(DataSource dataSource) throws SQLException {
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement("SELECT COUNT(*) FROM member");
             ResultSet rs = pstmt.executeQuery()) {
            rs.next();
            return rs.getInt(1);
        }
    }

    /** H2의 오류 메시지는 길어서 첫 줄의 앞부분만 보여 준다 */
    static String firstLine(SQLException e) {
        String message = e.getMessage();
        int cut = message.indexOf(';');
        return cut > 0 ? message.substring(0, cut) : message;
    }
}
