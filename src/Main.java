import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.regex.*;

public class Main {
    private JFrame frame;
    private JTextField emailField;
    private JPasswordField passwordField;
    private Database db;

    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Main window = new Main();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public Main() {
        db = new Database();
        initialize();
    }

    public void initialize() {
        frame = new JFrame();
        frame.setBounds(100, 100, 600, 400);  // Increase the size of the frame
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(null);

//        JLabel lblUsername = new JLabel("Username");
//        lblUsername.setBounds(58, 104, 100, 14);  // Adjust these values as needed
//        frame.getContentPane().add(lblUsername);
//
//        JTextField usernameField = new JTextField();
//        usernameField.setBounds(168, 101, 86, 20);  // Adjust these values as needed
//        frame.getContentPane().add(usernameField);
//        usernameField.setColumns(10);

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setBounds(58, 132, 100, 14);  // Increase the size of the label
        frame.getContentPane().add(lblEmail);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setBounds(58, 157, 100, 14);  // Increase the size of the label
        frame.getContentPane().add(lblPassword);

        emailField = new JTextField();
        emailField.setBounds(168, 129, 86, 20);  // Move the text field to the right
        frame.getContentPane().add(emailField);
        emailField.setColumns(10);

        passwordField = new JPasswordField();
        passwordField.setBounds(168, 154, 86, 20);  // Move the text field to the right
        frame.getContentPane().add(passwordField);

        JButton btnLogin = new JButton("Login");
        btnLogin.setBounds(58, 195, 89, 23);  // Set the bounds for the button
        btnLogin.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                String username = usernameField.getText();
                String email = emailField.getText();
                String password = new String(passwordField.getPassword());

                int result = db.checkUser(email, password);
                if (result == 0) {
                    // User exists, proceed with login
                    UserDashboard dashboard = new UserDashboard(email);
                    dashboard.setVisible(true);
                    frame.dispose();
                } else if (result == 1) {
                    // User exists but password is incorrect
                    JOptionPane.showMessageDialog(frame, "Incorrect password.");
                } else {
                    // User doesn't exist
                    JOptionPane.showMessageDialog(frame, "The email hasn't registered yet.");
                }
            }
        });
        frame.getContentPane().add(btnLogin);

        JButton btnRegister = new JButton("Register");
        btnRegister.setBounds(157, 195, 89, 23);  // Set the bounds for the button
        btnRegister.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Open the registration page
                RegistrationPage regPage = new RegistrationPage();
                regPage.setVisible(true);
            }
        });
        frame.getContentPane().add(btnRegister);
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pat = Pattern.compile(emailRegex);
        return pat.matcher(email).matches();
    }

    class RegistrationPage extends JFrame {
        private JTextField usernameField;
        private JTextField emailField;
        private JPasswordField passwordField;

        public RegistrationPage() {
            setBounds(100, 100, 450, 300);
            setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            getContentPane().setLayout(null);

            JLabel lblEmail = new JLabel("Email");
            lblEmail.setBounds(58, 132, 46, 14);
            getContentPane().add(lblEmail);

            JLabel lblPassword = new JLabel("Password");
            lblPassword.setBounds(58, 157, 46, 14);
            getContentPane().add(lblPassword);

            JLabel lblUsername = new JLabel("Username");
            lblUsername.setBounds(58, 104, 100, 14);  // Increase the size of the label
            getContentPane().add(lblUsername);

            usernameField = new JTextField();
            usernameField.setBounds(124, 101, 86, 20);  // Move the text field to the right
            getContentPane().add(usernameField);
            usernameField.setColumns(10);

            emailField = new JTextField();
            emailField.setBounds(124, 129, 86, 20);
            getContentPane().add(emailField);
            emailField.setColumns(10);

            passwordField = new JPasswordField();
            passwordField.setBounds(124, 154, 86, 20);
            getContentPane().add(passwordField);

            JButton btnRegister = new JButton("Register");
            btnRegister.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    String email = emailField.getText();
                    String password = new String(passwordField.getPassword());
                    String username = usernameField.getText();

                    if (isValidEmail(email)) {
                        db.addUser(username,email, password);
                        dispose();  // Close the registration page
                    } else {
                        // Email is invalid
                    }
                }
            });
            btnRegister.setBounds(157, 195, 89, 23);
            getContentPane().add(btnRegister);
        }
    }

    class UserDashboard extends JFrame {
        private String email;
        private String username;
        public UserDashboard(String email) {
            this.username = username;
            this.email = email;
            setBounds(100, 100, 450, 300);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            getContentPane().setLayout(new GridLayout(5, 1));

            JLabel lblTitle = new JLabel("CodeForNature", SwingConstants.CENTER);
            lblTitle.setFont(new Font("Serif", Font.BOLD, 24));
            getContentPane().add(lblTitle);

            JButton btnDailyCheckin = new JButton("Daily Checkin");
            btnDailyCheckin.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    int result = db.checkIn(email);  // You'll need to pass the user's email to the UserDashboard class

                    if (result == 0) {
                        JOptionPane.showMessageDialog(frame,  "You have checked in today.");
                    } else if (result > 0) {
                        JOptionPane.showMessageDialog(frame, "Welcome , 1 score is added, you now have " + result);
                    } else {
                        JOptionPane.showMessageDialog(frame, "An error occurred.");
                    }
                }
            });
            getContentPane().add(btnDailyCheckin);

            JButton btnNewsSection = new JButton("News Section");
            btnNewsSection.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    NewsSection newsSection = new NewsSection();
                    newsSection.setVisible(true);
                }
            });
            getContentPane().add(btnNewsSection);

            JButton btnTriviaQuestion = new JButton("Trivia Question");
            btnTriviaQuestion.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Add your logic here
                }
            });
            getContentPane().add(btnTriviaQuestion);

            JButton btnDonations = new JButton("Donations");
            btnDonations.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    // Add your logic here
                }
            });
            getContentPane().add(btnDonations);
        }
    }

}

