package com.example.jdbc.ex05dao;

import com.example.jdbc.common.Database;
import com.example.jdbc.common.Member;

import javax.sql.DataSource;

/**
 * [교안 5.2] MemberDao로 CRUD 실행 — 이 클래스에는 JDBC 코드가 없다
 */
public class MemberDaoExample {

    public static void main(String[] args) {
        DataSource dataSource = Database.dataSource();
        Database.init(dataSource);   // member: 홍길동, 김철수, 이영희
        MemberDao dao = new MemberDao(dataSource);

        Member saved = dao.save(new Member("박민수", 28));
        System.out.println("save      → " + saved);

        System.out.println("findById  → " + dao.findById(saved.getId()).orElseThrow());

        System.out.println("updateAge → " + dao.updateAge(saved.getId(), 29) + "행 수정");
        System.out.println("findById  → " + dao.findById(saved.getId()).orElseThrow());

        System.out.println("delete    → " + dao.delete(1L) + "행 삭제");
        System.out.println("findById(1) 결과가 있는가? " + dao.findById(1L).isPresent());

        System.out.println("findAll:");
        dao.findAll().forEach(m -> System.out.println("  " + m));
    }
}
