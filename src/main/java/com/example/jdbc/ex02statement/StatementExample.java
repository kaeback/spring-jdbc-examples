package com.example.jdbc.ex02statement;

import com.example.jdbc.common.Database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * [교안 3.4] Statement — SQL 문자열을 그대로 실행한다
 * 사용자 입력을 문자열로 이어 붙이면 SQL 인젝션이 생긴다.
 */
public class StatementExample {

    public static void main(String[] args) throws SQLException {
        DataSource dataSource = Database.dataSource();
        Database.init(dataSource);   // member: 홍길동, 김철수, 이영희

        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // 1) 변경(INSERT·UPDATE·DELETE) — executeUpdate()는 영향을 받은 행 수를 돌려준다
            int inserted = stmt.executeUpdate("INSERT INTO member (name, age) VALUES ('박민수', 28)");
            System.out.println("INSERT 결과: " + inserted + "행");
            int updated = stmt.executeUpdate("UPDATE member SET age = age + 1 WHERE age >= 25");
            System.out.println("UPDATE 결과: " + updated + "행");

            // 2) 조회(SELECT) — 정상 입력
            findByName(stmt, "홍길동");

            // 3) 조회(SELECT) — 악의적인 입력
            findByName(stmt, "' OR '1'='1");
        }
    }

    /** 입력받은 이름을 SQL 문자열에 그대로 이어 붙인다 — 이렇게 하면 안 된다 */
    static void findByName(Statement stmt, String name) throws SQLException {
        String sql = "SELECT id, name, age FROM member WHERE name = '" + name + "'";
        System.out.println();
        System.out.println("입력: " + name);
        System.out.println("실행한 SQL: " + sql);
        try (ResultSet rs = stmt.executeQuery(sql)) {
            int count = 0;
            while (rs.next()) {
                System.out.println("  찾음 → " + rs.getLong("id") + ", " + rs.getString("name") + ", " + rs.getInt("age"));
                count++;
            }
            System.out.println("  " + count + "건");
        }
    }
}
