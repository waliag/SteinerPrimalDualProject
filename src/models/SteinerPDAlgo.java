
package models;

import gui.DisplayPanel;
import java.util.List;

public class SteinerPDAlgo {
 
    public void runAlgorithm(SPDModel model, DisplayPanel graphPanel) throws InterruptedException
    {
        model.addNode(100, 200);
        model.addNode(110, 300);
        model.addNode(310, 100);
        model.addEdge("n1", "n2", 1);
        model.addEdge("n2", "n3", 4);
        
        graphPanel.drawMould(model.getAllEdges().subList(0, 1));
        Thread.sleep(5000);
        graphPanel.drawMould(model.getAllEdges().subList(1, 2));
        //graphPanel.repaint();
        
        
        sortEdges(model.getAllEdges());
         /*
         1. While there is a terminal node with connected = false
         2. take an edge from the sortedEdges
         3. If sum of dual_y for nodes = cost of edge, it is tight
         4. Draw mold for the tight edge
            Thread.sleep(1000);
            graphPanel.redraw(graphics,model);
        
         5. go to step 1
         */
        
        /*
         Do the pruning step
         */
        prune(model);
        
    }
    
    public void sortEdges(List<Edge> edges)
    {
        
    }
    public void prune(SPDModel model)
    {
        
    }
}
