package summarizers;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.PriorityQueue;
import summarizers.util.SentenceRanking;

/**
 * Created by jonathankeys on 8/29/17.
 */
public class TermFrequency extends Base {

    public void main() {
        HashMap<String, Integer> wordsValue = findWordOccurrences(articleWords);
        HashMap<String, Double> wordsNoStopWordsValue = findWordOccurrences(articleWords, stopWords, 0);
        HashMap<String, Double> sentenceValue = findWordInSentenceOccurrences(articleSentences,
              wordsNoStopWordsValue);

        PriorityQueue<SentenceRanking> sentenceRanked = rankSentences(sentenceValue);
        while (!sentenceRanked.isEmpty()) {
            SentenceRanking test = sentenceRanked.remove();
            System.out.println(test.getRank() + ": " + test.getSentence());
        }

        printAll(stopWords, articleSentences, articleWords, wordsNoStopWordsValue, sentenceValue);
    }

}
