#include<iostream>

using namespace std;

//--------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------
//--------------------------------------------------------------------------------------------

//题目563 ： 二叉树的坡度
/*
给定一个二叉树，计算整个树的坡度 。

一个树的节点的坡度定义即为，该节点左子树的节点之和和右子树节点之和的差的绝对值 。如果没有左子树的话，左子树的节点之和为 0 ；没有右子树的话也是一样。空结点的坡度是 0 。

整个树的坡度就是其所有节点的坡度之和。
*/

/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode() : val(0), left(nullptr), right(nullptr) {}
 *     TreeNode(int x) : val(x), left(nullptr), right(nullptr) {}
 *     TreeNode(int x, TreeNode *left, TreeNode *right) : val(x), left(left), right(right) {}
 * };
 */

//我的解法
//使用左树和右树两个递归遍历每个节点，逐步记录节点val之和并计算各个节点坡度之和

class Solution {
public:
    int findTilt(TreeNode* root) {
        if(!root) return 0;
        if(!root->left && !root->right) return 0;
        int tilt_sum_left = 0 , tilt_sum_right = 0;
        int val_sum_left = 0 , val_sum_right = 0;
        if(root->left){
            tilt_sum_left = findTilt(root->left);
            val_sum_left = root->left->val;
        }
        if(root->right){
            tilt_sum_right = findTilt(root->right);
            val_sum_right = root->right->val;
        }
        root->val = root->val + val_sum_left + val_sum_right;
        int tilt_self = abs(val_sum_left - val_sum_right);
        return tilt_self + tilt_sum_left + tilt_sum_right;
    }
};
