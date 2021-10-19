package jucumber;

import java.util.Stack;

public class Calculator {

    final Stack<String> stack = new Stack<>();

    public Calculator() {
        pressReset();
    }

    public void pressReset() {
        this.stack.clear();
        this.stack.push("0");
    }

    public void pressDigit(int digit) {
        if (stack.peek().equals("ERR")) {
            return;
        }
        if (digit < 0 || digit > 9) {
            stack.push("ERR");
        }
        stack.push(String.valueOf(Integer.parseInt(stack.pop()) * 10 + digit));
    }

    public void pressEnter() {
        if (stack.peek().equals("ERR")) {
            return;
        }
        stack.push("0");
    }

    public void pressAdd() {
        if (stack.peek().equals("ERR")) {
            return;
        }
        if (stack.size() < 2) {
            stack.push("ERR");
        }
        stack.push(String.valueOf(Integer.parseInt(stack.pop()) + Integer.parseInt(stack.pop())));
    }

    public String getResult() {
        return stack.peek();
    }
}