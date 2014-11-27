package models;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class SPDModel {

    private final List<Node> _node_list;
    private final List<Edge> _edges;
    private List<ActiveSet> _activeSets;
    private int _numActiveSets;

    public SPDModel() {
        _node_list = new ArrayList<Node>();
        _edges = new ArrayList<Edge>();
        _activeSets = new ArrayList<ActiveSet>();
        _numActiveSets = 0;
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
        return _node_list;//new ArrayList<>(_node_list);
    }

    public List<Edge> getAllEdges() {
        return _edges;//new ArrayList<>(_edges);
    }

    public int getNumActiveSets()
    {
        return _numActiveSets;
    }
    public void setNumActiveSets()
    {
        ++_numActiveSets;
    }
    public ActiveSet getActiveSetforSetId(int setId) {
        for (ActiveSet set : _activeSets) {
            if (set.getSetId() == setId) {
                return set;
            }
        }
        return null;
    }
    public ActiveSet getActiveSetForNode(Node node)
    {
        for(ActiveSet set: _activeSets){
           if(set.isActive() && set.isNodePresentinActiveSet(node) == true){
               return set;
           }            
        }
        return null;
    }
    public void addActiveSet(ActiveSet set)
    {
        _activeSets.add(set);
    }
    public void printActiveSets()
    {
        for(ActiveSet set:_activeSets)
        {
            if(set.isActive()){
                System.out.println("id " + set.getSetId());
                System.out.println("Nodes");
                for (Node n : set.getActiveSetNodes()) {
                    System.out.println(n.getName() + ",");
                }
            }
        }
    }
    public boolean requirementConnectivityMet()
    {
        boolean retVal = true;
        for(ActiveSet set: _activeSets)
        {
            if(set.isActive())
            {
                for(Node node:set.getActiveSetNodes())
                {
                    if(node.isTeminal())
                    {
                        List<Node> connectivityReq = node.getConnections();
                        for(Node n:connectivityReq )
                        {
                            if(set.isNodePresentinActiveSet(n) == false)
                                retVal = false;
                        }
                    }
                }
            }
        }
        return retVal;
    }
    public boolean isRequirementConnectivtyMetInSet(ActiveSet set)
    {
        boolean retVal = true;
        if (set.isActive()) {
            for (Node node : set.getActiveSetNodes()) {
                if (node.isTeminal()) {
                    List<Node> connectivityReq = node.getConnections();
                    for (Node n : connectivityReq) {
                        if (set.isNodePresentinActiveSet(n) == false) {
                            retVal = false;
                        }
                    }
                }
            }
        }

        return retVal;
    }
    
    public void clearState()
    {
        _activeSets.clear();
        for(Node node:_node_list){
            node.clearState();
        }
        for(Edge edge:_edges){
            edge.clearState();
        }
    }
}
