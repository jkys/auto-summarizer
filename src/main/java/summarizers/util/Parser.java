package summarizers.util;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;
import java.io.StringReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;


/**
 * Created by jonathankeys on 3/20/17.
 *
 * Parser uses the Stanford NLP library to take advantage of its advanced parts of speech recongonizer to be able to
 * rank sentences off of that.
 */

public class Parser {

    private final String pcgModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";

    private final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer
          .factory(new CoreLabelTokenFactory(), "invertible=true");

    private final LexicalizedParser parser = LexicalizedParser.loadModel(pcgModel);

    /**
     * Tokenize each sentence with it's correct parts of speech
     * @param sentence the sentence to tokenize
     * @return returns a tree of the words with their given parts of speech locator
     */
    private Tree parse(final String sentence) {
        List<CoreLabel> tokens = tokenizerFactory.getTokenizer(new StringReader(sentence)).tokenize();
        return parser.apply(tokens);
    }

    /**
     * Get's a HashMap of each sentence and it's value and return it to the user
     * @param sentences A HashMap of sentences and a starting value of 0
     * @param stopWords List of stop words to be omitted in value
     * @return return a HashMap containing all the sentences and their representative value given the Stanford NLP
     * parser
     */
    public static HashMap<String, Double> getHashMap(final List<String> sentences,
          final List<String> stopWords) {

        HashMap<String, Double> builder = new HashMap<>();
        Parser parser = new Parser();

        int numSentence = 0;

        for (String sentence : sentences) {

            String[] splited = sentence.split("\\b+"); //split on word boundries

            Double sentenceRank = 0.0;

            if (numSentence == 0) {
                sentenceRank += 100000.0;
                numSentence += 1;
            }

            for (String stopWord : stopWords) {
                if (Arrays.asList(splited).contains(stopWord)) {
                    sentenceRank -= 5.0;
                }
            }

            Tree tree = parser.parse(sentence);
            List<Tree> leaves = tree.getLeaves();

            // Print words and Pos Tags
            for (Tree leaf : leaves) {
                switch (leaf.parent(tree).label().value()) {
                    case "DT": // - Determiner
                    case "CC": // - Coordinating conjunction
                    case "EX": // - Existential there
                    case "FW": // - Foreign word
                    case "IN": // - Preposition or subordinating conjunction
                    case "TO": // - to
                        sentenceRank += 0.0;
                        break;
                    case "MD": // - Modal
                    case "PDT": // - Predeterminer
                    case "UH": // - Interjection
                        sentenceRank += 1.0;
                        break;
                    case "LS": // - List item marker
                    case "POS": // - Possessive ending
                    case "RP": // - Particle
                        sentenceRank += 2.0;
                        break;
                    case "JJ": // - Adjective
                    case "JJR": // - Adjective, comparative
                    case "JJS": // - Adjective, superlative
                    case "WDT": // - Wh-determiner
                    case "WP": // - Wh-pronoun
                    case "WP$": // - Possessive wh-pronoun
                    case "WRB": // - Wh-adverb
                        sentenceRank += 4.0;
                        break;
                    case "SYM": // - Symbol
                    case "RB": // - Adverb
                    case "RBR": // - Adverb, comparative
                    case "RBS": // - Adverb, superlative
                    case "CD": // - Cardinal number
                        sentenceRank += 5.0;
                        break;
                    case "VB": // - Verb, base form
                    case "VBD": // - Verb, past tense
                    case "VBG": // - Verb, gerund or present participle
                    case "VBN": // - Verb, past participle
                    case "VBP": // - Verb, non-3rd person singular present
                    case "VBZ": // - Verb, 3rd person singular present
                    case "NN": // - Noun, singular or mass
                    case "NNS": // - Noun, plural
                        sentenceRank += 6.0;
                        break;
                    case "NNP": // - Proper noun, singular
                    case "NNPS": // - Proper noun, plural
                        sentenceRank += 9.0;
                        break;
                    case "PRP$": // - Possessive pronoun
                        sentenceRank += 13.0;
                        break;
                    case "PRP": // - Personal pronoun
                        sentenceRank += 15.0;
                        break;
                    default:
                        // None
                        break;
                }
            }
            builder.put(sentence, (sentenceRank * 1.0));
        }
        return builder;
    }
}
