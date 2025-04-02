package viettelsoftware.intern.service.impl;

import jakarta.persistence.EntityNotFoundException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.request.UserRequest;
import viettelsoftware.intern.dto.request.UserSearchRequest;
import viettelsoftware.intern.dto.request.UserUpdateRequest;
import viettelsoftware.intern.dto.response.UserResponse;
import viettelsoftware.intern.entity.RoleEntity;
import viettelsoftware.intern.entity.UserEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.repository.RoleRepository;
import viettelsoftware.intern.repository.UserRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import viettelsoftware.intern.service.UserService;
import viettelsoftware.intern.util.ConversionUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class UserServiceImpl implements UserService {

    UserRepository userRepository;
    PasswordEncoder passwordEncoder;
    RoleRepository roleRepository;
    ModelMapper modelMapper;

    @Override
    public UserResponse create(UserRequest request) {
        if (userRepository.existsByUsername(request.getUsername()))
            throw new AppException(ErrorCode.USER_EXISTED);

        UserEntity userEntity = ConversionUtil.convertObject(request, x -> modelMapper.map(x, UserEntity.class));
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        Set<RoleEntity> roles = request.getRoles().stream()
                .map(role -> roleRepository.findByName(role.getName())
                        .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)))
                .collect(Collectors.toSet());

        userEntity.setRoles(roles);
        UserEntity response = userRepository.save(userEntity);
        return ConversionUtil.convertObject(response, x -> modelMapper.map(x, UserResponse.class));
    }


    @Override
    public UserResponse update(UserUpdateRequest request) {
        UserEntity userEntity = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getUsername() != null) {
            if (!userEntity.getUsername().equals(request.getUsername()) && userRepository.existsByUsername(request.getUsername())) {
                throw new AppException(ErrorCode.USER_EXISTED);
            }
            userEntity.setUsername(request.getUsername());
        }
        if (request.getFullName() != null) {
            userEntity.setFullName(request.getFullName());
        }
        if (request.getEmail() != null) {
            userEntity.setEmail(request.getEmail());
        }
        if (request.getPhone() != null) {
            userEntity.setPhone(request.getPhone());
        }
        if (request.getAddress() != null) {
            userEntity.setAddress(request.getAddress());
        }

        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<RoleEntity> roles = request.getRoles().stream()
                    .map(role -> roleRepository.findByName(role.getName())
                            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND)))
                    .collect(Collectors.toSet());
            userEntity.setRoles(roles);
        }

        // Lưu và trả về kết quả
        return ConversionUtil.convertObject(userRepository.save(userEntity),
                x -> modelMapper.map(x, UserResponse.class));
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
        return ConversionUtil.convertObject(userEntity, x -> modelMapper.map(x, UserResponse.class));
    }

    @Override
    public Page<UserResponse> getAllUser(Pageable pageable) {
        Page<UserEntity> page = userRepository.findAll(pageable);
        return ConversionUtil.convertPage(page, x -> modelMapper.map(x, UserResponse.class));
    }

    @Override
    public byte[] exportUsersToExcel(List<String> selectedFields) {
        try {
            List<UserEntity> userEntities = userRepository.findAll();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Users");

            // Nếu không có trường nào được chọn, xuất toàn bộ mặc định
            List<String> columns = (selectedFields == null || selectedFields.isEmpty()) ?
                    Arrays.asList("userId", "username", "fullName", "email", "phone", "address") : selectedFields;

            // Header row
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < columns.size(); i++) {
                headerRow.createCell(i).setCellValue(columns.get(i));
            }

            // Dữ liệu
            int rowIdx = 1;
            for (UserEntity userEntity : userEntities) {
                Row row = sheet.createRow(rowIdx++);
                for (int i = 0; i < columns.size(); i++) {
                    Object value = getUserFieldValue(userEntity, columns.get(i));
                    if (value != null) {
                        row.createCell(i).setCellValue(value.toString());
                    }
                }
            }

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            workbook.close();

            return outputStream.toByteArray();
        } catch (IOException e) {
            return new byte[0];
        }
    }

    // Hàm lấy giá trị thuộc tính theo tên
    private Object getUserFieldValue(UserEntity user, String fieldName) {
        try {
            Field field = UserEntity.class.getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(user);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    public byte[] importUsersFromExcelAndGenerateErrorReport(MultipartFile file) {
        validateFile(file); // Kiểm tra file trước

        Workbook workbook = null;
        InputStream inputStream = null;

        try {
            inputStream = file.getInputStream();
            String filename = file.getOriginalFilename();

            // Xác định loại workbook dựa trên đuôi file
            if (filename != null && filename.toLowerCase().endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(inputStream);
            } else if (filename != null && filename.toLowerCase().endsWith(".xls")) {
                workbook = new HSSFWorkbook(inputStream);
            } else {
                // Trường hợp này không nên xảy ra nếu validateFile chạy đúng
                throw new AppException(ErrorCode.INVALID_FILE_FORMAT);
            }

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            int errorColumnIndex = -1; // Index cột lỗi, sẽ xác định từ header

            // Xử lý header và xác định/thêm cột lỗi
            if (rows.hasNext()) {
                Row headerRow = rows.next();
                errorColumnIndex = headerRow.getLastCellNum(); // Mặc định là cột cuối cùng + 1
                // Tùy chọn: Tìm xem có cột "Error" chưa, nếu chưa thì tạo mới
                Cell errorHeaderCell = headerRow.createCell(errorColumnIndex);
                errorHeaderCell.setCellValue("Import Errors"); // Đặt tên cho cột lỗi
            }

            if (errorColumnIndex == -1) {
                throw new AppException(ErrorCode.FILE_PROCESSING_ERROR);
            }


             List<UserEntity> validUsersToSave = new ArrayList<>();

            // Duyệt từng hàng dữ liệu
            while (rows.hasNext()) {
                Row row = rows.next();
                // Bỏ qua các hàng trống hoàn toàn (ví dụ)
                if (isRowEmpty(row, errorColumnIndex)) {
                    continue;
                }

                StringBuilder errorMessage = new StringBuilder();
                boolean isValid = true;

                String username = getCellStringValue(row.getCell(1));
                String fullName = getCellStringValue(row.getCell(2)); // Có thể cho phép null/empty
                String email = getCellStringValue(row.getCell(3));
                String phone = getCellStringValue(row.getCell(4));
                String address = getCellStringValue(row.getCell(5));

                // Kiểm tra tính hợp lệ của dữ liệu
                if (username == null || username.trim().isEmpty()) {
                    errorMessage.append("Username is required. ");
                    isValid = false;
                } else if (userRepository.existsByUsername(username)) { // Kiểm tra tồn tại username
                    errorMessage.append("Username already exists. ");
                    isValid = false;
                }

                if (email == null || email.trim().isEmpty()) {
                    errorMessage.append("Email is required. ");
                    isValid = false;
                } else if (!isValidEmailFormat(email)) { // Thêm kiểm tra định dạng email
                    errorMessage.append("Invalid email format. ");
                    isValid = false;
                } else if (userRepository.existsByEmail(email)) { // Kiểm tra tồn tại email
                    errorMessage.append("Email already exists. ");
                    isValid = false;
                }

                if (phone == null || phone.trim().isEmpty()) {
                    errorMessage.append("Phone is required. ");
                    isValid = false;
                }
                if (address == null || address.trim().isEmpty()) {
                    errorMessage.append("Address is required. ");
                    isValid = false;
                }

                // --- Xử lý kết quả ---
                Cell errorCell = row.getCell(errorColumnIndex);
                if (errorCell == null) {
                    errorCell = row.createCell(errorColumnIndex);
                }

                if (!isValid) {
                    // Ghi lỗi vào cột lỗi
                    errorCell.setCellValue(errorMessage.toString().trim());
                    log.warn("Validation failed for row {}: {}", row.getRowNum() + 1, errorMessage);
                } else {
                    // Xóa thông báo lỗi cũ (nếu có) và xử lý user hợp lệ
                    errorCell.setCellValue(""); // Xóa lỗi nếu hàng này đã hợp lệ
                    UserEntity user = new UserEntity();
                    user.setUsername(username);
                    user.setFullName(fullName); // Đảm bảo xử lý null nếu cần
                    user.setEmail(email);
                    user.setPhone(phone);
                    user.setAddress(address);
                    user.setPassword(passwordEncoder.encode("123456")); // Mật khẩu mặc định

                    Set<RoleEntity> roles = new HashSet<>();
                    RoleEntity userRole = roleRepository.findByName("MEMBER")
                            .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
                    roles.add(userRole);
                    user.setRoles(roles);
                    try {
                        userRepository.save(user);
                        log.info("Successfully imported user: {}", username);
                    } catch (Exception ex) { // Bắt lỗi khi lưu (ví dụ: constraint violation dù đã check)
                        log.error("Error saving valid user {} from row {}: {}", username, row.getRowNum() + 1, ex.getMessage());
                        errorCell.setCellValue("Error saving user to database: " + ex.getMessage());
                    }
                     validUsersToSave.add(user);
                }
            }

            // Lưu tất cả user hợp lệ vào DB
            if (!validUsersToSave.isEmpty()) {
                try {
                    userRepository.saveAll(validUsersToSave);
                    log.info("Saved {} valid users to the database.", validUsersToSave.size());
                } catch (Exception e) {
                    log.error("Error bulk saving users: {}", e.getMessage());
                    throw new AppException(ErrorCode.DATABASE_ERROR);
                }
            }

            // Ghi workbook đã sửa đổi (có cột lỗi) vào byte array
            try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
                workbook.write(outputStream);
                return outputStream.toByteArray(); // Trả về dữ liệu file Excel dưới dạng byte array
            }

        } catch (IOException e) {
            log.error("Error processing Excel file: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.FILE_PROCESSING_ERROR);
        } catch (AppException e) { // Bắt lại AppException để log và re-throw
            log.error("Application error during import: {} - {}", e.getErrorCode().getMessages(), e.getMessage(), e);
            throw e; // Re-throw AppException
        } catch (Exception e) { // Bắt các lỗi không mong muốn khác
            log.error("Unexpected error during Excel import: {}", e.getMessage(), e);
            throw new AppException(ErrorCode.UNKNOWN);
        } finally {
            // Đóng workbook và input stream
            if (workbook != null) {
                try {
                    workbook.close();
                } catch (IOException e) {
                    log.error("Error closing workbook: {}", e.getMessage(), e);
                }
            }
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    log.error("Error closing input stream: {}", e.getMessage(), e);
                }
            }
        }
    }

    // Hàm kiểm tra file hợp lệ (giữ nguyên hoặc cải thiện)
    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new AppException(ErrorCode.INVALID_FILE);
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new AppException(ErrorCode.INVALID_FILE);
        }

        String fileExtension = FilenameUtils.getExtension(filename.toLowerCase()); // Convert to lower case
        if (!"xlsx".equals(fileExtension) && !"xls".equals(fileExtension)) {
            throw new AppException(ErrorCode.INVALID_FILE_FORMAT);
        }
        // Optional: Check file size
        // long maxSize = 5 * 1024 * 1024; // 5MB example
        // if (file.getSize() > maxSize) {
        //    throw new AppException(ErrorCode.FILE_TOO_LARGE, "File size exceeds the limit.");
        // }
    }

    // Hàm kiểm tra hàng trống (ví dụ)
    private boolean isRowEmpty(Row row, int lastDataColumnIndex) {
        if (row == null) {
            return true;
        }
        for (int c = 0; c < lastDataColumnIndex; c++) { // Chỉ kiểm tra các cột dữ liệu
            Cell cell = row.getCell(c, Row.MissingCellPolicy.RETURN_BLANK_AS_NULL);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellStringValue(cell);
                if(value != null) return false; // Nếu có bất kỳ cell nào có giá trị thì không trống
            }
        }
        return true; // Tất cả cell đều trống
    }

    // Hàm kiểm tra định dạng email (ví dụ đơn giản)
    private boolean isValidEmailFormat(String email) {
        if (email == null) return false;
        // Một regex đơn giản, có thể cần phức tạp hơn cho các trường hợp đặc biệt
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email.matches(emailRegex);
    }

        private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue();
            case NUMERIC:
                return String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            default:
                return "";
        }
    }

    private boolean isValidUser(UserEntity user) {
        return user.getUsername() != null && !user.getUsername().isEmpty() &&
                user.getEmail() != null && !user.getEmail().isEmpty();
    }

    @Override
    public List<UserResponse> searchUsers(UserSearchRequest searchRequest) {
        List<UserEntity> users = userRepository.searchUsers(searchRequest.getUsername(),
                searchRequest.getEmail(), searchRequest.getFullName(),
                searchRequest.getPhone(), searchRequest.getAddress());
        if (users.isEmpty()) {
            throw new AppException(ErrorCode.USER_NOT_FOUND);
        }
        return ConversionUtil.convertList(users, x -> modelMapper.map(x, UserResponse.class));
    }
}
