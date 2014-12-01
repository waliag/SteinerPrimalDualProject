package models;

import gui.DisplayPanel;

import java.util.ArrayList;
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
        
        sortEdges(model,graphPanel);
        constructForest(model, graphPanel);
        prune(model, graphPanel);
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
               growth = growth /2;
            }
            currEdge.setGrowth(growth);
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
        List<Edge> edges = model.getAllEdges();
        int i = 0;
        
        while (i < edges.size()) {
            Edge currentEdge = edges.get(i);
            Node startNode = currentEdge.getStartNode();
            Node endNode = currentEdge.getEndNode();
       
            boolean start_active = startNode.getActive();
            boolean end_active = endNode.getActive();

            if(currentEdge.getPrimal() == 1)
            {
                i++;
                continue;
            }
            if (start_active == true 
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
                model.addToConsideredEdge(currentEdge); //Add to list of edges currently considered
               
                if(startNodeSet == null || (model.isRequirementConnectivtyMetInSet(startNodeSet) == false))
                    startNode.setActive(true);
                
                if(endNodeSet == null || (model.isRequirementConnectivtyMetInSet(endNodeSet) == false))
                    endNode.setActive(true);
               
                model.updateDualVariables(currentEdge.getGrowth());
               // model.printDualVariables();
                
                graphPanel.drawMould(Arrays.asList(currentEdge), false);
                Thread.sleep(1000);
                i=0;//start from 0 to consider non terminal edges again
                

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
                    if(model.isRequirementConnectivtyMetInSet(newActiveNodeSet))
                    {   model.deactivateAllNodesInSet(newActiveNodeSet);
                    }
                        
                }
                //startNode is non terminal
                if (startNodeSet == null
                        && endNodeSet != null) {
                    endNodeSet.addNode(startNode);
                     if(model.isRequirementConnectivtyMetInSet(endNodeSet))
                    {   model.deactivateAllNodesInSet(endNodeSet);
                    }
                }
                //End node is non terminal
                if (startNodeSet != null
                        && endNodeSet == null) {
                    
                    startNodeSet.addNode(endNode);
                     if(model.isRequirementConnectivtyMetInSet(startNodeSet))
                    {   model.deactivateAllNodesInSet(startNodeSet);
                    }
                }
                //Both are terminal nodes
                if (startNodeSet != null && endNodeSet != null) {
                    if (startNodeSet.getSetId() != endNodeSet.getSetId()) {
                        startNodeSet.mergeActiveSet(endNodeSet);
                        model.deleteActiveSet(endNodeSet);
                        if(model.isRequirementConnectivtyMetInSet(startNodeSet))
                        {   
                             model.deactivateAllNodesInSet(startNodeSet);
                        }
                    }
                }
                sortEdges(model,graphPanel);
                //model.printActiveSets();
                //Exit condition for algo
                if(model.requirementConnectivityMet() == true)
                    break;
                continue;
            }
            i++;
        }
   }

    public void prune(SPDModel model, DisplayPanel graphPanel) throws InterruptedException {
    		model.checkConnectivityRequirements(graphPanel);
    }

}
