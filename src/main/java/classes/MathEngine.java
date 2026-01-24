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

    public Question generateQuestion(int difficulty, String mode) {
        if ("BINARY".equalsIgnoreCase(mode)) {
            return generateBinary(difficulty);
        }
        return generateQuestion(difficulty);
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

    private Question generateBinary(int difficulty) {
        int bits;
        if (difficulty <= 5) {
            bits = 4; // 4-bit numbers
        } else if (difficulty <= 10) {
            bits = 6; // 6-bit numbers
        } else {
            bits = 8; // 8-bit numbers
        }

        int maxVal = 1 << bits;
        int a = random.nextInt(maxVal);
        int b = random.nextInt(maxVal);

        int op = random.nextInt(5); // 0: AND, 1: OR, 2: XOR, 3: <<, 4: >>
        int result;
        String expr;
        
        // Format 'a' as binary string
        String binA = String.format("%" + bits + "s", Integer.toBinaryString(a)).replace(' ', '0');

        if (op <= 2) {
            // Standard Bitwise Operations (AND, OR, XOR)
            String symbol;
            String binB = String.format("%" + bits + "s", Integer.toBinaryString(b)).replace(' ', '0');
            
            switch (op) {
                case 0: result = a & b; symbol = "AND"; break;
                case 1: result = a | b; symbol = "OR"; break;
                default: result = a ^ b; symbol = "XOR"; break;
            }
            expr = binA + " " + symbol + " " + binB;
        } else {
            // Shift Operations (<<, >>) - Shift by a small amount (1-3)
            int shift = random.nextInt(3) + 1; 
            if (op == 3) {
                result = a << shift;
                expr = binA + " LSHIFT " + shift;
            } else {
                result = a >> shift;
                expr = binA + " RSHIFT " + shift;
            }
        }

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

            if (wrong != result && !choices.contains(wrong) && wrong >= 0) {
                choices.add(wrong);
            }
        }
        Collections.shuffle(choices);
        return new Question(expr, result, choices);
    }
}
