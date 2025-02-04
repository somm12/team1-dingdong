package com.ddbb.dingdong.domain.user.repository.userRepository;

import com.ddbb.dingdong.domain.user.entity.House;
import com.ddbb.dingdong.domain.user.entity.School;
import com.ddbb.dingdong.domain.user.entity.User;
import com.ddbb.dingdong.domain.user.repository.UserReadOnlyRepository;
import com.ddbb.dingdong.domain.user.repository.UserRepository;
import com.ddbb.dingdong.domain.user.repository.projection.UserStaticOnly;
import com.ddbb.dingdong.infrastructure.auth.encrypt.SHA512PasswordEncoder;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

@SpringBootTest
class GetStaticInfoTest {
    @Autowired
    private UserReadOnlyRepository userReadOnlyRepository;
    private SHA512PasswordEncoder encoder = new SHA512PasswordEncoder();
    private String password = encoder.encode("123456");
    private School school = new School(null, "seoul", "seoul", new BigDecimal("1.0"), new BigDecimal("1.0"));
    private House house = new House(null, "address", new BigDecimal("1.0"), new BigDecimal("1.0"), null);
    private User user = new User(null, "test", "test@test.com", password, LocalDateTime.now(), school, house);


    @Test
    @Transactional
    public void success() {
        User saved = userReadOnlyRepository.save(user);
        Optional<UserStaticOnly> optional = this.userReadOnlyRepository.findUserStaticInfoById(saved.getId());

        if (optional.isEmpty()) {
            Assertions.fail();
        }
        UserStaticOnly userStaticOnly = optional.get();
        Assertions.assertEquals(user.getEmail(), userStaticOnly.getEmail());
        Assertions.assertEquals(user.getName(), userStaticOnly.getUserName());
        Assertions.assertEquals(school.getName(), userStaticOnly.getSchoolName());
    }
}