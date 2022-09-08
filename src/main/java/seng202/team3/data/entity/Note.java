package seng202.team3.data.entity;

/**
 * Representation of a past or current Journey
 *
 * @author Matthew Doonan
 * @version 1.0.0, Sep 08
 */

public class Note {

    /** the id of the note in the database*/
    int reviewId;

    /** the id of the charger its reviewing*/
    int chargerId;

    /** the rating of the charger*/
    int rating;

    /** the users comment that is public about the charger*/
    String publicText;

    /** the users private comment about the charger*/
    String privateText;

    public Note (int chargerid, int rating) {
        this.chargerId = chargerid;
        this.rating = rating;
    }

    /**
     * Sets the review id
     * @param id an integer of the id
     */
    public void setReviewId(int id) { reviewId = id; }

    /**
     * returns the reviewId
     * @return the integer of the reviewId
     */
    public int getReviewId() { return reviewId; }

    /**
     * Sets the chargerId
     * @param id an integer for the charger id
     */
    public void setChargerId(int id) { chargerId = id; }

    /**
     * returns the chargerId
     * @return the integer of the charger id
     */
    public int getChargerId() { return chargerId; }

    /**
     * Sets what the rating is
     * @param rate the users rating
     */
    public void setRating(int rate) { rating = rate; }

    /**
     * Returns the rating of the user
     * @return the users rating as an integer
     */
    public int getRating() { return rating; }

    /**
     * Sets the users public text
     * @param text the users comment
     */
    public void setPublicText(String text) { publicText = text; }

    /**
     * Returns the string of the publicText
     * @return comment as a string
     */
    public String getPublicText() { return publicText; }

    /**
     * Sets the privateText
     * @param text the users private text
     */
    public void setPrivateText(String text) { privateText = text; }

    /**
     * Returns the users privateText
     * @return A string of the comment
     */
    public String getPrivateText() { return privateText; }


}
