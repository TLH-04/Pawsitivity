package com.ufund.api.ufundapi.model.need;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Represents a Volunteer based Need entity
 * 
 * @author Meghan Tomback
 */
public class VolunteerNeed extends Need {

    /**
     * Create a voltuneer need with the given id, name, description and goal
     * @param id The idea of need
     * @param name The name of then need 
     * @param description The description of the need
     * @param goal The 'help' goal of the need
     */
    public VolunteerNeed(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("goal") int goal) {
        super(id, name, description, goal, NeedType.VOLUNTEER);
    }


    @Override
    public Need createCopy(int id) {
        Need copy = new VolunteerNeed(id, this.name, this.description, this.goal);
        copy.setProgress(this.getProgress());
        copy.setListed(this.isListed());
        return copy;
    }
    
}
