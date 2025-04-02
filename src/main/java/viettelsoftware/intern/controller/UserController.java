package viettelsoftware.intern.controller;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
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
import viettelsoftware.intern.config.response.GeneralResponse;
import viettelsoftware.intern.config.response.ResponseFactory;
import viettelsoftware.intern.dto.request.UserRequest;
import viettelsoftware.intern.dto.request.UserSearchRequest;
import viettelsoftware.intern.dto.request.UserUpdateRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.UserResponse;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.service.impl.UserServiceImpl;

import java.nio.charset.StandardCharsets;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserController {

    UserServiceImpl userServiceImpl;
    ResponseFactory responseFactory;

    @PostMapping()
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    ResponseEntity<GeneralResponse<UserResponse>> create(@RequestBody UserRequest request) {
        return responseFactory.success(userServiceImpl.create(request));
    }

    @PutMapping()
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    ResponseEntity<GeneralResponse<UserResponse>> update(@RequestBody UserUpdateRequest request){
        return responseFactory.success(userServiceImpl.update(request));
    }

    @DeleteMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    ResponseEntity<GeneralResponse<Object>> delete(@PathVariable String userId) {
        userServiceImpl.delete(userId);
        return responseFactory.successNoData();
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_VIEW')")
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
        try {
            byte[] reportBytes = userServiceImpl.importUsersFromExcelAndGenerateErrorReport(file);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);

            String originalFilename = file.getOriginalFilename();
            String reportFilename = "import_report_" + (originalFilename != null ? originalFilename : "results.xlsx");
            headers.setContentDispositionFormData("attachment", reportFilename);

            return new ResponseEntity<>(reportBytes, headers, HttpStatus.OK);

        } catch (AppException e) {
            log.error("Import failed: {}", e.getMessage());
            String errorMessage = "Error Code: " + e.getErrorCode() + ", Message: " + e.getMessage();
            byte[] errorBytes = errorMessage.getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(errorBytes, headers, HttpStatus.INTERNAL_SERVER_ERROR);

        } catch (Exception e) {
            log.error("Unexpected error during import: {}", e.getMessage(), e);
            String errorMessage = "Unexpected error: " + e.getMessage();
            byte[] errorBytes = errorMessage.getBytes(StandardCharsets.UTF_8);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            return new ResponseEntity<>(errorBytes, headers, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/search")
    public ApiResponse<List<UserResponse>> searchUsers(@RequestBody UserSearchRequest searchRequest) {
        return ApiResponse.<List<UserResponse>>builder()
                .result(userServiceImpl.searchUsers(searchRequest))
                .build();
    }
}
