package utils;

import java.util.LinkedHashMap;
import java.util.Map;

public class ServiceSorterHelper {
	public static final String SORT = "sort";
	public static final String DESC = "desc";

	public static Map<String, Object> build(String key, Object value) {
		Map<String, Object> _innerSortion = new LinkedHashMap();
		Integer direction = Integer.valueOf(1);
		if ((value.equals("desc")) || (value.equals(Integer.valueOf(-1)))) {
			direction = Integer.valueOf(-1);
		}
		_innerSortion.put(key, direction);

		return _innerSortion;
	}
}
