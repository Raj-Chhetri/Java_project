import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Random;

public class User {
    private String accountNumber;
    private String username;

    public User(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public boolean validateCredentials(String password) {
        try (Connection conn = JDBCUtil.getConnection()) {
            String query = "SELECT * FROM users WHERE account_number = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, accountNumber);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                this.username = rs.getString("username");
                return true;
            }
            return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean createAccount(String username, String password) {
        String accountNumber = generateAccountNumber();
        try (Connection conn = JDBCUtil.getConnection()) {
            String checkUserQuery = "SELECT * FROM users WHERE account_number = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkUserQuery);
            checkStmt.setString(1, accountNumber);
            ResultSet rs = checkStmt.executeQuery();
            if (rs.next()) {
                // Account number already exists (very unlikely with random generation)
                return false;
            }

            String insertQuery = "INSERT INTO users (account_number, username, password, balance) VALUES (?, ?, ?, 0)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, accountNumber);
            insertStmt.setString(2, username);
            insertStmt.setString(3, password);
            insertStmt.executeUpdate();
            this.accountNumber = accountNumber;
            this.username = username;
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getBalance() {
        try (Connection conn = JDBCUtil.getConnection()) {
            String query = "SELECT balance FROM users WHERE account_number = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble("balance");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean updateBalance(double amount) {
        try (Connection conn = JDBCUtil.getConnection()) {
            return updateBalance(amount, conn);
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateBalance(double amount, Connection conn) throws SQLException {
        String query = "UPDATE users SET balance = balance + ? WHERE account_number = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setDouble(1, amount);
        stmt.setString(2, accountNumber);
        return stmt.executeUpdate() > 0;
    }

    public static String generateAccountNumber() {
        Random rand = new Random();
        StringBuilder accountNumber = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            accountNumber.append(rand.nextInt(10));
        }
        return accountNumber.toString();
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getUsername() {
        return username;
    }
}
