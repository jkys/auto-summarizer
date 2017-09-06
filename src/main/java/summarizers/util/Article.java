package summarizers.util;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by jonathankeys on 4/6/17.
 *
 * Article is a serializable HashMap<String, Double> to be able to persist the inverse document frquency method.
 */
public class Article implements Serializable {

    private HashMap<String, Double> article;

    public HashMap<String, Double> getArticle() {
        return this.article;
    }

    public Article(HashMap<String, Double> article) {
        this.article = article;
    }
}
