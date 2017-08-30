package summarizers.util;

import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.process.Tokenizer;
import edu.stanford.nlp.process.TokenizerFactory;
import edu.stanford.nlp.trees.Tree;

import java.io.StringReader;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;


/**
 * Created by jonathankeys on 3/20/17. Class to be implemented by priority queue to rank sentences in their correct
 * order.
 */

public class Parser {

    private final String pcgModel = "edu/stanford/nlp/models/lexparser/englishPCFG.ser.gz";

    private final TokenizerFactory<CoreLabel> tokenizerFactory = PTBTokenizer
          .factory(new CoreLabelTokenFactory(), "invertible=true");

    private final LexicalizedParser parser = LexicalizedParser.loadModel(pcgModel);

    public Tree parse(final String str) {
        List<CoreLabel> tokens = tokenize(str);
        Tree tree = parser.apply(tokens);
        return tree;
    }

    private List<CoreLabel> tokenize(final String str) {
        Tokenizer<CoreLabel> tokenizer = tokenizerFactory.getTokenizer(new StringReader(str));
        return tokenizer.tokenize();
    }

    public static Hashtable<String, Double> getHashTable(final Hashtable<String, Double> hash,
          final List<String> stopWords) {

        Set<String> keySet = hash.keySet();
        Hashtable<String, Double> builder = new Hashtable<>();
        Parser parser = new Parser();

        int numSentence = 0;

        for (String sentence : keySet) {

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

    public static void main(String[] args) {
        String str = "My dog also likes eating sausage.";
        Parser parser = new Parser();
        Tree tree = parser.parse(str);

        List<Tree> leaves = tree.getLeaves();

        // Print words and Pos Tags
        for (Tree leaf : leaves) {
            Tree parent = leaf.parent(tree);
            System.out.print(leaf.label().value() + "-" + parent.label().value() + " ");
        }

        System.out.println();
    }
}
