import java.util.*;

public class Utils {

    static void analysis(HashMap<Integer, ArrayList<ArticleVector>> clusters) {

        for (int i = 1; i <= clusters.size(); i++) {
            ArrayList<ArticleVector> articles = clusters.get(i);

            HashMap<String, Integer> map = new HashMap<>();

            for (ArticleVector a : articles) {
                if (map.containsKey(a.getLabel())) {
                    map.put(a.getLabel(), map.get(a.getLabel()) + 1);
                } else {
                    map.put(a.getLabel(), 1);
                }
                //if(map.containsKey(a.getLabel()))
            }

            System.out.println("Cluster: "+i+ " " + map);

        }
    }
    
}
