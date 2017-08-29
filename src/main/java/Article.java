import java.io.Serializable;
import java.util.Hashtable;

/**
 * Created by jonathankeys on 4/6/17.
 */
public class Article implements Serializable {

    Hashtable<String, Double> article;

    public Hashtable<String, Double> getArticle() {
        return this.article;
    }

    public void setArticle(Hashtable<String, Double> article) {
        this.article = article;
    }

    public Article(Hashtable<String, Double> article) {
        this.article = article;
    }

    public Article() {
    }

}
