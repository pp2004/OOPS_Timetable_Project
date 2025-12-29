package view;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class LoginPanel extends JPanel {
    private MainWindow parent;

    public LoginPanel(MainWindow parent) {
        this.parent = parent;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        JLabel title = new JLabel("Login to Time Table Builder");
        title.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel userLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(15);

        JLabel passLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField(15);

        JCheckBox showPassword = new JCheckBox("Show Password");
        showPassword.addActionListener(e -> {
            if (showPassword.isSelected()) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });

        JButton loginButton = new JButton("Login");

        JLabel statusLabel = new JLabel("");
        statusLabel.setForeground(Color.RED);

        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(title, gbc);

        gbc.gridwidth = 1; gbc.gridy++;
        add(userLabel, gbc);
        gbc.gridx = 1;
        add(usernameField, gbc);

        gbc.gridx = 0; gbc.gridy++;
        add(passLabel, gbc);
        gbc.gridx = 1;
        add(passwordField, gbc);

        gbc.gridx = 1; gbc.gridy++;
        add(showPassword, gbc);

        gbc.gridx = 0; gbc.gridy++; gbc.gridwidth = 2;
        add(loginButton, gbc);

        gbc.gridy++;
        add(statusLabel, gbc);

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = usernameField.getText().trim();
                String pass = new String(passwordField.getPassword()).trim();

                if (user.isEmpty() || pass.isEmpty()) {
                    statusLabel.setText("Username or password cannot be empty.");
                    return;
                }

                if (user.equals("admin") && pass.equals("admin123")) {
                    parent.switchTo("admin");
                } else if (user.equals("teacher") && pass.equals("teacher123")) {
                    parent.switchTo("teacher");
                } else if (user.equals("student") && pass.equals("student123")) {
                    parent.switchTo("student");
                } else {
                    statusLabel.setText("Invalid credentials.");
                }
            }
        });
    }
}
