import javax.swing.*;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.*;
import java.util.List;

public class NewsSection extends JFrame {
    private JList<String> list;

    public NewsSection() {
        setBounds(100, 100, 600, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout());

        list = new JList<>();
        JScrollPane scrollPane = new JScrollPane(list);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Call the method to display the news
        displayNews();
        getContentPane().revalidate();
        getContentPane().repaint();
    }

    public void displayNews() {
        String filePath = "NewsSample.txt";
        try {
            List<String> natureHeadlines = selectNews(readFile(filePath));
            Top5News(natureHeadlines);

            // Display the news in the list
            DefaultListModel<String> model = new DefaultListModel<>();
            for (int i = 0; i < 5 && i < natureHeadlines.size(); i++) {
                model.addElement("[" + (i + 1) + "]" + natureHeadlines.get(i));
            }
            list.setModel(model);
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading from file:\n" + e.getMessage());
        }
    }
    public static List<String> selectNews(List<String> newLines) {
        List<String> natureHeadlines = new ArrayList<>();
        int i=0;
        while(i<newLines.size()){
            String headline=newLines.get(i);
            String url=newLines.get(i+1);
            String date=newLines.get(i+2);

            if (headline.toLowerCase().contains("nature")) {
                natureHeadlines.add(headline+"\n"+url+"\n"+date);
            }
            i+=3;
        }
        sortNews(natureHeadlines);
        return natureHeadlines;
    }

    public static void sortNews(List<String>newsList){
        for (int i=0;i< newsList.size()-1;i++){
            for (int j=i+1;j<newsList.size();j++){
                String date1=extractDate(newsList.get(i));
                String date2=extractDate(newsList.get(j));
                int compare=compareDates(date1, date2);
                if (compare>0){
                    Collections.swap(newsList,i,j);
                }
            }
        }
    }

    public static String extractDate(String news) {
        int lastNewlineIndex=news.lastIndexOf("\n");
        if (lastNewlineIndex!=-1) {
            return news.substring(lastNewlineIndex+1);
        }else{
            return "";
        }
    }

    public static int compareDates(String date1, String date2) {
        SimpleDateFormat sdf=new SimpleDateFormat("dd MMM yyyy");
        try {
            Date d1=sdf.parse(date1);
            Date d2=sdf.parse(date2);
            if (d1.before(d2)){
                return 1;
            } else if (d1.after(d2)){
                return -1;
            } else {
                return 0;
            }
        } catch (ParseException e) {
            e.printStackTrace();

        }
        return 0;
    }


    public static void Top5News(List<String> natureHeadlines) {
        System.out.println("Top 5 News about Nature");
        int size = natureHeadlines.size();
        for (int j=0;j<5&&j<size;j++){
            System.out.println("["+(j+1)+"]"+ natureHeadlines.get(j)+"\n");
        }
    }

    public static List<String> readFile(String filePath) throws IOException {
        List<String> newLines=new ArrayList<>();
        try{
            BufferedReader s=new BufferedReader(new FileReader(new File(filePath)));
            String line;
            while ((line=s.readLine())!=null){
                newLines.add(line);
            }
        }
        catch(IOException e) {
            System.out.println("Error reading from file");
        }
        return newLines;
    }
}

