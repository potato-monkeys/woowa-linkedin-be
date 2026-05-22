package com.example.hackethon.action.ui;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.hackethon.action.dto.ActionRequestCreateRequest;
import com.example.hackethon.action.dto.ActionRequestUserRequest;
import com.example.hackethon.user.domain.User;
import com.example.hackethon.user.domain.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@WithMockUser
class ActionRequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    @DisplayName("행동 요청을 생성하고 수락 후 완료하면 관계가 생성된다")
    void createAcceptCompleteFlow() throws Exception {
        // given
        User requester = userRepository.save(new User("감자", "password", "감자입니다"));
        User receiver = userRepository.save(new User("고구마", "password", "고구마입니다"));

        ActionRequestCreateRequest createRequest =
                new ActionRequestCreateRequest(requester.getId(), receiver.getId(), "COFFEE");

        // when: 행동 요청 생성
        String location = mockMvc.perform(post("/api/action-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andReturn().getResponse().getHeader("Location");

        // then: 생성된 요청은 PENDING 상태
        mockMvc.perform(get(location))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.actionType").value("COFFEE"));

        // when: 수신자가 수락
        ActionRequestUserRequest receiverAction = new ActionRequestUserRequest(receiver.getId());
        mockMvc.perform(patch(location + "/accept")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(receiverAction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("ACCEPTED"));

        // when: 요청자가 수행 완료
        ActionRequestUserRequest requesterAction = new ActionRequestUserRequest(requester.getId());
        mockMvc.perform(patch(location + "/complete")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requesterAction)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));

        // then: 관계가 생성되고 가중치(COFFEE=+4)와 레벨이 반영된다
        mockMvc.perform(get("/api/relations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].weight").value(4))
                .andExpect(jsonPath("$[0].level").value("AWKWARD"))
                .andExpect(jsonPath("$[0].levelDescription").value("어색한 사이"));
    }

    @Test
    @DisplayName("동일 두 사용자가 행동을 반복하면 관계 점수가 누적된다")
    void weightAccumulation() throws Exception {
        // given
        User a = userRepository.save(new User("감자", "password", "감자입니다"));
        User b = userRepository.save(new User("고구마", "password", "고구마입니다"));

        // 첫 번째 행동: 커피(+4)
        completeAction(a.getId(), b.getId(), "COFFEE");
        // 두 번째 행동: 밥(+7)
        completeAction(a.getId(), b.getId(), "MEAL");
        // 세 번째 행동: 술(+10) → 총 21점, "꽤 친함"

        completeAction(b.getId(), a.getId(), "DRINK");

        // then
        mockMvc.perform(get("/api/users/" + a.getId() + "/relations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].weight").value(21))
                .andExpect(jsonPath("$[0].level").value("PRETTY_CLOSE"));
    }

    @Test
    @DisplayName("그래프 API는 노드(사용자)와 간선(관계)을 반환한다")
    void graphApi() throws Exception {
        User a = userRepository.save(new User("감자", "password", "감자"));
        User b = userRepository.save(new User("고구마", "password", "고구마"));
        User c = userRepository.save(new User("당근", "password", "당근"));

        completeAction(a.getId(), b.getId(), "FOLLOW");

        mockMvc.perform(get("/api/graph"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nodes.length()").value(3))
                .andExpect(jsonPath("$.edges.length()").value(1))
                .andExpect(jsonPath("$.edges[0].weight").value(1));
    }

    private void completeAction(Long requesterId, Long receiverId, String actionType) throws Exception {
        ActionRequestCreateRequest createRequest =
                new ActionRequestCreateRequest(requesterId, receiverId, actionType);

        String location = mockMvc.perform(post("/api/action-requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createRequest)))
                .andReturn().getResponse().getHeader("Location");

        mockMvc.perform(patch(location + "/accept")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ActionRequestUserRequest(receiverId))));

        mockMvc.perform(patch(location + "/complete")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(new ActionRequestUserRequest(requesterId))));
    }
}
