
package models;


public class Edge {
    private final Node _start_node;
    private final Node _end_node;
    private int _primal_x;
    private final int _cost; 
    private double _growth;
    
    Edge(Node start, Node end, int a_cost)
    {
        _start_node = start;
        _end_node = end;
        _primal_x = 0;
        _cost = a_cost;
        _growth = 0;
    }
    
    public void setPrimal(int a)
    {
        _primal_x = a;
    }
    public int getPrimal()
    {
        return _primal_x;
    }
    
    public Node getStartNode(){
        return _start_node;
    }
    
    public Node getEndNode(){
        return _end_node;
    }
    
    public int getCost(){
        return _cost;
    }
    
    public int getMidX(){
        return (_start_node.getX()+_end_node.getX())/2;
    }
    
    public int getMidY(){
        return (_start_node.getY()+_end_node.getY())/2;
    }
  
    public void setGrowth(double a){
    	_growth = a;
    }
    
    public double getGrowth(){
    	return _growth;
    }
    
    public void clearState()
    {
        _primal_x = 0;
        _growth = 0;
    }
}
