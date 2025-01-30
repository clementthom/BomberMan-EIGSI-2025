import App.ElementClasse;
import eigsi.*; // ne pas supprimer

//ce code a été en majorité développé par des élèves de l'EIGSI. La bibliothèque et la classe ont été codé par les professeurs.

public class App {
	
	// seul le code de cette fonction est exécuté
	public static void main(String[] args) {

		configurer();
		
		//Lecture d'une carte (selon un formatage approprié)
		Outils.chargerCarte("carteSurprise.txt");
		int[][] elements = Outils.obtenirElements();
		int[][] puissances = Outils.obtenirPuissances();
		int[][] longueursMeches = Outils.obtenirLongueursMeches();
		//matrice permettant de retenir pendant un tour les zones touchées par les déflagrations pendant le tour précédent
		int[][] feuCyclePrecedent = Outils.clonerMatrice(elements); //on remplit la matrice ...
		viderMatrice(feuCyclePrecedent); // ... puis on la vide


		//initialisation
		int temps = 0;
		int nombreActionsPossibles = 2; //nombre d'actions posssibles par cycle
		baseJour1(elements, puissances, longueursMeches, feuCyclePrecedent);
		temps = 1;
		

		//déroulement de la simulation
		while (temps < 20) {
			deroulementUnCycle(elements, puissances, longueursMeches, feuCyclePrecedent, temps, nombreActionsPossibles);
			FenetreGraphique.modifierMessageDroite("T0+"+temps);
			temps++;
		}
		finProgramme();
	}


	//configure les paramètres de la fenêtre
	static void configurer() {
		//Configuration.DECALAGE_HAUT= 100;
		Configuration.TITRE = "Carte";
		Configuration.IMAGE_TAILLE = 100; // en pixels
		Configuration.LARGEUR_INFORMATION_DROITE = 300; // en pixels
		Configuration.CELLULE_TEXTE_TAILLE_POLICE = 18;
		Configuration.CELLULE_TEXTE_COULEUR_PUISSANCE = Couleur.BLEU;
		Configuration.CELLULE_TEXTE_COULEUR_MECHE     = Couleur.ROUGE;
		Configuration.CELLULE_TEXTE_AFFICHER_SI_VALEUR_NEGATIVE = false;
	}



	// Code fourni montrant l'utilisation d'une partie des fonctions disponibles.
	static void baseJour1(int[][] elements, int[][] puissances, int[][] longueursMeches, int[][] feuCyclePrecedent) {
		
		
		//actualisation de l'affichage en console (utile en cas de modification de l'état des tables)
		miseAJourAffichageConsole(elements, puissances, longueursMeches, feuCyclePrecedent);


		// Association de la matrice à la fenêtre graphique et activation du clic
		FenetreGraphique.initialiserFenetre(elements, puissances, longueursMeches);
		FenetreGraphique.activerClicSouris(true);


		//initialisation d'une bombe verticale allumée de 4 de puissance et une mèche durant 3 cycle initialisée
		int yElement = 0;
		int xElement = 1;
		int puissanceElement = 4;
		int longueursMechesElement = 3;
		//modificationCaseCarte(elements, puissances, longueursMeches, "bombe_star_allumee", puissanceElement, longueursMechesElement, yElement, xElement);


		/* 
		//initialisation du nombre d'action possible par cycle (choisi par l'utilisateur)
		System.out.println("Choisissez le nombre d'actions possibles par tour :");
        int barriere = Console.saisirEntier(); */


		// Marquer une pause
		System.out.print("Appuyer sur Entrer pour suite");
		Console.appuyerSurEntrer();
	}


	//déroulé de la simulation après le cycle d'initialisation
	static void deroulementUnCycle(int[][] elements, int[][] puissances, int[][] longueursMeches, int[][] feuCyclePrecedent, int temps, int nombreActionsPossibles) {

		//coordonnées d'un élément
		//i --> yElement; numéro ligne sur laquelle est placée l'élement
		//j --> xElement; numéro colonne

		//l'utilisateur agit
		Console.effacerContenu();
		System.out.println("Cycle : " + temps);
		actionsUtilisateur(elements, nombreActionsPossibles);


		//changement du cycle
		viderMatrice(feuCyclePrecedent);

		for (int yElement = 0; yElement<elements.length; yElement++) { // on bouge sur la colonne (numeroLigne)
			for (int xElement = 0; xElement<elements[yElement].length; xElement++) {  //on bouge sur la ligne (numeroColonne)

				//System.out.println("temps : " + temps + " y " );
				//afficherMatriceEnConsole(feuCyclePrecedent);
				evolutionCombustion(elements, feuCyclePrecedent, yElement, xElement);

				//on change la taille des mèches
				if (longueursMeches[yElement][xElement] > 0) {
					longueursMeches[yElement][xElement] = changerEtatMeche(elements, longueursMeches, yElement, xElement) ;
					//FenetreGraphique.rafraichirElement(yElement, xElement);
				}
				//on fait tout sauter
				explosion(elements, puissances, longueursMeches, feuCyclePrecedent, yElement, xElement);
				//on change les éléments brûlés
			}
			System.out.println("ici : feuCyclePrecedent");
			afficherMatriceEnConsole(feuCyclePrecedent);
			System.out.print("");
		}


		//rendu à l'utilisateur de l'état du cycle avant changement
		miseAJourAffichageConsole(elements, puissances, longueursMeches, feuCyclePrecedent);
		miseAJourFenetre(elements);


		//fin du changement d'état du cycle
		FenetreGraphique.modifierMessageBas("carte rafraichie."); //on écrit "carte rafraichie." en bas à gauche de la fenêtre
		Outils.attendre(1500); //on attend 500 ms

		/* 
		System.out.print("Appuyer sur Entrer pour suite");
		Console.appuyerSurEntrer();
		*/
		
	}



	//prévient de la fin de l'exécution du code et ferme la fenêtre
	public static void finProgramme() {
		FenetreGraphique.messagePopUp("Fin : cliquer pour quitter");
		System.exit(0);
	}



	// Code fourni, pour affichager un tableau 2D (c'est à dire un tableau de tableaux).
	// Le premier index correspond aux lignes, le second aux colonnes.
	// Remarque : « Matrice » <=> tab 2D particulier où même nombre de colonnes à chaque ligne (mais non vérifié ici)
	public static void afficherMatriceEnConsole(int[][] matrice) {
		if (matrice == null)  {
			System.out.println("Pas d'affichage, matrice null.");
		}
		else { 
			for (int iLig = 0; iLig < matrice.length; iLig++) { // parcours ligne par ligne
				for (int iCol = 0; iCol < matrice[iLig].length; iCol++) { // parcours colonne par colonne
					int valeur = matrice[iLig][iCol];// contenu d'une case
					String valeurTextuelle = String.format("%3d", valeur); // int transformé en String occupant 3 caractères minimum
					System.out.print(valeurTextuelle);
					System.out.print(" ");
				}
				System.out.println();
			}
		}
		System.out.println();
	}



//ici commence nos propres méthodes secondaires

	//affiche tous les tableaux d'état de la grille en console
	public static void miseAJourAffichageConsole(int[][] elements, int[][] puissances, int[][] longueursMeches, int[][] feuCyclePrecedent) {
		System.out.println("Les éléments de la carte : ");
		afficherMatriceEnConsole(elements);

		System.out.println("Les caractéristiques des éléments (valeurs > 0 si caractéristiques pertinentes) : ");
		System.out.println("puissances :");
		afficherMatriceEnConsole(puissances);
		System.out.println("longueursMeches :");
		afficherMatriceEnConsole(longueursMeches);
		System.out.println("feuCyclePrecedent :");
		afficherMatriceEnConsole(feuCyclePrecedent);
	}



	//met à jour l'apparence de la fenêtre après l'écoulement d'un cycle
	public static void miseAJourFenetre(int[][] elements) {
		for (int i = 0; i<elements.length; i++) {
			for (int j = 0; j<elements[i].length; j++) {
				FenetreGraphique.rafraichirElement(i,j);
				//System.out.println("La cellule en position ["+ i +"]["+ j +"] a été mise à jour.");
			}
		}
	}



	//modifie l'état d'une case de la grille dans les 3 tableaux correspondant
	public static void modificationCaseCarte(int[][] elements, int puissances[][], int[][] longueursMeches, String nomElement, int puissanceElement, int longueursMechesElement, int yElement, int xElement) {
		elements[0][1] = Element.identifiant(nomElement);
		puissances[0][1] = 4;
		longueursMeches[0][1] = 3;
		// index ligne puis index colonne --> [y][x] en écriture cartésienne
		// dans matrice etats, valeur possible selon "legende.csv" !
		// Attention : informer la fenêtre après une mise à jour dans la matrice d'entiers !!
		FenetreGraphique.rafraichirElement(0,1); // préciser les mêmes index (ligne puis colonne)
		System.out.println("La cellule en position [0][1] a été mise à jour.");
		//pas besoin de retourner quoi que ce soit : les tableaux sont dynamiques
	}


	//permet de mettre toutes les valeurs d'une matrice à 0
	public static void viderMatrice(int[][] matriceAVider) {
		for(int i = 0; i < matriceAVider.length; i++) {
			for(int j = 0; j < matriceAVider[i].length; j++) {
				matriceAVider[i][j] = 0;
			}
		}
	}

	//réduit la mèche si longueur suffisante, sinon signale l'explosion
	public static int changerEtatMeche(int[][] elements, int[][]longueursMeches, int yElement, int xElement) {
		switch (elements[yElement][xElement]) {
			case ElementClasse.BOMBE_H_ALLUMEE, ElementClasse.BOMBE_H_ALLUMEE_FEU: //bombe H allumée 
				longueursMeches[yElement][xElement] --; //on réduit la mèche de 1 (valeurCaseMeche --)
				configurerColoration(longueursMeches[yElement][xElement], yElement, xElement); //colorie la case de la bombe
				return longueursMeches[yElement][xElement];
			case ElementClasse.BOMBE_V_ALLUMEE, ElementClasse.BOMBE_V_ALLUMEE_FEU: //bombe V allumée
				longueursMeches[yElement][xElement] --; 
				configurerColoration(longueursMeches[yElement][xElement], yElement, xElement);
				return longueursMeches[yElement][xElement];
			case ElementClasse.BOMBE_PLUS_ALLUMEE, ElementClasse.BOMBE_PLUS_ALLUMEE_FEU: //bombe + allumée 
				longueursMeches[yElement][xElement] --;
				configurerColoration(longueursMeches[yElement][xElement], yElement, xElement);
				return longueursMeches[yElement][xElement];
			case ElementClasse.BOMBE_X_ALLUMEE, ElementClasse.BOMBE_X_ALLUMEE_FEU: //bombe X allumée 
				longueursMeches[yElement][xElement] --;
				configurerColoration(longueursMeches[yElement][xElement], yElement, xElement);
				return longueursMeches[yElement][xElement];
			case ElementClasse.BOMBE_STAR_ALLUMEE, ElementClasse.BOMBE_STAR_ALLUMEE_FEU: //bombe * allumée 
				longueursMeches[yElement][xElement] --;
				configurerColoration(longueursMeches[yElement][xElement], yElement, xElement);
				return longueursMeches[yElement][xElement];
			default:
				return longueursMeches[yElement][xElement];
		}
	}


	//change la couleur d'une case selon l'état de la bombe qu'elle héberge
	static void configurerColoration(int valeurCaseMeche,int yElement,int xElement) {
		switch(valeurCaseMeche) {
			case 0: //bombe prête à exploser
				FenetreGraphique.mettreCouleurFond(yElement, xElement, Couleur.BLANC);
				//FenetreGraphique.rafraichirElement(i,j);
				break;
			case 1: //longueur de mèche restante : 1
				FenetreGraphique.mettreCouleurFond(yElement, xElement,Couleur.ORANGE);
				//FenetreGraphique.rafraichirElement(i,j);
				break;
			case 2:
				FenetreGraphique.mettreCouleurFond(yElement , xElement,Couleur.JAUNE);
				//FenetreGraphique.rafraichirElement(i,j);
				break;
			default:
				break;
		}
	}


	//fait exploser les bombes et modifie la matrice feuCyclePrecedent --> le cycle actuel devient un cycle passé aux yeux du programme
	public static void explosion(int[][] elements, int[][] puissances, int[][] longueursMeches,int[][] feuCyclePrecedent, int yElement, int xElement) {
		if (longueursMeches[yElement][xElement] == 0) { //bombe sur le point d'exploser
			switch (elements[yElement][xElement]) {
				case ElementClasse.BOMBE_H_ALLUMEE, ElementClasse.BOMBE_H_ALLUMEE_FEU, ElementClasse.BOMBE_H_FEU: //bombe H allumée et/ou en feu 
					explosionHorizontale(elements, puissances, feuCyclePrecedent, yElement, xElement);
					elements[yElement][xElement] = ElementClasse.BOMBE_H_EXPLOSION; //la bombe explose
					feuCyclePrecedent[yElement][xElement] = 1; //ici, le feu symbolise l'explosion de la bombe
					FenetreGraphique.rafraichirElement(yElement, xElement);
					break;
				case ElementClasse.BOMBE_V_ALLUMEE, ElementClasse.BOMBE_V_ALLUMEE_FEU, ElementClasse.BOMBE_V_FEU: //bombe V allumée et/ou en feu
					explosionVerticale(elements, puissances, feuCyclePrecedent, yElement, xElement);
					elements[yElement][xElement] = ElementClasse.BOMBE_V_EXPLOSION;
					feuCyclePrecedent[yElement][xElement] = 1; 
					FenetreGraphique.rafraichirElement(yElement, xElement);
					break;
				case ElementClasse.BOMBE_PLUS_ALLUMEE, ElementClasse.BOMBE_PLUS_ALLUMEE_FEU, ElementClasse.BOMBE_PLUS_FEU: //bombe + allumée et/ou en feu
					explosionHorizontale(elements, puissances, feuCyclePrecedent, yElement, xElement);
					explosionVerticale(elements, puissances, feuCyclePrecedent, yElement, xElement);
					elements[yElement][xElement] = ElementClasse.BOMBE_PLUS_EXPLOSION;
					feuCyclePrecedent[yElement][xElement] = 1;
					FenetreGraphique.rafraichirElement(yElement, xElement);
					break;
				case ElementClasse.BOMBE_X_ALLUMEE, ElementClasse.BOMBE_X_ALLUMEE_FEU, ElementClasse.BOMBE_X_FEU : //bombe X allumée et/ou en feu
					explosionDiagonale(elements, puissances, feuCyclePrecedent, yElement, xElement);
					elements[yElement][xElement] = ElementClasse.BOMBE_X_EXPLOSION;
					feuCyclePrecedent[yElement][xElement] = 1;
					FenetreGraphique.rafraichirElement(yElement, xElement);
					break;
				case ElementClasse.BOMBE_STAR_ALLUMEE, ElementClasse.BOMBE_STAR_ALLUMEE_FEU, ElementClasse.BOMBE_STAR_FEU : //bombe * allumée et/ou en feu
					explosionHorizontale(elements, puissances, feuCyclePrecedent, yElement, xElement);
					explosionVerticale(elements, puissances, feuCyclePrecedent, yElement, xElement);
					explosionDiagonale(elements, puissances, feuCyclePrecedent, yElement, xElement);
					elements[yElement][xElement] = ElementClasse.BOMBE_STAR_EXPLOSION;
					feuCyclePrecedent[yElement][xElement] = 1; 
					FenetreGraphique.rafraichirElement(yElement, xElement);
					break;
				default: //pas d'explosion
					FenetreGraphique.rafraichirElement(yElement, xElement);
					break;
			}
		}
	}


	//met en feu les élements horizontalement
	public static void explosionHorizontale(int[][] elements, int[][] puissances, int[][] feuCyclePrecedent, int yElement, int xElement) {
		for(int k = 1; k<puissances[yElement][xElement]+1; k++) { //on brûle à droite de la bombe, +1 --> on décale tout de 1
			if (k+xElement < elements[yElement].length ) { // si on ne sort pas par la droite du tableau
				if(elements[yElement][xElement+k] == ElementClasse.ROCHER || elements[yElement][xElement+k] == ElementClasse.BARRIERE) { 
					k = puissances[yElement][xElement]+1; // la bombe n'est pas brûlée : on sort de la boucle
				}
				else {
					combustionElement(elements, yElement, xElement+k); //on brûle l'élement concerné
					feuCyclePrecedent[yElement][xElement+k] = 1; //on indique que la case a brûlé sur la matrice dédiée
				}
			}
		}
		for(int k = 1; k<puissances[yElement][xElement]+1; k++) { //on brûle à droite de la bombe, +1 --> on décale tout de 1
			if (xElement-k >= 0) { // la bombe n'est pas brûlée
				if(elements[yElement][xElement-k] == ElementClasse.ROCHER || elements[yElement][xElement-k] == ElementClasse.BARRIERE) { 
					k = puissances[yElement][xElement]+1; //on sort du tableau
				}
				else {
					combustionElement(elements, yElement, xElement-k); //on brûle l'élement concerné
					feuCyclePrecedent[yElement][xElement-k] = 1; //case en feu
				}
			}
		}
	}


	//met en feu les élements verticalement
	public static void explosionVerticale(int[][] elements, int[][] puissances, int[][] feuCyclePrecedent, int yElement, int xElement) {
		for(int k = 1; k<puissances[yElement][xElement]+1; k++) { //on brûle vers le bas
			if (yElement+k < elements.length ) { //si on ne sort pas par le bas du tableau
				if(elements[yElement+k][xElement] == ElementClasse.ROCHER || elements[yElement+k][xElement] == ElementClasse.BARRIERE) { 
					k = puissances[yElement][xElement]+1;
				}
				else {
					combustionElement(elements, yElement+k, xElement);
					feuCyclePrecedent[yElement+k][xElement] = 1; 
				}
			}
		}
		for(int k = 1; k<puissances[yElement][xElement]+1; k++) { //on brûle vers le haut
			if (yElement-k >= 0 ) { //si on ne sort pas par le haut du tableau
				if(elements[yElement-k][xElement] == ElementClasse.ROCHER || elements[yElement-k][xElement] == ElementClasse.BARRIERE ) { //case 1 : on a un ROCHER
					k = puissances[yElement][xElement]+1;
				}
				else {
					combustionElement(elements, yElement-k, xElement);
					feuCyclePrecedent[yElement-k][xElement] = 1; 
				}
			}
		}
	}



	//met en feu les élements dans les directions diagonales
	public static void explosionDiagonale(int[][] elements, int[][] puissances, int[][] feuCyclePrecedent, int yElement, int xElement) {

		//ici, les yElement et xElement sont les coordonnées de l'explosion

		// Bas-droite (↘)
		for (int k = 1; k <= puissances[yElement][xElement]; k++) {
			if (yElement + k >= elements.length || xElement + k >= elements[0].length) //
				break; // Hors limites, on sort de la boucle --> marche car on est dans une boucle for
			if (elements[yElement + k][xElement + k] == ElementClasse.ROCHER || elements[yElement + k][xElement + k] == ElementClasse.BARRIERE)
				break; // Rencontre d'un rocher
			//si les deux conditions sont respectées :
			combustionElement(elements, yElement + k, xElement + k);
			feuCyclePrecedent[yElement+k][xElement+k] = 1; 
		}

		// Bas-gauche (↙)
		for (int k = 1; k <= puissances[yElement][xElement]; k++) {
			if (yElement + k >= elements.length || xElement - k < 0) //
				break; // Hors limites, on sort de la boucle --> marche car on est dans une boucle for
			if (elements[yElement + k][xElement - k] == ElementClasse.ROCHER || elements[yElement + k][xElement - k] == ElementClasse.BARRIERE)
				break; // Rencontre d'un rocher
			//si les deux conditions sont respectées :
			combustionElement(elements, yElement + k, xElement - k);
			feuCyclePrecedent[yElement+k][xElement-k] = 1; 
		}

		// Haut-gauche (↖)
		for (int k = 1; k <= puissances[yElement][xElement]; k++) {
			if (yElement - k < 0 || xElement - k < 0) //
				break; // Hors limites, on sort de la boucle --> marche car on est dans une boucle for
			if (elements[yElement - k][xElement - k] == ElementClasse.ROCHER || elements[yElement - k][xElement - k] == ElementClasse.BARRIERE)
				break; // Rencontre d'un rocher
			//si les deux conditions sont respectées :
			combustionElement(elements, yElement - k, xElement - k);
			feuCyclePrecedent[yElement-k][xElement-k] = 1; 
		}

		// Haut-droite (↗)
		for (int k = 1; k <= puissances[yElement][xElement]; k++) {
			if (yElement - k < 0 || xElement + k >= elements[0].length)
				break; // Hors limites
			if (elements[yElement - k][xElement + k] == ElementClasse.ROCHER || elements[yElement - k][xElement + k] == ElementClasse.BARRIERE)
				break; // Rencontre d'un rocher
			combustionElement(elements, yElement - k, xElement + k);
			feuCyclePrecedent[yElement-k][xElement+k] = 1; 
		}
	}


	//change l'état d'un éléments combustible
	public static void combustionElement(int[][] elements, int yElement, int xElement) {
		switch (elements[yElement][xElement]) {
			case ElementClasse.HERBE: // on a de l'herbe
				elements[yElement][xElement]= ElementClasse.HERBE_FEU; //l'herbe brûle
				break;
			case ElementClasse.HERBE_BRULEE: // on a de l'herbe brûlée
				elements[yElement][xElement] = ElementClasse.HERBE_BRULEE_FEU; //l'herbe brûlée rebrûle
				break;
			case ElementClasse.BOMBE_H_ETEINTE: // on a une bombe H éteinte
				elements[yElement][xElement] = ElementClasse.BOMBE_H_FEU; //la bombe H brûle
				break;
			case ElementClasse.BOMBE_H_ALLUMEE: // on a une bombe H allumée
				elements[yElement][xElement] = ElementClasse.BOMBE_H_ALLUMEE_FEU; //la bombe H allumée brûle
				break;
			case ElementClasse.BOMBE_V_ETEINTE: // on a une bombe V éteinte
				elements[yElement][xElement] = ElementClasse.BOMBE_V_FEU; //la bombe V brûle
				break;
			case ElementClasse.BOMBE_V_ALLUMEE: // on a une bombe V allumée
				elements[yElement][xElement] = ElementClasse.BOMBE_V_ALLUMEE_FEU; //la bombe V allumée brûle
				break;
			case ElementClasse.BOMBE_PLUS_ETEINTE: // on a une bombe + éteinte
				elements[yElement][xElement] = ElementClasse.BOMBE_PLUS_FEU; //la bombe + brûle
				break;
			case ElementClasse.BOMBE_PLUS_ALLUMEE: // on a une bombe + allumée
				elements[yElement][xElement] = ElementClasse.BOMBE_PLUS_ALLUMEE_FEU; //la bombe + allumée brûle
				break;
			case ElementClasse.BOMBE_X_ETEINTE: // on a une bombe X éteinte
				elements[yElement][xElement] = ElementClasse.BOMBE_X_FEU; //la bombe X brûle
				break;
			case ElementClasse.BOMBE_X_ALLUMEE: // on a une bombe X allumée
				elements[yElement][xElement] = ElementClasse.BOMBE_X_ALLUMEE_FEU; //la bombe X allumée brûle
				break;
			case ElementClasse.BOMBE_STAR_ETEINTE: // on a une bombe * éteinte
				elements[yElement][xElement] = ElementClasse.BOMBE_STAR_ALLUMEE; //la bombe * brûle
				break;
			case ElementClasse.BOMBE_STAR_ALLUMEE: // on a une bombe * allumée
				elements[yElement][xElement] = ElementClasse.BOMBE_STAR_ALLUMEE_FEU; //la bombe * allumée brûle
				break;
			case ElementClasse.ZONE_BRULEE: // on a une zone brûlée
				elements[yElement][xElement] = ElementClasse.ZONE_BRULEE_FEU; //la zone brûlée brûle
				break;
			case ElementClasse.EAU, ElementClasse.EAU_FEU: // on a de l'eau
				elements[yElement][xElement] = ElementClasse.EAU_FEU; //l'eau brûle
				break;
			default:
				break; //si on a autre chose
		}
		FenetreGraphique.rafraichirElement(yElement, xElement);
	}


	//fait changer l'état de chaque élément après combustion
	public static void evolutionCombustion(int[][] elements, int[][] feuCyclePrecedent, int yElement, int xElement) {
		if(feuCyclePrecedent[yElement][xElement]==0) { //la combustion est antérieure au tour précédent
			switch (elements[yElement][xElement]) {
				/* évolution des éléments en feu */
				case ElementClasse.HERBE_FEU, ElementClasse.HERBE_BRULEE_FEU: //herbe en feu ou herbe brûlée en feu
					elements[yElement][xElement] = ElementClasse.HERBE_BRULEE;//herbe brûlée
					break;
				case ElementClasse.BOMBE_H_FEU, ElementClasse.BOMBE_H_ALLUMEE_FEU: //bombe H en feu (allumée ou non)
						elements[yElement][xElement] = ElementClasse.BOMBE_H_ALLUMEE;//bombe H allumée
					break;
				case ElementClasse.BOMBE_V_FEU, ElementClasse.BOMBE_V_ALLUMEE_FEU: //bombe V en feu (allumée ou non)
					elements[yElement][xElement] = ElementClasse.BOMBE_V_ALLUMEE;//bombe V allumée
					break;
				case ElementClasse.BOMBE_PLUS_FEU, ElementClasse.BOMBE_PLUS_ALLUMEE_FEU: //bombe + en feu (allumée ou non)
					elements[yElement][xElement] = ElementClasse.BOMBE_PLUS_ALLUMEE;//bombe + allumée
					break;
				case ElementClasse.BOMBE_X_FEU, ElementClasse.BOMBE_X_ALLUMEE_FEU: //bombe X en feu (allumée ou non)
					elements[yElement][xElement] = ElementClasse.BOMBE_X_ALLUMEE;//bombe X allumée
					break;
				case ElementClasse.BOMBE_STAR_FEU, ElementClasse.BOMBE_STAR_ALLUMEE_FEU: //bombe * en feu (allumée ou non)
					elements[yElement][xElement] = ElementClasse.BOMBE_STAR_ALLUMEE;//bombe * allumée
					break;
				case ElementClasse.ZONE_BRULEE_FEU: //zone brûlée en feu
					elements[yElement][xElement] = ElementClasse.ZONE_BRULEE; //zone brûlée
					break;
				case ElementClasse.EAU_FEU: //eau en feu (?)
					elements[yElement][xElement] = ElementClasse.EAU; //eau pas en feu
					break;
				/* Carbonisation du sol sous les bombes */
				case ElementClasse.BOMBE_H_EXPLOSION, ElementClasse.BOMBE_V_EXPLOSION, ElementClasse.BOMBE_PLUS_EXPLOSION, ElementClasse.BOMBE_X_EXPLOSION, ElementClasse.BOMBE_STAR_EXPLOSION: //les bombes explosées
					elements[yElement][xElement] = ElementClasse.ZONE_BRULEE; //ça laisse une zone brûlée
					break;
				default:
					break;
			}
		}
		FenetreGraphique.rafraichirElement(yElement, xElement);
	}
	

	//altère la carte selon les actions de l'utilisateur
	public static void actionsUtilisateur(int[][] element, int nombreActionsPossibles) {

		FenetreGraphique.modifierMessageBas("actions utilisateur en cours");

		for(int numeroAction=0; numeroAction<nombreActionsPossibles; numeroAction++) {
			int nombreActionsRestantes = nombreActionsPossibles-numeroAction;
			System.out.println("Nombre d'action restant : " + nombreActionsRestantes + "\n\nQuelle action voulez-vous exécuter ? \n - poseBarriere \n - deplacerElement \n - desamorcerBombe \n - Entrée ou n'importe quoi pour ne rien faire \n");
			System.out.print("entrer la commande ici : ");
            String action = Console.saisirChaine();
			
			//on applique l'action choisie 
			switch (action) {
				case "poseBarriere":
					poseBarriere(element);
					break;
				case "deplacerElement":
					//deplacerElement(element);
					break;
				case "desamorcerBombe":
					//desamorceBombe(element);
					break;
				default:
					break;
		}
		}
	}


	//permet de poser une barrière sous certaines conditions
	public static void poseBarriere(int[][] elements) {

		//choix de la position
		//on initialise des positions nulles et le clic de souris si non fait
		int yElement = -1;
        int xElement = -1;
		FenetreGraphique.activerClicSouris(true);

		//on n'agit pas tant que l'utilisateur n'a pas choisi de case
		System.out.println("Choisissez la case où placer la barrière : ");
		while(yElement == -1 || xElement == -1 ) {
			yElement = FenetreGraphique.dernierIndexLigneClicSouris();
    		xElement = FenetreGraphique.dernierIndexColonneClicSouris();

			if(yElement != -1 && xElement != -1 ) {
				FenetreGraphique.encadrerElement(yElement, xElement, Couleur.NOIR, 5);
			} //on est dans le tableau : une case a été cliquée
			Outils.attendre(50); //latence avant d'actualiser la position de la case cliquée
		}

		//on change la case
		Outils.attendre(300); //latence avant d'actualiser la position de la case cliquée
		elements[yElement][xElement] = ElementClasse.BARRIERE; //on crée la barrière
		FenetreGraphique.rafraichirElement(yElement, xElement); //on l'affiche avec le cadre
		FenetreGraphique.modifierMessageBas("case selectionnée pour barrière : [" + yElement + "][" + xElement +"]");
		Outils.attendre(300); //latence avant de supprimer le cadre
		FenetreGraphique.supprimerCadreElement(yElement, xElement); //on vire le cadre
		FenetreGraphique.rafraichirElement(yElement, xElement); //on l'affiche sans cadre
		FenetreGraphique.modifierMessageDroite("Veuillez cliquer en dehors du tableau");
		System.out.println("Veuillez cliquer en dehors du tableau");
		while(yElement != -1 || xElement != -1 ) {
			yElement = FenetreGraphique.dernierIndexLigneClicSouris();
    		xElement = FenetreGraphique.dernierIndexColonneClicSouris();

			Outils.attendre(50); //latence avant d'actualiser la position de la case cliquée
		}
		FenetreGraphique.activerClicSouris(false); //on désactive la souris au cas où
    }


	/* 
	//affiche des données avant l'action utilisateur
	public static void afficherDonneesAffichage(int[][] element) {

		//valeurs affichage

		for (int yElement = 0; yElement<elements.length; yElement++) { // on bouge sur la colonne (numeroLigne)
			for (int xElement = 0; xElement<elements[yElement].length; xElement++) {  //on bouge sur la ligne (numeroColonne)
				switch (element[yElement][xElement]) {
					case ElementClasse.HERBE:		
						break;
					default:
						break;
				}
			}
		} */

//énumérations
	//énumération rassemblant les éléments et leur identifiant associé
	public enum Element {

		rocher (1, "rocher"),
		herbe (2, "herbe"),
		herbe_feu (3, "herbe_feu"),
		herbe_brulee (4, "herbe_brulee"),
		herbe_brulee_feu (5, "herbe_brulee_feu"),
		bombe_H_eteinte (6, "bombe_H_eteinte"),
		bombe_H_feu (7, "bombe_H_feu"),
		bombe_H_allumee (8, "bombe_H_allumee"),
		bombe_H_allumee_feu (9, "bombe_H_allumee_feu"),
		bombe_H_explosion (10, "bombe_H_explosion"),
		bombe_V_eteinte (11, "bombe_V_eteinte"),
		bombe_V_feu (12, "bombe_V_feu"),
		bombe_V_allumee (13, "bombe_V_allumee"),
		bombe_V_allumee_feu (14, "bombe_V_allumee_feu"),
		bombe_V_explosion (15, "bombe_V_explosion"),
		bombe_plus_eteinte (16, "bombe_plus_eteinte"),
		bombe_plus_feu (17, "bombe_plus_feu"),
		bombe_plus_allumee (18, "bombe_plus_allumee"),
		bombe_plus_allumee_feu (19, "bombe_plus_allumee_feu"),
		bombe_plus_explosion (20, "bombe_plus_explosion"),
		bombe_x_eteinte (21, "bombe_x_eteinte"),
		bombe_x_feu (22, "bombe_x_feu"),
		bombe_x_allumee (23, "bombe_x_allumee"),
		bombe_x_allumee_feu (24, "bombe_x_allumee_feu "),
		bombe_x_explosion (25, "bombe_x_explosion"),
		bombe_star_eteinte(26, "bombe_star_eteinte"),
		bombe_star_feu (27, "bombe_star_feu"),
		bombe_star_allumee (28, "bombe_star_allumee"),
		bombe_star_allumee_feu (29, "bombe_star_allumee_feu"),
		bombe_star_explosion (30, "bombe_star_explosion"),
		zone_brulee (31, "zone_brulee"),
		zone_brulee_feu (32, "zone_brulee_feu"),
		eau (33, "eau"),
		eau_feu (34, "eau_feu"),
		bombe_desamorcee (35, "bombe_desamorcee"),
		bombe_desamorcee_feu (36, "bombe_desamorcee_feu"),
		barriere (37, "barriere");

		public final int id; //identifiant
		public final String nom; //nom de l'élément (ex:rocher)

		//on définit un élément
		private Element(int id, String nom) {
			this.id = id;
			this.nom = nom;
		}

		//on obtient le nom de l'élément 
		public String obtenirNom() {
			return nom;
		}

		//on récupère l'élément à partir de son nom
        public static Element trouverElementViaId(String nom) {
			for (Element element : values()) { //on parcourt les valeurs de Elements
				if (element.obtenirNom() == nom) {//si l'id de l'élément parcouru est le même que celui qu'on a entré
					return element;  //on le renvoie
				}
			}
			throw new IllegalArgumentException("element inexistant : le développeur a fait n'importe quoi");
		}

		//on revoie l'identifiant associé, utilisable par le programme
		public static int identifiant(String nom) {
			Element element = trouverElementViaId(nom);
			return element.id;
		}
	}


	//une classe ayant le rôle d'énumération pour les switch (les paramètres d'énumération ne marchent pas)
	public class ElementClasse{
		public static final int ROCHER = 1;
		public static final int HERBE = 2;
		public static final int HERBE_FEU = 3;
		public static final int HERBE_BRULEE = 4;
		public static final int HERBE_BRULEE_FEU = 5;
		public static final int BOMBE_H_ETEINTE = 6;
		public static final int BOMBE_H_FEU = 7;
		public static final int BOMBE_H_ALLUMEE = 8;
		public static final int BOMBE_H_ALLUMEE_FEU = 9;
		public static final int BOMBE_H_EXPLOSION = 10;
		public static final int BOMBE_V_ETEINTE = 11;
		public static final int BOMBE_V_FEU = 12;
		public static final int BOMBE_V_ALLUMEE = 13;
		public static final int BOMBE_V_ALLUMEE_FEU = 14;
		public static final int BOMBE_V_EXPLOSION= 15;
		public static final int BOMBE_PLUS_ETEINTE = 16;
		public static final int BOMBE_PLUS_FEU = 17;
		public static final int BOMBE_PLUS_ALLUMEE = 18;
		public static final int BOMBE_PLUS_ALLUMEE_FEU = 19;
		public static final int BOMBE_PLUS_EXPLOSION = 20;
		public static final int BOMBE_X_ETEINTE = 21;
		public static final int BOMBE_X_FEU = 22;
		public static final int BOMBE_X_ALLUMEE = 23;
		public static final int BOMBE_X_ALLUMEE_FEU = 24;
		public static final int BOMBE_X_EXPLOSION= 25;
		public static final int BOMBE_STAR_ETEINTE = 26;
		public static final int BOMBE_STAR_FEU = 27;
		public static final int BOMBE_STAR_ALLUMEE = 28;
		public static final int BOMBE_STAR_ALLUMEE_FEU = 29;
		public static final int BOMBE_STAR_EXPLOSION = 30;
		public static final int ZONE_BRULEE = 31;
		public static final int ZONE_BRULEE_FEU = 32;
		public static final int EAU = 33;
		public static final int EAU_FEU = 34;
		public static final int BOMBE_DESAMORCEE = 35;
		public static final int BOMBE_DESAMORCEE_FEU = 36;
		public static final int BARRIERE = 37;
	
	}

} // ne pas supprimer (fin du programme)
