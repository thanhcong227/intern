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
    private final ResponseFactory responseFactory;

    @PostMapping()
    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    ResponseEntity<GeneralResponse<PermissionResponse>> create(@RequestBody PermissionRequest request) {
        return responseFactory.success(permissionServiceImpl.create(request));
    }

    @PutMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    ResponseEntity<GeneralResponse<PermissionResponse>> update(@PathVariable String permissionId, @RequestBody PermissionRequest request) {
        return responseFactory.success(permissionServiceImpl.update(permissionId, request));
    }

    @DeleteMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_MANAGE')")
    ResponseEntity<GeneralResponse<Object>> delete(@PathVariable String permissionId) {
        permissionServiceImpl.delete(permissionId);
        return responseFactory.successNoData();
    }

    @GetMapping("/{permissionId}")
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    ResponseEntity<GeneralResponse<PermissionResponse>> getPermission(@PathVariable String permissionId) {
        return responseFactory.success(permissionServiceImpl.getPermission(permissionId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('PERMISSION_VIEW')")
    ResponseEntity<GeneralResponse<Page<PermissionResponse>>> getAllPermission(@PageableDefault(size = 5) Pageable pageable) {
        return responseFactory.success(permissionServiceImpl.getAllPermission(pageable));
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
