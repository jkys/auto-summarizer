package summarizers;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.PriorityQueue;
import java.util.Set;
import summarizers.util.Article;
import summarizers.util.SentenceRanking;

/**
 * Created by jonathankeys on 8/29/17.
 */
public class InverseDocumentFrequency extends Base {

    public void main(String... args) throws IOException, ClassNotFoundException {
        Hashtable<String, Double> currentArticleStopWords = findWordOccurrences(articleWords, stopWords, 0);

        // Uncomment and comment rest of file to clear serial file and add new article
        //    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("src/learner.ser"));
        //    outputStream.writeObject(new summarizers.util.Article(currentArticleStopWords));
        //    outputStream.close();

        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream(learnerPath));
        Article allWordsArticle = (Article) inputStream.readObject();
        inputStream.close();

        Hashtable<String, Double> allWords = allWordsArticle.getArticle();

        Set<String> currentKeySet = currentArticleStopWords.keySet();
        Set<String> allKeySet = allWords.keySet();

        for (String currentKey : currentKeySet) {
            boolean found = false;
            for (String allKey : allKeySet) {
                if (currentKey.equalsIgnoreCase(allKey)) {
                    allWords.replace(allKey, allWords.get(allKey) + currentArticleStopWords.get(currentKey));
                    found = true;
                }
            }
            if (!found) {
                allWords.put(currentKey, currentArticleStopWords.get(currentKey));
            }
        }

        allWordsArticle = new Article(allWords);

        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream(learnerPath));
        outputStream.writeObject(allWordsArticle);
        outputStream.close();

        Hashtable<String, Double> finalAllWords = findWordOccurrences(articleWords, stopWords, allWords);

        Hashtable<String, Double> sentenceValue = findWordInSentenceOccurrences(articleSentences, finalAllWords);
        PriorityQueue<SentenceRanking> sentenceRanked = rankSentences(sentenceValue);
        //printLimitedSummary(sentenceRanked);
    }
}
