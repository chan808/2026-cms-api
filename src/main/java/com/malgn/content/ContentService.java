package com.malgn.content;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.malgn.common.exception.BusinessException;
import com.malgn.common.exception.ErrorCode;
import com.malgn.content.dto.ContentCreateRequest;
import com.malgn.content.dto.ContentResponse;
import com.malgn.content.dto.ContentUpdateRequest;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class ContentService {

    private final ContentRepository contentRepository;

    @Transactional
    public ContentResponse create(ContentCreateRequest request) {
        Content content = new Content(request.title(), request.description());
        return ContentResponse.from(contentRepository.save(content));
    }

    public Page<ContentResponse> getList(Pageable pageable) {
        return contentRepository.findAll(pageable)
                .map(ContentResponse::from);
    }

    @Transactional
    public ContentResponse getDetail(Long id) {
        contentRepository.incrementViewCount(id);
        Content content = findById(id);
        return ContentResponse.from(content);
    }

    @Transactional
    public ContentResponse update(Long id, ContentUpdateRequest request, String username, boolean isAdmin) {
        Content content = findById(id);
        validateAccess(content, username, isAdmin);
        content.update(request.title(), request.description());
        return ContentResponse.from(content);
    }

    @Transactional
    public void delete(Long id, String username, boolean isAdmin) {
        Content content = findById(id);
        validateAccess(content, username, isAdmin);
        contentRepository.delete(content);
    }

    private Content findById(Long id) {
        return contentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));
    }

    private void validateAccess(Content content, String username, boolean isAdmin) {
        if (!isAdmin && !content.getCreatedBy().equals(username)) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }
    }
}
