#include<iostream>

using namespace std;

//题目：面试题 02.03
/*
若链表中的某个节点，既不是链表头节点，也不是链表尾节点，则称其为该链表的「中间节点」。

假定已知链表的某一个中间节点，请实现一种算法，将该节点从链表中删除。

例如，传入节点 c（位于单向链表 a->b->c->d->e->f 中），将其删除后，剩余链表为 a->b->d->e->f
*/

struct ListNode {
      int val;
      ListNode *next;
      ListNode(int x) : val(x), next(NULL) {}
  };

class Solution_0203 {
public:
    void deleteNode(ListNode* node) {
        
        //ListNode* Next_reel = Next_delete->next;
        node->val = node->next->val;
        auto Next_delete = node->next;
        node->next = Next_delete->next;
        delete Next_delete;
    }
};

//-----------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------

//题目1290.
/*
给你一个单链表的引用结点 head。链表中每个结点的值不是 0 就是 1。已知此链表是一个整数数字的二进制表示形式。

请你返回该链表所表示数字的 十进制值 。

如：1 -》 0 -》 1 ==》101 ==》 5
*/
//我的做法（暴力做法）
class Solution_1290_mine {
public:
    int getDecimalValue(ListNode* head) {
          vector<int> vals;
          int count = 0;
          while(head != nullptr){
              count++;
              vals.push_back(head->val);
              head = head->next;
          }
          
          int result = 0;
          for(int i=0;i<count;i++){
              result += vals[i] * pow2(count-1-i);
          }
          return result;
    }
    
    int pow2(int x){
        if(x == 0) return 1;
        else{
            int res = 1;
            for(int i=0;i<x;i++){
                res = res * 2; 
            }
            return res;
        }
    }
};
//最优解1
/*
思路：每读取链表的一个节点值，可以认为读到的节点值是当前二进制数的最低位；
当读到下一个节点值的时候，需要将已经读到的结果乘以 22，再将新读到的节点值当作当前二进制数的最低位；
如此进行下去，直到读到了链表的末尾。
时间复杂度 O（N）  空间复杂度 O（1）
*/
class Solution_1290_best {
public:
    int getDecimalValue(ListNode* head) {
        ListNode* cur = head;
        int ans = 0;
        while (cur != nullptr) {
            ans = ans * 2 + cur->val;
            cur = cur->next;
        }
        return ans;
    }
};
//最优解2 位运算
//思路： 每读取一位就将这位bit左移一位从而可以直接得到结果
class Solution1290_meilleur_2 {
public:
    int getDecimalValue(ListNode* head) {
        int res = 0;
        while(head) {
            res = (res << 1) + head->val;
            head = head->next;
        }
        return res;
    }
};

//-----------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------


