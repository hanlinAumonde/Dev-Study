package com.devStudy.Trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class BTree<T extends Comparable<T>> {
	private int midPos_split;
	private int order;
	
	static class Node<T>{
		boolean isLeaf;
		Node<T> parent;
		List<Node<T>> children;
		List<T> dataList;
		
		Node(boolean isLeaf, Node<T> parent) {
			this.isLeaf = isLeaf;
			this.parent = parent;
			this.children = new ArrayList<Node<T>>();
			this.dataList = new ArrayList<T>();
		}
	}
	
	private Node<T> root;
	
	public BTree(int order) {
		if (order < 3) {
	        throw new IllegalArgumentException("B-tree order must be at least 3");
	    }
		this.order = order;
		this.midPos_split = (order - 1)/ 2;
		this.root = new Node<T>(true, null);
	}
	
	public void insert(T data) {
		if (data == null) {
	        throw new IllegalArgumentException("Cannot insert null into B-tree");
	    }
		insertNode(root, data);
	}
	
	private void insertNode(Node<T> node, T data) {
		if(node.isLeaf) {
            insertData(node, data);
		}else {
			int insertPosition = Collections.binarySearch(node.dataList, data);
			if (insertPosition < 0) {
				insertPosition = -insertPosition - 1;
			}
			insertNode(node.children.get(insertPosition), data);
		}
	}
	
	private void insertData(Node<T> node, T data) {
		int insertPosition = Collections.binarySearch(node.dataList, data);
		if(insertPosition < 0) {
			insertPosition = -insertPosition - 1;
		}else {
			return;
		}
		node.dataList.add(insertPosition, data);
		
		if(node.dataList.size() == this.order) {
			splitNode(node);
		}
	}
	
	private void splitNode(Node<T> node) {
		T dataAtSplitPos = node.dataList.get(midPos_split);
		
		Node<T> parent = node.parent;
		if(parent == null) {
			parent = new Node<T>(false, null);
			this.root = parent;
		}
		Node<T> leftNode = new Node<T>(node.isLeaf, parent);
		Node<T> rightNode = new Node<T>(node.isLeaf, parent);
		
		leftNode.dataList.addAll(node.dataList.subList(0, midPos_split));
		rightNode.dataList.addAll(node.dataList.subList(midPos_split+1, node.dataList.size()));
		
		if(!node.isLeaf) {
			List<Node<T>> leftChildren = node.children.subList(0, midPos_split + 1);
			List<Node<T>> rightChildren = node.children.subList(midPos_split + 1, node.children.size());
			
			leftChildren.stream().forEach(child -> child.parent = leftNode);
			rightChildren.stream().forEach(child -> child.parent = rightNode);
			
			leftNode.children.addAll(leftChildren);
			rightNode.children.addAll(rightChildren);
		}
		
		if(!parent.children.isEmpty()) {
			int nodeIndex = parent.children.indexOf(node);
			parent.children.set(nodeIndex, leftNode);
			parent.children.add(nodeIndex + 1, rightNode);
		}else {
			parent.children.add(leftNode);
			parent.children.add(rightNode);
		}
		insertData(parent, dataAtSplitPos);
	}
	
	public boolean contains(T data) {
	    return search(root, data) != null;
	}

	private Node<T> search(Node<T> node, T data) {
	    int pos = Collections.binarySearch(node.dataList, data);
	    if (pos >= 0) {
	        return node;
	    }
	    if (node.isLeaf) {
	        return null;
	    }
	    return search(node.children.get(-pos-1), data);
	}
	
	public List<T> toList() {
	    List<T> result = new ArrayList<>();
	    collectElements(root, result);
	    return result;
	}

	private void collectElements(Node<T> node, List<T> result) {
	    if (node.isLeaf) {
	        result.addAll(node.dataList);
	    } else {
	        for (int i = 0; i < node.dataList.size(); i++) {
	            collectElements(node.children.get(i), result);
	            result.add(node.dataList.get(i));
	        }
	        collectElements(node.children.get(node.children.size()-1), result);
	    }
	}
	
	public void printPerNode() {
		printPerNode(root);
	}
	
	private void printPerNode(Node<T> node) {
		if (node.isLeaf) {
			System.out.println("Cuurent Leaf Node: ");
			node.dataList.stream().forEach(data -> System.out.print(data + " "));
			System.out.println("\n");
		} else {
			System.out.println("Cuurent non-Leaf Node: ");
			node.dataList.stream().forEach(data -> System.out.print(data + " "));
			System.out.println("\n");
			for (int i = 0; i < node.children.size(); i++) {
				printPerNode(node.children.get(i));
			}
		}
	}
	
	public static void main(String[] args) {
		//Test BTree
		BTree<Integer> bTree = new BTree<>(5);
		List<Integer> list = Arrays.asList(17,6,13,1,4,8,11,14,16,23,35,47,55,19,22,27,34,38,45,49,53,65,74,79);
		
		list.stream().forEach(data -> bTree.insert(data));
		
		System.out.println(bTree.toList());
		bTree.printPerNode();
	}	
}
