package depavlo.ui.rest.v1.model.response;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.http.HttpStatus;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * The Class that represent "standard" InfoResponse.
 */
@Data
@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InfoResponse {

	/** The Http status. */
	private HttpStatus status;

	/** The time stamp when it happened. */
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "dd-MM-yyyy HH:mm:ss")
	private LocalDateTime timestamp;

	/** The base message. */
	private String message;

	/** The debug message. */
	private String debugMessage;

	/** The list of additional information. */
	private List<AbstractSubInfoResponse> subInfos;

	/**
	 * Instantiates a new info response.
	 */
	public InfoResponse() {
		timestamp = LocalDateTime.now();
	}

	/**
	 * Instantiates a new info response.
	 *
	 * @param status the HttpStatus
	 */
	public InfoResponse(HttpStatus status) {
		this();
		this.status = status;
	}

	/**
	 * Instantiates a new info response.
	 *
	 * @param status the HttpStatus
	 * @param ex     the Throwable
	 */
	public InfoResponse(HttpStatus status, Throwable ex) {
		this();
		this.status = status;
		this.message = "There is no detailed information";
		this.debugMessage = ex.getLocalizedMessage();
	}

	/**
	 * Instantiates a new info response.
	 *
	 * @param status  the HttpStatus
	 * @param message the String
	 * @param ex      the Throwable
	 */
	public InfoResponse(HttpStatus status, String message, Throwable ex) {
		this();
		this.status = status;
		this.message = message;
		this.debugMessage = ex.getLocalizedMessage();
	}

	/**
	 * Instantiates a new info response.
	 *
	 * @param status       the HttpStatus
	 * @param message      the String
	 * @param debugMessage the String
	 */
	public InfoResponse(HttpStatus status, String message, String debugMessage) {
		this();
		this.status = status;
		this.message = message;
		this.debugMessage = debugMessage;
	}

	/**
	 * Adds the sub info.
	 *
	 * @param info the AbstractSubInfoResponse
	 */
	public void addSubInfo(AbstractSubInfoResponse info) {
		if (subInfos == null) {
			subInfos = new ArrayList<AbstractSubInfoResponse>();
		}
		subInfos.add(info);
	}
}
