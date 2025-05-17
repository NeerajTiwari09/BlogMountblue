package com.example.Blog;

import com.example.Blog.enums.RoleName;
import com.example.Blog.model.*;
import com.example.Blog.repository.PostRepository;
import com.example.Blog.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@SpringBootApplication
public class BlogApplication {

	@Autowired
	private RoleRepository roleRepository;

	public static void main(String[] args) {
		SpringApplication.run(BlogApplication.class, args);
	}

	@PostConstruct
	public void init() {
		List<Role> roles = roleRepository.findAll();
		if (roles.isEmpty()) {
			Role author = new Role();
			author.setName(RoleName.AUTHOR);
			Role user = new Role();
			user.setName(RoleName.USER);
			Role admin = new Role();
			admin.setName(RoleName.ADMIN);
			roleRepository.saveAll(Arrays.asList(admin, author, user));
		}
	}
}
