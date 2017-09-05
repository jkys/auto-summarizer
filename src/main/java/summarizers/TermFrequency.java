package summarizers;

import java.util.HashMap;
import java.util.PriorityQueue;
import summarizers.util.SentenceRanking;

/**
 * Created by jonathankeys on 8/29/17.
 *
 * Term Frequency counts the usage of certain words outside of "stop" words to be able to rank sentences based on the
 * word occurrences within the article which are unique.
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
