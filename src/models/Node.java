
package models;


public class Node {
    
    private static int _gloabal_counter = 0;
    
    private final int _id;
    private final int _x_pos,_y_pos;
    
    public Node(int x_pos, int y_pos){
        _id = ++_gloabal_counter;
        _x_pos = x_pos;
        _y_pos = y_pos;
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
}
