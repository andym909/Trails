import java.sql.Timestamp;
import java.util.ArrayList;
public class Trail {

    private ArrayList<Node> points;

    public void addNode(double lon, double lat) {
        Node n = new Node(lon, lat);
        points.add(n);
    }

    public Node getNode(int index) {
        return points.get(index);
    }

    public ArrayList<Node> getFlags() {
        ArrayList<Node> list = new ArrayList<Node>();
        Node current;
        for(int i=0; i<points.size(); i++) {
            current = points.get(i);
            if(current.isFlagged())
                list.add(current);
        }
        return list;
    }


    
    public class Node {
        Timestamp time;
        double longitude;
        double latitude;
        boolean flagged;
        String message;

        public Node(double lon, double lat) {
            this.longitude = lon;
            this.latitude = lat;
            flagged = false;
            time = new Timestamp(System.currentTimeMillis());
            message = "";
        }

        public void flagNode(String message) {
            flagged = true;
            this.message = message;
        }

        public boolean isFlagged() {
            return this.flagged;
        }
    }
}
