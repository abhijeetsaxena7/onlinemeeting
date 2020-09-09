package com.libsys.onlinemeeting.controller.microsoft;

import java.util.Arrays;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.libsys.onlinemeeting.config.HelperMethods;
import com.libsys.onlinemeeting.config.SessionManagementHelper;
import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.microsoft.GraphServiceClientWrapper;
import com.libsys.onlinemeeting.config.vendor.microsoft.Microsoft;
import com.libsys.onlinemeeting.config.vendor.microsoft.MicrosoftScopes;
import com.libsys.onlinemeeting.model.UserModel;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.graph.models.extensions.AppRoleAssignment;
import com.microsoft.graph.models.extensions.PasswordProfile;
import com.microsoft.graph.models.extensions.User;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.requests.extensions.IUserCollectionRequest;

/**
 * 
 * @author Abhijeet Saxena
 * Class contains user related operations in microsoft. 
 * User with role UserAdministrater permissions can perform these operations.
 */
@RestController("Microsoft_UserController")
@RequestMapping(Constants.VendorPath.MICROSOFT +"/user")
public class UserController {
	@Autowired
	private Microsoft microsoft;
	@Autowired
	private HelperMethods helper;
	@Autowired
	private GraphServiceClientWrapper graphClientWrapper;
	@Autowired
	private SessionManagementHelper sessionManagementHelper;

	/**
	 * Create a user in microsoft AAD.
	 * Get IAuthenticationResult object from session.
	 * Set User object details from userModel.
	 * Set AccessToken in header.
	 * Build request and get Response
	 * 
	 * @param request
	 * @param response
	 * @param userModel
	 * @return If success HttpStatus 201 and UserModel, else, HttpStatus 500 and error msg.
	 */
	@PostMapping("")
	public ResponseEntity createUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody UserModel userModel) {
		ResponseEntity resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request,
//					microsoft.getReqScopes(MicrosoftScopes.User.Create.values()));
			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
			
			User user = new User();
			user.accountEnabled = true;
			user.displayName = userModel.getDisplayName();
			user.mailNickname = userModel.getMailNickname();
			user.userPrincipalName = userModel.getEmail();
			PasswordProfile passwordProfile = new PasswordProfile();
			passwordProfile.forceChangePasswordNextSignIn = true;
			passwordProfile.password = userModel.getDefaultPassword();
			user.passwordProfile = passwordProfile;
			user.department = userModel.getDepartment();
			user.birthday = userModel.getDob();

			HeaderOption option = new HeaderOption("Authorization", "Bearer " + result.accessToken());
			IUserCollectionRequest userReq = graphClientWrapper.getGraphServiceClient().users()
					.buildRequest(Arrays.asList(option));

			User res = userReq.post(user);
			userModel.setObjectId(res.id);
			resEntity = new ResponseEntity<>(userModel, HttpStatus.CREATED);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return resEntity;

	}

	/**
	 * Delete a user from microsoft AAD.
	 * Get IAuthenticationResult object from session.
	 * Set UserId.
	 * Set AccessToken in header.
	 * Build request and get Response
	 * 
	 * @param request
	 * @param response
	 * @param userId
	 * @return If success, HttpStatus 204, else HttpStatus 500 and error msg.
	 */
	@DeleteMapping("")
	public ResponseEntity<String> deleteUser(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String userId) {
		ResponseEntity<String> resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request,
//					microsoft.getReqScopes(MicrosoftScopes.User.Delete.values()));
			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
			
			HeaderOption option = new HeaderOption("Authorization", "Bearer " + result.accessToken());
			graphClientWrapper.getGraphServiceClient().users(userId).buildRequest(Arrays.asList(option)).delete();
			resEntity = new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return resEntity;
	}

	/**
	 * Assign role to user.
	 * Get IAuthenticationResult object from session.
	 * Set appRoleAssignment object.
	 * Set AccessToken in header.
	 * Build request and get Response
	 *  
	 * @param request
	 * @param response
	 * @param roleId
	 * @param assignerId
	 * @param assignedToId
	 * @return If success, HttpStatus 204, else HttpStatus 500 and error msg.
	 */
	@PostMapping("/role")
	public ResponseEntity<String> assignRoleToUser(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String roleId, @RequestParam String assignerId, @RequestParam String assignedToId) {
		ResponseEntity<String> resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request,
//					microsoft.getReqScopes(MicrosoftScopes.User.AddRole.values()));
			
			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
			AppRoleAssignment roleAssignment = new AppRoleAssignment();
			roleAssignment.principalId = UUID.fromString(assignedToId);
			roleAssignment.resourceId = UUID.fromString(assignerId);
			roleAssignment.appRoleId = UUID.fromString(roleId);

			HeaderOption option = new HeaderOption("Authorization", "Bearer " + result.accessToken());
			graphClientWrapper.getGraphServiceClient().users(assignedToId).buildRequest(Arrays.asList(option)).delete();
			resEntity = new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return resEntity;
	}

}
