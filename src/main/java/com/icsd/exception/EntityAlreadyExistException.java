package com.icsd.exception;

import java.util.List;

import com.icsd.dto.common.messages;

public class EntityAlreadyExistException extends RuntimeException {
	 private List<String> duplicateEmails;
	public EntityAlreadyExistException() {

	}

	public EntityAlreadyExistException(String msg) {
		super(msg);
	}

	
	
	public EntityAlreadyExistException(String msg, List<String> duplicateEmails) {
		super(messages.DUPLICATE_EMAILS + duplicateEmails);
        this.duplicateEmails = duplicateEmails;
    }

}
