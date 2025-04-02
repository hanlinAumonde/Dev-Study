#include "tp4.h"

//Q1
T_Arbre* initABR() {
    T_Arbre* abr = malloc(sizeof(T_Arbre));
    abr->Racine = NULL;
    return abr;
}

//Q2
T_Noeud* creerNoeud(int cle) {
    T_Noeud* noeud = malloc(sizeof(T_Noeud));
    noeud->cle = cle;
    noeud->nb_occ = 1;
    noeud->gauche = NULL;
    noeud->droite = NULL;
    return noeud;
}

//Q3
void ajoutElement(T_Arbre* abr , int cle){
    T_Noeud* temp;
    T_Noeud* it = abr->Racine;
    T_Noeud* e = creerNoeud(cle);
    if(it != NULL)
    {
        while(it != NULL){   //trouver la position pour ajout un nouveau noeud
            temp = it;
            if(cle < it->cle){
              it = it->gauche;
              if(it == NULL){temp->gauche = e;}
            }
            else if(cle > it->cle){
              it = it->droite;
              if(it == NULL){temp->droite = e;}
              }
        }
    }
    else{abr->Racine = e;}
}

//Q4
void afficherArbre(T_Noeud* n){ //parcour infixe pour avoir les nbs dans l'ordre croissant
    T_Noeud* it = n;
    if(it != NULL){
        afficherArbre(it->gauche);
        printf("cle:%d / occurrence:%d\t",it->cle,it->nb_occ);
        printf("|\t");
        afficherArbre(it->droite);
    }
}

//Q5
T_Noeud* rechercherElement(T_Arbre* abr,int val){
    T_Noeud* it = abr->Racine;
    while(it != NULL){
        if(val == it->cle){return it;}
        if(val < it->cle) it = it->gauche;
        else it = it->droite;
    }
    return NULL;
}

//Q6
void decrementerElement(T_Arbre* abr,int cle){
   T_Noeud* pere = NULL;
   T_Noeud* it = abr->Racine;
   while(it != NULL && it->cle != cle){
      pere = it;
      if(cle > it->cle) it = it->droite;
      if(cle < it->cle) it = it->gauche;
   }
   it->nb_occ--;
   if(it->nb_occ == 0){
      T_Noeud* tmpG = it->gauche;                     //on a besoin de verifier : 'it' c'est le fil gauche ou le fil droite de sa pere noeud?
      T_Noeud* tmpD = it->droite;                     //c'est aussi possible que it est la racine de l'arbre
      if(tmpG == NULL && tmpD == NULL){                                 //pas de fils
         if(pere == NULL) abr->Racine = NULL;
         else if(pere->gauche == it) pere->gauche = NULL;
         else if(pere->droite == it) pere->droite = NULL;
      }
      if(tmpG != NULL && tmpD == NULL){                                //un seul fil(gauche)
         if(pere == NULL) abr->Racine = it->gauche;
         else if(pere->gauche == it) pere->gauche = it->gauche;
         else if(pere->droite == it) pere->droite = it->gauche;
      }
      if(tmpG == NULL && tmpD != NULL){                                   //un seul fil(droit)
         if(pere == NULL) abr->Racine = it->droite;
         else if(pere->gauche == it) pere->gauche = it->droite;
         else if(pere->droite == it) pere->droite = it->droite;
      }
      if(tmpG != NULL && tmpD != NULL){  //deux fils
         T_Noeud* successeur;
         T_Noeud* temp = tmpD;
         T_Noeud* pere_succ = it;  //on doit supprimer le lien entre le pere de succ et succ aussi
         while(temp->gauche != NULL){
            pere_succ = temp;
            temp = temp->gauche;
         }
         successeur = temp;  //si 'it' a deux fils,on peux facilement trouver son successeur dans le sous-arbre droite
         if(pere_succ == it){    //c-a-d que le successeur de 'it',est exactement sa fil droit
            successeur->gauche = tmpG;   //et pere_succ est exactement 'it',pcq on va free 'it' apres,donc on n'a pas besoin de supprimer le lien entre 'it' et successeur
            successeur->droite = NULL;
         }
         else{
            successeur->gauche = tmpG;
            successeur->droite = tmpD;
            pere_succ->gauche = NULL;
         }
         if(pere == NULL) abr->Racine = successeur;
         else if(pere->gauche == it) pere->gauche = successeur;
         else if(pere->droite == it) pere->droite = successeur;
      }
      free(it);
   }
}

