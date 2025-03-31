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
import viettelsoftware.intern.dto.request.UserRequest;
import viettelsoftware.intern.dto.request.UserUpdateRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.UserResponse;
import viettelsoftware.intern.service.impl.UserServiceImpl;

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

    @PutMapping("/{userId}")
    @PreAuthorize("hasAuthority('USER_MANAGE')")
    ApiResponse<UserResponse> update(@PathVariable String userId, @RequestBody UserUpdateRequest request){
        return ApiResponse.<UserResponse>builder().result(userServiceImpl.update(userId, request)).build();
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
    public ResponseEntity<byte[]> exportUsersToExcel() {
        byte[] excelData = userServiceImpl.exportUsersToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=users.xlsx");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(excelData);
    }
}
