package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SPDModel {

    private final List<Node> _node_list;
    private final List<Edge> _edges;
    private List<ActiveSet> _activeSets;

    public SPDModel() {
        _node_list = new ArrayList<Node>();
        _edges = new ArrayList<Edge>();
        _activeSets = new ArrayList<ActiveSet>();
    }

    public void addNode(int x, int y) {
        _node_list.add(new Node(x, y));
    }

    public void addEdge(String startNodeName, String endNodeName, int cost) {
        Node startNode = getNodeByName(startNodeName);
        Node endNode = getNodeByName(endNodeName);
        if (startNode != null && endNode != null) {
            _edges.add(new Edge(startNode, endNode, cost));
        }
    }

    public void setTerminals(String startNodeName, String endNodeName) {
        Node startNode = getNodeByName(startNodeName);
        Node endNode = getNodeByName(endNodeName);
        if (startNode != null && endNode != null) {
            startNode.setTerminal(true);
            endNode.setTerminal(true);
            startNode.addToConnectNodes(endNode);
            endNode.addToConnectNodes(startNode);
        }
    }

    private Node getNodeByName(String a_name) {
        Node retNode = null, curNode = null;
        Iterator<Node> listItr = _node_list.iterator();
        while (listItr.hasNext()) {
            curNode = (Node) listItr.next();
            if (curNode.getName().toLowerCase().equals(a_name.toLowerCase())) {
                retNode = curNode;
                break;
            }
        }
        return retNode;
    }

    public List<Node> getAllNodes() {
        return new ArrayList<Node>(_node_list);
    }

    public List<Edge> getAllEdges() {
        return new ArrayList<Edge>(_edges);
    }

    public ActiveSet getActiveSet(int setId) {
        for (ActiveSet set : _activeSets) {
            if (set.getSetId() == setId) {
                return set;
            }
        }
        return null;
    }
}
