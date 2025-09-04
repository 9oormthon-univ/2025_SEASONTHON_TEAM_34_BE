package com.example.clearfootprint.application.service;

import com.example.clearfootprint.domain.User;
import com.example.clearfootprint.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;


@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void saveOrUpdateUser(String email, String name, String picture) {
        Optional<User> optionalUser = userRepository.findByEmail(email);
        User user;

        // TODO: 사용자 정보가 변경될 경우 업데이트 로직 추가
        user = optionalUser.orElseGet(() -> new User(email, name, picture));

        userRepository.save(user);
    }
}