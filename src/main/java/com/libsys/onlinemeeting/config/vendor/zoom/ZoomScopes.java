package com.libsys.onlinemeeting.config.vendor.zoom;

public interface ZoomScopes {

	public enum User{
		USER_WRITE_ADMIN("user:write:admin");
		String value;
		
		User(String value){
			this.value = value;
		}
	}
	
	public enum Team{
	}
	
	public enum Meeting{
		MEETING_WRITE("meeting:write");
		
		String value;
		
		Meeting(String value){
			this.value = value;
		}
	}
	
	public enum TeamMembership{
	}
	
	
	

}
