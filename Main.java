import java.security.KeyStore.Entry;
import java.util.*;
public class Main {

    public static DocumentCollection articles;

    public static void main(String[] args){

        System.out.println("Starting..");
        // currently testing with demo data
        //articles = new DocumentCollection("learn-ai-bbc/testing.csv", "articles");
        articles = new DocumentCollection("learn-ai-bbc/BBC News Train.csv", "articles");

        articles.normalize(articles);

        // uncomment to make it work
        KMeansClustering model = new KMeansClustering(articles.getAllDocuments(), 5);
    

        System.out.println("Ending...");
    }

}
