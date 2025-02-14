package com.devStudy.algoSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BFS extends BaseTree{
	private static void bfs_recursion(List<Node> list) {
		if(list.isEmpty()) return;
		List<Node> allChilds = new ArrayList<>();
		for(Node cur : list) {
			System.out.println(cur.data);
			for(Node curChild : cur.listChilds) {
				allChilds.add(curChild);
			}
		}
		bfs_recursion(allChilds);
	}
	
	//变体1：使用队列实现
	private static void bfs_queue(Node node) {
		if(node == null) return;
		
		Queue<Node> queue = new LinkedList<>();
		queue.add(node);
		
		while(!queue.isEmpty()) {
			Node currNode = queue.poll();
			System.out.println(currNode.data);
			
			for(Node child : currNode.listChilds) {
				queue.add(child);
			}
		}
	}
	
	public static void main(String[] args) {
		BFS inst = new BFS();
		inst.createExampleTree();
		System.out.println("遍历：\n");
		bfs_recursion(Arrays.asList(inst.root));
		System.out.println("\n变体1：不用递归：\n");
		bfs_queue(inst.root);
	}
}
