package com.libsys.onlinemeeting.controller.microsoft;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

import com.google.gson.JsonArray;
import com.libsys.onlinemeeting.config.HelperMethods;
import com.libsys.onlinemeeting.config.SessionManagementHelper;
import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.vendor.microsoft.GraphServiceClientWrapper;
import com.libsys.onlinemeeting.config.vendor.microsoft.Microsoft;
import com.libsys.onlinemeeting.config.vendor.microsoft.MicrosoftConfiguration;
import com.libsys.onlinemeeting.config.vendor.microsoft.MicrosoftScopes;
import com.libsys.onlinemeeting.model.GroupModel;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.graph.models.extensions.DirectoryObject;
import com.microsoft.graph.models.extensions.Group;
import com.microsoft.graph.options.HeaderOption;
import com.microsoft.graph.requests.extensions.IGroupCollectionRequest;

@RestController("Microsoft_GroupController")
@RequestMapping(Constants.VendorPath.MICROSOFT +"/group")
public class GroupController {
	@Autowired
	private Microsoft microsoft;
	@Autowired
	private HelperMethods helper;
	@Autowired
	private GraphServiceClientWrapper graphClientWrapper;
	@Autowired
	private MicrosoftConfiguration msConfig;
	@Autowired
	SessionManagementHelper sessionManagementHelper;
	
	@PostMapping("")
	public ResponseEntity createGroup(HttpServletRequest request, HttpServletResponse response, @RequestBody GroupModel groupModel){
		ResponseEntity resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request, microsoft.getReqScopes(MicrosoftScopes.Group.Create.values()));			
			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
			Group group = new Group();
			group.displayName = groupModel.getDisplayName();
			group.description = groupModel.getDescription();
			group.mailEnabled = true;
			group.mailNickname = groupModel.getDisplayName().toLowerCase();
			List<String> groupTypeList = new ArrayList<String>();
			groupTypeList.add("unified");
			group.groupTypes = groupTypeList;
			group.securityEnabled=false;
			
			HeaderOption option = new HeaderOption("Authorization", "Bearer " + result.accessToken());			
			IGroupCollectionRequest groupReq = graphClientWrapper.getGraphServiceClient().groups().buildRequest(Arrays.asList(option));
			Group res = groupReq.post(group);
			groupModel.setObjectId(res.id);
			resEntity = new ResponseEntity<>(groupModel,HttpStatus.CREATED);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
	
	@DeleteMapping("")
	public ResponseEntity<String> deleteGroup(HttpServletRequest request, HttpServletResponse response, @RequestParam String groupId){
		ResponseEntity<String> resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request, microsoft.getReqScopes(MicrosoftScopes.Group.Delete.values()));			
			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
			
			HeaderOption option = new HeaderOption("Authorization", "Bearer " + result.accessToken());			
			graphClientWrapper.getGraphServiceClient().groups(groupId).buildRequest(Arrays.asList(option)).delete();
			resEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return resEntity;
	}
	
	@PostMapping("/owner")
	public ResponseEntity<String> addGroupOwner(HttpServletRequest request, HttpServletResponse response, @RequestParam String groupId, @RequestParam String userId){
		ResponseEntity<String> resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request, microsoft.getReqScopes(MicrosoftScopes.Group.AddOwner.values()));			
			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
			
			DirectoryObject userObject= new DirectoryObject();
			userObject.id =userId;
			
			HeaderOption option = new HeaderOption("Authorization", "Bearer " + result.accessToken());			
			graphClientWrapper.getGraphServiceClient().groups(groupId).owners().references().buildRequest(Arrays.asList(option)).post(userObject);
			resEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return resEntity;
	}
	
	@PostMapping("/member")
	public ResponseEntity<String> addGroupMember(HttpServletRequest request, HttpServletResponse response, @RequestParam String groupId, @RequestParam List<String> memberIds){
		ResponseEntity<String> resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request, microsoft.getReqScopes(MicrosoftScopes.Group.AddMember.values()));
			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
			
			JsonArray members = new JsonArray();
			for(String memberId:memberIds) {
				members.add(msConfig.getGraphEndpointHost()+"v1.0/directoryObjects/"+memberId);
			}
			
			Group group = new Group();
			group.additionalDataManager().put("members@odata.bind", members);
			HeaderOption option = new HeaderOption("Authorization", "Bearer " + result.accessToken());			
			graphClientWrapper.getGraphServiceClient().groups(groupId).buildRequest(Arrays.asList(option)).patch(group);
			resEntity = new ResponseEntity<String>(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return resEntity;
	}
	
	@DeleteMapping("/owner")
	public ResponseEntity<String> deleteGroupOwner(HttpServletRequest request, HttpServletResponse response, @RequestParam String groupId, @RequestParam String userId){
		ResponseEntity<String> resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request, microsoft.getReqScopes(MicrosoftScopes.Group.DeleteOwner.values()));			
			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
			
			HeaderOption option = new HeaderOption("Authorization", "Bearer " + result.accessToken());			
			graphClientWrapper.getGraphServiceClient().groups(groupId).owners(userId).reference().buildRequest(Arrays.asList(option)).delete();
			resEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return resEntity;
	}
	
	@DeleteMapping("/member")
	public ResponseEntity<String> deleteGroupMember(HttpServletRequest request, HttpServletResponse response, @RequestParam String groupId, @RequestParam String memberId){
		ResponseEntity<String> resEntity;
		try {
//			IAuthenticationResult result = microsoft.getAuthResultBySilentFlow(request, microsoft.getReqScopes(MicrosoftScopes.Group.DeleteMember.values()));
			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getAuthSessionObject(request);
			
			HeaderOption option = new HeaderOption("Authorization", "Bearer " + result.accessToken());			
			graphClientWrapper.getGraphServiceClient().groups(groupId).members(memberId).reference().buildRequest(Arrays.asList(option)).delete();
			resEntity = new ResponseEntity<>(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}		
		return resEntity;
	}
	
}
