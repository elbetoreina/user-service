package com.areina.projecttracking.userservice.rest;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.persistence.PersistenceException;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

import org.jboss.logging.Logger;

import com.areina.projecttracking.userservice.model.User;
import com.areina.projecttracking.userservice.service.UserService;

@Stateless
@Path("api/users")
public class UserController {

	@EJB
	UserService us;

	Logger logger;

	@GET
	@Produces("application/json")
	@Path("{id}")
	public Response getById(@PathParam("id") String id) {

		Logger logger = Logger.getLogger(UserController.class);

		try {
			User user = us.getUserById(Integer.parseInt(id));
			if (user != null) {
				logger.info(
						"User with ID: " + id + " Found: " + user.getFirst_name() + " " + user.getLast_name() + ".");
				Jsonb jsonb = JsonbBuilder.create();
				String resultJson = jsonb.toJson(user);
				return Response.ok(resultJson).build();
			} else {
				logger.warn("User with ID: " + id + " was not found.");
				return Response.status(404).build();
			}

		} catch (IllegalArgumentException exc) {
			logger.error("Requested ID is with incorrect format - " + exc);
			return Response.status(400).build();
		} catch (JsonbException | NullPointerException exc) {
			logger.error("Error serializing to JSON - " + exc);
			return Response.status(400).build();
		}

	}

	@GET
	@Produces("application/json")
	public Response getAll() {

		Logger logger = Logger.getLogger(UserController.class);

		try {
			List<User> users = us.getAllUsers();
			Jsonb jsonb = JsonbBuilder.create();
			String resultJson = jsonb.toJson(users);
			if (!users.isEmpty()) {
				logger.info("List of all users retrieved.");
				return Response.ok(resultJson).build();
			} else {
				logger.warn("There are no users registered yet.");
				return Response.ok(resultJson).build();
			}

		} catch (JsonbException | NullPointerException exc) {
			logger.error("Error serializing to JSON - " + exc);
			return Response.status(400).build();
		} catch (PersistenceException pExc) {
			return Response.status(500).build();
		}
	}

	@POST
	@Consumes("application/json")
	public Response addUser(String user) {

		Logger logger = Logger.getLogger(UserController.class);
		Jsonb jsonb = JsonbBuilder.create();
		User newUser = jsonb.fromJson(user, User.class);

		try {
			if (newUser.getPassword() == null || newUser.getEmail() == null || newUser.getFirst_name() == null
					|| newUser.getLast_name() == null || newUser.getUser_name() == null) {
				logger.error("Error adding user - one or more fields required are null");
				return Response.status(400).build();
			} else {
				us.addUser(newUser);
				logger.info("User added: " + newUser.getFirst_name() + " " + newUser.getLast_name());
				return Response.status(201).build();
			}

		} catch (PersistenceException | InvalidKeySpecException | NoSuchAlgorithmException exc) {
			logger.error("Error adding User: " + newUser.getFirst_name() + " " + newUser.getLast_name() + exc);
			return Response.status(500).build();
		} catch (JsonbException | NullPointerException exc) {
			logger.error("Error serializing to JSON - " + exc);
			return Response.status(400).build();
		}
	}

	@DELETE
	@Path("{id}")
	public Response deleteUser(@PathParam("id") String id) {

		Logger logger = Logger.getLogger(UserController.class);

		try {
			User user = us.getUserById(Integer.parseInt(id));
			if (user != null) {
				
				logger.warn("User with ID: " + id + " will be deleted: " + user.getFirst_name() + " "
						+ user.getLast_name() + ".");				
				
				us.removeUser(user);
				
				return Response.status(200).build();
				
			} else {
				logger.warn("User with ID: " + id + " was not found.");
				return Response.status(404).build();
			}

		} catch (IllegalArgumentException exc) {
			logger.error("Requested ID is with incorrect format - " + exc);
			return Response.status(400).build();
		} 

	}
}
