package com.ufund.api.ufundapi.model.need;

import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * Represents a Resource based Need entity
 * 
 * @author Meghan Tomback
 */
public class ResourceNeed extends Need {

    /**
     * Create a resource need with the given id, name, description and goal
     * @param id The idea of need
     * @param name The name of then need 
     * @param description The description of the need
     * @param goal The 'help' goal of the need
     */
    public ResourceNeed(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("goal") int goal) {
        super(id, name, description, goal, NeedType.RESOURCE);
    }

    @Override
    public Need createCopy(int id) {
        Need copy = new ResourceNeed(id, this.name, this.description, this.goal);
        copy.setProgress(this.getProgress());
        copy.setListed(this.isListed());
        return copy;
    }


}
