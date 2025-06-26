package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Request;
import com.example.demo.entity.Users;

import lombok.Data;

@Data
public class CombinedApplicationDto {
	private Long id;
	private LocalDate date;
	private String username;
	private String requestType;
	private String requestStatus;
	private String reason;
	private String recordType;
	private Long deleteId;

	public CombinedApplicationDto(Attendance attendance, Users user, Request request) {
		this.username = (user != null) ? user.getUsername() : "不明なユーザー";

		if (request != null) {
			this.id = request.getId();
			this.deleteId = request.getId(); // Requestの削除用ID
			this.recordType = "request";
			this.date = attendance != null && attendance.getDate() != null ? attendance.getDate() : null;
			this.reason = request.getReason();
			// requestType の設定（applicationsが空の場合も考慮）
			List<String> types = new ArrayList<>();
			if (request.getPaid() == 1)
				types.add("有給");
			if (request.getEarly() == 1)
				types.add("早退");
			if (request.getAbsence() == 1)
				types.add("欠勤");
			if (request.getLate() == 1)
				types.add("遅刻");
			this.requestType = types.isEmpty() ? "不明な申請" : String.join(", ", types);
			String karistatus = "申請中";
			if (request.getPaid_app() == 1 || request.getLate_app() == 1 || request.getAbsence_app() == 1
					|| request.getEarly_app() == 1) {
				karistatus = "承認済み";
			}
			if (request.getPaid_app() == 2 || request.getLate_app() == 2 || request.getAbsence_app() == 2
					|| request.getEarly_app() == 2) {
				karistatus = "却下済み";
			}
			this.requestStatus = karistatus;

		} else if (attendance != null) {
			// 純粋な勤怠レコードの場合（この部分は変更なしでOK）
			this.id = attendance.getId();
			this.deleteId = attendance.getId();
			this.recordType = "attendance";
			this.requestType = "勤怠のみ";
			this.reason = "勤怠のみ";
			this.date = attendance.getDate() != null ? attendance.getDate() : null;
		} else {
			// どちらのデータもない、または初期化されない場合
			this.id = null;
			this.deleteId = null;
			this.recordType = "不明";
			this.requestType = "データなし";
			this.reason = "データなし";
			this.date = null;
		}

	}

}
