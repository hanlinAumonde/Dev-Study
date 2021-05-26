#include<iostream>
#include<algorithm>
#include<vector>
#include<unordered_map>

using namespace std;

//题目26
/*
给你一个有序数组 nums ，请你 原地 删除重复出现的元素，使每个元素 只出现一次 ，返回删除后数组的新长度。

不要使用额外的数组空间，你必须在 原地 修改输入数组 并在使用 O(1) 额外空间的条件下完成

示例
输入：nums = [1,1,2]
输出：2, nums = [1,2]
解释：函数应该返回新的长度 2 ，并且原数组 nums 的前两个元素被修改为 1, 2 。不需要考虑数组中超出新长度后面的元素。

*/
//思路：指针i指向实际索引，一旦发现重复数字就直接跳到下一项
//指针count指向有效数组索引，即每次循环中输出数组的最后一位，每次循环中count之前均为不重复的
//时间复杂度 O（N）  空间复杂度 O（1）
class Solution26 {
public:
    int removeDuplicates(vector<int>& nums) {
         int n = nums.size();
         if(n<2) return n;
         int i = 0 , count = 0;
         while(i<=n-2){
             if(nums[i+1] == nums[i]){
                  i++;
                  continue;
             }
             nums[++count] = nums[++i];
         }
         return count+1;
    }
};

//-----------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------
//-----------------------------------------------------------------------------------------------------------------------------------


//题目1
/*
给定一个整数数组 nums 和一个整数目标值 target，请你在该数组中找出 和为目标值 target  的那 两个 整数，并返回它们的数组下标。

你可以假设每种输入只会对应一个答案。但是，数组中同一个元素在答案里不能重复出现。

你可以按任意顺序返回答案。·
*/
//解法1 暴力枚举
class Solution {
public:
    vector<int> twoSum(vector<int>& nums, int target) {
        for(int i=0;i<nums.size();i++){
            for(int j=0;j<nums.size();j++){
                if(nums[i]+nums[j]==target && j!=i){
                    return vector<int>{i,j};
                }
            }
        }
        return vector<int>();
    }
};
//最优解 hash
/*
注意到方法一的时间复杂度较高的原因是寻找 target - x 的时间复杂度过高。因此，我们需要一种更优秀的方法，能够快速寻找数组中是否存在目标元素。如果存在，我们需要找出它的索引。

使用哈希表，可以将寻找 target - x 的时间复杂度降低到从 O(N)O(N) 降低到 O(1)O(1)。

这样我们创建一个哈希表，对于每一个 x，我们首先查询哈希表中是否存在 target - x，然后将 x 插入到哈希表中，即可保证不会让 x 和自己匹配。
*/
class Solution {
public:
    vector<int> twoSum(vector<int>& nums, int target) {
        unordered_map<int, int> hashtable;
        for (int i = 0; i < nums.size(); ++i) {
            auto it = hashtable.find(target - nums[i]);
            //此处搜索的是key值，即hashmap(target-nums[i])
            if (it != hashtable.end()) {
                return {it->second, i};
            }
            hashtable[nums[i]] = i;//将索引值i保存到hashmap的索引值nums[i]处
            //当输入的key值为负（比如nums[i] = -10时），hash变换可以将key值转换成一个正整数
            //然后再将这个正整数通过和数组长度进行取余来确定数据最终存储的位置
            //这样下次寻找时可以通过key值直接定位数据存储的位置从而读取数据
            //因此hash表的优势就在于快速的搜索目标索引值，其可以快速定位到某个索引值(不需要遍历)，时间复杂度可以缩短到几乎为O(1)
        }
        return {};
    }
};


