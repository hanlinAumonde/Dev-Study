#include<iostream>
#include<vector>
#include<unordered_map>

using namespace std;

//----------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------
//----------------------------------------------------------------------------------------------

//题目 391 : 完美矩形
/*
给你一个数组 rectangles ，其中 rectangles[i] = [xi, yi, ai, bi] 表示一个坐标轴平行的矩形。
这个矩形的左下顶点是 (xi, yi) ，右上顶点是 (ai, bi) 。
如果所有矩形一起精确覆盖了某个矩形区域，则返回 true ；否则，返回 false 。
*/

//我的解法
//遍历每一个矩阵，统计每个矩阵的四个顶点位置出现的次数，
//如果四个顶点各自只出现一次，且其他顶点只出现二或四次，则证明该组合矩阵为一个完美的矩阵
class Solution {
public:
    bool isRectangleCover(vector<vector<int>>& rectangles) {
        pair<int,int> min{10000,10000};
        pair<int,int> max{-10000,-10000};
        map<pair<int,int>,int> hashmap;
        long long int sum_surface = 0;
        long long int rect_whole = 0;
        for(const auto &rect : rectangles){
            sum_surface += (long long int)(rect[2] - rect[0]) * (rect[3] - rect[1]);
            vector<pair<int,int>> pts;
            pts.push_back({rect[0],rect[3]});
            pts.push_back({rect[0],rect[1]});
            pts.push_back({rect[2],rect[3]});
            pts.push_back({rect[2],rect[1]});
            if(rect[0] <= min.first && rect[1] <= min.second){
                min = pts[1];
            }
            if(rect[2] >= max.first && rect[3] >= max.second){
                max = pts[2];
            }
            if(rectangles.size() == 1) break;
            for(int i=0;i<4;i++){
                ++hashmap[pts[i]]; 
            }
        } 
        rect_whole = (long long int)(max.second - min.second) * (max.first - min.first);
        bool decision = true;
        for(const auto& [x,y] : hashmap){
            if((x==max || x==min || x==make_pair(min.first,max.second) || x==make_pair(max.first,min.second)) && y!=1) decision = false;
            else if(!(x==max || x==min || x==make_pair(min.first,max.second) || x==make_pair(max.first,min.second)) && (y % 2 != 0 || y>4)) decision = false;
        }
        if(sum_surface == rect_whole && decision) return true;
        else return false;
    }
};
