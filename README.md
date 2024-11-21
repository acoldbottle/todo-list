# 투두리스트 API 개발

OAuth2를 통한 소셜로그인
로그인 시 jwt토큰 발급 -> redis 로 관리
회원, 할일 -> mysql로 관리

|         | 기능             | HTTP   | API PATH                                             |
|---------|----------------|--------|------------------------------------------------------|
| 회원      | 로그인            | GET    | /login                                               |
|         | 로그아웃           | POST   | /logout                                              |
|         | access 토큰 재발급  | POST   | /reissue                                             |
|---------|----------------|--------|-------------------------------------------------------|
| 카테고리    | 카테고리 조회        | GET    | /api/todo-categories?dueDate=YYYY-MM-DD              |
|         | 카테고리 추가        | POST   | /api/todo-categories?dueDate=YYYY-MM-DD              |
|         | 카테고리 삭제        | DELETE | /api/todo-categories/{categoryId}                    |
|         |                |        |                                                      |
|----------|-----------------|-----------|-----------------------------------------------------|
| 특정 카테고리 | 할일 목록 조회       | GET    | /api/todo-categories/{categoryId}/details            |
|         | 할일 추가          | POST   | /api/todo-categories/{categoryId}/details            |
|         | 할일 수정(할일,만료여부) | PATCH  | /api/todo-categories/{categoryId}/details/{detailId} |
|         | 할일 삭제          | DELETE | /api/todo-categories/{categoryId}/details/{detailId} |
