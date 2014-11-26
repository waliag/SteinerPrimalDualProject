package models;

import java.util.Comparator;

public class EdgeComparator implements Comparator<Edge> {
    public int compare(Edge a, Edge b) {
        return Double.compare(a.getGrowth(), b.getGrowth());
    } 
} 