import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.SQLException;

public class BankingApp extends JFrame {
    private JTextField accountNumberField;
    private JPasswordField passwordField;
    private JLabel balanceLabel;
    private JLabel usernameLabel;  // Added to display username
    private User currentUser;

    public BankingApp() {
        setTitle("Banking Application");
        setSize(400, 350);  // Increased the size to accommodate the username label
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(7, 2));  // Changed to 7 rows to accommodate the username label

        panel.add(new JLabel("Account Number:"));
        accountNumberField = new JTextField();
        panel.add(accountNumberField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });
        panel.add(loginButton);

        JButton signupButton = new JButton("Signup");
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSignupForm();
            }
        });
        panel.add(signupButton);

        balanceLabel = new JLabel("Balance: ");
        panel.add(balanceLabel);

        JButton depositButton = new JButton("Deposit");
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deposit();
            }
        });
        panel.add(depositButton);

        JButton withdrawButton = new JButton("Withdraw");
        withdrawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                withdraw();
            }
        });
        panel.add(withdrawButton);

        JButton transferButton = new JButton("Transfer");
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                transfer();
            }
        });
        panel.add(transferButton);

        add(panel, BorderLayout.CENTER);

        // Add the username label at the bottom
        usernameLabel = new JLabel(" ");
        usernameLabel.setHorizontalAlignment(SwingConstants.CENTER);
        add(usernameLabel, BorderLayout.SOUTH);
    }

    private void login() {
        String accountNumber = accountNumberField.getText();
        String password = new String(passwordField.getPassword());

        currentUser = new User(accountNumber);
        if (currentUser.validateCredentials(password)) {
            double balance = currentUser.getBalance();
            balanceLabel.setText("Balance: " + balance);
            usernameLabel.setText("Logged in as: " + currentUser.getUsername());  // Update username label
            System.out.println("Login successful. Balance: " + balance);
        } else {
            JOptionPane.showMessageDialog(this, "Invalid account number or password", "Error", JOptionPane.ERROR_MESSAGE);
            currentUser = null;
            usernameLabel.setText(" ");  // Clear the username label
        }
    }

    private void deposit() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to deposit:");
        if (amountStr != null && !amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > 0) {
                    currentUser.updateBalance(amount);
                    updateBalanceLabel();
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void withdraw() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to withdraw:");
        if (amountStr != null && !amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > 0) {
                    currentUser.updateBalance(-amount);
                    updateBalanceLabel();
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void transfer() {
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this, "Please login first", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String recipientAccountNumber = JOptionPane.showInputDialog(this, "Enter recipient account number:");
        String amountStr = JOptionPane.showInputDialog(this, "Enter amount to transfer:");
        if (recipientAccountNumber != null && !recipientAccountNumber.isEmpty() && amountStr != null && !amountStr.isEmpty()) {
            try {
                double amount = Double.parseDouble(amountStr);
                if (amount > 0) {
                    User recipientUser = new User(recipientAccountNumber);

                    try (Connection conn = JDBCUtil.getConnection()) {
                        conn.setAutoCommit(false);

                        if (currentUser.updateBalance(-amount, conn) && recipientUser.updateBalance(amount, conn)) {
                            conn.commit();
                            updateBalanceLabel();
                            JOptionPane.showMessageDialog(this, "Transfer successful");
                        } else {
                            conn.rollback();
                            JOptionPane.showMessageDialog(this, "Transfer failed", "Error", JOptionPane.ERROR_MESSAGE);
                        }

                        conn.setAutoCommit(true);
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "Amount must be positive", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateBalanceLabel() {
        double balance = currentUser.getBalance();
        balanceLabel.setText("Balance: " + balance);
    }

    private void openSignupForm() {
        SignupForm signupForm = new SignupForm(this);
        signupForm.setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new BankingApp().setVisible(true);
            }
        });
    }
}
