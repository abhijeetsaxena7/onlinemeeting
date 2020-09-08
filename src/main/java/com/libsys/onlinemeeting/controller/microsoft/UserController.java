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

@RestController("Microsoft_UserController")
@RequestMapping(Constants.VendorPath.MICROSOFT +"/user")
public class UserController {
	@Autowired
	private Microsoft microsoft;
	@Autowired
	private HelperMethods helper;
	@Autowired
	private GraphServiceClientWrapper graphClientWrapper;

	@PostMapping("")
	public ResponseEntity createUser(HttpServletRequest request, HttpServletResponse response,
			@RequestBody UserModel userModel) {
		ResponseEntity resEntity;
		try {
			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request,
					microsoft.getReqScopes(MicrosoftScopes.User.Create.values()));
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

	@DeleteMapping("")
	public ResponseEntity<String> deleteUser(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String userId) {
		ResponseEntity<String> resEntity;
		try {
			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request,
					microsoft.getReqScopes(MicrosoftScopes.User.Delete.values()));

			HeaderOption option = new HeaderOption("Authorization", "Bearer " + result.accessToken());
			graphClientWrapper.getGraphServiceClient().users(userId).buildRequest(Arrays.asList(option)).delete();
			resEntity = new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}

		return resEntity;
	}

	@PostMapping("/role")
	public ResponseEntity<String> assignRoleToUser(HttpServletRequest request, HttpServletResponse response,
			@RequestParam String roleId, @RequestParam String assignerId, @RequestParam String assignedToId) {
		ResponseEntity<String> resEntity;
		try {
			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request,
					microsoft.getReqScopes(MicrosoftScopes.User.AddRole.values()));

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
