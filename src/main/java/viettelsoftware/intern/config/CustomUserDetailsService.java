package viettelsoftware.intern.config;

import lombok.AllArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import viettelsoftware.intern.constant.ResponseStatusCodeEnum;
import viettelsoftware.intern.entity.UserEntity;
import viettelsoftware.intern.exception.CustomException;
import viettelsoftware.intern.repository.UserRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsernameIgnoreCase(username).orElseThrow(() -> new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND));
        if (userEntity != null) {
            List<GrantedAuthority> roles = userEntity.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());

            return new CustomUser(userEntity.getUsername(), userEntity.getPassword(), roles);
        }
        throw new CustomException(ResponseStatusCodeEnum.USER_NOT_FOUND);
    }
}
