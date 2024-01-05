import javax.swing.*;

public class PointsShop extends JFrame {
    private String email;
    private Database db;
    public PointsShop(String email,Database db) {

        home homeFrame = new home(email,db);
        homeFrame.setVisible(true);
        homeFrame.pack();
        homeFrame.setLocationRelativeTo(null);

    }

}