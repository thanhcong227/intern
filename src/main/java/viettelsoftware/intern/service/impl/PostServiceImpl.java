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
import viettelsoftware.intern.config.modelmapper.PostMapper;
import viettelsoftware.intern.constant.ResponseStatusCodeEnum;
import viettelsoftware.intern.dto.request.PostRequest;
import viettelsoftware.intern.dto.response.PostResponse;
import viettelsoftware.intern.entity.PostEntity;
import viettelsoftware.intern.entity.UserEntity;
import viettelsoftware.intern.exception.CustomException;
import viettelsoftware.intern.repository.PostRepository;
import viettelsoftware.intern.repository.UserRepository;
import viettelsoftware.intern.service.PostService;
import viettelsoftware.intern.util.ConversionUtil;
import viettelsoftware.intern.util.SecurityUtil;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class PostServiceImpl implements PostService {

    PostRepository postRepository;
    UserRepository userRepository;
    ModelMapper modelMapper;
    PostMapper postMapper;

    @Override
    public PostResponse create(PostRequest request) {
        if (postRepository.existsByTitle(request.getTitle())) {
            throw new CustomException(ResponseStatusCodeEnum.POST_EXISTED);
        }

        PostEntity post = new PostEntity();
        post.setTitle(request.getTitle());
        post.setBody(request.getBody());
        post.setCreatedAt(LocalDate.now());

        // Log để kiểm tra giá trị username lấy từ SecurityContext
        String username = SecurityUtil.getCurrentUserLogin()
                .orElseThrow(() -> {
                    log.error("Username not found in SecurityContext.");
                    return new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND);
                });

        log.info("Current username: {}", username);
        log.info("Current username from SecurityContext: {}", username);

        UserEntity user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> {
                    log.error("User not found: {}", username);
                    return new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND);
                });

        post.setUser(user);
        postRepository.save(post);
        return ConversionUtil.convertObject(post, postMapper::toDto);
    }

    @Override
    public PostResponse update(String postId, PostRequest request) {
        PostEntity post = postRepository.findById(postId)
                .orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.POST_NOT_FOUND));

        Optional.ofNullable(request.getTitle())
                .filter(title -> !title.equals(post.getTitle()))
                .ifPresent(title -> {
                    if (postRepository.existsByTitle(title)) {
                        throw new CustomException(ResponseStatusCodeEnum.POST_EXISTED);
                    }
                    post.setTitle(title);
                });

        Optional.ofNullable(request.getBody()).ifPresent(post::setBody);

        post.setUpdatedAt(LocalDate.now());

        PostEntity updated = postRepository.save(post);
        return modelMapper.map(updated, PostResponse.class);
    }


    @Override
    public void delete(String postId) {
        if (!postRepository.existsById(postId))
            throw new CustomException(ResponseStatusCodeEnum.POST_NOT_FOUND);
        postRepository.deleteById(postId);
    }

    @Override
    public PostResponse getPost(String postId) {
        PostEntity post = postRepository.findById(postId).orElseThrow(
                () -> new CustomException(ResponseStatusCodeEnum.POST_NOT_FOUND));
        return ConversionUtil.convertObject(post, postMapper::toDto);
    }

    @Override
    public Page<PostResponse> getAllPosts(Pageable pageable) {
        Page<PostEntity> posts = postRepository.findAll(pageable);
        return ConversionUtil.convertPage(posts, postMapper::toDto);
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
