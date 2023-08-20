package sobad.code.IT.services;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import sobad.code.repositories.UserRepository;
import sobad.code.services.impl.UserRelationshipsServiceImpl;
import sobad.code.services.impl.UserServiceImpl;

@Configuration
@ComponentScan(basePackages = {"sobad.code.repositories", "sobad.code.config", "sobad.code.services.impl"})
public class UserServiceImplTestConfig {
    @Bean
    public UserServiceImpl userService(UserRepository userRepository,
                                       PasswordEncoder passwordEncoder) {

        return new UserServiceImpl(userRepository, passwordEncoder);
    }

}
