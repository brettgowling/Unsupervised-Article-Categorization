import javax.swing.text.Document;
import java.sql.Array;
import java.util.*;
import java.util.Map.Entry;

public class KMeanClusteringModel {
    DocumentCollection articles;
    int k = 0;
    HashMap<Integer, TextVector> centroids = new HashMap<>();
    HashMap<Integer, ArrayList<ArticleVector>> clusterToArticles = new HashMap<>();

    public KMeanClusteringModel(DocumentCollection articles, int k) {
        this.articles = articles;
        this.k = k;
        this.centroids = this.getRandomCentroids();
        this.clusterToArticles = new HashMap<>();
    }
    
    public void fitCentroids() {
        
        //HashMap<Integer, TextVector> centroids = this.getRandomCentroids();
//        HashMap<Integer, ArrayList<Integer>> clusterToArticles = new HashMap<>();
//        HashMap<Integer, TextVector> centroids = this.getRandomCentroids();
        HashMap<Integer, ArrayList<ArticleVector>> clusterToArticles = this.clusterToArticles;
        HashMap<Integer, TextVector> centroids = this.centroids;
        CosineDistance cosine = new CosineDistance();

        boolean isCentroidChanging = true;
        //HashMap<Integer, ArrayList<ArticleVector>> clusterToArticles = new HashMap<>();

        
        while (isCentroidChanging) {

            clusterToArticles.clear();

            for (ArticleVector myArticle : this.articles.getDocuments()) {
                double closest_distance = 0;
                int closest_cluster = -1;
                double curr_distance = 0.0;
                //System.out.println(i++);
                //System.out.println("For Article " + myArticle.article_id + ":");
                for (int cluster_num = 1; cluster_num <= this.k; cluster_num++) {
                    curr_distance = cosine.findDistance(myArticle, centroids.get(cluster_num), articles);
                    //System.out.println("Distance between Cluster #" + cluster_num + ": " + curr_distance);
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
                    clusterToArticles.get(closest_cluster).add(myArticle);
                } else {
                    ArrayList<ArticleVector> myArrayList = new ArrayList<>();
                    myArrayList.add(myArticle);
                    clusterToArticles.put(closest_cluster, myArrayList);
                }
                //System.out.println("Final closest cluster: " + closest_cluster);
            }

            System.out.println("\n\n####################\n\n");
            for (int x = 0; x < this.k; x++) {
                System.out.println("Cluster #" + (x + 1) + ": size = " + clusterToArticles.get(x + 1).size());
            }

            HashMap<Integer, TextVector> newCentroids = getUpdatedCentroids(clusterToArticles);

            System.out.println("Are both Same: " + areCentroidsSame(centroids, newCentroids));
            isCentroidChanging = !areCentroidsSame(centroids, newCentroids);

            centroids = (HashMap<Integer, TextVector>) newCentroids.clone();
        }

        Utils.analysis(clusterToArticles);

        
    }
    

    private HashMap<Integer, TextVector> getUpdatedCentroids(HashMap<Integer, ArrayList<ArticleVector>> clusterToArticles) {
        
        HashMap<Integer, TextVector> newCentroids = new HashMap<>();
        for (int i = 1; i <= this.k; i++) {
            ArrayList<ArticleVector> articles = clusterToArticles.get(i);
            HashSet<String> set = new HashSet<>();
            
            for (ArticleVector tv : articles) {
                for (Entry<String, Double> entry : tv.getNormalizedVectorEntrySet()) {
                    String word = entry.getKey();
                    set.add(word);
                }
            }

            ArticleVector newTextVector = new ArticleVector();

            for (String word : set) {
                double sum = 0.0;
                for (TextVector tv : articles) {
                    sum += tv.getNormalizedFrequency(word);
                }

                //if (sum>0.0)
                   // System.out.println("sum: "+(sum / (double) articles.size()));
                newTextVector.setNormalizedVector(word, sum / (double) articles.size());

            }
            
            newCentroids.put(i, newTextVector);
        }
        return newCentroids;
    }
    
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

                double valueForC2 = centroid2.get(i).getNormalizedFrequency(word);
                if (Double.compare(valueForC1, valueForC2) != 0) {
                    return false;
                }
                
            }
        }
        return true;
    }

    public double getWeightedPrecision(DocumentCollection articles){
        double total_weighted_precision = 0;
        int total_size = 0;
        for(ArrayList<ArticleVector> cluster:this.clusterToArticles.values()){
            total_size += cluster.size();
        }
        for(int cluster_num = 1; cluster_num <= this.k; cluster_num++){
            ArrayList<ArticleVector> cluster = this.clusterToArticles.get(cluster_num);
            int cluster_size = cluster.size();
            int TP = 0;
            int FP = 0;
            for(int i = 0; i < cluster.size(); i++){
                for(int j = i + 1; j < cluster.size(); j++){
                    if(Objects.equals(((ArticleVector) articles.getDocumentById(cluster.get(i).article_id)).getLabel(), ((ArticleVector) articles.getDocumentById(cluster.get(j).article_id)).getLabel())){
                        TP += 1;
                    }
                    else{
                        FP += 1;
                    }
                }
            }

            double weighted_cluster_precision = ((double)cluster_size / total_size) * ((double)TP / ((double)TP + (double)FP));
//            System.out.println("Weightet_cluster_precision for cluster #" + cluster_num + ": " + weighted_cluster_precision);
            total_weighted_precision += weighted_cluster_precision;
        }
        return total_weighted_precision;
    }

    public double getWeightedRecall(DocumentCollection articles){
        double total_weighted_recall = 0;
        int total_size = 0;
        for(ArrayList<ArticleVector> cluster:this.clusterToArticles.values()){
            total_size += cluster.size();
        }
        for(int cluster_num = 1; cluster_num <= this.k; cluster_num++){
            ArrayList<ArticleVector> cluster = this.clusterToArticles.get(cluster_num);
            int cluster_size = cluster.size();
            int TP = 0;
            int FP = 0;
            int FN = 0;
            for(int i = 0; i < cluster_size; i++){
                for(int j = i + 1; j < cluster_size; j++){
                    if(Objects.equals(((ArticleVector) articles.getDocumentById(cluster.get(i).article_id)).getLabel(), ((ArticleVector) articles.getDocumentById(cluster.get(j).article_id)).getLabel())){
                        TP += 1;
                    }
                    else{
                        FP += 1;
                    }
                }
                for(int second_cluster_num = 1; second_cluster_num <= this.k; second_cluster_num++){
                    if(second_cluster_num == cluster_num){
                        continue;
                    }
                    ArrayList<ArticleVector> second_cluster = this.clusterToArticles.get(second_cluster_num);
                    int second_cluster_size = second_cluster.size();
                    for(int j = 0; j < second_cluster_size; j++){
                        if(Objects.equals(((ArticleVector) articles.getDocumentById(cluster.get(i).article_id)).getLabel(), ((ArticleVector) articles.getDocumentById(second_cluster.get(j).article_id)).getLabel())) {
                            FN += 1;
                        }
                    }
                }
            }

            double weighted_cluster_recall = ((double)cluster_size / total_size) * ((double)TP / ((double)TP + (double)FN));
//            System.out.println("Weightet_cluster_recall for cluster #" + cluster_num + ": " + weighted_cluster_recall);
            total_weighted_recall += weighted_cluster_recall;
        }
        return total_weighted_recall;
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
            //System.out.println("Rand num " + i + ": " + random_integers.get(i));
            centroids.put(i + 1, articles.getDocumentById(random_integers.get(i)));
        }
        return centroids;
    }
}
