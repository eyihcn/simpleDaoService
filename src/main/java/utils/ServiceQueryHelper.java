package utils;

/*    */ 
/*    */ import java.util.LinkedHashMap;
/*    */ import java.util.Map;
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ public class ServiceQueryHelper
/*    */ {
/*    */   public static final String OR = "$or";
/*    */   public static final String GE = "$ge";
/*    */   public static final String LE = "$le";
/*    */   public static final String GT = "$gt";
/*    */   public static final String LT = "$lt";
/*    */   public static final String IN = "$in";
/*    */   public static final String NE = "$ne";
/*    */   public static final String LIKE = "$like";
/*    */   public static final String LEFT_LIKE = "$left_like";
/*    */   public static final String RIGHT_LIKE = "$right_like";
/*    */   public static final String NOT_LIKE = "$not_like";
/*    */   public static final String NOT_LEFT_LIKE = "$not_left_like";
/*    */   public static final String NOT_RIGHT_LIKE = "$not_right_like";
/*    */   public static final String NULL = "$null";
/*    */   public static final String NOT_NULL = "$not_null";
/*    */   public static final String NOT_IN = "$not_in";
/*    */   public static final String WHERE = "$where";
/*    */   
/*    */   public static Map<String, Object> and(Map<String, Object> query, Map<String, Object> andQuery)
/*    */   {
/* 49 */     Map<String, Object> newQuery = query;
/* 50 */     for (String key : andQuery.keySet()) {
/* 51 */       newQuery = and(newQuery, key, andQuery.get(key));
/*    */     }
/*    */     
/* 54 */     return newQuery;
/*    */   }
/*    */   
/*    */   public static Map<String, Object> and(Map<String, Object> query, String key, Object value) {
/* 58 */     return and(query, key, value, null);
/*    */   }
/*    */   
/*    */   public static Map<String, Object> and(Map<String, Object> query, String key, Object value, String compare) {
/* 62 */     if (null == query) {
/* 63 */       query = new LinkedHashMap();
/*    */     }
/* 65 */     if (null == compare) {
/* 66 */       query.put(key, value);
/*    */     } else {
/* 68 */       Map<String, Object> _innerQuery = new LinkedHashMap();
/* 69 */       if (query.containsKey(key)) {
/* 70 */         Object _iq = query.get(key);
/* 71 */         if ((_iq instanceof Map)) {
/* 72 */           _innerQuery = (Map)_iq;
/*    */         } else {
/* 74 */           _innerQuery = new LinkedHashMap();
/*    */         }
/*    */       }
/* 77 */       _innerQuery.put(compare, value);
/* 78 */       query.put(key, _innerQuery);
/*    */     }
/*    */     
/* 81 */     return query;
/*    */   }
/*    */   
/*    */   public static Map<String, Object> or(Map<String, Object> query, String key, Object value) {
/* 85 */     return or(query, key, value, null);
/*    */   }
/*    */   
/*    */   public static Map<String, Object> or(Map<String, Object> query, String key, Object value, String compare) {
/* 89 */     if (null == query) {
/* 90 */       query = new LinkedHashMap();
/*    */     }
/* 92 */     Map<String, Object> orQuery = (Map)query.get("$or");
/* 93 */     if (null == orQuery) {
/* 94 */       orQuery = new LinkedHashMap();
/*    */     }
/* 96 */     query.put("$or", and(orQuery, key, value, compare));
/*    */     
/* 98 */     return query;
/*    */   }
/*    */ }

/* Location:           D:\maven_repo\com\tomtop\jtomtoperp-system\1.1.4\jtomtoperp-system-1.1.4.jar
 * Qualified Name:     com.tomtop.system.libraries.util.ServiceQueryHelper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */