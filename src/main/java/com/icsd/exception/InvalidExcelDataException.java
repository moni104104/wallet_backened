package com.icsd.exception;

import java.util.List;

import com.icsd.dto.request.RowError;

public class InvalidExcelDataException extends RuntimeException {

	private final List<RowError> errors;

	public InvalidExcelDataException(List<RowError> errors) {
		super("Validation errors in Excel data" + errors);
		this.errors = errors;
	}

	public InvalidExcelDataException() {
		this.errors = null;

	}

	public List<RowError> getErrors() {
		return errors;
	}
}
