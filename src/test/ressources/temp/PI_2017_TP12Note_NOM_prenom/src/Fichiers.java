import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class Fichiers {
	/**
	 * @param nomFichier, nomFichier!=null, le nom d'un fichier texte existant.
	 * @param mot, mot!=null
	 * @return Retourne true si le mot mot figure sur au moins l'une des lignes(*) du fichier texte de nom nomFichier
	 * (retourne false dans tous les autres cas).
	 * 
	 * (*) On ne cherche pas a determiner si un mot commence a la fin d'une ligne et se termine au debut
	 *     de la suivante : le mot doit etre integralement sur la ligne.
	 * 
	 */
	public static boolean contient(String nomFichier, String mot) {
		return false; // A VOUS DE COMPLETER
	}

	/**
	 * @param dossier, dossier!=null, un dossier forme de sous-dossiers et de fichiers texte
	 * @param mot, mot!=null
	 * @return Retourne le nombre de fichiers figurant dans le dossier dossier et dans ses sous-dossiers qui contiennent
	 *  le mot mot sur au moins une de leurs lignes.
	 */
	public static int nbContient(File dossier, String mot) {
		return -1; // A VOUS DE COMPLETER
	}


}
