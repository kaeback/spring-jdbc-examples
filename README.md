# spring-jdbc-examples — JDBC 실습

자바 백엔드 강의 **JDBC** 장의 수업용 예제 프로젝트입니다.
교안의 "[예제]" 절이 이 프로젝트의 코드를 그대로 싣고 있습니다. 스프링 없이 순수 자바로 실행합니다.

- Java 21 / Gradle 9
- DB: **H2 인메모리**(별도 설치 불필요). 프로그램이 끝나면 데이터는 사라집니다.
- 의존성은 H2 하나뿐입니다(JDBC API `java.sql`은 JDK에 들어 있습니다).

## 실행

예제마다 `main` 메서드가 있습니다. 둘 중 편한 방법으로 실행합니다.

- IntelliJ: 예제 클래스를 열고 `main` 왼쪽의 ▶ 버튼
- 터미널: 예제마다 Gradle 태스크를 만들어 두었습니다(`-q`는 Gradle 진행 로그를 숨깁니다).

```bash
./gradlew -q ex01      # Windows: gradlew.bat -q ex01
./gradlew -q ex02
...
./gradlew -q ex07
```

모든 예제는 시작할 때 `Database.init()`으로 `schema.sql`을 실행해 `member` 테이블을 처음 상태(회원 3명)로 만듭니다.
그래서 몇 번을 실행해도 결과가 같습니다.

## 예제 목록

| 태스크 | 패키지 / 클래스 | 내용 | 교안 |
|---|---|---|---|
| — | `common/Database`, `common/Member`, `resources/schema.sql` | 접속 정보·DataSource·테이블 초기화, 행을 담는 객체 | 1.4 |
| `ex01` | `ex01connection/ConnectionExample` | `DriverManager`와 `DataSource`로 연결, try-with-resources로 닫기 | 2.4 |
| `ex02` | `ex02statement/StatementExample` | `Statement`로 실행, 문자열 연결이 만드는 SQL 인젝션 | 3.4 |
| `ex03` | `ex03prepared/PreparedStatementExample` | `PreparedStatement`와 `?` 바인딩으로 막기 | 3.5 |
| `ex04` | `ex04resultset/ResultSetExample` | `ResultSet`을 `Member`·`List`·`Optional`·`int`로 | 4.2 |
| `ex05` | `ex05dao/MemberDao`, `MemberDaoExample` | DAO로 CRUD 묶기, 생성된 키 받기 | 5.2 |
| `ex06` | `ex06transaction/TransactionExample` | 자동 커밋 vs `setAutoCommit(false)`·`commit`·`rollback` | 6.3 |
| `ex07` | `ex07template/JdbcHelper`, `RowMapper`, `MemberDaoV2`, `TemplateExample` | 반복 코드를 한곳으로 모으기 (JdbcTemplate의 원리) | 7.2 |
| `hw1` | `homework/Hw1AgeRangeExample` | 과제 1 풀이 — 나이 범위 조회(`?` 두 개) | 과제 |
| `hw2` | `homework/Hw2DaoTransactionExample` | 과제 2 확인용 — DAO 메서드를 여러 번 부르다 실패하면 | 과제 |

코드의 `[교안 x.y]` 주석은 교안의 절 번호입니다.

## 테이블

```sql
CREATE TABLE member (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(20) NOT NULL,
    age  INT         NOT NULL,
    CONSTRAINT chk_member_age CHECK (age >= 0)
);
-- 시작 데이터: 홍길동(20), 김철수(25), 이영희(31)
```
