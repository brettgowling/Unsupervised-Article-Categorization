import java.util.*;

public class Utils {

    static void analysis(HashMap<Integer, ArrayList<TextVector>> clusters) {

        for (int i = 1; i < clusters.size(); i++) {
            ArrayList<TextVector> articles = clusters.get(i);

            HashMap<String, Integer> map = new HashMap<>();

            for (TextVector a : articles) {
                //if(map.containsKey(a.getLabel()))
            }

        }
    }
    
}
