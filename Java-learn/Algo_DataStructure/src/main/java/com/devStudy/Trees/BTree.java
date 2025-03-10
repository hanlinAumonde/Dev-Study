package com.devStudy.Trees;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class BTree<T extends Comparable<T>> {
	private int minDataSizePerNode;
	private int midPos_split;
	private int order;
	
	/*
	 * For a B-tree, each node stores data including pointers to other nodes, key fields (such as unique + ordered in database indexes),
	 * and data records corresponding to the keys, corresponding to each key
	 * Here only simulates the key list
	 */
	static class Node<T>{
		boolean isLeaf;
		Node<T> parent;
		List<Node<T>> children;
		List<T> keys;
		//List<T> records;
		
		Node(boolean isLeaf, Node<T> parent) {
			this.isLeaf = isLeaf;
			this.parent = parent;
			this.children = new LinkedList<Node<T>>();
			this.keys = new LinkedList<T>();
			//this.records = new LinkedList<T>();
		}
	}
	
	private Node<T> root;
	
	//Constructor
	public BTree(int order) {
		if (order < 3) {
	        throw new IllegalArgumentException("B-tree order must be at least 3");
	    }
		this.order = order;
		this.midPos_split = (order - 1)/ 2;
		this.minDataSizePerNode = order / 2;
		this.root = new Node<T>(true, null);
	}
	
//---------------------------------------------------------------------------------------
//---------------------------------------Insertion---------------------------------------
//---------------------------------------------------------------------------------------
	
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
			int insertPosition = Collections.binarySearch(node.keys, data);
			if (insertPosition < 0) {
				insertPosition = -insertPosition - 1;
			}else {
				System.out.println("Data already exists in the B-tree");
				return;
			}
			insertNode(node.children.get(insertPosition), data);
		}
	}
	
	private void insertData(Node<T> node, T data) {
		int insertPosition = Collections.binarySearch(node.keys, data);
		if(insertPosition < 0) {
			insertPosition = -insertPosition - 1;
		}else {
			System.out.println("Data already exists in the B-tree");
			return;
		}
		node.keys.add(insertPosition, data);
		
		if(node.keys.size() == this.order) {
			splitNode(node);
		}
	}
	
	private void splitNode(Node<T> node) {
		T dataAtSplitPos = node.keys.get(midPos_split);
		
		Node<T> parent = node.parent;
		if(parent == null) {
			parent = new Node<T>(false, null);
			this.root = parent;
		}
		Node<T> leftNode = new Node<T>(node.isLeaf, parent);
		Node<T> rightNode = new Node<T>(node.isLeaf, parent);
		
		leftNode.keys.addAll(node.keys.subList(0, midPos_split));
		rightNode.keys.addAll(node.keys.subList(midPos_split+1, node.keys.size()));
		
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
	
//---------------------------------------------------------------------------------------
//---------------------------------------Search------------------------------------------
//---------------------------------------------------------------------------------------
	
	public boolean contains(T data) {
	    return search(root, data) != null;
	}

	private Node<T> search(Node<T> node, T data) {
	    int pos = Collections.binarySearch(node.keys, data);
	    if (pos >= 0) {
	        return node;
	    }
	    if (node.isLeaf) {
	        return null;
	    }
	    return search(node.children.get(-pos-1), data);
	}
	
//---------------------------------------------------------------------------------------
//---------------------------------------Deletion----------------------------------------
//---------------------------------------------------------------------------------------
	
	public void delete(T data) {
		if (data == null) {
	        throw new IllegalArgumentException("Cannot delete null");
	    }
		deleteNode(root, data);
	}
	
	private void deleteNode(Node<T> node, T data) {
		//int pos_dataToDelete = node.keys.indexOf(data);
		int pos_dataToDelete = Collections.binarySearch(node.keys, data);
		
		if(node.isLeaf) {
			//Data found in leaf node
			if(pos_dataToDelete >= 0) {
				node.keys.remove(pos_dataToDelete);
				if (node.keys.size() < this.minDataSizePerNode) {
					handleDeficientCase(node);
				}
			}
		}else {
			//Data found in non-leaf node, switch to the remove of data in leaf node
			if(pos_dataToDelete >= 0) {
				Node<T> nodeToReplace = findDataToReplace(node, data);
				T dataToReplace = nodeToReplace == node.children.get(pos_dataToDelete) ? 
		                nodeToReplace.keys.get(nodeToReplace.keys.size() - 1) :
		                nodeToReplace.keys.get(0);
				node.keys.set(pos_dataToDelete, dataToReplace);
				deleteNode(nodeToReplace, dataToReplace);
			//Data not found yet, continue to search
			}else {
				pos_dataToDelete = -pos_dataToDelete - 1;
				deleteNode(node.children.get(pos_dataToDelete), data);
			}
		}
	}
	
	private void handleDeficientCase(Node<T> node) {
		int borrowFlag = canBorrowDataFromSibling(node);
		if(borrowFlag < 0) {
            mergeNode(node,borrowFlag);
		}else if(borrowFlag > 0){
			borrowDataFromSibling(node, borrowFlag);
		}else {
			if(node.keys.isEmpty() && node.children.isEmpty()) {
				throw new IllegalStateException("Internal error: root node has no children/more than 1 child");
			}
			if(node.keys.isEmpty() && !node.children.isEmpty()) {
				this.root = node.children.get(0);
				this.root.parent = null;
			}
		}
	}
	
	private int canBorrowDataFromSibling(Node<T> node) {
		if (this.root.equals(node)) {
			return 0;
		}
		Node<T> parent = node.parent;
		int pos = parent.children.indexOf(node);
		if(pos < 0) {
			throw new IllegalStateException("Node is not child of parent");
		}
		
		if(pos == parent.children.size() - 1) {
            return parent.children.get(pos - 1).keys.size() > this.minDataSizePerNode ? 1 : -1;
		}
		
		if(pos == 0) {
			return parent.children.get(1).keys.size() > this.minDataSizePerNode? 2 : -2;
		}
		
		if(parent.children.get(pos - 1).keys.size() <= this.minDataSizePerNode
			   && parent.children.get(pos + 1).keys.size() <= this.minDataSizePerNode) {
			return -1;
		}
		return parent.children.get(pos - 1).keys.size() > parent.children.get(pos + 1).keys.size() ? 1 : 2;
	}
	
	private void borrowDataFromSibling(Node<T> node, int flag) {
		Node<T> parent = node.parent;
		int indexNode = parent.children.indexOf(node);
		switch (flag) {
			case 1:
				Node<T> siblingNode1 = parent.children.get(indexNode - 1);
				T siblingData1 = siblingNode1.keys.remove(siblingNode1.keys.size() - 1);
				node.keys.add(0, parent.keys.get(indexNode - 1));
				parent.keys.set(indexNode - 1, siblingData1);
				if(!node.isLeaf) {
					Node<T> childSibling1 = siblingNode1.children.remove(siblingNode1.children.size() - 1);
					childSibling1.parent = node;
					node.children.add(0, childSibling1);
				}
				break;
			case 2:
				Node<T> siblingNode2 = parent.children.get(indexNode + 1);
				T siblingData2 = siblingNode2.keys.remove(0);
				node.keys.add(parent.keys.get(indexNode));
				parent.keys.set(indexNode, siblingData2);
				if(!node.isLeaf) {
                    Node<T> childSibling2 = siblingNode2.children.remove(0);
                    childSibling2.parent = node;
                    node.children.add(childSibling2);
				}
				break;
		}
	}
	
	private void mergeNode(Node<T> node, int borrowFlag) {
		if(borrowFlag < 0) {
			Node<T> parent = node.parent;
            if(borrowFlag == -1) {
                mergeWithLeftSibling(node, parent);
            }else {
                mergeWithRightSibling(node, parent);
            }
            if((!this.root.equals(parent) && parent.keys.size() < this.minDataSizePerNode) ||
               (this.root.equals(parent) && parent.keys.isEmpty())
            ) {
                handleDeficientCase(parent);
            }
		} else {
			throw new IllegalStateException("Node can borrow from sibling");
		}
	}
	
	private void mergeWithLeftSibling(Node<T> node, Node<T> parent) {
		if (node.parent.children.indexOf(node) == 0) {
			throw new IllegalStateException("Node is the first child of parent");
		}else {
			int indexNode = parent.children.indexOf(node);
			Node<T> leftSibling = parent.children.get(indexNode - 1);
			leftSibling.keys.add(parent.keys.remove(indexNode - 1));
			leftSibling.keys.addAll(node.keys);
			if(!node.isLeaf) { 
				node.children.stream().forEach(child -> child.parent = leftSibling);
				leftSibling.children.addAll(node.children);
			}
			parent.children.remove(node);
		}
	}
	
	private void mergeWithRightSibling(Node<T> node, Node<T> parent) {
		if (node.parent.children.indexOf(node) == node.parent.children.size() - 1) {
			throw new IllegalStateException("Node is the last child of parent");
		} else {
			int indexNode = parent.children.indexOf(node);
			Node<T> rightSibling = parent.children.get(indexNode + 1);
			node.keys.add(parent.keys.remove(indexNode));
			node.keys.addAll(rightSibling.keys);
			if (!rightSibling.isLeaf) {
				rightSibling.children.stream().forEach(child -> child.parent = node);
				node.children.addAll(rightSibling.children);
			}
			parent.children.remove(rightSibling);
		}
	}
	
	private Node<T> findDataToReplace(Node<T> node, T data){
		if(node.keys.contains(data)) {
			int index = node.keys.indexOf(data);
			Node<T> predecessor = node.children.get(index);
			Node<T> successor = node.children.get(index + 1);
			
			while (!predecessor.isLeaf && !successor.isLeaf) {
				predecessor = predecessor.children.get(predecessor.children.size() - 1);
				successor = successor.children.get(0);
			}
			if (predecessor.isLeaf && successor.isLeaf) {
				return predecessor.keys.size() >= successor.keys.size() ? predecessor : successor;
			}
		}
		throw new IllegalStateException("Data to replace not found");
	}
	
//----------------------------------------------------------------------------------------
//---------------------------------------Validation---------------------------------------
//----------------------------------------------------------------------------------------
	
	private boolean isValid() {
	    return validateNode(root, null, null);
	}

	private boolean validateNode(Node<T> node, T min, T max) {
		// check if the node's value is within the valid range
	    for (T value : node.keys) {
	        if ((min != null && value.compareTo(min) <= 0) || 
	            (max != null && value.compareTo(max) >= 0)) {
	            return false;
	        }
	    }
	    // check the size of the node is within the valid range
	    if (node != root && node.keys.size() < minDataSizePerNode) {
	        return false;
	    }
	    // check if the number of children is correct for non-leaf nodes
	    if (!node.isLeaf && node.children.size() != node.keys.size() + 1) {
	        return false;
	    }
	    // recursively validate the children nodes
	    if (!node.isLeaf) {
	        for (int i = 0; i < node.keys.size(); i++) {
	            if (!validateNode(node.children.get(i), min, node.keys.get(i)) ||
	                !validateNode(node.children.get(i + 1), node.keys.get(i), max)) {
	                return false;
	            }
	        }
	    }
	    return true;
	}
	
//----------------------------------------------------------------------------------------
//---------------------------------------Print---------------------------------------------
//----------------------------------------------------------------------------------------
	public List<T> toList() {
	    List<T> result = new ArrayList<>();
	    collectElements(root, result);
	    return result;
	}

	private void collectElements(Node<T> node, List<T> result) {
	    if (node.isLeaf) {
	        result.addAll(node.keys);
	    } else {
	        for (int i = 0; i < node.keys.size(); i++) {
	            collectElements(node.children.get(i), result);
	            result.add(node.keys.get(i));
	        }
	        collectElements(node.children.get(node.children.size()-1), result);
	    }
	}
	
	public void printPerNode() {
		printPerNode(root);
	}
	
	private void printPerNode(Node<T> node) {
		if (node.isLeaf) {
			System.out.println("Current Leaf Node: ");
			node.keys.stream().forEach(data -> System.out.print(data + " "));
			System.out.println("\n");
		} else {
			System.out.println("Current non-Leaf Node: ");
			node.keys.stream().forEach(data -> System.out.print(data + " "));
			System.out.println("\n");
			for (int i = 0; i < node.children.size(); i++) {
				printPerNode(node.children.get(i));
			}
		}
	}
	
	public static void main(String[] args) {
		//Test BTree insert
		BTree<Integer> bTree = new BTree<>(5);
		List<Integer> list = Arrays.asList(45,30,42,10,40,41,43,44,51,65,74,90,46,47,50,53,57,60,68,72,76,83,86,92,98,50);
		
		list.stream().forEach(data -> {
			bTree.insert(data);
			System.out.println("\nb-tree sturcture is " + (bTree.isValid()? "valid" : "invalid"));
		});
		System.out.println(bTree.toList());
		bTree.printPerNode();
		
		//Test BTree delete		
		List<Integer> list2 = Arrays.asList(45,60,68,90,65,72,83);
		list2.stream().forEach(data -> {
			bTree.delete(data);
			System.out.println(bTree.toList());
			System.out.println("\nb-tree sturcture is " + (bTree.isValid() ? "valid" : "invalid"));
		});
		bTree.printPerNode();
	}	
}
