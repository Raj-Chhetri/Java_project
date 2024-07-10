import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SignupForm extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private BankingApp parentApp;

    public SignupForm(BankingApp parentApp) {
        this.parentApp = parentApp;

        setTitle("Signup Form");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(4, 2));
        

        panel.add(new JLabel("Username:"));
        usernameField = new JTextField();
        panel.add(usernameField);

        panel.add(new JLabel("Password:"));
        passwordField = new JPasswordField();
        panel.add(passwordField);

        panel.add(new JLabel("Confirm Password:"));
        confirmPasswordField = new JPasswordField();
        panel.add(confirmPasswordField);

        JButton signupButton = new JButton("Signup");
        signupButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                signup();
            }
        });
        panel.add(signupButton);

        add(panel);
    }

    private void signup() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Passwords do not match", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        User newUser = new User(null);
        if (newUser.createAccount(username, password)) {
            JOptionPane.showMessageDialog(this, "Signup successful. Your account number is " + newUser.getAccountNumber());
            dispose();  // Close the signup form
        } else {
            JOptionPane.showMessageDialog(this, "Signup failed. Account number may already exist.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
