package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.domain.user.UserRepository;
import hongik.ce.jolup.domain.user.UserRole;
import hongik.ce.jolup.web.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // UserDetailService 상속시 필수로 구현해야 하는 메소드
    // UserDetail 가 기본 반환 타입, Account 가 이를 상속하고 있으므로 자동으로 다운캐스팅됨
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));
    }

    @Transactional
    public Long save(UserDto userDto) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userDto.setPassword(encoder.encode(userDto.getPassword()));

        return userRepository.save(User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .password(userDto.getPassword())
                .role(UserRole.USER).build()).getId();
    }

    public Optional<User> findOne(String email) {
        return userRepository.findByEmail(email);
    }
}