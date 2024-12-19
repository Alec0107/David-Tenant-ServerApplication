package OLD;

import SERVER.Repository.DatabaseDAO;

public class ProductCache {

    // Private constructor to prevent instantiation
    private ProductCache() {
        databaseDAO = new DatabaseDAO(); // Initialize the DatabaseDAO for data access
    }

    private long lastUpdated; // Timestamp of the last cache update
    private static ProductCache instance; // Singleton instance of ProductCache
    private ProductListsResponse productListsResponse; // Cached product list response
    private DatabaseDAO databaseDAO; // Database access object for fetching data
    private long TTL = 60000; // Time to live for cache in milliseconds (1 minute)

    // Static method to get the singleton instance of ProductCache
    public static ProductCache getInstance() {
        if (instance == null) { // Check if instance is not yet created
            instance = new ProductCache(); // Create a new instance
        }
        return instance; // Return the singleton instance
    }

    // Method to get the product list response
    public ProductListsResponse getProductListResponse() {
        // Check if the cache has expired and refresh if needed
        if (isCacheExpired()) {
            refreshCache();
        }

        return productListsResponse; // Return the cached product list response
    }

    // Method to check if the cache has expired
    public boolean isCacheExpired() {
        long currentTime = System.currentTimeMillis(); // Get the current time
        // Determine if the cache is expired based on the last updated time and TTL
        boolean isExpired = (currentTime - lastUpdated) > TTL;
        return isExpired; // Return true if the cache is expired, false otherwise
    }

    // Method to refresh the cache from the database
    public void refreshCache() {
        System.out.println("Cache Expired. Refreshing from the database..."); // Log message for cache refresh

        // Initialize productListsResponse if it's null
        if (productListsResponse == null) {
            productListsResponse = new ProductListsResponse(); // Create a new instance if not initialized
        }

        // Clear existing products from the cached response
        productListsResponse.getCategoryProducts().clear();

        // Fetch the latest product category from the database
        ProductListsResponse cachProducts = databaseDAO.getProductCategory();


        // Update the cached product list with new data
        productListsResponse.setCategoryProducts(cachProducts.getCategoryProducts());

        // Update the last updated timestamp to the current time
        lastUpdated = System.currentTimeMillis();
    }
}