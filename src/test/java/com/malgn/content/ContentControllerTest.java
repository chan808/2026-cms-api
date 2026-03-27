package com.malgn.content;

import com.malgn.common.security.JwtProvider;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ContentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtProvider jwtProvider;

    private String userToken;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userToken = jwtProvider.generateToken("user", "USER");
        adminToken = jwtProvider.generateToken("admin", "ADMIN");
    }

    @Test
    @DisplayName("콘텐츠를 생성한다")
    void createContent() throws Exception {
        mockMvc.perform(post("/contents")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "테스트 제목", "description": "테스트 내용"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("테스트 제목"))
                .andExpect(jsonPath("$.createdBy").value("user"));
    }

    @Test
    @DisplayName("제목 없이 생성 시 400을 반환한다")
    void createContentWithBlankTitle() throws Exception {
        mockMvc.perform(post("/contents")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "", "description": "내용"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("INVALID_INPUT"));
    }

    @Test
    @DisplayName("콘텐츠 목록을 페이징 조회한다")
    void getContentList() throws Exception {
        createTestContent(userToken, "제목1");
        createTestContent(userToken, "제목2");

        mockMvc.perform(get("/contents")
                        .header("Authorization", "Bearer " + userToken)
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    @DisplayName("콘텐츠 상세 조회 시 조회수가 증가한다")
    void getContentDetail() throws Exception {
        Long id = createTestContentAndGetId(userToken, "조회수 테스트");

        mockMvc.perform(get("/contents/" + id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.viewCount").value(1));
    }

    @Test
    @DisplayName("본인이 작성한 콘텐츠를 수정한다")
    void updateOwnContent() throws Exception {
        Long id = createTestContentAndGetId(userToken, "원래 제목");

        mockMvc.perform(put("/contents/" + id)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "수정된 제목", "description": "수정된 내용"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("수정된 제목"));
    }

    @Test
    @DisplayName("타인의 콘텐츠 수정 시 403을 반환한다")
    void updateOtherUserContent() throws Exception {
        Long id = createTestContentAndGetId(adminToken, "관리자 콘텐츠");

        mockMvc.perform(put("/contents/" + id)
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "수정 시도", "description": "수정 시도"}
                                """))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.code").value("ACCESS_DENIED"));
    }

    @Test
    @DisplayName("ADMIN은 타인의 콘텐츠를 수정할 수 있다")
    void adminCanUpdateAnyContent() throws Exception {
        Long id = createTestContentAndGetId(userToken, "사용자 콘텐츠");

        mockMvc.perform(put("/contents/" + id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"title": "관리자가 수정", "description": "관리자가 수정"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("관리자가 수정"));
    }

    @Test
    @DisplayName("본인이 작성한 콘텐츠를 삭제한다")
    void deleteOwnContent() throws Exception {
        Long id = createTestContentAndGetId(userToken, "삭제할 콘텐츠");

        mockMvc.perform(delete("/contents/" + id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("타인의 콘텐츠 삭제 시 403을 반환한다")
    void deleteOtherUserContent() throws Exception {
        Long id = createTestContentAndGetId(adminToken, "관리자 콘텐츠");

        mockMvc.perform(delete("/contents/" + id)
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("ADMIN은 타인의 콘텐츠를 삭제할 수 있다")
    void adminCanDeleteAnyContent() throws Exception {
        Long id = createTestContentAndGetId(userToken, "사용자 콘텐츠");

        mockMvc.perform(delete("/contents/" + id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("존재하지 않는 콘텐츠 조회 시 404를 반환한다")
    void getNotFoundContent() throws Exception {
        mockMvc.perform(get("/contents/9999")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value("CONTENT_NOT_FOUND"));
    }

    private void createTestContent(String token, String title) throws Exception {
        mockMvc.perform(post("/contents")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\": \"" + title + "\", \"description\": \"내용\"}"));
    }

    private Long createTestContentAndGetId(String token, String title) throws Exception {
        MvcResult result = mockMvc.perform(post("/contents")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"title\": \"" + title + "\", \"description\": \"내용\"}"))
                .andReturn();

        String body = result.getResponse().getContentAsString();
        return Long.parseLong(body.replaceAll(".*\"id\":(\\d+).*", "$1"));
    }
}
