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
    private final ResponseFactory responseFactory;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    ResponseEntity<GeneralResponse<CommentResponse>> create(@RequestBody CommentRequest request) {
        return responseFactory.success(commentServiceImpl.create(request));
    }

    @PutMapping("/{commentId}")
    @PreAuthorize("hasAnyAuthority('COMMENT_MANAGE', 'COMMENT_EDIT_OWN')")
    ResponseEntity<GeneralResponse<CommentResponse>> update(@PathVariable String commentId, @RequestBody CommentRequest request) {
        return responseFactory.success(commentServiceImpl.create(request));
    }

    @DeleteMapping("/{commentId}")
    @PreAuthorize("hasAnyAuthority('COMMENT_MANAGE', 'COMMENT_DELETE_OWN')")
    ResponseEntity<GeneralResponse<Object>> delete(@PathVariable String commentId) {
        commentServiceImpl.delete(commentId);
        return responseFactory.success(commentServiceImpl);
    }

    @GetMapping("/{commentId}")
    @PreAuthorize("hasAuthority('COMMENT_VIEW')")
    ResponseEntity<GeneralResponse<CommentResponse>> getComment(@PathVariable String commentId) {
        return responseFactory.success(commentServiceImpl.getComment(commentId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('COMMENT_VIEW')")
    ResponseEntity<GeneralResponse<Page<CommentResponse>>> getAllComments(@PageableDefault(size = 5) Pageable pageable) {
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
