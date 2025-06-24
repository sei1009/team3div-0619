package com.example.demo.controller;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
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

	@GetMapping("/request/{id}")
	public Request requestreturn(@PathVariable Long id) {
		return requestdao.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "リクエストが見つかりません"));
	}

	@PostMapping("/attendance/{id}")
	public Attendance attendancereturn(@RequestBody Map<String, String> dateMap, @PathVariable long id) {
		String stdate = dateMap.get("date");
		LocalDate date = LocalDate.parse(stdate);

		return attendancedao.findByUsersIdAndDate(id, date);
	}

	@PostMapping("/attendance/clockin/{id}")
    @Transactional // トランザクションを追加
    public Attendance clockIn(@PathVariable long id, @RequestBody Map<String, String> body) {
        String timeStr = body.get("time");
        String dateStr = body.get("date");
        System.out.println("受け取ったJSON: " + body);

        LocalDate date = LocalDate.parse(dateStr);

        // まず、対象のUsersエンティティを取得
        Users user = usersdao.findById(id)
                             .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));

        // 該当ユーザーのその日の勤怠記録を探す
        Attendance att = attendancedao.findByUsersIdAndDate(id, date);

        if (att == null) {
            // データがなければ新規作成
            att = new Attendance();
            // IDはデータベースが自動生成するので、手動でセットしない！
            att.setDate(date);

            // ★最も重要な修正点: 双方向の関連付けを設定
            att.setUsers(user); // Attendance側からUsersへの参照を設定
            if (user.getAttendances() == null) {
                user.setAttendances(new ArrayList<>()); // リストがnullの場合の初期化
            }
            user.getAttendances().add(att); // Users側からAttendanceへの参照を追加
        }
        LocalTime sttime = LocalTime.parse(timeStr);
        att.setStart_time(sttime);

        // Usersエンティティを保存することで、@OneToMany(cascade = CascadeType.ALL) の設定により
        // 新しいAttendanceも自動的に保存されます。
        usersdao.save(user);

        // もし、更新されたAttendanceオブジェクト自体を返す必要がある場合は、
        // user.getAttendances() から該当するものを探すか、
        // 最終的に attendancedao.save(att) を呼び出しても良いですが、
        // 上記のカスケード設定があれば通常は不要で、単にattを返せば良いです。
        // return attendancedao.save(att); // これでも動くが、重複保存の可能性があるため注意
        return att; // usersDao.save(user) で既に永続化されているため、attをそのまま返せるはず
    }

	@PostMapping("/attendance/clockout/{id}")
    @Transactional // トランザクションを追加
    public Attendance clockOut(@PathVariable long id, @RequestBody Map<String, String> body) {
        String timeStr = body.get("time");
        String dateStr = body.get("date");

        LocalDate date = LocalDate.parse(dateStr);
        
        // Usersエンティティの存在を確認することが推奨されます
        usersdao.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ユーザーが見つかりません"));

        Attendance att = attendancedao.findByUsersIdAndDate(id, date);

        if (att == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "出勤データが存在しません。");
        }
        LocalTime entime = LocalTime.parse(timeStr);

        att.setEnd_time(entime);
        return attendancedao.save(att); // 既存レコードの更新なのでこれでOK
    }

}
