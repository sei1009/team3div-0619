package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

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
		// 有給申請が 0 → 1 になった場合
	    if (updated.getPaid() == 1 && existing.getPaid() == 0) {
	    	//Usersから有給残日数を取得
	        Users user = usersdao.findById(existing.getUserid())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));
	        //有給残日数の値が0以下か確認
	        if (user.getPaidDate() <= 0) {
	            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "有給残日数がありません。");
	        }

	        user.setPaidDate(user.getPaidDate() - 1); // 1日減らす
	        usersdao.save(user);
	    }
	    // 有給申請を取り消す（1 → 0）
	    if (updated.getPaid() == 0 && existing.getPaid() == 1) {
	    	//Usersから有給残日数を取得
	        Users user = usersdao.findById(existing.getUserid())
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));

	        user.setPaidDate(user.getPaidDate() + 1); // 1日戻す
	        usersdao.save(user);
	    }
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

	@GetMapping("/attendance/{id}")
	public Attendance attendancereturn(@PathVariable Long id) {
		return attendancedao.findByRequestid(id)
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

			Attendance newAtt = new Attendance();
			newAtt.setUserid(id);
			newAtt.setDate(date);
			newAtt.setRequestid(savedReq.getId()); // ← 自動採番IDをセット
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

		// DBから該当範囲の出退勤記録を取得
		List<Attendance> list = attendancedao.findByUseridAndDateBetween(id, start, end);
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
	
	@GetMapping("/request/user/details/{userId}")
	public List<Map<String, Object>> getRequestsWithAttendance(@PathVariable Long userId) {
	    List<Request> requests = requestdao.findByUserid(userId);
	    List<Map<String, Object>> result = new ArrayList<>();

	    for (Request req : requests) {
	        Map<String, Object> map = new HashMap<>();

	        List<String> contents = new ArrayList<>();
	        if (req.getPaid() == 1) contents.add("有給");
	        if (req.getEarly() == 1) contents.add("早退");
	        if (req.getAbsence() == 1) contents.add("欠勤");
	        if (req.getLate() == 1) contents.add("遅刻");
	        if (contents.isEmpty()) {
	            // 申請内容がないのでこのリクエストはスキップ
	            continue;
	        }
	        
	        String status = "申請中";
	        int maxStatus = Math.max(
	            Math.max(req.getPaid_app(), req.getEarly_app()),
	            Math.max(req.getAbsence_app(), req.getLate_app())
	        );
	        if (maxStatus == 1) status = "承認";
	        else if (maxStatus == 2) status = "却下";

	        Optional<Attendance> attOpt = attendancedao.findByRequestid(req.getId());

	        map.put("requestId", req.getId());
	        map.put("content", String.join(", ", contents));
	        map.put("status", status);
	        map.put("date", attOpt.map(att -> att.getDate().toString()).orElse("日付なし"));

	        result.add(map);
	    }
	    //日付けの昇順でソート
	    result.sort(Comparator.comparing(m -> {
	        String dateStr = (String) m.get("date");
	        try {
	            return LocalDate.parse(dateStr);
	        } catch (Exception e) {
	            return LocalDate.MAX; // パース失敗（例: "日付なし"）は末尾に
	        }
	    }));

	    return result;
	}
	
	// ✅ 新規追加：ユーザーIDから有給残日数を取得するエンドポイント
	@GetMapping("/user/{userId}/paidDate")
	public Map<String, Object> getPaidDateByUserId(@PathVariable Long userId) {
	    Users user = usersdao.findById(userId)
	            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));

	    Map<String, Object> response = new HashMap<>();
	    response.put("paidDate", user.getPaidDate());
	    return response;
	}

}
