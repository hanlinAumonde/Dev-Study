#include <stdio.h>
#include <stdlib.h>

#include "tp4.h"

#define NMAX 10
int main(){
    T_Arbre* tab[NMAX];
    int nbabr = 0;
    int a = 1;
    int choix,nb,e;
    int k = 0;
    do{
        printf("Qu-est ce que vous voulez faire?\n");
        printf("1.Cr¨¦er un ABR ¨¤ partir d¡¯une s¨¦rie de N entiers saisie par l¡¯utilisateur\n");
        printf("2.Cr¨¦er un ABR ¨¤ partir d¡¯une s¨¦rie de N entiers lus depuis un fichier texte\n");
        printf("3. Afficher tous les ¨¦l¨¦ments de l¡¯ABR\n");
        printf("4. Rechercher un ¨¦l¨¦ment dans l¡¯ABR\n");
        printf("5. Supprimer une occurrence d¡¯un ¨¦l¨¦ment de l¡¯ABR\n");
        printf("6.Quitter\n");
        scanf("%d",&choix);
        switch(choix){
            case 1:
                printf("Entrez le nombre des entiers que vous voulez saisir:\n");
                scanf("%d",&nb);
                if(nbabr > NMAX){
                    printf("Il n'y a pas encore de place pour cr¨¦er un arbre\n");
                    break;
                }
                printf("ca c'est le %deme arbre vous voulez cr¨¦er\n",nbabr+1);
                tab[nbabr] = initABR();
                for(int i=0;i<nb;i++){
                    printf("Entrez un entier:\n");
                    scanf("%d",&e);
                    if(rechercherElement(tab[nbabr],e) == NULL) ajoutElement(tab[nbabr],e);
                    else{
                        T_Noeud* noeud = rechercherElement(tab[nbabr],e);
                        noeud->nb_occ++;
                    }
                }
                nbabr++;
                break;
            case 2:
                break;
            case 3:
                while(k < nbabr){
                    printf("option %d : ABR %d\n",k+1,k+1);
                    k++;
                }
                printf("Entrez votre choix de l'arbre pour l'affichage\n");
                scanf("%d",&choix);
                if(tab[choix-1]->Racine == NULL) printf("L'arbre est vide\n");
                printf("Voici l'affichage de l'arbre %d : \n",choix);
                afficherArbre(tab[choix-1]->Racine);
                break;
            case 4:
                while(k < nbabr){
                    printf("option %d : ABR %d\n",k+1,k+1);
                    k++;
                }
                printf("Entrez votre choix de l'arbre\n");
                scanf("%d",&choix);
                printf("Entez l'element que vous voulez rechercher :\n");
                scanf("%d",&e);
                if(rechercherElement(tab[choix-1],e) == NULL) printf("L'element n'existe pas dans l'arbre %d\n",nbabr+1);
                else printf("L'element existe dans l'arbre %d , cle:%d , occurrence:%d\n",nbabr+1,rechercherElement(tab[choix-1],e)->cle,rechercherElement(tab[choix-1],e)->nb_occ);
                break;
            case 5:
                while(k < nbabr){
                    printf("option %d : ABR %d\n",k+1,k+1);
                    k++;
                }
                printf("Entrez votre choix de l'arbre\n");
                scanf("%d",&choix);
                printf("Entez l'element que vous voulez decrementer :\n");
                scanf("%d",&e);
                if(rechercherElement(tab[choix-1],e) == NULL) printf("Erreur!L'element n'existe pas!\n");
                else{
                    decrementerElement(tab[choix-1],e);
                    if(tab[choix-1]->Racine == NULL) printf("L'arbre est deja vide\n");
                    else {
                        printf("Apres votre modifiation , l'arbre %d:\n",choix);
                        afficherArbre(tab[choix-1]->Racine);
                    }
                }
                break;
            case 6:
                a = 0;
                break;
            default:
            printf("Erreur!Veuillez ressaisir votre choix svp\n");
            break;
         }
         printf("\n\n");
    }while(a != 0);
    return 0;
}
