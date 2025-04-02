#ifndef TP4_H
#define TP4_H

#include <stdlib.h>
#include <stdio.h>

typedef struct Noeud{
    int cle;
    int nb_occ;
    struct Noeud* gauche;
    struct Noeud* droite;
}T_Noeud;

typedef struct arbre{
    T_Noeud* Racine;
}T_Arbre;

T_Arbre* initABR();

T_Noeud* creerNoeud(int cle);

void ajoutElement(T_Arbre* abr,int cle);

void afficherArbre(T_Noeud* n);

T_Noeud* rechercherElement(T_Arbre* abr,int val);

void decrementerElement(T_Arbre* abr,int cle);
#endif
