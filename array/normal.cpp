#include <iostream>
#include <vector>

using namespace std;

//题目289
/*
根据 百度百科 ，生命游戏，简称为生命，是英国数学家约翰·何顿·康威在 1970 年发明的细胞自动机。

给定一个包含 m × n 个格子的面板，每一个格子都可以看成是一个细胞。每个细胞都具有一个初始状态：1 即为活细胞（live），或 0 即为死细胞（dead）。每个细胞与其八个相邻位置（水平，垂直，对角线）的细胞都遵循以下四条生存定律：

如果活细胞周围八个位置的活细胞数少于两个，则该位置活细胞死亡；
如果活细胞周围八个位置有两个或三个活细胞，则该位置活细胞仍然存活；
如果活细胞周围八个位置有超过三个活细胞，则该位置活细胞死亡；
如果死细胞周围正好有三个活细胞，则该位置死细胞复活；
下一个状态是通过将上述规则同时应用于当前状态下的每个细胞所形成的，其中细胞的出生和死亡是同时发生的。给你 m x n 网格面板 board 的当前状态，返回下一个状态。
*/
//我的最初解法 费时费力型（
//思路，直接额外复制一个表格，并在边界上包裹一圈，利用这个表格的数据来计算新表
class Solution_289_mine {
public:
    void gameOfLife(vector<vector<int>>& board) {
        vector<vector<int>> copy_orig;
        copy_orig.push_back(vector<int>(board[0].size()+2,0));
        for(int i=0;i<board.size();i++){
            copy_orig.push_back(vector<int>());
            copy_orig[i+1].push_back(0);
            copy_orig[i+1].insert(copy_orig[i+1].end(),board[i].begin(),board[i].end());
            copy_orig[i+1].push_back(0);
        }
        copy_orig.push_back(vector<int>(board[0].size()+2,0));
        for(int line=0;line<board.size();line++){
            for(int col=0;col<board[line].size();col++){
                if(board[line][col] == 0){
                    if(count(copy_orig,line+1,col+1,board[line][col]) == 3){
                           board[line][col] = 1;
                    }
                }else{
                    if(count(copy_orig,line+1,col+1,board[line][col]) == 2 || count(copy_orig,line+1,col+1,board[line][col]) == 3){
                           board[line][col] = 1;
                    }else{board[line][col] = 0;}
                }
            }
        }
    }

    int count(vector<vector<int>> ori,int pos_i,int pos_j,int value){
        int sum = 0;
        for(int i=pos_i-1;i<pos_i+2;i++){
            for(int j=pos_j-1;j<pos_j+2;j++){
                sum += ori[i][j];
            } 
        }
        return sum-value;
    }
};
//最优解
//对于矩阵内每个数据进行一定处理使得其能够同时表示之前和之后两种状态
class Solution_289_best {
public:
    void gameOfLife(vector<vector<int>>& board) {
        //活的仍然活着：1
        //活的死了：10
        //死的还是死的：0
        //死的活了：100
        vector<vector<int>> aa = board;
        int m = board.size();
        int n = board[0].size();
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                int alive = count(board,i,j,board[i][j]);
                if(board[i][j] == 0 && alive == 3) board[i][j] = 100;
                if(board[i][j] == 1 && (alive > 3 || alive < 2)) board[i][j] = 10;
            }
        }
        for(int i=0;i<m;i++){
            for(int j=0;j<n;j++){
                if(board[i][j] == 10) board[i][j] = 0;
                if(board[i][j] == 100) board[i][j] = 1;
            }
        }
    }

    int count(vector<vector<int>>& ori,int pos_i,int pos_j,int value){
        int sum = 0;
        int m = ori.size();
        int n = ori[0].size();
        for(int i=pos_i-1;i<pos_i+2;i++){
            for(int j=pos_j-1;j<pos_j+2;j++){
                //四个角,两排横边,两排竖边，去掉所有边界条件
                if((i == -1 && j == -1) || (i == m && j == -1) || (i == m && j == n) || (i == -1 && j == n) || (i == -1 && j >= 0 && j <= n) || (i == m && j >= 0 && j <= n) || (j == -1 && i >=0 && i <= m) || (j == n && i >= 0 && i <= m)){
                    continue;
                }
                sum += ori[i][j]; 
            } 
        }
        return ((sum - value) % 10) + (((sum - value) % 100) / 10);
    }
};

//
