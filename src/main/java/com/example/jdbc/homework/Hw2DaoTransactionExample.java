package com.example.jdbc.homework;

import com.example.jdbc.common.Database;
import com.example.jdbc.common.Member;
import com.example.jdbc.ex05dao.MemberDao;

import javax.sql.DataSource;
import java.util.List;

/**
 * [교안 과제 2] MemberDao.save()를 여러 번 부르다 실패하면? — 확인용 코드
 */
public class Hw2DaoTransactionExample {

    public static void main(String[] args) {
        DataSource dataSource = Database.dataSource();
        Database.init(dataSource);   // 회원 3명으로 시작
        MemberDao dao = new MemberDao(dataSource);

        List<Member> newMembers = List.of(
                new Member("박민수", 28),
                new Member("최지은", 22),
                new Member("정하늘", -1));   // CHECK (age >= 0) 위반
        try {
            for (Member m : newMembers) {
                dao.save(m);
                System.out.println("save " + m.getName());
            }
        } catch (IllegalStateException e) {
            System.out.println("실패: " + e.getMessage());
        }
        System.out.println("회원 수: " + dao.findAll().size());
    }
}
