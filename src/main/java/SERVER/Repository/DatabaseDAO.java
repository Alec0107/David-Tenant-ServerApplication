package SERVER.Repository;

import SERVER.DatabaseConnection.DatabaseConnection;
import SERVER.Models.*;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseDAO {


    public AuthResponse addUser(Account account) {

        String username = account.getUsername();
        String email    = account.getEmail();
        String password = account.getPassword();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = DatabaseConnection.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM useraccount WHERE username = ? OR email = ?");

            pstmt.setString(1, username);
            pstmt.setString(2,email);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                // User already exists
                //System.out.println("User already exists");
                return new AuthResponse( true, "User already exists.");

            }else{
                pstmt = con.prepareStatement("INSERT INTO useraccount (username, email, password) VALUES (?,?,?)");
                pstmt.setString(1, username);
                pstmt.setString(2,email);
                pstmt.setString(3,password);

                int rowsAffected =  pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    //System.out.println("User added successfully");
                    return new AuthResponse( false,"User added successfully.");
                } else {
                    return new AuthResponse( false, "Error adding user.");
                }

            }

        }catch(Exception e) {
            e.printStackTrace();
            return new AuthResponse(false,"Database error: " + e.getMessage());
        }finally {
            closeResources(con, pstmt, rs);
        }
    }


    public AuthResponse loginUser(Account account)  {

        AuthResponse authResponse = null;

        String email    = account.getEmail();
        String password = account.getPassword();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = DatabaseConnection.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM useraccount WHERE email = ? AND password = ?");

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();
            if (rs.next()) {

                String username = rs.getString("username"); // Get the username from the ResultSet
                authResponse = new AuthResponse( true, "Hello and Welcome, " + username);

            }else{
                authResponse = new AuthResponse( false, "No account associated with this email. Sign up to create an account. ");
            }

        }catch(Exception e){
            e.printStackTrace();
        }finally {
            closeResources(con, pstmt, rs);
        }

        return authResponse;
    }

    public ProductListsResponse getProductLists(){

        ProductListsResponse productListsResponse;
        List<Products> productsList = new ArrayList<>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

            try {
                con = DatabaseConnection.getConnection();
                pstmt = con.prepareStatement("SELECT * FROM products");

                rs = pstmt.executeQuery();
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
            }finally {
                closeResources(con, pstmt, rs);
            }

      return productListsResponse;
    }

    public ProductListsResponse getProductCategory(){

        ProductListsResponse productListsResponse = null;
        ArrayList<CategoryProduct> categoryProducts = new ArrayList<>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{

            con = DatabaseConnection.getConnection();
            pstmt = con.prepareStatement("SELECT p.*, c.category_name AS category_name FROM products p JOIN category c ON p.category_id = c.category_id");

            rs = pstmt.executeQuery();

            while(rs.next()){
                int id = rs.getInt("product_id");
                String brand_name = rs.getString("brand_name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                int stock = rs.getInt("stocks");
                String editor_note = rs.getString("editornote");
                String image_url = rs.getString("image_url");
                String category_name = rs.getString("category_name");

                CategoryProduct categoryProduct = new CategoryProduct(id, brand_name, description,
                                                                      price, stock, editor_note,
                                                                      image_url, category_name);

                categoryProducts.add(categoryProduct);

            }
            if(rs!=null) {
                productListsResponse = new ProductListsResponse( categoryProducts);
            }else{
                productListsResponse = new ProductListsResponse(  categoryProducts);
            }


        }catch(Exception e){e.printStackTrace();
        }finally {
            closeResources(con, pstmt, rs);
        }

        return productListsResponse;
    }



    // Method to close PreparedStatement, ResultSet, Connection
    private void closeResources(Connection con, PreparedStatement pstmt, ResultSet rs) {
        try {
            if (rs != null) rs.close();
            if (pstmt != null) pstmt.close();
            if (con != null && !con.isClosed()) con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }





}
