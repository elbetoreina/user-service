package com.areina.projecttracking.userservice.service;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityManager;
import javax.persistence.LockTimeoutException;
import javax.persistence.PersistenceContext;
import javax.persistence.PersistenceException;
import javax.persistence.PessimisticLockException;
import javax.persistence.QueryTimeoutException;
import javax.persistence.TransactionRequiredException;
import javax.persistence.TypedQuery;

import org.jboss.logging.Logger;
import com.areina.projecttracking.userservice.model.User;
import com.areina.projecttracking.userservice.security.Hash;

/**
 * Session Bean implementation class UserService
 */
@Stateless
public class UserService {

	/**
	 * Default constructor.
	 */

	@PersistenceContext(unitName = "track-projects-Unit")
	private EntityManager em;	

	private Logger logger;

	public UserService() {
		logger = Logger.getLogger(UserService.class);
	}

	public User getUserById(int id) {
		try {
			User user = em.find(User.class, id);
			logger.info("A search for the user with id " + id + " was performed.");
			return user;

		} catch (IllegalArgumentException exc) {
			logger.error("Error ocurred while searching user with id " + id + ": " + exc);
			throw new IllegalArgumentException(exc.getMessage());
		}
	}

	public List<User> getAllUsers() throws PersistenceException {

		Logger logger = Logger.getLogger(UserService.class);

		try {
			TypedQuery<User> query = em.createQuery("SELECT u FROM User u", User.class);
			List<User> users = query.getResultList();
			logger.info("A search for all users was performed.");
			return users;

		} catch (IllegalStateException | QueryTimeoutException | TransactionRequiredException | PessimisticLockException
				| LockTimeoutException exc) {
			logger.error("Error ocurred while searching for all users: " + exc);
			throw new PersistenceException(exc.getMessage());
		}
	}

	public void addUser(User user) throws NoSuchAlgorithmException, InvalidKeySpecException {

		try {
			logger.info("Agregando usuario:  " + user.getFirst_name() + " " + user.getLast_name());
			
			

			if (user.getPassword() != null) {
				String passwordWithSalt = Hash.generatePassword(user.getPassword());
				logger.warn("Password With Salt: " + passwordWithSalt);
				String password = passwordWithSalt.substring(0, passwordWithSalt.indexOf("-"));
				String salt = passwordWithSalt.substring(passwordWithSalt.indexOf("-") + 1,
						passwordWithSalt.length());
				logger.warn("Password: " + password);
				logger.warn("Salt: " + salt);
				user.setPassword(password);
				user.setSalt(salt);
				
				em.persist(user);
			}

		} catch (NoSuchAlgorithmException | InvalidKeySpecException naExc) {
			logger.error("Error ocurred while adding user: " + naExc);
			throw new SecurityException(naExc.getMessage());
		} catch (EntityExistsException | IllegalArgumentException | TransactionRequiredException exc) {
			logger.error("Error ocurred while adding user: " + exc);
			throw new PersistenceException(exc.getMessage());
		}

	}
	
	public void removeUser(User user){

		try {
			
			em.remove(user);
			logger.warn("User with ID " + user.getID() + " removed.");

		} catch (IllegalArgumentException exc) {
			logger.error("Error ocurred while deleting user with id " + user.getID() + ": " + exc);
			throw new IllegalArgumentException(exc.getMessage());
		}

	}
}
