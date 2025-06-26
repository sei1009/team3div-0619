package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dao.Attendancedao;
import com.example.demo.dao.Requestdao;
import com.example.demo.dao.Usersdao;
import com.example.demo.entity.Attendance;
import com.example.demo.entity.Request;
import com.example.demo.entity.Users;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {
	private final Requestdao requestdao;
	private final Usersdao usersdao;
	private final Attendancedao attendancedao;

	public UserController(Requestdao requestdao, Usersdao usersdao, Attendancedao attendancedao) {
		this.requestdao = requestdao;
		this.usersdao = usersdao;
		this.attendancedao = attendancedao;
	}

	@PutMapping("/request/{id}")
	public Request updateRequest(@PathVariable Long id, @RequestBody Request updated) {
		Request existing = requestdao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

		//		if (updated.getAbsence() == 1) {
		//			existing.setAbsence(updated.getAbsence());
		//		}
		//		if (updated.getEarly() == 1) {
		//			existing.setEarly(updated.getEarly());
		//
		//		}
		//		if (updated.getLate() == 1) {
		//
		//			existing.setLate(updated.getLate());
		//		}
		//		if (updated.getPaid() == 1) {
		//
		//			existing.setPaid(updated.getPaid());
		//		}
		//０・１の両方セットできる
		existing.setAbsence(updated.getAbsence());
		existing.setEarly(updated.getEarly());
		existing.setLate(updated.getLate());
		existing.setPaid(updated.getPaid());

		return requestdao.save(existing);
	}

	@GetMapping("/request/{id}")
	public Request requestreturn(@PathVariable Long id) {
		return requestdao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "リクエストが見つかりません"));
	}

	@GetMapping("/attendance/{id}") // ここでのIDはRequestのIDを想定
	public Attendance attendancereturn(@PathVariable Long id) {
		return attendancedao.findByRequestId(id) // 新しく定義したメソッドを使用
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "この申請に紐づく勤怠が見つかりません"));
	}

	@PostMapping("/attendance/{id}")
	public Attendance attendancereturn(@RequestBody Map<String, String> dateMap, @PathVariable long id) {
		String stdate = dateMap.get("punchDate");
		LocalDate date = LocalDate.parse(stdate);

		Users user = usersdao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));
		return attendancedao.findByUserAndDate(user, date);
	}

	@PostMapping("/attendance/clockin/{id}")
	public Attendance clockIn(@PathVariable long id, @RequestBody Map<String, String> body) {
		String timeStr = body.get("time");
		String dateStr = body.get("date");

		LocalDate date = LocalDate.parse(dateStr);

		Users user = usersdao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));
		Attendance att = attendancedao.findByUserAndDate(user, date);

		if (att == null) {
			att = new Attendance();
			att.setUser(user);
			att.setDate(date);
		}
		LocalTime sttime = LocalTime.parse(timeStr);

		att.setStart_time(sttime);
		return attendancedao.save(att);
	}

	@PostMapping("/attendance/clockout/{id}")
	public Attendance clockOut(@PathVariable long id, @RequestBody Map<String, String> body) {
		String timeStr = body.get("time");
		String dateStr = body.get("date");

		LocalDate date = LocalDate.parse(dateStr);
		Users user = usersdao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));
		Attendance att = attendancedao.findByUserAndDate(user, date);

		if (att == null) {
			throw new ResponseStatusException(HttpStatus.NOT_FOUND, "出勤データが存在しません。");
		}
		LocalTime entime = LocalTime.parse(timeStr);

		att.setEnd_time(entime);
		return attendancedao.save(att);
	}

	@PostMapping("/check/{id}")
	public void checkrequest(@RequestBody Map<String, String> dateMap, @PathVariable Long id) {
		String stdate = dateMap.get("punchDate");
		LocalDate date = LocalDate.parse(stdate);
		Users user = usersdao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));
		Attendance att = attendancedao.findByUserAndDate(user, date);

		if (att == null) {
			Request newreq = new Request();
			newreq.setUser(user);
			Request savedReq = requestdao.save(newreq);

			Attendance newAtt = new Attendance();
			newAtt.setUser(user);
			newAtt.setDate(date);
			newAtt.setRequest(savedReq);
			attendancedao.save(newAtt);
		}
	}

	@GetMapping("/attendance/month/{id}")
	public List<Map<String, Object>> getMonthlyAttendance(
			@PathVariable Long id,
			@RequestParam String month // 形式: "2025-06"
	) {
		System.out.println("ID=" + id);
		// 年と月に分解
		String[] parts = month.split("-");

		int year = Integer.parseInt(parts[0]);
		int monthValue = Integer.parseInt(parts[1]);

		// その月の1日〜末日を取得
		LocalDate start = LocalDate.of(year, monthValue, 1);
		LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

		Users user = usersdao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));
		List<Attendance> list = attendancedao.findByUserAndDateBetween(user, start, end); // メソッド名も修正

		System.out.println("取得対象: " + start + " ～ " + end);
		System.out.println("件数: " + list.size());
		// レスポンス形式を整える
		List<Map<String, Object>> response = new java.util.ArrayList<>();
		for (Attendance att : list) {
			Map<String, Object> item = new java.util.HashMap<>();
			item.put("date", att.getDate().toString());
			item.put("start", att.getStart_time() != null);
			item.put("end", att.getEnd_time() != null);
			item.put("start_time", att.getStart_time() != null ? att.getStart_time().toString() : null);
			item.put("end_time", att.getEnd_time() != null ? att.getEnd_time().toString() : null);

			response.add(item);
		}

		return response;
	}

}
