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
import viettelsoftware.intern.entity.PostEntity;
import viettelsoftware.intern.exception.AppException;
import viettelsoftware.intern.repository.PostRepository;
import viettelsoftware.intern.service.PostService;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImpl implements PostService {

    PostRepository postRepository;

    @Override
    public PostEntity create(PostEntity request) {
        if (postRepository.existsById(request.getPostId()) || postRepository.existsByTitle(request.getTitle()))
            throw new AppException(ErrorCode.POST_EXISTED);
        request.setCreatedAt(LocalDate.now());
        return postRepository.save(request);
    }

    @Override
    public PostEntity update(String postId, PostEntity request) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new AppException(ErrorCode.POST_NOT_FOUND));

        if (!post.getTitle().equals(request.getTitle()) && postRepository.existsByTitle(request.getTitle())) {
            throw new AppException(ErrorCode.POST_EXISTED);
        }

        post.setTitle(request.getTitle());
        post.setBody(request.getBody());
        post.setUpdatedAt(LocalDate.now());

        return postRepository.save(post);
    }


    @Override
    public void delete(String postId) {
        if (!postRepository.existsById(postId))
            throw new AppException(ErrorCode.POST_NOT_FOUND);
        postRepository.deleteById(postId);
    }

    @Override
    public PostEntity getPost(String postId) {
        return postRepository.findById(postId).orElseThrow(
                () -> new AppException(ErrorCode.POST_NOT_FOUND));
    }

    @Override
    public Page<PostEntity> getAllPosts(Pageable pageable) {
        return postRepository.findAll(pageable);
    }

    @Override
    public byte[] exportPostsToExcel(){
        try {
            List<PostEntity> posts = postRepository.findAll();
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Posts");

            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Title", "Body", "Created At"};
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
            }

            int rowIdx = 1;
            for (PostEntity postEntity : posts) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(postEntity.getPostId());
                row.createCell(1).setCellValue(postEntity.getTitle());
                row.createCell(2).setCellValue(postEntity.getBody());
                row.createCell(3).setCellValue(postEntity.getCreatedAt().toString());
            }

            String filePath = "posts.xlsx";
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
