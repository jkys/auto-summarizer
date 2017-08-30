package summarizers.util;

import java.util.Comparator;

/**
 * Created by jonathankeys on 4/5/17.
 *
 * Used to order PriorityQueue from highest value to smallest.
 */
public class QueueComparator implements Comparator<SentenceRanking> {

    @Override
    public int compare(SentenceRanking object1, SentenceRanking object2) {
        double x = object2.getRank() - object1.getRank();
        return x > 0 ? 1 : -1;
    }
}
