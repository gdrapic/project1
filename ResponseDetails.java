
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;


public class ResponseDetails<T>
{

	public enum RESP_KEY
	{
		apiResponse, responseDetails
	};

	public enum CATEGORY
	{
		SUCCESS, ERROR, WARNING,INFO
	};

	public enum COMPONENT
	{
		ALL, RSSSERV, USERPROFILESRV
	};

	public enum CODE
	{

		SUCCESS(CATEGORY.SUCCESS, COMPONENT.ALL, 200, "Success"),

		//RSSSERV 
		//Errors:...
		RSSSERV_ERR_GENERAL(CATEGORY.ERROR, COMPONENT.RSSSERV, 1000, "General Error"), 
		RSSSERV_ERR_REQUIRED(CATEGORY.ERROR,COMPONENT.RSSSERV, 1010, "Required fields are missing"), 
		RSSSERV_ERR_AUTHORIZATION(CATEGORY.ERROR,COMPONENT.RSSSERV, 1020, "Not Authorized"), 
		RSSSERV_ERR_SOURCE_OWNERSHIP(CATEGORY.ERROR,COMPONENT.RSSSERV, 1030, "Source Ownership Info is missing"),
		RSSSERV_ERR_FILE_TYPE_INVALID(CATEGORY.ERROR,COMPONENT.RSSSERV, 1040, "Attached File Type is invalid"),
		//Warnings
		RSSSERV_WARN_GENERAL(CATEGORY.WARNING, COMPONENT.RSSSERV, 1500, "General Warning"), 
		RSSSERV_WARN_NOTIFICATION_FAILURE(CATEGORY.WARNING, COMPONENT.RSSSERV, 1510, "Failed to send notification"), 
		RSSSERV_RACE_CONDITION(CATEGORY.ERROR, COMPONENT.RSSSERV, 1600, "Action by different users at the same time"),
		RSSSERV_INVALID_DOMAIN(CATEGORY.ERROR, COMPONENT.RSSSERV, 1610, "Invalid Domain"),
		//TODO: keep adding codes here..

		//USERPROFILESRV
		//Errors:...
		USERPROFILESRV_ERR_GENERAL(CATEGORY.ERROR, COMPONENT.USERPROFILESRV, 2000, "General Error"), 
		USERPROFILESRV_ERR_SUPPPORT(CATEGORY.ERROR,COMPONENT.USERPROFILESRV, 2001, "Unknow Exception,Support Required"), 
		
		USERPROFILESRV_ERR_ADD_POLICY_FAILURE(CATEGORY.ERROR,COMPONENT.USERPROFILESRV, 2010, "Failed to add policy"),
		USERPROFILESRV_ERR_REMOVE_POLICY_FAILURE(CATEGORY.ERROR,COMPONENT.USERPROFILESRV, 2011, "Failed to remove policy"), 


		public final CATEGORY category;
		public final COMPONENT component;
		public final Integer code;
		public final String description;

		private CODE(CATEGORY category, COMPONENT component, Integer code, String description)
		{
			this.category = category;
			this.component = component;
			this.code = code;
			this.description = description;
		}

	};

	@JsonProperty("responseCode")
	private Integer responseCode;

	@JsonProperty("category")
	private CATEGORY category;

	@JsonProperty("component")
	private COMPONENT component;

	@JsonProperty("description")
	private String description;

	@JsonProperty("details")
	private String details;

	@JsonProperty("apiResponse")
	private T apiResponse;

	public ResponseDetails(CODE code)
	{
		this(code, null);
	}

	public ResponseDetails(@JsonProperty("responseCode") Integer responseCode,
			@JsonProperty("category") CATEGORY category, @JsonProperty("component") COMPONENT component,
			@JsonProperty("description") String description, @JsonProperty("details") String details,
			@JsonProperty("apiResponse") T apiResponse)
	{
		super();
		this.responseCode = responseCode;
		this.category = category;
		this.component = component;
		this.description = description;
		this.details = details;
		this.apiResponse = apiResponse;
	}

	public ResponseDetails(CODE code, String details)
	{
		this.responseCode = code.code;
		this.category = code.category;
		this.component = code.component;
		this.description = code.description;
		this.details = details;
	}

	@JsonProperty("responseCode")
	public Integer getResponseCode()
	{

		return responseCode;
	}

	@JsonIgnore
	public static CODE getCode(Integer responseCode) throws Exception
	{

		CODE[] codes = CODE.values();
		for ( int i = 0 ; i < codes.length ; i++ )
		{
			if ( codes[i].code.intValue() == responseCode ){
				return codes[i];
			}
				
		}
		throw new Exception("Invalid responseCode: " + responseCode);
	}

	@JsonProperty("category")
	public CATEGORY getCategory()
	{

		return category;
	}

	@JsonProperty("component")
	public COMPONENT getComponent()
	{

		return component;
	}

	@JsonProperty("description")
	public String getDescription()
	{

		return description;
	}

	@JsonProperty("details")
	public String getDetails()
	{

		return details;
	}

	@JsonProperty("apiResponse")
	public T getApiResponse()
	{

		return apiResponse;
	}

	@JsonIgnore
	public ResponseEntity<ResponseDetails> getResponseEntity()
	{

		return getResponseEntity(null, HttpStatus.OK);
	}

	@JsonIgnore
	public ResponseEntity<ResponseDetails> getResponseEntity(T apiResponse)
	{

		return getResponseEntity(apiResponse, HttpStatus.OK);
	}

	@JsonIgnore
	public ResponseEntity<ResponseDetails> getResponseEntity(T apiResponse, HttpStatus httpStatus)
	{

		this.apiResponse = apiResponse;

		return new ResponseEntity<ResponseDetails>(this, httpStatus);
	}

	@Override
	public String toString()
	{

		return "ResponseDetails [responseCode=" + responseCode + ", category=" + category + ", component=" + component
				+ ", description=" + description + ", details=" + details + "]";
	}

}
