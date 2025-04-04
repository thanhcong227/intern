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
import viettelsoftware.intern.dto.request.PermissionRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.PermissionResponse;
import viettelsoftware.intern.service.impl.PermissionServiceImpl;

@RestController
@RequestMapping("/permission")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionController {

    PermissionServiceImpl permissionServiceImpl;

    @PostMapping()
    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    ApiResponse<PermissionResponse> create(@RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionServiceImpl.create(request))
                .build();
    }

    @PutMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    ApiResponse<PermissionResponse> update(@PathVariable String permissionId, @RequestBody PermissionRequest request) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionServiceImpl.update(permissionId, request))
                .build();
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    ApiResponse<Void> delete(@PathVariable String permissionId) {
        permissionServiceImpl.delete(permissionId);
        return ApiResponse.<Void>builder()
                .message("Permission deleted successfully")
                .build();
    }

    @GetMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    ApiResponse<PermissionResponse> getPermission(@PathVariable String permissionId) {
        return ApiResponse.<PermissionResponse>builder()
                .result(permissionServiceImpl.getPermission(permissionId))
                .build();
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    ApiResponse<Page<PermissionResponse>> getAllPermission(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<PermissionResponse>>builder()
                .result(permissionServiceImpl.getAllPermission(pageable))
                .build();
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('EXPORT_DATA')")
    public ResponseEntity<byte[]> exportPermissionsToExcel() {
        byte[] excelData = permissionServiceImpl.exportPermissionsToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=permissions.xlsx");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(excelData);
    }
}
