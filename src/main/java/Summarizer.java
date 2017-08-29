import java.io.*;
import java.util.*;
import java.util.regex.*;

/**
 * Created by jonathankeys on 3/18/17. Class Summarizer is used as the start point for the project and create all
 * necessary objects and method calls on those objects to compete the goal.
 */
public class Summarizer implements Serializable {

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String stop_filepath = "project_AI/src/stopWords.txt";
        String currentFile = "project_AI/src/article.txt";
        System.out.println("Enter a filepath: ");
        Scanner scan = new Scanner(System.in);
        String s = scan.next();

        //System.out.println();

        System.out.println("Enter: 1 for TF, 2 for IDF, 3 for Stanford parser");
        System.out.println();
        Scanner scan1 = new Scanner(System.in);
        int i = scan.nextInt();

        if (i == 1) {
            termFrequency(stop_filepath, s);
        } else if (i == 2) {
            inverseDocumentFrequency(stop_filepath, s);
        } else if (i == 3) {
            stanfordParser(stop_filepath, s);
        } else {
            System.out.println("Error: Enter an int 1-3");
        }

    }


    /**
     * Print out all objects created and used in project.
     *
     * @param stopWords String list of all stop words.
     * @param articleSentences String list of all sentences in article.
     * @param articleWords String list of all words in article.
     * @param wordsNoStopWordsValue Json object of all words and their occurrence amount.
     * @param sentenceValue Json object of all sentences and their ranking amount.
     */
    private static void printAll(List<String> stopWords, List<String> articleSentences, List<String> articleWords,
          Hashtable<String, Double> wordsNoStopWordsValue, Hashtable<String, Double> sentenceValue) {

        System.out.println("--- JSON Word Object ---");
        printJson(wordsNoStopWordsValue);

        System.out.println("--- JSON Sentence Object ---");
        printJson(sentenceValue);

        System.out.println("\n\n--- Article Words ---");
        printList(articleWords);

        System.out.println("\n\n--- Article Sentences ---");
        printList(articleSentences);

        System.out.println("\n\n--- Stop Words ---");
        printList(stopWords);
    }

    /**
     * Print out all keys and respective value for Json object.
     *
     * @param sentenceValue Json object with key/value pairs to be printed.
     */
    private static void printJson(Hashtable<String, Double> sentenceValue) {
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

    private static void stanfordParser(String stop, String path) throws FileNotFoundException {

        List<String> stopWords = createStopWords(stop);
        List<String> articleSentences = createArticleSentences(path);

        Hashtable<String, Double> stanfordParser = new Hashtable<>();

        for (String item : articleSentences) {
            stanfordParser.put(item, 0.0);
        }

        Hashtable<String, Double> sentenceValue = Parser.getHashTable(stanfordParser, stopWords);

        PriorityQueue<SentenceRanking> sentenceRanked = rankSentences(sentenceValue);

        printLimitedSummary(sentenceRanked);

    }


    private static void termFrequency(String stop, String newArticle) throws FileNotFoundException {

        List<String> stopWords = createStopWords(stop);
        List<String> articleWords = createArticleWords(newArticle);
        List<String> articleSentences = createArticleSentences(newArticle);

        Hashtable<String, Integer> wordsValue = findWordOccurrences(articleWords);
        Hashtable<String, Double> wordsNoStopWordsValue = findWordOccurrences(articleWords, stopWords, 0);
        Hashtable<String, Double> sentenceValue = findWordInSentenceOccurrences(articleSentences,
              wordsNoStopWordsValue);

        PriorityQueue<SentenceRanking> sentenceRanked = rankSentences(sentenceValue);
        while (!sentenceRanked.isEmpty()) {
            SentenceRanking test = sentenceRanked.remove();
            System.out.println(test.getRank() + ": " + test.getSentence());
        }

        //	    printAll(stopWords, articleSentences, articleWords, wordsNoStopWordsValue, sentenceValue);

    }

    private static Hashtable<String, Double> combineHashes(Hashtable<String, Double> combinedArticle,
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


    private static void inverseDocumentFrequency(String stop, String newArticle)
          throws IOException, ClassNotFoundException {

        List<String> stopWords = createStopWords(stop);
        List<String> currentArticleWords = createArticleWords(newArticle);
        List<String> currentArticleSentences = createArticleSentences(newArticle);

        Hashtable<String, Double> currentArticleStopWords = findWordOccurrences(currentArticleWords, stopWords, 0);

        // Uncomment and comment rest of file to clear serial file and add new article
        //    ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("src/learner.ser"));
        //    outputStream.writeObject(new Article(currentArticleStopWords));
        //    outputStream.close();

        ObjectInputStream inputStream = new ObjectInputStream(new FileInputStream("project_AI/src/learner.ser"));
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

        ObjectOutputStream outputStream = new ObjectOutputStream(new FileOutputStream("project_AI/src/learner.ser"));
        outputStream.writeObject(allWordsArticle);
        outputStream.close();

        Hashtable<String, Double> finalAllWords = findWordOccurrences(currentArticleWords, stopWords, allWords);

        Hashtable<String, Double> sentenceValue = findWordInSentenceOccurrences(currentArticleSentences, finalAllWords);
        PriorityQueue<SentenceRanking> sentenceRanked = rankSentences(sentenceValue);
        printLimitedSummary(sentenceRanked);

    }

    /**
     * Print out all items in SentenceRanking object and remove object after use.
     *
     * @param object PriorityQueue of SentenceRanking object.
     */
    private static void printSentenceRanking(PriorityQueue<SentenceRanking> object) {
        while (!object.isEmpty()) {
            SentenceRanking item = object.peek();
            System.out.println(item.getSentence());
            //System.out.println(item.getRank());
            object.remove();
        }
    }

    private static void printLimitedSummary(PriorityQueue<SentenceRanking> object) {

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
     * Create List of strings of stop words noted in file.
     *
     * @return stack of strings containing each stop word.
     */
    private static Stack<String> createStopWords(String path) throws FileNotFoundException {
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
    private static Stack<String> createArticleSentences(String path) throws FileNotFoundException {
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
    private static Stack<String> createArticleWords(String path) throws FileNotFoundException {
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
    private static Hashtable<String, Integer> findWordOccurrences(List<String> articleWords) {
        Hashtable<String, Integer> builder = new Hashtable<>();

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
    private static Hashtable<String, Double> findWordOccurrences(List<String> articleWords, List<String> stopWords,
          int inverse) {
        Hashtable<String, Double> builder = new Hashtable<>();

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

    private static Hashtable<String, Double> findWordOccurrences(List<String> articleWords, List<String> stopWords,
          Hashtable<String, Double> allWords) {

        Hashtable<String, Double> builder = new Hashtable<>();

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
    private static Hashtable<String, Double> findWordInSentenceOccurrences(List<String> articleSentences,
          Hashtable<String, Double> mergedObject) {

        Hashtable<String, Double> builder = new Hashtable<>();
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
                    //        	System.out.println(value);
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
    private static PriorityQueue<SentenceRanking> rankSentences(Hashtable<String, Double> sentenceValue) {
        Comparator<SentenceRanking> comparator = new QueueComparator();
        PriorityQueue<SentenceRanking> queue = new PriorityQueue<>(comparator);
        Set<String> keySet = sentenceValue.keySet();
        for (String key : keySet) {
            //queue.add(new SentenceRanking(key, Integer.parseInt(sentenceValue.get(key).toString())));
            // queue.add(new SentenceRanking(key, Double.parseDoubrankSentencesle(sentenceValue.get(key).toString())));
            queue.add(new SentenceRanking(key, sentenceValue.get(key)));
        }
        return queue;
    }
}