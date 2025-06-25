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
	private String recordType;
	private Long deleteId;

	public CombinedApplicationDto(Attendance attendance, Users user, Request request) {
		this.id = attendance.getId();
		this.date = attendance.getDate();
		this.username = (user != null) ? user.getUsername() : "不明なユーザー";
		this.deleteId = attendance.getId();
		this.recordType = "attendance";
		this.requestType = "勤怠のみ";

		if (request != null) {
			this.recordType = "request";
			this.deleteId = request.getId();

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
		}
	}
}
