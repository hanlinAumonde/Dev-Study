package com.devStudy.algoSearch;

import java.util.ArrayList;
import java.util.List;

public class BaseTree {
	protected class Node {
        int data;
        List<Node> listChilds = new ArrayList<>();
        
        Node(int data) {
            this.data = data;
        }
    }
	
	protected Node root;
	
	protected void createExampleTree() {
		root = new Node(1);
		
		root.listChilds.add(new Node(2));
		root.listChilds.add(new Node(3));
		root.listChilds.add(new Node(4));
		
		root.listChilds.get(0).listChilds.add(new Node(5));
		root.listChilds.get(0).listChilds.get(0).listChilds.add(new Node(9));
		
		root.listChilds.get(1).listChilds.add(new Node(6));
		root.listChilds.get(1).listChilds.add(new Node(7));
		root.listChilds.get(1).listChilds.get(0).listChilds.add(new Node(10));
		root.listChilds.get(1).listChilds.get(0).listChilds.get(0).listChilds.add(new Node(11));
		
		root.listChilds.get(2).listChilds.add(new Node(8));
	}
}
