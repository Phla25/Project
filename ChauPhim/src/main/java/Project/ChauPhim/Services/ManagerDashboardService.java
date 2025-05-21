package Project.ChauPhim.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import Project.ChauPhim.DAOs.ManagerDAO;

@Service
public class ManagerDashboardService {
    
    @Autowired
    private ManagerDAO managerDAO;
    
    /**
     * Get total number of users
     */
    public Long getUserCount() {
        return managerDAO.countUser();
    }
    
    /**
     * Get total number of discount codes
     */
    public Long getDiscountCount() {
        return managerDAO.countDiscount();
    }
    
    /**
     * Get sales count for today
     */
    public Long getTodaySales() {
        return managerDAO.countSold2Day();
    }
    
    /**
     * Get sales count for a specific month
     */
    public Long getMonthlySales(int month) {
        return managerDAO.countSoldMonth(month);
    }
    
    /**
     * Get sales count for a specific year
     */
    public Long getYearlySales(int year) {
        return managerDAO.countSoldYear(year);
    }
}
