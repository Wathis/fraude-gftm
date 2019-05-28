
public class Grille {

	// CONSTANTES
	private static final int N = -1; // valeur d'une case noire.
	private static final int B = 0;  // valeur d'une case vide sans numero.

	public static int nbLignes(int[][] cases) {
		return cases.length;
	}
	public static int nbColonnes(int[][] cases) {
		return cases[0].length;
	}
	/**
	 * @param cases, cases!=null, une matrice rectangulaire representant une grille de mot croises
	 * ATTENTION !!! Contrairement a ce qui est precise dans l'enonce papier le premier index correspond 
	 * a un index de ligne (et non de colonne)
	 * cases[lig][col] est la cases situe a l'intersection de la ligne d'index lig et de la colonne d'index col
	 * @param l, un index de ligne, 0<=l<nbLignes(cases)
	 * @param c, un index de colonne, 0<=c<nbColonnes(cases)
	 * @return Retourne true si la case situee a l'intersection de la colonne d'index c et de la ligne d'index l
	 * est le debut d'un mot horizontal. Retourne false si ce n'est pas le cas.
	 */
	public static boolean estUnDebutDeMotH(int[][] cases, int l, int c) {
		return false; // A VOUS DE COMPLETER
	}

	/**
	 * @param cases, cases!=null, une matrice rectangulaire representant une grille de mot croises
	 * ATTENTION !!! Contrairement a ce qui est precise dans l'enonce papier le premier index correspond 
	 * a un index de ligne (et non de colonne)
	 * cases[lig][col] est la cases situe a l'intersection de la ligne d'index lig et de la colonne d'index col
	 * @param l, un index de ligne, 0<=l<nbLignes(cases)
	 * @param c, un index de colonne, 0<=c<nbColonnes(cases)
	 * @return Retourne true si la case situee a l'intersection de la colonne d'index c et de la ligne d'index l
	 * est le debut d'un mot vertical. Retourne false si ce n'est pas le cas.
	 */
	public static boolean estUnDebutDeMotV(int[][] cases, int l, int c) {
		return false; // A VOUS DE COMPLETER
	}

	/**
	 * Complete la grille en ajoutant la numerotation anglo-saxonne (on mentionne un numero dans
	 * les cases qui sont un debut de mot)
	 * @param cases, cases!=null, une matrice representant une grille de mots croises
	 * ATTENTION !!! Contrairement a ce qui est precise dans l'enonce papier le premier index correspond 
	 * a un index de ligne (et non de colonne)
	 * cases[lig][col] est la cases situe a l'intersection de la ligne d'index lig et de la colonne d'index col
	 */
	public static void numeroter(int[][] cases) {
		// A VOUS DE COMPLETER
	}

	/**
	 * @param cases, cases!=null, une matrice representant une grille de mots croises deja numerotee
	 * ATTENTION !!! Contrairement a ce qui est precise dans l'enonce papier le premier index correspond 
	 * a un index de ligne (et non de colonne)
	 * cases[lig][col] est la cases situe a l'intersection de la ligne d'index lig et de la colonne d'index col
	 * @return Retourne une String correspondant a la grille (pas les definitions, ni les mots a trouver... uniquement la grille).
	 * L'interieur de chaque case occupe 2 caracteres. Ces deux caracteres sont '>' et '<' si il s'agit d'une
	 * case noire, deux espaces si il s'agit d'une case blanche non numerotee, et le numero (avec un espace
	 * devannt si le numero n'est qu'un chiffre) sinon.
	 * Les lignes de la grille proprement dite sont constituees des caracteres '-', '|' et '+'.
	 * Ne pas oublier les retours a la ligne ("\n").
	 * Exemple 1 : La chaine retournee lorsque la methode est appelee sur la grille donnee en exemple dans l'enonce est :
	 * "+--+--+--+--+--+--+--+--+--+--+\n"+
       "| 1| 2| 3| 4| 5|  | 6| 7|><| 8|\n"+
	   "+--+--+--+--+--+--+--+--+--+--+\n"+
	   "| 9|  |  |  |  |><|10|  |><|  |\n"+
	   "+--+--+--+--+--+--+--+--+--+--+\n"+
	   "|  |><|11|  |><|12|  |  |><|  |\n"+
	   "+--+--+--+--+--+--+--+--+--+--+\n"+
	   "|13|14|><|15|  |  |  |  |><|  |\n"+
	   "+--+--+--+--+--+--+--+--+--+--+\n"+
	   "|16|  |17|  |><|18|  |  |><|  |\n"+
	   "+--+--+--+--+--+--+--+--+--+--+\n"+
	   "|19|  |  |  |20|><|21|  |22|  |\n"+
	   "+--+--+--+--+--+--+--+--+--+--+\n"+
	   "|><|23|  |  |  |><|  |><|24|  |\n"+
	   "+--+--+--+--+--+--+--+--+--+--+\n"+
	   "|25|><|  |><|26|27|  |28|  |  |\n"+
	   "+--+--+--+--+--+--+--+--+--+--+\n"+
	   "|29|30|  |31|><|  |><|32|  |  |\n"+
	   "+--+--+--+--+--+--+--+--+--+--+\n"+
	   "|33|  |  |  |  |  |  |  |  |  |\n"+
	   "+--+--+--+--+--+--+--+--+--+--+\n";
	 */
	public static String toString(int[][] cases){

		return ""; // A VOUS DE COMPLETER

	}

	/**
	 * 
	 * @param cases, une grille deja numerotee
	 * @param defsH, les definitions des mots a trouver horizontalement
	 * @param defsV, les definitions des mots a trouver verticalement
	 * @return Retourne une String contenant les definitions horizonatles 
	 * (avec bien evidemment le numero de case correspond) puis les definitions verticales. 
	 * Indice : pour obtenir les numeros des definitions vous allez devoir parcourir la grille 
	 * numerotee a la recherche des cases qui sont des debuts de mots horizontaux (puis dans un second temps les verticaux)
	 * et recuperer le numero qu'il y a dans ces cases.
	 * Exemple : 
	 * Sur l'exemple de la feuille, la methode retourne la chaine : 
"HORIZONTALEMENT\n"+
"1 Lynx d'afrique\n"+
"9 Rieuse, tachetee ou rayee\n"+
"10 Possede\n"+
"11 Hors de portee\n"+
"12 Antilope\n"+
"13 Adverbe de lieu\n"+
"15 Girafide\n"+
"16 Asile\n"+
"18 Rongeur\n"+
"19 Rois faineants de la savane\n"+
"21 Retrograde\n"+
"23 vient apres l'effort\n"+
"24 Pronom personnel\n"+
"26 Nourriture hebraique\n"+
"29 Reservoir\n"+
"32 Deesse\n"+
"33 Trompeuses africaines\n"+
"\n"+
"VERTICALEMENT\n"+
"1 Loup africain\n"+
"2 Champagne\n"+
"3 Debut de reunion\n"+
"4 Prenom masculin\n"+
"5 Demonstratif\n"+
"6 Pantheres\n"+
"7 Telle une mort precoce\n"+
"8 Noir ou blanc d'Afrique\n"+
"12 Autobus\n"+
"14 Oiseau sacre en Afrique\n"+
"17 Monnaie de l'ex URSS\n"+
"20 Ne se mouille pas\n"+
"22 Conduit\n"+
"25 Mi-mouche africaine\n"+
"27 Recueil amusant\n"+
"28 Succes\n"+
"30 Pronom\n"+
"31 Opus abrege\n"+
	 */
	public static String toStringDefinitions(int[][] cases, String[] defsH, String[] defsV) {

		return ""; // A VOUS DE COMPLETER

	}

	/**
	 * 
	 * @param cases, une grille deja numerotee
	 * @param motsH, les mots a trouver horizontalement
	 * @param motsV, les mots a trouver verticalement
	 * @return Retourne une String correspondant a la grille remplie sans les contours des cases, et avec le
	 * caractere '*' pour les cases noires.
	 * Exemple : Sur la grille donnee en exemple sur l'enonce, la methode retourne la String suivante : 
"CARACALS*R\n"+
"HYENE*EU*H\n"+
"A*UT*COB*I\n"+
"CI*OKAPI*N\n"+
"ABRI*RAT*O\n"+
"LIONS*REAC\n"+
"*SUEE*D*ME\n"+
"T*B*CASHER\n"+
"SILO*N*INO\n"+
"ELEPHANTES\n";
	 */
	public static String toStringSolution(int[][] cases, String[] motsH, String[] motsV ) {
		return ""; // A VOUS DE COMPLETER
	}
}
