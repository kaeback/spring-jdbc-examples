-- 예제를 시작할 때마다 Database.init()이 실행하는 스크립트 [교안 1.4]
DROP TABLE IF EXISTS member;

CREATE TABLE member (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,   -- 저장할 때 DB가 번호를 붙인다
    name VARCHAR(20) NOT NULL,
    age  INT         NOT NULL,
    CONSTRAINT chk_member_age CHECK (age >= 0)  -- 나이는 0 이상만 허용
);

INSERT INTO member (name, age) VALUES ('홍길동', 20);
INSERT INTO member (name, age) VALUES ('김철수', 25);
INSERT INTO member (name, age) VALUES ('이영희', 31);
