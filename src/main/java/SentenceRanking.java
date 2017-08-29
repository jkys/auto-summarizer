/**
 * Created by jonathankeys on 3/20/17. Class to be implemented by priority queue to rank sentences in their correct
 * order.
 */
public class SentenceRanking implements Comparable<SentenceRanking> {

    private String sentence;
    private double rank;

    /**
     * Class constructor to initialise variables.
     *
     * @param sentence String value which will be the Json key.
     * @param rank int value which will be the Json key value.
     */
    public SentenceRanking(String sentence, double rank) {
        this.sentence = sentence;
        this.rank = rank;
    }

    /**
     * Getter method for sentence key.
     *
     * @return class variable string sentence.
     */
    public String getSentence() {
        return sentence;
    }

    /**
     * Getter method for sentence value.
     *
     * @return class variable int rank.
     */
    public double getRank() {
        return rank;
    }

    /**
     * Setter method for updating sentence value. Should not be used.
     *
     * @param sentence class variable String sentence.
     */
    public void setSentence(String sentence) {
        this.sentence = sentence;
    }

    /**
     * Setter method for updating sentence rank.
     *
     * @param rank class variable int rank.
     */
    public void setRank(double rank) {
        this.rank = rank;
    }

    /**
     * Compate value from sentenceRanking to the current one.
     *
     * @param object sentenceRanking object.
     * @return return comparison.
     */
    @Override
    public int compareTo(SentenceRanking object) {
        return this.getSentence().compareTo(object.getSentence());
    }
}