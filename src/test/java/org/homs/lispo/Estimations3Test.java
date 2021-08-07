package org.homs.lispo;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

// https://www.mountaingoatsoftware.com/blog/why-the-fibonacci-sequence-works-well-for-estimating
// https://www.projectmanagement.com/contentPages/wiki.cfm?ID=368763&thisPageURL=/wikis/368763/3-Points-Estimating#_=_

/**
 * Ranged Estimations Calculator - an experimental 2-Point method
 * <p>
 * Usually the teams estimates the tasks giving the estimated amount of work in an
 * absolute units (usually in a pseudo-Fibonacci scale). But sometimes this single, absolute, scalar value
 * doesn't reflects the uncertainly.
 * <p>
 * Sometimes the teammates needs to express the uncertainly, and there appears the need to give a kind of
 * optimistic value ("if works at the first attempt, and with no surprises...") and a pessimistic value,
 * considering that maybe during the development of the task, we can found problems and surprises.
 * <p>
 * The problem is that can be difficult and time-wasting to calculate the average of these ranged
 * estimations during the planning session. This is the purpose of this little console-based application.
 * <p>
 * This application reads from the console (the standard input) a typical estimations round copied
 * from a meeting chat:
 *
 * <pre>
 * Raul Capillas1:04 PM
 * 5-7-8
 * You1:04 PM
 * 3-5-5
 * Pau Borras I Diaz1:04 PM
 * 5-10-15
 * Javier Herrada1:04 PM
 * 3-6-8
 * Josep Maria Torras1:04 PM
 * 8-?-13
 * .
 * </pre>
 * <p>
 * Remember to enter the dot character (".") in order to indicate that the round is finished. After that,
 * a graph of the estimations will be displayed in a pseudo-Fibonacci scale:
 *
 * <pre>
 *                [X][X][X][X]
 *          [X][X][X]
 *                [X][X][X][X][X][X][X][X][X][X][X]
 *          [X][X][X][X][X][X]
 * |--|--|--|--+--|--+--+--|--+--+--+--+--|--+--+--+--+--+--+--|--
 * 0  1  2  3     5        8              13                   20
 *                      ^
 * ** 7 ***
 *
 * </pre>
 * <p>
 * ...and the mass center of the graph is shown as an integer (7), and this is the average of the estimations.
 *
 * @author mohms
 */
public class Estimations3Test {

    // TODO o = optimistic estimate; m = most likely estimate; p = pessimistic estimate
    static class Estimation {
        public final int min;
        public final int likely;
        public final int max;

        public Estimation(int min, int likely, int max) {
            this.min = min;
            this.likely = likely;
            this.max = max;
        }

        @Override
        public String toString() {
            return min + "-" + likely + "-" + max;
        }
    }

    public static void main(String[] args) throws IOException {

        System.out.println("==============================================================");
        System.out.println("Ranged Estimations Calculator - an experimental 2-Point method");
        System.out.println("==============================================================");
        System.out.println("Paste your estimations from the chat.");
        System.out.println("Every vote should be in the format <min>-<?>-<max>,");
        System.out.println("and the round is finished entering \".\".");

        while (true) {
            System.out.print("\n\n>");
            StringBuilder input = new StringBuilder();
            while (true) {
                int inputChar = System.in.read();
                if (inputChar < 0) {
                    System.exit(1);
                }
                if ((char) inputChar == '.') {
                    break;
                }
                input.append((char) inputChar);
            }

            Estimations3Test target = new Estimations3Test();

            List<Estimation> votes = target.parseVotes(input.toString());
            if (votes.isEmpty()) {
                System.out.println("*** NO ESTIMATIONS PARSED ***");
                continue;
            }

            List<Integer> fibonacciSequence = Arrays.asList(0, 1, 2, 3, 5, 8, 13, 20, 40);


            System.out.println();
            for (Estimation vote : votes) {
                String voteLine = target.getVoteGraphLine(fibonacciSequence, vote);
                System.out.println(voteLine);
            }
            String scale = target.computeGraphScale(fibonacciSequence);
            System.out.println(scale);

            System.out.println("-----------------------------------------");


            /**
             * likely-based normal average
             */
            {
                double normalAverage = 0.0;
                for (Estimation vote : votes) {
                    normalAverage += vote.likely;
                }
                System.out.println("likely-based normal average: " + (normalAverage / votes.size()));
            }

            /**
             * 2-Point mass center
             */
            {
                int massCenter = target.compute2PointMassCenter(fibonacciSequence, votes);
                System.out.print("2-Point mass center: ");
                if (massCenter == Integer.MIN_VALUE) {
                    System.out.println("*** THERE IS NO MASS ***");
                } else {
                    System.out.println(massCenter);
                }
            }

            /**
             * 3-Point triangular
             */
            {
                double normalAverage = 0.0;
                for (Estimation vote : votes) {
                    normalAverage += (vote.min + vote.likely + vote.max) / 3.0;
                }
                System.out.println("3-Point triangular: " + (int) (normalAverage / votes.size()));
            }

            /**
             * 3-Point Beta Distribution (PERT)
             */
            {
                double normalAverage = 0.0;
                for (Estimation vote : votes) {
                    normalAverage += (vote.min + (4 * vote.likely) + vote.max) / 6.0;
                }
                System.out.println("3-Point Beta Distribution (PERT): " + (int) (normalAverage / votes.size()));
            }
        }
    }

    @Test
    @DisplayName("Acceptance test for parsing and calculate the 2-Point average")
    void acceptanceTest() {

        // GIVEN a fibonacci scale
        List<Integer> fibonacciSequence = Arrays.asList(0, 1, 2, 3, 5, 8, 13, 20, 40);

        // AND an input pasted from a meeting chat properly parsed
        String input = "Raul Capillas1:04 PM\n" +
                "        5-5-8\n" +
                "        You1:04 PM\n" +
                "        3-3-5\n" +
                "        Pau Borras I Diaz1:04 PM\n" +
                "        5-5-15\n" +
                "        Javier Herrada1:04 PM\n" +
                "        3-3-8\n" +
                "        Josep Maria Torras1:04 PM\n" +
                "        8";
        List<Estimation> votes = parseVotes(input);

        // WHEN the mass center of the estimations is computed
        int massCenter = compute2PointMassCenter(fibonacciSequence, votes);

        // THEN the expected value should be 7.
        assertThat(massCenter).isEqualTo(7);
    }

    /**
     * Iterates all the estimations and find the (integer) mass center.
     * The algorithm is linear, and can be improved doing a binary search.
     *
     * @param fibonacciSequence the range of all estimations.
     * @param votes             the list of estimations.
     * @return the best mass center point found.
     */
    int compute2PointMassCenter(List<Integer> fibonacciSequence, List<Estimation> votes) {

        int bestCenterPoint = Integer.MIN_VALUE;
        int lowestErrorAmount = Integer.MAX_VALUE;
        for (int centerPoint = Collections.min(fibonacciSequence); centerPoint <= Collections.max(fibonacciSequence); centerPoint++) {

            int massInTheLeft = 0;
            for (Estimation vote : votes) {
                for (int i = Collections.min(fibonacciSequence); i < centerPoint; i++) {
                    if (vote.min <= i && i <= vote.max) {
                        massInTheLeft++;
                    }
                }
            }
            int massInTheRight = 0;
            for (Estimation vote : votes) {
                for (int i = centerPoint; i <= Collections.max(fibonacciSequence); i++) {
                    if (vote.min <= i && i <= vote.max) {
                        massInTheRight++;
                    }
                }
            }

            if (massInTheLeft == 0 && massInTheRight == 0) {
                break;
            }

            int errorAmount = Math.abs(massInTheLeft - massInTheRight);
            if (lowestErrorAmount > errorAmount) {
                lowestErrorAmount = errorAmount;
                bestCenterPoint = centerPoint;
            }
        }

        return bestCenterPoint;
    }

    @ParameterizedTest
    @CsvSource({
            "2,3, 3,4, 3",
            "1,3, 3,5, 3",
            "20,30, 30,40, -2147483648",
    })
    void testComputeMassCenter(int minVote1, int maxVote1, int minVote2, int maxVote2, int expectedMassCenter) {
        List<Integer> fibonacciSequence = Arrays.asList(1, 10);
        var votes = Arrays.asList(
                new Estimation(minVote1, -1, maxVote1), new Estimation(minVote2, -1, maxVote2)
        );

        int massCenter = compute2PointMassCenter(fibonacciSequence, votes);

        assertThat(massCenter).isEqualTo(expectedMassCenter);
    }

    List<Estimation> parseVotes(String input) {
        List<Estimation> votes = new ArrayList<>();
        Pattern p = Pattern.compile("(\\d+)-(\\d+)-(\\d+)");
        Matcher m = p.matcher(input);
        while (m.find()) {
            Estimation vote = new Estimation(Integer.parseInt(m.group(1)), Integer.parseInt(m.group(2)), Integer.parseInt(m.group(3)));
            votes.add(vote);
        }
        return votes;
    }

    @Test
    void testParseVotes() {
        String a = "Raul Capillas1:04 PM\n" +
                "        5-6-8\n" +
                "        You1:04 PM\n" +
                "        3-4-5\n" +
                "        Pau Borras I Diaz1:04 PM\n" +
                "        5-9-20\n" +
                "        Javier Herrada1:04 PM\n" +
                "        3-5-8\n" +
                "        Josep Maria Torras1:04 PM\n" +
                "        8";

        List<Estimation> votes = parseVotes(a);

        assertThat(votes).hasSize(4);
        assertThat(votes.get(0).toString()).isEqualTo("5-6-8");
        assertThat(votes.get(1).toString()).isEqualTo("3-4-5");
        assertThat(votes.get(2).toString()).isEqualTo("5-9-20");
        assertThat(votes.get(3).toString()).isEqualTo("3-5-8");
    }

    /**
     * Computes the graph scale, using 3 characters per unit. The numbers
     * of the {@param fibonacciSequence} will be part of the legend.
     *
     * @param fibonacciSequence the numbers that will be part of the legend.
     * @return an {@link String} with two lines.
     */
    String computeGraphScale(List<Integer> fibonacciSequence) {
        StringBuilder scale = new StringBuilder();
        StringBuilder legend = new StringBuilder();
        for (int i = Collections.min(fibonacciSequence); i <= Collections.max(fibonacciSequence); i++) {
            if (fibonacciSequence.contains(i)) {
                scale.append("|--");
                legend.append(String.format("%-3d", i));
            } else {
                scale.append("+--");
                legend.append("   ");
            }
        }
        return scale + "\n" + legend;
    }

    @Test
    void testComputeGraphScale() {
        List<Integer> fibonacciSequence = Arrays.asList(0, 1, 2, 3, 5, 8, 13);

        String scale = computeGraphScale(fibonacciSequence);

        assertThat(scale.split("\\n")[0]).isEqualTo("|--|--|--|--+--|--+--+--|--+--+--+--+--|--");
        assertThat(scale.split("\\n")[1]).isEqualTo("0  1  2  3     5        8              13 ");
    }

    /**
     * Based on the {@param fibonacciSequence}, converts a {@link Estimation} entity to
     * a graphical representation, for instance:
     *
     * <pre>
     *        [X][X][X]
     * </pre>
     * <p>
     * Every unit is represented as a row of 3 characters.
     *
     * @param fibonacciSequence used only to know the boundary of the grap (min and max).
     * @param vote              the vote instance
     * @return the {@link String} of the vote.
     */
    String getVoteGraphLine(List<Integer> fibonacciSequence, Estimation vote) {
        StringBuilder voteLine = new StringBuilder();
        for (int i = Collections.min(fibonacciSequence); i <= Collections.max(fibonacciSequence); i++) {
            if (vote.min <= i && i <= vote.max) {
                if (i == vote.likely) {
                    voteLine.append("[X]");
                } else {
                    voteLine.append("[-]");
                }
            } else {
                voteLine.append("   ");
            }
        }
        return voteLine.toString();
    }

    @ParameterizedTest
    @CsvSource({
            "1,5,10,'[-][-][-][-][X][-][-][-][-][-]'",
            "2,6,9, '   [-][-][-][-][X][-][-][-]   '",
            "5,7,7, '            [-][-][X]         '",
            "7,7,7, '                  [X]         '",
            "9,3,2, '                              '",
    })
    void testGetVoteGraphLine(int minVote, int likely, int maxVote, String expectedString) {
        List<Integer> fibonacciSequence = Arrays.asList(1, 10);

        String output = getVoteGraphLine(fibonacciSequence, new Estimation(minVote, likely, maxVote));

        assertThat(output).isEqualTo(expectedString);
    }

}