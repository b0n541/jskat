package org.jskat.ai.mjl;

import org.jskat.util.Card;
import org.jskat.util.CardList;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Vector;

/**
 * Some tools that are used in the whole application and by the skat players
 */
public class Tools {

    /**
     * Converts an int array to a String
     *
     * @param values int array
     * @return string
     */
    public static String dump(int[] values) {
        StringBuffer sb = new StringBuffer("{");
        for (int i = 0; i < values.length; i++) {
            sb.append(values[i]).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.append("}").toString();
    }

    /**
     * Converts a HashSet array of Cards to a String
     *
     * @param values int array
     * @return string
     */
    public static String dumpCards(HashSet<Card>[] values) {
        StringBuffer sb = new StringBuffer("{");
        for (int i = 0; i < values.length; i++) {
            Iterator<Card> iter = values[i].iterator();
            sb.append("[");
            while (iter.hasNext()) {
                sb.append(iter.next().toString()).append(",");
            }
            sb.deleteCharAt(sb.length() - 1);
            sb.append("],");
        }
        sb.deleteCharAt(sb.length() - 1);
        return sb.append("}").toString();
    }

    /**
     * Converts a Vector of CardVectors to a String
     *
     * @param values int array
     * @return string
     */
    public static String dumpCards(Vector<CardList> values) {

        StringBuffer sb = new StringBuffer("\n{");

        for (int i = 0; i < values.size(); i++) {

            Iterator<Card> iter = values.get(i).iterator();
            sb.append("\n[");

            while (iter.hasNext()) {
                sb.append(iter.next().toString()).append(",");
            }

            sb.deleteCharAt(sb.length() - 1);
            sb.append("]");
        }

        return sb.append("\n}").toString();
    }

    /**
     * Checks whether a number is in a given set of numbers
     *
     * @param toCheck Number that should be checked
     * @param list    List of numbers, usually defined by new int[] {a, b, c, ...}
     * @return TRUE if the number exists in the list
     */
    public static boolean isIn(int toCheck, int[] list) {
        for (int i = 0; i < list.length; i++) {
            if (toCheck == list[i])
                return true;
        }
        return false;
    }
}
