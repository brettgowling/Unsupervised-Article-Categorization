public class Main {

    public static DocumentCollection articles;

    public static void main(String[] args){

        System.out.println("Hello, world!");

        articles = new DocumentCollection("learn-ai-bbc/BBC News Train.csv", "articles");

        
    }

}
