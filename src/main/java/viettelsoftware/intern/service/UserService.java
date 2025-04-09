package viettelsoftware.intern.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;
import viettelsoftware.intern.dto.request.UserRequest;
import viettelsoftware.intern.dto.request.UserSearchRequest;
import viettelsoftware.intern.dto.request.UserUpdateRequest;
import viettelsoftware.intern.dto.response.UserResponse;
import viettelsoftware.intern.entity.UserEntity;

import java.io.IOException;
import java.util.List;

public interface UserService {

    UserResponse create(UserRequest request);

    UserResponse update(String userId, UserUpdateRequest request);

    void delete(String userId);
    UserResponse getUser(String userId);
    Page<UserResponse> getAllUser(Pageable pageable);
    byte[] exportUsersToExcel(List<String> selectedFields);

//    List<UserResponse> importUsersFromExcel(MultipartFile file) throws IOException;

    List<UserResponse> searchUsers(UserSearchRequest searchRequest);

    byte[] importUsersFromExcelAndGenerateErrorReport(MultipartFile file) throws IOException;
}
