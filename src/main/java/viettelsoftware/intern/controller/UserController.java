package viettelsoftware.intern.controller;

import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import viettelsoftware.intern.config.response.GeneralResponse;
import viettelsoftware.intern.config.response.ResponseFactory;
import viettelsoftware.intern.dto.request.UserRequest;
import viettelsoftware.intern.dto.request.UserSearchRequest;
import viettelsoftware.intern.dto.request.UserUpdateRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.UserResponse;
import viettelsoftware.intern.service.impl.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserServiceImpl userServiceImpl;
    ResponseFactory responseFactory;

    @PostMapping("/register")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    ResponseEntity<GeneralResponse<UserResponse>> create(@RequestBody @Valid UserRequest request) {
        return responseFactory.success(userServiceImpl.create(request));
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    ResponseEntity<GeneralResponse<UserResponse>> update(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return responseFactory.success(userServiceImpl.update(userId, request));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    ResponseEntity<GeneralResponse<Object>> delete(@PathVariable String userId) {
        userServiceImpl.delete(userId);
        return responseFactory.successNoData();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_VIEW') or #userId == authentication.principal.getUserId()")
    ResponseEntity<GeneralResponse<UserResponse>> getUser(@PathVariable String userId) {
        return responseFactory.success(userServiceImpl.getUser(userId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    ResponseEntity<GeneralResponse<Page<UserResponse>>> getAllUser(@PageableDefault(size = 5) Pageable pageable) {
        return responseFactory.success(userServiceImpl.getAllUser(pageable));
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('EXPORT_DATA')")
    public ResponseEntity<GeneralResponse<byte[]>> exportUsersToExcel(@RequestParam(value = "fields", required = false) List<String> fields) {
        byte[] excelData = userServiceImpl.exportUsersToExcel(fields);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");
        return responseFactory.successWithHeader(headers, excelData);
    }

    @PostMapping("/import")
    @PreAuthorize("hasAuthority('USER_IMPORT')")
    public ResponseEntity<byte[]> importUsersAndGetReport(@RequestParam("file") MultipartFile file) {
        byte[] reportData = userServiceImpl.importUsersFromExcelAndGenerateErrorReport(file);
        if (reportData.length == 0) {
            return ResponseEntity.ok("".getBytes(StandardCharsets.UTF_8));
        } else {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "user-import-errors.xlsx");
            return ResponseEntity.ok().headers(headers).body(reportData);
        }
    }

    @PostMapping("/search")
    public ApiResponse<Page<UserResponse>> searchUsers(@RequestBody UserSearchRequest searchRequest, Pageable pageable) {
        return ApiResponse.<Page<UserResponse>>builder()
                .result(userServiceImpl.searchUsers(searchRequest, pageable))
                .build();
    }
}
