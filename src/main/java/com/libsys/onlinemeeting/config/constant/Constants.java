package com.libsys.onlinemeeting.config.constant;

public interface Constants {
	
	public interface HeaderValue{
		String APPLICATION_X_WWW_FORM_ENCODED = "application/x-www-form-urlencoded";
		String APPLICATION_JSON = "application/json";
	}
	
	public interface QueryParams{
		String GRANT_TYPE = "grant_type";
		String RESPONSE_TYPE = "response_type";
		String CLIENT_ID = "client_id";
		String CLIENT_SECRET = "client_secret";
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
		String AUTH_OBJECT = "auth_object";    
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
	
	interface Roles{
		interface Microsoft{
			String USER_ADMIN = "fe930be7-5e62-47db-91af-98c3a49a38b1";
			String GLOBAL_ADMIN="62e90394-69f5-4237-9190-012177145e10";
		}
		interface Webex{
			String USER_ADMIN = "";
			String GLOBAL_ADMIN = "";
		}
	}
}
