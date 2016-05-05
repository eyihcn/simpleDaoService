package client;

import java.util.HashMap;

/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ public class FilterSession
/*     */ {
/*  33 */   private final String FILTER = "filter";
/*  34 */   private final String ADVANCED_TAG = "[advanced]";
/*     */   private HashMap<String, Filter> filterMap;
/*     */   private Filter filter;
/*  37 */   private SessionMap session = new SessionMap();
/*     */   
/*     */   public Filter getFilter()
/*     */   {
/*  41 */     HttpServletRequest request = ServletActionContext.getRequest();
/*  42 */     HashMap fm = (HashMap)session.get("filter");
/*  43 */     String requestUri = request.getRequestURI();
/*     */     
/*  45 */     return (Filter)fm.get(requestUri);
/*     */   }
/*     */   
/*     */   public void setFilter(Filter filter) {
/*  49 */     this.filter = filter;
/*     */   }
/*     */   
/*     */   public FilterSession() {
/*     */     try {
/*  54 */       HttpServletRequest request = ServletActionContext.getRequest();
/*  55 */       String requestUri = request.getRequestURI();
/*  56 */       if (session.get("filter") != null) {
/*  57 */         filterMap = ((HashMap)session.get("filter"));
/*  58 */         filter = ((Filter)filterMap.get(requestUri));
/*  59 */         if (null == filter) {
/*  60 */           filter = new Filter();
/*  61 */           filterMap.put(requestUri, filter);
/*     */         }
/*     */       } else {
/*  64 */         filterMap = new HashMap();
/*  65 */         filter = new Filter();
/*  66 */         filterMap.put(requestUri, filter);
/*  67 */         session.put("filter", filterMap);
/*     */       }
/*  69 */       if ("POST".equals(request.getMethod())) {
/*  70 */         ArrayList<Criterion> restrictions = new ArrayList();
/*  71 */         filter.setQuery(new HashMap());
/*  72 */         Map<String, List<Criterion>> criterias = new HashMap();
/*  73 */         Enumeration params = request.getParameterNames();
/*  74 */         String queryString = request.getQueryString();
/*  75 */         String sorterValue = request.getParameter("sorter-key");
/*  76 */         Map conditions = new HashMap();
/*  77 */         while (params.hasMoreElements()) {
/*  78 */           String param = (String)params.nextElement();
/*  79 */           String value = request.getParameter(param);
/*  80 */           if (((queryString == null) || (queryString.indexOf(param + '=') <= -1)) && 
/*     */           
/*     */ 
/*  83 */             (!param.startsWith("pager-")) && 
/*     */             
/*     */ 
/*  86 */             (!param.equals("_")) && 
/*     */             
/*     */ 
/*  89 */             (!param.startsWith("sorter-")))
/*     */           {
/*     */ 
/*     */ 
/*  93 */             Boolean isAdvancedParam = Boolean.valueOf(false);
/*  94 */             if (param.endsWith("[advanced]")) {
/*  95 */               param = param.substring(0, param.indexOf("[advanced]"));
/*  96 */               isAdvancedParam = Boolean.valueOf(true);
/*     */             } else {
/*  98 */               conditions.put(param, value);
/*  99 */               conditions.remove(param + "[advanced]");
/*     */             }
/* 101 */             conditions.put(param, value);
/*     */             
/* 103 */             String[] multiParams = param.split("\\|");
/* 104 */             if (multiParams.length == 1) {
/* 105 */               String[] dotParams = param.split("\\.");
/* 106 */               Criterion simpleExpression = null;
/* 107 */               if (dotParams.length == 1) {
/* 108 */                 if ((isAdvancedParam.booleanValue()) && (value.split(",").length > 1)) {
/* 109 */                   simpleExpression = _parseSimpleExpression(param, value.split(","));
/*     */                 } else {
/* 111 */                   simpleExpression = _parseSimpleExpression(param, value);
/*     */                 }
/* 113 */               } else if (dotParams.length >= 2) {
/* 114 */                 criterias = _parseDotParams(criterias, param, value);
/*     */               }
/* 116 */               if (simpleExpression != null) {
/* 117 */                 restrictions.add(simpleExpression);
/*     */               }
/*     */             } else {
/* 120 */               Criterion simpleExpression = null;
/* 121 */               Disjunction disjunction = Restrictions.disjunction();
/* 122 */               for (String splitParam : multiParams) {
/* 123 */                 splitParam = splitParam + "[like]";
/* 124 */                 simpleExpression = disjunction.add(_parseSimpleExpression(splitParam, value));
/*     */               }
/* 126 */               if (simpleExpression != null)
/* 127 */                 restrictions.add(simpleExpression);
/*     */             }
/*     */           }
/*     */         }
/* 131 */         _parseSorterValue(sorterValue, criterias);
/* 132 */         filter.setRestrictions(restrictions);
/* 133 */         filter.setCriterias(criterias);
/* 134 */         filter.setConditions(conditions);
/* 135 */         filterMap.put(requestUri, filter);
/*     */         
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/*     */ 
/* 142 */         session.put("filter", filterMap);
/*     */       }
/*     */     } catch (Exception e) {
/* 145 */       e.printStackTrace();
/*     */     }
/*     */   }
/*     */   
/*     */   public String getRestrictionsAttribute() {
/* 150 */     return "filter";
/*     */   }
/*     */   
/*     */   private void _parseSorterValue(String param, Map<String, List<Criterion>> criterias) {
/* 154 */     if (null == param) {
/* 155 */       SorterSession sorterSession = new SorterSession(null);
/* 156 */       if (null != sorterSession.getSorter()) {
/* 157 */         param = sorterSession.getSorter().getKey();
/*     */       }
/*     */     }
/* 160 */     if (null == param) {
/* 161 */       return;
/*     */     }
/* 163 */     if (param.indexOf("[") > 0) {
/* 164 */       param = param.substring(0, param.indexOf("["));
/*     */     }
/* 166 */     String[] dotParams = param.split("\\.");
/* 167 */     int lastIndex = param.lastIndexOf(".");
/* 168 */     if (lastIndex < 0) {
/* 169 */       return;
/*     */     }
/* 171 */     String lastParam = param.substring(0, lastIndex);
/*     */     
/* 173 */     if (!criterias.containsKey(lastParam)) {
/* 174 */       criterias.put(lastParam, null);
/*     */     }
/* 176 */     String tmpDotParam = "";
/* 177 */     for (int i = 0; i < dotParams.length - 2; i++) {
/* 178 */       if ("".equals(tmpDotParam)) {
/* 179 */         tmpDotParam = dotParams[i];
/*     */       } else {
/* 181 */         tmpDotParam = tmpDotParam + "." + dotParams[i];
/*     */       }
/* 183 */       if (!criterias.containsKey(tmpDotParam)) {
/* 184 */         criterias.put(tmpDotParam, null);
/*     */       }
/*     */     }
/*     */   }
/*     */   
/*     */   private Map<String, List<Criterion>> _parseDotParams(Map<String, List<Criterion>> criterias, String param, String value)
/*     */     throws ClassNotFoundException, NoSuchFieldException
/*     */   {
/* 192 */     String[] dotParams = param.split("\\.");
/* 193 */     int lastIndex = param.lastIndexOf(".");
/* 194 */     String lastParam = param.substring(0, lastIndex);
/* 195 */     List<Criterion> secondRstrictionList = (List)criterias.get(lastParam);
/* 196 */     if (null == secondRstrictionList) {
/* 197 */       secondRstrictionList = new ArrayList();
/*     */     }
/* 199 */     String[] values = value.split(",");
/*     */     
/* 201 */     param = lastParam.replace(".", "_") + param.substring(lastIndex);
/*     */     
/* 203 */     Criterion secondsimpleEx = null;
/* 204 */     if (values.length > 1) {
/* 205 */       secondsimpleEx = _parseSimpleExpression(param, values);
/*     */     } else {
/* 207 */       secondsimpleEx = _parseSimpleExpression(param, value);
/*     */     }
/* 209 */     if (secondsimpleEx != null) {
/* 210 */       secondRstrictionList.add(secondsimpleEx);
/*     */     }
/* 212 */     criterias.put(lastParam, secondRstrictionList);
/* 213 */     String tmpDotParam = "";
/* 214 */     for (int i = 0; i < dotParams.length - 2; i++) {
/* 215 */       if ("".equals(tmpDotParam)) {
/* 216 */         tmpDotParam = dotParams[i];
/*     */       } else {
/* 218 */         tmpDotParam = tmpDotParam + "." + dotParams[i];
/*     */       }
/* 220 */       List<Criterion> dotRstrictionList = (List)criterias.get(tmpDotParam);
/* 221 */       criterias.put(tmpDotParam, dotRstrictionList);
/*     */     }
/*     */     
/* 224 */     return criterias;
/*     */   }
/*     */   
/*     */   private Criterion _parseSimpleExpression(String param, String value)
/*     */     throws ClassNotFoundException, NoSuchFieldException
/*     */   {
/* 230 */     List<String> compareResult = _parseSimpleExpressionCompare(param);
/* 231 */     param = (String)compareResult.get(0);
/* 232 */     String compare = (String)compareResult.get(1);
/* 233 */     List<String> castResult = _parseSimpleExpressionCast(param);
/* 234 */     param = (String)castResult.get(0);
/* 235 */     String cast = (String)castResult.get(1);
/* 236 */     Criterion simpleExpression = _innerParseSimpleExpressionCast(param, value, compare, cast);
/*     */     
/* 238 */     return simpleExpression;
/*     */   }
/*     */   
/*     */   private Criterion _parseSimpleExpression(String param, String[] values) throws ClassNotFoundException, NoSuchFieldException
/*     */   {
/*     */     String compare;
/*     */     String compare;
/* 245 */     if (values.length > 1) {
/* 246 */       compare = "in";
/*     */     } else {
/* 248 */       List<String> compareResult = _parseSimpleExpressionCompare(param);
/* 249 */       param = (String)compareResult.get(0);
/* 250 */       compare = (String)compareResult.get(1);
/*     */     }
/* 252 */     List<String> castResult = _parseSimpleExpressionCast(param);
/* 253 */     param = (String)castResult.get(0);
/* 254 */     String cast = (String)castResult.get(1);
/* 255 */     Criterion simpleExpression = _innerParseCriterion(param, values, compare, cast);
/*     */     
/* 257 */     return simpleExpression;
/*     */   }
/*     */   
/*     */   protected List<String> _parseSimpleExpressionCompare(String param) {
/* 261 */     String compare = "eq";
/* 262 */     if (param.endsWith("[from]")) {
/* 263 */       param = param.substring(0, param.indexOf("[from]"));
/* 264 */       compare = "ge";
/* 265 */     } else if (param.endsWith("[to]")) {
/* 266 */       param = param.substring(0, param.indexOf("[to]"));
/* 267 */       compare = "le";
/* 268 */     } else if (param.endsWith("[like]")) {
/* 269 */       param = param.substring(0, param.indexOf("[like]"));
/* 270 */       compare = "like";
/* 271 */     } else if (param.endsWith("[null]")) {
/* 272 */       param = param.substring(0, param.indexOf("[null]"));
/* 273 */       compare = "null";
/* 274 */     } else if (param.endsWith("[!null]")) {
/* 275 */       param = param.substring(0, param.indexOf("[!null]"));
/* 276 */       compare = "!null";
/* 277 */     } else if (param.endsWith("[true]")) {
/* 278 */       param = param.substring(0, param.indexOf("[true]"));
/* 279 */       compare = "true";
/* 280 */     } else if (param.endsWith("[!true]")) {
/* 281 */       param = param.substring(0, param.indexOf("[!true]"));
/* 282 */       compare = "!true";
/*     */     }
/*     */     
/* 285 */     List<String> result = new ArrayList();
/* 286 */     result.add(param);
/* 287 */     result.add(compare);
/* 288 */     return result;
/*     */   }
/*     */   
/*     */   protected List<String> _parseSimpleExpressionCast(String param) throws ClassNotFoundException, NoSuchFieldException
/*     */   {
/* 293 */     List<String> result = new ArrayList();
/*     */     String cast;
/* 295 */     if (param.endsWith("[int]")) {
/* 296 */       param = param.substring(0, param.indexOf("[int]"));
/* 297 */       cast = "int"; } else if (param.endsWith("[long]")) {
/* 299 */         param = param.substring(0, param.indexOf("[long]"));
/* 300 */         cast = "long"; }else if (param.endsWith("[float]")) {
/* 302 */           param = param.substring(0, param.indexOf("[float]"));
/* 303 */           cast = "float"; } else if (param.endsWith("[double]")) {
/* 305 */             param = param.substring(0, param.indexOf("[double]"));
/* 306 */             cast = "double"; } else if (param.endsWith("[string]")) {
/* 308 */               param = param.substring(0, param.indexOf("[string]"));
/* 309 */               cast = "string";
/*     */             } else {
/* 311 */               String ormClass = getFilter().getOrmClass();
/* 312 */             if (null == ormClass) {
/* 313 */                 cast = "string";
/*     */               } else{
/* 315 */                 cast = Class.forName(ormClass).getDeclaredField(param).getType().getName();
/*     */             }
/*     */           } } } }
/* 318 */     result.add(param);
/* 319 */     result.add(cast);
/*     */     
/* 321 */     return result;
/*     */   }
/*     */   
/*     */   private void _saveFilterQuery(String key, Object value, String compare) {
/* 325 */     if (null == compare) {
/* 326 */       filter.getQuery().put(key, value);
/*     */     } else {
/* 328 */       Map<String, Object> query = filter.getQuery();
/* 329 */       Map<String, Object> _innerQuery = new LinkedHashMap();
/* 330 */       if (query.containsKey(key)) {
/* 331 */         _innerQuery = (Map)query.get(key);
/*     */       }
/* 333 */       _innerQuery.put(compare, value);
/* 334 */       query.put(key, _innerQuery);
/*     */     }
/*     */   }
/*     */   
/*     */   protected Criterion _innerParseSimpleExpressionCast(String param, String value, String compare, String cast) {
/*     */     Criterion simpleExpression;
/*     */     try {
/* 341 */       if ((cast.equals("int")) || (cast.equals("java.lang.Integer"))) {
/* 342 */         if (compare.equals("ge")) {
/* 343 */           Criterion simpleExpression = Restrictions.ge(param, Integer.valueOf(value));
/* 344 */           _saveFilterQuery(param, Integer.valueOf(value), "$ge");
/* 345 */         } else if (compare.equals("le")) {
/* 346 */           Criterion simpleExpression = Restrictions.le(param, Integer.valueOf(value));
/* 347 */           _saveFilterQuery(param, Integer.valueOf(value), "$le");
/*     */         } else {
/* 349 */           Criterion simpleExpression = Restrictions.eq(param, Integer.valueOf(value));
/* 350 */           _saveFilterQuery(param, Integer.valueOf(value), null);
/*     */         }
/* 352 */       } else if ((cast.equals("long")) || (cast.equals("java.lang.Long"))) {
/* 353 */         if (compare.equals("ge")) {
/* 354 */           Criterion simpleExpression = Restrictions.ge(param, Long.valueOf(value));
/* 355 */           _saveFilterQuery(param, Long.valueOf(value), "$ge");
/* 356 */         } else if (compare.equals("le")) {
/* 357 */           Criterion simpleExpression = Restrictions.le(param, Long.valueOf(value));
/* 358 */           _saveFilterQuery(param, Long.valueOf(value), "$le");
/*     */         } else {
/* 360 */           Criterion simpleExpression = Restrictions.eq(param, Long.valueOf(value));
/* 361 */           _saveFilterQuery(param, Long.valueOf(value), null);
/*     */         }
/* 363 */       } else if ((cast.equals("short")) || (cast.equals("java.lang.Short"))) {
/* 364 */         if (compare.equals("ge")) {
/* 365 */           Criterion simpleExpression = Restrictions.ge(param, Short.valueOf(value));
/* 366 */           _saveFilterQuery(param, Short.valueOf(value), "$ge");
/* 367 */         } else if (compare.equals("le")) {
/* 368 */           Criterion simpleExpression = Restrictions.le(param, Short.valueOf(value));
/* 369 */           _saveFilterQuery(param, Short.valueOf(value), "$le");
/*     */         } else {
/* 371 */           Criterion simpleExpression = Restrictions.eq(param, Short.valueOf(value));
/* 372 */           _saveFilterQuery(param, Short.valueOf(value), null);
/*     */         }
/* 374 */       } else if ((cast.equals("float")) || (cast.equals("java.lang.Float"))) {
/* 375 */         if (compare.equals("ge")) {
/* 376 */           Criterion simpleExpression = Restrictions.ge(param, Float.valueOf(value));
/* 377 */           _saveFilterQuery(param, Float.valueOf(value), "$ge");
/* 378 */         } else if (compare.equals("le")) {
/* 379 */           Criterion simpleExpression = Restrictions.le(param, Float.valueOf(value));
/* 380 */           _saveFilterQuery(param, Float.valueOf(value), "$le");
/*     */         } else {
/* 382 */           Criterion simpleExpression = Restrictions.eq(param, Float.valueOf(value));
/* 383 */           _saveFilterQuery(param, Float.valueOf(value), null);
/*     */         }
/* 385 */       } else if ((cast.equals("double")) || (cast.equals("java.lang.Double"))) {
/* 386 */         if (compare.equals("ge")) {
/* 387 */           Criterion simpleExpression = Restrictions.ge(param, Double.valueOf(value));
/* 388 */           _saveFilterQuery(param, Double.valueOf(value), "$ge");
/* 389 */         } else if (compare.equals("le")) {
/* 390 */           Criterion simpleExpression = Restrictions.le(param, Double.valueOf(value));
/* 391 */           _saveFilterQuery(param, Double.valueOf(value), "$le");
/*     */         } else {
/* 393 */           Criterion simpleExpression = Restrictions.eq(param, Double.valueOf(value));
/* 394 */           _saveFilterQuery(param, Double.valueOf(value), null);
/*     */         }
/* 396 */       } else if ((cast.equals("date")) || (cast.equals("java.util.Date"))) {
/* 397 */         Timestamp date = Timestamp.valueOf(value);
/* 398 */         if (compare.equals("ge")) {
/* 399 */           Criterion simpleExpression = Restrictions.ge(param, date);
/* 400 */           _saveFilterQuery(param, date, "$ge");
/* 401 */         } else if (compare.equals("le")) {
/* 402 */           Criterion simpleExpression = Restrictions.le(param, date);
/* 403 */           _saveFilterQuery(param, date, "$le");
/*     */         } else {
/* 405 */           Criterion simpleExpression = Restrictions.eq(param, date);
/* 406 */           _saveFilterQuery(param, date, null);
/*     */         }
/*     */       }
/* 409 */       else if (compare.equals("like")) {
/* 410 */         Criterion simpleExpression = Restrictions.ilike(param, "%" + value + "%");
/* 411 */         _saveFilterQuery(param, value, "$like");
/* 412 */       } else if (compare.equals("null")) {
/* 413 */         Criterion simpleExpression = Restrictions.isNull(param);
/* 414 */         _saveFilterQuery(param, value, "$null");
/* 415 */       } else if (compare.equals("!null")) {
/* 416 */         Criterion simpleExpression = Restrictions.isNotNull(param);
/* 417 */         _saveFilterQuery(param, value, "$not_null"); } else { Criterion simpleExpression;
/* 418 */         if (compare.equals("true")) {
/* 419 */           simpleExpression = Restrictions.eq(param, Boolean.valueOf(true)); } else { Criterion simpleExpression;
/* 420 */           if (compare.equals("!true")) {
/* 421 */             simpleExpression = Restrictions.ne(param, Boolean.valueOf(true));
/*     */           }
/* 423 */           else if (compare.equals("ge")) {
/* 424 */             Criterion simpleExpression = Restrictions.ge(param, value);
/* 425 */             _saveFilterQuery(param, value, "$ge");
/* 426 */           } else if (compare.equals("le")) {
/* 427 */             Criterion simpleExpression = Restrictions.le(param, value);
/* 428 */             _saveFilterQuery(param, value, "$le");
/*     */           } else {
/* 430 */             Criterion simpleExpression = Restrictions.eq(param, value);
/* 431 */             _saveFilterQuery(param, value, null);
/*     */           }
/*     */         }
/*     */       }
/*     */     } catch (NumberFormatException ex) {
/* 436 */       ex.printStackTrace();
/* 437 */       simpleExpression = null;
/*     */     }
/*     */     
/* 440 */     return simpleExpression;
/*     */   }
/*     */   
/*     */   private Criterion _innerParseCriterion(String param, String[] values, String compare, String cast) {
/* 444 */     Criterion criterion = null;
/*     */     try {
/* 446 */       List result = new ArrayList();
/* 447 */       if ((cast.equals("int")) || (cast.equals("java.lang.Integer"))) {
/* 448 */         for (String value : values) {
/* 449 */           result.add(Integer.valueOf(value));
/*     */         }
/* 451 */       } else if ((cast.equals("long")) || (cast.equals("java.lang.Long"))) {
/* 452 */         for (String value : values) {
/* 453 */           result.add(Long.valueOf(value));
/*     */         }
/* 455 */       } else if ((cast.equals("short")) || (cast.equals("java.lang.Short"))) {
/* 456 */         for (String value : values) {
/* 457 */           result.add(Short.valueOf(value));
/*     */         }
/* 459 */       } else if ((cast.equals("float")) || (cast.equals("java.lang.Float"))) {
/* 460 */         for (String value : values) {
/* 461 */           result.add(Float.valueOf(value));
/*     */         }
/* 463 */       } else if ((cast.equals("double")) || (cast.equals("java.lang.Double"))) {
/* 464 */         for (String value : values) {
/* 465 */           result.add(Double.valueOf(value));
/*     */         }
/* 467 */       } else if ((cast.equals("date")) || (cast.equals("java.util.Date"))) {
/* 468 */         for (String value : values) {
/* 469 */           result.add(Timestamp.valueOf(value));
/*     */         }
/*     */       } else {
/* 472 */         for (String value : values) {
/* 473 */           result.add(value.trim());
/*     */         }
/* 475 */         _saveFilterQuery(param, result, "$in");
/*     */       }
/* 477 */       disjunction = Restrictions.disjunction();
/* 478 */       if (compare.equals("like")) {
/* 479 */         for (Object value : result) {
/* 480 */           criterion = disjunction.add(Restrictions.ilike(param, "%" + value + "%"));
/*     */         }
/*     */       } else {
/* 483 */         for (Object value : result)
/* 484 */           criterion = disjunction.add(Restrictions.eq(param, value));
/*     */       }
/*     */     } catch (NumberFormatException ex) {
/*     */       Disjunction disjunction;
/* 488 */       criterion = null;
/*     */     }
/*     */     
/* 491 */     return criterion;
/*     */   }
/*     */ }

