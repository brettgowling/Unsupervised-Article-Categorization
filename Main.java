import java.io.*;
import java.util.ArrayList;

public class Main {

    /* Booleans to indicate whether to generate and serialize new objects or read from serialized files.
     *  - SERIALIZE_DOCUMENTS - Best practice is to serialize a new one the first time checking out the repo on a new
     *                          machine. Performance benefit is minimal for small datasets like we're using.
     *  - SERIALIZE_MODELS    - Useful for saving generated models since they are generally unique by nature.
     *                          Will serialize intermediate models from multiple iterations and then serialize the best
     *                          model.
     */
    private static final boolean SERIALIZE_DOCUMENTS = false;
    private static final boolean SERIALIZE_MODELS = true;

    // Serialized object path constants
    private static final String PATH_SERIALIZED_DIR = "serialized/";
    private static final String PATH_SERIALIZED_TRAINING_ARTICLE_COLLECTION =
            PATH_SERIALIZED_DIR + "train_articles_coln.bin";
    private static final String PATH_SERIALIZED_TESTING_ARTICLE_COLLECTION =
            PATH_SERIALIZED_DIR + "test_articles_coln.bin";
    private static final String PATH_SERIALIZED_MODEL_FORMAT_STRING =
            PATH_SERIALIZED_DIR + "model_output_%s.bin";

    // File paths to different sets of data
    private static final String PATH_TRAINING_DATA = "learn-ai-bbc/BBC News Train.csv";
    private static final String PATH_TESTING_DATA = "learn-ai-bbc/BBC News Test.csv";

    public static DocumentCollection articles;

    public static double get_f1_score(double precision, double recall){
        return (2 * precision * recall) / (precision + recall);
    }

    public static void main(String[] args) {

        System.out.println("Starting...");

        // Set runtime values from constants
        String dataPath = PATH_TRAINING_DATA;
        String serializedDocCollectionPath = PATH_SERIALIZED_TRAINING_ARTICLE_COLLECTION;

//        String dataPath = PATH_TESTING_DATA;
//        String serializedDocCollectionPath = PATH_SERIALIZED_TESTING_ARTICLE_COLLECTION;

        // Load DocumentCollection from memory to save time, or create one if specified or doesn't exist
        if (SERIALIZE_DOCUMENTS) {  // Generate and serialize new DocumentCollections
            System.out.printf("Generating new DocumentCollection from: %s\n", dataPath);
            articles = new DocumentCollection(dataPath, "articles");

            System.out.println("Normalizing articles...");
            articles.normalize(articles);

            System.out.printf("Serializing DocumentCollection to: %s\n", serializedDocCollectionPath);
            serialize(articles, serializedDocCollectionPath);
        } else {  // Read DocumentCollection from serialized object
            System.out.printf("Reading DocumentCollection from %s\n", serializedDocCollectionPath);
            articles = (DocumentCollection) load(serializedDocCollectionPath);
        }

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

        ArrayList<KMeanClusteringModel> models = new ArrayList<>();
        if (SERIALIZE_MODELS) {  // Generate and serialize new models
            for (int iteration = 0; iteration < (Math.log(articles.getSize()) / Math.log(2)); iteration++){
                KMeanClusteringModel model = new KMeanClusteringModel(articles, 5);
                model.fitCentroids();
                models.add(model);
                serialize(model, String.format(PATH_SERIALIZED_MODEL_FORMAT_STRING, iteration));

                break; //FIXME: force single iteration, so we only generate one model
            }
        } else {  // Read models from serialized objects
            File serializedDir = new File(PATH_SERIALIZED_DIR);
            if (serializedDir.isDirectory()) {
                // Get a list of all files that match model filename
                File[] modelFiles = serializedDir.listFiles((d, name) -> name.matches("model_output_\\d\\.bin"));
                assert modelFiles != null;
                // Iterate through the models in order since File.listFiles doesn't guarantee order
                for (int i = 0; i < modelFiles.length; i++) {
                    // Load each model and store in our `models` ArrayList
                    String thisModelFilename = String.format(PATH_SERIALIZED_MODEL_FORMAT_STRING, i);
                    KMeanClusteringModel model = (KMeanClusteringModel) (load(thisModelFilename));
                    models.add(model);
                }
            }
        }

        // After creating or loading models, we check their performance
        double max_precision = 0.0;
        double max_recall = 0.0;
        double max_f1_score = 0.0;
        int bestModelIndex = -1;
        for (KMeanClusteringModel model : models) {
            //FIXME: Shouldn't we be using testing data for these calculations?
            double curr_max_precision = model.getWeightedPrecision(articles);
            double curr_max_recall = model.getWeightedRecall(articles);
            double curr_f1_score = get_f1_score(curr_max_precision, curr_max_recall);

            if (curr_f1_score > max_f1_score) {
                max_precision = curr_max_precision;
                max_recall = curr_max_recall;
                max_f1_score = curr_f1_score;
                bestModelIndex = models.indexOf(model);
            }
        }

        System.out.println("Best Model: " + String.format(PATH_SERIALIZED_MODEL_FORMAT_STRING, bestModelIndex));
        System.out.println("FINAL MAX PRECISION: \t" + max_precision);
        System.out.println("FINAL MAX RECALL: \t" + max_recall);
        System.out.println("FINAL MAX F1-SCORE: \t" + max_f1_score);

        System.out.println("Ending...");
    }

    /* Method to serialize a provided object to the specified path. */
    private static void serialize(Object obj, String outputPath) {
        try (ObjectOutputStream os = new ObjectOutputStream(new FileOutputStream(outputPath))) {
            os.writeObject(obj);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    private static Object load(String pathToLoadFrom) {
        Object obj = null;
        try (ObjectInputStream is = new ObjectInputStream(new FileInputStream(pathToLoadFrom))) {
            obj = is.readObject();
        } catch (FileNotFoundException e) {
            System.out.printf("Tried to load object from path \"%s\", but file wasn't found. ", pathToLoadFrom);
            System.out.println("Try running with global variable SERIALIZE set to true.");
            System.out.println(e);
            System.exit(1);
        } catch (Exception e) {
            System.out.println(e);
            System.exit(1);
        }
        return obj;
    }
}
