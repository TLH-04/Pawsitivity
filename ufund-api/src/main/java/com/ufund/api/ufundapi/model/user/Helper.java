package com.ufund.api.ufundapi.model.user;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.ufund.api.ufundapi.model.Basket;

/**
 * Represents a Helper User
 * 
 * @author Jason Ugbaja
 */
public class Helper extends User {

    @JsonProperty("basket") Basket basket;
    @JsonProperty("contributedNeeds") int contributedNeeds;

    
     /**
     *  Creates a new helper with a pre extisting basket
     *  @param username the username of the helper
     *  @param basket the preexisting basket
     *  @param password the password of the user
     */
    public Helper(String username, String password ,@JsonProperty("basket") Basket basket) {
        super(username,password, UserType.HELPER);
        this.basket = basket;
        this.contributedNeeds = 0;
    }

    /**
     *  Creates a new helper with a pre extisting basket from a Json string
     *  @param username the username of the helper
     *  @param basket the preexisting basket
     *  @param password the password of the user
     *  @param hashed the status if the password is hashed
     */
    @JsonCreator
    public Helper(String username, String password ,@JsonProperty("basket") Basket basket,boolean hashed) {
        super(username,password, UserType.HELPER,hashed);
        this.basket = basket;
        this.contributedNeeds = 0;
    }

     /**
     *  Creates a new helper with a new basket being created
     *  @param username the username of the helper
     *  @param password the password of the user
     */
    public Helper(String username, String password) {
        super(username, password , UserType.HELPER);
        this.basket = new Basket();
        this.contributedNeeds = 0;
    }

    /**
     * gets the basket of the helper
     * @return the basket attached to the helper
     */
    public Basket getBasket() {
        return basket;
    }

    /**
     * gets the number of needs contibuted to by the helper
     * @return the number
     */
    public int getContributedNeeds() {
        return contributedNeeds;
    }

    /**
     * updates the count of needs contributed to
     * @param checkedOutAmount the amount of needs being contibuted to
     */
    public void updateContributedNeeds(int checkedOutAmount) {
        if(checkedOutAmount > 0){
            contributedNeeds += checkedOutAmount;
        }
    }
    
    

}
