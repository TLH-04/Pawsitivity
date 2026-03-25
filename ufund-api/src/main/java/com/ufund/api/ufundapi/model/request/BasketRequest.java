package com.ufund.api.ufundapi.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * A Json Body Class for Basket Change Requests
 * 
 * Used for CRUD operations
 * 
 * @author Jason Ugbaja
 */
public class BasketRequest {
    @JsonProperty("id") int id; 
    @JsonProperty("amount") int amount; 

    /**
     * The Creation of a Basket Request
     * 
     * @param id the id of the need you want to edit
     * @param amount the amount you modifying the need by
     */
    public BasketRequest(@JsonProperty("id") int id,@JsonProperty("amount") int amount){
        this.id = id;
        this.amount = amount;
    }

    /**
     * The amount in the request
     * @return the amount in the request body
     */
    public int getAmount() {
        return amount;
    }

    /**
     * the id of need 
     * @return the id of need in the request
     */
    public int getId() {
        return id;
    }

}
