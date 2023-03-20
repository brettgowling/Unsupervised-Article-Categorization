import java.util.ArrayList;

public class Cluster {

    private TextVector centroid;
    private ArrayList<TextVector> textVectors;
    public Cluster(TextVector centroid) {
        this.centroid = centroid;
        this.textVectors = new ArrayList<>();
    }

    public TextVector getCentroid() {
        return centroid;
    }

    public void calculateCentroid() {
        //TODO: finish Cluster class
    }
}
