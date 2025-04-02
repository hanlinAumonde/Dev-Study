#ifndef TTP3_H
#define TTP3_H


#include <stdlib.h>
#include <string.h>
#include <stdio.h>

typedef struct element{
    int col;
    int val;
    struct element *next;
}element;

typedef element *liste_ligne;

typedef struct matrice_creuse {
    liste_ligne *lignes;
    int Nlignes;
    int Ncolonnes;
}matrice_creuse;

void remplirMat(struct matrice_creuse *m, int N, int M);

void afficherMat(struct matrice_creuse m);

int getValue(struct matrice_creuse m, int i, int j);

void putValue(struct matrice_creuse m, int i, int j,int val);

void addMat(struct matrice_creuse m1,struct matrice_creuse m2);

int nombreOctetsGagnes(struct matrice_creuse m);

#endif

