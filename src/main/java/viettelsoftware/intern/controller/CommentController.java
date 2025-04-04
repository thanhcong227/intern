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
import viettelsoftware.intern.dto.request.CommentRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.CommentResponse;
import viettelsoftware.intern.entity.CommentEntity;
import viettelsoftware.intern.service.impl.CommentServiceImpl;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

    CommentServiceImpl commentServiceImpl;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    ApiResponse<CommentResponse> create(@RequestBody CommentRequest request) {
        return ApiResponse.<CommentResponse>builder()
                .result(commentServiceImpl.create(request))
                .build();
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyAuthority('COMMENT_MANAGE', 'COMMENT_EDIT_OWN')")
    ApiResponse<CommentEntity> update(@PathVariable String commentId, @RequestBody CommentEntity request) {
        return ApiResponse.<CommentEntity>builder()
                .result(commentServiceImpl.update(commentId, request))
                .build();
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyAuthority('COMMENT_MANAGE', 'COMMENT_DELETE_OWN')")
    ApiResponse<Void> delete(@PathVariable String commentId) {
        commentServiceImpl.delete(commentId);
        return ApiResponse.<Void>builder()
                .message("Comment deleted successfully")
                .build();
    }

    @GetMapping("/{commentId}")
    @PreAuthorize("hasAuthority('COMMENT_VIEW')")
    ApiResponse<CommentEntity> getComment(@PathVariable String commentId) {
        return ApiResponse.<CommentEntity>builder()
                .result(commentServiceImpl.getComment(commentId))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMMENT_VIEW')")
    ApiResponse<Page<CommentEntity>> getAllComments(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<CommentEntity>>builder()
                .result(commentServiceImpl.getAllComments(pageable))
                .build();
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
