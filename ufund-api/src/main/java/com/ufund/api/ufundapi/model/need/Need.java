package com.ufund.api.ufundapi.model.need;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;


/**
 * Represents a Need entity
 * 
 * @author Meghan Tomback
 */

@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "need-type"
)

@JsonSubTypes({
    @JsonSubTypes.Type(value = ResourceNeed.class, name = "Resource"),
    @JsonSubTypes.Type(value = VolunteerNeed.class, name = "Volunteer"),
    @JsonSubTypes.Type(value = MonetaryNeed.class, name = "Monetary")
})

public abstract class Need {
    /**
     * The need id
     */
    @JsonProperty("id") protected final int id;
    /**
     * The name of the need
     */
    @JsonProperty("name") protected String name;

    /**
     * The description of the need
     */
    @JsonProperty("description") protected String description;

    /**
     * The progress of the need
     */
    @JsonProperty("progress") protected  int progress;

     /**
     * The goal of the need
     */
    @JsonProperty("goal") protected  int goal;

     /**
     * The listing status of the need
     */
    @JsonProperty("listed") protected boolean listed;

     /**
     * The type of the need
     */
    @JsonIgnore
    protected final NeedType type;

    /**
     * Create a need with the given id, name, description and goal
     * @param id The idea of need
     * @param name The name of then need 
     * @param description The description of the need
     * @param goal The 'help' goal of the need
     * @param type the type of need
     */
    public Need(@JsonProperty("id") int id, @JsonProperty("name") String name, @JsonProperty("description") String description, @JsonProperty("goal") int goal, NeedType type) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.progress = 0;
        this.goal = goal;
        this.type = type;
        this.listed = true;
    }

    /**
     * Retrieves the id of the need
     * @return the id of the need
     */
    public int getId() {return id;}

    /**
     * Retrieves the listing status of the need
     * @return the listing status of the need
     */
    public boolean isListed() {return listed;}

    /**
     * Updates the listing status of the need
     * @param listed listing status of the need
     */
    public void setListed(boolean listed) {this.listed = listed;}

    /**
     * Sets the name of the need
     * @param name The new name of the need
     */
    public void setName(String name) {this.name = name;}

     /**
     * Retrieves the name of the need
     * @return the new name of the need
     */
    public String getName() {return name;}

    /**
     * Sets the description of the need
     * @param description The new description of the need
     */
    public void setDescription(String description) {this.description = description;}

     /**
     * Retrieves the description of the need
     * @return the description of the need
     */
    public String getDescription() {return description;}

    /**
     * Updates the progress of a need
     * @param progress The new progress amount of the need
     */
    public void setProgress(int progress) {
        this.progress = this.goal < progress ? this.goal : progress;
    }

     /**
     * Retrieves the progress of the need
     * @return the progress of the need
     */
    public int getProgress() {return progress;}

    /**
     * Sets the goal of the need
     * @param goal The new goal amount for the need
     */
    public void setGoal(int goal) {this.goal = goal;}

     /**
     * Retrieves the goal amount of the need
     * @return the goal of the need
     */
    public int getGoal() {return goal;}

    /**
     * Retrieves the need type of the need
     * @return the type of the need
     */
    public NeedType getType() {return type;}

    /**
     * Retrieves the remaining progress
     * @return remaining progress
     */
    @JsonIgnore
    public int getRemaining() {return (goal - progress);} // set progress can never set the progress higher than the goal no checks needed



    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Need){
            Need compare = (Need) obj;
            return this.id == compare.id && this.type == compare.type && this.goal == compare.goal;
        }
        return false;
    }


    @Override
    public int hashCode() {
        return (this.id + ":" + this.goal).hashCode();
    }

    /**
     * Creates a clone of a given need
     * @param id the id of a need
     * @return the cloned need
     */
    public abstract Need createCopy(int id);
    
}

