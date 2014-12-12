package models;

import gui.DisplayPanel;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SteinerPDAlgo {

    static int i = 0;
    static boolean iterationSkipped = false;

    public boolean runAlgorithm(SPDModel model, DisplayPanel graphPanel) throws InterruptedException {

        //START Test Code
        if (model.getAllNodes().isEmpty()) {
            int offset = 130;
            for (int i = 0; i < 3; i++) {
                model.addNode(offset + i * 100, 100);
                model.addNode(offset + i * 100, 200);
            }

            model.addEdge("n1", "n2", 4);
            model.addEdge("n1", "n3", 1);
            model.addEdge("n2", "n4", 3);
            model.addEdge("n3", "n5", 13);
            model.addEdge("n3", "n4", 2);
            model.addEdge("n4", "n6", 15);
            model.addEdge("n6", "n5", 9);

            model.setTerminals("n1", "n2");
            model.setTerminals("n5", "n6");
        }
        //END Test code

        //reset
        if (i == 0) {
           
            //Initially put all terminal nodes in diff active sets
            for (Node n : model.getAllNodes()) {
                if (n.isTeminal()) {
                    model.setNumActiveSets();
                    ActiveSet set = new ActiveSet(model.getNumActiveSets());
                    set.addNode(n);
                    model.addActiveSet(set);

                }
            }
            sortEdges(model, graphPanel);
        }
        iterationSkipped = false;
        boolean retVal = true;

        do {
            retVal = constructForest(model, graphPanel);
        } while (iterationSkipped == true && retVal == true);

        return retVal;
    }

    public void sortEdges(SPDModel model, DisplayPanel graphPanel) throws InterruptedException {
        double growth;
        List<Edge> edges = model.getAllEdges();

        for (int i = 0; i < edges.size(); i++) {
            Edge currEdge = edges.get(i);
            Node startNode = currEdge.getStartNode();
            Node endNode = currEdge.getEndNode();
            boolean start = startNode.getActive();
            boolean end = endNode.getActive();

            growth = currEdge.getCost() - (startNode.getDual() + endNode.getDual());

            if (start == true && end == true) {
                growth = growth / 2;
            }
            currEdge.setGrowth(growth);
        }
        Collections.sort(edges, new EdgeComparator());
        for (int j = 0; j < edges.size(); j++) {
         System.out.println(edges.get(j).getCost());
         }

    }

    public boolean constructForest(SPDModel model, DisplayPanel graphPanel) throws InterruptedException {
        List<Edge> edges = model.getAllEdges();
        if (i < edges.size()) {
            Edge currentEdge = edges.get(i);
            Node startNode = currentEdge.getStartNode();
            Node endNode = currentEdge.getEndNode();

            boolean start_active = startNode.getActive();
            boolean end_active = endNode.getActive();

            if (currentEdge.getPrimal() == 1) {
                i++;
                iterationSkipped = true;
                return true;
            }
            if (start_active == true
                    || end_active == true) {

                ActiveSet startNodeSet = model.getActiveSetForNode(startNode);
                ActiveSet endNodeSet = model.getActiveSetForNode(endNode);
                if (startNodeSet == endNodeSet) {
                    i++;
                    iterationSkipped = true;
                    return true;
                    //  continue;
                }
                if ((startNodeSet == null || model.isRequirementConnectivtyMetInSet(startNodeSet) == true)
                        && (endNodeSet == null || model.isRequirementConnectivtyMetInSet(endNodeSet) == true)) {
                    i++;
                    iterationSkipped = true;
                    return true;
                    //continue;
                }

                currentEdge.setPrimal(1);
                model.addToConsideredEdge(currentEdge); //Add to list of edges currently considered
                model.updateDualVariables(currentEdge.getGrowth());

                if (startNodeSet == null || (model.isRequirementConnectivtyMetInSet(startNodeSet) == false)) {
                    startNode.setActive(true);
                }

                if (endNodeSet == null || (model.isRequirementConnectivtyMetInSet(endNodeSet) == false)) {
                    endNode.setActive(true);
                }

                // model.printDualVariables();
                graphPanel.drawMould(Arrays.asList(currentEdge), false);
                // Thread.sleep(2000);
                i = 0;//start from 0 to consider non terminal edges again

                //Update Active Set
                if (startNodeSet == null
                        && endNodeSet == null) {

                    //Both nodes are non terminals
                    //Create a new active set
                    model.setNumActiveSets();
                    ActiveSet newActiveNodeSet = new ActiveSet(model.getNumActiveSets());

                    newActiveNodeSet.addNode(startNode);
                    newActiveNodeSet.addNode(endNode);
                    model.addActiveSet(newActiveNodeSet);
                    if (model.isRequirementConnectivtyMetInSet(newActiveNodeSet)) {
                        model.deactivateAllNodesInSet(newActiveNodeSet);
                    }

                }
                //startNode is non terminal
                if (startNodeSet == null
                        && endNodeSet != null) {
                    endNodeSet.addNode(startNode);
                    if (model.isRequirementConnectivtyMetInSet(endNodeSet)) {
                        model.deactivateAllNodesInSet(endNodeSet);
                    }
                }
                //End node is non terminal
                if (startNodeSet != null
                        && endNodeSet == null) {

                    startNodeSet.addNode(endNode);
                    if (model.isRequirementConnectivtyMetInSet(startNodeSet)) {
                        model.deactivateAllNodesInSet(startNodeSet);
                    }
                }
                //Both are terminal nodes
                if (startNodeSet != null && endNodeSet != null) {
                    if (startNodeSet.getSetId() != endNodeSet.getSetId()) {
                        startNodeSet.mergeActiveSet(endNodeSet);
                        model.deleteActiveSet(endNodeSet);
                        if (model.isRequirementConnectivtyMetInSet(startNodeSet)) {
                            model.deactivateAllNodesInSet(startNodeSet);
                        }
                    }
                }
                sortEdges(model, graphPanel);
                //model.printActiveSets();
                //Exit condition for algo
                if (model.requirementConnectivityMet() == true) {
                    return false;
                }
                //continue;
            }
            i++;
        }
        if (i > edges.size()) {
            iterationSkipped = true;
        }
        return true;
    }

    public void prune(SPDModel model, DisplayPanel graphPanel) throws InterruptedException {
        model.checkConnectivityRequirements(graphPanel);
        i = 0;
        iterationSkipped = false;
    }

    public void clearAlgo(SPDModel model, DisplayPanel graphPanel) {
        model.deletestruct(graphPanel);
        i = 0;
        iterationSkipped = false;
    }

    public void resetAlgo(SPDModel model, DisplayPanel graphPanel) {
        model.clearState();
        graphPanel.repaint();
        i = 0;
        iterationSkipped = false;
    }
}
