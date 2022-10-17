package seng202.team3.logic.util;

/**
 * A class which has static methods to handle string formatting
 * 
 * @author James Billows
 * @version 1.0, Oct 22
 */
public class StringFormatter {

    /**
     * Unused constructor
     */
    private StringFormatter() {
        // unused
    }

    /**
     * Takes a string and converts it to title case
     * 
     * @param str input string
     * @return formatted string
     */
    public static String toTitleCase(String str) {
        String[] words = str.split("\s");
        StringBuilder capitalizeWord = new StringBuilder();
        for (String w : words) {
            String first = w.substring(0, 1);
            String afterfirst = w.substring(1);
            capitalizeWord.append(first.toUpperCase());
            capitalizeWord.append(afterfirst.toLowerCase());
            capitalizeWord.append(" ");
        }
        return capitalizeWord.toString().trim();
    }

}