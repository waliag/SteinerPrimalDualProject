
package models;

import java.util.ArrayList;

import java.util.List;
/*import java.util.Enumeration;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;*/


public class Node {
    
    private static int _global_counter = 0;
    
    private final int _id;
    private final int _x_pos,_y_pos;
    private boolean _is_terminal;
    private List<Node> _must_be_connected_nodes;
    private boolean _active;
    private double _dual_y;
    
    public Node(int x_pos, int y_pos){
        _id = ++_global_counter;
        _x_pos = x_pos;
        _y_pos = y_pos;
        _must_be_connected_nodes = new ArrayList<Node>();
        _active = false;
        _dual_y = 0;
        _is_terminal = false;
    }
    public void setcounter(){
    	_global_counter = 0;
    }
    
    public String getName(){
        return "N"+_id; 
    }
    
    public int getX(){
        return _x_pos; 
    }
    
    public int getY(){
        return _y_pos; 
    }
    
    public boolean isTeminal(){
    	return _is_terminal;
    }
    
    public void setTerminal(boolean terminal) {
    	_is_terminal = terminal;
        _active = true;
    }
    
    public void addToConnectNodes(Node connectNode){
    	_must_be_connected_nodes.add(connectNode);
    }
    
    public List<Node> getConnections()
    {
    	return _must_be_connected_nodes;
    }
    public void setActive(boolean active)
    {
    	_active = active;
    }
    
    public boolean getActive()
    {
    	return _active;
    }
    
    public void clearState()
    {
        if(_is_terminal == true )
             _active = true;
        else
            _active = false;
        _dual_y = 0.0;
    }
    
   /* public boolean traverse(Node n)
    {
    	List<Node> temp_list = new ArrayList<Node>();
    	temp_list.add(n);
    	DefaultMutableTreeNode root = new DefaultMutableTreeNode(n);
    	JTree tree = new JTree(root);
    	@SuppressWarnings("rawtypes")
		Enumeration e = root.depthFirstEnumeration();
    	while(e.hasMoreElements()){
    		Node x = (Node) e.nextElement();
    		if((x.getActive()==false)&&(n._must_be_connected_nodes.contains(x))){
    		return false;
    		}
    		else
    		continue;	
    			
    	}
    	return true;
    	}
    	*/
    
    public double getDual()
    {
        return _dual_y;
    }
   public void setDual(double val)
   {
       _dual_y += val;
   }
   
  
    
}
