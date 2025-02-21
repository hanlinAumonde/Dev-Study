package com.devStudy.Trees;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class B_plus_Tree<K extends Comparable<K>,V> {
	private int order;
	private int minKeysSizePerNode;
	private int splitIndex;
	
//----------------------------------------------------------------------------------
//-------------------------- Node classes ------------------------------------------
//----------------------------------------------------------------------------------
	abstract static class Node<K extends Comparable<K>, V> {
		boolean isLeaf;
		Node<K, V> parent;
		List<K> keys;
		
		Node(boolean isLeaf, Node<K, V> parent) {
			this.isLeaf = isLeaf;
            this.parent = parent;
            this.keys = new LinkedList<>();
		}
		
		public K getMaxKey() {
			return this.keys.get(this.keys.size() - 1);
		}
		
		public void setMaxKey(K key) {
			this.keys.set(this.keys.size() - 1, key);
		}
	}
	
	static class InternalNode<K extends Comparable<K>,V> extends Node<K,V> {
		List<Node<K, V>> childs;
		
		InternalNode(Node<K, V> parent) {
			super(false, parent);
			this.childs = new LinkedList<>();
		}
		
		public void addAllKeysAndChilds(int startIndex, int endIndex, InternalNode<K, V> internalNode) {
            this.keys.addAll(internalNode.keys.subList(startIndex, endIndex));
            List<Node<K, V>> childs = internalNode.childs.subList(startIndex, endIndex);
            childs.stream().forEach(child -> 
            	child.parent = this
            );
            this.childs.addAll(childs);
		}
	}
	
	static class LeafNode<K extends Comparable<K>, V> extends Node<K, V> {
		List<V> values;
		LeafNode<K, V> next;
		LeafNode<K, V> previous;

		LeafNode(Node<K, V> parent) {
			super(true, parent);
			this.values = new LinkedList<>();
			this.next = null;
			this.previous = null;
		}
		
		//To operate the leaf node, we only use these methods
		public void addKeyAndValue(K key, V value, int index) {
			this.keys.add(index, key);
			this.values.add(index, value);
		}
		
		public void addSubListOfKV(int startIndex, int endIndex, LeafNode<K, V> leafNode) {
			this.keys.addAll(leafNode.keys.subList(startIndex, endIndex));
			this.values.addAll(leafNode.values.subList(startIndex, endIndex));
		}
	}
	
	private Node<K, V> root;
	private LeafNode<K, V> LeafNodeEntry;
	
	B_plus_Tree(int order) {
		if(order < 3) {
            throw new IllegalArgumentException("Order should be greater than 2");
        }
		this.order = order;
		this.minKeysSizePerNode = (int) Math.floor(order / 2.0);
		this.splitIndex = (int) Math.ceil(order / 2.0);
		this.root = new LeafNode<>(null);
		this.LeafNodeEntry = (LeafNode<K, V>) this.root;
	}
	
//----------------------------------------------------------------------------------
//-------------------------- Insertion methods -------------------------------------
//----------------------------------------------------------------------------------
	
	public void insert(K key, V value) {
		if (key == null || value == null) {
			throw new IllegalArgumentException("Key and value should not be null");
		}
		insertData(key, value, root);
	}
	
	private void insertData(K key, V value, Node<K, V> node) {
		if(node.isLeaf) {
			insertLeafNode(key, value, node);
		}else {
			int indexPos = Collections.binarySearch(node.keys, key);
            if(indexPos < 0) {
                indexPos = indexPos == -1 - node.keys.size() ? node.keys.size()-1 : -indexPos - 1;
            }else {
            	System.out.println("Key already exists");
            	return;
            }

            //when inserting a key that is greater than the max key of the whole tree,
            //we need to update the max key of all corresponding nodes on the rightmost side
            if(key.compareTo(node.getMaxKey()) > 0) {
            	node.setMaxKey(key);
            }
            
            insertData(key, value, ((InternalNode<K, V>) node).childs.get(indexPos));
		}
	}
	
	private void insertLeafNode(K key, V value, Node<K, V> leafNode) {
		LeafNode<K,V> leaf = (LeafNode<K, V>) leafNode;
		int indexPos = Collections.binarySearch(leaf.keys, key);
		if(indexPos < 0) {
            indexPos = -indexPos - 1;
        }else {
		    System.out.println("Key already exists");
			return;
        }
		leaf.addKeyAndValue(key, value, indexPos);
		
		if (leaf.keys.size() == order + 1) {
			splitNode(leaf);
		}
		System.out.println("Key " + key + " inserted successfully");
	}
	
	private void splitNode(Node<K, V> node) {
		// A parent node is always an internal node, or null if the node is the root
		InternalNode<K, V> parent = node == this.root ? null : (InternalNode<K, V>) node.parent;
		if(parent == null) {
			parent = new InternalNode<>(null);
			this.root = parent;
		}
		if(node.isLeaf) {
			LeafNode<K, V> currentNode = (LeafNode<K, V>) node;
			LeafNode<K, V> leftNode = new LeafNode<>(parent);
			LeafNode<K, V> rightNode = new LeafNode<>(parent);
			leftNode.addSubListOfKV(0, splitIndex, currentNode);
			rightNode.addSubListOfKV(splitIndex, node.keys.size(), currentNode);
			
			if (node == this.LeafNodeEntry) {
				this.LeafNodeEntry = leftNode;
			}
			leftNode.next = rightNode;
			leftNode.previous = currentNode.previous;
			if (currentNode.previous != null) {
				currentNode.previous.next = leftNode;
			}
			
			rightNode.previous = leftNode;
			rightNode.next = currentNode.next;
			if (currentNode.next != null) {
				currentNode.next.previous = rightNode;
            }
			
			updateParentChildList(currentNode, parent, leftNode, rightNode);
			
		} else {
			InternalNode<K, V> currentNode = ((InternalNode<K, V>) node);
			InternalNode<K, V> leftNode = new InternalNode<>(parent);
			InternalNode<K, V> rightNode = new InternalNode<>(parent);
			
			leftNode.addAllKeysAndChilds(0, splitIndex, currentNode);
			rightNode.addAllKeysAndChilds(splitIndex, currentNode.keys.size(), currentNode);
			updateParentChildList(currentNode, parent, leftNode, rightNode);
		}
	}
	
	private void updateParentChildList(Node<K,V> current, InternalNode<K, V> parent, Node<K, V> leftNode, Node<K, V> rightNode) {
		if (!parent.childs.isEmpty()) {
			int nodeIndex = Collections.binarySearch(parent.keys, current.getMaxKey());
			if (nodeIndex < 0) {
				throw new IllegalStateException("Node not found in parent's child list");
			}
			parent.childs.set(nodeIndex, leftNode);
			parent.keys.set(nodeIndex, leftNode.getMaxKey());
			
			parent.childs.add(nodeIndex + 1, rightNode);
			parent.keys.add(nodeIndex + 1, rightNode.getMaxKey());
		}else {
			parent.childs.add(leftNode);
			parent.childs.add(rightNode);
			
			parent.keys.add(leftNode.getMaxKey());
			parent.keys.add(rightNode.getMaxKey());
		}
		
		if (parent.keys.size() == order + 1) {
			splitNode(parent);
		}
	}
	
//------------------------------------------------------------------------------------------------------------------------
//-------------------------- Search methods ------------------------------------------------------------------------------
//------------------------------------------------------------------------------------------------------------------------
	
	static class SearchedResult<K extends Comparable<K>,V>{
		LeafNode<K, V> resultNode;
		int index;
		
		SearchedResult(LeafNode<K, V> resultNode, int index) {
			this.resultNode = resultNode;
			this.index = index;
		}
		
		public K getKey() {
			return index != this.resultNode.keys.size() ? this.resultNode.keys.get(index) : null;
		}
		
		public V getValue() {
			if (index != this.resultNode.keys.size()) {
                return this.resultNode.values.get(index);
			}else {
				System.out.println("Key not found");
				return null;
			}
			//return index != this.resultNode.keys.size()? this.resultNode.values.get(index) : null;
		}
	}
	//----------------Search a single key-----------------
	
	//Search from the root node
	public V searchFromRoot(K key) {
		return searchKeyFromRoot(key, this.root).getValue();
	}
	
	private SearchedResult<K, V> searchKeyFromRoot(K key, Node<K, V> node) {
		int searchIndex = Collections.binarySearch(node.keys, key);
		if(searchIndex >= 0) {
			return node.isLeaf? new SearchedResult<>((LeafNode<K, V>) node, searchIndex)
			        : searchKeyFromRoot(key, ((InternalNode<K, V>) node).childs.get(searchIndex));
		}
		if (node.isLeaf) {
			//System.out.println("Key not found");
			return new SearchedResult<>((LeafNode<K, V>) node, -searchIndex-1);
		}
		searchIndex = (searchIndex == -1 - node.keys.size())? node.keys.size() - 1 : -searchIndex - 1;
		return searchKeyFromRoot(key, ((InternalNode<K, V>) node).childs.get(searchIndex));
	}
	
	//Search from leaf node pointer
	public V searchFromLeaf(K key) {
		return searchKeyFromLeaf(key, this.LeafNodeEntry);
    }
	
	private V searchKeyFromLeaf(K key, LeafNode<K, V> node) {
		int searchIndex = Collections.binarySearch(node.keys, key);
		if (searchIndex >= 0) {
			return node.values.get(searchIndex);
		}
		if (node.next == null) {
			System.out.println("Key not found");
			return null;
		}
		return searchKeyFromLeaf(key, node.next);
	}
	
	//----------------Search a range of keys-----------------
	
	public List<V> searchRange(K startKey, K endKey){
		SearchedResult<K, V> start = searchKeyFromRoot(startKey, this.root);
		LeafNode<K, V> currentNode = start.resultNode;
		int currentIndex = start.index;
		K currentKey = start.getKey();
		if(currentKey == null) {
            return Collections.emptyList();
        }
		List<V> result = new LinkedList<>();
		while(currentNode != null && currentKey.compareTo(endKey) <= 0) {
			result.add(currentNode.values.get(currentIndex));
			if(currentIndex == currentNode.keys.size() - 1) {
                currentNode = currentNode.next;
                currentIndex = 0;
            }else {
            	currentIndex++;
            }
			currentKey = currentNode == null ? null : currentNode.keys.get(currentIndex);
		}
		return result;
	}
	
//-------------------------------------------------------------------------------------------
//-------------------------- Deletion methods -----------------------------------------------
//-------------------------------------------------------------------------------------------
	
	

//-------------------------------------------------------------------------------------------
//-------------------------- Print method ---------------------------------------------------
//-------------------------------------------------------------------------------------------
	
	public void printTreeByNode() {
		printPerNode(this.root);
	}
	
	private void printPerNode(Node<K, V> node) {
		if (node.isLeaf) {
			LeafNode<K, V> leafNode = (LeafNode<K, V>) node;
			System.out.println("Leaf Node: ");
			leafNode.keys.stream().forEach(key -> {
				System.out.println(key + " : " + leafNode.values.get(leafNode.keys.indexOf(key)));
			});
		} else {
			InternalNode<K, V> internalNode = (InternalNode<K, V>) node;
			System.out.println("Internal Node: ");
			internalNode.keys.stream().forEach(key -> {
				System.out.println(key);
			});
			internalNode.childs.stream().forEach(child -> {
				printPerNode(child);
			});
		}
	}
	
	public static void main(String[] args) {
		//Test the B+ tree
		String prefixString = "Test - Saved value:";
		B_plus_Tree<Integer, String> bPlusTree = new B_plus_Tree<>(4);
		List<Integer> keys = Arrays.asList(10,15,21,37,44,51,59,63,72,85,91,97);
		keys.stream().forEach(key -> {
			bPlusTree.insert(key, prefixString + key);
		});
		//bPlusTree.printTreeByNode();
		//Test search
		System.out.println(bPlusTree.searchFromRoot(10));
		System.out.println(bPlusTree.searchFromRoot(63));
		System.out.println(bPlusTree.searchFromLeaf(51));
		
		//Test search range
		System.out.println(bPlusTree.searchRange(17, 64));
	}
}
