package com.libsys.onlinemeeting.controller.microsoft;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.libsys.onlinemeeting.model.EventModel;
import com.microsoft.aad.msal4j.IAuthenticationResult;
import com.microsoft.graph.models.extensions.DateTimeTimeZone;
import com.microsoft.graph.models.extensions.Event;
import com.microsoft.graph.models.extensions.ItemBody;
import com.microsoft.graph.models.generated.BodyType;
import com.microsoft.graph.models.generated.OnlineMeetingProviderType;
import com.microsoft.graph.options.HeaderOption;

/**
 * Contains operations related to micrsoft events in calendar
 * 
 * @author Abhijeet Saxena
 *
 */
@RestController("Microsoft_EventController")
@RequestMapping(Constants.VendorPath.MICROSOFT + "/event")
public class EventController {
	@Autowired
	private Microsoft microsoft;
	@Autowired
	private HelperMethods helper;
	@Autowired
	private GraphServiceClientWrapper graphClientWrapper;
	@Autowired
	private SessionManagementHelper sessionManagementHelper;

	/**
	 * 
	 * Create events with the given details. If online meeting parameter is
	 * specified, then create an online meeting on the skype. 
	 * Get IAuthenticationResult object from session. 
	 * Set access token in header.
	 * Build request and get response.
	 * 
	 * @param request
	 * @param List<EventModel>
	 * @return If success, HttpStatus 204 and List<EventModel>, else HttpStatus
	 *         500 and error msg
	 */
	@PostMapping("")
	public ResponseEntity createEvent(HttpServletRequest request, @RequestBody List<EventModel> eventModels) {
		ResponseEntity resEntity;
//		IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper.getSessionPrincipal(request);
		String accessToken = microsoft.getAccessTokenFromSession(request);
		HeaderOption option = new HeaderOption("Authorization", "Bearer " + accessToken);

		for (EventModel eventModel : eventModels) {
			try {

				Event event = new Event();
				event.subject = eventModel.getSubject();

				ItemBody body = new ItemBody();
				body.contentType = eventModel.isHtml() ? BodyType.HTML : BodyType.TEXT;
				body.content = eventModel.getBody();
				event.body = body;

				DateTimeTimeZone start = new DateTimeTimeZone();

				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

				start.dateTime = sdf.format(eventModel.getStartDatetime());
				Calendar cal = Calendar.getInstance();
				cal.setTime(eventModel.getStartDatetime());
				start.timeZone = cal.getTimeZone().getID();
				event.start = start;
				DateTimeTimeZone end = new DateTimeTimeZone();
				end.dateTime = sdf.format(eventModel.getEndDatetime());
				cal.setTime(eventModel.getEndDatetime());
				end.timeZone = cal.getTimeZone().getID();
				event.end = end;

				if (eventModel.isOnlineMeeting()) {
					event.isOnlineMeeting = eventModel.isOnlineMeeting();
					event.onlineMeetingProvider = OnlineMeetingProviderType.SKYPE_FOR_CONSUMER;
				}

				if (eventModel.isSetReminder()) {
					event.isReminderOn = true;
				}

				Event res = graphClientWrapper.getGraphServiceClient().me().events().buildRequest(Arrays.asList(option))
						.post(event);

				eventModel.setId(res.id);
				eventModel.setOnlineMeetingUrl(res.onlineMeetingUrl);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		resEntity = new ResponseEntity(eventModels,HttpStatus.CREATED);
		return resEntity;
	}

	/**
	 * Update event details. Only updates the details provided. 
	 * @param request
	 * @param eventModel
	 * @return HttpStatus 201 and event model. On failure HttpStatus 500 and error message
	 */
	@PatchMapping("")
	public ResponseEntity updateEvent(HttpServletRequest request, @RequestBody EventModel eventModel) {
		ResponseEntity resEntity;
		try {
//			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper
//					.getSessionPrincipal(request);
			String accessToken = microsoft.getAccessTokenFromSession(request);

			Event event = new Event();
			if (eventModel.getSubject() != null) {
				event.subject = eventModel.getSubject();
			}

			if (eventModel.getBody() != null) {
				ItemBody body = new ItemBody();
				body.contentType = eventModel.isHtml() ? BodyType.HTML : BodyType.TEXT;
				body.content = eventModel.getBody();
				event.body = body;
			}
			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
			if (eventModel.getStartDatetime() != null) {
				DateTimeTimeZone start = new DateTimeTimeZone();

				start.dateTime = sdf.format(eventModel.getStartDatetime());
				cal.setTime(eventModel.getStartDatetime());
				start.timeZone = cal.getTimeZone().getID();
				event.start = start;

			}

			if (eventModel.getEndDatetime() != null) {
				DateTimeTimeZone end = new DateTimeTimeZone();
				end.dateTime = sdf.format(eventModel.getEndDatetime());
				cal.setTime(eventModel.getEndDatetime());
				end.timeZone = cal.getTimeZone().getID();
				event.end = end;
			}
			if (eventModel.isOnlineMeeting()) {
				event.isOnlineMeeting = eventModel.isOnlineMeeting();
				event.onlineMeetingProvider = OnlineMeetingProviderType.SKYPE_FOR_CONSUMER;
			}

			if (eventModel.isSetReminder()) {
				event.isReminderOn = true;
			}

			HeaderOption option = new HeaderOption("Authorization", "Bearer " + accessToken);
			Event res = graphClientWrapper.getGraphServiceClient().me().events(eventModel.getId())
					.buildRequest(Arrays.asList(option)).patch(event);

			eventModel.setId(res.id);
			eventModel.setOnlineMeetingUrl(res.onlineMeetingUrl);
			resEntity = new ResponseEntity(eventModel, HttpStatus.CREATED);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}

	/**
	 * Delete event based on the given eventId
	 * @param request
	 * @param eventId
	 * @return
	 */
	@DeleteMapping("")
	public ResponseEntity deleteEvent(HttpServletRequest request, @RequestParam String eventId) {
		ResponseEntity resEntity;
		try {
//			IAuthenticationResult result = (IAuthenticationResult) sessionManagementHelper
//					.getSessionPrincipal(request);
			String accessToken = microsoft.getAccessTokenFromSession(request);

			HeaderOption option = new HeaderOption("Authorization", "Bearer " + accessToken);
			graphClientWrapper.getGraphServiceClient().me().events(eventId).buildRequest(Arrays.asList(option))
					.delete();
			resEntity = new ResponseEntity(HttpStatus.NO_CONTENT);
		} catch (Throwable e) {
			e.printStackTrace();
			resEntity = new ResponseEntity(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return resEntity;
	}
}
