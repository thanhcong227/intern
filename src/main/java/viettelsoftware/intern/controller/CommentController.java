package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import viettelsoftware.intern.config.response.GeneralResponse;
import viettelsoftware.intern.config.response.ResponseFactory;
import viettelsoftware.intern.dto.request.CommentRequest;
import viettelsoftware.intern.dto.response.CommentResponse;
import viettelsoftware.intern.service.impl.CommentServiceImpl;

@RestController
@RequestMapping("/post/{postId}/comments")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

    CommentServiceImpl commentServiceImpl;
    ResponseFactory responseFactory;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GeneralResponse<CommentResponse>> create(
            @PathVariable String postId,
            @RequestBody CommentRequest request) {
        return responseFactory.success(commentServiceImpl.create(postId, request));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyAuthority('COMMENT_MANAGE', 'COMMENT_EDIT_OWN')")
    public ResponseEntity<GeneralResponse<CommentResponse>> update(
            @PathVariable String postId,
            @PathVariable String commentId,
            @RequestBody CommentRequest request) {
        return responseFactory.success(commentServiceImpl.update(commentId, request));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyAuthority('COMMENT_MANAGE', 'COMMENT_DELETE_OWN')")
    public ResponseEntity<GeneralResponse<Object>> delete(
            @PathVariable String postId,
            @PathVariable String commentId) {
        commentServiceImpl.delete(commentId);
        return responseFactory.success("Xoá comment thành công");
    }

    @GetMapping("/{commentId}")
    @PreAuthorize("hasAuthority('COMMENT_VIEW')")
    public ResponseEntity<GeneralResponse<CommentResponse>> getComment(
            @PathVariable String postId,
            @PathVariable String commentId) {
        return responseFactory.success(commentServiceImpl.getComment(commentId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMMENT_VIEW')")
    public ResponseEntity<GeneralResponse<Page<CommentResponse>>> getAllComments(
            @PathVariable String postId,
            @PageableDefault(size = 5) Pageable pageable) {
        return responseFactory.success(commentServiceImpl.getAllComments(pageable));
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('EXPORT_DATA')")
    public ResponseEntity<byte[]> exportCommentsToExcel() {
        byte[] excelData = commentServiceImpl.exportCommentsToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=comments.xlsx");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(excelData);
    }
}
