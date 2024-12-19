package SERVER.Service;

import OLD.*;
import SERVER.Models.Account;
import SERVER.Models.Product;
import SERVER.Repository.DatabaseDAO;

import java.util.List;

public class DTService {

    private DatabaseDAO databaseDAO;
    private ProductCache productCache;


    public DTService(){
        this.databaseDAO = new DatabaseDAO();
        this.productCache = ProductCache.getInstance();
    }


    public List<Product> fetchProducts(){
        return databaseDAO.fetchProducts();
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
       return productCache.getProductListResponse();
    }

    public boolean addToCart(Cart cart){
       return databaseDAO.saveProductCart(cart);
    }

    public CartResponse getCartProducts(int userId){
        return databaseDAO.getCartProducts(userId);
    }

}
