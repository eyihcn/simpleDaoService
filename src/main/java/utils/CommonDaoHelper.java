package utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
 
 public class CommonDaoHelper {
	public static final String DESC = "-1";
	public static final String ASC = "1";
	public static final String PLUS_REPLACE_TOKEN = "TOMTOP___PLUS____REPLACE___TOKEN";
	public static final Integer PAGE_SIZE = Integer.valueOf(20);
	public static final Integer PAGE_NUMBER = Integer.valueOf(1);
	public static final Integer MAX_PAGE_SIZE = Integer.valueOf(5000);
	public static final String WHERE = "$where";
	public static final String OR = "$or";
	public static final String AND = "$and";
	public static final String GE = "$ge";
	public static final String LE = "$le";
	public static final String GT = "$gt";
	public static final String LT = "$lt";
	public static final String IN = "$in";
	public static final String NOT_IN = "$not_in";
	public static final String NE = "$ne";
	public static final String LIKE = "$like";
	public static final String LEFT_LIKE = "$left_like";
	public static final String RIGHT_LIKE = "$right_like";
	public static final String NOT_LIKE = "$not_like";
	public static final String NOT_LEFT_LIKE = "$not_left_like";
	public static final String NOT_RIGHT_LIKE = "$not_right_like";
	public static final String NULL = "$null";
	public static final String NOT_NULL = "$not_null";

	public static String getRequestSortField(Map<String, Object> requestArgs) {
		String sortField = "id";
		HashMap<String, Object> sort = (HashMap) requestArgs.get("sort");
		if (null != sort) {
			Iterator localIterator = sort.keySet().iterator();
			if (localIterator.hasNext()) {
				String key = (String) localIterator.next();
				sortField = key;
			}
     }

		return sortField;
   }
   
	public static String getRequestSortDirection(Map<String, Object> requestArgs) {
		String sortDirection = "-1";
		HashMap<String, Object> sort = (HashMap) requestArgs.get("sort");
		if (null != sort) {
			Iterator localIterator = sort.keySet().iterator();
			if (localIterator.hasNext()) {
				String key = (String) localIterator.next();
				sortDirection = sort.get(key).toString();
			}
		}
 
     return sortDirection;
   }
   
	public static Integer getRequestPageSize(Map<String, Object> requestArgs) {
		Integer pageSize = MAX_PAGE_SIZE;
		HashMap<String, Object> pagination = (HashMap) requestArgs.get("pagination");
		if ((null != pagination) && (null != pagination.get("pageSize")) && (org.apache.commons.lang3.StringUtils.isNumeric(pagination.get("pageSize").toString()))) {
			pageSize = new Integer(pagination.get("pageSize").toString());
		}
		if (pageSize.intValue() > MAX_PAGE_SIZE.intValue()) {
			pageSize = MAX_PAGE_SIZE;
		}

		return pageSize;
   }
   
	public static Integer getRequestPageNumber(Map<String, Object> requestArgs) {
		Integer pageNumber = PAGE_NUMBER;
		HashMap<String, Object> pagination = (HashMap) requestArgs.get("pagination");
		if ((null != pagination) && (null != pagination.get("pageNumber")) && (org.apache.commons.lang3.StringUtils.isNumeric(pagination.get("pageNumber").toString()))) {
			pageNumber = new Integer(pagination.get("pageNumber").toString());
		}

		return pageNumber;
   }
   
	public static List<String> getRequestFields(Map<String, Object> requestArgs) {
		List<String> fields = new ArrayList();
		if (null != requestArgs.get("fields")) {
			fields = (List) requestArgs.get("fields");
		}

		return fields;
   }
 }

