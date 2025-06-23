package com.example.demo.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.Requestdao;
import com.example.demo.dao.Usersdao;
import com.example.demo.entity.Request;
import com.example.demo.entity.Users;

@RestController
@CrossOrigin
public class AdminController {

	@Autowired
	private Usersdao userDao;

	@Autowired
	private Requestdao requestDao;

	// 特定のユーザー1人の情報を取得
	@GetMapping("/master/home/{id}")
	public ResponseEntity<Users> userFindOne(@PathVariable Long id) {
		Optional<Users> userOptional = userDao.findById(id);
		if (userOptional.isPresent()) {
			return ResponseEntity.ok(userOptional.get());
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// ユーザー一覧情報の取得
	@GetMapping("/user/list")
	public ResponseEntity<List<Users>> getAllUsers() {
		List<Users> users = userDao.findAll();
		return ResponseEntity.ok(users);
	}

	// ユーザーの情報を編集
	@PutMapping("/user/edit/{id}")
	@Transactional
	public ResponseEntity<Users> updateQuiz(@PathVariable Long id, @RequestBody Users userDetails) {
		Optional<Users> userOptional = userDao.findById(id);

		if (userOptional.isPresent()) {
			Users existUser = userOptional.get();
			existUser.setUsername(userDetails.getUsername());
			existUser.setPassword(userDetails.getPassword());
			existUser.setEmail(userDetails.getEmail());
			existUser.setCategory_id(userDetails.getCategory_id());

			Users updatedUser = userDao.save(existUser);
			return ResponseEntity.ok(updatedUser);

		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// ユーザー情報の削除
	@DeleteMapping("/user/delete/{id}")
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		Optional<Users> userOptional = userDao.findById(id);

		if (userOptional.isPresent()) {
			userDao.delete(userOptional.get());
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 申請情報の取得
	@GetMapping("/application")
	public ResponseEntity<List<Request>> getAllRequest() {
		List<Request> requests = requestDao.findAll();
		return ResponseEntity.ok(requests);
	}

	// 申請情報の削除
	@DeleteMapping("/application/delete/{id}")
	public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
		Optional<Request> requestOptional = requestDao.findById(id);

		if (requestOptional.isPresent()) {
			requestDao.delete(requestOptional.get());
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}
}
