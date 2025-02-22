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
		
		public K getMinKey() {
			return this.keys.get(0);
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
		
		public void addKeyAndChild(K key, Node<K, V> child, int index) {
			this.keys.add(index, key);
			child.parent = this;
			this.childs.add(index, child);
		}
		
		public void removeKeyAndChild(int index) {
			this.keys.remove(index);
			this.childs.remove(index);
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
		
		public void removeKeyAndValue(int index) {
			this.keys.remove(index);
			this.values.remove(index);
		}
		
		public V getMaxValue() {
			return this.values.get(this.values.size() - 1);
		}
		
		public V getMinValue() {
			return this.values.get(0);
		}
	}
	
	private Node<K, V> root;
	private LeafNode<K, V> LeafNodeEntry;
	
	B_plus_Tree(int order) {
		if(order < 3) {
            throw new IllegalArgumentException("Order should be greater than 2");
        }
		this.order = order;
		this.minKeysSizePerNode = (int) Math.ceil(order / 2.0);
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
		boolean found;
		
		SearchedResult(LeafNode<K, V> resultNode, int index, boolean found) {
			this.resultNode = resultNode;
			this.index = index;
			this.found = found;
		}
		
		public int getExactIndex(boolean ascending) {
			return found? this.index : ascending? this.index : this.index - 1;
		}
		
		public K getKey(boolean ascending) {
			return found? this.resultNode.keys.get(index) : 
				ascending? this.resultNode.keys.get(index) : this.resultNode.keys.get(index-1);
		}
		
		public V getValue() {
			if (found) {
                return this.resultNode.values.get(index);
			}else {
				System.out.println("Key not found");
				return null;
			}
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
			return node.isLeaf? new SearchedResult<>((LeafNode<K, V>) node, searchIndex, true)
			        : searchKeyFromRoot(key, ((InternalNode<K, V>) node).childs.get(searchIndex));
		}
		if (node.isLeaf) {
			//System.out.println("Key not found");
			return new SearchedResult<>((LeafNode<K, V>) node, -searchIndex-1, false);
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
	
	public List<V> searchRange(K startKey, K endKey, boolean ascending) {
		SearchedResult<K, V> start = ascending? searchKeyFromRoot(startKey, this.root) : searchKeyFromRoot(endKey, this.root);
		LeafNode<K, V> currentNode = start.resultNode;
		int currentIndex = start.getExactIndex(ascending);
		K currentKey = start.getKey(ascending);
		if(currentKey == null) {
            return Collections.emptyList();
        }
		List<V> result = new LinkedList<>();
		while(currentNode != null && (ascending? currentKey.compareTo(endKey) <= 0 : currentKey.compareTo(startKey) >= 0)) {
			result.add(currentNode.values.get(currentIndex));
			if(currentIndex == (ascending? currentNode.keys.size() - 1 : 0)) {
                currentNode = ascending? currentNode.next : currentNode.previous;
				if (currentNode == null) {
					break;
				}
                currentIndex = ascending? 0 : currentNode.keys.size()-1;
            }else {
            	currentIndex += ascending? 1 : -1;
            }
			currentKey = currentNode == null ? null : currentNode.keys.get(currentIndex);
		}
		return result;
	}
	
	public List<V> searchRange_SingleEdge(K key, boolean ascending, boolean greaterThan){
		return greaterThan? searchRange(key, this.root.getMaxKey(), ascending) :
			searchRange(this.LeafNodeEntry.getMinKey(), key, ascending);
	}
	
//-------------------------------------------------------------------------------------------
//-------------------------- Deletion methods -----------------------------------------------
//-------------------------------------------------------------------------------------------
	
	public void delete(K key) {
		if (key == null) {
			throw new IllegalArgumentException("Key should not be null");
		}
		SearchedResult<K, V> resultToDelete = searchKeyFromRoot(key, this.root);
		if (resultToDelete.getValue() == null) {
			System.out.println("Key not found");
			return;
		}
		deleteKeyFromLeaf(key, resultToDelete);
	}
	
	private void deleteKeyFromLeaf(K key, SearchedResult<K, V> resultToDelete) {
		LeafNode<K, V> node = resultToDelete.resultNode;
		K maxKeyBeforeDelete = node.getMaxKey();
		node.removeKeyAndValue(resultToDelete.index);
		if (key == maxKeyBeforeDelete) {
			updateParentKeys((InternalNode<K, V>) node.parent, maxKeyBeforeDelete, node.getMaxKey(),false);
		}
		if(node.keys.size() < this.minKeysSizePerNode) {
			handleDeficientCase(node);
		}
	}
	
	private void updateParentKeys(InternalNode<K, V> parent, K oldKey, K newKey, boolean afterMerge) {
		int index = Collections.binarySearch(parent.keys, oldKey);
		if (index < 0) {
			throw new IllegalStateException("Key not found in parent node");
		}
		parent.keys.set(index, newKey);
		if(afterMerge) {
			return;
		}
		if (parent.parent != null && index == parent.keys.size() - 1) {
			updateParentKeys((InternalNode<K, V>) parent.parent, oldKey, newKey, afterMerge);
		}
	}
	
	private void handleDeficientCase(Node<K, V> node) {
		int borrowFlag = canBorrowFromSibling(node);
		switch (borrowFlag) {
			case 1,2:
				borrowFromSibling(node,borrowFlag);
				break;
			case -1,-2:
				mergeWithSibling(node, borrowFlag);
				break;
			case 0:
				if(!node.isLeaf) {
					if(node.keys.isEmpty())
						throw new IllegalStateException("Root node should not be empty in this case");
					if (node.keys.size() == 1) {
						this.root = ((InternalNode<K, V>) node).childs.get(0);
						this.root.parent = null;
					}
				}
				break;
		}
	}
	
	private int canBorrowFromSibling(Node<K, V> node) {
		if (node == this.root) {
			return 0;
		}
		InternalNode<K, V> parent = (InternalNode<K, V>) node.parent;
		int nodeIndex = Collections.binarySearch(parent.keys, node.getMaxKey());
		if (nodeIndex < 0) {
			throw new IllegalStateException("Node not found in parent's child list");
		}
		if (nodeIndex == parent.childs.size() - 1) {
			return parent.childs.get(nodeIndex - 1).keys.size() > this.minKeysSizePerNode ? 1 : -1;
		}
		if (nodeIndex == 0) {
			return parent.childs.get(1).keys.size() > this.minKeysSizePerNode ? 2 : -2;
		}
		if (parent.childs.get(nodeIndex - 1).keys.size() <= this.minKeysSizePerNode
				&& parent.childs.get(nodeIndex + 1).keys.size() <= this.minKeysSizePerNode) {
			return -1;
		}
		return parent.childs.get(nodeIndex - 1).keys.size() > parent.childs.get(nodeIndex + 1).keys.size() ? 1 : 2;
	}
	
	private void borrowFromSibling(Node<K, V> node, int borrowFlag) {
		InternalNode<K, V> parent = (InternalNode<K,V>) node.parent;
		int nodeIndex = Collections.binarySearch(parent.keys, node.getMaxKey());
		Node<K, V> sibling = borrowFlag == 1 ? parent.childs.get(nodeIndex - 1) : parent.childs.get(nodeIndex + 1);
		K borrowedKey = borrowFlag == 1 ? sibling.getMaxKey() : sibling.getMinKey();
		if(node.isLeaf) {
			V borrowedValue = borrowFlag == 1 ? ((LeafNode<K, V>) sibling).getMaxValue()
					: ((LeafNode<K, V>) sibling).getMinValue();
			((LeafNode<K, V>) sibling).removeKeyAndValue(borrowFlag == 1 ? sibling.keys.size() - 1 : 0);
			((LeafNode<K, V>) node).addKeyAndValue(borrowedKey, borrowedValue, borrowFlag == 1 ? 0 : node.keys.size());
		}else {
			Node<K, V> borrowedChild = borrowFlag == 1
					? ((InternalNode<K, V>) sibling).childs.get(sibling.keys.size() - 1)
					: ((InternalNode<K, V>) sibling).childs.get(0);
			((InternalNode<K, V>) sibling).removeKeyAndChild(borrowFlag == 1 ? sibling.keys.size() - 1 : 0);
			((InternalNode<K, V>) node).addKeyAndChild(borrowedKey, borrowedChild,
					borrowFlag == 1 ? 0 : node.keys.size());
		}
		updateParentKeys(parent,
				borrowFlag == 1 ? borrowedKey : node.keys.get(node.keys.size() - 2),
				borrowFlag == 1 ? sibling.getMaxKey() : borrowedKey,
				false);
	}
	
	private void mergeWithSibling(Node<K, V> node, int borrowedFlag) {
		if(borrowedFlag >= 0) {
			throw new IllegalStateException("Node can borrow from sibling");
		}
		InternalNode<K, V> parent = (InternalNode<K, V>) node.parent;
		if ((borrowedFlag == -1 && parent.getMinKey() == node.getMaxKey())
				|| (borrowedFlag == -2 && parent.getMaxKey() == node.getMaxKey())) {
			throw new IllegalStateException("Node cannot merge with left/right sibling");
		}
		int nodeIndex = Collections.binarySearch(parent.keys, node.getMaxKey());
		if (nodeIndex < 0) {
			throw new IllegalStateException("Node not found in parent's child list");
		}
		Node<K, V> sibling = parent.childs.get(borrowedFlag == -1 ? nodeIndex - 1 : nodeIndex + 1);
		K oldKey = borrowedFlag == -1? sibling.getMaxKey() : node.getMaxKey();
		K newKey = borrowedFlag == -1? node.getMaxKey() : sibling.getMaxKey();
		if(node.isLeaf) {
			if(borrowedFlag == -1) {
                ((LeafNode<K, V>) sibling).addSubListOfKV(0, node.keys.size(), (LeafNode<K, V>) node);
                ((LeafNode<K, V>) sibling).next = ((LeafNode<K, V>) node).next;
                if (((LeafNode<K, V>) node).next != null) 
                    ((LeafNode<K, V>) node).next.previous = ((LeafNode<K, V>) sibling);
            }else {
				((LeafNode<K, V>) sibling).addSubListOfKV(0, sibling.keys.size(), (LeafNode<K, V>) sibling);
				((LeafNode<K, V>) node).next = ((LeafNode<K, V>) sibling).next;
				if (((LeafNode<K, V>) sibling).next != null)
					((LeafNode<K, V>) sibling).next.previous = ((LeafNode<K, V>) node);
            }
		}else {
			((InternalNode<K, V>) sibling).addAllKeysAndChilds(0, node.keys.size(), (InternalNode<K, V>) node);
		}
		parent.removeKeyAndChild(nodeIndex);
		updateParentKeys(parent, oldKey, newKey, true);
		if ((parent != this.root && parent.keys.size() < this.minKeysSizePerNode) 
				|| (parent == this.root && parent.keys.size() == 1)) {
			handleDeficientCase(parent);
		}
	}

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
		//search range from 21 to 72, ascending order
		System.out.println(bPlusTree.searchRange(21, 72, true));
		//search range from 21 to 72, descending order
		System.out.println(bPlusTree.searchRange(21, 72, false));
		//search range that is greater than 36, ascending order
		System.out.println(bPlusTree.searchRange_SingleEdge(36, true, true));
		//search range that is greater than 36, descending order
		System.out.println(bPlusTree.searchRange_SingleEdge(36, false, true));
		//search range that is less than 86, ascending order
		System.out.println(bPlusTree.searchRange_SingleEdge(86, true, false));
		//search range that is less than 86, descending order
		System.out.println(bPlusTree.searchRange_SingleEdge(86, false, false));
		
		//Test delete
		bPlusTree.delete(37);
		bPlusTree.delete(63);
		bPlusTree.delete(97);
		bPlusTree.delete(91);
		
		//Test search range after deletion
	    System.out.println(bPlusTree.searchRange(21, 72, true));
	    System.out.println(bPlusTree.searchRange_SingleEdge(50, true, true));
	}
}
