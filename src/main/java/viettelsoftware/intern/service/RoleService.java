package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.request.RoleRequest;
import viettelsoftware.intern.dto.response.RoleResponse;

public interface RoleService {
    RoleResponse create(RoleRequest request);
    void delete(String roleId);

    RoleResponse update(String roleId, RoleRequest request);

    RoleResponse getRole(String roleId);
    Page<RoleResponse> getAllRole(Pageable pageable);
    byte[] exportRolesToExcel();
}
