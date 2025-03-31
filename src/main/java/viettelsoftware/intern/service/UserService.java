package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import viettelsoftware.intern.dto.request.UserRequest;
import viettelsoftware.intern.dto.request.UserUpdateRequest;
import viettelsoftware.intern.dto.response.UserResponse;

public interface UserService {

    UserResponse create(UserRequest request);
    void delete(String userId);
    UserResponse getUser(String userId);
    Page<UserResponse> getAllUser(Pageable pageable);
    UserResponse update(String userId, UserUpdateRequest request);
    byte[] exportUsersToExcel();
}
