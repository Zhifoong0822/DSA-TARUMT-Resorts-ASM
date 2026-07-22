// Author: [Your Name]
package control;

import adt.MapInterface;
import Dao.GuestDAO;
import entity.GuestProfile;

public class FrontDeskController {

    private MapInterface<String, GuestProfile> guestMap;
    private GuestDAO guestDAO;

    public FrontDeskController() {
        // Instantiate DAO
        this.guestDAO = new GuestDAO();
        
        // Use DAO to retrieve and populate the guestMap ADT
        this.guestMap = guestDAO.loadGuests();
    }

    public GuestProfile findGuestByConfirmation(String confirmationNum) {
        return guestMap.get(confirmationNum);
    }
}