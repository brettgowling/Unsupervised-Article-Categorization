import java.util.*;
import java.util.Map.Entry;

public class KMeanClusteringModel {
    DocumentCollection articles;
    int k = 0;

    public KMeanClusteringModel(DocumentCollection articles, int k) {
        this.articles = articles;
        this.k = k;
    }
    
    public void fitCentroids() {
        HashMap<Integer, ArrayList<Integer>> clusterToArticles = new HashMap<>();
        HashMap<Integer, TextVector> centroids = this.getRandomCentroids();
        CosineDistance cosine = new CosineDistance();

        for (ArticleVector myArticle : articles.getDocuments()) {
            double closest_distance = 0;
            int closest_cluster = -1;
            double curr_distance = 0.0;
            System.out.println("For Article " + myArticle.article_id + ":");
            for (int cluster_num = 1; cluster_num <= centroids.size(); cluster_num++) {
                curr_distance = cosine.findDistance(myArticle, centroids.get(cluster_num), articles);
                System.out.println("Distance between Cluster #" + cluster_num + ": " + curr_distance);
                if (closest_distance == 0) {
                    closest_distance = curr_distance;
                    closest_cluster = cluster_num;
                } else {
                    if (curr_distance > closest_distance) {
                        closest_distance = curr_distance;
                        closest_cluster = cluster_num;
                    }
                }
            }
            if (clusterToArticles.containsKey(closest_cluster)) {
                clusterToArticles.get(closest_cluster).add(myArticle.article_id);
            } else {
                ArrayList<Integer> myArrayList = new ArrayList<>();
                myArrayList.add(myArticle.article_id);
                clusterToArticles.put(closest_cluster, myArrayList);
            }
            System.out.println("Final closest cluster: " + closest_cluster);
        }

        System.out.println("\n\n####################\n\n");
        for (int x = 0; x < centroids.size(); x++) {
            System.out.println("Cluster #" + (x + 1) + ": size = " + clusterToArticles.get(x + 1).size());
        }

    }
    

    // private HashMap<Integer, TextVector> getUpdatedCentroids(HashMap<Integer, ArrayList<Integer>> clusterToArticles) {
    //     for (int i = 1; i <= this.k; i++) {
    //         ArrayList<Integer> articleIds = clusterToArticles.get(i);

    //     }
    //     return null;
    // }
    
    private boolean areCentroidsSame(HashMap<Integer, TextVector> centroid1, HashMap<Integer, TextVector> centroid2) {
        for (int i = 1; i <= this.k; i++) {

            HashSet<String> set = new HashSet<>();
            for (Entry<String, Double> entry : centroid1.get(i).getNormalizedVectorEntrySet()) {
                String word = entry.getKey();
                set.add(word);
            }

            for (Entry<String, Double> entry : centroid2.get(i).getNormalizedVectorEntrySet()) {
                String word = entry.getKey();
                set.add(word);
            }

            for (String word : set) {
                double valueForC1 = centroid1.get(i).getNormalizedFrequency(word);

                double valueForC2 = centroid1.get(i).getNormalizedFrequency(word);
                if (Double.compare(valueForC1, valueForC2) != 0) 
                    return false;
                
            }
        }
        return true;
    }
    
    private HashMap<Integer, TextVector> getRandomCentroids() {
        HashMap<Integer, TextVector> centroids = new HashMap<>();
        // Get the 'k' starting id numbers
        ArrayList<Integer> random_integers = new ArrayList<>();
        while (random_integers.size() < this.k) {
            int random_int = (int) Math.round(Math.random() * articles.getSize()) + 1;
            if (!random_integers.contains(random_int) && (articles.getDocumentById(random_int) != null)) {
                random_integers.add(random_int);
            }
        }
        for (int i = 0; i < random_integers.size(); i++) {
            System.out.println("Rand num " + i + ": " + random_integers.get(i));
            centroids.put(i + 1, articles.getDocumentById(random_integers.get(i)));
        }
        return centroids;
    }
}
