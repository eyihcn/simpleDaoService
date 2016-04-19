package service;

public class ServiceResponse {

	private Double version = Double.valueOf(1.0D);
	private Integer code = ServiceResponseCode.SUCCESS;
	private String description = "Success";
	private Object result;

	public Double getVersion() {
		return version;
	}

	public void setVersion(Double version) {
		this.version = version;
	}

	public Integer getCode() {
		return code;
	}

	public void setCode(Integer code) {
		this.code = code;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
	}

}


