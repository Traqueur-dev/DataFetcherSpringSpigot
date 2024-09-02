package fr.traqueur.datafetcher.api.errors;

import fr.traqueur.datafetcher.exceptions.PlayerAlreadyExistException;
import fr.traqueur.datafetcher.errors.Error;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
public class RestExceptionHandler extends ResponseEntityExceptionHandler {


    @ExceptionHandler(PlayerAlreadyExistException.class)
    protected ResponseEntity<Object> handleEntityAlreadyExist(PlayerAlreadyExistException ex) {
        Error error = new Error(HttpStatus.CONFLICT, ex.getMessage(), ex);
        return buildResponseEntity(error);
    }

    private ResponseEntity<Object> buildResponseEntity(Error error) {
        return new ResponseEntity<>(error, error.getStatus());
    }
}
