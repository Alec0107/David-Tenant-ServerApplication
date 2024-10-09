package SERVER.Service;

import SERVER.Models.Account;
import SERVER.Models.AuthResponse;
import SERVER.Models.ProductListsResponse;
import SERVER.Repository.DatabaseDAO;

public class DTService {

    private DatabaseDAO databaseDAO;


    public DTService(){
        this.databaseDAO = new DatabaseDAO();
    }

    public AuthResponse addUser(Account account){
        return databaseDAO.addUser(account);
    }

    public AuthResponse loginUser(Account account){
        return databaseDAO.loginUser(account);
    }

    public ProductListsResponse getProducts(){
        return databaseDAO.getProductLists();
    }

    public ProductListsResponse getProductCategory(){
        return databaseDAO.getProductCategory();
    }

}
