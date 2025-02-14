package com.devStudy.algoSearch;

import java.util.ListIterator;
import java.util.Stack;

public class DFS extends BaseTree{
	private static void dfs_recursion(Node node) {
		if(node == null) return;
		System.out.println(node.data);
		for(Node n : node.listChilds) {
			dfs_recursion(n);
		}
	}
	
	//变体1：寻找最深(浅)层数
	private static int dfs_findHeight_Max(Node node) {
		if(node == null) return 0;
		if(node.listChilds.isEmpty()) return 1;
		int maxHeight = 0;
		//int minHeight = Integer.MAX_VALUE;
		for(Node n : node.listChilds) {
			int branchHeight = 1;
			branchHeight += dfs_findHeight_Max(n);
			if(branchHeight >= maxHeight) maxHeight = branchHeight;
			//if(branchHeight <= minHeight) minHeight = branchHeight;
		}
		//return minHeight;
		return maxHeight;
	}
	
	private static int dfs_findHeight_Min(Node node) {
		if(node == null) return 0;
		if(node.listChilds.isEmpty()) return 1;
		int minHeight = Integer.MAX_VALUE;
		for(Node n : node.listChilds) {
			int branchHeight = 1;
			branchHeight += dfs_findHeight_Min(n);
			if(branchHeight <= minHeight) minHeight = branchHeight;
		}
		return minHeight;
	}
	
	//变体2：不使用递归
	private static void dfs_stack(Node node) {
		if(node == null) return;
		
		Stack<Node> stack = new Stack<>();
		stack.push(node);
		
		while(!stack.isEmpty()) {
			Node currentNode = stack.pop();
			System.out.println(currentNode.data);
			
			ListIterator<Node> ite = currentNode.listChilds.listIterator(currentNode.listChilds.size());
			while(ite.hasPrevious()) {
				Node child = ite.previous();
				stack.push(child);
			}
		}
	}
	
	
	public static void main(String[] args) {
		DFS inst = new DFS();
		inst.createExampleTree();
		System.out.println("遍历：\n");
		dfs_recursion(inst.root);
		System.out.println("\n变体1：\n最深深度为： "+dfs_findHeight_Max(inst.root));
		System.out.println("\n变体1：\n最浅深度为： "+dfs_findHeight_Min(inst.root));
		System.out.println("\n变体2：不用递归：\n");
		dfs_stack(inst.root);
	}
}
