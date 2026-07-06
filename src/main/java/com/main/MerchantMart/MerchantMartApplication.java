package com.main.MerchantMart;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class MerchantMartApplication {

	public static void main(String[] args) {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Kolkata"));
        System.out.println(TimeZone.getDefault().getID());
        SpringApplication.run(MerchantMartApplication.class, args);
	}

}
