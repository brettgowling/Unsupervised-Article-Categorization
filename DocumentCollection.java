import java.io.File;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.util.*;

public class DocumentCollection implements Serializable {

    private HashMap<Integer, ArticleVector> documents = new HashMap<Integer, ArticleVector>();
    public HashMap<String, Integer> category_counts = new HashMap<>();

    public static  String noiseWordArray[] = {"a", "about", "above", "all", "along",
            "also", "although", "am", "an", "and", "any", "are", "aren't", "as", "at",
            "be", "because", "been", "but", "by", "can", "cannot", "could", "couldn't",
            "did", "didn't", "do", "does", "doesn't", "e.g.", "either", "etc", "etc.",
            "even", "ever", "enough", "for", "from", "further", "get", "gets", "got", "had", "have",
            "hardly", "has", "hasn't", "having", "he", "hence", "her", "here",
            "hereby", "herein", "hereof", "hereon", "hereto", "herewith", "him",
            "his", "how", "however", "i", "i.e.", "if", "in", "into", "it", "it's", "its",
            "me", "more", "most", "mr", "my", "near", "nor", "now", "no", "not", "or", "on", "of", "onto",
            "other", "our", "out", "over", "really", "said", "same", "she",
            "should", "shouldn't", "since", "so", "some", "such",
            "than", "that", "the", "their", "them", "then", "there", "thereby",
            "therefore", "therefrom", "therein", "thereof", "thereon", "thereto",
            "therewith", "these", "they", "this", "those", "through", "thus", "to",
            "too", "under", "until", "unto", "upon", "us", "very", "was", "wasn't",
            "we", "were", "what", "when", "where", "whereby", "wherein", "whether",
            "which", "while", "who", "whom", "whose", "why", "with", "without",
            "would", "you", "your", "yours", "yes" };

        
    public TextVector getDocumentById(int id) {
        return documents.get(id);
    }
    
    public HashMap<Integer, ArticleVector> getAllDocuments(){
        return this.documents;
    }
    public int getAverageDocumentLength(){
        int sum = 0;
        for (TextVector tv:documents.values()) {
             sum += tv.getTotalWordCount();
        }
        return (int)Math.round((double)sum / documents.size());
    }
    public int getSize(){
        return documents.size();
    }
    public Collection<ArticleVector> getDocuments(){
        return documents.values();
    }
    public Set<Map.Entry<Integer, ArticleVector>> getEntrySet(){
        return documents.entrySet();
    }
    public int getDocumentFrequency(String word){
        int frequency = 0;
        for (TextVector tv:documents.values()) {
            if(tv.contains(word)){
                frequency += 1;
            }
        }
        return frequency;
    }
    public DocumentCollection(String file, String is_document){
        String curr = "";
        int index;
        int index_for_queries = 1;
        String[] row = new String[3];

        // This is the constructor for the Unsupervised Article Categorization
        if(is_document.equalsIgnoreCase("articles")){
            try{
                Scanner my_scanner = new Scanner(new File(file));

                if(my_scanner.hasNextLine()){
                    curr = my_scanner.nextLine();
                }

                while(my_scanner.hasNextLine()){
                    curr = my_scanner.nextLine();
                    row = curr.split(",");

                    ArticleVector tv = new ArticleVector();

                    for (String word:row[1].split("[^a-zA-Z]+")){
                        word = word.toLowerCase();
                        if(!isNoiseWord(word) && (word.length() > 1)){
                            tv.add(word);
                        }
                    }

//                    System.out.println(row[0]);

                    tv.article_id = Integer.parseInt(row[0]);
                    tv.label = row[2];
                    if(category_counts.get(tv.label) == null){
                        category_counts.put(tv.label, 1);
                    }
                    else{
                        category_counts.put(tv.label, category_counts.get(tv.label) + 1);
                    }

                    documents.put(tv.article_id, tv);
                }
                my_scanner.close();
            }
            catch (FileNotFoundException e){
                e.printStackTrace();
            }
        }
    }
    private boolean isNoiseWord(String word){
        if (Arrays.asList(noiseWordArray).contains(word)){
            return true;
        }
        else{
            return false;
        }
    }

    public void normalize(DocumentCollection dc){
        for(TextVector tv:documents.values()){
            tv.normalize(dc);
        }
    }


//
//    public HashMap<Integer, TextVector> gethmap() {
//        return this.documents;
//    }
//
//    public void sethmap(HashMap<Integer, TextVector> documents) {
//        this.documents = documents;
//    }

}
