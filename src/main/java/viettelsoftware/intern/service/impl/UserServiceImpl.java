package viettelsoftware.intern.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.request.UserRequest;
import viettelsoftware.intern.dto.request.UserUpdateRequest;
import viettelsoftware.intern.dto.response.UserResponse;
import viettelsoftware.intern.entity.RoleEntity;
import viettelsoftware.intern.entity.UserEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.mapper.UserMapper;
import viettelsoftware.intern.repository.RoleRepository;
import viettelsoftware.intern.repository.UserRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import viettelsoftware.intern.service.UserService;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        UserEntity userEntity = userMapper.toUser(request);
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<RoleEntity> roles = request.getRoles().stream()
                .map(role -> roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)))
                .collect(Collectors.toSet());

        userEntity.setRoles(roles);
        UserEntity response = userRepository.save(userEntity);
        return userMapper.toUserResponse(response);
    }

    @Override
    public UserResponse update(String userId, UserUpdateRequest request) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        userMapper.updateUser(userEntity, request);

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<RoleEntity> roles = request.getRoles().stream()
                    .map(role -> roleRepository.findByName(role.getName())
                            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)))
                    .collect(Collectors.toSet());
            userEntity.setRoles(roles);
        }

        return userMapper.toUserResponse(userRepository.save(userEntity));
    }


    @Override
    public void delete(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new EntityNotFoundException(ErrorCode.USER_NOT_FOUND.getMessages());
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponse getUser(String userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        return userMapper.toUserResponse(userEntity);
    }

    @Override
    public Page<UserResponse> getAllUser(Pageable pageable) {
        Page<UserEntity> page = userRepository.findAll(pageable);
        return page.map(userMapper::toUserResponse);
    }

    @Override
    public byte[] exportUsersToExcel(){
        try {
            List<UserEntity> userEntities = userRepository.findAll();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Users");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Username", "Full Name", "Email", "Phone", "Address"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (UserEntity userEntity : userEntities) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(userEntity.getUserId());
                row.createCell(1).setCellValue(userEntity.getUsername());
                row.createCell(2).setCellValue(userEntity.getFullName());
                row.createCell(3).setCellValue(userEntity.getEmail());
                row.createCell(4).setCellValue(userEntity.getPhone());
                row.createCell(5).setCellValue(userEntity.getAddress());
            }

            String filePath = "users.xlsx";
            try (FileOutputStream fileOut = new FileOutputStream(filePath)) {
                workbook.write(fileOut);
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
