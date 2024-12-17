package SERVER.Repository;

import SERVER.DatabaseConnection.DatabaseConnection;
import SERVER.Models.*;
import SERVER.Models.ProductModels.Product;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.springframework.security.core.userdetails.User;

import java.lang.reflect.Type;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class DatabaseDAO {

    public static Gson gson = new Gson();
   public static Scanner scanner = new Scanner(System.in);


                                                  /** ADMIN QUERIES **/
    public void addProduct(String name, int brandID, String description, double price, int categoryID, boolean isNew, boolean isPopular, String gender) {

        String QUERY = "INSERT INTO Product (name, brand_id, description, price, category_id, is_new_in, is_popular, gender) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = DatabaseConnection.getConnection();
            pstmt = con.prepareStatement(QUERY, PreparedStatement.RETURN_GENERATED_KEYS);


            pstmt.setString(1, name);
            pstmt.setInt(2, brandID);
            pstmt.setString(3, description);
            pstmt.setDouble(4, price);
            pstmt.setInt(5, categoryID);
            pstmt.setBoolean(6, isNew);
            pstmt.setBoolean(7, isPopular);
            pstmt.setString(8, gender);
            int count = pstmt.executeUpdate();

            if (count == 1) {
                rs = pstmt.getGeneratedKeys();
                if (rs.next()) {
                    int productId = rs.getInt(1);
                    System.out.println("Product added with ID: " + productId);

                    insertImages(productId);
                }
            }


        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(con, pstmt, rs);

        }


    }

    public void insertImages(int productID) throws SQLException {


        int num = 4;
        String IMAGE_QUERY = "INSERT INTO Images (product_id, image_url) VALUES (?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        con = DatabaseConnection.getConnection();
        pstmt = con.prepareStatement(IMAGE_QUERY);

        for (int i = 0; i < num; i++) {
            System.out.println("Enter the image url for product ID " + productID + ":");
            String imageURL = scanner.nextLine();

            pstmt.setInt(1, productID);
            pstmt.setString(2, imageURL);
            pstmt.executeUpdate();

        }


        insertSizesAndStocks(productID);


        System.out.println("Images added successfully!");

    }

    public void insertSizesAndStocks(int productID) throws SQLException {

        System.out.println("Enter the number of Sizes");
        int sizedCount = scanner.nextInt();
        scanner.nextLine(); // Consume the newline character

        String IMAGE_QUERY = "INSERT INTO ProductSizes (product_id, size, stock) VALUES (?, ?, ?)";
        Connection con = null;
        PreparedStatement pstmt = null;

        con = DatabaseConnection.getConnection();
        pstmt = con.prepareStatement(IMAGE_QUERY);

        for (int i = 0; i < sizedCount; i++) {
            System.out.println("Enter the Size Type " + productID + ":");
            String sizeType = scanner.nextLine();

            System.out.println("Enter number of stocks for  " + sizeType + ":");
            int stockCount = scanner.nextInt(); scanner.nextLine();
            pstmt.setInt(1, productID);
            pstmt.setString(2, sizeType);
            pstmt.setInt(3, stockCount);
            pstmt.executeUpdate();

        }

        System.out.println("Sizes and Stocks added successfully!");


    }



                                             /** CLIENT QUERY REQUESTS **/
    public List<Product> fetchProducts(){

        List<Product> products = new ArrayList<>();

        String QUERY =  "WITH ImageAggregation AS (\n" +
                "    SELECT \n" +
                "        i.product_id, \n" +
                "        ARRAY_AGG(DISTINCT i.image_url) AS image_urls\n" +
                "    FROM Images i\n" +
                "    GROUP BY i.product_id\n" +
                "), \n" +
                        "SizeAggregation AS (\n" +
                "    SELECT \n" +
                "        ps.product_id, \n" +
                "        JSON_AGG(JSON_BUILD_OBJECT('size', ps.size, 'stock', ps.stock)) AS sizes_with_stock\n" +
                "    FROM (\n" +
                "        SELECT DISTINCT product_id, size, stock\n" +
                "        FROM ProductSizes\n" +
                "    ) ps\n" +
                "    GROUP BY ps.product_id\n" +
                ")\n" +
                        "\n" +
                        "SELECT \n" +
                "    p.product_id, \n" +
                "    p.name AS product_name, \n" +
                "    b.brand_name, \n" +
                "    p.description, \n" +
                "    p.price AS base_price, \n" +
                "    c.category_name, \n" +
                "    p.gender, \n" +
                "    p.is_new_in, \n" +
                "    p.is_popular, \n" +
                "    COALESCE(ia.image_urls, '{}') AS image_urls, \n" +
                "    COALESCE(sa.sizes_with_stock, '[]') AS sizes_with_stock\n" +
                "FROM \n" +
                "    Product p\n" +
                "JOIN Brand b ON p.brand_id = b.brand_id\n" +
                "JOIN Category c ON p.category_id = c.category_id\n" +
                "LEFT JOIN ImageAggregation ia ON p.product_id = ia.product_id\n" +
                "LEFT JOIN SizeAggregation sa ON p.product_id = sa.product_id;";

        Product product = null;
        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try{
            con = DatabaseConnection.getConnection();
            pstmt = con.prepareStatement(QUERY);
            rs = pstmt.executeQuery();


            while(rs.next()){



                product = new Product();

                product.setProductID(rs.getInt("product_id"));
                product.setProductName(rs.getString("product_name"));
                product.setBrandName(rs.getString("brand_name"));
                product.setProductDescription(rs.getString("description"));
                product.setBasePrice(rs.getDouble("base_price"));
                product.setCategoryName(rs.getString("category_name"));
                product.setGender(rs.getString("gender"));
                product.setNew(rs.getBoolean("is_new_in"));
                product.setPopular(rs.getBoolean("is_popular"));

                // Convert to Java array to lists
                Array arrayImages = rs.getArray("image_urls");
                String[] stringArrayImages = (String []) arrayImages.getArray();
                List<String> imageUrls = List.of(stringArrayImages);
                product.setImageUrls(imageUrls);


                // Convert Size and Stocks to java list
                Type sizeStockListType = new TypeToken<List<Product.SizeStock>>() {}.getType();
                List<Product.SizeStock> sizeWithStock = gson.fromJson(rs.getString("sizes_with_stock"), sizeStockListType);
                product.setSizeStocks(sizeWithStock);

                products.add(product);
            }




        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            closeResources(con, pstmt, rs);
        }

        return products;

    }

                                            /** CLIENT TOKEN REQUESTS **/


     public int fetchUser(int userID){

         String query = "SELECT * FROM clientaccount WHERE user_id = ?";
         Connection con = null;
         PreparedStatement pstmt = null;
         ResultSet rs = null;

         try{
             con = DatabaseConnection.getConnection();
             pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

             pstmt.setInt(1, userID);
             rs = pstmt.executeQuery();

             while(rs.next()){
              return rs.getInt("user_id");
             }

         }catch (SQLException ex) {
             ex.printStackTrace();
         }
         return 0;
     }




     public int insertClient(String username, String email, String hashedPassword){

         String query = "INSERT INTO clientaccount (username, email, password_hash) VALUES (?, ?, ?)";

         Connection con = null;
         PreparedStatement pstmt = null;
         ResultSet rs = null;

         try{
             con = DatabaseConnection.getConnection();
             pstmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

             pstmt.setString(1, username);
             pstmt.setString(2, email);
             pstmt.setString(3, hashedPassword);

             pstmt.executeUpdate();

             rs = pstmt.getGeneratedKeys();
             if(rs.next()){
                 return rs.getInt(1);
             }

         } catch (Exception e) {
             throw new RuntimeException(e);
         }

         return 0;
     }


     public void insertToken(int useriD, String token, Date expiryDate){

         Connection con = null;
         PreparedStatement pstmt = null;

         try{
             con = DatabaseConnection.getConnection();
             pstmt = con.prepareStatement("INSERT INTO token(user_id, token_value, expires_at) VALUES (?, ?, ?)");

             pstmt.setInt(1, useriD);
             pstmt.setString(2, token);
             pstmt.setDate(3, new java.sql.Date(expiryDate.getTime()));

             int count = pstmt.executeUpdate();

             if(count > 0){
                 System.out.println("Inserted " + count + " token");
             }

         }catch (SQLException ex){
             ex.printStackTrace();
         }

     }



     public UserDetails getUserDetails(String token){

         UserDetails userDetails = null;

         String query = "SELECT t.token_value, c.user_id, c.username, c.email, c.password_hash " +
                 "FROM Token t JOIN clientaccount c ON t.user_id = c.user_id " +
                 "WHERE t.token_value = ?";

         Connection con = null;
         PreparedStatement pstmt = null;

         try{
             con = DatabaseConnection.getConnection();
             pstmt = con.prepareStatement(query);

             pstmt.setString(1, token);
             ResultSet rs = pstmt.executeQuery();

             while(rs.next()){
                 String tokenValue = rs.getString("token_value");
                 int userId = rs.getInt("user_id");
                 String username = rs.getString("username");
                 String email = rs.getString("email");
                 String passwordHash = rs.getString("password_hash");
                 userDetails = new UserDetails(userId, username, email, passwordHash);
             }
         }catch (SQLException ex){
             ex.printStackTrace();
         }
         return userDetails;
     }




    public synchronized AuthResponse addUser(Account account) {

        String username = account.getUsername();
        String email = account.getEmail();
        String password = account.getPassword();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnection.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM useraccount WHERE username = ? OR email = ?");

            pstmt.setString(1, username);
            pstmt.setString(2, email);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                // User already exists
                //System.out.println("User already exists");
                return new AuthResponse(true, "User already exists.");

            } else {
                pstmt = con.prepareStatement("INSERT INTO useraccount (username, email, password) VALUES (?,?,?)");
                pstmt.setString(1, username);
                pstmt.setString(2, email);
                pstmt.setString(3, password);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    //System.out.println("User added successfully");
                    return new AuthResponse(false, "User added successfully.");
                } else {
                    return new AuthResponse(false, "Error adding user.");
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
            return new AuthResponse(false, "Database error: " + e.getMessage());
        } finally {
            closeResources(con, pstmt, rs);
        }
    }

    public AuthResponse loginUser(Account account) {

        AuthResponse authResponse = null;

        String email = account.getEmail();
        String password = account.getPassword();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnection.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM useraccount WHERE email = ? AND password = ?");

            pstmt.setString(1, email);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();
            if (rs.next()) {
                // Get the username and id  from the ResultSet
                int userID = rs.getInt("id");
                String username = rs.getString("username");
                authResponse = new AuthResponse(true, "Hello and Welcome, " + username, userID, username);

            } else {
                authResponse = new AuthResponse(false, "No account associated with this email. Sign up to create an account. ");
                System.out.println("Error here!");

            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(con, pstmt, rs);
        }

        return authResponse;
    }

    public ProductListsResponse getProductLists() {

        ProductListsResponse productListsResponse;
        List<Products> productsList = new ArrayList<>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            con = DatabaseConnection.getConnection();
            pstmt = con.prepareStatement("SELECT * FROM products");

            rs = pstmt.executeQuery();
            while (rs.next()) {

                String name = rs.getString("name");
                String description = rs.getString("description");
                double price = rs.getDouble("price");
                String image_url = rs.getString("image_url");

                Products products = new Products(name, description, String.valueOf(price), image_url);
                productsList.add(products);

            }

            productListsResponse = new ProductListsResponse("200 OK", productsList);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            closeResources(con, pstmt, rs);
        }

        return productListsResponse;
    }

    public ProductListsResponse getProductCategory() {

        ProductListsResponse productListsResponse = null;
        ArrayList<CategoryProduct> categoryProducts = new ArrayList<>();

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {

            con = DatabaseConnection.getConnection();
            pstmt = con.prepareStatement("SELECT p.*, c.category_name AS category_name FROM products p JOIN category c ON p.category_id = c.category_id");

            rs = pstmt.executeQuery();

            while (rs.next()) {
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
            if (rs != null) {
                productListsResponse = new ProductListsResponse(categoryProducts);
            } else {
                productListsResponse = new ProductListsResponse(categoryProducts);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeResources(con, pstmt, rs);
        }

        return productListsResponse;
    }

    public boolean saveProductCart(Cart cart) {

        Connection con = null;
        PreparedStatement pstmt = null;

        try {

            con = DatabaseConnection.getConnection();

            pstmt = con.prepareStatement("SELECT quantity FROM cart WHERE user_id = ? AND product_id = ? AND size = ?");
            pstmt.setInt(1, cart.getUserID());
            pstmt.setInt(2, cart.getProductID());
            pstmt.setString(3, cart.getSize());
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {

                int currentQty = rs.getInt("quantity");
                int updatedQty = currentQty + cart.getQty();

                pstmt = con.prepareStatement("UPDATE cart SET quantity = ? WHERE user_id = ? AND product_id = ? AND size = ?");
                pstmt.setInt(1, updatedQty);
                pstmt.setInt(2, cart.getUserID());
                pstmt.setInt(3, cart.getProductID());
                pstmt.setString(4, cart.getSize());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Updated quantity in cart successfully.");
                    return true;
                } else {
                    System.out.println("Failed to update quantity in cart.");
                    return false;
                }

            } else {

                pstmt = con.prepareStatement("INSERT INTO cart (user_id, product_id, size, quantity) VALUES (?,?,?,?)");
                pstmt.setInt(1, cart.getUserID());
                pstmt.setInt(2, cart.getProductID());
                pstmt.setString(3, cart.getSize());
                pstmt.setInt(4, cart.getQty());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Saved to cart successfully.");
                    return true;
                } else {
                    System.out.println("Failed to save to cart.");
                    return false;
                }

            }


        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Unable to save to cart.");
        } finally {
            closeResources(con, pstmt, null);
        }
        return false;
    }

    public CartResponse getCartProducts(int userId){

       ArrayList<CartResponseProduct> cartProducts = new ArrayList<>();
       CartResponse cartResponse = null;

        Connection con = null;
        PreparedStatement pstmt = null;
        String query = "SELECT " +
                       "useraccount.id, " +
                       "useraccount.username, " +
                       "products.product_id, " +
                       "products.brand_name, " +
                       "products.description, " +
                       "products.price, " +
                       "products.editornote, " +
                       "products.image_url, " +
                       "category.category_name, " +
                       "cart.size, " +
                       "cart.quantity " +
                       "FROM useraccount " +
                       "INNER JOIN cart ON useraccount.id = cart.user_id " +
                       "INNER JOIN products ON cart.product_id = products.product_id " +
                       "INNER JOIN category ON products.category_id = category.category_id " +
                       "WHERE useraccount.id = ?";

        try{
           con = DatabaseConnection.getConnection();
           pstmt = con.prepareStatement(query);

           pstmt.setInt(1, userId);

           ResultSet rs = pstmt.executeQuery();

           if(rs == null){
               System.out.println("No cart products found.");
           }

           while (rs.next()) {
               int userID = rs.getInt("id");
               String username = rs.getString("username");
               int productID = rs.getInt("product_id");
               String brandName = rs.getString("brand_name");
               String description = rs.getString("description");
               double price = rs.getDouble("price");
               String editornote = rs.getString("editornote");
               String image_url = rs.getString("image_url");
               String category_name = rs.getString("category_name");
               String size = rs.getString("size");
               int quantity = rs.getInt("quantity");
               CartResponseProduct cartResponseProduct = new CartResponseProduct(userID, username, productID, brandName, description, price, editornote, image_url, category_name, size, quantity);
               System.out.println(cartResponseProduct.toString());
               cartProducts.add(cartResponseProduct);
           }
           cartResponse = new CartResponse(cartProducts);


        } catch (Exception e) {
            e.printStackTrace();
        }
        return cartResponse;
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
