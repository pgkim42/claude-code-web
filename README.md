# Bookmark GraphQL API

스프링부트 + JPA + GraphQL로 만든 북마크 관리 시스템입니다.

## 기술 스택

- **Spring Boot 3.2.0** - 백엔드 프레임워크
- **Spring for GraphQL** - GraphQL API 구현
- **Spring Data JPA** - 데이터베이스 ORM
- **H2 Database** - 인메모리 데이터베이스
- **Lombok** - 보일러플레이트 코드 감소
- **Java 17** - 프로그래밍 언어

## 주요 기능

- ✅ 북마크 CRUD (생성, 조회, 수정, 삭제)
- ✅ 카테고리별 북마크 관리
- ✅ 북마크 검색 기능
- ✅ GraphQL API
- ✅ GraphiQL 인터페이스 제공

## 실행 방법

### 사전 요구사항
- Java 17 이상
- Gradle (wrapper 포함됨)

### 애플리케이션 실행

```bash
# Gradle wrapper를 이용한 실행
./gradlew bootRun

# 또는 빌드 후 실행
./gradlew build
java -jar build/libs/bookmark-graphql-0.0.1-SNAPSHOT.jar
```

애플리케이션이 실행되면:
- GraphQL Endpoint: http://localhost:8080/graphql
- GraphiQL UI: http://localhost:8080/graphiql
- H2 Console: http://localhost:8080/h2-console

## GraphQL 쿼리 예제

### 모든 북마크 조회
```graphql
query {
  bookmarks {
    id
    title
    url
    description
    category {
      id
      name
    }
    createdAt
  }
}
```

### 카테고리별 북마크 조회
```graphql
query {
  bookmarksByCategory(categoryId: 1) {
    id
    title
    url
    category {
      name
    }
  }
}
```

### 북마크 검색
```graphql
query {
  searchBookmarks(query: "spring") {
    id
    title
    url
    description
  }
}
```

### 북마크 생성
```graphql
mutation {
  createBookmark(input: {
    title: "My New Bookmark"
    url: "https://example.com"
    description: "A great resource"
    categoryId: 1
  }) {
    id
    title
    url
  }
}
```

### 북마크 수정
```graphql
mutation {
  updateBookmark(id: 1, input: {
    title: "Updated Title"
    description: "Updated description"
  }) {
    id
    title
    description
  }
}
```

### 북마크 삭제
```graphql
mutation {
  deleteBookmark(id: 1)
}
```

### 모든 카테고리 조회
```graphql
query {
  categories {
    id
    name
    description
    bookmarks {
      id
      title
    }
  }
}
```

### 카테고리 생성
```graphql
mutation {
  createCategory(input: {
    name: "Learning"
    description: "Educational resources"
  }) {
    id
    name
  }
}
```

## 프로젝트 구조

```
src/main/java/com/example/bookmark/
├── BookmarkApplication.java       # 메인 애플리케이션
├── DataInitializer.java           # 샘플 데이터 초기화
├── model/                         # 도메인 엔티티
│   ├── Bookmark.java
│   └── Category.java
├── repository/                    # JPA 리포지토리
│   ├── BookmarkRepository.java
│   └── CategoryRepository.java
├── service/                       # 비즈니스 로직
│   ├── BookmarkService.java
│   └── CategoryService.java
└── resolver/                      # GraphQL 리졸버
    ├── BookmarkResolver.java
    └── CategoryResolver.java

src/main/resources/
├── graphql/
│   └── schema.graphqls            # GraphQL 스키마 정의
└── application.properties         # 애플리케이션 설정
```

## 데이터베이스

H2 인메모리 데이터베이스를 사용합니다.
- URL: `jdbc:h2:mem:bookmarkdb`
- Username: `sa`
- Password: (비어있음)

H2 Console에 접속하여 데이터를 직접 확인할 수 있습니다.

## 샘플 데이터

애플리케이션 시작 시 자동으로 다음과 같은 샘플 데이터가 생성됩니다:

**카테고리:**
- Development
- Design
- News

**북마크:**
- Spring Boot Documentation
- GraphQL Official
- GitHub
- Dribbble
- Figma
- Hacker News
- The Verge

## 라이센스

MIT License
