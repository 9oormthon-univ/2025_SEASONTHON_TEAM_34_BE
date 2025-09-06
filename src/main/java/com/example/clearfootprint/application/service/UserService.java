package com.example.clearfootprint.application.service;

import com.example.clearfootprint.domain.user.User;
import com.example.clearfootprint.domain.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Transactional 추가

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    // 데이터 변경이 일어나므로 @Transactional 어노테이션을 추가하는 것이 좋습니다.
    @Transactional
    public void saveOrUpdateUser(String email, String name, String picture) {
        Optional<User> optionalUser = userRepository.findByEmail(email);

        if (optionalUser.isPresent()) {
            // 이미 존재하는 사용자인 경우
            User user = optionalUser.get();
            // 이름이나 사진이 변경되었는지 확인하고 업데이트
            user.update(name, picture);
            // JPA의 Dirty Checking 기능으로 인해 user.update() 메소드 호출만으로
            // 트랜잭션이 끝날 때 자동으로 UPDATE 쿼리가 실행됩니다.
            // 따라서 userRepository.save(user)를 명시적으로 호출할 필요가 없습니다.
        } else {
            // 새로운 사용자인 경우
            User newUser = new User(email, name, picture);
            userRepository.save(newUser);
        }
    }
}