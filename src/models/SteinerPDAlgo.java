package models;

import gui.DisplayPanel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class SteinerPDAlgo {

    public void runAlgorithm(SPDModel model, DisplayPanel graphPanel) throws InterruptedException {

        final List<Edge> _edges = model.getAllEdges();
        final List<Node> _nodes = model.getAllNodes();
        sortEdges(_edges, _nodes, graphPanel);
        constructForest(_edges, _nodes, graphPanel);

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
            } else if ((start == true && end == false) || (start == false && end == true)) {
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
    
    public void constructForest(List<Edge> edges, List<Node> nodes, DisplayPanel graphPanel) throws InterruptedException {
        boolean start;
        boolean end;
        int end_loop = 0;
        int i = 0;
        int numActiveSets = 0;
        while (end_loop != edges.size()) {

            start = edges.get(i).getStartNode().isTeminal();
            end = edges.get(i).getEndNode().isTeminal();
            if ((start == true && end == false)
                    || (start == false && end == true) || (start == true && end == true)) {
                edges.get(i).setPrimal(1);
                edges.get(i).getStartNode().setActive(true);
                edges.get(i).getEndNode().setActive(true);
                List<Edge> temp_list = new ArrayList<Edge>();
                temp_list.add(edges.get(i));

                end_loop++;
                graphPanel.drawMould(temp_list);
                Thread.sleep(1000);

            }

            boolean start_active = edges.get(i).getStartNode().getActive();
            boolean end_active = edges.get(i).getEndNode().getActive();
            if ((edges.get(i).getGrowth() == 0) && (start_active == true || end_active == true)) {
                edges.get(i).setPrimal(1);
                edges.get(i).getStartNode().setActive(true);
                edges.get(i).getEndNode().setActive(true);
                List<Edge> temp_list = new ArrayList<Edge>();
                temp_list.add(edges.get(i));

                end_loop++;
                graphPanel.drawMould(temp_list);
                Thread.sleep(1000);

            }

            i++;
            if (i >= edges.size()) {
                for (int j = 0; j < edges.size(); j++) {
                    boolean start_active1 = edges.get(j).getStartNode().getActive();
                    boolean end_active1 = edges.get(j).getEndNode().getActive();
                    if ((edges.get(j).getGrowth() == 0) && (start_active1 == true || end_active1 == true)) {
                        edges.get(j).setPrimal(1);
                        edges.get(j).getStartNode().setActive(true);
                        edges.get(j).getEndNode().setActive(true);
                        List<Edge> temp_list1 = new ArrayList<Edge>();
                        temp_list1.add(edges.get(j));

                        end_loop++;
                        graphPanel.drawMould(temp_list1);
                        Thread.sleep(1000);

                    }

                }

            }
            //sortEdgesSecondItr(edges,nodes,graphPanel);
        }
  
    }

    
    public void prune(SPDModel model) {

    }

}
