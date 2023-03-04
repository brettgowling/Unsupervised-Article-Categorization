import java.util.Map;
import java.util.Set;

public class CosineDistance implements DocumentDistance{

    public double findDistance(TextVector query, TextVector document, DocumentCollection documents){

        Set<Map.Entry<String, Double>> query_normalized = query.getNormalizedVectorEntrySet();
        Set<Map.Entry<String, Double>> document_normalized = document.getNormalizedVectorEntrySet();

        double dot_protduct_sum = 0;
        double query_doc_frequency_squared_sum = 0;
        double document_doc_frequency_squared_sum = 0;

        for(Map.Entry<String, Double> query_entry:query_normalized){
            query_doc_frequency_squared_sum += Math.pow(query_entry.getValue(), 2);
            for(Map.Entry<String, Double> document_entry:document_normalized){
                document_doc_frequency_squared_sum += Math.pow(document_entry.getValue(), 2);
                if(query_entry.getKey().equalsIgnoreCase(document_entry.getKey())){
                    dot_protduct_sum += (query_entry.getValue() * document_entry.getValue());
                    break;
                }
            }
        }

        return dot_protduct_sum / (Math.sqrt(query_doc_frequency_squared_sum) * Math.sqrt(document_doc_frequency_squared_sum));
    }

}
