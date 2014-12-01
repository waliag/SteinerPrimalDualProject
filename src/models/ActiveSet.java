package models;

import java.util.ArrayList;
import java.util.List;

public class ActiveSet {
    private final int _setId;
    private List<Node> _activeNodes;
    ActiveSet(int setId)
    {
        _activeNodes = new ArrayList<Node>();
        _setId = setId;
    }
    public void addNode(Node node)
    {
        _activeNodes.add(node);
    }
    public List<Node> getActiveSetNodes()
    {
        return new ArrayList<Node>(_activeNodes);
    }
    public int getSetId()
    {
        return _setId;
    }
    public void addNodeinActiveSet(Node node)
    {
        _activeNodes.add(node);
    }
    public void mergeActiveSet(ActiveSet set)
    {
        for(Node node :set.getActiveSetNodes())
        {
            addNodeinActiveSet(node);
        }
    }
    public boolean isNodePresentinActiveSet(Node node)
    {
        boolean present = false;
        for(Node setNode: _activeNodes){
            if(setNode.getName().equals(node.getName())){
                present = true;
                break;
            }
        }
        return present;
    }
}
