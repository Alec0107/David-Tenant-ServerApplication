package SERVER.Service;

import SERVER.Models.Account;
import SERVER.Models.AuthResponse;
import SERVER.Repository.DatabaseDAO;

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

}
