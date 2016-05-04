package client;

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
/*    */ public class ServicePaginationHelper
/*    */ {
/*    */   public static final String PAGINATION = "pagination";
/*    */   public static final String PAGE_SIZE = "pageSize";
/*    */   public static final String PAGE_NUMBER = "pageNumber";
/*    */   
/*    */   public static Map<String, Object> build(Integer pageSize, Integer pageNumber)
/*    */   {
/* 34 */     Map<String, Object> _innerPagination = new LinkedHashMap();
/* 35 */     _innerPagination.put("pageSize", pageSize);
/* 36 */     _innerPagination.put("pageNumber", pageNumber);
/*    */     
/* 38 */     return _innerPagination;
/*    */   }
/*    */ }

/* Location:           D:\maven_repo\com\tomtop\jtomtoperp-system\1.1.4\jtomtoperp-system-1.1.4.jar
 * Qualified Name:     com.tomtop.system.libraries.util.ServicePaginationHelper
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */