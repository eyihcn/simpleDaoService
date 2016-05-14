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
/*    */ public class ServiceSorterHelper
/*    */ {
/*    */   public static final String SORT = "sort";
/*    */   public static final String DESC = "desc";
/*    */   
/*    */   public static Map<String, Object> build(String key, Object value)
/*    */   {
/* 33 */     Map<String, Object> _innerSortion = new LinkedHashMap();
/* 34 */     Integer direction = Integer.valueOf(1);
/* 35 */     if ((value.equals("desc")) || (value.equals(Integer.valueOf(-1)))) {
/* 36 */       direction = Integer.valueOf(-1);
/*    */     }
/* 38 */     _innerSortion.put(key, direction);
/*    */     
/* 40 */     return _innerSortion;
/*    */   }
/*    */ }

/* Location:           D:\maven_repo\com\tomtop\jtomtoperp-system\1.1.4\jtomtoperp-system-1.1.4.jar
 * Qualified Name:     com.tomtop.system.libraries.util.ServiceSorterHelper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */