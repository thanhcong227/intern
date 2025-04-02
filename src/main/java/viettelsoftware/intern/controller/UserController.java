package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.request.UserRequest;
import viettelsoftware.intern.dto.request.UserSearchRequest;
import viettelsoftware.intern.dto.request.UserUpdateRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.UserResponse;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.service.impl.UserServiceImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserServiceImpl userServiceImpl;

    @PostMapping()
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    ApiResponse<UserResponse> create(@RequestBody UserRequest request) {
        return ApiResponse.<UserResponse>builder()
                .result(userServiceImpl.create(request))
                .build();
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    ApiResponse<UserResponse> update(@RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder().result(userServiceImpl.update(request)).build();
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    ApiResponse<Void> delete(@PathVariable String userId) {
        userServiceImpl.delete(userId);
        return ApiResponse.<Void>builder()
                .message("User deleted successfully")
                .build();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
    ApiResponse<UserResponse> getUser(@PathVariable String userId) {
        return ApiResponse.<UserResponse>builder()
                .result(userServiceImpl.getUser(userId))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('USER_VIEW')")
    ApiResponse<Page<UserResponse>> getAllUser(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<UserResponse>>builder()
                .result(userServiceImpl.getAllUser(pageable))
                .build();
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('EXPORT_DATA')")
    public ResponseEntity<byte[]> exportUsersToExcel(@RequestParam(value = "fields", required = false) List<String> fields) {
        byte[] excelData = userServiceImpl.exportUsersToExcel(fields);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(excelData);
    }

    @PostMapping("/import")
    public ApiResponse<?> importUsersAndGetReport(@RequestParam("file") MultipartFile file) {
        try {
            byte[] reportBytes = userServiceImpl.importUsersFromExcelAndGenerateErrorReport(file);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            String originalFilename = file.getOriginalFilename();
            String reportFilename = "import_report_" + (originalFilename != null ? originalFilename : "results.xlsx");
            headers.setContentDispositionFormData("attachment", reportFilename);

            return ApiResponse.builder()
                    .result(reportBytes)
                    .build();

        } catch (AppException e) {
            log.error("Import failed: {}", e.getMessage()); // Ghi log lỗi
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errorCode", e.getErrorCode());
            errorResponse.put("message", e.getMessage());
            return ApiResponse.builder()
                    .result(errorResponse)
                    .build();

        } catch (Exception e) {
            log.error("Unexpected error during import: {}", e.getMessage(), e); // Ghi log đầy đủ stacktrace
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("errorCode", "UNEXPECTED_ERROR");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            return ApiResponse.builder()
                    .result(errorResponse)
                    .build();
        }
    }

    @PostMapping("/search")
    public ApiResponse<List<UserResponse>> searchUsers(@RequestBody UserSearchRequest searchRequest) {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userServiceImpl.searchUsers(searchRequest))
                .build();
    }
}
