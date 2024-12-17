package SERVER.ADMIN;

import SERVER.Models.ProductModels.Product;
import SERVER.Models.Products;
import SERVER.Repository.DatabaseDAO;

import java.util.List;
import java.util.Scanner;

public class Queries {

    public static DatabaseDAO dao = new DatabaseDAO();
    private static Scanner scanner = new Scanner(System.in);

        public static void main(String[] args) {

          while(true){

              System.out.println("Welcome to Admin Dashboard!");
              System.out.println("1. View All Products");
              System.out.println("2. Add Product");
              System.out.print("Please, Enter your choice: ");
              int choice = scanner.nextInt(); scanner.nextLine();


              switch (choice){

                  case 1:
                          viewAllProducts();
                      break;
                  case 2:
                          addProduct();

                  case 3:
                          removeProduct();

                  default:
                      break;

              }

          }

        }

        private static void addProduct(){

            System.out.println("Enter number of products to add: ");
            int numberOfProducts = scanner.nextInt();
            scanner.nextLine(); // Consume the newline character

            for (int i = 0; i < numberOfProducts; i++) {
                System.out.print("Enter product name: ");
                String name = scanner.nextLine();

                System.out.println("1. STÒFFA");
                System.out.println("2. SAMAN AMEL");
                System.out.println("3. LOEWE");
                System.out.println("4. CELINE HOMME");
                System.out.println("5. CHERRY LOS ANGELES");
                System.out.println("6. AGOLDE");
                System.out.println("7. BRUNELLO CUCINELLI");
                System.out.println("8. LORO PIANA");
                System.out.print("Enter Brand ID (1-6): ");
                int brandID = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Enter Description: ");
                String description = scanner.nextLine();

                System.out.print("Enter Price: ");
                double price = scanner.nextDouble();

                System.out.println("1. Jeans \n2. Shorts \n3. T-Shirts \n4. Jackets");
                System.out.print("Enter Category ID (1-4): ");
                int categoryID = scanner.nextInt();
                scanner.nextLine();

                System.out.print("Is the product NEW? (true/false): ");
                boolean isNew = scanner.nextBoolean();

                System.out.print("Is the product POPULAR? (true/false): ");
                boolean isPopular = scanner.nextBoolean();
                scanner.nextLine();

                /*
                System.out.println("Select Gender: ");
                System.out.println("1. Men");
                System.out.println("2. Women");
                System.out.print("Enter Gender (1 or 2): ");
                int genderOption = scanner.nextInt();
                scanner.nextLine();

                //String gender = (genderOption == 1) ? "Men" : "Women";
*/
                dao.addProduct(name, brandID, description, price, categoryID, isNew, isPopular, "Women");

            }


        }

        private static void viewAllProducts() {

            List<Product> products = dao.fetchProducts();

            if (!products.isEmpty()) {

                for (Product product : products) {
                    System.out.println(product);
                }

            } else {
                System.out.println("No products found.");
            }
        }


        public static void removeProduct(){

        }


    }

