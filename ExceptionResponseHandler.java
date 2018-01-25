

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;



@ControllerAdvice(assignableTypes =
{ ApiControllerV1.class })
public class ExceptionResponseHandler
{

	//	private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionResponseHandler.class);
	private static final SFSystemLogger sfSystemLogger = new SFSystemLogger();

	@ExceptionHandler(value = ExceptionResponse.class)
	@ResponseStatus(HttpStatus.ACCEPTED)
	protected ResponseEntity<ResponseDetails> handleException(ExceptionResponse eResp, HttpServletRequest request,
			HttpServletResponse response)
	{

		sfSystemLogger.info("apiproxy", //String component
				ApiControllerV1.class.getName(), //String clazz
				LogSeverity.ERROR, //LogSeverity severity
				LogClassification.APIError, //LogClassification classification
				"ResponseDetails:{}, request: <<{}>>", // String format
				eResp.getResponseDetails(), new AppHttpRequestLog(request).toJsonString()//Object argument
		);
		return eResp.getResponseEntity();
	}

}
