import java.io.Serializable;
import java.util.*;

public abstract class TextVector implements Serializable {

    private HashMap<String, Integer> rawVector = new HashMap<String, Integer>();

    public Set<Map.Entry<String, Integer>> getRawVectorEntrySet(){
        return rawVector.entrySet();
    }
    public void add(String word){
        if (rawVector.containsKey(word)){
            rawVector.put(word, rawVector.get(word) + 1);
        }
        else{
            rawVector.put(word, 1);
        }
    }
    public boolean contains(String word){
        if(rawVector.containsKey(word)){
            return true;
        }
        return false;
    }
    public int getRawFrequency(String word){
        if(rawVector.containsKey(word)){
            return rawVector.get(word);
        }
        else{
            return 0;
        }
    }
    public int getTotalWordCount(){
        int sum = 0;
        for (Integer frequency:rawVector.values()){
            sum += frequency;
        }
        return sum;
    }
    public int getDistinctWordCount(){
        return rawVector.size();
    }
    public int getHighestRawFrequency(){
        int highest = 0;
        for(Integer frequency:rawVector.values()){
            if (frequency > highest){
                highest = frequency;
            }
        }
        return highest;
    }
    public String getMostFrequentWord(){
        int highest = 0;
        String frequent_word = "";
        for(String word:rawVector.keySet()){
            if(rawVector.get(word) > highest){
                highest = rawVector.get(word);
                frequent_word = word;
            }
        }
        return frequent_word;
    }

    public abstract Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet();

    public abstract void normalize(DocumentCollection dc);

    public abstract double getNormalizedFrequency(String word);

    public double getL2Norm(){

        double sum_of_squares = 0.0;

        for (String word:rawVector.keySet()){
            sum_of_squares += Math.pow(getNormalizedFrequency(word), 2);
        }

        return Math.sqrt(sum_of_squares);
    }

    public ArrayList<Integer> findClosestDocuments(DocumentCollection documents, DocumentDistance distanceAlg){

        ArrayList<Integer> top20 = new ArrayList<>();
        int list_size = 0;
        double curr_dist = 0;
        double curr_max = 0;
        Integer curr_max_doc_num = 0;
        HashMap<Integer, Double> distances = new HashMap<>();


        for(Integer i = 1; i <= documents.getSize(); i++){
            if(documents.getDocumentById(i).rawVector.size() == 0){
                curr_dist = 0;
            }
            else{
                curr_dist = distanceAlg.findDistance(this, documents.getDocumentById(i), documents);
            }
            distances.put(i, curr_dist);
        }

        List<Map.Entry<Integer, Double> > list =
                new LinkedList<Map.Entry<Integer, Double> >(distances.entrySet());

        list.sort(new Comparator<Map.Entry<Integer, Double>>() {
            public int compare(Map.Entry<Integer, Double> o1,
                               Map.Entry<Integer, Double> o2) {
//                if(o2.getValue().compareTo(o1.getValue()) == 0){
//                    System.out.println("EQUAL: " + o2.getKey() + "\t" + o1.getKey());
//                }
                return (o2.getValue()).compareTo(o1.getValue());
            }
        });

        for(int i = 0; i < 20; i++){
            top20.add(list.get(i).getKey());
        }

        return top20;
    }

}


//////////////////////////////////////////////////////////////
