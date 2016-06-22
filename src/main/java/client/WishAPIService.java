package client;

import java.util.HashMap;
import java.util.Map;

public class WishAPIService extends BaseServiceClient {
	
	private String currentAccount;
	private final String UPLOAD_WISH_TRACKNUMBER = "/wish/uploadTrackNumber";
	private final String UPDATE_WISH_TRACKNUMBER = "/wish/updateTrackNumber";
	private final String FETCH_WISH_PRODUCTS = "/wish/fetchWishProducts";
	private final String CREATE_WISH_PRODUCT = "/wish/createAProduct";
	private final String CREATE_WISH_PRODUCT_VARIATION = "/wish/createAProductVariation";
	private final String UPDATE_WISH_PRODUCT_BASIC_INFO = "/wish/updateProductBasicInfo";
	private final String UPDATE_WISH_PRODUCT_VARIATION = "/wish/updateProductVariantInfo";
	private final String RETRIEVE_WISH_PRODUCT = "/wish/retrieveProductInfo";
	private final String RETRIEVE_WISH_PRODUCT_VARIATION = "/wish/retrieveProductVariantInfo";
	private final String ENABLE_WISH_PRODUCT = "/wish/enableaProduct";
	private final String DISABLE_WISH_PRODUCT = "/wish/disableaProduct";
	private final String ENABLE_WISH_PRODUCT_VARIATION = "/wish/enableaProductVariation";
	private final String DISABLE_WISH_PRODUCT_VARIATION = "/wish/disableaProductVariation";
	private final String CONTEST_TAG_SEARCH= "/wish/search";
	private final String UPLOAD_WISH_TEMP_IMAGE = "/wish/uploadTempImg";
	private final String UPDATE_ACCESS_TOKEN = "/wish/oauth";
	//wishTicket
	private final String RETRIEVE_A_TICKRT = "/wish/retrieveOneTicket";
	private final String RETRIEVE_ALL_TICKRT_AWAITING_FOR_YOU = "/wish/retrieveAllTicket";
	private final String REPLY_TO_A_TICKET = "/wish/replyToATicket";
	private final String CLOSE_A_TICKRT = "/wish/closeATicket";
	private final String REOPEN_A_TICKRT = "/wish/reOpenATicket";
	private final String APPEAL_TO_WISH_SUPPORT = "/wish/appealToWishSupportForTicket";
	
	
	public WishAPIService() {
		initServiceAddressAndToken("WISH_API_SERVICE");
	}
	
	public Map<String, Object> uploadWishTrackNumber(HashMap<String, Object> request) {
		return getMapResponse(UPLOAD_WISH_TRACKNUMBER, request);
	}
	
	public Map<String, Object> updateWishTrackNumber(HashMap<String, Object> request) {
		return getMapResponse(UPDATE_WISH_TRACKNUMBER, request);
	}
	
	public Map<String, Object> fetchWishProducts(HashMap<String, Object> request) {
		return getMapResponse(FETCH_WISH_PRODUCTS, request);
	}
	
//	public Map<String, Object> createAProduct(HashMap<String, Object> request) {
//		setServiceEntry(CREATE_WISH_PRODUCT);
//		setServiceRequest(request);
//		
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> createAProductVariation(HashMap<String, Object> request) {
//		setServiceEntry(CREATE_WISH_PRODUCT_VARIATION);
//		setServiceRequest(request);
//		
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> updateProductBasic(HashMap<String, Object> request) {
//		setServiceEntry(UPDATE_WISH_PRODUCT_BASIC_INFO);
//		setServiceRequest(request);
//		
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> updateProductVariation(HashMap<String, Object> request) {  
//		setServiceEntry(UPDATE_WISH_PRODUCT_VARIATION);
//		setServiceRequest(request);
//		
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> retrieveProduct(HashMap<String, Object> request) {
//		setServiceEntry(RETRIEVE_WISH_PRODUCT);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> retrieveProductVariation(HashMap<String, Object> request) {
//		setServiceEntry(RETRIEVE_WISH_PRODUCT_VARIATION);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> enableaProduct(HashMap<String, Object> request) {
//		setServiceEntry(ENABLE_WISH_PRODUCT);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> disableaProduct(HashMap<String, Object> request) {
//		setServiceEntry(DISABLE_WISH_PRODUCT);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> enableaProductVariation(HashMap<String, Object> request) {
//		setServiceEntry(ENABLE_WISH_PRODUCT_VARIATION);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> disableaProductVariation(HashMap<String, Object> request) {
//		setServiceEntry(DISABLE_WISH_PRODUCT_VARIATION);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> contestTagSearch(HashMap<String, Object> request) {
//		initService(request);
//		setServiceEntry(CONTEST_TAG_SEARCH);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> uploadWishLocalImage(HashMap<String, Object> request){
//		setServiceEntry(UPLOAD_WISH_TEMP_IMAGE);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> updateAccessToken(HashMap<String, Object> request){
//		setServiceEntry(UPDATE_ACCESS_TOKEN);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	//wishTicket
//	public Map<String, Object> retrieveOneTicket(HashMap<String, Object> request){
//		setServiceEntry(RETRIEVE_A_TICKRT);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> retrieveAllTicket(HashMap<String, Object> request){
//		setServiceEntry(RETRIEVE_ALL_TICKRT_AWAITING_FOR_YOU);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> replyToATicket(HashMap<String, Object> request){
//		setServiceEntry(REPLY_TO_A_TICKET);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> closeATicket(HashMap<String, Object> request){
//		initService(request);
//		setServiceEntry(CLOSE_A_TICKRT);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> appealToWishSupportForTicket(HashMap<String, Object> request){
//		setServiceEntry(APPEAL_TO_WISH_SUPPORT);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
//	
//	public Map<String, Object> reOpenATicket(HashMap<String, Object> request){
//		setServiceEntry(REOPEN_A_TICKRT);
//		setServiceRequest(request);
//		return (Map<String, Object>) request();
//	}
}
