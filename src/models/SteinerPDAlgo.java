package models;

import gui.DisplayPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SteinerPDAlgo {

    public void runAlgorithm(SPDModel model, DisplayPanel graphPanel) throws InterruptedException {

        //Test Code
        for (int i = 0; i < 3; i++) {
            model.addNode(100 + i * 100, 100);
            model.addNode(100 + i * 100, 200);
        }

        model.addEdge("n1", "n3", 1);
        model.addEdge("n3", "n5", 13);
        model.addEdge("n5", "n6", 9);
        model.addEdge("n6", "n4", 15);
        model.addEdge("n4", "n2", 2);
        model.addEdge("n2", "n1", 10);
        model.addEdge("n3", "n4", 3);

        model.setTerminals("n1", "n2");
        model.setTerminals("n5", "n6");

        Thread.sleep(3000);
        //END test code

        final List<Edge> _edges = model.getAllEdges();
        final List<Node> _nodes = model.getAllNodes();
        sortEdges(_edges, _nodes, graphPanel);
        constructForest(model, _edges, graphPanel);

        /*prune(model);*/
    }

    public void sortEdges(List<Edge> edges, List<Node> nodes, DisplayPanel graphPanel) throws InterruptedException {
        double growth;
        for (int i = 0; i < edges.size(); i++) {
            boolean start = edges.get(i).getStartNode().isTeminal();
            boolean end = edges.get(i).getEndNode().isTeminal();
            if (start == true && end == true) {
                growth = edges.get(i).getCost() / 2;
                edges.get(i).setGrowth(growth);
            } else {
                growth = edges.get(i).getCost();
                edges.get(i).setGrowth(growth);
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
    public void constructForest(SPDModel model, List<Edge> edges, DisplayPanel graphPanel) throws InterruptedException {
        boolean start;
        boolean end;
        int end_loop = 0;
        int i = 0;
        int numActiveSets = 0;
       // List<Edge> edges = model.getAllEdges();

        while (end_loop != edges.size() && i < edges.size()) {

            Node startNode = edges.get(i).getStartNode();
            Node endNode = edges.get(i).getEndNode();
            Edge currentEdge = edges.get(i);
            start = startNode.isTeminal();
            end = endNode.isTeminal();

            boolean start_active = startNode.getActive();
            boolean end_active = endNode.getActive();

            if (((start == true && end == false)
                    || (start == false && end == true)
                    || (start == true && end == true))
                    || (start_active == true || end_active == true)) {

                currentEdge.setPrimal(1);
                startNode.setActive(true);
                endNode.setActive(true);
                List<Edge> temp_list = new ArrayList<Edge>();
                temp_list.add(currentEdge);

                end_loop++;
                graphPanel.drawMould(temp_list);
                Thread.sleep(1000);
                i++;

                //Update Active Set
                ActiveSet startNodeSet = model.getActiveSetForNode(startNode);
                ActiveSet endNodeSet = model.getActiveSetForNode(endNode);

                if (startNodeSet == null
                        && endNodeSet == null) {
                    //Create separate activeSet for both nodes
                    ActiveSet newActiveNodeSet = new ActiveSet(++numActiveSets);

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

                model.printActiveSets();
            }
        }
    }

    public void prune(SPDModel model) {

    }

}
