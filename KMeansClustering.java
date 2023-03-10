import java.util.*;

public class KMeansClustering {

    private DocumentCollection articles;

    private HashSet<String> bagOfWords = new HashSet<>();

    public KMeansClustering(DocumentCollection articles) {
        System.out.println("KMeansClustering Constructor");

        this.articles = articles;
        this.initBagOfWords();

        System.out.println(getBagOfWords().size());

    }
    
    public HashSet<String> getBagOfWords() {
        return this.bagOfWords;
    }

    // initialising the bag of words
    private void initBagOfWords() {
        for (Map.Entry<Integer, ArticleVector> entry : articles.getEntrySet()) {
            // System.out.println("\n");
            //System.out.println(entry.getValue().getNormalizedVectorEntrySet());
            for (Map.Entry<String, Double> wordEntry : entry.getValue().getNormalizedVectorEntrySet()) {
                String word = wordEntry.getKey();
                Double value = wordEntry.getValue();
                bagOfWords.add(word);
            }

        }
    }
}
