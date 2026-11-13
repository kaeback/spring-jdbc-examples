package com.example.jdbc.ex07template;

import com.example.jdbc.common.Database;

import javax.sql.DataSource;

/**
 * [교안 7.2] MemberDaoV2도 MemberDao와 같은 결과를 낸다
 */
public class TemplateExample {

    public static void main(String[] args) {
        DataSource dataSource = Database.dataSource();
        Database.init(dataSource);   // member: 홍길동, 김철수, 이영희
        MemberDaoV2 dao = new MemberDaoV2(dataSource);

        System.out.println("findById  → " + dao.findById(2L).orElseThrow());
        System.out.println("updateAge → " + dao.updateAge(2L, 26) + "행 수정");
        System.out.println("findById  → " + dao.findById(2L).orElseThrow());
        System.out.println("delete    → " + dao.delete(1L) + "행 삭제");
        System.out.println("findAll:");
        dao.findAll().forEach(m -> System.out.println("  " + m));
    }
}
