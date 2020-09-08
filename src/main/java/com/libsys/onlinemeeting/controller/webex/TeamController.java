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
import com.libsys.onlinemeeting.config.vendor.webex.sdk.Spark;
import com.libsys.onlinemeeting.config.vendor.webex.sdk.Team;
import com.libsys.onlinemeeting.model.GroupModel;

@RestController("Webex_TeamController")
@RequestMapping(Constants.VendorPath.WEBEX + "/team")
public class TeamController {
	private WebexConfiguration webexConfig;
	private Webex webex;
	
	@Autowired
	public TeamController(WebexConfiguration webexConfig, Webex webex) {
		this.webexConfig = webexConfig;
		this.webex = webex;
	}

	@PostMapping("")
	public ResponseEntity createTeam(HttpServletRequest request, @RequestBody GroupModel groupModel) {
		ResponseEntity resEntity;
		try {
			String accessToken = webex.getAccessTokenValueFromSession(request);
			Spark spark = Spark.builder().accessToken(accessToken).build();			
			
			Team team = new Team();
			team.setName(groupModel.getDisplayName());

			Team res = spark.teams().post(team);
			groupModel.setObjectId(res.getId());
			resEntity = new ResponseEntity(groupModel,HttpStatus.CREATED);
		}catch(Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
	
	@DeleteMapping("")
	public ResponseEntity deleteUser(HttpServletRequest request, @RequestParam String teamId) {
		ResponseEntity resEntity;
		try {
			String accessToken = webex.getAccessTokenValueFromSession(request);
			
			Spark spark = Spark.builder().accessToken(accessToken).build();			
			spark.teams().path("/"+teamId).delete();
			resEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
		}catch(Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}

}
