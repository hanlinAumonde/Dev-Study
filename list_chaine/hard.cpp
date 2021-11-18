#include<iostream>
#include<vector>

using namespace std;

//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------
//-------------------------------------------------------------------------------------------

//题目 23 : 合并K个升序链表
/*
给你一个链表数组，每个链表都已经按升序排列。
请你将所有链表合并到一个升序链表中，返回合并后的链表。
*/

class Solution {
public:
    ListNode* mergeKLists(vector<ListNode*>& lists) {
        //解法1 暴力 内存消耗少但慢的一批
        ListNode* result = new ListNode(0,nullptr);
        ListNode* current_res = result; 
        int min_index = -1;
        bool empty = false;
        while(!empty){
            empty = true;
            int min = 100000;
            ListNode* min_node = nullptr;
            for(int i=0;i<lists.size();i++){
                if(lists[i]!=nullptr){
                     if(lists[i]->val < min){
                         min = lists[i]->val;
                         min_node = lists[i];
                         min_index = i;
                     }
                }             
            }
            if(min_node != nullptr){
                empty = false;
                lists[min_index] = lists[min_index]->next;
                current_res->next = min_node;
                current_res = current_res->next;
                current_res->next = nullptr;
            }
        }
        result = result->next;
        return result;
        
        
        
        
    }
    ListNode* mergeKLists1(vector<ListNode*>& lists) {
        //解法2 两两合并 快了一点但反而整体来看速度内存都一般
        ListNode* result = nullptr;
        int n = lists.size();
        if(n == 0) return result;
        else if(n == 1) return lists[0];
        result = combine2(lists[0],lists[1]);
        if(n == 2) return result;
        for(int i=2;i<n;i++){
            result = combine2(result,lists[i]);
        }
        return result;
        
    }

    //解法2 两两合并 
    
    ListNode* combine2(ListNode* list1 , ListNode* list2){
        ListNode* result = new ListNode();
        ListNode* ptr1 = list1;
        ListNode* ptr2 = list2;
        //if(list1->val <= list2->val) result = ptr1;
        //else result = ptr2;
        ListNode* current = result;
        while(ptr1 != nullptr || ptr2 != nullptr){
            if(ptr1 == nullptr && ptr2 != nullptr){
                  current->next = ptr2;
                  break;
            }
            if(ptr1 != nullptr && ptr2 == nullptr){
                  current->next = ptr1;
                  break;
            }
            if(ptr1->val <= ptr2->val){
                  current->next = ptr1;
                  ptr1 = ptr1->next;
            }
            else{
                  current->next = ptr2;
                  ptr2 = ptr2->next;
            }
            current = current->next;
        }
        result = result->next;
        return result;
    }
    
};
