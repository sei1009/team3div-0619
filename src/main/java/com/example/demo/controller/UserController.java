package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.example.demo.dao.Attendancedao;
import com.example.demo.dao.Requestdao;
import com.example.demo.dao.Usersdao;
import com.example.demo.entity.Attendance;
import com.example.demo.entity.Request;

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

	@GetMapping("/request/{id}")
	public Request requestreturn(@PathVariable Long id) {
		return requestdao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "リクエストが見つかりません"));
	}

	@PostMapping("/attendance/{id}")
	public Attendance attendancereturn(@RequestBody Map<String, String> dateMap, @PathVariable long id) {
		String stdate = dateMap.get("punchDate");
		LocalDate date = LocalDate.parse(stdate);

		return attendancedao.findByUseridAndDate(id, date);
	}

	@PostMapping("/attendance/clockin/{id}")
	public Attendance clockIn(@PathVariable long id, @RequestBody Map<String, String> body) {
		String timeStr = body.get("time");
		String dateStr = body.get("date");
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println("受け取ったJSON: " + body);
		LocalDate date = LocalDate.parse(dateStr);
		Attendance att = attendancedao.findByUseridAndDate(id, date);

		if (att == null) {
			// データがなければ新規作成（必要に応じて調整）
			att = new Attendance();
			att.setUserid(id);
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
		Attendance att = attendancedao.findByUseridAndDate(id, date);

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

		Attendance att = attendancedao.findByUseridAndDate(id, date);
		if (att == null) {
			Request newreq = new Request();
			newreq.setUserid(id);
			Request savedReq = requestdao.save(newreq);
			System.out.println("新規リクエストID: " + savedReq.getId());
			System.out.println(savedReq.getId());
			System.out.println(savedReq.getUserid());
			System.out.println(savedReq.getId());
			System.out.println(savedReq.getUserid());
			System.out.println(savedReq.getId());
			System.out.println(savedReq.getUserid());
			System.out.println(savedReq.getId());
			System.out.println(savedReq.getUserid());
			Attendance newAtt = new Attendance();
			newAtt.setUserid(id);
			newAtt.setDate(date);
			newAtt.setRequestid(savedReq.getId()); // ← 自動採番IDをセット
			attendancedao.save(newAtt);
		}
	}

}
