package com.example.demo.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dao.Attendancedao;
import com.example.demo.dao.Requestdao;
import com.example.demo.dao.Usersdao;
import com.example.demo.entity.Attendance;
import com.example.demo.entity.Request;
import com.example.demo.entity.Users;

@RestController
@CrossOrigin
public class MasterController {

	@Autowired
	private Usersdao userDao;

	@Autowired
	private Requestdao requestDao;

	@Autowired
	private Attendancedao attendanceDao;

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
	public ResponseEntity<Users> updateUser(@PathVariable Long id, @RequestBody Users userDetails) {
		Optional<Users> userOptional = userDao.findById(id);

		if (userOptional.isPresent()) {
			Users existUser = userOptional.get();
			existUser.setUsername(userDetails.getUsername());
			existUser.setPassword(userDetails.getPassword());
			existUser.setEmail(userDetails.getEmail());
			existUser.setCategoryId(userDetails.getCategoryId());

			Users updatedUser = userDao.save(existUser);
			return ResponseEntity.ok(updatedUser);

		} else {

			return ResponseEntity.notFound().build();
		}
	}

	// ユーザー情報の削除
	@DeleteMapping("/user/delete/{id}")
	@Transactional
	public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
		Optional<Users> userOptional = userDao.findById(id);

		if (userOptional.isPresent()) {
			Users userToDelete = userOptional.get();

			// 1. ユーザーに紐づく勤怠レコードの削除
			// userToDelete オブジェクトを渡すように修正
			List<Attendance> userAttendances = attendanceDao.findByUser(userToDelete);

			// 関連するリクエストを先に処理
			for (Attendance att : userAttendances) {
				if (att.getRequest() != null) { // もし勤怠に紐づくリクエストがあれば関連を解除
					att.setRequest(null);
					attendanceDao.save(att);
				}
			}
			attendanceDao.deleteAll(userAttendances); // 紐づく勤怠レコードを一括削除

			// 2. ユーザーに紐づく申請レコードの削除
			// userToDelete オブジェクトを渡すように修正
			List<Request> userRequests = requestDao.findByUser(userToDelete);

			// 申請に紐づく勤怠のrequestidをnullにする処理
			for (Request req : userRequests) {
				Optional<Attendance> attendanceToUpdate = attendanceDao.findByRequest(req);
				attendanceToUpdate.ifPresent(att -> {
					att.setRequest(null);
					attendanceDao.save(att);
				});
			}
			requestDao.deleteAll(userRequests); // 紐づく申請レコードを一括削除

			// 3. 最後にユーザーを削除
			userDao.delete(userToDelete);
			return ResponseEntity.noContent().build();
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	// 申請情報の取得
	@GetMapping("/application/attendance")
	public ResponseEntity<List<Attendance>> getAllAttendance() {
		List<Attendance> attendances = attendanceDao.findAll();
		return ResponseEntity.ok(attendances);
	}

	@GetMapping("/application/request")
	public ResponseEntity<List<Request>> getAllRequest() {
		List<Request> requests = requestDao.findAll();
		return ResponseEntity.ok(requests);
	}

	// 申請情報の削除
	@Transactional
	@DeleteMapping("/application/delete/{id}")
	public ResponseEntity<Void> deleteRequest(@PathVariable Long id) {
		Request existing = requestDao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "申請が見つかりません"));

		Optional<Attendance> attendanceToUpdate = attendanceDao.findByRequest(existing);
		attendanceToUpdate.ifPresent(att -> {
			att.setRequest(null);
			attendanceDao.save(att);
		});

		requestDao.delete(existing);
		return ResponseEntity.noContent().build();
	}

	@DeleteMapping("/application/attendance/delete/{id}")
	public ResponseEntity<Void> deleteAttendance(@PathVariable Long id) {
		Optional<Attendance> attendanceOptional = attendanceDao.findById(id);
		if (attendanceOptional.isEmpty()) { // Java 9+ の Optional#isEmpty()。Java 8 なら !isPresent()
			return ResponseEntity.notFound().build();
		}

		attendanceDao.deleteById(id);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/application/combined") // パスを明確化
	public ResponseEntity<List<CombinedApplicationDto>> getAllCombinedApplications() {
		List<Request> requests = requestDao.findAllWithUserAndAttendance();

		List<CombinedApplicationDto> dtos = requests.stream()
				.map(req -> {
					Users user = req.getUser(); // RequestエンティティがUserオブジェクトを持っているため、直接使用

					Optional<Attendance> optionalAttendance = attendanceDao.findByRequest(req);
					Attendance attendance = optionalAttendance.orElse(null); // 勤怠が紐づいていない可能性もある

					return new CombinedApplicationDto(
							attendance != null ? attendance : new Attendance(), // 勤怠がない場合は新しい空のAttendanceオブジェクトを渡す
							user,
							req);
				})
				.collect(Collectors.toList());

		// 2. 申請に紐づかない純粋な勤怠レコードを取得し、DTOに変換して追加
		List<Attendance> pureAttendances = attendanceDao.findByRequestIsNull();

		pureAttendances.forEach(att -> {
			Users user = att.getUser();
			dtos.add(new CombinedApplicationDto(att, user, null));
		});

		// 3. 必要に応じて日付でソート (CombinedApplicationDtoに日付フィールドがあるのでソート可能)
		dtos.sort(Comparator.comparing(CombinedApplicationDto::getDate));

		return ResponseEntity.ok(dtos);
	}
}