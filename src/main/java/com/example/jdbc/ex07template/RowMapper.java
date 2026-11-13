package com.example.jdbc.ex07template;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * [교안 7.2] ResultSet의 현재 행 하나를 객체 하나로 바꾸는 방법
 * DAO마다 다른 부분은 이것뿐이라, 이 부분만 밖에서 받는다.
 */
@FunctionalInterface
public interface RowMapper<T> {

    T mapRow(ResultSet rs) throws SQLException;
}
