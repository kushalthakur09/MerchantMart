package com.main.MerchantMart.repository;

import com.main.MerchantMart.domain.Role;
import com.main.MerchantMart.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class UserRepositoryTest {
    private UserRepository userRepository;

    @Autowired
   public UserRepositoryTest(UserRepository userRepository){
        this.userRepository=userRepository;
    }

    @Test
    void testFindByEmail(){
        User user=new User();
        user.setEmail("test123@gmail.com");
        user.setFullUserName("test-1");
        user.setPassword("test-password");
        user.setRole(Role.ROLE_USER);
        user.setPhoneNo("9876543211");
        user.setProvider("LOCAL");

        userRepository.save(user);

        Optional<User> userFrmDB=userRepository.findByEmail(user.getEmail());

        assertTrue(userFrmDB.isPresent());
        assertEquals("test123@gmail.com",userFrmDB.get().getEmail() );

    }
}
