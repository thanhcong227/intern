package viettelsoftware.intern.service.impl;

import io.micrometer.common.util.StringUtils;
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
import viettelsoftware.intern.constant.AppConstant;
import viettelsoftware.intern.constant.ResponseStatusCodeEnum;
import viettelsoftware.intern.dto.request.UserRequest;
import viettelsoftware.intern.dto.request.UserSearchRequest;
import viettelsoftware.intern.dto.request.UserUpdateRequest;
import viettelsoftware.intern.dto.response.UserResponse;
import viettelsoftware.intern.entity.RoleEntity;
import viettelsoftware.intern.entity.UserEntity;
import viettelsoftware.intern.exception.CustomException;
import viettelsoftware.intern.repository.RoleRepository;
import viettelsoftware.intern.repository.UserRepository;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import viettelsoftware.intern.service.UserService;
import viettelsoftware.intern.util.ConversionUtil;
import viettelsoftware.intern.util.FileUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
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
            throw new CustomException(ResponseStatusCodeEnum.USER_EXISTED.getCode());
        UserEntity userEntity = ConversionUtil.convertObject(request, x -> modelMapper.map(x, UserEntity.class));
        userEntity.setPassword(passwordEncoder.encode(request.getPassword()));
        if (request.getRoles() == null || request.getRoles().isEmpty()) {
            RoleEntity role = roleRepository.findByName("MEMBER")
                    .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.ROLE_NOT_FOUND.getCode()));
            userEntity.setRoles(Set.of(role));
        } else {
            Set<RoleEntity> roles = request.getRoles().stream()
                    .map(role -> roleRepository.findByName(role.getName())
                            .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.ROLE_NOT_FOUND.getCode())))
                    .collect(Collectors.toSet());
            userEntity.setRoles(roles);
        }
        UserEntity response = userRepository.save(userEntity);
        return ConversionUtil.convertObject(response, x -> modelMapper.map(x, UserResponse.class));
    }


    @Override
    public UserResponse update(String userId, UserUpdateRequest request) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND.getCode()));

        // Update username nếu khác và hợp lệ
        Optional.ofNullable(request.getUsername())
                .filter(username -> !username.equals(userEntity.getUsername()))
                .ifPresent(username -> {
                    if (userRepository.existsByUsername(username)) {
                        throw new CustomException(ResponseStatusCodeEnum.USER_EXISTED.getCode());
                    }
                    userEntity.setUsername(username);
                });

        // Update các trường còn lại nếu có
        Optional.ofNullable(request.getFullName()).ifPresent(userEntity::setFullName);
        Optional.ofNullable(request.getEmail()).ifPresent(userEntity::setEmail);
        Optional.ofNullable(request.getPhone()).ifPresent(userEntity::setPhone);
        Optional.ofNullable(request.getAddress()).ifPresent(userEntity::setAddress);

        // Update roles nếu có
        if (request.getRoles() != null && !request.getRoles().isEmpty()) {
            Set<RoleEntity> roles = request.getRoles().stream()
                    .map(role -> roleRepository.findByName(role.getName())
                            .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.ROLE_NOT_FOUND.getCode())))
                    .collect(Collectors.toSet());
            userEntity.setRoles(roles);
        }

        // Save và convert kết quả
        UserEntity updated = userRepository.save(userEntity);
        return modelMapper.map(updated, UserResponse.class);
    }

    @Override
    public void delete(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND.getCode());
        }
        userRepository.deleteById(userId);
    }

    @Override
    public UserResponse getUser(String userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(
                () -> new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND.getCode()));
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
            return field.get(user);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    public byte[] importUsersFromExcelAndGenerateErrorReport(MultipartFile file) {
        validateFile(file);

        List<Object[]> errorData = new ArrayList<>();
        List<UserEntity> validUsersToSave = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream()) {
            Workbook workbook = Objects.requireNonNull(file.getOriginalFilename()).toLowerCase().endsWith("xlsx") ?
                    new XSSFWorkbook(inputStream) : new HSSFWorkbook(inputStream);

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();

            // Skip header
            if (rows.hasNext()) rows.next();

            Set<String> existingUsernames = new HashSet<>(userRepository.findAllUsernames());
            Set<String> existingEmails = new HashSet<>(userRepository.findAllEmails());

            Set<String> seenUsernames = new HashSet<>();
            Set<String> seenEmails = new HashSet<>();

            RoleEntity userRole = roleRepository.findByName("USER")
                    .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.ROLE_NOT_FOUND.getCode()));
            String password = passwordEncoder.encode("123456");

            while (rows.hasNext()) {
                Row row = rows.next();
                Object[] rowData = new Object[6];
                StringBuilder errorMsg = new StringBuilder();
                boolean isValid = true;

                String username = getCellStringValue(row.getCell(1));
                String fullName = getCellStringValue(row.getCell(2));
                String email = getCellStringValue(row.getCell(3));
                String phone = getCellStringValue(row.getCell(4));
                String address = getCellStringValue(row.getCell(5));

                // Set rowData
                rowData[0] = username;
                rowData[1] = fullName;
                rowData[2] = email;
                rowData[3] = phone;
                rowData[4] = address;

                if(!seenUsernames.add(username)) {
                    errorMsg.append("Duplicate username: ").append(username).append(". ");
                    isValid = false;
                }

                if(!seenEmails.add(email)) {
                    errorMsg.append("Duplicate email: ").append(email).append(". ");
                    isValid = false;
                }

                if (StringUtils.isBlank(username)) {
                    errorMsg.append("Username is required. ");
                    isValid = false;
                } else if (existingUsernames.contains(username)) {
                    errorMsg.append("Username already exists. ");
                    isValid = false;
                }

                if (StringUtils.isBlank(email)) {
                    errorMsg.append("Email is required. ");
                    isValid = false;
                } else if (!email.matches(AppConstant.REGEX_EMAIL)) {
                    errorMsg.append("Invalid email format. ");
                    isValid = false;
                } else if (existingEmails.contains(email)) {
                    errorMsg.append("Email already exists. ");
                    isValid = false;
                }

                if (StringUtils.isBlank(phone)) {
                    errorMsg.append("Phone is required. ");
                    isValid = false;
                }

                if (StringUtils.isBlank(address)) {
                    errorMsg.append("Address is required. ");
                    isValid = false;
                }

                if (!isValid) {
                    rowData[5] = errorMsg.toString().trim();
                    errorData.add(rowData);
                    continue;
                }

                try {
                    UserEntity user = new UserEntity();
                    user.setUsername(username);
                    user.setFullName(fullName);
                    user.setEmail(email);
                    user.setPhone(phone);
                    user.setAddress(address);
                    user.setPassword(password); // Default password
                    user.setRoles(Set.of(userRole));

                    validUsersToSave.add(user);
                } catch (Exception e) {
                    errorMsg.append("Error creating user: ").append(e.getMessage());
                    rowData[5] = errorMsg.toString().trim();
                }
            }

            if (!validUsersToSave.isEmpty()) {
                try {
                    userRepository.saveAll(validUsersToSave);
                } catch (Exception e) {
                    throw new CustomException(ResponseStatusCodeEnum.DATABASE_ERROR.getCode());
                }
            }

            if (!errorData.isEmpty()) {
                return generateErrorReportExcel(errorData);
            } else {
                log.info("All users imported successfully.");
            }

            return new byte[0];

        } catch (IOException e) {
            throw new CustomException(ResponseStatusCodeEnum.FILE_PROCESSING_ERROR.getCode());
        }
    }

    private byte[] generateErrorReportExcel(List<Object[]> errorData) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Error Report");

            // Tạo tiêu đề
            Row headerRow = sheet.createRow(0);
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Tạo các cột tiêu đề
            String[] columns = {"Username", "Họ Tên", "Email", "Số Điện Thoại", "Địa Chỉ", "Lỗi"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }

            // Điền dữ liệu lỗi
            int rowNum = 1;
            for (Object[] rowData : errorData) {
                Row row = sheet.createRow(rowNum++);
                for (int i = 0; i < rowData.length; i++) {
                    Cell cell = row.createCell(i);
                    if (rowData[i] != null) {
                        cell.setCellValue(rowData[i].toString());
                    } else {
                        cell.setCellValue("");
                    }
                }
            }

            // Tự động điều chỉnh độ rộng cột
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }

            // Chuyển workbook thành mảng byte
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            workbook.write(outputStream);
            return outputStream.toByteArray();

        } catch (IOException e) {
            throw new CustomException(ResponseStatusCodeEnum.FILE_PROCESSING_ERROR.getCode());
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ResponseStatusCodeEnum.INVALID_FILE.getCode());
        }

        String filename = file.getOriginalFilename();
        if (filename == null) {
            throw new CustomException(ResponseStatusCodeEnum.INVALID_FILE.getCode());
        }

        String fileExtension = FilenameUtils.getExtension(filename.toLowerCase()); // Convert to lower case
        if (!"xlsx".equals(fileExtension) && !"xls".equals(fileExtension)) {
            throw new CustomException(ResponseStatusCodeEnum.INVALID_FILE_FORMAT.getCode());
        }
    }
    private String getCellStringValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((int) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }

    @Override
    public Page<UserResponse> searchUsers(UserSearchRequest searchRequest, Pageable pageable) {
        Page<UserEntity> users = userRepository.searchUsers(searchRequest.getUserId(), searchRequest.getUsername(),
                searchRequest.getEmail(), searchRequest.getFullName(),
                searchRequest.getPhone(), searchRequest.getAddress(), pageable);
        if (users.isEmpty()) {
            throw new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND.getCode());
        }
        return ConversionUtil.convertPage(users, x -> modelMapper.map(x, UserResponse.class));
    }

    public byte[] exportUsersToExcel() throws IOException {
        List<UserEntity> users = userRepository.findAll();

        List<UserResponse> userDTOs = users.stream()
                .map(user -> new UserResponse(user.getUserId(), user.getUsername(), user.getFullName(), user.getEmail(), user.getPhone(), user.getAddress(), user.getRoles().stream().map(RoleEntity::getName).collect(Collectors.toSet())))
                .toList();

//        Map<String, Object> params = new HashMap<>();
//        params.put("reportTitle", "Danh sách Người dùng");
//        params.put("generatedDate", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));

        String templatePath = "templates/excel/TemplateExcel.xlsx";
        String outputPath = "tmp/excel-exports/users_export.xlsx";

        FileUtil.writeToFileExcel(userDTOs, null, templatePath, outputPath);

        return Files.readAllBytes(Paths.get(outputPath));
    }
}
