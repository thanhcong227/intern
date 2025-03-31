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
import org.springframework.web.bind.annotation.*;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.entity.CommentEntity;
import viettelsoftware.intern.service.impl.CommentServiceImpl;

@RestController
@RequestMapping("/comment")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentController {

    CommentServiceImpl commentServiceImpl;

    @PostMapping()
    ApiResponse<CommentEntity> create(@RequestBody CommentEntity request) {
        return ApiResponse.<CommentEntity>builder()
                .result(commentServiceImpl.create(request))
                .build();
    }

    @PutMapping("/{commentId}")
    ApiResponse<CommentEntity> update(@PathVariable String commentId, @RequestBody CommentEntity request) {
        return ApiResponse.<CommentEntity>builder()
                .result(commentServiceImpl.update(commentId, request))
                .build();
    }

    @DeleteMapping("/{commentId}")
    ApiResponse<Void> delete(@PathVariable String commentId) {
        commentServiceImpl.delete(commentId);
        return ApiResponse.<Void>builder()
                .message("Comment deleted successfully")
                .build();
    }

    @GetMapping("/{commentId}")
    ApiResponse<CommentEntity> getComment(@PathVariable String commentId) {
        return ApiResponse.<CommentEntity>builder()
                .result(commentServiceImpl.getComment(commentId))
                .build();
    }

    @GetMapping
    ApiResponse<Page<CommentEntity>> getAllComments(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<CommentEntity>>builder()
                .result(commentServiceImpl.getAllComments(pageable))
                .build();
    }

    @GetMapping("/export/excel")
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
