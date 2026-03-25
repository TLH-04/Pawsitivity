package com.ufund.api.ufundapi.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import com.ufund.api.ufundapi.model.need.MonetaryNeed;
import com.ufund.api.ufundapi.model.need.Need;
import com.ufund.api.ufundapi.model.need.NeedType;
import static com.ufund.api.ufundapi.model.need.NeedType.MONETARY;
import static com.ufund.api.ufundapi.model.need.NeedType.RESOURCE;
import static com.ufund.api.ufundapi.model.need.NeedType.VOLUNTEER;
import com.ufund.api.ufundapi.model.need.ResourceNeed;
import com.ufund.api.ufundapi.model.need.VolunteerNeed;



/**
 * Unit tests for Need class
 * 
 * @author Trinity Hampton
 */
@Tag("Model-tier")
public class NeedTest {
    @Test
    public void testMonetaryNeedCreation(){
        // Setup
        int expected_id = 1;
        String expected_name = "dummy need";
        String expected_desc = "dummy";
        int expected_progress = 0;
        int expected_goal = 100;
        NeedType expected_type = MONETARY;

        // Invoke
        Need monetaryNeed = new MonetaryNeed(expected_id, expected_name, expected_desc, expected_goal);

        // Assert
        assertEquals(monetaryNeed.getId(), expected_id);
        assertEquals(monetaryNeed.getName(), expected_name);
        assertEquals(monetaryNeed.getDescription(), expected_desc);
        assertEquals(monetaryNeed.getGoal(), expected_goal);
        assertEquals(monetaryNeed.getProgress(), expected_progress);
        assertEquals(monetaryNeed.getType(), expected_type);
        
    }


    @Test
    public void testResourceNeedCreation(){
        // Setup
        int expected_id = 1;
        String expected_name = "dummy need";
        String expected_desc = "dummy";
        int expected_progress = 0;
        int expected_goal = 100;
        NeedType expected_type = RESOURCE;

        // Invoke
        Need resourceNeed = new ResourceNeed(expected_id, expected_name, expected_desc, expected_goal);

        // Assert
        assertEquals(resourceNeed.getId(), expected_id);
        assertEquals(resourceNeed.getName(), expected_name);
        assertEquals(resourceNeed.getDescription(), expected_desc);
        assertEquals(resourceNeed.getGoal(), expected_goal);
        assertEquals(resourceNeed.getProgress(), expected_progress);
        assertEquals(resourceNeed.getType(), expected_type);
        
    }


    @Test
    public void testVolunteerNeedCreation(){
        // Setup
        int expected_id = 1;
        String expected_name = "dummy need";
        String expected_desc = "dummy";
        int expected_progress = 0;
        int expected_goal = 100;
        NeedType expected_type = VOLUNTEER;

        // Invoke
        Need volunteerNeed = new VolunteerNeed(expected_id, expected_name, expected_desc, expected_goal);

        // Assert
        assertEquals(volunteerNeed.getId(), expected_id);
        assertEquals(volunteerNeed.getName(), expected_name);
        assertEquals(volunteerNeed.getDescription(), expected_desc);
        assertEquals(volunteerNeed.getGoal(), expected_goal);
        assertEquals(volunteerNeed.getProgress(), expected_progress);
        assertEquals(volunteerNeed.getType(), expected_type);

    }


    @Test
    public void testNeedEquals(){
        // Setup
        int id = 15;
        String name = "hot pockets";
        String description = "pizza, but in a pocket";
        int goal = 10000;

        Need resourceNeed = new ResourceNeed(id, name, description, goal);
        Need resourceNeed2 = new ResourceNeed(id + 1, name, description, goal + 1);
        Need monetaryNeedGoalDiff = new MonetaryNeed(id, name, description, goal + 1);
        Need monetaryNeedClone = monetaryNeedGoalDiff.createCopy(id);
        monetaryNeedClone.setGoal(goal-1);

        // Assert
        assertEquals(resourceNeed.equals(resourceNeed),true);
        assertEquals(resourceNeed.equals(resourceNeed2),false);
        assertEquals(resourceNeed.equals(null),false);
    }

    @Test
    public void testNeedEqualsbranch(){
        // Setup
        int id = 15;
        String name = "hot pockets";
        String description = "pizza, but in a pocket";
        int goal = 10000;

        Need resourceNeed = new ResourceNeed(id, name, description, goal);
        Need monetaryNeed = new MonetaryNeed(id + 1, name, description, goal + 1);
           
        Need monetaryNeedSameValues = new MonetaryNeed(id, name, description, goal);
        Need resourceNeedClone = resourceNeed.createCopy(id);
        Need monetaryNeedGoalDiff = new MonetaryNeed(id, name, description, goal + 1);
        Need monetaryNeedClone = monetaryNeedGoalDiff.createCopy(id);
        monetaryNeedClone.setGoal(goal-1);

        assertEquals(resourceNeed.equals("This is not a need"),false);
        assertEquals(monetaryNeed.equals(resourceNeed),false);
        assertEquals(monetaryNeedGoalDiff.equals(monetaryNeedClone),false);
     
        assertEquals(true, resourceNeed.equals(resourceNeedClone)); 
        assertEquals(false, resourceNeed.equals(monetaryNeedSameValues));
    }

    @Test
    public void testNeedEqualsClone(){
        // Setup
        int id = 15;
        String name = "hot pockets";
        String description = "pizza, but in a pocket";
        int goal = 10000;

        Need resourceNeed = new ResourceNeed(id, name, description, goal);
        Need monetaryNeedSameValues = new MonetaryNeed(id, name, description, goal);
        Need resourceNeedClone = resourceNeed.createCopy(id);
    
        assertEquals(true, resourceNeed.equals(resourceNeedClone)); 
        assertEquals(false, resourceNeed.equals(monetaryNeedSameValues));
    }


    @Test
    public void testNeedNameUpdate(){
        // Setup
        int id = 15;
        String name = "hot pockets";
        String description = "pizza, but in a pocket";
        int goal = 10000;
        Need resourceNeed = new ResourceNeed(id, name, description, goal);

        String expected_name = "cold pockets";

        // Invoke
        resourceNeed.setName(expected_name);

        // Assert
        assertEquals(resourceNeed.getName(), expected_name);

    }

    @Test
    public void testGetRemaining(){
        // Setup
        int id = 15;
        String name = "hot pockets";
        String description = "pizza, but in a pocket";
        int goal = 15;
        Need resourceNeed = new ResourceNeed(id, name, description, goal);

        int expected_remain = 15;
        int expected_remainmax = 0;

        assertEquals(resourceNeed.getRemaining(), expected_remain);
        resourceNeed.setProgress(200);
        assertEquals(resourceNeed.getRemaining(), expected_remainmax);
    }


    @Test
    public void testNeedDescUpdate(){
        // Setup
        int id = 15;
        String name = "hot pockets";
        String description = "pizza, but in a pocket";
        int goal = 10000;
        Need resourceNeed = new ResourceNeed(id, name, description, goal);

        String expected_desc = "I HATE HOT POCKETS";

        // Invoke
        resourceNeed.setDescription(expected_desc);

        // Assert
        assertEquals(resourceNeed.getDescription(), expected_desc);

    }


    @Test
    public void testNeedProgressUpdate(){
        // Setup
        int id = 15;
        String name = "hot pockets";
        String description = "pizza, but in a pocket";
        int goal = 10000;
        Need resourceNeed = new ResourceNeed(id, name, description, goal);

        int expected_progress = 24;

        // Invoke
        resourceNeed.setProgress(expected_progress);

        // Assert
        assertEquals(resourceNeed.getProgress(), expected_progress);

    }

    @Test
    public void testNeedProgressUpdateMax(){
        // Setup
        int id = 15;
        String name = "hot pockets";
        String description = "pizza, but in a pocket";
        int goal = 5;
        Need resourceNeed = new ResourceNeed(id, name, description, goal);

        int expected_progress = 5;

        // Invoke
        resourceNeed.setProgress(25);

        // Assert
        assertEquals(resourceNeed.getProgress(), expected_progress);

    }


    @Test
    public void testNeedGoalUpdate(){
        // Setup
        int id = 8;
        String name = "hot pockets";
        String description = "pizza, but in a pocket";
        int goal = 10000;
        Need resourceNeed = new ResourceNeed(id, name, description, goal);

        int expected_goal = 15000;

        // Invoke
        resourceNeed.setGoal(expected_goal);

        // Assert
        assertEquals(resourceNeed.getGoal(), expected_goal);

    }
}
