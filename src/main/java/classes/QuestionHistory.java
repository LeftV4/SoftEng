package classes;

public class QuestionHistory {
    Question question;      // Holds the expression like "5 + 5"
    int selectedAnswer;     // Holds what the user actually clicked (e.g., 12)
    boolean isCorrect;      // True if they got it right, false otherwise

    // This is the "Constructor" to easily create the record
    QuestionHistory(Question q, int ans, boolean corr) {
        this.question = q;
        this.selectedAnswer = ans;
        this.isCorrect = corr;
    }
}
