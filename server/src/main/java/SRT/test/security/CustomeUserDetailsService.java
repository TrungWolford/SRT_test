package SRT.test.security;

import SRT.test.entity.User;
import SRT.test.repo.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomeUserDetailsService implements UserDetailsService {

    private final IUserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrAccount) throws UsernameNotFoundException {
        User user = userRepository.findByAccountName(usernameOrAccount)
                .or(() -> userRepository.findByUserName(usernameOrAccount))
                .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + usernameOrAccount));
        return new CustomUserDetails(user);
    }
}

