package com.libsys.onlinemeeting.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.libsys.onlinemeeting.config.constant.Constants;
import com.libsys.onlinemeeting.config.constant.Constants.Vendors;

/**
 * 
 * @author Abhijeet Saxena Contains utility methods to be used in project.
 */
@Component
public class HelperMethods {
	@Autowired
	ObjectMapper mapper;

	/**
	 * Check whether vendor attribute is present in session.
	 * 
	 * @param request
	 * @return
	 */
	public boolean hasVendor(HttpServletRequest request) {
		return request.getSession() != null && request.getSession().getAttribute(Constants.Attributes.VENDOR) != null;
	}

	/**
	 * Get vendor Id from session attribute
	 * 
	 * @param request
	 * @return
	 */
	public int getVendor(HttpServletRequest request) {
		return (int) request.getSession().getAttribute(Constants.Attributes.VENDOR);
	}

	/**
	 * Set vendor Id in request session attribute based on path.
	 * 
	 * @param request
	 * @param path
	 */
	public void setVendor(HttpServletRequest request, String path) {
		Vendors vendor = null;
		if (path.contains("/" + Vendors.Microsoft.getPath() + "/") || path.contains("/login.microsoftonline.com/")) {
			vendor = Vendors.Microsoft;
		} else if (path.contains("/" + Vendors.Webex.getPath() + "/") || path.contains("/webexapis.com/")) {
			vendor = Vendors.Webex;
		} else if (path.contains("/" + Vendors.Zoom.getPath() + "/") || path.contains("/zoom.us/")) {
			vendor = Vendors.Zoom;
		}

		if (vendor == null) {
			throw new RuntimeException("Invalid url path accessed");
		}
		request.getSession(true).setAttribute(Constants.Attributes.VENDOR, vendor.getId());
	}

	/**
	 * Build a url based on url and query params.
	 * 
	 * @param url
	 * @param queryParams
	 * @return encoded url
	 */
	public String getUri(String url, MultiValueMap<String, String> queryParams) {
		return getUri(url, queryParams, true);
	}

	public String getUri(String url, MultiValueMap<String, String> queryParams, boolean encode) {
		UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(url).queryParams(queryParams);
		if (encode) {
			return uriBuilder.encode().build().toString();
		}

		return uriBuilder.build().toString();

	}

	public String encodeUrl(String url) throws UnsupportedEncodingException {
		return URLEncoder.encode(url, StandardCharsets.UTF_8.toString());
	}

	public byte[] getBytes(Object data) {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream out = null;
		byte[] byteData = null;
		try {
			out = new ObjectOutputStream(bos);
			out.writeObject(data);
			out.flush();
			byteData = bos.toByteArray();

		} catch (IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot convert object to byte array");
		} finally {
			try {
				bos.close();
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException("Cannot convert object to byte array");
			}
		}

		return byteData;
	}

	private Object getObjectFromByte(byte[] bytes) {
		ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
		ObjectInput in = null;
		Object obj =null;
		try {
			in = new ObjectInputStream(bis);
			obj = in.readObject();
		} catch (ClassNotFoundException | IOException e) {
			e.printStackTrace();
			throw new RuntimeException("Cannot get object from bytes");
		} finally {
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
				throw new RuntimeException("Cannot get object from bytes");
			}
		}
		return obj;
	}
	
	public String getStringForObject(Object obj) {
		try {
			return mapper.writeValueAsString(obj);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Object getObjectFromString(String obj, Class clas) {
		try {
			return mapper.readValue(obj,clas);
		} catch (JsonProcessingException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	public Object getAuthObjectFromSession(HttpServletRequest request) {
		return request.getSession().getAttribute(Constants.Session.AUTH_OBJECT);
	}

}
