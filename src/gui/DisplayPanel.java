package gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import models.*;

/**
 * Displays the nodes and connections in the graph
 */
public class DisplayPanel extends JPanel
{
    private static final int _DIA = 30;
    
    public DisplayPanel(final SPDModel model)
    {
        setBackground(Color.white);
        repaint();
        setLayout(null);
        
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                while (true)
                {
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(SteinerPrimalDualGUI.class.getName())
                                .log(Level.SEVERE, null, ex);
                    }
                    for(Node node:model.getAllNodes()){
                        drawNode(node);
                    }
                    for(Edge edge:model.getAllEdges()){
                        drawEdge(edge);
                    }
                }
            }
        }).start();
    }
    
    private void drawNode(Node node)
    {
        Graphics g = getGraphics();
        g.setColor(Color.MAGENTA);
        g.drawOval(node.getX()-_DIA/2,
                node.getY()-_DIA/2,
                _DIA,
                _DIA);
        g.drawString(node.getName(), node.getX()-_DIA/4, node.getY()+_DIA/4);
    }
    
    private void drawEdge(Edge edge)
    {
        Graphics g = getGraphics();
        g.setColor(Color.RED);
        g.drawLine(edge.getStartNode().getX(),
                           edge.getStartNode().getY(),
                           edge.getEndNode().getX(),
                           edge.getEndNode().getY());
        g.drawString(String.valueOf(edge.getCost()),edge.getMidX(),edge.getMidY() - 10);
    }
    
    public void drawMould(List<Edge> edges){
        Graphics g = getGraphics();
        Graphics2D g2d = (Graphics2D) g;
        Composite originalComposite = g2d.getComposite();
        g2d.setPaint(Color.gray);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        BasicStroke stroke = new BasicStroke(_DIA*2,BasicStroke.CAP_ROUND,
                        BasicStroke.JOIN_ROUND);
        Stroke originalStroke = g2d.getStroke();
        g2d.setStroke(stroke);
                
        for (Edge edge : edges) {
            g2d.drawLine(edge.getStartNode().getX(), edge.getStartNode().getY(), 
                    edge.getEndNode().getX(), edge.getEndNode().getY());
            
        }
        g2d.setComposite(originalComposite);
        g2d.setStroke(originalStroke);
    } 
}