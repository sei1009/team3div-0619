package com.example.demo.controller;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.dao.Attendancedao;
import com.example.demo.dao.Requestdao;
import com.example.demo.dao.Usersdao;
import com.example.demo.entity.Attendance;
import com.example.demo.entity.Request;
import com.example.demo.entity.Users;

@RestController
@CrossOrigin
public class AdminController {

	@Autowired
	private Usersdao userDao;

	@Autowired
	private Requestdao requestDao;

	@Autowired
	private Attendancedao attendanceDao;

	// ユーザー一覧情報の取得
	@GetMapping("/admin/user/list")
	public ResponseEntity<List<Users>> getAllUsers() {
		List<Users> users = userDao.findAll();
		return ResponseEntity.ok(users);
	}

	// 申請情報の取得 (AttendanceとRequestは別々のエンドポイントとして維持)
	@GetMapping("/admin/attendance")
	public ResponseEntity<List<Attendance>> getAllAttendance() {
		List<Attendance> attendances = attendanceDao.findAll();
		return ResponseEntity.ok(attendances);
	}

	@GetMapping("/admin/request")
	public ResponseEntity<List<Request>> getAllRequest() {
		List<Request> requests = requestDao.findAll();
		return ResponseEntity.ok(requests);
	}

	// 結合された申請データの取得 (Requestを主軸に結合)
	@GetMapping("/admin/combined") // パスを明確化
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

	// --- 申請の承認・却下API (前回の提案から変更なし) ---
	/**
	 * 特定の申請タイプを承認するAPI
	 * @param id 承認する申請のID
	 * @param type 承認する申請タイプ (e.g., "paid", "early", "absence", "late")
	 * @return 更新された申請エンティティ、またはエラー
	 */
	@PutMapping("/admin/request/{id}/approve/{type}")
	public ResponseEntity<Request> approveRequestType(@PathVariable Long id, @PathVariable String type) {
		Optional<Request> optionalRequest = requestDao.findById(id);
		if (optionalRequest.isPresent()) {
			Request request = optionalRequest.get();
			updateApprovalStatus(request, type, 1); // 承認ステータスを1に設定
			Request updatedRequest = requestDao.save(request);
			return ResponseEntity.ok(updatedRequest);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * 特定の申請タイプを却下するAPI
	 * @param id 却下する申請のID
	 * @param type 却下する申請タイプ (e.g., "paid", "early", "absence", "late")
	 * @return 更新された申請エンティティ、またはエラー
	 */
	@PutMapping("/admin/request/{id}/reject/{type}")
	public ResponseEntity<Request> rejectRequestType(@PathVariable Long id, @PathVariable String type) {
		Optional<Request> optionalRequest = requestDao.findById(id);
		if (optionalRequest.isPresent()) {
			Request request = optionalRequest.get();
			updateApprovalStatus(request, type, 2); // 却下ステータスを2に設定
			Request updatedRequest = requestDao.save(request);
			return ResponseEntity.ok(updatedRequest);
		} else {
			return ResponseEntity.notFound().build();
		}
	}

	/**
	 * 申請タイプに基づいて承認ステータスを更新するヘルパーメソッド
	 * @param request 更新対象のRequestオブジェクト
	 * @param type 申請タイプ文字列
	 * @param status 更新するステータス値 (1=承認, 2=却下)
	 */
	private void updateApprovalStatus(Request request, String type, int status) {
		switch (type) {
		case "paid":
			request.setPaid_app(status);
			break;
		case "early":
			request.setEarly_app(status);
			break;
		case "absence":
			request.setAbsence_app(status);
			break;
		case "late":
			request.setLate_app(status);
			break;
		default:
			System.err.println("Unknown request type: " + type);
			break;
		}
	}
}