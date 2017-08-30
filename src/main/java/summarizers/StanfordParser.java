package summarizers;

import java.util.Hashtable;
import java.util.PriorityQueue;
import summarizers.util.Parser;
import summarizers.util.SentenceRanking;

/**
 * Created by jonathankeys on 8/29/17.
 */
public class StanfordParser extends Base {

    public void main() {
        Hashtable<String, Double> stanfordParser = new Hashtable<>();

        for (String item : articleSentences) {
            stanfordParser.put(item, 0.0);
        }

        Hashtable<String, Double> sentenceValue = Parser.getHashTable(stanfordParser, stopWords);
        PriorityQueue<SentenceRanking> sentenceRanked = rankSentences(sentenceValue);
        printLimitedSummary(sentenceRanked);
    }

}
