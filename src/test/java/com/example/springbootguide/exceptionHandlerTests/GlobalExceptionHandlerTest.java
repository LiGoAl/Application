package com.example.springbootguide.exceptionHandlerTests;

import com.example.springbootguide.exception.GlobalExceptionHandler;
import com.example.springbootguide.exception.RequestValidationException;
import com.example.springbootguide.exception.ResourceAlreadyOccupiedException;
import com.example.springbootguide.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@WebMvcTest(GlobalExceptionHandler.class)
public class GlobalExceptionHandlerTest {
    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void testHandleValidationExceptions_Success() {
        BindingResult bindingResult = mock(BindingResult.class);
        List<ObjectError> errorList = new ArrayList<>();

        FieldError fieldError1 = new FieldError("employee", "name", "Name can't be empty");
        FieldError fieldError2 = new FieldError("employee", "email", "Email doesn't match the form");

        errorList.add(fieldError1);
        errorList.add(fieldError2);

        when(bindingResult.getAllErrors()).thenReturn(errorList);

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleValidationExceptions(exception);

        Map<String, Object> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
        assertEquals("Name can't be empty", responseBody.get("name"));
        assertEquals("Email doesn't match the form", responseBody.get("email"));
    }

    @Test
    public void testHandleHttpMessageNotReadable_Success() {
        HttpMessageNotReadableException exception = mock(HttpMessageNotReadableException.class);

        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleHttpMessageNotReadable(exception);

        Map<String, Object> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
        assertEquals("Malformed JSON request", responseBody.get("error"));
        assertEquals("Please ensure that your request body is properly structured and valid JSON.", responseBody.get("message"));
    }

    @Test
    public void testHandleResourceNotFound_Success() {
        ResourceNotFoundException exception = new ResourceNotFoundException("Id not found");

        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleResourceNotFound(exception);

        Map<String, Object> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.NOT_FOUND.value(), responseBody.get("status"));
        assertEquals("Resource not found", responseBody.get("error"));
        assertEquals("Id not found", responseBody.get("message"));
    }

    @Test
    public void testHandleResourceAlreadyOccupied_Success() {
        ResourceAlreadyOccupiedException exception = new ResourceAlreadyOccupiedException("Name is occupied");

        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleResourceAlreadyOccupied(exception);

        Map<String, Object> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.CONFLICT.value(), responseBody.get("status"));
        assertEquals("Resource already occupied", responseBody.get("error"));
        assertEquals("Name is occupied", responseBody.get("message"));
    }

    @Test
    public void testHandleRequestValidation_Success() {
        RequestValidationException exception = new RequestValidationException("Name can't be empty");

        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleRequestValidation(exception);

        Map<String, Object> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.BAD_REQUEST.value(), responseBody.get("status"));
        assertEquals("Invalid request", responseBody.get("error"));
        assertEquals("Name can't be empty", responseBody.get("message"));
    }

    @Test
    public void testHandleAllException_Success() {
        Exception exception = new Exception("bruh...");

        ResponseEntity<Map<String, Object>> responseEntity = globalExceptionHandler.handleAllException(exception);

        Map<String, Object> responseBody = responseEntity.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value(), responseBody.get("status"));
        assertEquals("An unexpected error occurred", responseBody.get("error"));
        assertEquals("bruh...", responseBody.get("message"));
    }
}
