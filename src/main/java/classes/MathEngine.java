package classes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class MathEngine {

    private final Random random = new Random();

    public Question generateQuestion(int difficulty) {
        if (difficulty <= 5) {
            return generateBasic(difficulty);      //Level 1-5: + and -
        } else if (difficulty <= 10) {
            return generateIntermediate(difficulty); //Level 6-10: * and /
        } else {
            return generateAdvanced(difficulty);     //Level 10+: () and Mixed
        }
    }

    private Question generateBasic(int difficulty) {
        int a = random.nextInt(10 * difficulty) + 1;
        int b = random.nextInt(10 * difficulty) + 1;

        boolean isAddition = random.nextBoolean();
        int result;
        String expr;

        if (isAddition) {
            result = a + b;
            expr = a + " + " + b;
        } else {
            if (a < b) { int temp = a; a = b; b = temp; } // Avoid negatives
            result = a - b;
            expr = a + " - " + b;
        }
        return createQuestionObj(expr, result);

    }

    private Question generateIntermediate(int difficulty) {
        boolean isMult = random.nextBoolean();
        int result;
        String expr;

        if (isMult) {
            int a = random.nextInt(5 * (difficulty - 2)) + 2;
            int b = random.nextInt(10) + 2;
            result = a * b;
            expr = a + " x " + b;
        } else {
            // REVERSE DIVISION: Generate Answer first, then calculate Question
            int answer = random.nextInt(10) + 2;
            int divisor = random.nextInt(10) + 2;
            int dividend = answer * divisor;

            result = answer;
            expr = dividend + " ÷ " + divisor;
        }
        return createQuestionObj(expr, result);
    }

    private Question generateAdvanced(int difficulty) {
        int a = random.nextInt(10) + 1;
        int b = random.nextInt(10) + 1;
        int c = random.nextInt(5) + 2;

        int result = (a + b) * c;
        String expr = "(" + a + " + " + b + ") × " + c;

        return createQuestionObj(expr, result);
    }

    //Generates Wrong Answers
    private Question createQuestionObj(String expr, int result) {
        List<Integer> choices = new ArrayList<>();
        choices.add(result);

        while (choices.size() < 4) {
            // Generate numbers close to the answer (-15 to +15 from the actual answer)
            int offset = random.nextInt(31) - 15;
            int wrong = result + offset;

            if (wrong != result && !choices.contains(wrong) && wrong > 0) {
                choices.add(wrong);
            }
        }
        Collections.shuffle(choices);
        return new Question(expr, result, choices);
    }
}
