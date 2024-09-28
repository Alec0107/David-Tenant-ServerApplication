package SERVER.Repository;

import SERVER.DatabaseConnection.DatabaseConnection;
import SERVER.Models.Account;
import SERVER.Models.AuthResponse;
import SERVER.Models.ProductListsResponse;
import SERVER.Models.Products;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDAO {


    public AuthResponse addUser(Account account) {

        String username = account.getUsername();
        String email    = account.getEmail();
        String password = account.getPassword();
        PreparedStatement pstmt;

        try{
            Connection con = DatabaseConnection.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM useraccount WHERE username = ? OR email = ?");

            pstmt.setString(1, username);
            pstmt.setString(2,email);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                // User already exists
                return new AuthResponse( true, "User already exists.");

            }else{
                pstmt = con.prepareStatement("INSERT INTO useraccount (username, email, password) VALUES (?,?,?)");
                pstmt.setString(1, username);
                pstmt.setString(2,email);
                pstmt.setString(3,password);

                int rowsAffected =  pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    return new AuthResponse( false,"User added successfully.");
                } else {
                    return new AuthResponse( false, "Error adding user.");
                }

            }

        }catch(Exception e) {
            e.printStackTrace();
            return new AuthResponse(false,"Database error: " + e.getMessage());
        }
    }


    public AuthResponse loginUser(Account account) {

        String email    = account.getEmail();
        String password = account.getPassword();

        try{
            Connection con = DatabaseConnection.getConnection();
            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM useraccount WHERE email = ? AND password = ?");

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {

                String username = rs.getString("username"); // Get the username from the ResultSet

                return new AuthResponse(true, "Hello and Welcome, " + username);
            }else{
                return new AuthResponse(false, "No account associated with this email. Sign up to create an account. ");
            }

        }catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }

    public ProductListsResponse getProductLists(){

        ProductListsResponse productListsResponse;
        List<Products> productsList = new ArrayList<>();

            Connection con = DatabaseConnection.getConnection();
            try {
                PreparedStatement pstmt = con.prepareStatement("SELECT * FROM products");

                ResultSet rs = pstmt.executeQuery();
                while(rs.next()){

                    String name        = rs.getString("name");
                    String description = rs.getString("description");
                    double price       = rs.getDouble("price");
                    String image_url   = rs.getString("image_url");

                    Products products = new Products(name,description, String.valueOf(price), image_url);
                    productsList.add(products);

                }

                productListsResponse = new ProductListsResponse("200 OK", productsList);

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

      return productListsResponse;
    }
}
