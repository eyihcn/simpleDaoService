package client;

public enum RequestMethodName {

	SAVE("save"),UPDATE("update"),SAVE_OR_UPDATE("saveOrUpdate"),DELETE("delete"),DELETE_BY_ID("deleteById"),COUNTS("counts"),FIND_BY_ID("findById"),
	FIND_ONE("findOne"),FIND_LIST("findList"),FIND_COLLECTION("findCollection");
	
	private String methodName;
	
	private RequestMethodName() {
	}

	private RequestMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}
	
}
