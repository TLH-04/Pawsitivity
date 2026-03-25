package com.ufund.api.ufundapi.persistence;

import java.io.IOException;
import java.util.Map;

import com.ufund.api.ufundapi.model.need.Need;

/**
 * Defines the interface for Need object persistence
 * 
 * @author Anthony Lansing
 */
public interface CupboardDAO {

    /**
     * Retrieves all {@linkplain Need needs}
     * 
     * @return An array of {@link Need need} objects, may be empty
     * 
     * @throws IOException if an issue with storage
     */
    Need[] getNeeds() throws Exception;

    /**
     * Finds all {@linkplain Need needs} whose name contains the given text
     * 
     * @param containsText The text to match against
     * 
     * @return An array of {@link Need need} objects whose name contains the given text
     * 
     * @throws Exception if an issue with storage
     */
    Need[] findNeeds(String containsText) throws Exception;

    /**
     * Retrieves a {@linkplain Need need} with a given id
     * 
     * @param id The id of the {@link Need need} object
     * 
     * @return a {@link Need need} object with the matching need
     * <br>
     * will be null if no Need exists with the given id.
     * 
     * @throws Exception if an issue with storage
     */
    Need getNeed(int id) throws Exception;

    /**
     * Creates and saves a {@linkplain Need need}
     * 
     * @param need {@linkplain Need need} object to be stored in the cupboard
     * <br>
     * a unique id will be assigned.
     * 
     * @return new {@link Need need} if successful, null otherwise
     * 
     * @throws Exception if an issue with storage
     */
    Need createNeed(Need need) throws Exception;

    /**
     * Updates and saves a {@linkplain Need need} in the cupboard
     * 
     * @param need {@link Need need} object to be updated and saved
     * 
     * @return updated {@link Need need} if successful, null otherwise
     * 
     * @throws Exception if issues with storage
     */
    Need updateNeed(Need need) throws Exception;


    /**
     * Updates and saves a List of {@linkplain Need need} in the cupboard; used on checkout
     * 
     * @param needList  Map of Ids,{@link Need need} object to be updated and saved
     * 
     * @throws Exception if issues with storage
     */
    void updateNeeds(Map<Integer,Integer> needList) throws Exception;

     /**
      * Deletes a {@linkplain Need need} with the given id
      *
      * @param id the id of the {@link Need need} object to be deleted
      *
      * @return true if successful, false if {@link Need need} was not found
      *
      * @throws Exception if issue with storage.
      */
    Need deleteNeed(int id) throws Exception;

    /**
     * Retrieves listing status of a {@linkplain Need need} with a given id
     * 
     * @param id The id of the {@link Need need} object
     * 
     * @return listing status
     * <br>
     * will be null if no Need exists with the given id.
     * 
     * @throws Exception if an issue with storage
     */
    boolean getListingStatus(int id) throws Exception;

    /**
     * Updates (Toggles) and saves the listing status of a {@linkplain Need need} in the cupboard
     * 
     * @param need {@link Need need} object to be updated and saved
     * 
     * @throws Exception if issues with storage
     * 
     * @return updated need
     */
    Need updateListingStatus(Need need) throws Exception;
}
