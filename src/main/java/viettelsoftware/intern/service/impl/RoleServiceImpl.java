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
import viettelsoftware.intern.dto.request.RoleRequest;
import viettelsoftware.intern.dto.response.RoleResponse;
import viettelsoftware.intern.entity.PermissionEntity;
import viettelsoftware.intern.entity.RoleEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.mapper.RoleMapper;
import viettelsoftware.intern.repository.PermissionRepository;
import viettelsoftware.intern.repository.RoleRepository;
import viettelsoftware.intern.service.RoleService;

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
public class RoleServiceImpl implements RoleService {

    RoleRepository roleRepository;
    RoleMapper roleMapper;
    PermissionRepository permissionRepository;

    @Override
    public RoleResponse create(RoleRequest request) {
        if (roleRepository.existsByName(request.getName()))
            throw new AppException(ErrorCode.ROLE_EXISTED);
        RoleEntity roleEntity = roleMapper.toRole(request);
        Set<PermissionEntity> set = request.getPermissionIds().stream()
                .map(permissionDto -> permissionRepository.findByName(permissionDto.getName())
                        .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND)))
                .collect(Collectors.toSet());
        roleEntity.setPermissions(set);
        roleRepository.save(roleEntity);
        return roleMapper.toRoleResponse(roleEntity);
    }

    @Override
    public void delete(String roleId) {
        if (!roleRepository.existsById(roleId))
            throw new AppException(ErrorCode.ROLE_NOT_FOUND);
        roleRepository.deleteById(roleId);
    }

    @Override
    public RoleResponse update(String roleId, RoleRequest request) {
        RoleEntity role = roleRepository.findById(roleId)
                .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));

        if (!role.getName().equals(request.getName()) && roleRepository.existsByName(request.getName())) {
            throw new AppException(ErrorCode.ROLE_EXISTED);
        }

        role.setName(request.getName());

        Set<PermissionEntity> updatedPermissions = request.getPermissionIds().stream()
                .map(permissionDto -> permissionRepository.findByName(permissionDto.getName())
                        .orElseThrow(() -> new AppException(ErrorCode.PERMISSION_NOT_FOUND)))
                .collect(Collectors.toSet());
        role.setPermissions(updatedPermissions);

        return roleMapper.toRoleResponse(roleRepository.save(role));
    }

    @Override
    public RoleResponse getRole(String roleId) {
        RoleEntity roleEntity = roleRepository.findById(roleId).orElseThrow(
                () -> new AppException(ErrorCode.ROLE_NOT_FOUND));
        return roleMapper.toRoleResponse(roleEntity);
    }

    @Override
    public Page<RoleResponse> getAllRole(Pageable pageable) {
        return roleRepository.findAll(pageable).map(roleMapper::toRoleResponse);
    }

    @Override
    public byte[] exportRolesToExcel() {
        try {
            List<RoleEntity> roles = roleRepository.findAll();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Roles");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Name"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (RoleEntity roleEntity : roles) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(roleEntity.getRoleId());
                row.createCell(1).setCellValue(roleEntity.getName());
            }

            String filePath = "roles.xlsx";
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
