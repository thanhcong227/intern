package viettelsoftware.intern.service.impl;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.constant.ResponseStatusCodeEnum;
import viettelsoftware.intern.dto.request.PermissionRequest;
import viettelsoftware.intern.dto.response.PermissionResponse;
import viettelsoftware.intern.entity.PermissionEntity;
import viettelsoftware.intern.entity.RoleEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.exception.CustomException;
import viettelsoftware.intern.mapper.PermissionMapper;
import viettelsoftware.intern.repository.PermissionRepository;
import viettelsoftware.intern.repository.RoleRepository;
import viettelsoftware.intern.service.PermissionService;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PermissionServiceImpl implements PermissionService {

    PermissionRepository permissionRepository;
    RoleRepository roleRepository;
    PermissionMapper permissionMapper;

    @Override
    public PermissionResponse create(PermissionRequest request) {
        if (permissionRepository.existsByName(request.getName()))
            throw new CustomException(ResponseStatusCodeEnum.PERMISSION_EXISTED);
        Set<RoleEntity> roles = request.getRoles();
        if (Optional.ofNullable(roles).isPresent()) {
            roles.stream()
                    .map(role -> roleRepository.findByName(role.getName())
                            .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.ROLE_NOT_FOUND)))
                    .collect(Collectors.toSet());
        } else {
            roles = Set.of();
        }
        PermissionEntity permissionEntity = PermissionEntity.builder()
                .name(request.getName())
                .roles(roles)
                .createdAt(LocalDate.now())
                .build();
        permissionEntity = permissionRepository.save(permissionEntity);
        return permissionMapper.toPermissionResponse(permissionEntity);
    }

    @Override
    public PermissionResponse update(String permissionId, PermissionRequest request) {
        PermissionEntity existingPermission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.PERMISSION_NOT_FOUND));

        if (!existingPermission.getName().equals(request.getName()) && permissionRepository.existsByName(request.getName())) {
            throw new CustomException(ResponseStatusCodeEnum.PERMISSION_EXISTED);
        }

        Set<RoleEntity> roles = request.getRoles().stream()
                .map(roleRequest -> roleRepository.findByName(roleRequest.getName())
                        .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.ROLE_NOT_FOUND)))
                .collect(Collectors.toSet());

        existingPermission.setName(request.getName());
        existingPermission.setRoles(roles);
        existingPermission.setUpdatedAt(LocalDate.now());

        return permissionMapper.toPermissionResponse(permissionRepository.save(existingPermission));
    }


    @Override
    public void delete(String permissionId) {
        if (!permissionRepository.existsById(permissionId))
            throw new CustomException(ResponseStatusCodeEnum.PERMISSION_NOT_FOUND);
        permissionRepository.deleteById(permissionId);
    }

    @Override
    public PermissionResponse getPermission(String permissionId) {
        PermissionEntity permissionEntity = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.PERMISSION_NOT_FOUND));
        return permissionMapper.toPermissionResponse(permissionEntity);
    }

    @Override
    public Page<PermissionResponse> getAllPermission(Pageable pageable) {
        return permissionRepository.findAll(pageable).map(permissionMapper::toPermissionResponse);
    }

    @Override
    public byte[] exportPermissionsToExcel(){
        try {
            List<PermissionEntity> permissions = permissionRepository.findAll();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Permissions");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Name"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (PermissionEntity permissionEntity : permissions) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(permissionEntity.getPermissionId());
                row.createCell(1).setCellValue(permissionEntity.getName());
            }

            String filePath = "permissions.xlsx";
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
