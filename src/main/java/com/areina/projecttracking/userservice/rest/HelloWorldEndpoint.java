package com.areina.projecttracking.userservice.rest;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import com.areina.projecttracking.userservice.model.User;
import com.areina.projecttracking.userservice.service.UserService;

import java.sql.SQLException;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.naming.NamingException;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;

@Stateless
@Path("/hello")
public class HelloWorldEndpoint {

	@EJB
	UserService us;

	@GET
	@Produces("text/plain")
	public Response doGet() throws NamingException, SQLException {

		User user = us.getUserById(1);

		try {
			return Response.ok("User with ID = 1 is " + user.getFirst_name() + " " + user.getLast_name()).build();
		} catch (Exception exc) {
			throw exc;
		}

	}
}
