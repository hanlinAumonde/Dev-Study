#include "tp3.h"



//Q1

static liste_ligne creerNode(int val, int col) {
    liste_ligne node = malloc(sizeof(struct element));
    node->col = col;
    node->val = val;
    node->next = NULL;
    return node;
}

static void creerLigne(liste_ligne *firstLigne , int line, int  NbCol) {
    liste_ligne it; // = *firstLigne; //premier node de la ligne
    for (int i = 0 ; i < NbCol; i++) {
        int val;
        printf("M[%d][%d]\t", line, i+1);
        printf("rentrez un entier : ");
        scanf(" %d", &val);
        if (val == 0){continue;}
        else{
           liste_ligne node = creerNode(val, i+1);
           if (*firstLigne == NULL) {
               *firstLigne = node;
               it=*firstLigne;
           } else {
               while(it->next!=NULL){
                   it = it->next;
               }
           it->next = node;
           }
        }
    }
}

void remplirMat(struct matrice_creuse *m, int N, int M) {
    printf("========= Remplissage de la matrice =========\n");
    int i;
    m->Nlignes = N;
    m->Ncolonnes = M;
    m->lignes = malloc(N*sizeof(liste_ligne));
    for(i=0;i<N;i++){m->lignes[i]=NULL;}
    for(i = 0; i < N; i++) {
        creerLigne(&(m->lignes[i]),i+1, M);
    }

}


//Question 2

void afficherMat(struct matrice_creuse m) {
    printf("========= Affichage de la matrice =========\n");
    for (int i = 0; i < m.Nlignes; i++) {
        if (m.lignes[i] == NULL) {      //si le premier est nulle, afficher une ligne de 0, comme ça pas besoin de refaire les tests à chaque itération
            for (int i = 0; i < m.Ncolonnes; i++)
                printf("0\t");
        } else {
            liste_ligne it = m.lignes[i]; //premier node de la ligne
            for (int j = 0; j < m.Ncolonnes; j++) {
                if (it != NULL) {
                    if(it->col != j+1) {
                        printf("0\t");
                    } else {
                        printf("%d\t", it->val);
                        it = it->next;
                    }
                } else
                    printf("0\t");
            }
        }
        printf("\n");
    }
}




//Q3
int getValue(struct matrice_creuse m,int i,int j){
            liste_ligne it=m.lignes[i];
            if(it == NULL){
               return 0;
            }else{
               while(it!=NULL){
                   if(it->col==j){return it->val;}
                   else {it=it->next;}
               }
            }

     return 0;
}




//Q4
static liste_ligne pred(liste_ligne *tab,liste_ligne it){    //trouver le pred d'un node 'it'
     liste_ligne e;
     if(it==*tab){return NULL;}
     else{
        e=*tab;
        while(e->next!=it){e=e->next;}
        return e;
     }
}

void putValue(struct matrice_creuse m,int i,int j,int val){
      liste_ligne e;
      liste_ligne it=m.lignes[i];
      while((it!=NULL) && (it->col!=j)){it=it->next;}
      if(val==0){               //val=0,m[i,j]≠0,c-a-d le suppression d'un node
          if(pred(&(m.lignes[i]),it)!=NULL){     //supprimer un node entre deux autres nodes
              pred(&(m.lignes[i]),it)->next=it->next;
              free(it);
          }else{               //supprimer un node en tete de liste
              e=m.lignes[i];
              m.lignes[i]=m.lignes[i]->next;
              free(e);
          }
      }else{
          if(getValue(m,i,j)!=0){    //val≠0,m[i,j]≠0
              it->val=val;
          }else{        //val≠0,m[i,j]=0,c-a-d l'insertion d'un node
              liste_ligne it1=m.lignes[i];
              e=creerNode(val,j);
              if(it1==NULL){   //si la liste est vide
                    m.lignes[i]=e;
              }
              liste_ligne it2=m.lignes[i]->next;
              while(it1!=NULL){
                    if(it1->col>j){                //j(debut)->node->node->.....
                        e->next=m.lignes[i];
                        m.lignes[i]=e;
                        break;
                    }
                    if((it1->col<j) && (it2==NULL)){     //node->node->j(fin)
                        it1->next=e;                   
                        break;
                    }
                    if((it1->col<j) && (it2->col>j)){   //node->node->j->node.....
                        e->next=it2;
                        it1->next=e;
                        break;
                    }

                    it1=it1->next;
                    if(it2!=NULL){it2=it2->next;}
                }

          }
       }
       printf("\nLa nouvelle matrice:\n\n");
       afficherMat(m);
}


//Q5
void addMat(struct matrice_creuse m1,struct matrice_creuse m2){
    liste_ligne e;
    liste_ligne fin=NULL;
    for(int i=0;i<m1.Nlignes;i++){
        liste_ligne it1=m1.lignes[i];
        liste_ligne it2=m2.lignes[i];
        while(it1!=NULL || it2!=NULL){      //le boucle ne s'arrete pas si it1 ou it2 est pas vide
            if(it2 == NULL){             //si it2 est null,on n'a pas besion de changer it1
                it1=it1->next;
                continue;
            }
            if(it1 == NULL){       //si it1 est null,on doit ajouter un node dans la liste,c obligatoire de verifier que si la liste est vide ou pas
                e=creerNode(it2->val,it2->col);
                if(fin != NULL){      //pour le verifier,on ajoute un var de type liste_ligne 'fin',qui retourne le dernier node d'une liste,si la liste est vide,fin=null
                    fin->next=e;
                    fin=e;        //pour toutes les situations,on doit avancer fin
                }
                else{
                    m1.lignes[i]=e;
                    fin=m1.lignes[i];    //pour toutes les situations,on doit avancer fin
                }
                it1=fin->next;     //dans ce cas,pour continuer l'additon,on doit avancer it1 a fin->next(c-a-d null) et it2
                it2=it2->next;
                continue;
            }
            if(it1->col == it2->col){   //dans ce cas,on peut seulement changer la val de it1 et verifier que si la val==0 apres l'addition
                it1->val=it1->val+it2->val;
                  if(it1->val==0){                     //si le nouveau val est 0,on doit supprimer ce node,ex:-1 + 1 = 0
                      if(pred(&(m1.lignes[i]),it1)!=NULL){     //supprimer un node entre deux autres nodes
                        e=it1->next;
                        pred(&(m1.lignes[i]),it1)->next=it1->next;
                        free(it1);
                        it1=e;   //avancer it1
                      }else{               //supprimer un node en tete de liste
                         e=m1.lignes[i];
                         m1.lignes[i]=m1.lignes[i]->next;
                         free(e);
                         it1=m1.lignes[i];  //avancer it1
                      }
                  }else{
                      fin=it1;   //avancer fin
                      it1=it1->next;
                  }
                  it2=it2->next;  //it1 et it2 ont participe a l'addition,donc on doit avancer tous les deux
                  continue;
            }
            if(it1->col < it2->col){  //on ne change pas de it1,et puis avance it1 et fin
                fin=it1;
                it1=it1->next;
                continue;
            }
            if(it1->col > it2->col){  //dans ce cas on doit ajoute un nouveau node et avancer it2 et fin
                e=creerNode(it2->val,it2->col);
                if(pred(&(m1.lignes[i]),it1) == NULL){   //si on l'ajout en tete
                    e->next=m1.lignes[i];
                    m1.lignes[i]=e;
                }else{
                    pred(&(m1.lignes[i]),it1)->next=e;   //si on l'ajout entre 2 nodes
                    e->next=it1;
                }
                fin=it1;
                it2=it2->next;
            }
        }
    }
    printf("\nLa nouvelle matrice:\n\n");
    afficherMat(m1);
}


//Q6
int nombreOctetsGagnes(struct matrice_creuse m){
    int res=0;
    for(int i=0;i<m.Nlignes;i++){
        liste_ligne it=m.lignes[i];
        while(it!=NULL){
            res+=2*sizeof(int)+sizeof(element*);
            it=it->next;
        }
    }
    int nb=(m.Nlignes*m.Ncolonnes)*sizeof(int);
    return nb-res;
}
