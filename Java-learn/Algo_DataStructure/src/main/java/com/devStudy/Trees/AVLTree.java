package com.devStudy.Trees;

public class AVLTree<T extends Comparable<T>> {
	private class Node {
        T data;
        Node left, right;
        int height;  // 节点高度
        
        Node(T data) {
            this.data = data;
            this.height = 1;
        }
    }
    
    private Node root;
    
    // 获取节点高度
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }
    
    // 获取平衡因子
    private int getBalanceFactor(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }
    
    // 更新节点高度
    private void updateHeight(Node node) {
        if (node != null) {
            node.height = Math.max(height(node.left), height(node.right)) + 1;
        }
    }
    
    //旋转操作
    private Node rotateRight(Node node) {
    	System.out.println("触发右旋转");
    	
        Node nodeLeft = node.left;
        Node nodeLeftRight = nodeLeft.right;

        // 执行旋转
        nodeLeft.right = node;
        node.left = nodeLeftRight;

        return nodeLeft;  // 新的根节点
    }
    
    private Node rotateLeft(Node node) {
    	System.out.println("触发左旋转");
    	
        Node nodeRight = node.right;
        Node nodeRightLeft = nodeRight.left;

        // 执行旋转
        nodeRight.left = node;
        node.right = nodeRightLeft;

        return nodeRight;  // 新的根节点
    }
    
    // 插入节点
    public void insertNode(T data) {
        root = insert(root, data);
    }
    
    private Node insert(Node node, T data) {
        // 常规BST插入
        if (node == null) {
            return new Node(data);
        }
        
        if (data.compareTo(node.data) < 0) {
            node.left = insert(node.left, data);
        } else if (data.compareTo(node.data) > 0) {
            node.right = insert(node.right, data);
        } else {
            return node;
        }
        
        // 更新高度
        updateHeight(node);
        
        // 获取平衡因子
        int balance = getBalanceFactor(node);
        
        // 处理不平衡情况
        // LL
        if (balance > 1 && data.compareTo(node.left.data) < 0) {
            return rotateRight(node);
        }
        
        // RR
        if (balance < -1 && data.compareTo(node.right.data) > 0) {
            return rotateLeft(node);
        }
        
        // LR
        if (balance > 1 && data.compareTo(node.left.data) > 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        
        // RL
        if (balance < -1 && data.compareTo(node.right.data) < 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        
        return node;
    }
    
    private boolean searchNode(T dataSearch) {
    	return search(root, dataSearch) == null? false : true;
    }
    
    private Node search(Node node, T dataSearch) {
    	if(node ==null) return null;
    	if(dataSearch.compareTo(node.data) < 0) {
    		return search(node.left, dataSearch);
    	}else if(dataSearch.compareTo(node.data) > 0) {
    		return search(node.right, dataSearch);
    	}
    	return node; 	
    }
    
    //删除节点
    private void deleteNode(T dataDelete) {
    	root = delete(root, dataDelete);
    }
    
    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }
    
    private Node delete(Node node, T dataDelete) {
    	if(node == null) return null;
    	if(dataDelete.compareTo(node.data) < 0) {
    		node.left = delete(node.left, dataDelete);
    	}else if(dataDelete.compareTo(node.data) > 0){
    		node.right = delete(node.right, dataDelete);
    	}else {
    		// 情况1：叶子节点
            if (node.left == null && node.right == null) {
                return null;
            }
            // 情况2：只有一个子节点
            else if (node.left == null) {
                return node.right;
            } else if (node.right == null) {
                return node.left;
            }
            // 情况3：有两个子节点
            else {
                // 找到右子树中最小的节点
                Node minNode = findMin(node.right);
                // 用最小节点的值替换当前节点
                node.data = minNode.data;
                // 删除最小节点
                node.right = delete(node.right, minNode.data);
            }
    	}
    	
    	// 更新高度
        updateHeight(node);
        
        // 获取平衡因子
        int balance = getBalanceFactor(node);
        
        // 处理不平衡情况
        // LL
        if (balance > 1 && getBalanceFactor(node.left) >= 0) {
            return rotateRight(node);
        }
        
        // RR
        if (balance < -1 && getBalanceFactor(node.right) <= 0) {
            return rotateLeft(node);
        }
        
        // LR
        if (balance > 1 && getBalanceFactor(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        
        // RL
        if (balance < -1 && getBalanceFactor(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }
        
        return node;
    }
    
    // 中序遍历（得到排序序列）
    public void inorderTraversal() {
        inorder(root);
    }

    private void inorder(Node node) {
        if (node != null) {
            inorder(node.left);
            System.out.print(node.data + " ");
            inorder(node.right);
        }
    }
    
    public static void main(String[] args) {
        AVLTree<Integer> bst = new AVLTree<>();
        
        // 插入节点
        bst.insertNode(50);
        bst.insertNode(40);
        bst.insertNode(60);
        bst.insertNode(55);
        bst.insertNode(30);
        bst.insertNode(45);
        bst.insertNode(25);
        
        //触发旋转
        bst.insertNode(26);
        bst.insertNode(24);
        
        // 中序遍历（将输出有序序列）
        System.out.println("中序遍历：");
        bst.inorderTraversal();  
        
        // 查找节点
        System.out.println("\n查找40：" + bst.searchNode(40));  // true
        System.out.println("查找90：" + bst.searchNode(90));  // false
        
        // 删除节点
        bst.deleteNode(55);
        System.out.println("删除30后的中序遍历：");
        bst.inorderTraversal();  
    }
    
}
