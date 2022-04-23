package hongik.ce.jolup.service;

import hongik.ce.jolup.domain.user.User;
import hongik.ce.jolup.repository.UserRepository;
import hongik.ce.jolup.domain.user.UserRole;
import hongik.ce.jolup.dto.UserDto;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    // UserDetailService 상속시 필수로 구현해야 하는 메소드
    // UserDetail 가 기본 반환 타입, Account 가 이를 상속하고 있으므로 자동으로 다운캐스팅됨
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Could not found : " + email));
    }

    public Long save(UserDto userDto) {
        validateDuplicateUser(userDto);

        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        userDto.setPassword(encoder.encode(userDto.getPassword()));

        return userRepository.save(User.builder()
                .email(userDto.getEmail())
                .name(userDto.getName())
                .password(userDto.getPassword())
                .role(UserRole.USER).build()).getId();
    }

    private void validateDuplicateUser(UserDto userDto) {
        Optional<User> findUser = userRepository.findByEmail(userDto.getEmail());
        if (!findUser.isEmpty()) {
            throw new IllegalStateException("이미 존재하는 아이디입니다!");
        }
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public UserDto findOne(String email) {
        Optional<User> userWrapper = userRepository.findByEmail(email);
        if (userWrapper.isEmpty()) {
            return null;
        }
        User user = userWrapper.get();
        UserDto userDto = User.toDto(user);
        return userDto;
    }

    public void delete(Long id) {
        userRepository.deleteById(id);
    }
}