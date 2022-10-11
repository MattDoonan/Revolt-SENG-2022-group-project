package seng202.team3.logic;


/**
 * A class which has static methods to handle string formatting
 * @author James Billows
 * @version 1.0, Oct 22
 */
public class StringFormatter {  

    /**
     * Unused constructor
     */
    public StringFormatter() {
        //unused
    }

    /**
     * Takes and string and capitalizes the first
     * letter of every word
     * @param str input string
     * @return formatted string
     */
    public static String capitalizeWord(String str) {
        String[] words = str.split("\s");  
        String capitalizeWord = "";  
        for (String w : words) {  
            String first = w.substring(0, 1);  
            String afterfirst = w.substring(1);  
            capitalizeWord += first.toUpperCase() + afterfirst.toLowerCase() + " ";  
        }  
        return capitalizeWord.trim();  
    }

}  