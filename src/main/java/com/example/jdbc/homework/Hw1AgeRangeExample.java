package com.example.jdbc.homework;

import com.example.jdbc.common.Database;
import com.example.jdbc.common.Member;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * [교안 과제 1] 나이 범위로 회원 찾기 — 풀이
 */
public class Hw1AgeRangeExample {

    public static void main(String[] args) throws SQLException {
        DataSource dataSource = Database.dataSource();
        Database.init(dataSource);   // member: 홍길동(20), 김철수(25), 이영희(31)

        try (Connection conn = dataSource.getConnection()) {
            System.out.println("20~29세 → " + findByAgeBetween(conn, 20, 29));
            System.out.println("30~39세 → " + findByAgeBetween(conn, 30, 39));
            System.out.println("40~49세 → " + findByAgeBetween(conn, 40, 49));
        }
    }

    static List<Member> findByAgeBetween(Connection conn, int min, int max) throws SQLException {
        String sql = "SELECT id, name, age FROM member WHERE age BETWEEN ? AND ? ORDER BY age";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, min);   // 첫 번째 ?
            pstmt.setInt(2, max);   // 두 번째 ?
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Member> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(new Member(rs.getLong("id"), rs.getString("name"), rs.getInt("age")));
                }
                return result;
            }
        }
    }
}
