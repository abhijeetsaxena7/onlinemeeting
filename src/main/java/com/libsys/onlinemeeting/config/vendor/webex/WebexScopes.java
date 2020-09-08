package com.libsys.onlinemeeting.config.vendor.webex;

public interface WebexScopes {

	public enum User{
		ADMIN_PEOPLE_WRITE("spark-admin:people_write");
		
		String value;
		
		User(String value){
			this.value = value;
		}
	}
	
	public enum Team{
		ADMIN_TEAM_WRITE("spark:teams_write");
		
		String value;
		
		Team(String value){
			this.value = value;
		}
	}
	
	public enum Meeting{
		MEETING_WRITE("meeting:schedules_write");
		
		String value;
		
		Meeting(String value){
			this.value = value;
		}
	}
	
	public enum TeamMembership{
		MEMBER_WRITE("spark:team_memberships_write");
		
		String value;
		
		TeamMembership(String value){
			this.value = value;
		}
	}
	
	
	

}
