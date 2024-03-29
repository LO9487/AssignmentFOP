import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;
import java.util.Calendar;

public class Database {
    private Connection conn;

    public Database() {
        try {
            // Connect to the database
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-user", "root", "Lojiakeng87");

            // Create a table for the users if it doesn't exist
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS users (username VARCHAR(255), email VARCHAR(255), password VARCHAR(255), registration_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP, last_login TIMESTAMP, score INT DEFAULT 0, last_checkin DATE)");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addUser(String username, String email, String password) {
        try {
            int shift = 3;

            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,.!?@#$%^&*()<>[]{}_-=+;:| ";

            StringBuilder cipherText = new StringBuilder();
            for (int i = 0; i < password.length(); i++) {
                int charPosition = alphabet.indexOf(password.charAt(i));
                if (charPosition == -1) {
                    throw new IllegalArgumentException("Invalid character in password");
                }
                int keyValue = (shift + charPosition) % alphabet.length();
                char replaceVal = alphabet.charAt(keyValue);
                cipherText.append(replaceVal);
            }
            String cipherPassword = cipherText.toString();

            // Insert the user into the database
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO users (username, email, password) VALUES (?, ?, ?)");
            pstmt.setString(1, username);
            pstmt.setString(2, email);
            pstmt.setString(3, cipherPassword);  // Store the ciphered password
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int checkUser(String email, String password) {
        try {

            int shift = 3;

            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,.!?@#$%^&*()<>[]{}_-=+;:| ";

            StringBuilder cipherText = new StringBuilder();
            for (int i = 0; i < password.length(); i++) {
                int charPosition = alphabet.indexOf(password.charAt(i));
                if (charPosition == -1) {
                    throw new IllegalArgumentException("Invalid character in password");
                }
                int keyValue = (shift + charPosition) % alphabet.length();
                char replaceVal = alphabet.charAt(keyValue);
                cipherText.append(replaceVal);
            }
            String cipherPassword = cipherText.toString();

            // Check if the user exists in the database
            PreparedStatement pstmt = conn.prepareStatement("SELECT * FROM users WHERE email = ?");
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            // If the query returns a result, the user exists
            if (rs.next()) {
                if (rs.getString("password").equals(cipherPassword)) {
                    // Update the last login time
                    PreparedStatement pstmtUpdate = conn.prepareStatement("UPDATE users SET last_login = CURRENT_TIMESTAMP WHERE email = ?");
                    pstmtUpdate.setString(1, email);
                    pstmtUpdate.executeUpdate();

                    return 0;  // User exists and password is correct
                }
                else {
                    return 1;  // User exists but password is incorrect
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 2;  // User doesn't exist
    }

    public void updateScore(String email, int score) {
        String query = "UPDATE users SET score = score + ? WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, score);
            stmt.setString(2, email);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updatePassword(String email, String password){
        try {
            int shift = 3;
            String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789,.!?@#$%^&*()<>[]{}_-=+;:| ";

            StringBuilder cipherText = new StringBuilder();
            for (int i = 0; i < password.length(); i++) {
                int charPosition = alphabet.indexOf(password.charAt(i));
                if (charPosition == -1) {
                    throw new IllegalArgumentException("Invalid character in password");
                }
                int keyValue = (shift + charPosition) % alphabet.length();
                char replaceVal = alphabet.charAt(keyValue);
                cipherText.append(replaceVal);
            }
            String cipherPassword = cipherText.toString();

            String query ="UPDATE users SET password = ? WHERE email = ?";
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, cipherPassword);  // Store the ciphered password
                stmt.setString(2, email);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void updatePointForDonation(String username, int newPoint){
        try{
            try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-user", "root", "Lojiakeng87")){
                String query= "UPDATE users SET score = ? WHERE username=?";
                try(PreparedStatement preparedStatement = con.prepareStatement(query)){
                    preparedStatement.setInt(1,newPoint);
                    preparedStatement.setString(2,username);
                    preparedStatement.executeUpdate();
                }
            }
            System.out.println("Point updated successfully");
            JOptionPane.showMessageDialog(null, "Thank you for your donation, your point updated!");
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
    }

    public int getScore(String email) {
        String query = "SELECT score FROM users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("score");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getCurrentPointForDonation(String username){
        int currentPoints=0;
        try{
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-user", "root", "Lojiakeng87");
            String query="SELECT *FROM users WHERE username = ?";

            try(PreparedStatement preparedStatement = con.prepareStatement(query)){
                preparedStatement.setString(1, username);
                try(ResultSet resultSet = preparedStatement.executeQuery()){
                    if(resultSet.next()){
                        currentPoints = resultSet.getInt("score");

                    }
                }
            }

        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return currentPoints;
    }

    public String getUsername(String email) {
        String query = "SELECT username FROM users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("username");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    public int getCurrentXp(String username){
        int currentPoints=0;
//        try{
//            Connection con =DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-user", "root", "");
        String query="SELECT *FROM users WHERE username = ?";

        try(PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setString(1, username);
            try(ResultSet resultSet = preparedStatement.executeQuery()){
                if(resultSet.next()){
                    currentPoints = resultSet.getInt("xp");
                }
            }
        }catch(Exception e){
            System.out.println(e.getMessage());
        }
        return currentPoints;
    }


    public void saveXp(String username, int pluspoint){
        int newXp =getCurrentXp(username)+pluspoint;
//        try(Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-user", "root", "")){
        String query= "UPDATE users SET xp = ?,xpLastUpdate =CURRENT_TIMESTAMP WHERE username=?";
        try(PreparedStatement preparedStatement = conn.prepareStatement(query)){
            preparedStatement.setInt(1,newXp);
            preparedStatement.setString(2,username);
            preparedStatement.executeUpdate();
        }catch(Exception e){
            System.out.println(e.getMessage());
        }

        System.out.println("Point updated successfully");

    }


    public LocalDate getRegistrationDate(String email) {
        String query = "SELECT registration_date FROM users WHERE email = ?";
        try (PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, email);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Date regDate = rs.getDate("registration_date");
                    return regDate.toLocalDate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void initializeUserButtons(String username) {
        try {
            String sql = "INSERT INTO UserButtons (Username, Button1, Button2, Button3, Button4, Button5, Button6, Button7, Button8, Button9, Button10) VALUES (?, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE, FALSE)";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void setButtonUsed(String username, int buttonId) {
        try {
            String sql = "UPDATE UserButtons SET button" + buttonId + " = TRUE WHERE Username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isButtonUsed(String username, int buttonId) {
        try {
            String sql = "SELECT button" + buttonId + " FROM UserButtons WHERE Username = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBoolean(1);
            } else {
                throw new IllegalArgumentException("Invalid username");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public int checkIn(String email) {

        try {
            // Get the last check-in date
            PreparedStatement pstmt = conn.prepareStatement("SELECT last_checkin FROM users WHERE email = ?");
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                Date lastCheckin = rs.getDate("last_checkin");
                Calendar cal = Calendar.getInstance();
                cal.setTimeInMillis(System.currentTimeMillis());
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                java.sql.Date today = new java.sql.Date(cal.getTimeInMillis());
//                    long millis=System.currentTimeMillis();
//                    java.sql.Date today = new java.sql.Date(millis);
//                    Date today = new Date(System.currentTimeMillis());

                // Check if the user has already checked in today
                if (lastCheckin != null && lastCheckin.equals(today)) {
                    // User has already checked in today
                    return 0;
                } else {
                    // User hasn't checked in today, so increase the score and update the last check-in date
                    PreparedStatement pstmtUpdate = conn.prepareStatement("UPDATE users SET score = score + 1, last_checkin = ? WHERE email = ?");
                    pstmtUpdate.setDate(1, today);
                    pstmtUpdate.setString(2, email);
                    pstmtUpdate.executeUpdate();

                    // Get the new score
                    pstmt = conn.prepareStatement("SELECT score FROM users WHERE email = ?");
                    pstmt.setString(1, email);
                    rs = pstmt.executeQuery();

                    if (rs.next()) {
                        return 1;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }
    public boolean isEmailRegistered(String email) {
        try {
        // Establish a connection to the database
        Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/jdbc-user", "root", "Lojiakeng87");

        // Create a statement
        Statement stmt = conn.createStatement();

        // Execute a query to check if the email is already registered
        ResultSet rs = stmt.executeQuery("SELECT * FROM users WHERE email = '" + email + "'");

        // If the email is already registered
        if (rs.next()) {
            JOptionPane.showMessageDialog(null, "The email has been registered");
            return true;
        }

        // Close the connection
        conn.close();
    } catch (SQLException e) {
        e.printStackTrace();
    }

    return false;
}

}