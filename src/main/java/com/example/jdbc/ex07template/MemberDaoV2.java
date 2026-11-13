package com.example.jdbc.ex07template;

import com.example.jdbc.common.Member;

import javax.sql.DataSource;
import java.util.List;
import java.util.Optional;

/**
 * [교안 7.2] JdbcHelper를 쓴 MemberDao — SQL과 매핑 코드만 남았다
 * (save는 생성된 키를 받는 코드가 따로 필요해 이 예제에서는 뺐다)
 */
public class MemberDaoV2 {

    private final JdbcHelper jdbc;

    // 행 → Member 변환은 한 번만 정의하고 여러 메서드에서 쓴다
    private final RowMapper<Member> memberMapper =
            rs -> new Member(rs.getLong("id"), rs.getString("name"), rs.getInt("age"));

    public MemberDaoV2(DataSource dataSource) {
        this.jdbc = new JdbcHelper(dataSource);
    }

    public Optional<Member> findById(Long id) {
        return jdbc.queryOne("SELECT id, name, age FROM member WHERE id = ?", memberMapper, id);
    }

    public List<Member> findAll() {
        return jdbc.query("SELECT id, name, age FROM member ORDER BY id", memberMapper);
    }

    public int updateAge(Long id, int age) {
        return jdbc.update("UPDATE member SET age = ? WHERE id = ?", age, id);
    }

    public int delete(Long id) {
        return jdbc.update("DELETE FROM member WHERE id = ?", id);
    }
}
