package com.icsd.exception;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import com.icsd.dto.common.ApiResponse;
import com.icsd.dto.common.messages;

import lombok.extern.slf4j.Slf4j;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

	//query - if i comment this function - than op is different incase i call deposit amount in account function - request dto- for customer id =0- i.e customer id should not be 0 
	//i want to check the ip is same but - op is diff - for commented and uncommented code.
	
	
	@ExceptionHandler(Exception.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiResponse> handleException(Exception ex,WebRequest webRequest){
	
		ApiResponse apiResponse = new ApiResponse(400,ex.getMessage());
		return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.BAD_REQUEST);
		
	}
	
	@ExceptionHandler(IcsdException.class)
	@ResponseStatus(code = HttpStatus.BAD_REQUEST)
	public ResponseEntity<ApiResponse> handleIcsdException(IcsdException ex){
		ApiResponse apiResponse = new ApiResponse(400,"something went wrong",ex.getMessage());
		return new ResponseEntity<ApiResponse>(apiResponse,HttpStatus.BAD_REQUEST);
	}
	

    @ExceptionHandler(IllegalArgumentException.class)
   public ResponseEntity<ApiResponse> handleIllegalArgument(IllegalArgumentException ex) {
        ApiResponse response = new ApiResponse(HttpStatus.BAD_REQUEST.value(),ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
   }
	
    @ExceptionHandler(InvalidExcelDataException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidExcelData(InvalidExcelDataException ex) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.BAD_REQUEST.value());
        response.put("message", "Excel data validation failed");
        response.put("errors", ex.getErrors()); 
        response.put("timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }


    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(MethodArgumentNotValidException ex) {
        Map<String, String> fieldErrors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error -> {
            fieldErrors.put(error.getField(), error.getDefaultMessage());
        });

        Map<String, Object> response = new HashMap<>();
        response.put("code", HttpStatus.BAD_REQUEST.value());
        response.put("message",messages.DATA_VALIDATION);
        response.put("errors", fieldErrors);
        response.put("timestamp", java.time.LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss")));

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(EntityAlreadyExistException.class)
    public ResponseEntity<ApiResponse> handleEntityAlreadyExistsException(EntityAlreadyExistException ex,
                                                                          WebRequest webRequest) 
    {
        return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    }
    

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<ApiResponse> handleResourecNotFoundExecption(ResourceNotFoundException ex, WebRequest webRequest) {
    	
        return new ResponseEntity<>(new ApiResponse(HttpStatus.NOT_ACCEPTABLE.value(), ex.getMessage()),
                HttpStatus.BAD_REQUEST);
    } 
}

