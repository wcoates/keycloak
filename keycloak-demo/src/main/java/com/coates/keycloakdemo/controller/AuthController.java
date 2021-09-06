
package com.coates.keycloakdemo.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
	@RequestMapping("/home")
	public String home() {
		return "Hello!";
	}
}
