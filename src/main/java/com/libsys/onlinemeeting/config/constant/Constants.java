package com.libsys.onlinemeeting.config.constant;

public interface Constants {
	
	public interface HeaderValue{
		String APPLICATION_X_WWW_FORM_ENCODED = "application/x-www-form-url-encoded";
		String APPLICATION_JSON = "application/json";
	}
	
	public interface QueryParams{
		String GRANT_TYPE = "grant_type";
		String RESPONSE_TYPE = "response_type";
		String CLIENT_ID = "client_id";
		String CLIENT_SECRET = "";
		String STATE = "state";
		String SCOPE = "scope";
		String AUTH_CODE = "code";
		String REDIRECT_URI = "redirect_uri";
		String REFRESH_TOKEN = "refresh_token";
	}
	public interface Attributes{
		String VENDOR = "vendor";
	}
	
	public interface Session{
		String PRINCIPAL_SESSION_NAME = "principal";
		String TOKEN_CACHE = "token_cache";
		 String STATES = "states";
		 Integer STATE_TTL = 3600;    
	}
	
	public enum Vendors {

		Microsoft(1, "microsoft"), Webex(2, "webex"), Zoom(3,"zoom");

		int id;
		String path;

		Vendors(int id, String path) {
			this.id = id;
			this.path = path;
		}

		public int getId() {
			return this.id;
		}

		public String getPath() {
			return this.path;
		}
	}
	
	interface VendorPath{
		String MICROSOFT = "/microsoft";
		String WEBEX = "/webex";
		String ZOOM = "/zoom";
	}
}
