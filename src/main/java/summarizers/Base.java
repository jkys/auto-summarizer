package summarizers;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import summarizers.util.QueueComparator;
import summarizers.util.SentenceRanking;

/**
 * Created by jonathankeys on 8/29/17.
 *
 * Base class contains all the methods needed to retrieve and process data on
 * the article/corpus which the data is from.
 */
class Base {

    private final String stopWordsPath = "text/stopWords.txt";
    private final String articlePath = "text/article.txt";
    final String learnerPath = "serialized/learner.ser";

    Stack<String> stopWords;
    Stack<String> articleSentences;
    Stack<String> articleWords;

    // To initialize all the articles, sentences, and words
    {
        try {
            stopWords = createStopWords(stopWordsPath);
            articleSentences = createArticleSentences(articlePath);
            articleWords = createArticleWords(articlePath);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    /**
     * Create List of strings of stop words noted in file.
     *
     * @return stack of strings containing each stop word.
     */
    private Stack<String> createStopWords(String path) throws FileNotFoundException {
        Stack<String> stopWords = new Stack<>();
        Scanner stopWordList = new Scanner(new FileReader(path));
        while (stopWordList.hasNextLine()) {
            String stopWord = stopWordList.nextLine();
            stopWords.push(stopWord);
        }
        return stopWords;
    }

    /**
     * Create list of sentences in the article.
     *
     * @return stack of strings containing each sentence of the article.
     */
    private Stack<String> createArticleSentences(String path) throws FileNotFoundException {
        Stack<String> articleSentences = new Stack<>();
        Pattern sentencePattern = Pattern
              .compile("[^.!?\\s][^.!?]*(?:[.!?](?!['\"]?\\s|$)[^.!?]*)*[" + ".!?]?['\"]?(?=\\s|$)",
                    Pattern.MULTILINE | Pattern.COMMENTS);

        Scanner article = new Scanner(new FileReader(path));

        while (article.hasNextLine()) {
            String line = article.nextLine();
            Matcher matcher = sentencePattern.matcher(line);
            while (matcher.find()) {
                String sentence = matcher.group();
                articleSentences.push(sentence);
            }
        }
        return articleSentences;
    }

    /**
     * Create list of words in the article.
     *
     * @return stack of strings containing each individual word (without punctuation) of the article.
     */
    private Stack<String> createArticleWords(String path) throws FileNotFoundException {
        Stack<String> articleWords = new Stack<>();

        Scanner article = new Scanner(new FileReader(path));

        while (article.hasNext()) {
            String word = article.next().replaceAll("[^a-zA-Z ]", "").toLowerCase();
            if (!word.equals("")) {
                articleWords.push(word);
            }
        }
        return articleWords;
    }

    /**
     * Create Json object of all words in the article and the amount of occurrences they have.
     *
     * @param articleWords List of strings containing each word in the article.
     * @return Json object containing all words as keys with their values being the amount of occurrences of that word.
     */
    HashMap<String, Integer> findWordOccurrences(List<String> articleWords) {
        HashMap<String, Integer> builder = new HashMap<>();

        Set<String> articleWordsSet = new HashSet<>(articleWords);
        for (String word : articleWordsSet) {
            int occurrence = Collections.frequency(articleWords, word);
            builder.put(word, occurrence);
        }
        return builder;
    }

    /**
     * Create Json object of all words in the article and the amount of occurrences they have, ignoring all stop words
     * in each sentence and setting them to a value of 0.
     *
     * @param articleWords List of strings containing each word in the article.
     * @param stopWords List of strings containing each stop word.
     * @return Json object containing all words as keys with their values being the amount of occurrences of that word,
     * except if the key is a stop word - it will be set to 0.
     */
    HashMap<String, Double> findWordOccurrences(List<String> articleWords, List<String> stopWords, int inverse) {
        HashMap<String, Double> builder = new HashMap<>();

        double occurrence;

        Set<String> articleWordsSet = new HashSet<>(articleWords);
        for (String word : articleWordsSet) {
            if (inverse == 1) {

                occurrence = (1.0 / (Collections.frequency(articleWords, word))) * 1.0;

            } else {
                occurrence = Collections.frequency(articleWords, word);
            }

            for (String stopWord : stopWords) {
                if (word.equalsIgnoreCase(stopWord)) {
                    occurrence = 0;
                    break;
                }
            }
            builder.put(word, occurrence);
        }
        return builder;
    }

    HashMap<String, Double> findWordOccurrences(List<String> articleWords, List<String> stopWords,
          HashMap<String, Double> allWords) {

        HashMap<String, Double> builder = new HashMap<>();

        double occurrence;

        Set<String> articleWordsSet = new HashSet<>(articleWords);
        for (String word : articleWordsSet) {
            occurrence = (1.0 / allWords.get(word));
            for (String stopWord : stopWords) {
                if (word.equalsIgnoreCase(stopWord)) {
                    occurrence = 0;
                    break;
                }
            }
            builder.put(word, occurrence);
        }
        return builder;
    }

    /**
     * Create Json object of all sentences in the article and amount of occurrences of non-stop words, the higher value
     * the sentence, the more important it will be ranked.
     *
     * @param articleSentences List of strings containing each sentence of the article.
     * @param mergedObject Json object containing all words as keys with their values being the amount of occurrences of
     * that word.
     * @return Json object containing each sentence as keys with their values being the amount of occurrences of words
     * in the sentence.
     */
    HashMap<String, Double> findWordInSentenceOccurrences(List<String> articleSentences,
          HashMap<String, Double> mergedObject) {

        HashMap<String, Double> builder = new HashMap<>();
        Set<String> keySet = mergedObject.keySet();
        Set<String> articleSentencesSet = new HashSet<>(articleSentences);

        //  printJson(words);

        for (String sentence : articleSentencesSet) {
            Double occurrences = 0.0;
            for (String key : keySet) {

                if (sentence.contains(key)) {
                    // 	String value = words.getString(key);
                    // 	System.out.println(value);
                    // Double value = ((Double)builder.get(key)).doubleValue();
                    Double value = (Double) mergedObject.get(key);
                    occurrences += value;
                }
            }

            builder.put(sentence, (occurrences * 1.0));
        }
        return builder;
    }

    /**
     * Create an organized list of sentences sorted so that the first sentence is the most important and each sentence
     * thereafter being of less and less importance.
     *
     * @param sentenceValue Json object containing each sentence as keys with their values being the amount of
     * occurrences of words in the sentence.
     * @return list of strings sorted so that the more important the sentence, the earlier on in the list it will occur.
     */
    PriorityQueue<SentenceRanking> rankSentences(HashMap<String, Double> sentenceValue) {
        Comparator<SentenceRanking> comparator = new QueueComparator();
        PriorityQueue<SentenceRanking> queue = new PriorityQueue<>(comparator);
        Set<String> keySet = sentenceValue.keySet();
        for (String key : keySet) {
            //queue.add(new summarizers.util.SentenceRanking(key, Integer.parseInt(sentenceValue.get(key).toString())));
            // queue.add(new summarizers.util.SentenceRanking(key, Double.parseDoubrankSentencesle(sentenceValue.get(key).toString())));
            queue.add(new SentenceRanking(key, sentenceValue.get(key)));
        }
        return queue;
    }

    /**
     * Print out all items in summarizers.util.SentenceRanking object and remove object after use.
     *
     * @param object PriorityQueue of summarizers.util.SentenceRanking object.
     */
    protected static void printSentenceRanking(PriorityQueue<SentenceRanking> object) {
        while (!object.isEmpty()) {
            SentenceRanking item = object.peek();
            System.out.println(item.getSentence());
            object.remove();
        }
    }

    static void printLimitedSummary(PriorityQueue<SentenceRanking> object) {

        System.out.println("enter a whole number (1-100)");
        Scanner in = new Scanner(System.in);
        int n = in.nextInt();
        Double percent = n / 100.0 * 1.0;
        int numArticleSentences = object.size();
        double numDisplayed = percent * numArticleSentences;

        int nFinal = (int) Math.rint(numDisplayed);

        for (int i = 0; i < nFinal; i++) {
            SentenceRanking item = object.peek();
            System.out.println(item.getRank() + ": " + item.getSentence());
            object.remove();
        }

    }

    /**
     * Print out all keys and respective value for Json object.
     *
     * @param sentenceValue Json object with key/value pairs to be printed.
     */
    private static void printJson(HashMap<String, Double> sentenceValue) {
        Set<String> keySet = sentenceValue.keySet();
        for (String key : keySet) {
            System.out.println("Key: " + key + "\nValue: " + sentenceValue.get(key) + "\n");
        }
    }

    /**
     * Print out all items in String list.
     *
     * @param stopWords List of strings to be printed out.
     */
    private static void printList(List<String> stopWords) {
        stopWords.forEach(System.out::println);
    }

    protected static Hashtable<String, Double> combineHashes(Hashtable<String, Double> combinedArticle,
          Hashtable<String, Double> currentArticleWords) {

        Set<String> keySet = combinedArticle.keySet();
        Set<String> keySet1 = currentArticleWords.keySet();

        for (String key : keySet) {
            for (String key1 : keySet1) {
                if (key.equalsIgnoreCase(key1)) {
                    combinedArticle.replace(key, combinedArticle.get(key) + 1);
                } else {
                    combinedArticle.put(key, 1.0);
                }
            }
        }

        return combinedArticle;
    }


    /**
     * Print out all objects created and used in project.
     *  @param stopWords String list of all stop words.
     * @param articleSentences String list of all sentences in article.
     * @param articleWords String list of all words in article.
     * @param wordsNoStopWordsValue Json object of all words and their occurrence amount.
     * @param sentenceValue Json object of all sentences and their ranking amount.
     */
    static void printAll(List<String> stopWords, List<String> articleSentences, List<String> articleWords,
          HashMap<String, Double> wordsNoStopWordsValue, HashMap<String, Double> sentenceValue) {

        System.out.println("--- JSON Word Object ---");
        printJson(wordsNoStopWordsValue);

        System.out.println("--- JSON Sentence Object ---");
        printJson(sentenceValue);

        System.out.println("\n\n--- summarizers.util.Article Words ---");
        printList(articleWords);

        System.out.println("\n\n--- summarizers.util.Article Sentences ---");
        printList(articleSentences);

        System.out.println("\n\n--- Stop Words ---");
        printList(stopWords);
    }

}
