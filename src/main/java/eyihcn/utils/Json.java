package eyihcn.utils;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;


public class Json
 {
	public static String toJson(Object object) {
		ObjectMapper objectMapper = new ObjectMapper();
		String result = "";
		try {
			objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
			result = objectMapper.writeValueAsString(object);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return result;
	}

	public static <T> T fromJson(String requestStr, Class<T> clazz) {
		ObjectMapper objectMapper = new ObjectMapper();
		T object = null;
		try {
			object = objectMapper.readValue(requestStr, clazz);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return object;
	}
}