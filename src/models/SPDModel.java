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
    private final List<Edge> _edges;
    private List<ActiveSet> _activeSets;
    private int _numActiveSets;
    private List<Edge> _considered_edges;

    public SPDModel() {
        _node_list = new ArrayList<Node>();
        _edges = new ArrayList<Edge>();
        _activeSets = new ArrayList<ActiveSet>();
        _considered_edges = new ArrayList<Edge>();
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
        for (Edge edge : _edges) {
            edge.clearState();
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
        Map<Node, Integer> distance = new HashMap<Node, Integer>();

        for (int edge_itr = (_considered_edges.size() - 1); edge_itr >= 0; edge_itr--) {
            considered_edge = _considered_edges.get(edge_itr);
            _considered_edges.remove(edge_itr);
            for (Node node : _node_list) {
                loadDistanceForCurrNode(node, distance);
                canBePruned = checkIfEdgeCanBePruned(node, distance);
                distance.clear();
                if (canBePruned == false) {
                    break;
                }
            }
            if (canBePruned == false) {
                _considered_edges.add(edge_itr, considered_edge);
            } else {
                graphPanel.drawMould(Arrays.asList(considered_edge), true);
                Thread.sleep(1000);
                System.out.println(considered_edge.getCost());
            }
        }

    }

    private void loadDistanceForCurrNode(Node node, Map<Node, Integer> distance) {
        HashSet<Node> unProccessedNodes = new HashSet<Node>();
        HashSet<Node> proccessedNodes = new HashSet<Node>();
        Node currNode;
        distance.put(node, 0);
        unProccessedNodes.add(node);
        while (unProccessedNodes.size() > 0) {
            currNode = getMinimumCostNode(unProccessedNodes, distance);
            proccessedNodes.add(currNode);
            unProccessedNodes.remove(currNode);
            findMinimumDistancesForCurrNode(currNode, proccessedNodes, unProccessedNodes, distance);
        }

    }

    private void findMinimumDistancesForCurrNode(Node currNode, HashSet<Node> proccessedNodes, HashSet<Node> unProccessedNodes, Map<Node, Integer> distance) {
        List<Node> adjacentNodes = getNeighbors(currNode, proccessedNodes);
        for (Node target : adjacentNodes) {
            if (getShortestDistance(target, distance) > getShortestDistance(currNode, distance) + getDistance(currNode, target)) {
                distance.put(target, getShortestDistance(currNode, distance) + getDistance(currNode, target));
                unProccessedNodes.add(target);
            }
        }
    }

    private int getDistance(Node currNode, Node target) {
        int retVal = 0;
        for (Edge edge : _considered_edges) {
            if (edge.getStartNode().equals(currNode)
                    && edge.getEndNode().equals(target)) {
                retVal = edge.getCost();
                break;
            } else if (edge.getEndNode().equals(currNode)
                    && edge.getStartNode().equals(target)) {
                retVal = edge.getCost();
                break;
            }
        }
        return retVal;
    }

    private List<Node> getNeighbors(Node currNode, HashSet<Node> proccessedNodes) {
        List<Node> neighbors = new ArrayList<Node>();
        for (Edge edge : _considered_edges) {
            if (edge.getStartNode().equals(currNode)
                    && !isSettled(edge.getEndNode(), proccessedNodes)) {
                neighbors.add(edge.getEndNode());
            } else if (edge.getEndNode().equals(currNode)
                    && !isSettled(edge.getStartNode(), proccessedNodes)) {
                neighbors.add(edge.getStartNode());
            }
        }
        return neighbors;
    }

    private boolean isSettled(Node currNode, HashSet<Node> proccessedNodes) {
        return proccessedNodes.contains(currNode);
    }

    private Node getMinimumCostNode(HashSet<Node> unProccessedNodes, Map<Node, Integer> distance) {
        Node min = null;
        for (Node node : unProccessedNodes) {
            if (min == null) {
                min = node;
            } else {
                if (getShortestDistance(node, distance) < getShortestDistance(min, distance)) {
                    min = node;
                }
            }
        }
        return min;
    }

    private int getShortestDistance(Node node, Map<Node, Integer> distance) {
        Integer dist = distance.get(node);
        if (dist == null) {
            return Integer.MAX_VALUE;
        } else {
            return dist;
        }
    }

    private boolean checkIfEdgeCanBePruned(Node node, Map<Node, Integer> distance) {
        List<Node> connectivityReq = node.getConnections();
        boolean retVal = true;
        for (Node currNode : connectivityReq) {
            if (distance.get(currNode) == null) {
                retVal = false;
                break;
            }
        }
        return retVal;
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
}
