package utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceQueryHelper {
	public static final String OR = "$or";
	public static final String GE = "$ge";
	public static final String LE = "$le";
	public static final String GT = "$gt";
	public static final String LT = "$lt";
	public static final String IN = "$in";
	public static final String NE = "$ne";
	public static final String LIKE = "$like";
	public static final String LEFT_LIKE = "$left_like";
	public static final String RIGHT_LIKE = "$right_like";
	public static final String NOT_LIKE = "$not_like";
	public static final String NOT_LEFT_LIKE = "$not_left_like";
	public static final String NOT_RIGHT_LIKE = "$not_right_like";
	public static final String NULL = "$null";
	public static final String NOT_NULL = "$not_null";
	public static final String NOT_IN = "$not_in";
	public static final String WHERE = "$where";

	public static Map<String, Object> and(Map<String, Object> query,
			Map<String, Object> andQuery) {
		Map<String, Object> newQuery = query;
		for (String key : andQuery.keySet()) {
			newQuery = and(newQuery, key, andQuery.get(key));
		}

		return newQuery;
	}

	public static Map<String, Object> and(Map<String, Object> query,
			String key, Object value) {
		return and(query, key, value, null);
	}

	public static Map<String, Object> and(Map<String, Object> query,
			String key, Object value, String compare) {
		if (null == query) {
			query = new LinkedHashMap();
		}
		if (null == compare) {
			query.put(key, value);
		} else {
			Map<String, Object> _innerQuery = new LinkedHashMap();
			if (query.containsKey(key)) {
				Object _iq = query.get(key);
				if ((_iq instanceof Map)) {
					_innerQuery = (Map) _iq;
				} else {
					_innerQuery = new LinkedHashMap();
				}
			}
			_innerQuery.put(compare, value);
			query.put(key, _innerQuery);
		}

		return query;
	}

	public static Map<String, Object> or(Map<String, Object> query, String key,
			Object value) {
		return or(query, key, value, null);
	}

	public static Map<String, Object> or(Map<String, Object> query, String key,
			Object value, String compare) {
		if (null == query) {
			query = new LinkedHashMap();
		}
		Map<String, Object> orQuery = (Map) query.get("$or");
		if (null == orQuery) {
			orQuery = new LinkedHashMap();
		}
		query.put("$or", and(orQuery, key, value, compare));

		return query;
	}
}
