
package com.coates.keycloakdemo.authendpoints;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AuthController {
	@RequestMapping("/home")
	public String home() {
		return "Hello!";
	}
}
