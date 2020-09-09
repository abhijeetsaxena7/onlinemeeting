package com.libsys.onlinemeeting.config;

import java.util.Date;

/**\
 * 
 * @author abhij
 * This class is used to authenticated state and nonce value in microsoft. 
 */
public class StateData {

	private String nonce;
	private Date expirationDate;

	StateData(String nonce, Date expirationDate) {
		this.nonce = nonce;
		this.expirationDate = expirationDate;
	}

	public String getNonce() {
		return nonce;
	}

	public Date getExpirationDate() {
		return expirationDate;
	}

}
