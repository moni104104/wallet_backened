package com.icsd.dto.common;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data

public class ApiResponse {

	

 	private Integer code;
 	
 	
 	private String message;
 	@JsonFormat(shape=JsonFormat.Shape.STRING,pattern="dd-MM-yyyy hh:mm:ss")
 	private LocalDateTime timestamp;
 	public ApiResponse()
 	{
 		this.timestamp=LocalDateTime.now();
 	}
	
	private Object data;
	public ApiResponse(Integer code, String message, Object data) {
		
		this();
		this.code = code;
		this.message = message;
		this.data = data;
	}
	public ApiResponse(Integer code, String message) {
		
		this();
		this.code = code;
		this.message = message;
	}
	
	



	
	
}
