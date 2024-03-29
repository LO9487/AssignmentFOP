import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TriviaQuestion {
    public JFrame frame;
    private JButton[] questionButtons;
    private JTextField answerField;
    private JButton submitButton;
    private JButton exitButton;
    private JTextArea questionArea;
    private JLabel resultLabel;
    private JPanel cards;
    private CardLayout cardLayout;
    private String[] questions;
    private String[] answers;
    private String[] correctAnswers; // New array for correct answers
    private int[] score;
    private int[] attempts;
    private int totalPoints;
    private int currentQuestion;
    private JList<String> optionsList;
    private JTextField answerText;
    private String email;
    private boolean[] answeredCorrectly;
    private String[] correctAnswerInWord;


    public TriviaQuestion(String email, Database db) {
        this.email = email;
        // frame
        frame = new JFrame("Trivia Question: ");
        frame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
        frame.setSize(800, 400);

        // Set up labels, buttons, and card layouts
        questionButtons = new JButton[10];
        answerField = new JTextField(); // Single Line text editor
        submitButton = new JButton("Submit Answer");
        exitButton = new JButton("Exit");
        questionArea = new JTextArea(); // Multiple line text editor
        resultLabel = new JLabel("");

        cards = new JPanel(new CardLayout());
        cardLayout = (CardLayout) cards.getLayout();
        questions = new String[10];
        answers = new String[10];
        correctAnswers = new String[10]; // Initialize the correctAnswers array
        attempts = new int[10];
        answeredCorrectly = new boolean[10];
        correctAnswerInWord = new String[10];

        loadQuestionsFromFile("TriviaSample.txt");
//        resetTrivia("TriviaSample.txt");

        // Specify the file name here

        // Create panels for different components
        JPanel questionPanel = new JPanel(new BorderLayout());
        questionPanel.add(new JLabel("Question:"), BorderLayout.NORTH);
        questionPanel.add(questionArea, BorderLayout.CENTER);

        JPanel optionsPanel = new JPanel(new BorderLayout());
        optionsList = new JList<>();
        optionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        optionsList.setEnabled(true);
        optionsPanel.add(new JLabel("Options:"), BorderLayout.NORTH);
        optionsPanel.add(new JScrollPane(optionsList), BorderLayout.CENTER);

        JPanel answerPanel = new JPanel(new BorderLayout());
        answerText = new JTextField(); // Change to JTextField
        answerPanel.add(new JLabel("Your Answer:"), BorderLayout.NORTH);
        answerPanel.add(answerText, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(submitButton);
        buttonPanel.add(exitButton);

        JPanel resultPanel = new JPanel();
        resultPanel.add(resultLabel);

        JPanel answerPage = new JPanel(new BorderLayout());
        answerPage.add(questionPanel, BorderLayout.NORTH);
//        answerPage.add(optionsPanel, BorderLayout.WEST);
        answerPage.add(answerPanel, BorderLayout.CENTER);
        answerPage.add(resultPanel, BorderLayout.SOUTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(answerPage, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        submitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                submitAnswer(currentQuestion, answerText.getText(),db);
            }
        });

        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                cardLayout.previous(cards); // Go to the previous card
                answerText.setText(""); // Clear the answer text field
                resultLabel.setText(""); // Reset the result label
                resetTrivia("TriviaSample.txt");
            }
        });

        JPanel questionPanelMain = new JPanel();
        questionPanelMain.setLayout(new GridLayout(10, 1));
        LocalDate userRegistrationDate = db.getRegistrationDate(email);
        int dayCount = calculateDayCount(userRegistrationDate, LocalDate.now());
        for (int i = 0; i < 10; i++) {
            questionButtons[i] = new JButton("Question " + (i + 1));
            int numberI = i;
            if (i > dayCount) {
                questionButtons[i].setEnabled(false);
            }
            questionButtons[i].addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    currentQuestion = numberI;
                    cardLayout.next(cards);

                    if (e.getSource() == questionButtons[numberI]) {
                        // Check if the button has been used before
                        if (db.isButtonUsed(db.getUsername(email), numberI+1)) {
                            displayQuestionOnly(numberI);
                        } else {
                            displayQuestion(numberI);
                            // Mark the button as used
                            db.setButtonUsed(db.getUsername(email), numberI+1);
//                            questionButtons[numberI].setEnabled(false);
                        }
                    }
                }
            });
            questionPanelMain.add(questionButtons[i]);
        }

        cards.add(questionPanelMain, "Question Page");
        cards.add(mainPanel, "Answer Page");

        frame.add(cards);
        frame.setVisible(true);
    }

    private void loadQuestionsFromFile(String fileName) {
        try (FileReader reader = new FileReader(fileName);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            String question =null;
            String option;
            String answer;
            int questionIndex = 0;

            while ((line = br.readLine()) != null && questionIndex < 10) {
//                In case the questions are in multiple lines
                    String nextLine = br.readLine();
                    if(nextLine.contains("?")){
                        question= line +"\n"+ nextLine;
                        option = br.readLine();}
                    else{ option = nextLine;}

//                    In case options in multiple lines
                    String lineAfterQuestion = br.readLine();
                    if(lineAfterQuestion.contains(",")||lineAfterQuestion.contains("character")){
                        option = option + lineAfterQuestion;
                        answer = br.readLine();
                    }
                    else{answer = lineAfterQuestion;}
                    String blank = br.readLine();
                questions[questionIndex] = question;
                answers[questionIndex] = option ;// Store the options in the answers array
                correctAnswers[questionIndex] = answer; // Store the correct answer in the correctAnswers array

                questionIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> separateChoices(String option) {

        String[] choicesArray = option.split(",");
        List<String> choices = new ArrayList<>();
        for (String choice : choicesArray) {
            choices.add(choice.trim());
        }
        return choices;
    }

    private void displayQuestion(int questionNumber) {
        answerText.setEnabled(true);
        List<String> choices = separateChoices(answers[questionNumber]);
        Collections.shuffle(choices); // Shuffle the options
        answers[questionNumber] = String.join(",", choices); // Update the answers with the shuffled options

        // Find the new position of the correct answer after shuffling
        int correctAnswerIndex = choices.indexOf(correctAnswers[questionNumber]);
        correctAnswerInWord[questionNumber] =  correctAnswers[questionNumber];
        correctAnswers[questionNumber] = String.valueOf(correctAnswerIndex);

        questionArea.setText("Day " + (questionNumber + 1) + " Trivia (Attempt #" + (attempts[questionNumber] + 1) + ")\n" +
                "======================================================================================================\n" +
                questions[questionNumber] + "\n" +
                "======================================================================================================");
        char option = 'A';
        for (String choice : choices) {
            questionArea.append("\n[" + option + "] " + choice);
            option++;
        }
        questionArea.append("\n======================================================================================================\n" +
                "Enter your answer (A/B/C/D):");

        // Force the GUI to refresh and display the new text
        questionArea.repaint();
        questionArea.revalidate();
        questionArea.setEditable(false);

    }

    private void displayQuestionOnly(int questionNumber) {
        answerText.setEnabled(false);
        List<String> choices = separateChoices(answers[questionNumber]);

        questionArea.setText("Day " + (questionNumber + 1) + " Trivia (Attempt #" + (attempts[questionNumber] + 1) + ")\n" +
                "======================================================================================================\n" +
                questions[questionNumber] + "\n" +
                "======================================================================================================");
        char option = 'A';
        for (String choice : choices) {
            questionArea.append("\n[" + option + "] " + choice);
            option++;
        }
        questionArea.append("\n======================================================================================================\n" +
                "Enter your answer (A/B/C/D):");

        // Find the index of the correct answer in the list of choices
        int correctAnswerIndex = choices.indexOf(correctAnswers[questionNumber]);

        answerText.setText("Answer: [" + (char)('A' + correctAnswerIndex%26)+"]"+correctAnswers[questionNumber]);
        // Force the GUI to refresh and display the new text
        questionArea.repaint();
        questionArea.revalidate();
        questionArea.setEditable(false);
    }

    private void submitAnswer(int questionNumber, String userAnswer, Database db) {
        if (userAnswer.charAt(0) < 'A' || userAnswer.charAt(0)> 'D') {
            resultLabel.setText("Invalid input. Please enter a letter between A and D.");
            return;
        }
        attempts[questionNumber]++;
        List<String> choices = separateChoices(answers[questionNumber]);
        int answerIndex = userAnswer.toUpperCase().charAt(0) - 'A';
        String selectedOption = choices.get(answerIndex); // Get the selected option based on user's input
        int correctAnswerIndex = Integer.parseInt(correctAnswers[questionNumber]); // Get the correct answer from the correctAnswers array
        int points = 0;
        if (answerIndex == correctAnswerIndex) {
            if (!answeredCorrectly[questionNumber]) {
                // Only update the score if the question hasn't been answered correctly yet
//                int points = (attempts[questionNumber] == 1) ? 2 : 1;

                if(attempts[questionNumber] == 1){points = 2;}
                else if(attempts[questionNumber] == 2){points = 1;}
                else if(attempts[questionNumber] >2){points = 0;}


                answeredCorrectly[questionNumber] = true;  // Mark the question as answered correctly
                int totalScore = db.getScore(email);  // Retrieve the updated score
                if(points ==2||points == 1){
                    resultLabel.setText("Congratulations! You answered it correctly. You have been awarded " + points + " points, you now have " + (totalScore+points) + " points.");

                }
            } else {
                resultLabel.setText("The correct answer is: [" + (char)('A' + correctAnswerIndex%26)+"]"+correctAnswerInWord[questionNumber] + ". You can try again but you will not get any marks.");
            }
        } else {
            if (attempts[questionNumber] == 1) {
                resultLabel.setText("Whoops, that doesn’t look right, try again!");
            } else if (attempts[questionNumber] == 2) {
                resultLabel.setText("Your answer is still incorrect, the correct answer is: [" + (char)('A' + correctAnswerIndex%26)+"]"+correctAnswerInWord[questionNumber]);
            } else {
                resultLabel.setText("The correct answer is: [" + (char)('A' + correctAnswerIndex%26)+"]"+correctAnswerInWord[questionNumber] + ". You can try again but you will not get any marks.");
            }
        }
        db.updateScore(email, points);  // Update the score in the database
        db.saveXp(db.getUsername(email),points);
        String correctAnswer = choices.get(correctAnswerIndex);  // Save the correct answer string

        Collections.shuffle(choices);
        answers[questionNumber] = String.join(",", choices);

// Find the new position of the correct answer after shuffling
        correctAnswerIndex = choices.indexOf(correctAnswer);
        correctAnswers[questionNumber] = correctAnswer;

// Display the next question with the shuffled options
        displayQuestion(questionNumber);
    }

    public static int calculateDayCount(LocalDate userRegistrationDate, LocalDate currentDate) {
        long days = ChronoUnit.DAYS.between(userRegistrationDate, currentDate) ;
        return (int) days;
    }

    public void resetTrivia(String fileName) {
        try (FileReader reader = new FileReader(fileName);
             BufferedReader br = new BufferedReader(reader)) {

            String line;
            int questionIndex = 0;

            while ((line = br.readLine()) != null && questionIndex < 10) {
                String question = line;
                String options = br.readLine();
                String answer = br.readLine();
                String blank = br.readLine();

                questions[questionIndex] = question;
                answers[questionIndex] = options; // Store the options in the answers array
                correctAnswers[questionIndex] = answer; // Store the correct answer in the correctAnswers array

                questionIndex++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}