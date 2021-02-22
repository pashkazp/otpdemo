package depavlo.ui.rest.v1.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.WebRequest;

import depavlo.ui.rest.v1.model.response.InfoResponse;
import io.jsonwebtoken.ExpiredJwtException;
import lombok.extern.slf4j.Slf4j;

@ControllerAdvice
@Slf4j
public class ExpiredJwtExceptionHandler {
	@ResponseBody()
	@ExceptionHandler(ExpiredJwtException.class)
	public ResponseEntity<Object> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
		log.debug("handleExpiredJwtException] - Gets exception: {}", ex.getMessage());

		InfoResponse infoResponse = new InfoResponse(HttpStatus.FORBIDDEN, "Access denied",
				ex.getMessage());

		String headers = request.getHeader(HttpHeaders.ACCEPT);

		MediaType mt;
		if (headers.indexOf(MediaType.APPLICATION_XML_VALUE) == -1) {
			mt = MediaType.APPLICATION_JSON;
		} else {
			mt = MediaType.APPLICATION_XML;
		}
		return ResponseEntity.status(infoResponse.getStatus()).contentType(mt).body(infoResponse);
	}
}
