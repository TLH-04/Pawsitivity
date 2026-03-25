package com.ufund.api.ufundapi.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.stereotype.Service;

import com.ufund.api.ufundapi.model.Basket;
import com.ufund.api.ufundapi.model.need.Need;
import com.ufund.api.ufundapi.model.user.Helper;
import com.ufund.api.ufundapi.persistence.CupboardDAO;


/**
 * Handles logic for user based, basket operations
 * 
 * @author Trinity Hampton
 */

@Service
public class BasketService {
    private final AuthService authService;
    private final CupboardDAO cupboardDAO;
    private static final Logger LOG = Logger.getLogger(Need.class.getName());

    /**
     * Creates a Service to respond to requests
     * 
     * @param authService - The {@link AuthService} to perform CRUD operations
     * @param cupboardDAO - The {@link CupboardDAO} to perform CRUD operations
     */
    public BasketService(AuthService authService, CupboardDAO cupboardDAO){
        this.authService = authService;
        this.cupboardDAO = cupboardDAO;
    }


    /**
     * Attempts to get basket of user
     * 
     * Also updates the user basket {@link #updateBasket(sessionId)}
     * 
     * @param sessionId - session id of helper
     * 
     * @return basket of user
     * Otherwise Null if basket cannot be found
     */
    public Basket getUserBasket(String sessionId) {
        this.updateBasket(sessionId);
        Helper user = (Helper) this.authService.getLoggedUser(sessionId);
        this.authService.updateUser(sessionId);
        return user.getBasket();
    }

    /**
     * Updates the given basket based on need avability and removes needs no longer found in the cupboard
     * @param sessionId current sessionId
     * @return Updated basket
     */
    private void updateBasket(String sessionId){
        try {
            Helper user = (Helper) this.authService.getLoggedUser(sessionId);
            Need[] needs = this.cupboardDAO.getNeeds();
            List<Integer> needIds = new ArrayList<>();
            Map<Integer,Integer> content = user.getBasket().getBasketContents();
           
            for (Need n : needs) {
                needIds.add(n.getId());
                if (content.containsKey(n.getId())) {
                    if (!n.isListed()) {
                        user.getBasket().removeNeed(n.getId(), n.getGoal());
                    } else if (n.getRemaining() < content.get(n.getId())){
                        user.getBasket().setNeed(n.getId(), n.getRemaining());

                        if (user.getBasket().getNeedAmount(n.getId()) == 0){
                            user.getBasket().removeNeed(n.getId(), n.getGoal());
                        }
                    }
                   
                }
            }

            List<Integer> currentNeeds = new ArrayList<>(content.keySet());
            currentNeeds.removeAll(needIds); // Removes all verifed needs to check for needs that are remaining

            for (int id : currentNeeds){
                user.getBasket().removeNeed(id,user.getBasket().getNeedAmount(id));
            }


            this.authService.updateUser(sessionId);
        } catch (Exception ex) {
            
        }
    }

    /**
     * Attempts to get basket of user
     * 
     * @param sessionId - the session id of the one preforming the operation
     * @param id - id of the Need {@link Need}
     * @param amount - the amount you are adding to your basket
     * 
     * @return True if needs was added
     * False if need was not added due to conflict
     * Otherwise throws Exception error if need cannot be found
     * 
     * @throws Exception on cupboardDAO error
     */
    public Boolean addNeed(String sessionId,int id, int amount) throws Exception{

        Need need = this.cupboardDAO.getNeed(id);
        if (need == null || !need.isListed()){
            String Msg = "Need Not Found: " + id;
            LOG.log(Level.WARNING,Msg);
            return false;
        }

        int finalamount = (need.getRemaining()) < amount ? (need.getRemaining()) : amount;

        if (need.getRemaining() < amount){
            String Msg = "Enter more than remaining amount, Changing Quanity to" + finalamount;
            LOG.log(Level.WARNING,Msg);
        }

        Basket basket = this.getUserBasket(sessionId);

        Boolean status = basket.addNeed(id,finalamount);
        LOG.log(Level.WARNING,status.toString());
        this.authService.updateUser(sessionId);
       
    
        return status;
    }

    /**
     * Attempts to get basket of user
     * 
     * @param sessionId - the session id of the one preforming the operation
     * @param id - id of the Need
     * 
     * @return True if need was added
     * False if need was not added due to conflict
     * Otherwise throws Exception error if need cannot be found
     * @throws Exception on IOexception
     */
    public Boolean addNeed(String sessionId,int id) throws Exception{
        return this.addNeed(sessionId,id,1);
    }


    /**
     * Attempts to get basket of user
     * 
     * @param sessionId - the session id of the one preforming the operation
     * @param id - id of the Need
     * @param amount - the amount you are adding to your basket
     * 
     * @return Returns removed need if successful
     * Otherwise null if need cannot be removed
     * @throws Exception on IOexception
     */
    public Need removeNeed(String sessionId,int id,int amount) throws Exception{
        Need need = this.cupboardDAO.getNeed(id);

        if (need != null){
            this.updateBasket(sessionId);
            Helper user = (Helper) this.authService.getLoggedUser(sessionId);
            user.getBasket().removeNeed(need.getId(),amount);
            this.authService.updateUser(sessionId);
            return need;
        }
        return null;
    }

    
    /**
     * Attempts to checkout and update all needs on the cupboard
     * 
     * @param sessionId the session id of the person checking out
     * @return status of checkout
     * @throws Exception on IOexception
     */
    public Boolean userCheckout(String sessionId) throws Exception{
        Helper user = (Helper) this.authService.getLoggedUser(sessionId);
        Basket basket = user.getBasket();
        if (basket.getBasketContents().isEmpty()){
            throw new Exception();
        }
        Map<Integer,Integer> needList = basket.checkout();
        user.updateContributedNeeds(needList.keySet().size());
        this.cupboardDAO.updateNeeds(needList);
        this.authService.updateUser(sessionId);

        return true;
    }
}
