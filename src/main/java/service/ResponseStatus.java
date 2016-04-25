package service;

public enum ResponseStatus {

	SUCCESS("SUCCESS", 200), ERROR("ERROR", 400), SERVER_ERROR("SERVER ERROR", 500), UNAUTHORIZED("UNAUTHORIZED", 401);

	private String description;
	private Integer code;
	
	private ResponseStatus() {
	}
	
	private ResponseStatus(String description, Integer code) {
		this.description = description;
		this.code = code;
	}

	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getCode() {
		return code;
	}
	public void setCode(Integer code) {
		this.code = code;
	}
	
	
}
