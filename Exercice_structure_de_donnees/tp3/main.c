#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "tp3.h"

#define NMAX 100
int main() {
   int nb_m=0;
   struct matrice_creuse tab_m[NMAX];
   int a=1;
   int i,j,val,choix,c,v,ligne,col,reste;
   int k=0;
   do{
      printf("Veuillez saisir votre choix de l'operation\n");
      printf("1. Remplir une matrice creuse\n");
      printf("2. Afficher une matrice creuse\n");
      printf("3. Donner la valeur d'un element d'une matrice creuse\n");
      printf("4. Affecter une valeur un element d'une matrice creuse\n");
      printf("5. Additionner deux matrices creuses\n");
      printf("6. Calculer le gain en espace en utilisant cette reprsentation pour une matrice donne\n");
      printf("7. Quitter\n");
      printf("\nEntrez votre choix:\n");
      scanf("%d",&choix);
      switch(choix){
         case 1:
           printf("Attention:Vous pouvez entrer totalement 100 matrices\n");
           reste=100-nb_m;
           printf("Et maintenant,il reste %d matrices que vous pouvez entrer\n",reste);
           printf("Pour la %d eme matrice,entrez sa nb_ligne:\n",nb_m+1);
           scanf("%d",&ligne);
           printf("Puis sa nb_col:\n");
           scanf("%d",&col);
           if( ligne<=0 || col<=0){
                printf("Erreur!");
                break;
            }
           remplirMat(&tab_m[nb_m],ligne,col);
           nb_m++;
           printf("La matrice est bien saisie\n");
           break;
         case 2:
            while(k<nb_m){
                printf("\noption %d: m%d\n",k+1,k+1);
                k++;
            }
            k=0;
            printf("Votre choix de l'affichage:\n");
            scanf("%d",&c);
            afficherMat(tab_m[c-1]);
            break;
         case 3:
            while(k<nb_m){
                printf("\noption %d: m%d\n",k+1,k+1);
                k++;
            }
            k=0;
            printf("Votre choix de la matrice:\n");
            scanf("%d",&c);
            printf("NB_ligne:\n");
            scanf("%d",&ligne);
            printf("NB_colonne:\n");
            scanf("%d",&col);
            if(ligne>tab_m[c-1].Nlignes || col>tab_m[c-1].Ncolonnes || ligne<0 || col<0){
                printf("Erreur!");
                break;
            }
            val=getValue(tab_m[c-1],ligne-1,col);
            printf("la valeur est %d",val);
            break;
         case 4:
            while(k<nb_m){
                printf("\noption %d: m%d\n",k+1,k+1);
                k++;
            }
            k=0;
            printf("Votre choix de la matrice:\n");
            scanf("%d",&c);
            printf("NB_ligne:\n");
            scanf("%d",&ligne);
            printf("NB_colonne:\n");
            scanf("%d",&col);
            if(ligne>tab_m[c-1].Nlignes || col>tab_m[c-1].Ncolonnes || ligne<0 || col<0){
                printf("Erreur!");
                break;
            }
            printf("Saisir la nouvelle valeur:\n");
            scanf("%d",&v);
            if(v == getValue(tab_m[c-1],ligne-1,col)){
               printf("Erreur!La valeur n'a pas change!");
               break;
      }
            putValue(tab_m[c-1],ligne-1,col,v);
            break;
         case 5:
            while(k<nb_m){
                printf("\noption %d: m%d\n",k+1,k+1);
                k++;
            }
            k=0;
            printf("Saisir le numero de matrice m1:\n");
            scanf("%d",&i);
            printf("Saisir le numero de matrice m2:\n");
            scanf("%d",&j);
            addMat(tab_m[i-1],tab_m[j-1]);
            break;
         case 6:
            while(k<nb_m){
                printf("\noption %d: m%d\n",k+1,k+1);
                k++;
            }
            k=0;
            printf("Saisir le numero de matrice m:\n");
            scanf("%d",&c);
            if(nombreOctetsGagnes(tab_m[c-1])<0){printf("il ne gagne pas d'octet\n");}
            else{
               printf("les octets gagne: %d\n",nombreOctetsGagnes(tab_m[c-1]));
            }
            break;
         case 7:
            a=0;
            printf("\nTerminer");
            break;
         default:
            printf("Erreur!Veuillez ressaisir votre choix svp\n");
            break;
      }
      printf("\n\n");
   }while(a!=0);
   return 0;
}
