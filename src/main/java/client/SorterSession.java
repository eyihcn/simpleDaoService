package client;

/*    */ 
/*    */ /*    */ import java.util.HashMap;
/*    */ import java.util.Map;

import org.junit.runner.manipulation.Sorter;
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
/*    */ public class SorterSession
/*    */ {
/*    */   private Sorter sorter;
/* 24 */   private final String SORTER = "sorter";
/*    */   private Map<String, Sorter> sorterMap;
/*    */   
/*    */   public SorterSession(Sorter defaultSorter) {
/*    */     try {
/* 29 */       HttpServletRequest request = ServletActionContext.getRequest();
/* 30 */       String requestUri = request.getRequestURI();
/* 31 */       SessionMap session = new SessionMap();
/*    */       
/* 33 */       if (session.get("sorter") != null) {
/* 34 */         sorterMap = ((HashMap)session.get("sorter"));
/* 35 */         sorter = ((Sorter)sorterMap.get(requestUri));
/* 36 */         if ((null == sorter) && (null != defaultSorter)) {
/* 37 */           sorter = defaultSorter;
/* 38 */           sorterMap.put(requestUri, sorter);
/* 39 */           session.put("sorter", sorterMap);
/*    */         }
/*    */       } else {
/* 42 */         sorterMap = new HashMap();
/* 43 */         sorter = defaultSorter;
/* 44 */         sorterMap.put(requestUri, sorter);
/* 45 */         session.put("sorter", sorterMap);
/*    */       }
/* 47 */       if (("POST".equals(request.getMethod())) && (request.getParameter("sorter-key") != null)) {
/* 48 */         sorter.setKey(request.getParameter("sorter-key"));
/* 49 */         sorter.setDirection(request.getParameter("sorter-direction"));
/* 50 */         sorter.setIndex(new Integer(request.getParameter("sorter-index")).intValue());
/* 51 */         sorterMap.put(requestUri, sorter);
/*    */         
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/*    */ 
/* 58 */         session.put("sorter", sorterMap);
/*    */       }
/*    */     }
/*    */     catch (Exception e) {}
/*    */   }
/*    */   
/*    */   public Sorter getSorter() {
/* 65 */     return sorter;
/*    */   }
/*    */   
/*    */   public void setSorter(Sorter sorter) {
/* 69 */     this.sorter = sorter;
/*    */   }
/*    */   
/*    */   public String getSorterAttribute() {
/* 73 */     return "sorter";
/*    */   }
/*    */ }

/* Location:           D:\maven_repo\com\tomtop\jtomtoperp-system\1.1.4\jtomtoperp-system-1.1.4.jar
 * Qualified Name:     com.tomtop.system.core.session.page.SorterSession
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */