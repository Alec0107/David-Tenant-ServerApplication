package SERVER.Service;

import SERVER.Models.Account;
import SERVER.Models.AuthResponse;
import SERVER.Models.CategoryProduct;
import SERVER.Models.ProductListsResponse;
import SERVER.Repository.DatabaseDAO;

import java.util.List;

public class AuthService {

    private DatabaseDAO databaseDAO;

    public  AuthService(){
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
