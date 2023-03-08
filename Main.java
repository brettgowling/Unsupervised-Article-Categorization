public class Main {

    public static DocumentCollection articles;

    public static void main(String[] args){

        System.out.println("Hello, world!");

        articles = new DocumentCollection("learn-ai-bbc/BBC News Train.csv", "articles");

        articles.normalize(articles);

        // here, we can define multiple distance functions: (ex. Manhattan, Euclidean, ...)
        DocumentDistance cosine = new CosineDistance();

        // select 5 centroids at random
        // KMeansClustering model = new KMeansClustering();
        //model.fit(k=5)
        //model.predict(my_new_article)


    }

}
