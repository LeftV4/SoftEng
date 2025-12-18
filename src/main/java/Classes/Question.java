package Classes;

import java.util.List;

public class Question {
    private final String expression;
    private final int correctAnswer;
    private final List<Integer> choices;

    //Constructor
    public Question(String expression, int correctAnswer, List<Integer> choices) {
        this.expression = expression;
        this.correctAnswer = correctAnswer;
        this.choices = choices;
    }

    //Getters
    public String getExpression() { return expression; }
    public int getCorrectAnswer() { return correctAnswer; }
    public List<Integer> getChoices() { return choices; }

    //toString
    @Override
    public String toString() {
        return "Question[" +
                "expression=" + expression + ", " +
                "correctAnswer=" + correctAnswer + ", " +
                "choices=" + choices + ']';
    }
}