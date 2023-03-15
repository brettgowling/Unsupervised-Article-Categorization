import java.security.KeyStore.Entry;
import java.util.*;

import javax.swing.text.html.parser.Entity;
public class Main {

    public static DocumentCollection articles;



    public static void main(String[] args) {

        System.out.println("Starting..");
        // currently testing with demo data
        articles = new DocumentCollection("learn-ai-bbc/testing.csv", "articles");
        //articles = new DocumentCollection("learn-ai-bbc/BBC News Train.csv", "articles");

        articles.normalize(articles);

        // HashMap<Integer, ArrayList<Integer>> articleToCluster = new HashMap<>();
        // HashMap<Integer, TextVector> myClusters = new HashMap<>();

        // // Get the 5 starting id numbers
        // ArrayList<Integer> random_integers = new ArrayList<>();
        // while (random_integers.size() < 5) {
        //     int random_int = (int) Math.round(Math.random() * articles.getSize()) + 1;
        //     if (!random_integers.contains(random_int) && (articles.getDocumentById(random_int) != null)) {
        //         random_integers.add(random_int);
        //     }
        // }
        // for (int i = 0; i < random_integers.size(); i++) {
        //     System.out.println("Rand num " + i + ": " + random_integers.get(i));
        //     myClusters.put(i + 1, articles.getDocumentById(random_integers.get(i)));
        // }

        // // This is just for testing. These articles are all business articles
        // // 1833, 154, 1101, 917, 2034
        // //        myClusters.put(1, articles.getDocumentById(1833));
        // //        myClusters.put(2, articles.getDocumentById(154));
        // //        myClusters.put(3, articles.getDocumentById(1101));
        // //        myClusters.put(4, articles.getDocumentById(917));
        // //        myClusters.put(5, articles.getDocumentById(2034));

        // CosineDistance cosine = new CosineDistance();

        // for (ArticleVector myArticle : articles.getDocuments()) {
        //     double closest_distance = 0;
        //     int closest_cluster = -1;
        //     double curr_distance = 0.0;
        //     System.out.println("For Article " + myArticle.article_id + ":");
        //     for (int cluster_num = 1; cluster_num <= random_integers.size(); cluster_num++) {
        //         curr_distance = cosine.findDistance(myArticle, myClusters.get(cluster_num), articles);
        //         System.out.println("Distance between Cluster #" + cluster_num + ": " + curr_distance);
        //         if (closest_distance == 0) {
        //             closest_distance = curr_distance;
        //             closest_cluster = cluster_num;
        //         } else {
        //             if (curr_distance > closest_distance) {
        //                 closest_distance = curr_distance;
        //                 closest_cluster = cluster_num;
        //             }
        //         }
        //     }
        //     if (articleToCluster.get(closest_cluster) != null) {
        //         articleToCluster.get(closest_cluster).add(myArticle.article_id);
        //     } else {
        //         ArrayList<Integer> myArrayList = new ArrayList<>();
        //         myArrayList.add(myArticle.article_id);
        //         articleToCluster.put(closest_cluster, myArrayList);
        //     }
        //     System.out.println("Final closest cluster: " + closest_cluster);
        // }

        // System.out.println("\n\n####################\n\n");
        // for (int x = 0; x < myClusters.size(); x++) {
        //     System.out.println("Cluster #" + (x + 1) + ": size = " + articleToCluster.get(x + 1).size());
        // }

        // System.out.println();

        // // This is just for my sanity in order to see how many articles we expect in each cluster
        // System.out.println("Expected cluster sizes:");
        // for (String string : articles.category_counts.keySet()) {
        //     System.out.println(string + ": " + articles.category_counts.get(string));
        // }

        // uncomment to make it work
        KMeanClusteringModel model = new KMeanClusteringModel(articles, 5);
        model.fitCentroids();

        System.out.println(model.getWeightedPrecision(articles));

        System.out.println("Ending...");
    }


}
