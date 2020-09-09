package com.libsys.onlinemeeting.controller.webex;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.webex.Webex;
import com.libsys.onlinemeeting.config.vendor.webex.WebexConfiguration;
import com.libsys.onlinemeeting.config.vendor.webex.sdk.Spark;
import com.libsys.onlinemeeting.config.vendor.webex.sdk.TeamMembership;

/**
 * @author Abhijeet Saxena
 * Contains methods related team membership operations in webex teams.
 */
@RestController("Webex_TeamMembershipController")
@RequestMapping(Constants.VendorPath.WEBEX + "/membership")
public class TeamMembershipController {
	private WebexConfiguration webexConfig;
	private Webex webex;
	
	@Autowired
	public TeamMembershipController(WebexConfiguration webexConfig, Webex webex) {
		this.webexConfig = webexConfig;
		this.webex = webex;
	}
	
	/**
	 * Add a user to team in webex teams.
	 * Get access token from session.
	 * Build spark object from token.
	 * Set teamMembership object and call request.
	 * @param request
	 * @param teamId
	 * @param personId
	 * @param isOwner whether team member is team owner or not
	 * @return If success, HttpStatus 201 and membershipId, else HttpStatus500 and error msg.
	 */
	@PostMapping("")
	public ResponseEntity addToTeam(HttpServletRequest request, @RequestParam String teamId, @RequestParam String personId, @RequestParam boolean isOwner ) {
		ResponseEntity resEntity;
		try {
			String accessToken = webex.getAccessTokenValueFromSession(request);
			Spark spark = Spark.builder().accessToken(accessToken).build();			
			
			TeamMembership teamMembership = new TeamMembership();
			teamMembership.setTeamId(teamId);
			teamMembership.setPersonId(personId);
			teamMembership.setIsModerator(isOwner);

			TeamMembership res = spark.teamMemberships().post(teamMembership);

			resEntity = new ResponseEntity(res.getId(),HttpStatus.CREATED);
		}catch(Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
	
	/**
	 * Remove a user from team in webex teams.
	 * Get access token from session.
	 * Build spark object from token.
	 * Pass teamMembership id and call request.
	 * @param request
	 * @param membershipId
	 * @return
	 */
	@DeleteMapping("")
	public ResponseEntity deleteFromTeam(HttpServletRequest request, @RequestParam String membershipId) {
		ResponseEntity resEntity;
		try {
			String accessToken = webex.getAccessTokenValueFromSession(request);
			
			Spark spark = Spark.builder().accessToken(accessToken).build();			
			spark.teamMemberships().path("/"+membershipId).delete();
			resEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
		}catch(Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
}
