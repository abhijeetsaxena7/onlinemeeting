package com.libsys.onlinemeeting.config.auth;

import org.springframework.data.repository.CrudRepository;

public interface AuthRepo extends CrudRepository<AuthTbl, String> {
	
}
