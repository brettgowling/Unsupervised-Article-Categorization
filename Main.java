import java.security.KeyStore.Entry;
import java.util.*;
public class Main {

    public static DocumentCollection articles;

    public static void main(String[] args){

        System.out.println("Starting..");

        articles = new DocumentCollection("learn-ai-bbc/BBC News Train.csv", "articles");

        articles.normalize(articles);

        // select 5 centroids at random
        KMeansClustering model = new KMeansClustering(articles);
        //model.fit(k=5)
        //model.predict(my_new_article)

        System.out.println("Ending...");
    }

}
