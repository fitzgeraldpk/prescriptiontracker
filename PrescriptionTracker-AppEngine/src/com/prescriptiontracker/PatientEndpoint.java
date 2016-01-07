package com.prescriptiontracker;

import com.prescriptiontracker.EMF;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.datanucleus.query.JPACursorHelper;

import java.util.List;

import javax.annotation.Nullable;
import javax.inject.Named;
import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import javax.persistence.EntityManager;
import javax.persistence.Query;

@Api(name = "patientendpoint", namespace = @ApiNamespace(ownerDomain = "prescriptiontracker.com", ownerName = "prescriptiontracker.com", packagePath = ""))
public class PatientEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listPatient")
	public CollectionResponse<Patient> listPatient(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Patient> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr.createQuery("select from Patient as Patient");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Patient>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Patient obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Patient> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getPatient")
	public Patient getPatient(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		Patient patient = null;
		try {
			patient = mgr.find(Patient.class, id);
		} finally {
			mgr.close();
		}
		return patient;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param patient the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertPatient")
	public Patient insertPatient(Patient patient) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsPatient(patient)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(patient);
		} finally {
			mgr.close();
		}
		return patient;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param patient the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updatePatient")
	public Patient updatePatient(Patient patient) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsPatient(patient)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(patient);
		} finally {
			mgr.close();
		}
		return patient;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 * @return The deleted entity.
	 */
	@ApiMethod(name = "removePatient")
	public Patient removePatient(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		Patient patient = null;
		try {
			patient = mgr.find(Patient.class, id);
			mgr.remove(patient);
		} finally {
			mgr.close();
		}
		return patient;
	}

	private boolean containsPatient(Patient patient) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Patient item = mgr.find(Patient.class, patient.getPatientId());
			if (item == null) {
				contains = false;
			}
		} finally {
			mgr.close();
		}
		return contains;
	}

	private static EntityManager getEntityManager() {
		return EMF.get().createEntityManager();
	}

}
