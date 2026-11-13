package com.example.jdbc.ex03prepared;

import com.example.jdbc.common.Database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * [교안 3.5] PreparedStatement — SQL의 값 자리를 ?로 두고, 값은 따로 바인딩한다
 */
public class PreparedStatementExample {

    public static void main(String[] args) throws SQLException {
        DataSource dataSource = Database.dataSource();
        Database.init(dataSource);   // member: 홍길동, 김철수, 이영희

        try (Connection conn = dataSource.getConnection()) {
            // 1) 같은 두 입력으로 조회
            findByName(conn, "홍길동");
            findByName(conn, "' OR '1'='1");

            // 2) 작은따옴표가 들어 있는 이름도 그대로 저장된다
            String sql = "INSERT INTO member (name, age) VALUES (?, ?)";
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, "O'Brien");   // 첫 번째 ? 에 문자열
                pstmt.setInt(2, 40);             // 두 번째 ? 에 정수
                int inserted = pstmt.executeUpdate();
                System.out.println();
                System.out.println("INSERT 결과: " + inserted + "행");
            }
            findByName(conn, "O'Brien");
        }
    }

    static void findByName(Connection conn, String name) throws SQLException {
        String sql = "SELECT id, name, age FROM member WHERE name = ?";   // 값 자리는 ?
        System.out.println();
        System.out.println("입력: " + name);
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);   // 입력값은 SQL 문장이 아니라 '값'으로만 전달된다
            try (ResultSet rs = pstmt.executeQuery()) {
                int count = 0;
                while (rs.next()) {
                    System.out.println("  찾음 → " + rs.getLong("id") + ", " + rs.getString("name") + ", " + rs.getInt("age"));
                    count++;
                }
                System.out.println("  " + count + "건");
            }
        }
    }
}
