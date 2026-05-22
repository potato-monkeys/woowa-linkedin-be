# 우테코 링크드인 - 프로젝트 API 및 도메인 명세서

본 문서는 우테코 링크드인 프로젝트의 **도메인(유저, 추천, 스와이프, 메시지 등)** 구현 내용을 정리한 문서입니다. 기획 문서의 "관계 간선 그래프" 기반 인맥 시각화 및 행동 제안 기능을 지원하도록 설계되었습니다.

---

## 🏗️ 도메인 구조 및 기능 요약

### 1. 관계 그래프 (Relation Domain)
크루 간의 상호작용 행위를 바탕으로 관계 가중치를 누적하고 그래프를 형성합니다.

- **`RelationshipEdge`**: 두 유저(크루) 사이의 누적 가중치(Weight)를 저장하는 간선(Edge) 엔티티. 이 데이터를 기반으로 프론트엔드에서 인맥 네트워크 그래프를 그리고 추천 점수를 계산합니다.
- **`ActionRequest`**: 특정 크루에게 행위(커피 마시기, 밥 먹기 등)를 제안(요청)했을 때 생성되는 엔티티.
  - 상태값: `PENDING` (대기 중), `ACCEPTED` (수락됨), `REJECTED` (거절됨).
- **`RelationAction` (행위 가중치 Enum)**:
  - `FOLLOW` (1점)
  - `MESSAGE` (2점)
  - `COFFEE` (4점)
  - `MEAL` (7점)
  - `DRINK` (10점)

- **`RelationLevel` (가중치 누적에 따른 관계 단계)**:
  - `0 ~ 3점`: 모르는 사이
  - `4 ~ 9점`: 어색한 사이
  - `10 ~ 19점`: 친해지는 중
  - `20 ~ 39점`: 꽤 친함
  - `40점 이상`: 감자 동맹

### 2. 추천 시스템 (Recommendation Domain)
관계 그래프의 간선 가중치(Weight)를 활용하여 나와 가까운(점수가 높은) 크루를 추천해 줍니다.

- **API**: `GET /recommendations`
- **로직**:
  - 오늘 이미 스와이프한 대상은 추천 목록에서 제외됩니다.
  - 하루 추천 할당량(예: 20명)을 초과하면 더 이상 추천되지 않습니다.
  - 추천 점수는 `RelationshipEdge`의 Weight에 비례하며, 시연을 위해 데모용 랜덤 팩터가 일부 포함되어 있습니다.

### 3. 스와이프 및 행위 제안 (Swipe Domain)
소개팅 어플처럼 추천된 크루를 스와이프하여 구체적인 행동을 제안하거나 거절합니다.

- **API**: `POST /swipes`
- **로직**:
  - **제안 (`PROPOSE`)**: `ActionRequest`를 생성하여 해당 크루에게 구체적인 행위(예: `COFFEE`, `MEAL`)를 요청(`PENDING`)합니다.
  - **거절 (`PASS`)**: 제안을 하지 않고 대상 크루를 넘깁니다. 
  - **자동 메시지**: 모두 거절(PASS)하는 경우, 해당 크루에게 안내 및 위로의 쪽지가 자동 발송되어 소통의 기회를 남겨둡니다.

### 4. 메시지 (Message Domain)
크루 간의 1:1 쪽지 기능을 담당하며, 소통할수록 관계가 깊어집니다.

- **API 목록**:
  - `POST /messages`: 특정 유저에게 쪽지 발송.
  - `GET /messages/rooms`: 내가 속한 대화방 목록 조회 (마지막 메시지 및 안읽은 개수 포함).
  - `GET /messages/{userId}`: 특정 유저와의 전체 쪽지 내역 조회 (자동 읽음 처리).
- **가중치 연동**:
  - 쪽지를 보낼 때마다 `RelationshipEdge`에 `MESSAGE(2점)` 가중치가 더해져 두 유저의 관계가 더욱 끈끈해지는 구조로 구현되어 있습니다.

---

## 📝 API 명세서 (API Specification)

모든 인증이 필요한 API는 Spring Security의 `@AuthenticationPrincipal`을 통해 현재 로그인된 유저의 정보를 받아옵니다. 프론트엔드는 로그인 시 발급받은 토큰(또는 세션)을 통해 인증을 수행해야 합니다.

### 1. 유저 (User) API
#### `POST /api/users/signup`
새로운 크루 회원가입을 진행합니다.
- **Request Body**
  ```json
  {
    "nickname": "감자",
    "password": "password123!",
    "introduction": "안녕하세요!"
  }
  ```
- **Response**: `201 Created`

#### `POST /api/users/login`
로그인을 수행하고 인증 정보를 반환받습니다.
- **Request Body**
  ```json
  {
    "nickname": "감자",
    "password": "password123!"
  }
  ```
- **Response Body**
  - 로그인 성공에 대한 토큰 및 유저 정보 객체 반환 (`LoginResponse`)

#### `GET /api/users/me`
현재 로그인한 나의 프로필 정보를 조회합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Response Body** (`UserResponse`)
  ```json
  {
    "id": 1,
    "nickname": "감자",
    "introduction": "안녕하세요!",
    "profileImageUrl": "https://...",
    "recordingFileUrl": "https://..."
  }
  ```

#### `PATCH /api/users/me`
내 프로필 정보(닉네임, 소개글, 비밀번호 등)를 수정합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Request Body**
  ```json
  {
    "nickname": "구운감자",
    "introduction": "반가워요",
    "currentPassword": "password123!",
    "newPassword": "newpassword123!"
  }
  ```
- **Response Body**: 수정된 `UserResponse` 객체

#### `POST /api/users/me/profile-image`
내 프로필 이미지를 업로드/수정합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Request (Multipart/form-data)**
  - `file`: 이미지 파일 (MultipartFile)
- **Response Body**: 수정된 `UserResponse` 객체

#### `POST /api/users/me/recording`
내 음성 소개 녹음 파일을 업로드/수정합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Request (Multipart/form-data)**
  - `file`: 녹음 파일 (MultipartFile, Optional)
  - `defaultUrl`: 기본 URL (String, Optional)
- **Response Body**: 수정된 `UserResponse` 객체

#### `GET /api/users/{id}`
특정 유저의 프로필 정보를 조회합니다.
- **Path Variable**: `id` (유저 ID)
- **Response Body**: `UserResponse` 객체

---

### 2. 추천 (Recommendation) API
#### `GET /recommendations`
나와 가까운(관계 가중치 점수가 높은) 크루 최대 20명을 추천합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Response Body**
  ```json
  [
    {
      "userId": 2,
      "score": 15
    }
  ]
  ```

---

### 3. 스와이프 (행위 제안) API
#### `POST /swipes`
추천된 크루에게 특정 행위를 제안하거나 거절(PASS)합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Request Body**
  ```json
  {
    "targetUserId": 2,
    "action": "PROPOSE", // "PROPOSE" 또는 "PASS"
    "relationAction": "COFFEE" // action이 "PROPOSE"일 때만 포함 (FOLLOW, MESSAGE, COFFEE, MEAL, DRINK)
  }
  ```
- **Response**: `200 OK` (모두 거절 `PASS` 처리 시에는 서버에서 자동으로 해당 대상에게 위로 메시지를 발송합니다.)

---

### 4. 제안 수락/거절 API
#### `GET /requests/received`
나에게 들어온 대기 중(`PENDING`)인 행위 제안 목록을 최신순으로 조회합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Response Body**
  ```json
  [
    {
      "id": 1,
      "senderId": 3,
      "action": "COFFEE",
      "createdAt": "2026-05-22T14:00:00"
    }
  ]
  ```

#### `POST /requests/{id}/accept`
상대방이 나에게 제안한 특정 행위(예: 커피 마시기)를 수락합니다. 수락 시 제안된 행위의 가중치만큼 두 사람의 관계 그래프 가중치가 증가합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Path Variable**: `id` (ActionRequest 제안 ID)
- **Response**: `200 OK`

#### `POST /requests/{id}/reject`
상대방이 나에게 제안한 행위를 거절합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Path Variable**: `id` (ActionRequest 제안 ID)
- **Response**: `200 OK`

---

### 5. 메시지 (Message) API
#### `POST /messages`
특정 크루에게 쪽지를 발송합니다. (발송 시 `MESSAGE` 가중치 2점이 관계 그래프에 자동으로 추가됩니다.)
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Request Body**
  ```json
  {
    "receiverId": 2,
    "content": "안녕하세요, 같이 커피 한 잔 하실래요?"
  }
  ```
- **Response**: `200 OK`

#### `GET /messages/rooms`
내가 참여 중인 대화방 목록을 최근 업데이트 순으로 조회합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Response Body**
  ```json
  [
    {
      "roomId": 1,
      "partnerId": 2,
      "lastMessageContent": "안녕하세요, 같이 커피 한 잔 하실래요?",
      "unreadCount": 1,
      "updatedAt": "2026-05-22T14:00:00"
    }
  ]
  ```

#### `GET /messages/{userId}`
특정 크루와의 전체 메시지 내역을 조회합니다. API 호출 시 상대방이 보낸 메시지들은 일괄 읽음(`isRead = true`) 처리됩니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Path Variable**: `userId` (상대방 유저 ID)
- **Response Body**
  ```json
  [
    {
      "messageId": 1,
      "senderId": 1,
      "content": "안녕하세요, 같이 커피 한 잔 하실래요?",
      "isRead": true,
      "createdAt": "2026-05-22T14:00:00"
    }
  ]
  ```

---

---

### 6. 그래프 (Graph) API
#### `GET /api/graph`
전체 유저 노드와 연결된 관계 간선(Edge) 리스트를 반환하여 프론트엔드 네트워크 그래프 렌더링을 지원합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Response Body**
  ```json
  {
    "nodes": [
      {
        "id": 1,
        "name": "감자",
        "introduction": "안녕하세요!"
      }
    ],
    "edges": [
      {
        "id": 1,
        "source": 1,
        "target": 2,
        "weight": 15,
        "level": "GETTING_CLOSER",
        "levelDescription": "친해지는 중"
      }
    ]
  }
  ```

---

### 7. 관계 전체 조회 (Relation) API
#### `GET /api/relations`
시스템에 존재하는 모든 관계 내역을 조회합니다.
- **Authentication**: Required (`@AuthenticationPrincipal`)
- **Response Body**
  ```json
  [
    {
      "id": 1,
      "userAId": 1,
      "userBId": 2,
      "weight": 15,
      "level": "GETTING_CLOSER",
      "levelDescription": "친해지는 중",
      "lastInteractedAt": "2026-05-22T14:00:00",
      "performedActions": ["FOLLOW", "COFFEE"]
    }
  ]
  ```

#### `GET /api/users/{id}/relations`
특정 유저의 모든 관계 내역을 조회합니다.
- **Path Variable**: `id` (유저 ID)
- **Response Body**
  ```json
  [
    {
      "id": 1,
      "userAId": 1,
      "userBId": 2,
      "weight": 15,
      "level": "GETTING_CLOSER",
      "levelDescription": "친해지는 중",
      "lastInteractedAt": "2026-05-22T14:00:00",
      "performedActions": ["FOLLOW", "COFFEE"]
    }
  ]
  ```

---

### 8. 직접 행동 제안 (Action Request - 직접 요청 API)
추천이나 스와이프를 통하지 않고, 유저 간에 직접 행위를 요청하고 응답하는 API입니다.

#### `POST /api/action-requests`
새로운 행동 요청을 생성합니다.
- **Request Body**
  ```json
  {
    "requesterId": 1,
    "receiverId": 2,
    "actionType": "COFFEE"
  }
  ```

#### `GET /api/action-requests/{id}`
특정 행동 요청의 현재 상태를 단건 조회합니다.

#### `PATCH /api/action-requests/{id}/accept`
받은 요청을 수락합니다.
- **Request Body**
  ```json
  {
    "userId": 2
  }
  ```

#### `PATCH /api/action-requests/{id}/reject`
받은 요청을 거절합니다.
- **Request Body**
  ```json
  {
    "userId": 2
  }
  ```

#### `PATCH /api/action-requests/{id}/complete`
수락된 행동을 최종 완료(완수) 처리합니다.
- **Request Body**
  ```json
  {
    "userId": 1
  }
  ```

(위 API들의 공통 Response Body 구조)
  ```json
  {
    "id": 1,
    "requesterId": 1,
    "receiverId": 2,
    "actionType": "COFFEE",
    "status": "PENDING",
    "createdAt": "2026-05-22T14:00:00",
    "respondedAt": null,
    "completedAt": null
  }
  ```
