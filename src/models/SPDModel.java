package models;

import gui.DisplayPanel;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.Map;

public class SPDModel {

    private final List<Node> _node_list;
    private  final List<Edge> _edges;
    private final List<ActiveSet> _activeSets;
    private int _numActiveSets;
    private final List<Edge> _considered_edges;
    private final List<Edge> _original_edges;

    public SPDModel() {
        _node_list = new ArrayList<Node>();
        _edges = new ArrayList<Edge>();
        _activeSets = new ArrayList<ActiveSet>();
        _considered_edges = new ArrayList<Edge>();
        _numActiveSets = 0;
        _original_edges = new ArrayList<Edge>();
    }

    public void addNode(int x, int y) {
        _node_list.add(new Node(x, y));
    }

    public void addEdge(String startNodeName, String endNodeName, int cost) {
        Node startNode = getNodeByName(startNodeName);
        Node endNode = getNodeByName(endNodeName);
        if (startNode != null && endNode != null) {
            _edges.add(new Edge(startNode, endNode, cost));
            _original_edges.add(new Edge(startNode, endNode, cost));
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
    
    public List<Node> getNewListOfAllNodes() {
        return new ArrayList<Node>(_node_list);
    }

    public List<Edge> getNewListOfAllEdges() {
        return new ArrayList<Edge>(_edges);
    }

    public int getNumActiveSets() {
        return _numActiveSets;
    }

    public void setNumActiveSets() {
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

    public ActiveSet getActiveSetForNode(Node node) {
        for (ActiveSet set : _activeSets) {
            if (set.isNodePresentinActiveSet(node) == true) {
                return set;
            }
        }
        return null;
    }

    public void addActiveSet(ActiveSet set) {
        _activeSets.add(set);
    }

    public void deleteActiveSet(ActiveSet set) {
        _activeSets.remove(set);
    }

    public void printActiveSets() {
        for (ActiveSet set : _activeSets) {

            System.out.println("id " + set.getSetId());
            System.out.println("Nodes");
            for (Node n : set.getActiveSetNodes()) {
                System.out.println(n.getName() + ",");
            }

        }
    }

    public boolean requirementConnectivityMet() {
        boolean retVal = true;
        for (ActiveSet set : _activeSets) {
            for (Node node : set.getActiveSetNodes()) {
                if (node.isTeminal()) {
                    List<Node> connectivityReq = node.getConnections();
                    for (Node n : connectivityReq) {
                        if (set.isNodePresentinActiveSet(n) == false) {
                            retVal = false;
                            break;
                        }
                    }
                }
                if (retVal == false) {
                    break;
                }
            }

            if (retVal == false) {
                break;
            }
        }
        return retVal;
    }

    public boolean isRequirementConnectivtyMetInSet(ActiveSet set) {
        boolean retVal = true;

        for (Node node : set.getActiveSetNodes()) {
            if (node.isTeminal()) {
                List<Node> connectivityReq = node.getConnections();
                for (Node n : connectivityReq) {
                    if (set.isNodePresentinActiveSet(n) == false) {
                        retVal = false;
                        break;
                    }
                }
            }
            if (retVal == false) {
                break;
            }
        }

        return retVal;
    }

    public void deactivateAllNodesInSet(ActiveSet set) {

        for (Node node : set.getActiveSetNodes()) {
            node.setActive(false);
        }

    }

    public void clearState() {
        _activeSets.clear();
        for (Node node : _node_list) {
            node.clearState();
        }
        _edges.clear();
        for (Edge edge : _original_edges) {
            edge.clearState();
            _edges.add(edge);
        }
       
        _considered_edges.clear();
        _numActiveSets = 0;
    }

    public void addToConsideredEdge(Edge currentEdge) {
        _considered_edges.add(currentEdge);
    }

    public void checkConnectivityRequirements(DisplayPanel graphPanel) throws InterruptedException {
        Edge considered_edge;
        boolean canBePruned = true;

        for (int edge_itr = (_considered_edges.size() - 1); edge_itr >= 0; edge_itr--) {
            considered_edge = _considered_edges.get(edge_itr);
            _considered_edges.remove(edge_itr);
            for (Node node : _node_list) {
                canBePruned = checkIfEdgeCanBePruned(node);
                if (canBePruned == false) {
                    break;
                }
            }
            if (canBePruned == false) {
                _considered_edges.add(edge_itr, considered_edge);
            } else {
                graphPanel.drawMould(Arrays.asList(considered_edge), true);
                Thread.sleep(2000);
               // System.out.println(considered_edge.getCost());
            }
        }

    }

    private List<Node> getAdjacentNeighborsForNode(Node currNode, HashSet<Node> visitedNodes) {
        List<Node> nodeNeighbors = new ArrayList<Node>();
        for (Edge edge : _considered_edges) {
            if (edge.getStartNode().equals(currNode)
                    && !isVisited(edge.getEndNode(), visitedNodes)) {
            	nodeNeighbors.add(edge.getEndNode());
            } else if (edge.getEndNode().equals(currNode)
                    && !isVisited(edge.getStartNode(), visitedNodes)) {
            	nodeNeighbors.add(edge.getStartNode());
            }
        }
        return nodeNeighbors;
    }

    private boolean isVisited(Node currNode, HashSet<Node> visitedNodes) {
        return visitedNodes.contains(currNode);
    }

    private boolean checkIfEdgeCanBePruned(Node node) {
        List<Node> connectivityReq = node.getConnections();
        boolean retVal = true;
        HashSet<Node> visitedNodes = new HashSet<Node>();
        for (Node connectNode : connectivityReq) {
            if (checkPathExistsUsingDFS(node,connectNode,visitedNodes) == false) {
                retVal = false;
                break;
            }
            visitedNodes.clear();
        }
        
        return retVal;
    }

    private boolean checkPathExistsUsingDFS(Node node, Node connectNode, HashSet<Node> visitedNodes) {
    	 List<Node> adjacentNodes = getAdjacentNeighborsForNode(node, visitedNodes);
    	 boolean retval = false;
         for (Node adjacentNode : adjacentNodes) {
        	 if(adjacentNode==connectNode){
        		 retval = true;
        		 break;
        	 }
        	 visitedNodes.add(adjacentNode);
        	 retval = checkPathExistsUsingDFS(adjacentNode,connectNode,visitedNodes);
        	 if(retval == true){
        		 break;
        	 }
         }
		return retval;
	}

	public void updateDualVariables(double growthFactor) {
        for (Node n : _node_list) {
            if (n.getActive()) {
                n.setDual(growthFactor);
            }
        }
    }

    public void printDualVariables() {
        for (Node n : _node_list) {
            System.out.println("Id :" + n.getName() + " dual: " + n.getDual());

        }
    }
    
    public void deletestruct(DisplayPanel graphPanel)
    {
    	_edges.clear();
        _original_edges.clear();
    	_node_list.clear();
    	_activeSets.clear();
    	_considered_edges.clear();
    	
        Node n = new Node(0,0);
        n.setcounter();
       
        _numActiveSets = 0;
  
        graphPanel.repaint();
    }    
}
