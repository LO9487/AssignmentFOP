import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class User extends JFrame {
    private JLabel lblEmail, lblUsername, lblPassword, lblRegDate, lblPoints;
    private JTextField txtEmail, txtUsername, txtRegDate, txtPoints;
    private JPasswordField txtPassword;
    private JButton btnShowPassword;

    public User(String email) {
        setLayout(new GridLayout(6, 2));

        lblEmail = new JLabel("Email:");
        lblUsername = new JLabel("Username:");
        lblPassword = new JLabel("Password:");
        lblRegDate = new JLabel("Registration Date:");
        lblPoints = new JLabel("Current Points:");

        txtEmail = new JTextField();
        txtUsername = new JTextField();
        txtPassword = new JPasswordField();
        txtRegDate = new JTextField();
        txtPoints = new JTextField();


        btnShowPassword = new JButton("Show Password");
        btnShowPassword.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getActionCommand().equals("Show Password")) {
                    txtPassword.setEchoChar((char) 0);
                    btnShowPassword.setText("Hide Password");
                } else {
                    txtPassword.setEchoChar('*');
                    btnShowPassword.setText("Show Password");
                }
            }
        });

        add(lblEmail);
        add(txtEmail);
        add(lblUsername);
        add(txtUsername);
        add(lblPassword);
        add(txtPassword);
        add(new JLabel());  // Empty space
        add(btnShowPassword);
        add(lblRegDate);
        add(txtRegDate);
        add(lblPoints);
        add(txtPoints);

        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-user", "root", "Lojiakeng87");
            PreparedStatement stmt = con.prepareStatement("SELECT * FROM Users WHERE email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                txtEmail.setText(rs.getString("email"));
                txtUsername.setText(rs.getString("username"));

                String cipherPassword = rs.getString("password");
                String decryptedPassword = decryptPassword(cipherPassword);
                txtPassword.setText(decryptedPassword);

                txtRegDate.setText(rs.getString("registration_date"));
                txtPoints.setText(String.valueOf(rs.getInt("score")));
            }

            con.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        txtEmail.setEditable(false);
        txtUsername.setEditable(false);
        txtPassword.setEditable(false);
        txtRegDate.setEditable(false);
        txtPoints.setEditable(false);

        pack();
        setVisible(true);
    }

    private String decryptPassword(String cipherPassword) {
        int shift = 3;
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,.!?@#$%^&*()";
        StringBuilder plainText = new StringBuilder();

        for (int i = 0; i < cipherPassword.length(); i++) {
            int charPosition = alphabet.indexOf(cipherPassword.charAt(i));
            int keyValue = (charPosition - shift) % alphabet.length();
            if (keyValue < 0) {
                keyValue = alphabet.length() + keyValue;
            }
            char replaceVal = alphabet.charAt(keyValue);
            plainText.append(replaceVal);
        }

        return plainText.toString();
    }
}
