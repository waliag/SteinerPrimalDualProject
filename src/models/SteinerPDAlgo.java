package models;

import gui.DisplayPanel;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class SteinerPDAlgo {

    public void runAlgorithm(SPDModel model, DisplayPanel graphPanel) throws InterruptedException {

        //START Test Code
        if(model.getAllNodes().isEmpty()){
            int offset = 130;
            for (int i = 0; i < 3; i++) {
                model.addNode(offset + i * 100, 100);
                model.addNode(offset + i * 100, 200);
            }

            model.addEdge("n1", "n3", 3);
            model.addEdge("n3", "n5", 13);
            model.addEdge("n5", "n6", 9);
            model.addEdge("n6", "n4", 15);
            model.addEdge("n4", "n2", 1);
            model.addEdge("n2", "n1", 10);
            model.addEdge("n3", "n4", 2);

            model.setTerminals("n1", "n2");
            model.setTerminals("n5", "n6");
        }
        //END Test code
        
        //reset
        model.clearState();
        graphPanel.repaint();
        
        Thread.sleep(1000);
        
        //Initially put all terminal nodes in diff active sets
        for(Node n:model.getAllNodes())
        {
            if(n.isTeminal())
            {
                model.setNumActiveSets();
                ActiveSet set = new ActiveSet(model.getNumActiveSets());
                set.addNode(n);
                model.addActiveSet(set);
                
            }
        }
        
        final List<Edge> edges = model.getAllEdges();
        final List<Node> nodes = model.getAllNodes();
        
        sortEdges(edges, nodes, graphPanel);
        constructForest(model, graphPanel);
        /*prune(model);*/
    }

    public void sortEdges(List<Edge> edges, List<Node> nodes, DisplayPanel graphPanel) throws InterruptedException {
        double growth;
        for (int i = 0; i < edges.size(); i++) {
            Edge currEdge = edges.get(i);
            boolean start = currEdge.getStartNode().isTeminal();
            boolean end = currEdge.getEndNode().isTeminal();
            
            if (start == true && end == true) {
                growth = currEdge.getCost() / 2;
                currEdge.setGrowth(growth);
            } else {
                growth = currEdge.getCost();
                currEdge.setGrowth(growth);
            }

        }
        Collections.sort(edges, new EdgeComparator());
        for (int j = 0; j < edges.size(); j++) {
            System.out.println(edges.get(j).getCost());
        }

    }

    /* public void sortEdgesSecondItr(List<Edge> edges, List<Node> node, DisplayPanel graphPanel) throws InterruptedException
     {
     double growth;
     for(int i = 0; i<edges.size();i++)
     {
     boolean start_active = edges.get(i).getStartNode().getActive();
     boolean end_active = edges.get(i).getStartNode().getActive();
     if(edges.get(i).getPrimal()==1)
     {
     edges.get(i).setGrowth(0);
     }
 	   
     else if(start_active==true && end_active==true)
     {
     growth = edges.get(i).getCost()/2;
     edges.get(i).setGrowth(growth);
     }
     else if((start_active==true && end_active == false)||(start_active==false && end_active==true))
     {
     growth = edges.get(i).getCost();
     edges.get(i).setGrowth(growth);
     }
 		   
     }
     Collections.sort(edges, new EdgeComparator());
     for(int j=0;j<edges.size();j++)
     System.out.println(edges.get(j).getCost());
    
     }*/
    public void constructForest(SPDModel model, DisplayPanel graphPanel) throws InterruptedException {
        boolean start;
        boolean end;
        List<Edge> edges = model.getAllEdges();
        int i = 0;
        
        while (i < edges.size()) {
            Edge currentEdge = edges.get(i);
            Node startNode = currentEdge.getStartNode();
            Node endNode = currentEdge.getEndNode();
            start = startNode.isTeminal();
            end = endNode.isTeminal();

            boolean start_active = startNode.getActive();
            boolean end_active = endNode.getActive();

            if(currentEdge.getPrimal() == 1)
            {
                i++;
                continue;
            }
            if (start == true 
                    || end == true 
                    || start_active == true 
                    || end_active == true) {

                ActiveSet startNodeSet = model.getActiveSetForNode(startNode);
                ActiveSet endNodeSet = model.getActiveSetForNode(endNode);
                if((startNodeSet == null || model.isRequirementConnectivtyMetInSet(startNodeSet) == true)
                        && (endNodeSet == null || model.isRequirementConnectivtyMetInSet(endNodeSet) == true))
                {
                    i++;
                    continue;
                }

                currentEdge.setPrimal(1);
                startNode.setActive(true);
                endNode.setActive(true);
                graphPanel.drawMould(Arrays.asList(currentEdge));
                Thread.sleep(1000);
                i=0;//start from 0 to consider non terminal edges again

                //Update Active Set
                if (startNodeSet == null
                    && endNodeSet == null) {
                    //Create separate activeSet for both nodes
                    model.setNumActiveSets();
                    ActiveSet newActiveNodeSet = new ActiveSet(model.getNumActiveSets());

                    newActiveNodeSet.addNode(startNode);
                    newActiveNodeSet.addNode(endNode);
                    model.addActiveSet(newActiveNodeSet);

                }
                if (startNodeSet == null
                        && endNodeSet != null) {
                    endNodeSet.addNode(startNode);
                }
                if (startNodeSet != null
                        && endNodeSet == null) {
                    startNodeSet.addNode(endNode);
                }
                if (startNodeSet != null && endNodeSet != null) {
                    if (startNodeSet.getSetId() != endNodeSet.getSetId()) {
                        startNodeSet.mergeActiveSet(endNodeSet);
                    }
                }

                if(model.requirementConnectivityMet() == true)
                    break;
                continue;
            }
            i++;
        }
   }

    public void prune(SPDModel model) {

    }

}
