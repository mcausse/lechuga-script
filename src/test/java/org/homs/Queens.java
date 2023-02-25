package org.homs;

import java.util.Arrays;

public class Queens {

    public static void main(String[] args) {
        int n = 8;
        final int[] state = new int[n];
        state[0] = -1;
        queenAtPos(n, state, 0);
    }

    static int solutionsFound = 0;

    static void displayState(int[] state) {
        System.out.println("SOLUTION #" + solutionsFound);
        solutionsFound++;
        System.out.println(Arrays.toString(state));
        for (int i = 0; i < state.length; i++) {
            for (int j = 0; j < state.length; j++) {
                System.out.print(state[i] == j ? "Q " : "· ");
            }
            System.out.println();
        }
        System.out.println();
    }

    static void queenAtPos(int n, int[] state, int row) {
        if (row == n) {
            displayState(state);
        } else {
            for (int col = 0; col < n; col++) {
                if (!isSafe(state, row, col)) {
                    continue;
                }
                state[row] = col;
                queenAtPos(n, state, row + 1);
            }
        }
    }

    static boolean isSafe(int[] state, int row, int col) {
//        for (int i = 0; i < row; i++) {
//            if (state[i] == col) {
//                return false;
//            }
//            if (Math.abs(state[i] - col) == Math.abs(i - row)) {
//                return false;
//            }
//        }
//        return true;
        boolean r = true;
        int i = 0;
        while (i < row && r) {
            r = state[i] != col && Math.abs(state[i] - col) != Math.abs(i - row);
            i++;
        }
        return r;
    }
}
