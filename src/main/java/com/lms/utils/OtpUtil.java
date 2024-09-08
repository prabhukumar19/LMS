package com.lms.utils;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class OtpUtil {
    public static String generateOtp(){

        Random random = new Random();
        int randomNumbers= random.nextInt(999999);
        String otp = Integer.toString(randomNumbers);
        while(otp.length()<6){
            otp+="0";

        }
        return otp;
    }
}
