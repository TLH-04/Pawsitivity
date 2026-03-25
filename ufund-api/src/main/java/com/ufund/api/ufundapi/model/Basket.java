package com.ufund.api.ufundapi.model;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufund.api.ufundapi.model.need.Need;

/**
 * Represents a Basket entity
 * 
 * @author Trinity Hampton
 */

public class Basket {
    // private static final Logger LOG = Logger.getLogger()
    
    @JsonProperty("needsMap") private final HashMap<Integer,Integer> needsMap;

    /**
     * Create a Needs basket
     */
    public Basket(){
        needsMap = new HashMap<>();
    }

    
    /**
     * Retreives list of current needs within basket
     * @return List of currently added needs
     */
    @JsonIgnore
    public HashMap<Integer,Integer> getBasketContents() {return needsMap;}

    /**
     * Retreives quanity of need in basket
     * @return quanity if the need is found
     * @param id the need id you checking in basket
     * @throws Exception if need isnt found
     */
    public int getNeedAmount(int id) throws Exception{
        if (this.hasNeed(id)){
            return needsMap.get(id);
        }
        throw new Exception("Need id not found");
    }




    
    /**
     * Adds given Need to Basket
     * @param needId The need id to be added to the basket
     * @param amount the amount you are adding to the basket
     * 
     * @return true - need added to basket without conflict
     * false - need not added to basket due to conflict
     */
    public Boolean addNeed(int needId, int amount){
        if(needsMap.containsKey(needId)){
            needsMap.put(needId,needsMap.get(needId) + amount);
        } else {
            needsMap.put(needId, amount);
        }
        return true;
    }

    /**
     * Adds given Need to Basket
     * @param need The need to be added to the basket
     * @param amount the amount you are adding to the basket
     * @return true - need added to basket without conflict
     * false - need not added to basket due to conflict
     */
    public Boolean addNeed(Need need, int amount){
        return this.addNeed(need.getId(),amount);
    }

    /**
     * Sets the amount of a need in Basket
     * @param id the id of the need
     * @param amount the quanity to set in basket
     */
    public void setNeed(int id, int amount){
        this.needsMap.put(id, amount);
    }

    /**
     * Checks if a basket has a need or not
     * @param id the id of the need {@link Need}
     * @return weather the id was found in the basket
     */
    public Boolean hasNeed(int id){
        return needsMap.containsKey(id);
    }

    /**
     * Removes need from basket with given id
     * @param id The id of the need {@link Need}
     * @param amount the quanity to remove in basket
     *
     */
    public void removeNeed(int id, int amount){
        if (needsMap.containsKey(id)){
            if (needsMap.get(id) - amount <= 0){
                needsMap.remove(id);
            } else {
                needsMap.put(id, needsMap.get(id) - amount);
            }
        }
    }

    /**
     * Removes need from basket with given need
     * 
     * @param need selected need
    */
    public void removeNeed(Need need){
        this.removeNeed(need.getId(), 1);
    }


    /**
     * Removes all needs from basket and updates the progress;
     * @return List of the updated needs
     */
    public Map<Integer,Integer> checkout(){
        HashMap<Integer,Integer> nList = new HashMap<>(needsMap);
        needsMap.clear();
        return nList;
    }

    /**
     * @return String of basket
     */
    @Override
    public String toString() {
        return "Basket: " + needsMap.toString();
    }


    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Basket){
            Basket compare = (Basket) obj;
            return this.hashCode() == compare.hashCode();
        }
        return false;
    }

    @Override
    public int hashCode() {
        return this.toString().hashCode();
    }

    
    
}
