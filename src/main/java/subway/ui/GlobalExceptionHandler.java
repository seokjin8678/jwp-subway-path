package subway.ui;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import subway.dto.response.Response;
import subway.exception.line.DuplicateLineException;
import subway.exception.line.IllegalSectionException;
import subway.exception.line.LineNotFoundException;
import subway.exception.station.DuplicateStationException;
import subway.exception.station.StationNotFoundException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Response> handleRuntimeException(RuntimeException e) {
        log.error(e.getMessage(), e);
        return Response.internalServerError()
                .message("서버에 오류가 발생했습니다.")
                .build();
    }

    @ExceptionHandler(DuplicateLineException.class)
    public ResponseEntity<Response> handleDuplicateLineException(DuplicateLineException e) {
        return Response.badRequest()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(LineNotFoundException.class)
    public ResponseEntity<Response> handleLineNotFoundException(LineNotFoundException e) {
        return Response.badRequest()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(DuplicateStationException.class)
    public ResponseEntity<Response> handleDuplicateStationException(DuplicateStationException e) {
        return Response.badRequest()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<Response> handleStationNotFoundException(StationNotFoundException e) {
        return Response.badRequest()
                .message(e.getMessage())
                .build();
    }

    @ExceptionHandler(IllegalSectionException.class)
    public ResponseEntity<Response> handleIllegalSectionException(IllegalSectionException e) {
        return Response.badRequest()
                .message(e.getMessage())
                .build();
    }
}
