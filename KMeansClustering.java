import java.util.*;
import java.util.Map.Entry;

/*
 * BBC Dataset ~2,000 entries
 * https://www.kaggle.com/c/learn-ai-bbc
 */
public class KMeansClustering {

    private HashMap<Integer, ArticleVector> articles = new HashMap<>();
    private HashMap<String, Double> bagOfWords = new HashMap<>();
    private HashMap<Integer, HashMap<String, Double>> clusters = new HashMap<>();
    // {1: Map, 2: Map, 3: Map, 4: Map, 5: Map}
    int k = 0;

    public KMeansClustering(HashMap<Integer, ArticleVector> articles, int k) {
        System.out.println("Calling KMeansClustering Constructor");

        this.init(articles, k);
        this.fit();

    }

    public HashMap<String, Double> getBagOfWords() {
        return this.bagOfWords;
    }

    // initialising
    private void init(HashMap<Integer, ArticleVector> articles, int k) {

        this.k = k;
        // filter the articles [len should be less than 750]
        HashMap<Integer, ArticleVector> filteredArticles = new HashMap<>();
        HashMap<Integer, Double> clusters = new HashMap<>();
        HashSet<String> bagOfWordsSet = new HashSet<>();

        int articleNumber = 1;
        for (Map.Entry<Integer, ArticleVector> articleEntry : articles.entrySet()) {
            // System.out.println(articleEntry.getValue().getLabel());
            //System.out.println(entry.getValue().getNormalizedVectorEntrySet());
            ArticleVector article = articleEntry.getValue();
            if (article.getDistinctWordCount() <= 750) {
                filteredArticles.put(articleNumber, article);

                for (Map.Entry<String, Double> wordEntry : article.getNormalizedVectorEntrySet()) {
                    String word = wordEntry.getKey();
                    bagOfWordsSet.add(word);
                }
                articleNumber++;
            }

        }

        for (String word : bagOfWordsSet) {
            this.bagOfWords.put(word, 0.0);
        }

        HashMap<Integer, ArticleVector> bagOfWordArticles = new HashMap<>();

        for (Entry<Integer, ArticleVector> articleEntry : filteredArticles.entrySet()) {
            int key = articleEntry.getKey();
            ArticleVector article = articleEntry.getValue();

            for (String bow : this.bagOfWords.keySet()) {
                if (!article.contains(bow)) {
                    article.setNormalizedVector(bow, 0.0);
                }
            }

            bagOfWordArticles.put(key, article);

        }

        this.articles = bagOfWordArticles;

    }


    public void printClusterSize(HashMap<Integer, HashSet<Integer>> closestClusterMap){
        int sum =0;

        for (Entry<Integer, HashSet<Integer>> set : closestClusterMap.entrySet()) {
            sum += set.getValue().size();
        }
        System.out.println("Cluster Size: " + sum);
    }

    public void fit() {
        HashMap<Integer, HashMap<String, Double>> clusters = this.generateKRandomClusters();

        // HashMap<Integer, HashMap<String, Double>> clusters = this.generateKRandomClusters();

        // It will store for each cluster what the the articles (article num) are closest to them

        //printClusterSize(closestClusterMap);

        boolean changeInClusters = true;

        int turn = 0;
        //if (true) {
        while (turn++<1) {
        //while (changeInClusters) {

            HashMap<Integer, HashSet<Integer>> closestClusterMap = new HashMap<Integer, HashSet<Integer>>();
            for (int cluster = 1; cluster <= this.k; cluster++) {
                closestClusterMap.put(cluster, new HashSet<>());
            }

            for (Map.Entry<Integer, ArticleVector> articleEntry : articles.entrySet()) {
                int articleID = articleEntry.getKey();
                ArticleVector article = articleEntry.getValue();
                HashMap<String, Double> articleVertices = article.getNormaizedVector();
                int closestClusterNumber = 1;
                double closestClusterValue = Double.MAX_VALUE;

                ArrayList<Double> distances = new ArrayList<>();

                for (Entry<Integer, HashMap<String, Double>> clusterEntry : clusters.entrySet()) {
                    int clusterNumber = clusterEntry.getKey();
                    HashMap<String, Double> clusterVertices = clusterEntry.getValue();

                    double distance = this.calcuEuclidianDistance(clusterVertices, articleVertices);
                    distances.add(distance);
                    //System.out.println(distance);

                    if (distance < closestClusterValue) {
                        closestClusterValue = distance;
                        closestClusterNumber = clusterNumber;
                    }
                }

                //System.out.println();

                System.out.println(articleID+"->"+closestClusterNumber);
                System.out.println(distances);

                HashSet<Integer> set = closestClusterMap.get(closestClusterNumber);
                set.add(articleID);

                closestClusterMap.put(closestClusterNumber, set);

            }

            // print the count of articles
            System.out.println("\n");
            printClusterSize(closestClusterMap);
            for (Entry<Integer, HashSet<Integer>> set : closestClusterMap.entrySet()) {
                System.out.println("For Cluster: " + set.getKey());
                System.out.println(set.getValue().size());
            }

            HashMap<Integer, HashMap<String, Double>> newClusters = (HashMap<Integer, HashMap<String, Double>>) this.generateUpdatedClusters(clusters,
                    closestClusterMap).clone();

            changeInClusters = !isBothClustersSame(clusters, newClusters);
            clusters = (HashMap<Integer, HashMap<String, Double>>) newClusters.clone();
        }


    }
    
    boolean isBothClustersSame(HashMap<Integer, HashMap<String, Double>> prevCluster,
            HashMap<Integer, HashMap<String, Double>> newCluster) {
        
        for (Entry<Integer, HashMap<String, Double>> set : prevCluster.entrySet()) {
            int clusterNumber = set.getKey();
            HashMap<String, Double> prevMap = prevCluster.get(clusterNumber);
            HashMap<String, Double> newMap = newCluster.get(clusterNumber);

            String s1 = "";
            String s2 = "";
            for (String word : this.bagOfWords.keySet()) {
                s1 = s1.concat(String.valueOf(prevMap.get(word))) + " : ";
                s2 = s2.concat(String.valueOf(prevMap.get(word))) + " : ";

                // if(prevMap.get(word)!=newMap.get(word))
                //     return false;

            }
            
            if (!s1.equals(s2)) {
                    return false;
            }

            // System.out.println("\n");
            // System.out.println(s1.length());
            // System.out.println(s2.length());
            
        }        
        return true;
    }

    public double calcuEuclidianDistance(HashMap<String, Double> from, HashMap<String, Double> to) {

        double sumOfSquares = 0.0;
        for (String word : this.bagOfWords.keySet()) {
            double d = Math.abs(from.get(word) - to.get(word));
            sumOfSquares += (d * d);
        }
        return Math.sqrt(sumOfSquares);
    }
    

    public HashMap<Integer, HashMap<String, Double>> generateUpdatedClusters(HashMap<Integer, HashMap<String, Double>> prevClusters, HashMap<Integer, HashSet<Integer>> closestClusterMap) {
        HashMap<Integer, HashMap<String, Double>> clustersMap = new HashMap<>();

        // print the count of articles
        for (Entry<Integer, HashSet<Integer>> set : closestClusterMap.entrySet()) {
            int cluster = set.getKey();
            HashSet<Integer> articleNumbersSet = set.getValue();

            HashMap<String, Double> clusterValue = new HashMap<>();

            for (String bow : this.bagOfWords.keySet()) {
                double sum = 0.0;
                for (Integer articleID : articleNumbersSet) {
                    double valueOfArticle = this.articles.get(articleID).getNormaizedVector().get(bow);
                    sum += valueOfArticle;
                }

                clusterValue.put(bow, (double) sum / articleNumbersSet.size());
            }

            clustersMap.put(cluster, clusterValue);

        }
        

        // for (Entry<Integer, HashMap<String, Double>> set : prevClusters.entrySet()) {
        //     System.out.println("For Cluster: " + set.getKey());
        //     System.out.println(set.getValue().size());
        // }
        

        return clustersMap;
    }


    public HashMap<Integer, HashMap<String, Double>> generateKRandomClusters() {
        int N = this.articles.size();
        HashMap<Integer, HashMap<String, Double>> clustersMap = new HashMap<>();

        ArrayList<Integer> list = new ArrayList<>();
        while (list.size() != this.k) {
            int random = (int) (1 + (Math.random() * (N - 1)));

            if (!list.contains(random)) {
                list.add(random);
            }
        }
        int k = 1;
        for (int key : list) {
            clustersMap.put(k, this.articles.get(key).getNormaizedVector());
            k++;
        }

        return clustersMap;

    }
}
