package com.example.demo.controller;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.example.demo.entity.Attendance;
import com.example.demo.entity.Request;

import lombok.Data;

@Data
public class CombinedApplicationDto {
	private Long id;
	private LocalDate date;
	private String username;
	private String requestType;
	private String recordType;
	private Long deleteId;

	public CombinedApplicationDto(Attendance attendance) {
		this.id = attendance.getId();
		this.date = attendance.getDate();

		if (attendance.getUsers() != null) {
			this.username = attendance.getUsers().getUsername();
		} else {
			this.username = "不明なユーザー";
		}

		this.deleteId = attendance.getId();
		this.recordType = "attendance";
		this.requestType = "勤怠のみ";

		if (attendance.getRequest() != null) {
			this.recordType = "request";
			this.deleteId = attendance.getRequest().getId();

			List<String> types = new ArrayList<>();
			Request req = attendance.getRequest();
			if (req.getPaid() == 1)
				types.add("有給");
			if (req.getEarly() == 1)
				types.add("早退");
			if (req.getAbsence() == 1)
				types.add("欠勤");
			if (req.getLate() == 1)
				types.add("遅刻");
			this.requestType = types.isEmpty() ? "不明な申請" : String.join(", ", types);
		}
	}
}
