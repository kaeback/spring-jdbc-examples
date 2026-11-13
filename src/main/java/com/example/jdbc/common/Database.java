package com.example.jdbc.common;

import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 예제들이 함께 쓰는 DB 설정 [교안 1.4]
 * - 접속 정보(URL, 사용자, 비밀번호)를 한곳에 모은다.
 * - init()은 schema.sql을 실행해 member 테이블을 처음 상태로 만든다.
 */
public final class Database {

    // H2 인메모리 DB. DB_CLOSE_DELAY=-1: 마지막 커넥션이 닫혀도 프로그램이 끝날 때까지 DB를 유지한다
    public static final String URL = "jdbc:h2:mem:jdbcdb;DB_CLOSE_DELAY=-1";
    public static final String USER = "sa";
    public static final String PASSWORD = "";

    private Database() {
    }

    /** 접속 정보를 담은 DataSource를 만든다 [교안 2.2] */
    public static DataSource dataSource() {
        JdbcDataSource dataSource = new JdbcDataSource();   // H2 드라이버가 제공하는 DataSource 구현체
        dataSource.setURL(URL);
        dataSource.setUser(USER);
        dataSource.setPassword(PASSWORD);
        return dataSource;
    }

    /** schema.sql의 문장을 ; 단위로 나눠 차례로 실행한다 */
    public static void init(DataSource dataSource) {
        String script = readSchema();
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {
            for (String sql : script.split(";")) {
                if (!sql.isBlank()) {
                    stmt.execute(sql);
                }
            }
        } catch (SQLException e) {
            throw new IllegalStateException("schema.sql 실행 실패", e);
        }
    }

    private static String readSchema() {
        try (InputStream in = Database.class.getResourceAsStream("/schema.sql")) {
            return new String(in.readAllBytes(), StandardCharsets.UTF_8)
                    .replaceAll("--.*", "");   // 주석(-- …)을 지운다
        } catch (IOException e) {
            throw new IllegalStateException("schema.sql을 읽을 수 없습니다", e);
        }
    }
}
