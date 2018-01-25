
import org.springframework.http.ResponseEntity;

public class ExceptionResponse extends Exception
{


	private static final long serialVersionUID = 2997157685152559395L;

	private ResponseDetails responseDetails;

	public ExceptionResponse(String message, ResponseDetails responseDetails)
	{
		super(message.concat("\n").concat(responseDetails.toString()));
		this.responseDetails = responseDetails;
	}

	public ExceptionResponse(ResponseDetails responseDetails)
	{
		super(responseDetails.toString());
		this.responseDetails = responseDetails;
	}

	public ResponseDetails getResponseDetails()
	{

		return responseDetails;
	}

	public void setResponseDetails(ResponseDetails responseDetails)
	{

		this.responseDetails = responseDetails;
	}

	public ResponseEntity<ResponseDetails> getResponseEntity()
	{

		return responseDetails.getResponseEntity();
	}

}
