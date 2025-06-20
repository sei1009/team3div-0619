package com.example.demo.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dao.Requestdao;
import com.example.demo.dao.Usersdao;
import com.example.demo.entity.Request;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
	private final Requestdao requestdao;
	private final Usersdao usersdao;

	public UserController(Requestdao requestdao, Usersdao usersdao) {
		this.requestdao = requestdao;
		this.usersdao = usersdao;
	}

	@PostMapping("/{id}")
	public Request viewUser(@RequestBody Request request, @PathVariable long id) {
		usersdao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));
		return requestdao.findByUser_Id(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "リクエストが見つかりません"));
	}

}
