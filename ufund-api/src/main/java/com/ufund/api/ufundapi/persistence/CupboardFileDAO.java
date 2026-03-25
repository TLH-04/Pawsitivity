package com.ufund.api.ufundapi.persistence;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ufund.api.ufundapi.model.need.Need;
/**
 * Implements the functionality for JSON file-based persistence for Needs
 * 
 * {@literal @}Component Spring annotation instantiates a single instance of this
 * class and injects the isntance into other classes as needed
 * 
 * @author Anthony Lansing
 */
@Component
public class CupboardFileDAO implements CupboardDAO {
    private static final Logger LOG = Logger.getLogger(CupboardFileDAO.class.getName());
    private Map<Integer,Need> needs;
    
    private final ObjectMapper objectMapper;

    
    private final static AtomicInteger nextId = new AtomicInteger();
    private final String filename;
    /**
     * Creates a Cupboard File Data Access Object
     * 
     * @param filename Filename to read/write
     * @param objectMapper Provides JSON Object to/from Java Object
     * 
     * @throws IOException when file cannot be accessed
     */
    public CupboardFileDAO(@Value("${needs.file}") String filename, ObjectMapper objectMapper) throws IOException {
        this.filename = filename;
        this.objectMapper = objectMapper;
        load();
    }



    /**
     * Generates the next id for a new {@linkplain Need hero}
     * 
     * @return the next id
     */
    private synchronized static int nextId() {
        int id = nextId.incrementAndGet();
        return id;
    }

    /**
     * Generates an array of {@linkplain Need needs}
     * 
     * @return The array of {@link Need needs}, may be empty
     */
    private Need[] getNeedsArray() {
    
        return getNeedsArray(null);
    }

    /**
     * Generates an array of {@linkplain Need needs} of all {@linkplain Need needs}
     * that contain the given containsText. If null, all needs are returned
     * 
     * @return The array of {@link Need needs}
     */
    private Need[] getNeedsArray(String containsText) {
        ArrayList<Need> needArrayList = new ArrayList<>();

        for (Need need : needs.values()) {
            if (containsText == null || need.getName().toLowerCase().contains(containsText.toLowerCase()))
                needArrayList.add(need);
        }

        Need[] needArray = new Need[needArrayList.size()];
        needArrayList.toArray(needArray);
        return needArray;
    }

    /**
     * Saves the {@linkplain Need needs} from the map into the file as an array of JSON objects
     * 
     * @return true if written successfully, throws exception otherwise
     * 
     * @throws IOException when the file cannot be written to
     */
    private boolean save() throws IOException {
        Need[] needsArray = getNeedsArray();

        // serialize the java object into JSON and write to file
        objectMapper.writeValue(new File(filename), needsArray);
        return true;
    }

    /**
     * Loads {@linkeplain Need needs} from JSON file into the map
     * <br>
     * also sets id to one after the max id found
     * 
     * @return true if successful, throws expcetion otherwise
     * 
     * @throws IOException if file cannot be read from
     */
    private void load() throws IOException {
        needs = new TreeMap<>();

        // deserialize the JSON objects to Java
        Need[] needsArray = objectMapper.readValue(new File(filename), Need[].class);
        // Add each need to the tree map and keep track of the highest id
        for (Need need: needsArray) {
            needs.put(need.getId(), need);
            if (need.getId() > nextId.get())
                nextId.set(need.getId());
        }

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Need[] getNeeds() {
        synchronized(needs) {
            return getNeedsArray();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need[] findNeeds(String containsText) {
        synchronized(needs) {
            return getNeedsArray(containsText);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need getNeed (int id)  {
        synchronized(needs) {
            if (needs.containsKey(id))
                return needs.get(id);
            else
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need createNeed(Need need) throws IOException {
        synchronized(needs) {
            LOG.info(need.isListed() + " OLD");
            Need newNeed = need.createCopy(nextId());
            LOG.info(newNeed.isListed() + "");
            needs.put(newNeed.getId(), newNeed);
            save();
            return newNeed;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need updateNeed(Need need) throws IOException {
        synchronized(needs) {
            if (!needs.containsKey(need.getId()))
                return null;
            else{
                needs.put(need.getId(), need);
                save();
                return need;
            }
        }
    }

     /**
     * {@inheritDoc}
     */
    @Override
    public void updateNeeds(Map<Integer,Integer> needMap) throws IOException {
        synchronized(needs){
            for (Map.Entry<Integer, Integer> entry : needMap.entrySet()) {
                int needId = entry.getKey();
                int amount = entry.getValue();
                if (needs.containsKey(needId)){
                    Need need = needs.get(needId);
                    need.setProgress(need.getProgress() + amount);

                    if(need.getProgress() == need.getGoal()){
                        need.setListed(false);
                    }
                }
            }
            save();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need deleteNeed(int id) {
        synchronized(needs) {
            if (needs.containsKey(id)) {
                Need n = needs.get(id);
                needs.remove(id);
                return n;
            }
            else
                return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getListingStatus(int id){
        synchronized (needs) {
            Need n = needs.get(id);
            return n.isListed();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Need updateListingStatus(Need need) throws IOException {
        synchronized (needs) {
            Need storedNeed = needs.get(need.getId());

            if(storedNeed == null){
                return null;
            
            } else {
                storedNeed.setListed(!storedNeed.isListed());
                save();
                return storedNeed;
            }
            
        }
    }
}
