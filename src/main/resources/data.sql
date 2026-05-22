-- ============================================================
-- 더미 데이터
-- "감자(id=1)"를 로그인한 나로 가정하면, 모든 RelationLevel을
-- 한 화면에서 확인할 수 있도록 구성.
-- 추가로 다른 사용자끼리의 관계(B-C 간선)도 포함해
-- /api/graph 와 /api/users/1/relations 의 차이를 비교 가능.
-- ============================================================

-- ---------------- USERS ----------------
-- 모든 더미 계정의 평문 비밀번호: "password"
-- BCrypt 해시(cost=10): $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy
INSERT INTO users (id, nickname, password, introduction) VALUES (1, '감자',   '$2a$10$/B7nFOJqDXg9sSbmJ.y/PuXeIrjUXJDbSOVNR7FO9mQbj1iTpkBgW', '안녕하세요, 감자입니다.');
INSERT INTO users (id, nickname, password, introduction) VALUES (2, '고구마', '$2a$10$/B7nFOJqDXg9sSbmJ.y/PuXeIrjUXJDbSOVNR7FO9mQbj1iTpkBgW', '달콤한 고구마예요.');
INSERT INTO users (id, nickname, password, introduction) VALUES (3, '당근',   '$2a$10$/B7nFOJqDXg9sSbmJ.y/PuXeIrjUXJDbSOVNR7FO9mQbj1iTpkBgW', '건강한 당근입니다.');
INSERT INTO users (id, nickname, password, introduction) VALUES (4, '양파',   '$2a$10$/B7nFOJqDXg9sSbmJ.y/PuXeIrjUXJDbSOVNR7FO9mQbj1iTpkBgW', '눈물나는 양파.');
INSERT INTO users (id, nickname, password, introduction) VALUES (5, '마늘',   '$2a$10$/B7nFOJqDXg9sSbmJ.y/PuXeIrjUXJDbSOVNR7FO9mQbj1iTpkBgW', '톡 쏘는 마늘.');
INSERT INTO users (id, nickname, password, introduction) VALUES (6, '호박',   '$2a$10$/B7nFOJqDXg9sSbmJ.y/PuXeIrjUXJDbSOVNR7FO9mQbj1iTpkBgW', '둥글둥글 호박.');
INSERT INTO users (id, nickname, password, introduction) VALUES (7, '가지',   '$2a$10$/B7nFOJqDXg9sSbmJ.y/PuXeIrjUXJDbSOVNR7FO9mQbj1iTpkBgW', '보라보라 가지.');
INSERT INTO users (id, nickname, password, introduction) VALUES (8, '토마토', '$2a$10$/B7nFOJqDXg9sSbmJ.y/PuXeIrjUXJDbSOVNR7FO9mQbj1iTpkBgW', '빨간 토마토입니다.');

-- ---------------- RELATIONS ----------------
-- (user_a_id < user_b_id 정렬 규칙 준수)

-- STRANGER (0~3)  ────────────────────────
-- 감자 ↔ 고구마 : FOLLOW (1)
INSERT INTO relations (id, user_a_id, user_b_id, weight, level, last_interacted_at)
VALUES (1, 1, 2, 1, 'STRANGER', CURRENT_TIMESTAMP);
-- 감자 ↔ 당근 : FOLLOW + MESSAGE (1+2 = 3)
INSERT INTO relations (id, user_a_id, user_b_id, weight, level, last_interacted_at)
VALUES (2, 1, 3, 3, 'STRANGER', CURRENT_TIMESTAMP);

-- AWKWARD (4~9)  ────────────────────────
-- 감자 ↔ 양파 : COFFEE (4)
INSERT INTO relations (id, user_a_id, user_b_id, weight, level, last_interacted_at)
VALUES (3, 1, 4, 4, 'AWKWARD', CURRENT_TIMESTAMP);
-- 감자 ↔ 마늘 : FOLLOW + MESSAGE + COFFEE (1+2+4 = 7)
INSERT INTO relations (id, user_a_id, user_b_id, weight, level, last_interacted_at)
VALUES (4, 1, 5, 7, 'AWKWARD', CURRENT_TIMESTAMP);

-- GETTING_CLOSER (10~19) ────────────────
-- 감자 ↔ 호박 : DRINK (10)
INSERT INTO relations (id, user_a_id, user_b_id, weight, level, last_interacted_at)
VALUES (5, 1, 6, 10, 'GETTING_CLOSER', CURRENT_TIMESTAMP);
-- 감자 ↔ 가지 : COFFEE + MEAL + COFFEE (4+7+4 = 15)
INSERT INTO relations (id, user_a_id, user_b_id, weight, level, last_interacted_at)
VALUES (6, 1, 7, 15, 'GETTING_CLOSER', CURRENT_TIMESTAMP);

-- PRETTY_CLOSE (20~39) ──────────────────
-- 감자 ↔ 토마토 : DRINK + MEAL + COFFEE (10+7+4 = 21)
INSERT INTO relations (id, user_a_id, user_b_id, weight, level, last_interacted_at)
VALUES (7, 1, 8, 21, 'PRETTY_CLOSE', CURRENT_TIMESTAMP);
-- 고구마 ↔ 당근 (B-C 관계, 내 화면엔 /api/graph 에서만 보임)
-- MEAL + DRINK + MEAL + MESSAGE (7+10+7+2 = 26)
INSERT INTO relations (id, user_a_id, user_b_id, weight, level, last_interacted_at)
VALUES (8, 2, 3, 26, 'PRETTY_CLOSE', CURRENT_TIMESTAMP);

-- POTATO_ALLIANCE (40+) ─────────────────
-- 고구마 ↔ 양파 : DRINK*4 + MEAL + COFFEE (40+7+4 = 51)
INSERT INTO relations (id, user_a_id, user_b_id, weight, level, last_interacted_at)
VALUES (9, 2, 4, 51, 'POTATO_ALLIANCE', CURRENT_TIMESTAMP);
-- 당근 ↔ 마늘 : DRINK*4 (40)
INSERT INTO relations (id, user_a_id, user_b_id, weight, level, last_interacted_at)
VALUES (10, 3, 5, 40, 'POTATO_ALLIANCE', CURRENT_TIMESTAMP);

-- ---------------- RELATION_ACTIONS (수행한 행동 목록) ----------------
INSERT INTO relation_actions (relation_id, action_type) VALUES (1, 'FOLLOW');

INSERT INTO relation_actions (relation_id, action_type) VALUES (2, 'FOLLOW');
INSERT INTO relation_actions (relation_id, action_type) VALUES (2, 'MESSAGE');

INSERT INTO relation_actions (relation_id, action_type) VALUES (3, 'COFFEE');

INSERT INTO relation_actions (relation_id, action_type) VALUES (4, 'FOLLOW');
INSERT INTO relation_actions (relation_id, action_type) VALUES (4, 'MESSAGE');
INSERT INTO relation_actions (relation_id, action_type) VALUES (4, 'COFFEE');

INSERT INTO relation_actions (relation_id, action_type) VALUES (5, 'DRINK');

INSERT INTO relation_actions (relation_id, action_type) VALUES (6, 'COFFEE');
INSERT INTO relation_actions (relation_id, action_type) VALUES (6, 'MEAL');
INSERT INTO relation_actions (relation_id, action_type) VALUES (6, 'COFFEE');

INSERT INTO relation_actions (relation_id, action_type) VALUES (7, 'DRINK');
INSERT INTO relation_actions (relation_id, action_type) VALUES (7, 'MEAL');
INSERT INTO relation_actions (relation_id, action_type) VALUES (7, 'COFFEE');

INSERT INTO relation_actions (relation_id, action_type) VALUES (8, 'MEAL');
INSERT INTO relation_actions (relation_id, action_type) VALUES (8, 'DRINK');
INSERT INTO relation_actions (relation_id, action_type) VALUES (8, 'MEAL');
INSERT INTO relation_actions (relation_id, action_type) VALUES (8, 'MESSAGE');

INSERT INTO relation_actions (relation_id, action_type) VALUES (9, 'DRINK');
INSERT INTO relation_actions (relation_id, action_type) VALUES (9, 'DRINK');
INSERT INTO relation_actions (relation_id, action_type) VALUES (9, 'DRINK');
INSERT INTO relation_actions (relation_id, action_type) VALUES (9, 'DRINK');
INSERT INTO relation_actions (relation_id, action_type) VALUES (9, 'MEAL');
INSERT INTO relation_actions (relation_id, action_type) VALUES (9, 'COFFEE');

INSERT INTO relation_actions (relation_id, action_type) VALUES (10, 'DRINK');
INSERT INTO relation_actions (relation_id, action_type) VALUES (10, 'DRINK');
INSERT INTO relation_actions (relation_id, action_type) VALUES (10, 'DRINK');
INSERT INTO relation_actions (relation_id, action_type) VALUES (10, 'DRINK');
