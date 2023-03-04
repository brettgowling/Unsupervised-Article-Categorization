import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class DocumentVector extends TextVector{

    private HashMap<String, Double> normalizedVector = new HashMap<String, Double>();

    public Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet(){
        return normalizedVector.entrySet();
    }

    public void normalize(DocumentCollection dc){

        double coefficient = 0;
        int max_freq = super.getHighestRawFrequency();
        String curr_word;
        int num_docs = dc.getSize();
        int document_freq;
        Double norm_weight = 0.0;

        for(Map.Entry<String, Integer> entry:super.getRawVectorEntrySet()){
            coefficient = (double)entry.getValue() / max_freq;
            curr_word = entry.getKey();
            document_freq = dc.getDocumentFrequency(curr_word);

            if(document_freq == 0){
                norm_weight = 0.0;
            }
            else{
                norm_weight = coefficient * Math.log(((double)num_docs/document_freq)) / Math.log(2);
            }

            normalizedVector.put(curr_word, norm_weight);
        }

    }

    public double getNormalizedFrequency(String word){
        return normalizedVector.get(word);
    }

}
