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

@Api(name = "prescriptionendpoint", namespace = @ApiNamespace(ownerDomain = "prescriptiontracker.com", ownerName = "prescriptiontracker.com", packagePath = ""))
public class PrescriptionEndpoint {

	/**
	 * This method lists all the entities inserted in datastore.
	 * It uses HTTP GET method and paging support.
	 *
	 * @return A CollectionResponse class containing the list of all entities
	 * persisted and a cursor to the next page.
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	@ApiMethod(name = "listPrescription")
	public CollectionResponse<Prescription> listPrescription(
			@Nullable @Named("cursor") String cursorString,
			@Nullable @Named("limit") Integer limit) {

		EntityManager mgr = null;
		Cursor cursor = null;
		List<Prescription> execute = null;

		try {
			mgr = getEntityManager();
			Query query = mgr
					.createQuery("select from Prescription as Prescription");
			if (cursorString != null && cursorString != "") {
				cursor = Cursor.fromWebSafeString(cursorString);
				query.setHint(JPACursorHelper.CURSOR_HINT, cursor);
			}

			if (limit != null) {
				query.setFirstResult(0);
				query.setMaxResults(limit);
			}

			execute = (List<Prescription>) query.getResultList();
			cursor = JPACursorHelper.getCursor(execute);
			if (cursor != null)
				cursorString = cursor.toWebSafeString();

			// Tight loop for fetching all entities from datastore and accomodate
			// for lazy fetch.
			for (Prescription obj : execute)
				;
		} finally {
			mgr.close();
		}

		return CollectionResponse.<Prescription> builder().setItems(execute)
				.setNextPageToken(cursorString).build();
	}

	/**
	 * This method gets the entity having primary key id. It uses HTTP GET method.
	 *
	 * @param id the primary key of the java bean.
	 * @return The entity with primary key id.
	 */
	@ApiMethod(name = "getPrescription")
	public Prescription getPrescription(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		Prescription prescription = null;
		try {
			prescription = mgr.find(Prescription.class, id);
		} finally {
			mgr.close();
		}
		return prescription;
	}

	/**
	 * This inserts a new entity into App Engine datastore. If the entity already
	 * exists in the datastore, an exception is thrown.
	 * It uses HTTP POST method.
	 *
	 * @param prescription the entity to be inserted.
	 * @return The inserted entity.
	 */
	@ApiMethod(name = "insertPrescription")
	public Prescription insertPrescription(Prescription prescription) {
		EntityManager mgr = getEntityManager();
		try {
			if (containsPrescription(prescription)) {
				throw new EntityExistsException("Object already exists");
			}
			mgr.persist(prescription);
		} finally {
			mgr.close();
		}
		return prescription;
	}

	/**
	 * This method is used for updating an existing entity. If the entity does not
	 * exist in the datastore, an exception is thrown.
	 * It uses HTTP PUT method.
	 *
	 * @param prescription the entity to be updated.
	 * @return The updated entity.
	 */
	@ApiMethod(name = "updatePrescription")
	public Prescription updatePrescription(Prescription prescription) {
		EntityManager mgr = getEntityManager();
		try {
			if (!containsPrescription(prescription)) {
				throw new EntityNotFoundException("Object does not exist");
			}
			mgr.persist(prescription);
		} finally {
			mgr.close();
		}
		return prescription;
	}

	/**
	 * This method removes the entity with primary key id.
	 * It uses HTTP DELETE method.
	 *
	 * @param id the primary key of the entity to be deleted.
	 * @return The deleted entity.
	 */
	@ApiMethod(name = "removePrescription")
	public Prescription removePrescription(@Named("id") String id) {
		EntityManager mgr = getEntityManager();
		Prescription prescription = null;
		try {
			prescription = mgr.find(Prescription.class, id);
			mgr.remove(prescription);
		} finally {
			mgr.close();
		}
		return prescription;
	}

	private boolean containsPrescription(Prescription prescription) {
		EntityManager mgr = getEntityManager();
		boolean contains = true;
		try {
			Prescription item = mgr.find(Prescription.class,
					prescription.getPrescriptionId());
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
