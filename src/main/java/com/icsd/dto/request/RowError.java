package com.icsd.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor

public class RowError {
	private int rowNumber;
	private String errorMessage;

	public RowError(int rowNumber, String errorMessage) {
		this.rowNumber = rowNumber;
		this.errorMessage = errorMessage;
	}

}
