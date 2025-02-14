package com.devStudy.algoSearch;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class PathFinder {
	// 方向数组：上、右、下、左
    private static final int[][] DIRECTIONS = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
    
    private List<Point> pathFound;
    
    static class Point {
        int x, y;
        List<Point> path;  // 记录路径
        
        Point(int x, int y, List<Point> path) {
            this.x = x;
            this.y = y;
            this.path = new ArrayList<>(path);
            this.path.add(this);
        }
    }
    
    //队列循环版本
    public List<Point> findPathBFS(int[][] grid, Point start) {
        int n = grid.length;
        int m = grid[0].length;
        
        // BFS
        Queue<Point> queue = new LinkedList<>();
        boolean[][] visited = new boolean[n][m];
        queue.offer(start);
        visited[start.x][start.y] = true;
        
        while (!queue.isEmpty()) {
            Point current = queue.poll();
            
            // 找到终点
            if (grid[current.x][current.y] == 3) {
                return current.path;
            }
            
            // 探索四个方向
            for (int[] dir : DIRECTIONS) {
                int newX = current.x + dir[0];
                int newY = current.y + dir[1];
                
                if (isValid(newX, newY, n, m) && !visited[newX][newY] 
                    && grid[newX][newY] != 2) {
                    visited[newX][newY] = true;
                    queue.offer(new Point(newX, newY, current.path));
                }
            }
        }
        
        return null;  // 没找到路径
    }
    
    private boolean isValid(int x, int y, int n, int m) {
        return x >= 0 && x < n && y >= 0 && y < m;
    }
    
    //递归版本
    private  void BFSRecur(int[][] grid, Point start) {
    	int n = grid.length;
    	int m = grid[0].length;
    	boolean[][] visited = new boolean[n][m];
    	visited[start.x][start.y] = true;
    	findPathBFS_recur(grid,Arrays.asList(start),visited);
    }
    
    private void findPathBFS_recur(int[][] grid, List<Point> list, boolean[][] visited) {
    	int n = grid.length;
    	int m = grid[0].length;
    	List<Point> allNextPoints = new ArrayList<>();
    	for(Point p : list) {
    		// 找到终点
            if (grid[p.x][p.y] == 3) {
                pathFound = p.path;
                return;
            }
            // 探索四个方向
            for (int[] dir : DIRECTIONS) {
            	int newX = p.x + dir[0];
                int newY = p.y + dir[1];
                if (isValid(newX, newY, n, m) && !visited[newX][newY] && grid[newX][newY] != 2) {
                	visited[newX][newY] = true;
                	allNextPoints.add(new Point(newX,newY,p.path));
                }
            }
    	}
    	findPathBFS_recur(grid,allNextPoints,visited);
    }
    
    public static void main(String[] args) {
        int[][] grid = {
            {1, 1, 0, 1, 1},
            {1, 2, 1, 2, 1},
            {1, 1, 2, 1, 1},
            {1, 1, 1, 3, 1}
        };
        
        PathFinder finder = new PathFinder();
        Point start = new Point(0,1,Arrays.asList());
        
        // 测试BFS
        List<Point> bfsPath = finder.findPathBFS(grid,start);
        System.out.println("BFS路径：");
        if (bfsPath == null) {
            System.out.println("没有找到路径");
            return;
        }
        
        for (Point p : bfsPath) {
            System.out.printf("(%d,%d) ", p.x, p.y);
        }
        
        //测试BFS_recur
        finder.BFSRecur(grid, start);
        List<Point> bfsPath1 = finder.pathFound;
        System.out.println("\nBFSRecur路径：");
        if (bfsPath1 == null) {
            System.out.println("没有找到路径");
            return;
        }
        
        for (Point p : bfsPath1) {
            System.out.printf("(%d,%d) ", p.x, p.y);
        }
        
    }
}
