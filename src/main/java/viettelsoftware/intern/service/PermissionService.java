package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.request.PermissionRequest;
import viettelsoftware.intern.dto.response.PermissionResponse;

public interface PermissionService {
    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(String permissionId, PermissionRequest request);

    void delete(String roleId);
    PermissionResponse getPermission(String roleId);
    Page<PermissionResponse> getAllPermission(Pageable pageable);
    byte[] exportPermissionsToExcel();
}
