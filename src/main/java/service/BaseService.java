package service;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

public abstract class BaseService<T>  {

	private static final String SAVE = "save";
	private static final String UPDATE = "update";
	private static final String FIND_ONE = "findOne";
	private static final String DELETE_BY_ID = "deleteById";
	private static final String FIND_LIST = "findList";

	private ServiceResponse response = new ServiceResponse();
	private String request = "";
	private Object result;
	private Integer responseCode = ServiceResponseCode.SUCCESS;
	private String responseDescription = "Success";
	protected final String COLLECTION = "collection";
	protected final String COLLECTION_COUNT = "collectionCount";
	protected final String RESULT = "result";
	protected final String SUCCESS = "success";

	public void _execute() throws Exception {
	}


	@RequestMapping(value = SAVE, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse save(@RequestBody Map<String, Object> request) {
		try {
			if (!daoSave(request)) {
				setResponseCode(ServiceResponseCode.ERROR);
				setResponseDescription(ServiceResponseDescription.ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse();
	}

	/**
	 * 子类必须实现的方法， 由具体的dao提供save实现
	 * 
	 * @param request
	 * @return
	 */
	public abstract boolean daoSave(Map<String, Object> request);

	@RequestMapping(value = UPDATE, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse update(@RequestBody Map<String, Object> request) {
		try {
			if (!daoUpdate(request)) {
				setResponseCode(ServiceResponseCode.ERROR);
				setResponseDescription(ServiceResponseDescription.ERROR);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse();
	}

	public abstract boolean daoUpdate(Map<String, Object> request);

	@RequestMapping(value = FIND_ONE, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse findOne(@RequestBody Map<String, Object> request) {
		try {
			T entity = daoFindOne(request);
			setResult(entity);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse();
	}

	public abstract T daoFindOne(Map<String, Object> request);

	@RequestMapping(value = FIND_LIST, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse findCollection(@RequestBody Map<String, Object> request) {
		try {
			Map<String, Object> collectionInfo = new HashMap<String, Object>();
			collectionInfo.put("collection", daoFindCollectionCount(request));
			collectionInfo.put("collectionCount", daoFindCollectionCount(request));
			setResult(collectionInfo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse();
	}

	public abstract List<T> daoFindCollection(@RequestBody Map<String, Object> request);

	public abstract Long daoFindCollectionCount(@RequestBody Map<String, Object> request);

	@RequestMapping(value = DELETE_BY_ID, method = RequestMethod.POST)
	@ResponseBody
	public ServiceResponse delete(@RequestBody Integer id) {
		try {
			daoDeleteById(id);
			setResult(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return getResponse();
	}

	public abstract boolean daoDeleteById(@RequestBody Integer id);

	public String getJsonData() {
		return Json.toJson(response);
	}

	public ServiceResponse getResponse() {
		return response;
	}

	public void setResponse(ServiceResponse response) {
		this.response = response;
	}

	public Object getResult() {
		return result;
	}

	public void setResult(Object result) {
		this.result = result;
		response.setResult(result);
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer responseCode) {
		this.responseCode = responseCode;
		response.setCode(responseCode);
	}

	public String getResponseDescription() {
		return responseDescription;
	}

	public void setResponseDescription(String responseDescription) {
		this.responseDescription = responseDescription;
		response.setDescription(responseDescription);
	}

	public Map<String, Object> getRequest() {
		return Json.fromJson(request, LinkedHashMap.class);
	}

	public void setRequest(String request) {
		this.request = request;
	}
}
