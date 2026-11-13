package com.example.jdbc.ex01connection;

import com.example.jdbc.common.Database;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * [교안 2.4] DB에 연결하기
 * 1) DriverManager로 연결 — URL, 사용자, 비밀번호를 직접 넘긴다
 * 2) DataSource로 연결 — 접속 정보는 DataSource가 가지고 있다
 * 3) try-with-resources가 끝나면 커넥션이 닫힌다 — 예전 방식(finally)과 비교
 */
public class ConnectionExample {

    public static void main(String[] args) throws SQLException {
        System.out.println("== 1. DriverManager로 연결 ==");
        String url = "jdbc:h2:mem:jdbcdb;DB_CLOSE_DELAY=-1";
        Connection saved;   // 블록이 끝난 뒤 닫혔는지 확인하려고 바깥에 둔다
        try (Connection conn = DriverManager.getConnection(url, "sa", "")) {
            DatabaseMetaData meta = conn.getMetaData();
            System.out.println("DB 제품   : " + meta.getDatabaseProductName() + " " + meta.getDatabaseProductVersion());
            System.out.println("드라이버  : " + meta.getDriverName() + " " + meta.getDriverVersion());
            System.out.println("URL       : " + meta.getURL());
            System.out.println("구현 클래스: " + conn.getClass().getName());   // Connection은 인터페이스, 실제 객체는 드라이버가 만든다
            System.out.println("닫혔는가? " + conn.isClosed());
            saved = conn;
        }   // 여기서 conn.close()가 자동으로 호출된다
        System.out.println("try 블록 뒤 — 닫혔는가? " + saved.isClosed());

        System.out.println();
        System.out.println("== 2. DataSource로 연결 ==");
        DataSource dataSource = Database.dataSource();   // 접속 정보는 한 번만 설정해 둔다
        try (Connection conn = dataSource.getConnection()) {   // 쓰는 쪽은 URL을 몰라도 된다
            System.out.println("DataSource 구현 클래스: " + dataSource.getClass().getName());
            System.out.println("URL       : " + conn.getMetaData().getURL());
        }

        System.out.println();
        System.out.println("== 3. finally로 닫기 (try-with-resources 이전 방식) ==");
        closeWithFinally(dataSource);
    }

    /** try-with-resources가 없던 시절의 코드 — 비교용 [교안 2.3] */
    static void closeWithFinally(DataSource dataSource) throws SQLException {
        Connection conn = null;
        try {
            conn = dataSource.getConnection();
            System.out.println("닫혔는가? " + conn.isClosed());
        } finally {                       // 예외가 나도 실행된다
            if (conn != null) {           // 연결에 실패했다면 닫을 것이 없다
                try {
                    conn.close();
                } catch (SQLException e) { // close()도 예외를 던질 수 있다
                    System.out.println("닫다가 실패: " + e.getMessage());
                }
            }
        }
        System.out.println("finally 뒤 — 닫혔는가? " + conn.isClosed());
    }
}
