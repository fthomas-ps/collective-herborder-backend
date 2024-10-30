package de.ydsgermany.herborder.global;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {EntityNotFoundException.class})
    protected ResponseEntity<Object> handleEntityNotFound(
        HttpServletRequest request,
        EntityNotFoundException exception) {
        return ResponseEntity
            .notFound()
            .build();
    }

    @ExceptionHandler(value = {ValidationException.class})
    protected ResponseEntity<Object> handleValidationException(
        HttpServletRequest request,
        ValidationException exception) {
        if (useProblemResponse(request)) {
            ProblemDetail problem = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
            return ResponseEntity
                .badRequest()
                .body(problem);
        }
        return ResponseEntity
            .badRequest()
            .body(exception.getMessage());
    }

    private boolean useProblemResponse(HttpServletRequest request) {
        return getAcceptedHeaderValues(request)
            .contains(MediaType.APPLICATION_PROBLEM_JSON_VALUE);
    }

    private List<String> getAcceptedHeaderValues(HttpServletRequest request) {
        return Optional.ofNullable(request.getHeaders(HttpHeaders.ACCEPT))
            .map(Collections::list)
            .orElse(new ArrayList<>())
            .stream()
            .flatMap(value -> Stream.of(StringUtils.split(value, ",")))
            .toList();
    }

}
