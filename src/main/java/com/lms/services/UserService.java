package com.lms.services;

import com.lms.entities.Role;
import com.lms.entities.User;
import com.lms.repositories.RoleRepository;
import com.lms.repositories.UserRepository;
import com.lms.utils.EmailUtil;
import com.lms.utils.OtpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import com.lms.dto.RegisterDto;
import com.lms.dto.LoginDto;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Collections;

@Service
public class UserService {

    @Autowired
    UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailUtil emailUtil;

    public ResponseEntity<String> createUser(RegisterDto registerDto) {
        System.out.println("Called");
        String otp=OtpUtil.generateOtp();
        try {
            emailUtil.sendOtp(registerDto.getEmail(), otp);
        }
        catch (Exception e){
            throw  new RuntimeException("Unable to sent OTP please try again"+e);
        }

        // add check for username exists in a DB
        if(userRepository.existsByEmail(registerDto.getEmail())){
            return new ResponseEntity<>("Username is already taken!", HttpStatus.BAD_REQUEST);
        }

        // add check for email exists in DB
        if(userRepository.existsByEmail(registerDto.getEmail())){
            return new ResponseEntity<>("Email is already taken!", HttpStatus.BAD_REQUEST);
        }

        // create user object
        User user = new User();
        user.setName(registerDto.getName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setOTP(otp);
        user.setOtpGeneratedAt(LocalDateTime.now());
        Role roles = roleRepository.findByName("ROLE_ADMIN").get();
        user.setRoles(Collections.singleton(roles));
        userRepository.save(user);
        return new ResponseEntity<>("User Registration is Successful", HttpStatus.CREATED);
    }

    public String verifyAccount(String email, String otp) {
       User user= userRepository.findByEmail(email)
               .orElseThrow(()->new RuntimeException("User not found with :-"+email));
       if(user.getOTP().equals(otp) &&
               Duration.between(user.getOtpGeneratedAt(),LocalDateTime.now()).getSeconds()<(2*60)){
           user.setActive(true);
           return "OTP is verified";

       }
        return "Please regenerate OTP and try again";
    }

    public String regenerateOtp(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(()->new RuntimeException("User is not found"));
        String otp2= OtpUtil.generateOtp();
        try {
            emailUtil.sendOtp(email, otp2);
        }
        catch (Exception e){
            throw  new RuntimeException("Unable to sent OTP please try again"+e);
        }
        user.setOTP(otp2);
        user.setOtpGeneratedAt(LocalDateTime.now());
        userRepository.save(user);
        return "OTP has been sent to your email "+email;
    }
}
