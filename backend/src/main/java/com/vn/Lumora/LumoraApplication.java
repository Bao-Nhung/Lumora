package com.vn.Lumora;

import com.vn.Lumora.model.User;
import com.vn.Lumora.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@SpringBootApplication
public class LumoraApplication {

	public static void main(String[] args) {
		SpringApplication.run(LumoraApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDatabase(UserRepository userRepository) {
		return args -> {
			String emailUser = "ngbao";
			if (!userRepository.existsByEmail(emailUser)) {
				String password = "Bao56270";
				String hashedPassword = hashPassword(password);
				User defaultUser = new User("NgBao", emailUser, hashedPassword, "USER");
				userRepository.save(defaultUser);
				System.out.println("Default user pre-created: NgBao / Bao56270 (USER)");
			}

			String emailAdmin = "admin";
			if (!userRepository.existsByEmail(emailAdmin)) {
				String password = "Admin56270";
				String hashedPassword = hashPassword(password);
				User defaultAdmin = new User("Admin", emailAdmin, hashedPassword, "ADMIN");
				userRepository.save(defaultAdmin);
				System.out.println("Default admin pre-created: Admin / Admin56270 (ADMIN)");
			}
		};
	}

	private String hashPassword(String password) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
			StringBuilder hexString = new StringBuilder(2 * encodedhash.length);
			for (byte b : encodedhash) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1) {
					hexString.append('0');
				}
				hexString.append(hex);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException("SHA-256 algorithm not found", e);
		}
	}
}
