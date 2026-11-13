package com.example.jdbc.common;

/**
 * member 테이블의 한 행을 담는 객체 [교안 4.2]
 * 컬럼(id, name, age)과 필드를 하나씩 맞춰 두었다.
 */
public class Member {

    private Long id;      // 저장하기 전에는 null, 저장하면 DB가 붙인 번호
    private String name;
    private int age;

    public Member(String name, int age) {          // 저장할 새 회원
        this(null, name, age);
    }

    public Member(Long id, String name, int age) { // DB에서 읽어 온 회원
        this.id = id;
        this.name = name;
        this.age = age;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    @Override
    public String toString() {
        return "Member{id=" + id + ", name=" + name + ", age=" + age + "}";
    }
}
