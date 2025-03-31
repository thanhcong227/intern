package viettelsoftware.intern.config;

import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import viettelsoftware.intern.entity.*;
import viettelsoftware.intern.repository.*;

import java.time.LocalDate;
import java.util.Set;

@Configuration
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class InitializerConfig {

    UserRepository userRepository;
    RoleRepository roleRepository;
    PasswordEncoder passwordEncoder;
    GenreRepository genreRepository;
    BookRepository bookRepository;

    @Bean
    public ApplicationRunner initializer() {
        return args -> {
            if (userRepository.count() == 0) {
                RoleEntity adminRole = roleRepository.save(new RoleEntity(null, "ADMIN", null, null));
                RoleEntity userRole = roleRepository.save(new RoleEntity(null, "USER", null, null));

                UserEntity admin = UserEntity.builder()
                        .username("admin")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("ADMIN")
                        .email("admin@gmail.com")
                        .phone("0987654321")
                        .address("Ha Noi")
                        .roles(Set.of(adminRole))
                        .build();

                UserEntity user = UserEntity.builder()
                        .username("user")
                        .password(passwordEncoder.encode("123456"))
                        .fullName("USER")
                        .email("user@gmail.com")
                        .phone("0123456789")
                        .address("Ha Noi")
                        .roles(Set.of(userRole))
                        .build();

                userRepository.save(admin);
                userRepository.save(user);

                log.info("Create account admin and user successful");
            }

            // Thêm thể loại sách nếu chưa có
            if (genreRepository.count() == 0) {
                GenreEntity tinhCam = GenreEntity.builder()
                        .name("Tinh Cam")
                        .createdAt(LocalDate.now())
                        .build();

                GenreEntity haiHuoc = GenreEntity.builder()
                        .name("Hai Huoc")
                        .createdAt(LocalDate.now())
                        .build();

                GenreEntity hanhDong = GenreEntity.builder()
                        .name("Hanh Dong")
                        .createdAt(LocalDate.now())
                        .build();

                genreRepository.saveAll(Set.of(tinhCam, haiHuoc, hanhDong));

                log.info("Inserted genres successfully");
            }

            // Thêm thể loại sách nếu chưa có
            if (bookRepository.count() == 0) {
                GenreEntity tinhCam = genreRepository.findByName("Tinh Cam").orElseThrow(() -> new RuntimeException("Genre not found"));
                GenreEntity haiHuoc = genreRepository.findByName("Hai Huoc").orElseThrow(() -> new RuntimeException("Genre not found"));
                GenreEntity hanhDong = genreRepository.findByName("Hanh Dong").orElseThrow(() -> new RuntimeException("Genre not found"));

                BookEntity book1 = BookEntity.builder()
                        .title("Cuộc sống vui vẻ")
                        .author("Nguyen Van A")
                        .year(2022)
                        .createdAt(LocalDate.now())
                        .updatedAt(LocalDate.now())
                        .genres(Set.of(haiHuoc)) // Đã lấy từ DB
                        .build();

                BookEntity book2 = BookEntity.builder()
                        .title("Hành trình tình yêu")
                        .author("Tran Thi B")
                        .year(2020)
                        .createdAt(LocalDate.now())
                        .updatedAt(LocalDate.now())
                        .genres(Set.of(tinhCam))
                        .build();

                BookEntity book3 = BookEntity.builder()
                        .title("Anh hùng báo thù")
                        .author("Le Van C")
                        .year(2021)
                        .createdAt(LocalDate.now())
                        .updatedAt(LocalDate.now())
                        .genres(Set.of(hanhDong))
                        .build();

                bookRepository.saveAll(Set.of(book1, book2, book3));

                log.info("Inserted books successfully");
            }
        };
    }
}
