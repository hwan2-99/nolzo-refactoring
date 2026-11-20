# nolzo API 문서

소스 코드 정적 분석으로 자동 생성 (my_mcp_server `api_doc_export`)

총 42개 엔드포인트, 9개 컨트롤러 (소스 130개 파일 분석)

### AuthController  (nolzo-api/src/main/java/com/noljo/nolzo/auth/controller/AuthController.java)
- POST   /auth/login                              -> login()
- POST   /auth/logout                             -> logout()
- POST   /auth/register                           -> register()
- GET    /auth/reissue                            -> reissueAccessToken()

### EventController  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- GET    /event                                   -> getAllEvents()  (params=!category)
- GET    /event                                   -> getEventsByCategory()
- POST   /event                                   -> createEvent()
- POST   /event/delete/{id}                       -> deleteEvent()
- GET    /event/popular                           -> getTop6PopularEvents()
- GET    /event/rankings                          -> getRankingsByCategory()
- POST   /event/recommend                         -> recommendEvents()
- GET    /event/search                            -> getSearchEventList()
- GET    /event/{id}                              -> getEventDetail()
- PATCH  /event/{id}                              -> updateEvent()
- PATCH  /event/{id}                              -> updateJson()

### MemberController  (nolzo-api/src/main/java/com/noljo/nolzo/member/controller/MemberController.java)
- DELETE /member                                  -> changePassword()  [인증]
- GET    /member                                  -> getMember()  [인증]
- POST   /member/password                         -> changePassword()  [인증]
- GET    /member/readAll                          -> getAllMembers()

### NotificationSubscriptionController  (nolzo-api/src/main/java/com/noljo/nolzo/notification/controller/NotificationSubscriptionController.java)
- GET    /notifications/subscriptions             -> readAll()  [인증]
- POST   /notifications/subscriptions             -> create()  [인증]
- DELETE /notifications/subscriptions/{subscriptionId} -> cancel()  [인증]

### PaymentController  (nolzo-api/src/main/java/com/noljo/nolzo/payment/controller/PaymentController.java)
- POST   /payments                                -> createPayment()  [인증]

### ReservationController  (nolzo-api/src/main/java/com/noljo/nolzo/reservation/controller/ReservationController.java)
- GET    /reservations                            -> getReservations()  [인증]
- POST   /reservations                            -> create()  [인증]
- GET    /reservations/cancel                     -> getCancelReservations()  [인증]
- GET    /reservations/confirmed                  -> getReservationConfirmed()  [인증]
- GET    /reservations/details/{reservationId}    -> getReservationDetails()  [인증]
- GET    /reservations/reservation/{eventId}      -> findSeatsByEventId()
- DELETE /reservations/reservation/{reservationId} -> cancelReservation()  [인증]
- GET    /reservations/used                       -> getTicketsUsed()  [인증]
- POST   /reservations/{eventId}                  -> chooseEventDateTime()

### ReviewController  (nolzo-api/src/main/java/com/noljo/nolzo/review/controller/ReviewController.java)
- POST   /reviews                                 -> createReview()  [인증]
- GET    /reviews/events/{eventId}                -> getReviewsByEvent()
- GET    /reviews/events/{eventId}/my             -> getMyReviewByEvent()  [인증]
- GET    /reviews/my                              -> getMemberReview()  [인증]
- DELETE /reviews/{reviewId}                      -> deleteReview()  [인증]
- GET    /reviews/{reviewId}                      -> getReview()
- PUT    /reviews/{reviewId}                      -> updateReview()  [인증]

### QueueRecoveryTestController  (nolzo-api/src/main/java/com/noljo/nolzo/queue/controller/QueueRecoveryTestController.java)
- POST   /test/queue/recover                      -> recover()

### TicketController  (nolzo-api/src/main/java/com/noljo/nolzo/ticket/controller/TicketController.java)
- GET    /tickets                                 -> findTickets()
- GET    /tickets/{ticketId}                      -> findTicket()

특정 API 문서는 api_doc 도구에 경로(예: /reservations)나 컨트롤러 이름을 넘겨 조회하세요.

---

## POST /auth/login
- 핸들러: `AuthController.login()`  (nolzo-api/src/main/java/com/noljo/nolzo/auth/controller/AuthController.java)
- 인증: 명시된 인증 주체 없음

### 요청
본문 (LoginRequest):
```json
{
  "email": "user@example.com",
  "password": "P@ssw0rd!"
}
```
필드:
- `email` String  — @Email
- `password` String  — @NotNull

예시 호출:
```
curl -X POST 'http://localhost:8080/auth/login' \
  -H 'Content-Type: application/json' \
  -d '{ "email": "user@example.com", "password": "P@ssw0rd!" }'
```

### 응답 200 OK  (TokensResponse)
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.example",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9.example"
}
```

### 에러
- **500** MethodArgumentNotValidException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 검증 실패 (email: @Email, password: @NotNull)"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** HttpMessageNotReadableException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 JSON 파싱 실패"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — AuthService.findMemberByEmail(): "존재하지 않는 회원 입니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## POST /auth/logout
- 핸들러: `AuthController.logout()`  (nolzo-api/src/main/java/com/noljo/nolzo/auth/controller/AuthController.java)
- 인증: 명시된 인증 주체 없음

### 요청
쿠키:
- `REFRESH_TOKEN` String, 선택

예시 호출:
```
curl -X POST 'http://localhost:8080/auth/logout' \
  --cookie 'REFRESH_TOKEN=eyJhbGciOiJIUzI1NiJ9.example'
```

### 응답 204 No Content
(본문 없음)

### 에러
소스에서 발견된 예외 없음

---

## POST /auth/register
- 핸들러: `AuthController.register()`  (nolzo-api/src/main/java/com/noljo/nolzo/auth/controller/AuthController.java)
- 인증: 명시된 인증 주체 없음

### 요청
본문 (RegisterRequest):
```json
{
  "email": "user@example.com",
  "password": "P@ssw0rd!",
  "name": "홍길동",
  "birth": "1995-03-14"
}
```
필드:
- `email` String
- `password` String
- `name` String
- `birth` LocalDate
(검증 어노테이션 없음)

예시 호출:
```
curl -X POST 'http://localhost:8080/auth/register' \
  -H 'Content-Type: application/json' \
  -d '{ "email": "user@example.com", "password": "P@ssw0rd!", "name": "홍길동", "birth": "1995-03-14" }'
```

### 응답 200 OK  (RegisterResponse)
```json
{
  "memberId": 1,
  "name": "홍길동"
}
```

### 에러
- **500** MethodArgumentNotValidException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 검증 실패 (검증 규칙 없음)"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** HttpMessageNotReadableException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 JSON 파싱 실패"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — AuthService.validateDuplicateEmail(): "이미 존재하는 회원입니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /auth/reissue
- 핸들러: `AuthController.reissueAccessToken()`  (nolzo-api/src/main/java/com/noljo/nolzo/auth/controller/AuthController.java)
- 인증: 명시된 인증 주체 없음

### 요청
쿠키:
- `REFRESH_TOKEN` String, 필수

예시 호출:
```
curl -X GET 'http://localhost:8080/auth/reissue' \
  --cookie 'REFRESH_TOKEN=eyJhbGciOiJIUzI1NiJ9.example'
```

### 응답 200 OK  (AccessTokenResponse)
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.example"
}
```

### 에러
- **500** MissingRequestCookieException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 쿠키 'REFRESH_TOKEN' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** IllegalStateException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — JwtTokenService.validateRefreshToken(): "유효하지 않은 Token 입니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — JwtTokenService.getRefreshToken(): "RefreshToken을 찾을 수 없습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalStateException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — JwtTokenService.validateRefreshTokenNotExpired(): "RefreshToken이 만료되었습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalStateException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — JwtTokenService.validateRefreshTokenMatches(): "RefreshToken이 일치하지 않습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /event
- 핸들러: `EventController.getAllEvents()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음
- 매핑 조건: params=!category

### 요청
파라미터 없음

### 응답 200 OK  (List<EventResponse>)
```json
[
  {
    "id": 1,
    "title": "예시 제목",
    "venue": "서울 올림픽공원 KSPO DOME",
    "description": "예시 내용입니다.",
    "posterImageUrl": "https://example.com/image.png",
    "startDate": "2026-09-01",
    "endDate": "2026-09-01",
    "schedules": [
      {
        "id": 1,
        "showDate": "2026-09-01",
        "showTime": "19:00:00",
        "reservationStart": "2026-09-01T19:00:00",
        "reservationEnd": "2026-09-01T19:00:00"
      }
    ],
    "eventCategory": "MUSICAL",
    "runtime": 120,
    "ageLimit": 19,
    "rating": 5,
    "reviewCount": 10,
    "viewCount": 10
  }
]
```

### 에러
소스에서 발견된 예외 없음

---

## GET /event
- 핸들러: `EventController.getEventsByCategory()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음

### 요청
쿼리 파라미터:
- `category` EventCategory, 필수, 값: MUSICAL/CONCERT/PLAY
- `page` int, 선택, 기본값 0
- `condition` String, 선택, 기본값 viewCount
- `age` Integer, 선택

예시 호출:
```
curl -X GET 'http://localhost:8080/event?category=MUSICAL&page=0&condition=viewCount&age=19'
```

### 응답 200 OK  (Slice<EventResponse>)
```json
{
  "content": [
    {
      "id": 1,
      "title": "예시 제목",
      "venue": "서울 올림픽공원 KSPO DOME",
      "description": "예시 내용입니다.",
      "posterImageUrl": "https://example.com/image.png",
      "startDate": "2026-09-01",
      "endDate": "2026-09-01",
      "schedules": [
        {
          "id": 1,
          "showDate": "2026-09-01",
          "showTime": "19:00:00",
          "reservationStart": "2026-09-01T19:00:00",
          "reservationEnd": "2026-09-01T19:00:00"
        }
      ],
      "eventCategory": "MUSICAL",
      "runtime": 120,
      "ageLimit": 19,
      "rating": 5,
      "reviewCount": 10,
      "viewCount": 10
    }
  ],
  "number": 0,
  "size": 10,
  "first": true,
  "last": false,
  "hasNext": true
}
```

### 에러
- **500** MissingServletRequestParameterException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 쿼리 파라미터 'category' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (category: EventCategory, page: int, age: Integer)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## POST /event
- 핸들러: `EventController.createEvent()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음
- 요청 Content-Type: multipart/form-data

### 요청
멀티파트 'dto' (EventRequest):
```json
{
  "title": "예시 제목",
  "venue": "서울 올림픽공원 KSPO DOME",
  "description": "예시 내용입니다.",
  "posterImageUrl": "https://example.com/image.png",
  "startDate": "2026-09-01",
  "endDate": "2026-09-01",
  "eventCategory": "MUSICAL",
  "runtime": 120,
  "ageLimit": 19,
  "schedules": [
    {
      "id": 1,
      "showDate": "2026-09-01",
      "showTime": "19:00:00",
      "reservationStart": "2026-09-01T19:00:00",
      "reservationEnd": "2026-09-01T19:00:00"
    }
  ]
}
```
필드:
- `title` String  — @NotBlank(message=제목 지정 필수)
- `venue` String  — @NotBlank(message=장소 지정 필수)
- `description` String
- `posterImageUrl` String
- `startDate` LocalDate  — @NotNull(message=시작일 지정 필수)
- `endDate` LocalDate  — @NotNull(message=종료일 지정 필수)
- `eventCategory` EventCategory  — @NotNull(message=카테고리 지정 필수)  (값: MUSICAL/CONCERT/PLAY)
- `runtime` int
- `ageLimit` int
- `schedules` List<ScheduleInfo>
멀티파트 'eventImage' (MultipartFile, 선택): 파일 업로드

예시 호출:
```
curl -X POST 'http://localhost:8080/event' \
  -F 'dto={ "title": "예시 제목", "venue": "서울 올림픽공원 KSPO DOME", "description": "예시 내용입니다.", "posterImageUrl": "https://example.com/image.png", "startDate": "2026-09-01", "endDate": "2026-09-01", "eventCategory": "MUSICAL", "runtime": 120, "ageLimit": 19, "schedules": [ { "id": 1, "showDate": "2026-09-01", "showTime": "19:00:00", "reservationStart": "2026-09-01T19:00:00", "reservationEnd": "2026-09-01T19:00:00" } ] };type=application/json' \
  -F 'eventImage=@./file.png'
```

### 응답 200 OK  (EventResponse)
```json
{
  "id": 1,
  "title": "예시 제목",
  "venue": "서울 올림픽공원 KSPO DOME",
  "description": "예시 내용입니다.",
  "posterImageUrl": "https://example.com/image.png",
  "startDate": "2026-09-01",
  "endDate": "2026-09-01",
  "schedules": [
    {
      "id": 1,
      "showDate": "2026-09-01",
      "showTime": "19:00:00",
      "reservationStart": "2026-09-01T19:00:00",
      "reservationEnd": "2026-09-01T19:00:00"
    }
  ],
  "eventCategory": "MUSICAL",
  "runtime": 120,
  "ageLimit": 19,
  "rating": 5,
  "reviewCount": 10,
  "viewCount": 10
}
```

### 에러
- **500** MethodArgumentNotValidException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "멀티파트 'dto' 검증 실패 (title: @NotBlank(message=제목 지정 필수), venue: @NotBlank(message=장소 지정 필수), startDate: @NotNull(message=시작일 지정 필수), endDate: @NotNull(message=종료일 지정 필수), eventCategory: @NotNull(message=카테고리 지정 필수))"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** MissingServletRequestPartException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 멀티파트 'dto' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
- **500** RuntimeException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — EventService.save(): "S3 이미지 업로드 실패: …"
  처리: GlobalExceptionHandler.handleRuntimeException()
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — SeatService.findScheduleById(): "No schedule found for the given id"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## POST /event/delete/{id}
- 핸들러: `EventController.deleteEvent()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음

### 요청
경로 변수:
- `id` Long, 필수

예시 호출:
```
curl -X POST 'http://localhost:8080/event/delete/1'
```

### 응답 204 No Content
(본문 없음)

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (id: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — EventService.getEvent(): "해당 이벤트가 존재하지 않습니다 id : …"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /event/popular
- 핸들러: `EventController.getTop6PopularEvents()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음

### 요청
파라미터 없음

### 응답 200 OK  (List<EventResponse>)
```json
[
  {
    "id": 1,
    "title": "예시 제목",
    "venue": "서울 올림픽공원 KSPO DOME",
    "description": "예시 내용입니다.",
    "posterImageUrl": "https://example.com/image.png",
    "startDate": "2026-09-01",
    "endDate": "2026-09-01",
    "schedules": [
      {
        "id": 1,
        "showDate": "2026-09-01",
        "showTime": "19:00:00",
        "reservationStart": "2026-09-01T19:00:00",
        "reservationEnd": "2026-09-01T19:00:00"
      }
    ],
    "eventCategory": "MUSICAL",
    "runtime": 120,
    "ageLimit": 19,
    "rating": 5,
    "reviewCount": 10,
    "viewCount": 10
  }
]
```

### 에러
소스에서 발견된 예외 없음

---

## GET /event/rankings
- 핸들러: `EventController.getRankingsByCategory()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음

### 요청
쿼리 파라미터:
- `category` EventCategory, 필수, 값: MUSICAL/CONCERT/PLAY

예시 호출:
```
curl -X GET 'http://localhost:8080/event/rankings?category=MUSICAL'
```

### 응답 200 OK  (List<EventResponse>)
```json
[
  {
    "id": 1,
    "title": "예시 제목",
    "venue": "서울 올림픽공원 KSPO DOME",
    "description": "예시 내용입니다.",
    "posterImageUrl": "https://example.com/image.png",
    "startDate": "2026-09-01",
    "endDate": "2026-09-01",
    "schedules": [
      {
        "id": 1,
        "showDate": "2026-09-01",
        "showTime": "19:00:00",
        "reservationStart": "2026-09-01T19:00:00",
        "reservationEnd": "2026-09-01T19:00:00"
      }
    ],
    "eventCategory": "MUSICAL",
    "runtime": 120,
    "ageLimit": 19,
    "rating": 5,
    "reviewCount": 10,
    "viewCount": 10
  }
]
```

### 에러
- **500** MissingServletRequestParameterException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 쿼리 파라미터 'category' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (category: EventCategory)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## POST /event/recommend
- 핸들러: `EventController.recommendEvents()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음

### 요청
본문 (EventRecommendRequest):
```json
{
  "memberId": 1,
  "query": "검색어"
}
```
필드:
- `memberId` Long
- `query` String  — @NotBlank(message=추천 질의는 비어 있을 수 없습니다.)

예시 호출:
```
curl -X POST 'http://localhost:8080/event/recommend' \
  -H 'Content-Type: application/json' \
  -d '{ "memberId": 1, "query": "검색어" }'
```

### 응답 200 OK  (EventRecommendResponse)
```json
{
  "originalQuery": "검색어",
  "message": "예시 내용입니다.",
  "condition": {
    "region": "string",
    "dateRange": "2026-09-01",
    "maxPrice": 15000,
    "category": "ACTIVE",
    "mood": "string"
  },
  "recommendations": [
    {
      "eventId": 1,
      "title": "예시 제목",
      "venue": "서울 올림픽공원 KSPO DOME",
      "startDate": "2026-09-01",
      "endDate": "2026-09-01",
      "category": "MUSICAL",
      "recommendationReason": "예시 사유"
    }
  ]
}
```

### 에러
- **500** MethodArgumentNotValidException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 검증 실패 (query: @NotBlank(message=추천 질의는 비어 있을 수 없습니다.))"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** HttpMessageNotReadableException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 JSON 파싱 실패"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /event/search
- 핸들러: `EventController.getSearchEventList()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음

### 요청
쿼리 파라미터:
- `search` String, 필수

예시 호출:
```
curl -X GET 'http://localhost:8080/event/search?search=검색어'
```

### 응답 200 OK  (List<EventResponse>)
```json
[
  {
    "id": 1,
    "title": "예시 제목",
    "venue": "서울 올림픽공원 KSPO DOME",
    "description": "예시 내용입니다.",
    "posterImageUrl": "https://example.com/image.png",
    "startDate": "2026-09-01",
    "endDate": "2026-09-01",
    "schedules": [
      {
        "id": 1,
        "showDate": "2026-09-01",
        "showTime": "19:00:00",
        "reservationStart": "2026-09-01T19:00:00",
        "reservationEnd": "2026-09-01T19:00:00"
      }
    ],
    "eventCategory": "MUSICAL",
    "runtime": 120,
    "ageLimit": 19,
    "rating": 5,
    "reviewCount": 10,
    "viewCount": 10
  }
]
```

### 에러
- **500** MissingServletRequestParameterException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 쿼리 파라미터 'search' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```

---

## GET /event/{id}
- 핸들러: `EventController.getEventDetail()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음

### 요청
경로 변수:
- `id` Long, 필수

예시 호출:
```
curl -X GET 'http://localhost:8080/event/1'
```

### 응답 200 OK  (EventResponse)
```json
{
  "id": 1,
  "title": "예시 제목",
  "venue": "서울 올림픽공원 KSPO DOME",
  "description": "예시 내용입니다.",
  "posterImageUrl": "https://example.com/image.png",
  "startDate": "2026-09-01",
  "endDate": "2026-09-01",
  "schedules": [
    {
      "id": 1,
      "showDate": "2026-09-01",
      "showTime": "19:00:00",
      "reservationStart": "2026-09-01T19:00:00",
      "reservationEnd": "2026-09-01T19:00:00"
    }
  ],
  "eventCategory": "MUSICAL",
  "runtime": 120,
  "ageLimit": 19,
  "rating": 5,
  "reviewCount": 10,
  "viewCount": 10
}
```

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (id: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — EventService.getEvent(): "해당 이벤트가 존재하지 않습니다 id : …"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## PATCH /event/{id}
- 핸들러: `EventController.updateEvent()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음
- 요청 Content-Type: multipart/form-data

### 요청
경로 변수:
- `id` Long, 필수
멀티파트 'dto' (EventUpdateRequest):
```json
{
  "title": "예시 제목",
  "venue": "서울 올림픽공원 KSPO DOME",
  "description": "예시 내용입니다.",
  "startDate": "2026-09-01",
  "endDate": "2026-09-01",
  "schedules": [
    {
      "id": 1,
      "showDate": "2026-09-01",
      "showTime": "19:00:00",
      "reservationStart": "2026-09-01T19:00:00",
      "reservationEnd": "2026-09-01T19:00:00"
    }
  ]
}
```
필드:
- `title` String  — @NotBlank(message=제목 입력 필수)
- `venue` String  — @NotBlank(message=장소 입력 필수)
- `description` String  — @NotBlank(message=설명 입력 필수)
- `startDate` LocalDate  — @NotNull(message=시작 입력 필수)
- `endDate` LocalDate  — @NotNull(message=종료 입력 필수)
- `schedules` List<ScheduleInfo>
멀티파트 'eventImage' (MultipartFile, 선택): 파일 업로드

예시 호출:
```
curl -X PATCH 'http://localhost:8080/event/1' \
  -F 'dto={ "title": "예시 제목", "venue": "서울 올림픽공원 KSPO DOME", "description": "예시 내용입니다.", "startDate": "2026-09-01", "endDate": "2026-09-01", "schedules": [ { "id": 1, "showDate": "2026-09-01", "showTime": "19:00:00", "reservationStart": "2026-09-01T19:00:00", "reservationEnd": "2026-09-01T19:00:00" } ] };type=application/json' \
  -F 'eventImage=@./file.png'
```

### 응답 200 OK  (EventResponse)
```json
{
  "id": 1,
  "title": "예시 제목",
  "venue": "서울 올림픽공원 KSPO DOME",
  "description": "예시 내용입니다.",
  "posterImageUrl": "https://example.com/image.png",
  "startDate": "2026-09-01",
  "endDate": "2026-09-01",
  "schedules": [
    {
      "id": 1,
      "showDate": "2026-09-01",
      "showTime": "19:00:00",
      "reservationStart": "2026-09-01T19:00:00",
      "reservationEnd": "2026-09-01T19:00:00"
    }
  ],
  "eventCategory": "MUSICAL",
  "runtime": 120,
  "ageLimit": 19,
  "rating": 5,
  "reviewCount": 10,
  "viewCount": 10
}
```

### 에러
- **500** MethodArgumentNotValidException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "멀티파트 'dto' 검증 실패 (title: @NotBlank(message=제목 입력 필수), venue: @NotBlank(message=장소 입력 필수), description: @NotBlank(message=설명 입력 필수), startDate: @NotNull(message=시작 입력 필수), endDate: @NotNull(message=종료 입력 필수))"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** MissingServletRequestPartException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 멀티파트 'dto' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (id: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — SeatService.findScheduleById(): "No schedule found for the given id"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — EventService.getEvent(): "해당 이벤트가 존재하지 않습니다 id : …"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## PATCH /event/{id}
- 핸들러: `EventController.updateJson()`  (nolzo-api/src/main/java/com/noljo/nolzo/event/controller/EventController.java)
- 인증: 명시된 인증 주체 없음

### 요청
경로 변수:
- `id` Long, 필수
본문 (EventUpdateRequest):
```json
{
  "title": "예시 제목",
  "venue": "서울 올림픽공원 KSPO DOME",
  "description": "예시 내용입니다.",
  "startDate": "2026-09-01",
  "endDate": "2026-09-01",
  "schedules": [
    {
      "id": 1,
      "showDate": "2026-09-01",
      "showTime": "19:00:00",
      "reservationStart": "2026-09-01T19:00:00",
      "reservationEnd": "2026-09-01T19:00:00"
    }
  ]
}
```
필드:
- `title` String  — @NotBlank(message=제목 입력 필수)
- `venue` String  — @NotBlank(message=장소 입력 필수)
- `description` String  — @NotBlank(message=설명 입력 필수)
- `startDate` LocalDate  — @NotNull(message=시작 입력 필수)
- `endDate` LocalDate  — @NotNull(message=종료 입력 필수)
- `schedules` List<ScheduleInfo>

예시 호출:
```
curl -X PATCH 'http://localhost:8080/event/1' \
  -H 'Content-Type: application/json' \
  -d '{ "title": "예시 제목", "venue": "서울 올림픽공원 KSPO DOME", "description": "예시 내용입니다.", "startDate": "2026-09-01", "endDate": "2026-09-01", "schedules": [ { "id": 1, "showDate": "2026-09-01", "showTime": "19:00:00", "reservationStart": "2026-09-01T19:00:00", "reservationEnd": "2026-09-01T19:00:00" } ] }'
```

### 응답 200 OK  (EventResponse)
```json
{
  "id": 1,
  "title": "예시 제목",
  "venue": "서울 올림픽공원 KSPO DOME",
  "description": "예시 내용입니다.",
  "posterImageUrl": "https://example.com/image.png",
  "startDate": "2026-09-01",
  "endDate": "2026-09-01",
  "schedules": [
    {
      "id": 1,
      "showDate": "2026-09-01",
      "showTime": "19:00:00",
      "reservationStart": "2026-09-01T19:00:00",
      "reservationEnd": "2026-09-01T19:00:00"
    }
  ],
  "eventCategory": "MUSICAL",
  "runtime": 120,
  "ageLimit": 19,
  "rating": 5,
  "reviewCount": 10,
  "viewCount": 10
}
```

### 에러
- **500** MethodArgumentNotValidException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 검증 실패 (title: @NotBlank(message=제목 입력 필수), venue: @NotBlank(message=장소 입력 필수), description: @NotBlank(message=설명 입력 필수), startDate: @NotNull(message=시작 입력 필수), endDate: @NotNull(message=종료 입력 필수))"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** HttpMessageNotReadableException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 JSON 파싱 실패"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (id: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — SeatService.findScheduleById(): "No schedule found for the given id"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — EventService.getEvent(): "해당 이벤트가 존재하지 않습니다 id : …"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## DELETE /member
- 핸들러: `MemberController.changePassword()`  (nolzo-api/src/main/java/com/noljo/nolzo/member/controller/MemberController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
파라미터 없음

### 응답 200 OK  (Object)
```
delete successful.
```

### 에러
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — MemberService.deleteMember(): "member not found."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```

---

## GET /member
- 핸들러: `MemberController.getMember()`  (nolzo-api/src/main/java/com/noljo/nolzo/member/controller/MemberController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
파라미터 없음

### 응답 200 OK  (MemberDto)
```json
{
  "id": 1,
  "name": "홍길동",
  "email": "user@example.com",
  "birth": "1995-03-14",
  "role": "USER"
}
```

### 에러
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — MemberService.readMember(): "member not found"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```

---

## POST /member/password
- 핸들러: `MemberController.changePassword()`  (nolzo-api/src/main/java/com/noljo/nolzo/member/controller/MemberController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
본문 (PasswordChangeRequest):
```json
{
  "password": "P@ssw0rd!"
}
```
필드:
- `password` String  — @NotBlank @Size(min=8)

예시 호출:
```
curl -X POST 'http://localhost:8080/member/password' \
  -H 'Authorization: Bearer <accessToken>' \
  -H 'Content-Type: application/json' \
  -d '{ "password": "P@ssw0rd!" }'
```

### 응답 200 OK  (Object)
```
password change successful.
```

### 에러
- **500** MethodArgumentNotValidException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 검증 실패 (password: @NotBlank, password: @Size(min=8))"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** HttpMessageNotReadableException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 JSON 파싱 실패"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — MemberService.changeMemberPassword(): "member not found"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /member/readAll
- 핸들러: `MemberController.getAllMembers()`  (nolzo-api/src/main/java/com/noljo/nolzo/member/controller/MemberController.java)
- 인증: 명시된 인증 주체 없음

### 요청
파라미터 없음

### 응답 200 OK  (List<MemberDto>)
```json
[
  {
    "id": 1,
    "name": "홍길동",
    "email": "user@example.com",
    "birth": "1995-03-14",
    "role": "USER"
  }
]
```

### 에러
소스에서 발견된 예외 없음

---

## GET /notifications/subscriptions
- 핸들러: `NotificationSubscriptionController.readAll()`  (nolzo-api/src/main/java/com/noljo/nolzo/notification/controller/NotificationSubscriptionController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
파라미터 없음

### 응답 200 OK  (List<SeatAvailabilitySubscriptionResponse>)
```json
[
  {
    "id": 1,
    "memberId": 1,
    "eventId": 1,
    "eventScheduleId": 1,
    "channel": "EMAIL",
    "status": "ACTIVE",
    "lastNotifiedAt": "2026-09-01T19:00:00",
    "createdAt": "2026-09-01T19:00:00"
  }
]
```

### 에러
소스에서 발견된 예외 없음

---

## POST /notifications/subscriptions
- 핸들러: `NotificationSubscriptionController.create()`  (nolzo-api/src/main/java/com/noljo/nolzo/notification/controller/NotificationSubscriptionController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
본문 (CreateSeatAvailabilitySubscriptionRequest):
```json
{
  "eventId": 1,
  "eventScheduleId": 1,
  "channel": "EMAIL"
}
```
필드:
- `eventId` Long
- `eventScheduleId` Long
- `channel` NotificationChannel  (값: EMAIL)
(검증 어노테이션 없음)

예시 호출:
```
curl -X POST 'http://localhost:8080/notifications/subscriptions' \
  -H 'Authorization: Bearer <accessToken>' \
  -H 'Content-Type: application/json' \
  -d '{ "eventId": 1, "eventScheduleId": 1, "channel": "EMAIL" }'
```

### 응답 200 OK  (SeatAvailabilitySubscriptionResponse)
```json
{
  "id": 1,
  "memberId": 1,
  "eventId": 1,
  "eventScheduleId": 1,
  "channel": "EMAIL",
  "status": "ACTIVE",
  "lastNotifiedAt": "2026-09-01T19:00:00",
  "createdAt": "2026-09-01T19:00:00"
}
```

### 에러
- **500** HttpMessageNotReadableException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 JSON 파싱 실패"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — SeatAvailabilitySubscriptionService.create(): "이미 동일한 빈자리 알림을 구독 중입니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — SeatAvailabilitySubscriptionService.validateCreate(): "존재하지 않는 회차입니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — SeatAvailabilitySubscriptionService.validateCreate(): "해당 이벤트에 속하지 않은 회차입니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## DELETE /notifications/subscriptions/{subscriptionId}
- 핸들러: `NotificationSubscriptionController.cancel()`  (nolzo-api/src/main/java/com/noljo/nolzo/notification/controller/NotificationSubscriptionController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
경로 변수:
- `subscriptionId` Long, 필수

예시 호출:
```
curl -X DELETE 'http://localhost:8080/notifications/subscriptions/1' \
  -H 'Authorization: Bearer <accessToken>'
```

### 응답 204 No Content
(본문 없음)

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (subscriptionId: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — SeatAvailabilitySubscriptionService.cancel(): "해당 구독 정보가 없습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — SeatAvailabilitySubscriptionService.cancel(): "이미 해지된 구독입니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## POST /payments
- 핸들러: `PaymentController.createPayment()`  (nolzo-api/src/main/java/com/noljo/nolzo/payment/controller/PaymentController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
본문 (PaymentRequest):
```json
{
  "reservationId": 1,
  "paymentMethod": "CREDIT_CARD",
  "paymentStatus": "ACTIVE"
}
```
필드:
- `reservationId` Long
- `paymentMethod` PaymentMethod  (값: CREDIT_CARD/DEBIT_CARD/PAYPAL/BANK_TRANSFER/CASH)
- `paymentStatus` String
(검증 어노테이션 없음)

예시 호출:
```
curl -X POST 'http://localhost:8080/payments' \
  -H 'Authorization: Bearer <accessToken>' \
  -H 'Content-Type: application/json' \
  -d '{ "reservationId": 1, "paymentMethod": "CREDIT_CARD", "paymentStatus": "ACTIVE" }'
```

### 응답 200 OK  (PaymentResponse)
```json
{
  "id": 1,
  "price": 15000,
  "paymentMethod": "CREDIT_CARD"
}
```

### 에러
- **500** HttpMessageNotReadableException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 JSON 파싱 실패"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```

---

## GET /reservations
- 핸들러: `ReservationController.getReservations()`  (nolzo-api/src/main/java/com/noljo/nolzo/reservation/controller/ReservationController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
파라미터 없음

### 응답 200 OK  (List<ReservationEventInfo>)
```json
[
  {
    "event": {
      "id": 1,
      "title": "예시 제목",
      "venue": "서울 올림픽공원 KSPO DOME",
      "date": "2026-09-01",
      "time": "19:00:00",
      "image": "https://example.com/image.png"
    },
    "detail": {
      "id": 1,
      "tickets": [
        {
          "ticketId": 1,
          "ticketStatus": "NOT_USED",
          "eventTitle": "예시 제목",
          "rowName": "홍길동",
          "seatNumber": 3,
          "seatSection": "A"
        }
      ],
      "status": "ACTIVE",
      "totalPrice": 15000,
      "reservationNumber": "NOLZO2600001",
      "createdAt": "2026-09-01T19:00:00",
      "paymentMethod": "CREDIT_CARD"
    }
  }
]
```

### 에러
소스에서 발견된 예외 없음

---

## POST /reservations
- 핸들러: `ReservationController.create()`  (nolzo-api/src/main/java/com/noljo/nolzo/reservation/controller/ReservationController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
헤더:
- `Idempotency-Key` String, 필수
본문 (ReservationRequest):
```json
{
  "eventId": 1,
  "seats": [
    {
      "id": 1,
      "rowName": "홍길동",
      "seatNumber": 3,
      "seatSection": "A",
      "floor": "string",
      "price": 15000,
      "status": "RESERVED",
      "schedule": {
        "id": 1,
        "showDate": "2026-09-01",
        "showTime": "19:00:00",
        "reservationStart": "2026-09-01T19:00:00",
        "reservationEnd": "2026-09-01T19:00:00",
        "event": {
          "id": 1,
          "title": "예시 제목",
          "venue": "서울 올림픽공원 KSPO DOME",
          "description": "예시 내용입니다.",
          "posterImageUrl": "https://example.com/image.png",
          "startDate": "2026-09-01",
          "endDate": "2026-09-01",
          "eventCategory": "MUSICAL",
          "runtime": 120,
          "ageLimit": 19,
          "viewCount": 10,
          "averageRating": 4.5
        }
      }
    }
  ]
}
```
필드:
- `eventId` Long
- `seats` List<Seat>
(검증 어노테이션 없음)

예시 호출:
```
curl -X POST 'http://localhost:8080/reservations' \
  -H 'Authorization: Bearer <accessToken>' \
  -H 'Idempotency-Key: NOLZO2600001' \
  -H 'Content-Type: application/json' \
  -d '{ "eventId": 1, "seats": [ { "id": 1, "rowName": "홍길동", "seatNumber": 3, "seatSection": "A", "floor": "string", "price": 15000, "status": "RESERVED", "schedule": { "id": 1, "showDate": "2026-09-01", "showTime": "19:00:00", "reservationStart": "2026-09-01T19:00:00", "reservationEnd": "2026-09-01T19:00:00", "event": { "id": 1, "title": "예시 제목", "venue": "서울 올림픽공원 KSPO DOME", "description": "예시 내용입니다.", "posterImageUrl": "https://example.com/image.png", "startDate": "2026-09-01", "endDate": "2026-09-01", "eventCategory": "MUSICAL", "runtime": 120, "ageLimit": 19, "viewCount": 10, "averageRating": 4.5 } } } ] }'
```

### 응답 200 OK  (ReservationResponse)
```json
{
  "id": 1,
  "status": "PENDING",
  "totalPrice": 15000,
  "reservationNumber": "NOLZO2600001"
}
```

### 에러
- **500** MissingRequestHeaderException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 헤더 'Idempotency-Key' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** HttpMessageNotReadableException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 JSON 파싱 실패"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — SeatService.findSeatByIdWithPessimisticLock(): "Seat with id …"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** SeatException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — SeatService.validateIsAvailable()
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /reservations/cancel
- 핸들러: `ReservationController.getCancelReservations()`  (nolzo-api/src/main/java/com/noljo/nolzo/reservation/controller/ReservationController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
파라미터 없음

### 응답 200 OK  (List<ReservationEventInfo>)
```json
[
  {
    "event": {
      "id": 1,
      "title": "예시 제목",
      "venue": "서울 올림픽공원 KSPO DOME",
      "date": "2026-09-01",
      "time": "19:00:00",
      "image": "https://example.com/image.png"
    },
    "detail": {
      "id": 1,
      "tickets": [
        {
          "ticketId": 1,
          "ticketStatus": "NOT_USED",
          "eventTitle": "예시 제목",
          "rowName": "홍길동",
          "seatNumber": 3,
          "seatSection": "A"
        }
      ],
      "status": "ACTIVE",
      "totalPrice": 15000,
      "reservationNumber": "NOLZO2600001",
      "createdAt": "2026-09-01T19:00:00",
      "paymentMethod": "CREDIT_CARD"
    }
  }
]
```

### 에러
소스에서 발견된 예외 없음

---

## GET /reservations/confirmed
- 핸들러: `ReservationController.getReservationConfirmed()`  (nolzo-api/src/main/java/com/noljo/nolzo/reservation/controller/ReservationController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
파라미터 없음

### 응답 200 OK  (List<ReservationEventInfo>)
```json
[
  {
    "event": {
      "id": 1,
      "title": "예시 제목",
      "venue": "서울 올림픽공원 KSPO DOME",
      "date": "2026-09-01",
      "time": "19:00:00",
      "image": "https://example.com/image.png"
    },
    "detail": {
      "id": 1,
      "tickets": [
        {
          "ticketId": 1,
          "ticketStatus": "NOT_USED",
          "eventTitle": "예시 제목",
          "rowName": "홍길동",
          "seatNumber": 3,
          "seatSection": "A"
        }
      ],
      "status": "ACTIVE",
      "totalPrice": 15000,
      "reservationNumber": "NOLZO2600001",
      "createdAt": "2026-09-01T19:00:00",
      "paymentMethod": "CREDIT_CARD"
    }
  }
]
```

### 에러
소스에서 발견된 예외 없음

---

## GET /reservations/details/{reservationId}
- 핸들러: `ReservationController.getReservationDetails()`  (nolzo-api/src/main/java/com/noljo/nolzo/reservation/controller/ReservationController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
경로 변수:
- `reservationId` Long, 필수

예시 호출:
```
curl -X GET 'http://localhost:8080/reservations/details/1' \
  -H 'Authorization: Bearer <accessToken>'
```

### 응답 200 OK  (ReservationEventInfo)
```json
{
  "event": {
    "id": 1,
    "title": "예시 제목",
    "venue": "서울 올림픽공원 KSPO DOME",
    "date": "2026-09-01",
    "time": "19:00:00",
    "image": "https://example.com/image.png"
  },
  "detail": {
    "id": 1,
    "tickets": [
      {
        "ticketId": 1,
        "ticketStatus": "NOT_USED",
        "eventTitle": "예시 제목",
        "rowName": "홍길동",
        "seatNumber": 3,
        "seatSection": "A"
      }
    ],
    "status": "ACTIVE",
    "totalPrice": 15000,
    "reservationNumber": "NOLZO2600001",
    "createdAt": "2026-09-01T19:00:00",
    "paymentMethod": "CREDIT_CARD"
  }
}
```

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (reservationId: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** IllegalStateException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — ReservationService.findReservationDetails(): "예약에 연결된 공연이 없습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /reservations/reservation/{eventId}
- 핸들러: `ReservationController.findSeatsByEventId()`  (nolzo-api/src/main/java/com/noljo/nolzo/reservation/controller/ReservationController.java)
- 인증: 명시된 인증 주체 없음

### 요청
경로 변수:
- `eventId` Long, 필수
쿼리 파라미터:
- `date` String, 필수
- `time` String, 필수

예시 호출:
```
curl -X GET 'http://localhost:8080/reservations/reservation/1?date=2026-09-01&time=19:00:00'
```

### 응답 200 OK  (List<SeatResponse>)
```json
[
  {
    "id": 1,
    "rowName": "홍길동",
    "seatNumber": 3,
    "seatSection": "A",
    "floor": "string",
    "price": 15000,
    "status": "RESERVED"
  }
]
```

### 에러
- **500** MissingServletRequestParameterException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 쿼리 파라미터 'date' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** MissingServletRequestParameterException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 쿼리 파라미터 'time' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (eventId: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## DELETE /reservations/reservation/{reservationId}
- 핸들러: `ReservationController.cancelReservation()`  (nolzo-api/src/main/java/com/noljo/nolzo/reservation/controller/ReservationController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
경로 변수:
- `reservationId` Long, 필수

예시 호출:
```
curl -X DELETE 'http://localhost:8080/reservations/reservation/1' \
  -H 'Authorization: Bearer <accessToken>'
```

### 응답 200 OK  (ReservationCancelResponse)
```json
{
  "id": 1,
  "status": "ACTIVE"
}
```

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (reservationId: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — ReservationService.cancelReservationById(): "해당 예약 정보가 없습니다"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — ReservationService.cancelReservationById(): "해당 예약자만 예약을 취소할 수 있습니다"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /reservations/used
- 핸들러: `ReservationController.getTicketsUsed()`  (nolzo-api/src/main/java/com/noljo/nolzo/reservation/controller/ReservationController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
파라미터 없음

### 응답 200 OK  (List<ReservationEventInfo>)
```json
[
  {
    "event": {
      "id": 1,
      "title": "예시 제목",
      "venue": "서울 올림픽공원 KSPO DOME",
      "date": "2026-09-01",
      "time": "19:00:00",
      "image": "https://example.com/image.png"
    },
    "detail": {
      "id": 1,
      "tickets": [
        {
          "ticketId": 1,
          "ticketStatus": "NOT_USED",
          "eventTitle": "예시 제목",
          "rowName": "홍길동",
          "seatNumber": 3,
          "seatSection": "A"
        }
      ],
      "status": "ACTIVE",
      "totalPrice": 15000,
      "reservationNumber": "NOLZO2600001",
      "createdAt": "2026-09-01T19:00:00",
      "paymentMethod": "CREDIT_CARD"
    }
  }
]
```

### 에러
소스에서 발견된 예외 없음

---

## POST /reservations/{eventId}
- 핸들러: `ReservationController.chooseEventDateTime()`  (nolzo-api/src/main/java/com/noljo/nolzo/reservation/controller/ReservationController.java)
- 인증: 명시된 인증 주체 없음

### 요청
경로 변수:
- `eventId` Long, 필수
쿼리 파라미터:
- `selectDate` LocalDate, 필수, 형식 yyyy-MM-dd
- `selectTime` LocalTime, 필수, 형식 HH:mm:ss

예시 호출:
```
curl -X POST 'http://localhost:8080/reservations/1?selectDate=2026-09-01&selectTime=19:00:00'
```

### 응답 200 OK  (EventDateTimeResponse)
```json
{
  "id": 1,
  "showDate": "2026-09-01",
  "showTime": "19:00:00"
}
```

### 에러
- **500** MissingServletRequestParameterException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 쿼리 파라미터 'selectDate' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** MissingServletRequestParameterException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "필수 쿼리 파라미터 'selectTime' 누락"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (eventId: Long, selectDate: LocalDate, selectTime: LocalTime)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — ReservationService.readSelectedEventDateTime(): "해당 이벤트가 존재하지 않습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — ReservationService.readSelectedEventDateTime(): "선택한 날짜와 시간의 스케줄이 존재하지 않습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## POST /reviews
- 핸들러: `ReviewController.createReview()`  (nolzo-api/src/main/java/com/noljo/nolzo/review/controller/ReviewController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
본문 (ReviewCreateRequest):
```json
{
  "content": "예시 내용입니다.",
  "rating": 5,
  "eventId": 1
}
```
필드:
- `content` String  — @NotBlank
- `rating` int  — @NotNull @Min(1) @Max(5)
- `eventId` Long  — @NotNull

예시 호출:
```
curl -X POST 'http://localhost:8080/reviews' \
  -H 'Authorization: Bearer <accessToken>' \
  -H 'Content-Type: application/json' \
  -d '{ "content": "예시 내용입니다.", "rating": 5, "eventId": 1 }'
```

### 응답 200 OK  (ReviewResponse)
```json
{
  "id": 1,
  "content": "예시 내용입니다.",
  "rating": 5,
  "eventId": 1,
  "createdAt": "2026-09-01T19:00:00"
}
```

### 에러
- **500** MethodArgumentNotValidException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 검증 실패 (content: @NotBlank, rating: @NotNull, rating: @Min(1), rating: @Max(5), eventId: @NotNull)"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** HttpMessageNotReadableException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 JSON 파싱 실패"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalStateException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — ReviewService.validateEventParticipation(): "리뷰는 관람 완료된 이벤트에 대해서만 작성할 수 있습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalStateException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — ReviewService.validateAlreadyReviewed(): "이미 해당 이벤트에 대한 리뷰를 작성하였습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /reviews/events/{eventId}
- 핸들러: `ReviewController.getReviewsByEvent()`  (nolzo-api/src/main/java/com/noljo/nolzo/review/controller/ReviewController.java)
- 인증: 명시된 인증 주체 없음

### 요청
경로 변수:
- `eventId` Long, 필수
쿼리 파라미터:
- `page` int, 선택, 기본값 0
- `size` int, 선택, 기본값 5

예시 호출:
```
curl -X GET 'http://localhost:8080/reviews/events/1?page=0&size=5'
```

### 응답 200 OK  (EventReviewPageResponse)
```json
{
  "averageRating": 4.5,
  "totalReviewCount": 10,
  "reviews": [
    {
      "id": 1,
      "content": "예시 내용입니다.",
      "rating": 5,
      "author": "string",
      "createdAt": "2026-09-01T19:00:00"
    }
  ],
  "currentPage": 0,
  "totalPages": 0,
  "pageSize": 0,
  "hasNext": true,
  "hasPrevious": true
}
```

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (eventId: Long, page: int, size: int)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```

---

## GET /reviews/events/{eventId}/my
- 핸들러: `ReviewController.getMyReviewByEvent()`  (nolzo-api/src/main/java/com/noljo/nolzo/review/controller/ReviewController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
경로 변수:
- `eventId` Long, 필수

예시 호출:
```
curl -X GET 'http://localhost:8080/reviews/events/1/my' \
  -H 'Authorization: Bearer <accessToken>'
```

### 응답 200 OK  (ReviewResponse)
```json
{
  "id": 1,
  "content": "예시 내용입니다.",
  "rating": 5,
  "eventId": 1,
  "createdAt": "2026-09-01T19:00:00"
}
```

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (eventId: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — ReviewService.getReviewByMemberIdAndEventId(): "리뷰를 조회할 수 없습니다"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /reviews/my
- 핸들러: `ReviewController.getMemberReview()`  (nolzo-api/src/main/java/com/noljo/nolzo/review/controller/ReviewController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
파라미터 없음

### 응답 200 OK  (List<ReviewResponse>)
```json
[
  {
    "id": 1,
    "content": "예시 내용입니다.",
    "rating": 5,
    "eventId": 1,
    "createdAt": "2026-09-01T19:00:00"
  }
]
```

### 에러
소스에서 발견된 예외 없음

---

## DELETE /reviews/{reviewId}
- 핸들러: `ReviewController.deleteReview()`  (nolzo-api/src/main/java/com/noljo/nolzo/review/controller/ReviewController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
경로 변수:
- `reviewId` Long, 필수

예시 호출:
```
curl -X DELETE 'http://localhost:8080/reviews/1' \
  -H 'Authorization: Bearer <accessToken>'
```

### 응답 204 No Content
(본문 없음)

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (reviewId: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** IllegalStateException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — ReviewService.validateReviewOwner(): "해당 리뷰를 수정할 권한이 없습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## GET /reviews/{reviewId}
- 핸들러: `ReviewController.getReview()`  (nolzo-api/src/main/java/com/noljo/nolzo/review/controller/ReviewController.java)
- 인증: 명시된 인증 주체 없음

### 요청
경로 변수:
- `reviewId` Long, 필수

예시 호출:
```
curl -X GET 'http://localhost:8080/reviews/1'
```

### 응답 200 OK  (ReviewResponse)
```json
{
  "id": 1,
  "content": "예시 내용입니다.",
  "rating": 5,
  "eventId": 1,
  "createdAt": "2026-09-01T19:00:00"
}
```

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (reviewId: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```

---

## PUT /reviews/{reviewId}
- 핸들러: `ReviewController.updateReview()`  (nolzo-api/src/main/java/com/noljo/nolzo/review/controller/ReviewController.java)
- 인증: 필요 (@AuthenticationPrincipal / 보안 어노테이션)

### 요청
경로 변수:
- `reviewId` Long, 필수
본문 (ReviewUpdateRequest):
```json
{
  "content": "예시 내용입니다.",
  "rating": 5
}
```
필드:
- `content` String  — @NotBlank
- `rating` int  — @Min(1) @Max(5)

예시 호출:
```
curl -X PUT 'http://localhost:8080/reviews/1' \
  -H 'Authorization: Bearer <accessToken>' \
  -H 'Content-Type: application/json' \
  -d '{ "content": "예시 내용입니다.", "rating": 5 }'
```

### 응답 200 OK  (ReviewUpdateResponse)
```json
{
  "content": "예시 내용입니다.",
  "rating": 5,
  "modifiedAt": "2026-09-01T19:00:00"
}
```

### 에러
- **500** MethodArgumentNotValidException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 검증 실패 (content: @NotBlank, rating: @Min(1), rating: @Max(5))"
  처리: GlobalExceptionHandler.handleException() — 상위 타입 Exception 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** HttpMessageNotReadableException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "요청 본문 JSON 파싱 실패"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (reviewId: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
- **500** IllegalStateException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — ReviewService.validateReviewOwner(): "해당 리뷰를 수정할 권한이 없습니다."
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

## POST /test/queue/recover
- 핸들러: `QueueRecoveryTestController.recover()`  (nolzo-api/src/main/java/com/noljo/nolzo/queue/controller/QueueRecoveryTestController.java)
- 인증: 명시된 인증 주체 없음

### 요청
파라미터 없음

### 응답 204 No Content
(본문 없음)

### 에러
소스에서 발견된 예외 없음

---

## GET /tickets
- 핸들러: `TicketController.findTickets()`  (nolzo-api/src/main/java/com/noljo/nolzo/ticket/controller/TicketController.java)
- 인증: 명시된 인증 주체 없음

### 요청
쿼리 파라미터:
- `memberId` Long, 선택

예시 호출:
```
curl -X GET 'http://localhost:8080/tickets?memberId=1'
```

### 응답 200 OK  (List<TicketResponse>)
```json
[
  {
    "ticketId": 1,
    "ticketStatus": "NOT_USED",
    "eventTitle": "예시 제목",
    "rowName": "홍길동",
    "seatNumber": 3,
    "seatSection": "A"
  }
]
```

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (memberId: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```

---

## GET /tickets/{ticketId}
- 핸들러: `TicketController.findTicket()`  (nolzo-api/src/main/java/com/noljo/nolzo/ticket/controller/TicketController.java)
- 인증: 명시된 인증 주체 없음

### 요청
경로 변수:
- `ticketId` Long, 필수

예시 호출:
```
curl -X GET 'http://localhost:8080/tickets/1'
```

### 응답 200 OK  (TicketResponse)
```json
{
  "ticketId": 1,
  "ticketStatus": "NOT_USED",
  "eventTitle": "예시 제목",
  "rowName": "홍길동",
  "seatNumber": 3,
  "seatSection": "A"
}
```

### 에러
- **500** MethodArgumentTypeMismatchException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — Spring (요청 바인딩): "경로/쿼리 파라미터 타입 불일치 (ticketId: Long)"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨
  ```json
  {
    "status": 500,
    "message": "서버 내부 오류가 발생했습니다."
  }
  ```
- **500** IllegalArgumentException [GlobalErrorCode.INTERNAL_SERVER_ERROR] — TicketService.findTicketById(): "Ticket Not Found"
  처리: GlobalExceptionHandler.handleRuntimeException() — 상위 타입 RuntimeException 핸들러로 처리됨

---

