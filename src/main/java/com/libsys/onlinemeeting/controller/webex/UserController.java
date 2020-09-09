package com.libsys.onlinemeeting.controller.webex;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.webex.Webex;
import com.libsys.onlinemeeting.config.vendor.webex.WebexConfiguration;
import com.libsys.onlinemeeting.config.vendor.webex.sdk.Person;
import com.libsys.onlinemeeting.config.vendor.webex.sdk.Spark;
import com.libsys.onlinemeeting.model.UserModel;

/**
 * 
 * @author Abhijeet Saxena
 * Contains methods related to user operations in webex admin portal.
 */
@RestController("Webex_UserController")
@RequestMapping(Constants.VendorPath.WEBEX + "/user")
public class UserController {
	private WebexConfiguration webexConfig;
	private Webex webex;
	
	@Autowired
	public UserController(WebexConfiguration webexConfig, Webex webex) {
		this.webexConfig = webexConfig;
		this.webex = webex;
	}
	
	/**
	 * Create a user in webex admin.
	 * Get access token from session.
	 * Build spark object from token.
	 * Set Person from userModel object and call request.
	 * @param request
	 * @param userModel
	 * @return
	 */
	@PostMapping("")
	public ResponseEntity createUser(HttpServletRequest request, @RequestBody UserModel userModel) {
		
		ResponseEntity resEntity;
		try {
			String accessToken = webex.getAccessTokenValueFromSession(request);
			
			Spark spark = Spark.builder().accessToken(accessToken).build();
			Person person = new Person();
			person.setEmails(new String[] {userModel.getEmail()});
			person.setDisplayName(userModel.getDisplayName());
			person.setRoles(userModel.getRoles());
			
			Person res = spark.people().post(person);
			userModel.setObjectId(res.getId());	
			resEntity = new ResponseEntity(userModel, HttpStatus.CREATED);
		}catch(Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
	
	/**
	 * Delete a user in webex admin.
	 * Get access token from session.
	 * Build spark object from token.
	 * Pass personId and call request.
	 * @param request
	 * @param personId
	 * @return
	 */
	@DeleteMapping("")
	public ResponseEntity deleteUser(HttpServletRequest request, @RequestParam String personId) {
		ResponseEntity resEntity;
		try {
			String accessToken = webex.getAccessTokenValueFromSession(request);
			
			Spark spark = Spark.builder().accessToken(accessToken).build();			
			spark.people().path("/"+personId).delete();
			resEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
		}catch(Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}

}
