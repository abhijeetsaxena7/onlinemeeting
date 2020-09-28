package com.libsys.onlinemeeting.controller.zoom;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.zoom.Zoom;
import com.libsys.onlinemeeting.config.vendor.zoom.ZoomConstants;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.User;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.UserCreate;
import com.libsys.onlinemeeting.config.vendor.zoom.sdk.ZoomClient;
import com.libsys.onlinemeeting.model.UserModel;

/**
 * User related operation in zoom. These are only applicable when using admin
 * managed app type.
 * 
 * @author Abhijeet Saxena
 *
 */
@RestController("Zoom_UserController")
@RequestMapping(Constants.VendorPath.ZOOM + "/user")
public class UserController {
	@Autowired
	private Zoom zoom;

	/**
	 * Create a user in zoom admin directory. This is the only method where we have
	 * not used the sdk due to the fact that input and output type are different.
	 * 
	 * Get Access Token value.
	 * Set data in UserCreateModel.
	 * Send request and return response
	 * @param request
	 * @param response
	 * @param userModel
	 * @return HttpStatus 201 and userModel, on failure HttpStatus 500 and error msg.
	 */
	@PostMapping("")
	public ResponseEntity createUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody UserModel userModel) {
		ResponseEntity resEntity;
		try {
			String accessToken = zoom.getAccessTokenValueFromSession(request);
			UserCreate userCreate = new UserCreate();
			userCreate.setAction("create");

			User user = new User();
			user.setEmailId(userModel.getEmail());
			user.setType(ZoomConstants.USER_TYPE.BASIC);
			user.setFirstName(userModel.getFirstName());
			user.setLastName(userModel.getLastName());
			userCreate.setUser(user);
			// exception...sdk not used due to input model different from output model

			HttpHeaders headers = new HttpHeaders();
			headers.add(HttpHeaders.AUTHORIZATION, "Bearer " + accessToken);
			headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE);

			HttpEntity entity = new HttpEntity(userCreate, headers);
			RestTemplate restTemplate = new RestTemplate();
			String url = "https://api.zoom.us/v2/users";
			ResponseEntity<User> res = restTemplate.exchange(url, HttpMethod.POST, entity, User.class);
			User resUser = res.getBody();
			userModel.setObjectId(resUser.getId());
			resEntity = new ResponseEntity(userModel, HttpStatus.CREATED);

		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}

	/**
	 * Delete the user from zoom admin directory based on the passed userId(emailId).
	 * @param request
	 * @param response
	 * @param userId
	 * @return httpStatus 204, else httpstatus 500 and error msg.
	 */
	@DeleteMapping("")
	public ResponseEntity deleteUser(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String userId) {
		ResponseEntity resEntity;
		try {
			String accessToken = zoom.getAccessTokenValueFromSession(request);
			ZoomClient zoomClient = ZoomClient.builder(accessToken).build();

			zoomClient.user(userId).queryParam("action", "delete").delete();
			resEntity = new ResponseEntity(HttpStatus.NO_CONTENT);

		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
}
