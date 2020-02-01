package bearmaps;

import bearmaps.utils.MyTrieSet;
import bearmaps.utils.graph.streetmap.Node;
import bearmaps.utils.graph.streetmap.StreetMapGraph;
import bearmaps.utils.ps.KDTree;
import bearmaps.utils.ps.Point;
import bearmaps.utils.ps.WeirdPointSet;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.LinkedList;

/**
 * An augmented graph that is more powerful that a standard StreetMapGraph.
 * Specifically, it supports the following additional operations:
 *
 *
 * @author Alan Yao, Josh Hug, ________
 */
public class AugmentedStreetMapGraph extends StreetMapGraph {

    private List<Node> nodes;
    private HashMap<Point, Long> pointNodeMap;
    private KDTree wps;
    private MyTrieSet trie;
    private HashMap<String, LinkedList<String>> trieHash = new HashMap<>();
    private HashMap<String, LinkedList<Node>> nodeTrieHash = new HashMap<>();

    public AugmentedStreetMapGraph(String dbPath) {
        super(dbPath);
        // You might find it helpful to uncomment the line below:
        nodes = this.getNodes();
        List<Point> points = new LinkedList<>();
        pointNodeMap = new HashMap<>();

        for (Node n : nodes) {
            if (neighbors(n.id()).size() > 0) {
                Point p = new Point(n.lon(), n.lat());
                pointNodeMap.put(p, n.id());
                points.add(p);
            }
        }
        wps = new KDTree(points);
        // Convert list of nodes into a trie
        trie = new MyTrieSet();
        for (Node n : nodes) {
            if (n.name() != null) {
                String cleanedString = cleanString(n.name());
                if (!trieHash.containsKey(cleanedString)) {
                    // Create new linked list and add to hashmap
                    LinkedList<String> value = new LinkedList<>();
                    value.add(n.name());
                    trieHash.put(cleanedString, value);
                    LinkedList<Node> nodeLL = new LinkedList<>();
                    nodeLL.add(n);
                    nodeTrieHash.put(cleanedString, nodeLL);
                } else {
                    trieHash.get(cleanedString).add(n.name());
                    nodeTrieHash.get(cleanedString).add(n);
                }
                trie.add(cleanedString);
            }
        }
    }


    /**
     * For Project Part II
     * Returns the vertex closest to the given longitude and latitude.
     * @param lon The target longitude.
     * @param lat The target latitude.
     * @return The id of the node in the graph closest to the target.
     */
    public long closest(double lon, double lat) {
        Point closest = wps.nearest(lon, lat);
        return pointNodeMap.get(closest);
    }


    /**
     * For Project Part III (extra credit)
     * In linear time, collect all the names of OSM locations that prefix-match the query string.
     * @param prefix Prefix string to be searched for. Could be any case, with our without
     *               punctuation.
     * @return A <code>List</code> of the full names of locations whose cleaned name matches the
     * cleaned <code>prefix</code>.
     */
    public List<String> getLocationsByPrefix(String prefix) {
        // Use method from MyTrieSet to find words with this prefix
        List<String> temp = new LinkedList<>();
        temp.addAll(trie.keysWithPrefix(prefix));
        LinkedList<String> finalList = new LinkedList<>();
        for (String key : temp) {
            finalList.addAll(trieHash.get(key));
        }
        return finalList;
    }

    /**
     * For Project Part III (extra credit)
     * Collect all locations that match a cleaned <code>locationName</code>, and return
     * information about each node that matches.
     * @param locationName A full name of a location searched for.
     * @return A list of locations whose cleaned name matches the
     * cleaned <code>locationName</code>, and each location is a map of parameters for the Json
     * response as specified: <br>
     * "lat" -> Number, The latitude of the node. <br>
     * "lon" -> Number, The longitude of the node. <br>
     * "name" -> String, The actual name of the node. <br>
     * "id" -> Number, The id of the node. <br>
     */
    public List<Map<String, Object>> getLocations(String locationName) {
        LinkedList<Map<String, Object>> temp = new LinkedList<>();
        if (!nodeTrieHash.containsKey(locationName)) {
            return temp;
        }
        LinkedList<Node> nodeLL = nodeTrieHash.get(locationName);
        for (Node n : nodeLL) {
            HashMap<String, Object> nodeMap = new HashMap<>();
            nodeMap.put("lat", n.lat());
            nodeMap.put("lon", n.lon());
            nodeMap.put("name", n.name());
            nodeMap.put("id", n.id());
            temp.add(nodeMap);
        }
        return temp;
    }


    /**
     * Useful for Part III. Do not modify.
     * Helper to process strings into their "cleaned" form, ignoring punctuation and capitalization.
     * @param s Input string.
     * @return Cleaned string.
     */
    private static String cleanString(String s) {
        return s.replaceAll("[^a-zA-Z ]", "").toLowerCase();
    }

}
