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
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import viettelsoftware.intern.constant.ErrorCode;
import viettelsoftware.intern.dto.request.CommentRequest;
import viettelsoftware.intern.dto.response.CommentResponse;
import viettelsoftware.intern.entity.CommentEntity;
import viettelsoftware.intern.entity.PostEntity;
import viettelsoftware.intern.entity.UserEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.repository.CommentRepository;
import viettelsoftware.intern.repository.PostRepository;
import viettelsoftware.intern.repository.UserRepository;
import viettelsoftware.intern.service.CommentService;
import viettelsoftware.intern.util.ConversionUtil;
import viettelsoftware.intern.util.SecurityUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CommentServiceImpl implements CommentService {

    CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Override
    public CommentResponse create(CommentRequest request) {
        PostEntity post = postRepository.findById(request.getPostId()).orElseThrow(
                () -> new AppException(ErrorCode.POST_NOT_FOUND));

        String username = SecurityUtil.getCurrentUserLogin().orElseThrow();
        UserEntity user = userRepository.findByUsername(username).orElseThrow(
                () -> new AppException(ErrorCode.USER_NOT_FOUND));
        CommentEntity commentEntity = CommentEntity.builder()
                .content(request.getContent())
                .createdAt(LocalDate.now())
                .post(post)
                .user(user)
                .build();
        CommentEntity c = commentRepository.save(commentEntity);
        return ConversionUtil.convertObject(c, x -> modelMapper.map(x, CommentResponse.class));
    }

    @Override
    public CommentEntity update(String commentId, CommentEntity request) {
        CommentEntity existingComment = commentRepository.findById(commentId)
                .orElseThrow(() -> new AppException(ErrorCode.COMMENT_NOT_FOUND));

        existingComment.setContent(request.getContent());
        existingComment.setUpdatedAt(LocalDate.now());

        return commentRepository.save(existingComment);
    }

    @Override
    public void delete(String commentId) {
        if (!commentRepository.existsById(commentId))
            throw new AppException(ErrorCode.COMMENT_NOT_FOUND);
        commentRepository.deleteById(commentId);
    }

    @Override
    public CommentEntity getComment(String commentId) {
        return commentRepository.findById(commentId).orElseThrow(
                () -> new AppException(ErrorCode.COMMENT_NOT_FOUND));
    }

    @Override
    public Page<CommentEntity> getAllComments(Pageable pageable) {
        return commentRepository.findAll(pageable);
    }

    @Override
    public byte[] exportCommentsToExcel(){
        try {
            List<CommentEntity> comments = commentRepository.findAll();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Posts");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Content", "Created At", "Post ID", "User ID"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (CommentEntity commentEntity : comments) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(commentEntity.getCommentId());
                row.createCell(1).setCellValue(commentEntity.getContent());
                row.createCell(2).setCellValue(commentEntity.getCreatedAt().toString());
                row.createCell(3).setCellValue(commentEntity.getPost().getPostId());
                row.createCell(4).setCellValue(commentEntity.getUser().getUserId());
            }

            String filePath = "comments.xlsx";
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
