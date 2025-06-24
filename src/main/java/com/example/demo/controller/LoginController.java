package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.Usersdao;
import com.example.demo.entity.Log_Form;
import com.example.demo.entity.Reg_Form;
import com.example.demo.entity.Users;
import com.example.demo.service.LoginService;

@RestController
@CrossOrigin
public class LoginController {

	private final LoginService loginService;
	private final Usersdao usersdao;

	public LoginController(LoginService loginService, Usersdao usersdao) {
		super();
		this.loginService = loginService;
		this.usersdao = usersdao;
	}

	// ログイン処理
	@PostMapping("/login")
	public ResponseEntity<Users> loginUser(@RequestBody Log_Form log_form) {
		try {
			Users user = loginService.login(log_form); // ← メソッドも LoginRequest を受け取る形に統一
			return ResponseEntity.ok(user);
		} catch (IllegalArgumentException e) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}
	}

	// 登録処理
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody Reg_Form reg_form) {
		try {
			loginService.register(reg_form);
			return ResponseEntity.ok("登録成功");
		} catch (IllegalArgumentException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}

}