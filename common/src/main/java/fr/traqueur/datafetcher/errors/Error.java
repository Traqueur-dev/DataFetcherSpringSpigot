package fr.traqueur.datafetcher.errors;

import org.springframework.http.HttpStatus;

public class Error {

   private HttpStatus status;
   private String message;
   private String debugMessage;

    public Error(HttpStatus status, String message, Throwable ex) {
       this.status = status;
       this.message = message;
       this.debugMessage = ex.getLocalizedMessage();
   }

    public HttpStatus getStatus() {
         return status;
    }

    public String getMessage() {
         return message;
    }

    public String getDebugMessage() {
         return debugMessage;
    }
}