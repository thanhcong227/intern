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
    private final ResponseFactory responseFactory;

    @PostMapping()
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    ResponseEntity<GeneralResponse<RoleResponse>> create(@RequestBody RoleRequest request) {
        return responseFactory.success(roleServiceImpl.create(request));
    }

    @PutMapping("/{roleId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    ResponseEntity<GeneralResponse<RoleResponse>> update(@PathVariable String roleId, @RequestBody RoleRequest request) {
        return responseFactory.success(roleServiceImpl.update(roleId, request));
    }

    @DeleteMapping("/{roleId}")
    @PreAuthorize("hasAuthority('ROLE_MANAGE')")
    ResponseEntity<GeneralResponse<Object>> delete(@PathVariable String roleId) {
        roleServiceImpl.delete(roleId);
        return responseFactory.successNoData();
    }

    @GetMapping("/{roleId}")
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    ResponseEntity<GeneralResponse<RoleResponse>> getRole(@PathVariable String roleId) {
        return responseFactory.success(roleServiceImpl.getRole(roleId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_VIEW')")
    ResponseEntity<GeneralResponse<Page<RoleResponse>>> getAllRole(@PageableDefault(size = 5) Pageable pageable) {
        return responseFactory.success(roleServiceImpl.getAllRole(pageable));
    }

    @GetMapping("/export/excel")
    @PreAuthorize("hasAuthority('EXPORT_DATA')")
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
