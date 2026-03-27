package com.malgn.content;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import com.malgn.common.exception.BusinessException;
import com.malgn.common.exception.ErrorCode;
import com.malgn.content.dto.ContentCreateRequest;
import com.malgn.content.dto.ContentResponse;
import com.malgn.content.dto.ContentUpdateRequest;

@ExtendWith(MockitoExtension.class)
class ContentServiceTest {

    @Mock
    private ContentRepository contentRepository;

    @InjectMocks
    private ContentService contentService;

    @Test
    @DisplayName("콘텐츠를 생성하면 저장된 결과를 반환한다")
    void createContent() {
        ContentCreateRequest request = new ContentCreateRequest("제목", "내용");
        Content content = new Content("제목", "내용");
        given(contentRepository.save(any(Content.class))).willReturn(content);

        ContentResponse response = contentService.create(request);

        assertThat(response.title()).isEqualTo("제목");
    }

    @Test
    @DisplayName("존재하지 않는 콘텐츠 조회 시 예외가 발생한다")
    void getDetailNotFound() {
        given(contentRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> contentService.getDetail(999L))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.CONTENT_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("작성자가 아닌 USER가 수정 시 예외가 발생한다")
    void updateByOtherUser() {
        Content content = new Content("제목", "내용");
        ReflectionTestUtils.setField(content, "createdBy", "originalUser");
        given(contentRepository.findById(1L)).willReturn(Optional.of(content));

        assertThatThrownBy(() ->
                contentService.update(1L, new ContentUpdateRequest("새 제목", "새 내용"), "otherUser", false))
                .isInstanceOf(BusinessException.class)
                .hasMessageContaining(ErrorCode.ACCESS_DENIED.getMessage());
    }

    @Test
    @DisplayName("ADMIN은 타인의 콘텐츠를 수정할 수 있다")
    void updateByAdmin() {
        Content content = new Content("제목", "내용");
        ReflectionTestUtils.setField(content, "createdBy", "originalUser");
        given(contentRepository.findById(1L)).willReturn(Optional.of(content));

        ContentResponse response = contentService.update(1L, new ContentUpdateRequest("수정됨", "수정 내용"), "admin", true);

        assertThat(response.title()).isEqualTo("수정됨");
    }
}
