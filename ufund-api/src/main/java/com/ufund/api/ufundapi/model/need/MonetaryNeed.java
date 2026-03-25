package com.ufund.api.ufundapi.model.need;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Monetary based Need entity
 * 
 * @author Meghan Tomback
 */
public class MonetaryNeed extends Need{

    /**
     * Create a monetary need with the given id, name, description and goal
     * @param id The idea of need
     * @param name The name of then need 
     * @param description The description of the need
     * @param goal The 'help' goal of the need
     */
    public MonetaryNeed(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("goal") int goal) {
        super(id, name, description, goal, NeedType.MONETARY);
    }

    @Override
    public Need createCopy(int id) {
        Need copy = new MonetaryNeed(id, this.name, this.description, this.goal);
        copy.setProgress(this.getProgress());
        copy.setListed(this.isListed());
        return copy;
    }
    
}
