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

### 핵심 기능
- ✅ 북마크 CRUD (생성, 조회, 수정, 삭제)
- ✅ 카테고리별 북마크 관리
- ✅ 태그 시스템 (Many-to-Many 관계)
- ✅ GraphQL API & GraphiQL UI

### 고급 기능
- ⭐ **즐겨찾기 시스템** - 중요한 북마크 표시
- 🌟 **별점 시스템** - 1-5점 평점 부여
- 📊 **방문 통계** - 방문 횟수 및 마지막 방문 시간 추적
- 🔐 **공개/비공개** - 북마크 공개 여부 설정
- 🔍 **고급 검색** - 제목, 설명, 카테고리, 태그, 즐겨찾기, 별점으로 필터링
- 📈 **통계 대시보드** - 전체 통계 및 카테고리별 통계

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

### 기본 쿼리

#### 모든 북마크 조회 (태그 포함)
```graphql
query {
  bookmarks {
    id
    title
    url
    description
    isFavorite
    rating
    visitCount
    lastVisitedAt
    category {
      id
      name
    }
    tags {
      id
      name
      color
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

## 고급 기능 예제

### 즐겨찾기 조회
```graphql
query {
  favoriteBookmarks {
    id
    title
    url
    rating
    visitCount
  }
}
```

### 고급 검색 (필터링)
```graphql
query {
  advancedSearch(filter: {
    query: "spring"
    categoryId: 1
    isFavorite: true
    minRating: 4
  }) {
    id
    title
    rating
    isFavorite
    tags {
      name
    }
  }
}
```

### 태그별 북마크 조회
```graphql
query {
  bookmarksByTag(tagName: "Java") {
    id
    title
    url
    tags {
      name
      color
    }
  }
}
```

### 최다 방문 북마크 (Top 5)
```graphql
query {
  mostVisitedBookmarks(limit: 5) {
    id
    title
    url
    visitCount
    lastVisitedAt
  }
}
```

### 최근 방문 북마크
```graphql
query {
  recentlyVisitedBookmarks(limit: 5) {
    id
    title
    url
    lastVisitedAt
  }
}
```

### 고평점 북마크 (4점 이상)
```graphql
query {
  topRatedBookmarks(minRating: 4) {
    id
    title
    rating
    url
  }
}
```

### 전체 통계 조회
```graphql
query {
  bookmarkStatistics {
    totalBookmarks
    totalFavorites
    totalVisits
    averageRating
    totalCategories
    totalTags
  }
}
```

### 카테고리별 통계
```graphql
query {
  categoryStatistics {
    categoryId
    categoryName
    bookmarkCount
  }
}
```

### 방문 기록
```graphql
mutation {
  recordVisit(id: 1) {
    id
    visitCount
    lastVisitedAt
  }
}
```

### 즐겨찾기 토글
```graphql
mutation {
  toggleFavorite(id: 1) {
    id
    isFavorite
  }
}
```

### 별점 부여
```graphql
mutation {
  setRating(id: 1, rating: 5) {
    id
    rating
  }
}
```

### 태그 생성
```graphql
mutation {
  createTag(input: {
    name: "Kotlin"
    color: "#7F52FF"
  }) {
    id
    name
    color
  }
}
```

### 북마크에 태그 추가
```graphql
mutation {
  addTagToBookmark(bookmarkId: 1, tagId: 2) {
    id
    title
    tags {
      id
      name
    }
  }
}
```

### 북마크에서 태그 제거
```graphql
mutation {
  removeTagFromBookmark(bookmarkId: 1, tagId: 2) {
    id
    title
    tags {
      id
      name
    }
  }
}
```

### 태그 포함하여 북마크 생성
```graphql
mutation {
  createBookmark(input: {
    title: "Awesome Tutorial"
    url: "https://example.com/tutorial"
    description: "Great learning resource"
    categoryId: 1
    tagIds: [1, 2, 3]
    isFavorite: true
    rating: 5
    isPublic: true
  }) {
    id
    title
    tags {
      name
      color
    }
  }
}
```

## 프로젝트 구조

```
src/main/java/com/example/bookmark/
├── BookmarkApplication.java       # 메인 애플리케이션
├── DataInitializer.java           # 샘플 데이터 초기화
├── model/                         # 도메인 엔티티
│   ├── Bookmark.java              # 북마크 엔티티 (태그, 즐겨찾기, 별점, 방문 통계 포함)
│   ├── Category.java              # 카테고리 엔티티
│   └── Tag.java                   # 태그 엔티티 (Many-to-Many)
├── repository/                    # JPA 리포지토리
│   ├── BookmarkRepository.java    # 고급 쿼리 메서드 포함
│   ├── CategoryRepository.java
│   └── TagRepository.java
├── service/                       # 비즈니스 로직
│   ├── BookmarkService.java       # 북마크 비즈니스 로직 (검색, 통계 등)
│   ├── CategoryService.java
│   └── TagService.java
├── resolver/                      # GraphQL 리졸버
│   ├── BookmarkResolver.java      # 북마크 Query & Mutation
│   ├── CategoryResolver.java
│   └── TagResolver.java
└── dto/                           # 데이터 전송 객체
    ├── BookmarkFilter.java        # 검색 필터
    ├── BookmarkStatistics.java    # 북마크 통계
    └── CategoryStatistics.java    # 카테고리 통계

src/main/resources/
├── graphql/
│   └── schema.graphqls            # GraphQL 스키마 정의 (확장됨)
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

**카테고리 (4개):**
- Development - 프로그래밍 리소스
- Design - 디자인 도구
- News - 기술 뉴스
- Learning - 학습 자료

**태그 (8개):**
- Java, Spring, GraphQL, Design, UI/UX, News, Tutorial, Documentation
- 각 태그는 고유한 색상 코드 보유

**북마크 (10개):**
1. Spring Boot Documentation ⭐ (즐겨찾기, 별점 5)
2. GraphQL Official ⭐ (즐겨찾기, 별점 5)
3. GitHub ⭐ (즐겨찾기, 별점 5, 방문 150회)
4. Baeldung (별점 4, 방문 42회)
5. Java Documentation (별점 4)
6. Dribbble ⭐ (즐겨찾기, 별점 5)
7. Figma ⭐ (즐겨찾기, 별점 5, 방문 67회)
8. Hacker News (별점 4, 방문 89회)
9. The Verge (별점 3)
10. MDN Web Docs (별점 5, 방문 54회)

모든 북마크는 방문 기록, 별점, 태그가 설정되어 있어 즉시 고급 기능을 테스트할 수 있습니다.

## 라이센스

MIT License
