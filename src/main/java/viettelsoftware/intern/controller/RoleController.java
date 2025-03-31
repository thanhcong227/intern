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
import viettelsoftware.intern.dto.request.RoleRequest;
import viettelsoftware.intern.dto.response.ApiResponse;
import viettelsoftware.intern.dto.response.RoleResponse;
import viettelsoftware.intern.service.impl.RoleServiceImpl;

@RestController
@RequestMapping("/role")
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RoleController {

    RoleServiceImpl roleServiceImpl;

    @PostMapping()
    ApiResponse<RoleResponse> create(@RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleServiceImpl.create(request))
                .build();
    }

    @PutMapping("/{roleId}")
    ApiResponse<RoleResponse> update(@PathVariable String roleId, @RequestBody RoleRequest request) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleServiceImpl.update(roleId, request))
                .build();
    }

    @DeleteMapping("/{roleId}")
    ApiResponse<Void> delete(@PathVariable String roleId) {
        roleServiceImpl.delete(roleId);
        return ApiResponse.<Void>builder()
                .message("Role deleted successfully")
                .build();
    }

    @GetMapping("/{roleId}")
    ApiResponse<RoleResponse> getRole(@PathVariable String roleId) {
        return ApiResponse.<RoleResponse>builder()
                .result(roleServiceImpl.getRole(roleId))
                .build();
    }

    @GetMapping
    ApiResponse<Page<RoleResponse>> getAllRole(@PageableDefault(size = 5) Pageable pageable) {
        return ApiResponse.<Page<RoleResponse>>builder()
                .result(roleServiceImpl.getAllRole(pageable))
                .build();
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportRolesToExcel() {
        byte[] excelData = roleServiceImpl.exportRolesToExcel();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=roles.xlsx");
        return ResponseEntity
                .status(HttpStatus.OK)
                .headers(headers)
                .body(excelData);
    }
}
