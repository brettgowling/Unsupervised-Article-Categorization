import java.util.Map;
import java.util.Set;

public class ArticleVector extends TextVector{

    public Integer article_id = 0;
    public String label = "";


    @Override
    public Set<Map.Entry<String, Double>> getNormalizedVectorEntrySet() {
        return null;
    }

    @Override
    public void normalize(DocumentCollection dc) {

    }

    @Override
    public double getNormalizedFrequency(String word) {
        return 0;
    }
}
