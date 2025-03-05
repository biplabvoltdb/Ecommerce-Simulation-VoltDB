import org.voltdb.*;

public class PlaceOrderProcedure extends VoltProcedure {

  // Check if stock is available
  public final SQLStmt checkStock = new SQLStmt(
    "SELECT stock, price FROM warehouse_products WHERE product_id = ? AND stock > 0;"
  );

  // Reduce stock
  public final SQLStmt updateStock = new SQLStmt(
    "UPDATE warehouse_products SET stock = stock - 1 WHERE product_id = ?;"
  );

  // Credit the respective product's wallet
  public final SQLStmt updateWallet = new SQLStmt(
    "UPDATE warehouse_wallets SET balance = balance + ?, total_credited = total_credited + ? WHERE product_id = ?;"
  );

  // Insert the order (Now includes timestamp from Java)
  public final SQLStmt insertOrder = new SQLStmt(
    "INSERT INTO warehouse_orders (order_id, product_id, amount_paid, order_time) VALUES (?, ?, ?, ?);"
  );

  public long run(String orderId, String productId, long orderTimeMillis) throws VoltAbortException {

    // Step 1: Check stock and price
    voltQueueSQL(checkStock, productId);
    VoltTable[] stockResults = voltExecuteSQL();
     
    if (stockResults[0].getRowCount() == 0) {
      throw new VoltAbortException("Insufficient stock for product: " + productId);
    }

    stockResults[0].advanceRow();
    int stockAvailable = (int) stockResults[0].getLong(0);
    double productPrice = stockResults[0].getDouble(1);

    if (stockAvailable <= 0) {
      throw new VoltAbortException("Stock is unavailable for product: " + productId);
    }

    // Step 2: Deduct stock
    voltQueueSQL(updateStock, productId);

    // Step 3: Update the wallet for this product
    voltQueueSQL(updateWallet, productPrice, productPrice, productId);

    // Step 4: Insert order with timestamp
    voltQueueSQL(insertOrder, orderId, productId, productPrice, orderTimeMillis);

    // Execute all steps atomically
    voltExecuteSQL(true);

    return 0; // Success
  }
}
