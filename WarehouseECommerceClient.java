import org.voltdb.client.Client2;
import org.voltdb.client.Client2Config;
import org.voltdb.client.ClientFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class WarehouseECommerceClient {

    private static final String SERVERS = "172.31.39.199,172.31.12.10,172.31.28.117";
    private static final int DURATION = 300;
    private static final int RATE_LIMIT = Integer.MAX_VALUE;

    private final Client2 client;
    private final List<String> productIds;

    public WarehouseECommerceClient(int numProducts) throws Exception {
        Client2Config config = new Client2Config();
        config.outstandingTransactionLimit(RATE_LIMIT);
        client = ClientFactory.createClient(config);

        // Generate product IDs
        productIds = new ArrayList<>();
        for (int i = 1; i <= numProducts; i++) {
            productIds.add("Product" + i);
        }
    }

    public void connect(String servers) {
        for (String server : servers.split(",")) {
            try {
                client.connectSync(server);
                System.out.println("Connected to: " + server);
            } catch (Exception e) {
                System.err.println("Failed to connect to: " + server + " - " + e.getMessage());
            }
        }
    }

    public void initializeDatabase() throws Exception {
        System.out.println("Initializing warehouse database...");

        Random random = new Random();

        // Insert products with 5 million stock each
        for (String productId : productIds) {
            String productName = "Product_" + productId;
            double price = random.nextDouble() * 100 + 50;
            long stock = 50_000_000; // 5 million stock per product

            client.callProcedureSync("WAREHOUSE_PRODUCTS.insert", productId, productName, stock, price);
            client.callProcedureSync("WAREHOUSE_WALLETS.insert", productId, 0.0, 0.0);
        }

        System.out.println("Warehouse database initialized.");
    }

    public void simulateOrders(int numUsers) throws Exception {
        int numThreads = Math.min(numUsers, 18); // Limit concurrent threads to prevent memory overload
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);

        for (int i = 0; i < numUsers; i++) {
            final int userIndex = i;
            executor.submit(() -> {
                long endTime = System.currentTimeMillis() + (DURATION * 1000);

                while (System.currentTimeMillis() < endTime) {
                    String orderId = UUID.randomUUID().toString();
                    String productId = productIds.get(userIndex % productIds.size()); // Distribute product selection

                    long orderTimeMillis = System.currentTimeMillis(); // Pass timestamp

                    try {
                        client.callProcedureSync("PlaceOrderProcedure", orderId, productId, orderTimeMillis);
                    } catch (Exception e) {
                        System.err.println("Order failed: " + e.getMessage());
                    }
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(DURATION + 5, TimeUnit.SECONDS);
        client.close();
    }

    public static void main(String[] args) throws Exception {
        int numUsers = 1000; // Set the number of concurrent users
        int numProducts = 1; // Set the number of available products

        WarehouseECommerceClient client = new WarehouseECommerceClient(numProducts);
        client.connect(SERVERS);
        client.initializeDatabase();

        // Simulate high concurrency workload with 1:1 user-to-product ratio
        client.simulateOrders(numUsers);
    }
}
