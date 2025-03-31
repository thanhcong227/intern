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
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.entity.PostEntity;
import viettelsoftware.intern.service.impl.PostServiceImpl;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {

    PostServiceImpl postServiceImpl;

    @PostMapping()
    @PreAuthorize("hasAuthority('POST_MANAGE')")
    ApiResponse<PostEntity> create(@RequestBody PostEntity request) {
        return ApiResponse.<PostEntity>builder()
                .result(postServiceImpl.create(request))
                .build();
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasAnyAuthority('POST_MANAGE','POST_EDIT_OWN')")
    ApiResponse<PostEntity> update(@PathVariable String postId, @RequestBody PostEntity request) {
        return ApiResponse.<PostEntity>builder()
                .result(postServiceImpl.update(postId, request))
                .build();
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyAuthority('POST_MANAGE','POST_DELETE_OWN')")
    ApiResponse<Void> delete(@PathVariable String postId) {
        postServiceImpl.delete(postId);
        return ApiResponse.<Void>builder()
                .message("Post deleted successfully")
                .build();
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasAuthority('POST_VIEW_ALL')")
    ApiResponse<PostEntity> getPost(@PathVariable String postId) {
        return ApiResponse.<PostEntity>builder()
                .result(postServiceImpl.getPost(postId))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('POST_VIEW_ALL')")
    ApiResponse<Page<PostEntity>> getAllPosts(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<PostEntity>>builder()
                .result(postServiceImpl.getAllPosts(pageable))
                .build();
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('EXPORT_DATA')")
    public ResponseEntity<byte[]> exportPostsToExcel() {
        byte[] excelData = postServiceImpl.exportPostsToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=posts.xlsx");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(excelData);
    }
}
