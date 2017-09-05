package summarizers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import summarizers.util.Parser;
import summarizers.util.SentenceRanking;

/**
 * Created by jonathankeys on 8/29/17.
 *
 * Stanford Parser uses the parser class to be able to parse through the data of the corpse using the Stanford NLP
 * library to pick out parts of speech and ranking sentences based on that.
 */
public class StanfordParser extends Base {

    public void main() {
        List<String> stanfordParser = new ArrayList<>();

        stanfordParser.addAll(articleSentences);

        HashMap<String, Double> sentenceValue = Parser.getHashMap(stanfordParser, stopWords);
        PriorityQueue<SentenceRanking> sentenceRanked = rankSentences(sentenceValue);
        printLimitedSummary(sentenceRanked);
    }

}
