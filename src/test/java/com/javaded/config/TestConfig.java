package com.javaded.config;

import com.javaded.repository.TaskRepository;
import com.javaded.repository.UserRepository;
import com.javaded.service.ImageService;
import com.javaded.service.impl.AuthServiceImpl;
import com.javaded.service.impl.ImageServiceImpl;
import com.javaded.service.impl.MailServiceImpl;
import com.javaded.service.impl.TaskServiceImpl;
import com.javaded.service.impl.UserServiceImpl;
import com.javaded.service.props.JwtProperties;
import com.javaded.service.props.MinioProperties;
import com.javaded.web.security.JwtTokenProvider;
import com.javaded.web.security.JwtUserDetailsService;
import freemarker.template.Configuration;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@TestConfiguration
@RequiredArgsConstructor
public class TestConfig {

    @Bean
    @Primary
    public BCryptPasswordEncoder testPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JwtProperties jwtProperties() {
        JwtProperties jwtProperties = new JwtProperties();
        jwtProperties.setSecret(
                "dmdqYmhqbmttYmNhamNjZWhxa25hd2puY2xhZWtic3ZlaGtzYmJ1dg=="
        );
        return jwtProperties;
    }

    @Bean
    public UserDetailsService userDetailsService(
            final UserRepository userRepository
    ) {
        return new JwtUserDetailsService(userService(userRepository));
    }

    @Bean
    public MinioClient minioClient() {
        return Mockito.mock(MinioClient.class);
    }

    @Bean
    public MinioProperties minioProperties() {
        MinioProperties properties = new MinioProperties();
        properties.setBucket("images");
        return properties;
    }

    @Bean
    public Configuration configuration() {
        return Mockito.mock(Configuration.class);
    }

    @Bean
    public JavaMailSender mailSender() {
        return Mockito.mock(JavaMailSender.class);
    }

    @Bean
    @Primary
    public MailServiceImpl mailService() {
        return new MailServiceImpl(configuration(), mailSender());
    }

    @Bean
    @Primary
    public ImageService imageService() {
        return new ImageServiceImpl(minioClient(), minioProperties());
    }

    @Bean
    public JwtTokenProvider tokenProvider(
            final UserRepository userRepository
    ) {
        return new JwtTokenProvider(jwtProperties(),
                userDetailsService(userRepository),
                userService(userRepository));
    }

    @Bean
    @Primary
    public UserServiceImpl userService(
            final UserRepository userRepository
    ) {
        return new UserServiceImpl(
                userRepository,
                testPasswordEncoder(),
                mailService()
        );
    }

    @Bean
    @Primary
    public TaskServiceImpl taskService(
            final TaskRepository taskRepository
    ) {
        return new TaskServiceImpl(taskRepository, imageService());
    }

    @Bean
    @Primary
    public AuthServiceImpl authService(
            final UserRepository userRepository,
            final AuthenticationManager authenticationManager
    ) {
        return new AuthServiceImpl(
                authenticationManager,
                userService(userRepository),
                tokenProvider(userRepository)
        );
    }

    @Bean
    public UserRepository userRepository() {
        return Mockito.mock(UserRepository.class);
    }

    @Bean
    public TaskRepository taskRepository() {
        return Mockito.mock(TaskRepository.class);
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return Mockito.mock(AuthenticationManager.class);
    }
}
