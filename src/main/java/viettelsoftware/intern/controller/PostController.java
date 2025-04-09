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
import viettelsoftware.intern.dto.request.PostRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.PostResponse;
import viettelsoftware.intern.entity.PostEntity;
import viettelsoftware.intern.service.impl.PostServiceImpl;

@RestController
@RequestMapping("/post")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostController {

    PostServiceImpl postServiceImpl;
    private final ResponseFactory responseFactory;

    @PostMapping()
    @PreAuthorize("hasAuthority('POST_MANAGE')")
    ResponseEntity<GeneralResponse<PostResponse>> create(@RequestBody PostRequest request) {
        return responseFactory.success(postServiceImpl.create(request));
    }

    @PutMapping("/{postId}")
    @PreAuthorize("hasAnyAuthority('POST_MANAGE','POST_EDIT_OWN')")
    ResponseEntity<GeneralResponse<PostResponse>> update(@PathVariable String postId, @RequestBody PostRequest request) {
        return responseFactory.success(postServiceImpl.update(postId, request));
    }

    @DeleteMapping("/{postId}")
    @PreAuthorize("hasAnyAuthority('POST_MANAGE','POST_DELETE_OWN')")
    ResponseEntity<GeneralResponse<Object>> delete(@PathVariable String postId) {
        postServiceImpl.delete(postId);
        return responseFactory.successNoData();
    }

    @GetMapping("/{postId}")
    @PreAuthorize("hasAuthority('POST_VIEW_ALL')")
    ResponseEntity<GeneralResponse<PostResponse>> getPost(@PathVariable String postId) {
        return responseFactory.success(postServiceImpl.getPost(postId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('POST_VIEW_ALL')")
    ResponseEntity<GeneralResponse<Page<PostResponse>>> getAllPosts(@PageableDefault(size = 5) Pageable pageable) {
        return responseFactory.success(postServiceImpl.getAllPosts(pageable));
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
