import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestsLog  implements ClipboardOwner {
	public static int DELAI = 250; // Temps d'attente entre les affichages lors d'une levee d'exception


	public static Class getClass(String nomClasse) {
		Class t;
		try  {
			t = Class.forName(nomClasse);
			return t;
		}  catch (ClassNotFoundException e) {
			return null;
		}
	}

	public static boolean isAbstract(Class c) {
		return Modifier.isAbstract(c.getModifiers());
	}

	public static boolean implementsInterface(Class c, Class interfac) {
		List<Class> interfaces = Arrays.asList( c.getInterfaces() );
		return interfaces.contains(interfac);
	}

	public static boolean extendsClass(Class c, Class superClasse) {
		return (c.getSuperclass().equals(superClasse));
	}

	public static Class[] getArgs(String[] argsClass) {
		if (argsClass==null) {
			return null;
		}
		Class[] args = new Class[ argsClass.length];
		for (int i=0; i<argsClass.length; i++) {
			if (argsClass[i].equals("int")) {
				args[i]=int.class;
			} else if (argsClass[i].equals("String")) {
				args[i]=String.class;
			} else {
				args[i]=getClass(argsClass[i]);
			}
		}
		return args;

	}
	public static Constructor getConstructor(Class c, Class[] args) {
		Constructor cons=null;
		try {
			//System.out.println(Arrays.toString(c.getConstructors()));
			cons= c.getConstructor(args);
		} catch(Exception e) {};
		return cons;
	}
	public static Constructor getConstructor(Class c, String[] argsClass) {
		return getConstructor( c, getArgs(argsClass));
	}

	public static Method getMethode(Class c, String methode, Class[] args) {
		try {
			return c.getMethod(methode, args);
		} catch (Exception nonCapturee) {
			return null;
		}
	}
	public static Method getMethode(Class c, String methode,String[] args) {
		return getMethode(c, methode,  getArgs(args));
	}
	public static String remove(String fic, String mot) {
		String res = fic; 
		while (res.contains(mot)) {
			res=res.replace(mot,"");
		}
		return res;
	}
	public static String removeStartingChars(String s, char c) {
		String res= s;
		while (res!=null && res.length()>0 && res.charAt(0)==c) {
			res=res.substring(1);
		}
		return res;
	}
	public static String startingId(String s) {
		String res= s;
		String delimiters = " \t,;(){}";
		while (s.length()>0 && delimiters.contains(""+s.charAt(0))) {
			s=s.substring(1);
		}
		int end=0;

		while (res!=null && res.length()>0 && end<res.length() && !delimiters.contains(""+res.charAt(end))) {
			end++;
		}
		return res.substring(0, end);
	}
	public static String withoutStartingId(String s) {
		String res= s;
		String delimiters = " \t,;(){}"+(char)10+(char)13;
		while (res.length()>0 && delimiters.contains(""+res.charAt(0))) {
			res=res.substring(1);
		}
		int end=0;
		while (res!=null && res.length()>0 && end<res.length() && !delimiters.contains(""+res.charAt(end))) {
			end++;
		}
		return res.substring(end, res.length());
	}
	public static String constructeurNomFic(String nomfic, String nomClasse, String[] args) {
		String fic = sansCommentaires(nomfic);
		return constructeur(fic, nomClasse, args);
	}
	public static String sansEspacesAvantParentheses(String s) {
		String res = s;
		while (res.indexOf(" (")>=0) {
			res = res.replace(" (","(");
		}
		while (res.indexOf("\t(")>=0) {
			res = res.replace("\t(","(");
		}
		return res;
	}
	public static String constructeur(String fic, String nomClasse, String[] args) {
		fic = sansEspacesAvantParentheses(fic);
		int index = fic.indexOf(nomClasse+"(");
		if (index<0) {
			return "";
		} else {
			//System.out.println("index = "+index);
			fic = fic.substring(index);
			fic = withoutStartingId(fic);
			if (fic.length()>0) fic = fic.substring(1);
			//System.out.println(" trouve : "+fic);
			//int ip=index+1;
			int p = 0;
			while (//ip<fic.length() &&
					p<args.length 
					&& fic.charAt(0)!=')') {
				//fic = removeStartingChars(fic, ' ');
				//fic = removeStartingChars(fic, '\t');
				if (startingId(fic).equals(args[p])) {
					//System.out.println("avant="+fic);
					//System.out.println("un seul = "+withoutStartingId(fic));
					fic = withoutStartingId( withoutStartingId(fic));
					//System.out.println("apres="+fic);
					p++;
				} else {
					//	System.out.println("aie... fic="+fic+"\n ne commence pas par "+args[p]);
					return constructeur(fic, nomClasse, args);
				}
			}
			//System.out.println("FFic="+fic);
			fic =removeStartingChars(fic,' ');
			fic = removeStartingChars(fic, '\t');
			//System.out.println(" trouve : "+fic);System.exit(0);
			if (p<args.length || fic.length()==0 || fic.charAt(0)!=')') {
				//System.out.println(" trouve : "+fic);//System.exit(0);
				return  constructeur(fic, nomClasse, args);
			}
			int i= 0;//index+1;
			while (i<fic.length() && fic.charAt(i)!='{') {
				i++;
			}
			i++;
			int nbOuvertes=1;
			while (nbOuvertes>0 && i<fic.length()) {
				if (fic.charAt(i)=='{') {
					nbOuvertes++;
				}
				if (fic.charAt(i)=='}') {
					nbOuvertes--;
				}
				i++;
			}
			if (i<fic.length()) {
				//return fic.substring(index, i);
				return fic.substring(fic.indexOf("{"), i);
			} else {
				return "";
			}
		}
	}
	public static String methode(String fic, String nomMethode) {
		//System.out.println("fic recu par methode() : "+fic);
		int index = fic.indexOf(nomMethode);
		if (index<0) {
			return "";
		} else {
			int i= index+1;
			while (i<fic.length() && fic.charAt(i)!='{') {
				i++;
			}
			i++;
			int nbOuvertes=1;
			while (nbOuvertes>0 && i<fic.length()) {
				if (fic.charAt(i)=='{') {
					nbOuvertes++;
				}
				if (fic.charAt(i)=='}') {
					nbOuvertes--;
				}
				i++;
			}
			if (i<fic.length()) {
				//	System.out.println("fic renvoye par methode() : "+fic.substring(index, i));
				return fic.substring(index, i);
			} else {
				return "";
			}
		}
	}

	//	public static List<String> lignesSansCommentaires(String fic) {
	//		List<String> l=new ArrayList<String>();
	//		List<String> m=new ArrayList<String>();
	//		//String res="";
	//		try {
	//			FileReader fr = new FileReader(fic);
	//			BufferedReader br = new BufferedReader( fr );
	//			String s;
	//			do {
	//				s = br.readLine();
	//				if (s!=null) {
	//					l.add(s);
	//				}
	//			} while (s!=null);
	//
	//			//Iterator<String> it = br.lines().iterator();
	//
	//			//String s;
	//			//while (it.hasNext()) {
	//			//	s =it.next();
	//			/*				int index = s.indexOf("//");
	//				if (index>=0) { 
	//					s=s.substring(0, index);
	//				}
	//			 */			//	l.add(s);
	//			//}
	//
	//			br.close();
	//		} catch (Exception e) {
	//			System.out.println(e);
	//		}	
	//		int i=0; 
	//
	//		while (i<l.size()) {
	//			int debut = l.get(i).indexOf("/*");
	//			int fin = l.get(i).indexOf("*/");
	//			if (debut>=0) {
	//				if (fin>=0) {
	//					m.add(l.get(i).substring(0, debut)+""+ l.get(i).substring(fin+2));
	//				} else {
	//					m.add(l.get(i).substring(0, debut));
	//					i++;
	//					while (i<l.size() && l.get(i).indexOf("*/")<0) {
	//						i++;
	//					}
	//					fin = l.get(i).indexOf("*/");
	//					m.add(l.get(i).substring(fin+2));
	//				}
	//			} else if ( l.get(i).indexOf("//")>=0) {
	//				m.add(l.get(i).substring(0, l.get(i).indexOf("//")));
	//			}	else {
	//				m.add(l.get(i));
	//			}
	//			i++;
	//		}
	//		//for (String r : m) {
	//		//	res=res+r+"\n";
	//		//}
	//		return m;//res;
	//	}
	//	public static String sansCommentaires(String fic) {
	//		return reunirLignes( lignesSansCommentaires(fic));
	//	}
	public static List<String> lignesSansCommentaires(String fic) {
		List<String> l=new ArrayList<String>();
		List<String> m=new ArrayList<String>();
		//String res="";
		try {
			FileReader fr = new FileReader(fic);
			BufferedReader br = new BufferedReader( fr );
			String s;
			do {
				s = br.readLine();
				if (s!=null) {
					l.add(s);
				}
			} while (s!=null);

			//Iterator<String> it = br.lines().iterator();

			//String s;
			//while (it.hasNext()) {
			//	s =it.next();
			/*				int index = s.indexOf("//");
				if (index>=0) { 
					s=s.substring(0, index);
				}
			 */			//	l.add(s);
			//}

			br.close();
		} catch (Exception e) {
			System.out.println(e);
		}	
		int i=0; 

		while (i<l.size()) {
			int debut = l.get(i).indexOf("/*");
			int fin = l.get(i).indexOf("*/");
			if (debut>=0) {
				int iCL = l.get(i).indexOf("//");
				if (iCL>=0 && iCL<debut) {
					m.add(l.get(i).substring(0, iCL));
				} else if (fin>=0) {
					m.add(l.get(i).substring(0, debut)+""+ l.get(i).substring(fin+2));
				} else {
					m.add(l.get(i).substring(0, debut));
					i++;
					while (i<l.size() && l.get(i).indexOf("*/")<0) {
						i++;
					}
					fin = l.get(i).indexOf("*/");
					m.add(l.get(i).substring(fin+2));
				}
			} else if ( l.get(i).indexOf("//")>=0) {
				m.add(l.get(i).substring(0, l.get(i).indexOf("//")));
			}	else {
				m.add(l.get(i));
			}
			i++;
		}
		//for (String r : m) {
		//	res=res+r+"\n";
		//}
		return m;//res;
	}
	public static String sansCommentaires(String fic) {
		return reunirLignes( lignesSansCommentaires(fic));
	}
	public static String reunirLignes(List<String> lignes) {
		String res="";
		for (String r : lignes) {
			res=res+r+"\n";
		}
		return res;
	}


	public static int nbDeclaredFields(Class c) {
		return c.getDeclaredFields().length;
	}
	public static int nbDeclaredFieldsOfType(Class c, Class typ) {
		Field[] fields = c.getDeclaredFields();
		int nb=0;
		for (Field f : fields) {
			if (f.getType().equals(typ)) {
				nb++;
			}
		}
		return nb;
	}

	public static boolean fieldsDeclaredPrivate(//String file,
			Class c) {
		//	boolean res=true;
		//	List<String> lignes = lignesSansCommentaires(file);
		Field[] fields = c.getDeclaredFields();
		for (Field f : fields) {
			if (!Modifier.isPrivate(f.getModifiers())) {
				return false;
			}
		}
		return true;
	}
	/*	public static boolean fieldsDeclaredPrivate(String file, Class c) {
		boolean res=true;
		List<String> lignes = lignesSansCommentaires(file);
		Field[] fields = c.getDeclaredFields();
		for (Field f : fields) {
			int lig =0;
			while (lig<lignes.size() && !lignes.get(lig).contains(f.getName())) {
				lig++;
			}
			if (lig<lignes.size() && !lignes.get(lig).contains("private")) {
				res=false;
			}
		}
		return res;
	}*/
	public static boolean hasFieldOfType(Class c, Class type ) {
		boolean res=true;
		Field[] fields = c.getDeclaredFields();
		for (Field f : fields) {
			if (f.getType().equals(type)) {
				return true;
			} 
		}
		return false;
	}

	public static List<String> getMots(String s) {
		List<String> l = new ArrayList<String>();
		String delimiteurs = " ,.;:/?%*+=-<>\\_()[]{}&"+(char)13+"|"+(char)10+"\t";
		String mot="";
		for (int i=0; i<s.length(); i++) {
			if (delimiteurs.contains(""+s.charAt(i))) {
				if (!mot.equals("")) {
					l.add(mot);
					mot="";
				}
			} else {
				mot=mot+s.charAt(i);
			}
		}
		return l;

	}

	public static boolean estUneVariableLocale(String file, Class c, String methode, String var) {
		String met = methode(sansCommentaires(file),methode);
		List<String> mots = getMots(met);
		int pos = mots.indexOf(var);
		if (pos>0) {
			String[] primitifs= {"int", "char", "boolean", "float", "long", "double"};
			List<String> lprimitifs = Arrays.asList(primitifs);
			if (lprimitifs.contains(mots.get(pos-1))) {
				return true;
			} else {
				//System.out.println(c.getPackage().getName());
				return getClass(mots.get(pos-1))!=null ||  getClass(c.getPackage().getName()+"."+mots.get(pos-1))!=null;
			}
		} else {
			return false;
		}
	}
	public static boolean respecteDeveloppementEnCouche(String file, Class c, String methode) {
		String met = methode(sansCommentaires(file),methode);

		//System.out.println(" ---- avant retrait des methodes : "+met);
		Method[] methodes = c.getDeclaredMethods();
		String sans = met;
		for (Method m : methodes) {
			sans = remove(sans, m.getName());
		}
		//System.out.println(" ---- apres retrait des methodes : "+sans);
		boolean couche = true;
		Field[] fields = c.getDeclaredFields();

		int i=0;
		while (couche && i<fields.length) {
			//System.out.println("field "+fields[i].getName()+"\n"+sans);
			//System.out.println("mots :"+getMots(sans));
			if (getMots(sans).contains(fields[i].getName())){// && sans.contains(fields[i].getName()+".")) {
				if (!estUneVariableLocale(file, c, methode,fields[i].getName() )) {
					couche=false;
				}
				//System.out.println("field "+fields[i].getName()+" trouve");
			}
			i++;
		}
		return couche;
	}

	public static void afficheThrowable( Throwable e, String test) {
		System.out.println(">>>>>> AIE..."+test+" lance "+e.toString());
		try {Thread.sleep(DELAI);} catch (Exception ex) {};
		e.printStackTrace();
		try {Thread.sleep(DELAI);} catch (Exception ex) {};
		if (e.getCause()!=null) { 
			System.out.println("Cause :");
			e.getCause().printStackTrace();
		}
		try {Thread.sleep(DELAI);} catch (Exception ex) {};
		System.out.println("<<<<<<<<<<<<<<<<<<");
	}

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////START
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//	public static int testIdentiques() {
	//		int note=100;
	//		System.out.println("Test de identiques :");
	//		int[] i1 = {2, 12, 4, 2, 2, 12, 6};
	//		int[] i2 = {2, 12, 4, 2, 2, 12, 6};
	//		int[] i3 = {3, 12, 4, 2, 2, 12, 6};
	//		int[] i4 = {2, 1, 4, 2, 2, 12, 6};
	//		int[] i5 = {2, 12, 4, 2, 2, 12, 16};
	//		int[] i6 = {2, 12, 4, 2, 2, 12};
	//		int[] i7 = {2, 12, 6, 2, 2, 12, 4};
	//		if (!Tableaux.identiques(i1, i2)) {
	//			System.out.println("   Aie... identiques("+toString(i1)+", "+toString(i2)+") retourne false au lieu de true");
	//			note=0;
	//		} else if (Tableaux.identiques(i1, i3)) {
	//			System.out.println("   Aie... identiques("+toString(i1)+", "+toString(i3)+") retourne true au lieu de false");
	//			note=0;
	//		} else if (Tableaux.identiques(i1, i4)) {
	//			System.out.println("   Aie... identiques("+toString(i1)+", "+toString(i4)+") retourne true au lieu de false");
	//			note=0;
	//		} else if (Tableaux.identiques(i1, i5)) {
	//			System.out.println("   Aie... identiques("+toString(i1)+", "+toString(i5)+") retourne true au lieu de false");
	//			note=0;
	//		} else if (Tableaux.identiques(i1, i6)) {
	//			System.out.println("   Aie... identiques("+toString(i1)+", "+toString(i6)+") retourne true au lieu de false");
	//			note=0;
	//		} else if (Tableaux.identiques(i1, i7)) {
	//			System.out.println("   Aie... identiques("+toString(i1)+", "+toString(i7)+") retourne true au lieu de false");
	//			note=0;
	//		} 
	//		if (note>0) {
	//			String met = methode(sansCommentaires("src"+File.separator+"Tableaux.java"),"identiques");
	//			//System.out.println(met);
	//			if (met.contains("for")) {
	//				System.out.println("   Aie... vous utilisez une boucle for dans votre implementation de identiques alors qu'une boucle while aurait ete plus judicieuse pour cette fonction");
	//				note=note-25;
	//			}
	//		} 
	//		if (note==100) {		
	//			System.out.println("  Ok. Votre code passe le test");
	//		}
	//		return note;
	//	}
	//	
	//	
	//	
	//	
	//	public static int testNbOccurrences() {
	//		int note=100;
	//
	//		System.out.println("\nTest de nbOccurrences :");
	//		int[] t = {2, 12, 4, 2, 2, 12, 6};
	//		if (Tableaux.nbOccurrences(t,2)!=3) {
	//			System.out.println("  Aie... nbOccurrences({2, 12, 4, 2, 2, 12, 6}, 2) retourne "+Tableaux.nbOccurrences(t,2)+" au lieu de 3");
	//			note=0;
	//		} else if (Tableaux.nbOccurrences(t,12)!=2){
	//			System.out.println("  Aie... nbOccurrences({2, 12, 4, 2, 2, 12, 6}, 12) retourne "+Tableaux.nbOccurrences(t,12)+" au lieu de 2");
	//			note=0;
	//		} else if (Tableaux.nbOccurrences(t,4)!=1){
	//			System.out.println("  Aie... nbOccurrences({2, 12, 4, 2, 2, 12, 6}, 4) retourne "+Tableaux.nbOccurrences(t,4)+" au lieu de 1");
	//			note=0;
	//		} else if (Tableaux.nbOccurrences(t,6)!=1){
	//			System.out.println("  Aie... nbOccurrences({2, 12, 4, 2, 2, 12, 6}, 6) retourne "+Tableaux.nbOccurrences(t,6)+" au lieu de 1");
	//			note=0;
	//		} else if (Tableaux.nbOccurrences(t,60)!=0){
	//			System.out.println("  Aie... nbOccurrences({2, 12, 4, 2, 2, 12, 6}, 60) retourne "+Tableaux.nbOccurrences(t,60)+" au lieu de 0");
	//			note=0;
	//		}
	//		if (note>0) {
	//			String met = methode(sansCommentaires("src"+File.separator+"Tableaux.java"),"nbOccurrences");
	//			//System.out.println(met);
	//			if (met.contains("nbOccurrencesRec")) {
	//				System.out.println("   Aie... votre implementation de nbOccurrences ne doit pas utiliser la fonction nbOccurrencesRec");
	//				note=0;
	//			}
	//		} 
	//		if (note>0) {
	//			String met = methode(sansCommentaires("src"+File.separator+"Tableaux.java"),"nbOccurrences").substring(2);
	//			//System.out.println(met);
	//			if (met.contains("nbOccurrences")) {
	//				System.out.println("   Aie... votre implementation de nbOccurrences ne doit pas s'inspirer de la fonction nbOccurrencesRec");
	//				note=0;
	//			}
	//		} 
	//		if (note>0) {
	//			String met = methode(sansCommentaires("src"+File.separator+"Tableaux.java"),"nbOccurrences");
	//			//System.out.println(met);
	//			if (met.contains("while")) {
	//				System.out.println("   Aie... vous utilisez une boucle while dans votre implementation de nbOccurrences alors qu'une boucle for aurait ete plus judicieuse pour cette fonction");
	//				note=note-25;
	//			}
	//		} 
	//		if (note==100) {		
	//			System.out.println("  Ok. Votre code passe le test");
	//		}
	//		return note;
	//	}	
	//	
	//	public static int testEgaux() {
	//		int note=100;
	//
	//		System.out.println("\nTest de egaux :");
	//		int[] e1 = {2, 1, 4, 2, 2, 1};
	//		int[] e2 = {2, 1, 2, 4, 2, 1};
	//		int[] e3 = {4, 1, 2, 2, 2, 1};
	//		int[] e4 = {1, 2, 4, 2, 2, 1};
	//		int[] e5 = {2, 1, 4, 2, 1, 2};
	//		int[] e6 = {2, 2, 4, 2, 2, 1};
	//		int[] e7 = {2, 1, 4, 2, 2, 2};
	//		int[] e8 = {2, 1, 1, 2, 2, 1};
	//		int[] e9 = {2, 1, 4, 2, 2};
	//
	//		if (!Tableaux.egaux(e1, e2)) {
	//			System.out.println("   Aie... egaux("+toString(e1)+", "+toString(e2)+") retourne false au lieu de true");
	//			note=0;
	//		} else if (!Tableaux.egaux(e1, e3)) {
	//			System.out.println("   Aie... egaux("+toString(e1)+", "+toString(e3)+") retourne false au lieu de true");
	//			note=0;
	//		} else if (!Tableaux.egaux(e1, e4)) {
	//			System.out.println("   Aie... egaux("+toString(e1)+", "+toString(e4)+") retourne false au lieu de true");
	//			note=0;
	//		} else if (!Tableaux.egaux(e1, e5)) {
	//			System.out.println("   Aie... egaux("+toString(e1)+", "+toString(e5)+") retourne false au lieu de true");
	//			note=0;
	//		} else if (Tableaux.egaux(e1, e6)) {
	//			System.out.println("   Aie... egaux("+toString(e1)+", "+toString(e6)+") retourne true au lieu de false");
	//			note=0;
	//		} else if (Tableaux.egaux(e1, e7)) {
	//			System.out.println("   Aie... egaux("+toString(e1)+", "+toString(e7)+") retourne true au lieu de false");
	//			note=0;
	//		} else if (Tableaux.egaux(e1, e8)) {
	//			System.out.println("   Aie... egaux("+toString(e1)+", "+toString(e8)+") retourne true au lieu de false");
	//			note=0;
	//		} else if (Tableaux.egaux(e1, e9)) {
	//			System.out.println("   Aie... egaux("+toString(e1)+", "+toString(e9)+") retourne true au lieu de false");
	//			note=0;
	//		} 
	//		if (note>0) {
	//			String met = methode(sansCommentaires("src"+File.separator+"Tableaux.java"),"egaux");
	//			//System.out.println(met);
	//			if (met.contains("for")) {
	//				System.out.println("   Aie... vous utilisez une boucle for dans votre implementation de egaux alors qu'une boucle while aurait ete plus judicieuse pour cette fonction");
	//				note=note-25;
	//			}
	//		} 
	//		if (note==100) {		
	//			System.out.println("  Ok. Votre code passe le test");
	//		}
	//		return note;
	//	}	
	//
	//	public static int testSupprimerToutes() {
	//		int note=100;
	//		System.out.println("\nTest de supprimerToutes :");
	//		int[] t = {2, 12, 4, 2, 2, 12, 6};
	//		int[] r2 = {12, 4, 12, 6};
	//		int[] r12 = {2, 4, 2, 2, 6};
	//		int[] r4 = {2, 12, 2, 2, 12, 6};
	//
	//		if (Tableaux.supprimerToutes(t,2)==null ) {
	//			System.out.println("  Aie... supprimerToutes({2, 12, 4, 2, 2, 12, 6}, 2) retourne null");
	//			note=0;
	//		} else if (Tableaux.supprimerToutes(t,200)==t ){
	//			System.out.println("  Aie... supprimerToutes({2, 12, 4, 2, 2, 12, 6}, 200) retourne le tableau passe en parametre au lieu d'une copie de celui ci");
	//			note=0;
	//		} else if (!equals(Tableaux.supprimerToutes(t,2), r2)){
	//			System.out.println("  Aie... supprimerToutes({2, 12, 4, 2, 2, 12, 6}, 2) retourne "+toString(Tableaux.supprimerToutes(t,2))+" au lieu de "+toString(r2));
	//			note=0;
	//		} else  if (!equals(Tableaux.supprimerToutes(t,12), r12)){
	//			System.out.println("  Aie... supprimerToutes({2, 12, 4, 2, 2, 12, 6}, 12) retourne "+toString(Tableaux.supprimerToutes(t,12))+" au lieu de "+toString(r12));
	//			note=0;
	//		} else if (!equals(Tableaux.supprimerToutes(t,4), r4)){
	//			System.out.println("  Aie... supprimerToutes({2, 12, 4, 2, 2, 12, 6}, 4) retourne "+toString(Tableaux.supprimerToutes(t,4))+" au lieu de "+toString(r4));
	//			note=0;
	//		} 
	//		if (note==100) {		
	//			System.out.println("  Ok. Votre code passe le test");
	//		}
	//		return note;
	//	}	
	//	public static int testSupprimer() {
	//		int note=100;
	//		System.out.println("\nTest de supprimer :");
	//
	//		int[] t = {2, 12, 4, 2, 2, 12, 6};
	//
	//		int[] rr2 = {12, 4, 2, 2, 12, 6};
	//		int[] rr12 = {2, 4, 2, 2, 12, 6};
	//		int[] rr4 = {2, 12, 2, 2, 12, 6};
	//
	//		if (Tableaux.supprimer(t,2)==null ) {
	//			System.out.println("  Aie... supprimer({2, 12, 4, 2, 2, 12, 6}, 2) retourne null");
	//			note=0;
	//		} else if (Tableaux.supprimer(t,200)==t ){
	//			System.out.println("  Aie... supprimer({2, 12, 4, 2, 2, 12, 6}, 200) retourne le tableau passe en parametre au lieu d'une copie de celui ci");
	//			note=0;
	//		} else if (!equals(Tableaux.supprimer(t,2), rr2)){
	//			System.out.println("  Aie... supprimer({2, 12, 4, 2, 2, 12, 6}, 2) retourne "+toString(Tableaux.supprimer(t,2))+" au lieu de "+toString(rr2));
	//			note=0;
	//		} else  if (!equals(Tableaux.supprimer(t,12), rr12)){
	//			System.out.println("  Aie... supprimer({2, 12, 4, 2, 2, 12, 6}, 12) retourne "+toString(Tableaux.supprimer(t,12))+" au lieu de "+toString(rr12));
	//			note=0;
	//		} else if (!equals(Tableaux.supprimer(t,4), rr4)){
	//			System.out.println("  Aie... supprimer({2, 12, 4, 2, 2, 12, 6}, 4) retourne "+toString(Tableaux.supprimer(t,4))+" au lieu de "+toString(rr4));
	//			note=0;
	//		} 
	//		if (note==100) {		
	//			System.out.println("  Ok. Votre code passe le test");
	//		}
	//		return note;
	//	}	
	//
	/*
	public static int testTriangulaire() {
		int note=100;
		int[] res = {0, 
				1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 105, 120, 136, 153, 171, 190, 210, 
				231, 253, 276, 300, 325, 351, 378, 406, 435, 465, 496, 528, 561, 595, 630, 666, 703, 741, 780, 820, 
				861, 903, 946, 990, 1035, 1081, 1128, 1176, 1225, 1275, 1326, 1378, 1431, 1485, 1540, 1596, 1653, 1711, 1770, 1830, 
				1891, 1953, 2016, 2080, 2145, 2211, 2278, 2346, 2415, 2485, 2556, 2628, 2701, 2775, 2850, 2926, 3003, 3081, 3160, 3240, 
				3321, 3403, 3486, 3570, 3655, 3741, 3828, 3916, 4005, 4095, 4186, 4278, 4371, 4465, 4560, 4656, 4753, 4851, 4950, 5050, 
				5151, 5253, 5356, 5460, 5565, 5671, 5778, 5886, 5995, 6105, 6216, 6328, 6441, 6555, 6670, 6786, 6903, 7021, 7140};
		System.out.println("Test de triangulaire :");
	 */
	/*for (int i=0; i<120; i++) {
			System.out.print(NombresTriangulaires.triangulaire(i)+", ");
			if (i%20==0) {
				System.out.println();
			}
		}*/
	/*	int k=0;
		boolean ok=true;
		while (k<res.length && ok) {
			ok = (NombresTriangulaires.triangulaire(k)==res[k]);
			if (!ok) {
				System.out.println("   Aie... triangulaire("+k+") retourne "+NombresTriangulaires.triangulaire(k)+" au lieu de "+res[k]);
				note=0;
			}
			k++;
		}
		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"NombresTriangulaires.java"),"triangulaire").substring(2);
			//System.out.println(met);
			if (!met.contains("triangulaire")) {
				System.out.println("   Aie... votre implementation de triangulaire doit etre RECURSIVE");
				note=0;
			}
		} 


		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}

	public static int testDebutRec() {
		int note=100;
		String[] jeux = {"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890",
				"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa",
		"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"};

		String[][] resDeb= {{"", 
			"a", "ab", "abc", 
			"abcd", "abcde", "abcdef", 
			"abcdefg", "abcdefgh", "abcdefghi", 
			"abcdefghij", "abcdefghijk", "abcdefghijkl", 
			"abcdefghijklm", "abcdefghijklmn", "abcdefghijklmno", 
			"abcdefghijklmnop", "abcdefghijklmnopq", "abcdefghijklmnopqr", 
			"abcdefghijklmnopqrs", "abcdefghijklmnopqrst", "abcdefghijklmnopqrstu", 
			"abcdefghijklmnopqrstuv", "abcdefghijklmnopqrstuvw", "abcdefghijklmnopqrstuvwx", 
			"abcdefghijklmnopqrstuvwxy", "abcdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyzA", 
			"abcdefghijklmnopqrstuvwxyzAB", "abcdefghijklmnopqrstuvwxyzABC", "abcdefghijklmnopqrstuvwxyzABCD", 
			"abcdefghijklmnopqrstuvwxyzABCDE", "abcdefghijklmnopqrstuvwxyzABCDEF", "abcdefghijklmnopqrstuvwxyzABCDEFG", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGH", "abcdefghijklmnopqrstuvwxyzABCDEFGHI", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJ", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJK", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKL", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLM", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNO", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOP", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQ", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRS", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRST", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTU", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUV", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVW", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWX", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"}
		,{
			"", 
			"f", "ff", "ffd", 
			"ffdf", "ffdfk", "ffdfkl", 
			"ffdfklf", "ffdfklfj", "ffdfklfjk", 
			"ffdfklfjkl", "ffdfklfjkls", "ffdfklfjklsf", 
			"ffdfklfjklsfj", "ffdfklfjklsfjl", "ffdfklfjklsfjlj", 
			"ffdfklfjklsfjljk", "ffdfklfjklsfjljkl", "ffdfklfjklsfjljkle", 
			"ffdfklfjklsfjljklej", "ffdfklfjklsfjljklejz", "ffdfklfjklsfjljklejzz", 
			"ffdfklfjklsfjljklejzzr", "ffdfklfjklsfjljklejzzri", "ffdfklfjklsfjljklejzzriz", 
			"ffdfklfjklsfjljklejzzrizo", "ffdfklfjklsfjljklejzzrizoj", "ffdfklfjklsfjljklejzzrizojn", 
			"ffdfklfjklsfjljklejzzrizojnj", "ffdfklfjklsfjljklejzzrizojnjn", "ffdfklfjklsfjljklejzzrizojnjnk", 
			"ffdfklfjklsfjljklejzzrizojnjnkn", "ffdfklfjklsfjljklejzzrizojnjnknj", "ffdfklfjklsfjljklejzzrizojnjnknjn", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnk", "ffdfklfjklsfjljklejzzrizojnjnknjnkj", "ffdfklfjklsfjljklejzzrizojnjnknjnkjq", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqn", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqna", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaq", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqaz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqaza", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazaz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazl", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlx", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxm", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzl", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzld", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzo", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzos", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzosz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszx", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzd", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdz", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzd", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzz", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzo", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa"
		},{
			"", 
			"a", "aa", "aaa", 
			"aaas", "aaask", "aaaskj", 
			"aaaskjn", "aaaskjnx", "aaaskjnxa", 
			"aaaskjnxad", "aaaskjnxadk", "aaaskjnxadkd", 
			"aaaskjnxadkdq", "aaaskjnxadkdqz", "aaaskjnxadkdqzn", 
			"aaaskjnxadkdqznd", "aaaskjnxadkdqzndl", "aaaskjnxadkdqzndlx", 
			"aaaskjnxadkdqzndlxd", "aaaskjnxadkdqzndlxdz", "aaaskjnxadkdqzndlxdzd", 
			"aaaskjnxadkdqzndlxdzdz", "aaaskjnxadkdqzndlxdzdze", "aaaskjnxadkdqzndlxdzdzex", 
			"aaaskjnxadkdqzndlxdzdzexd", "aaaskjnxadkdqzndlxdzdzexdd", "aaaskjnxadkdqzndlxdzdzexddd", 
			"aaaskjnxadkdqzndlxdzdzexddde", "aaaskjnxadkdqzndlxdzdzexddded", "aaaskjnxadkdqzndlxdzdzexdddedx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxf", "aaaskjnxadkdqzndlxdzdzexdddedxfq", "aaaskjnxadkdqzndlxdzdzexdddedxfqg", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgq", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxq", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxe", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxeg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxege", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxeger", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegerg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergr", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgr", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgre", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgreg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrg", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrx", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxz", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgx", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxh", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"		
		}};
		String[][] resFin= {
				{
					"", 
					"0", "90", "890", 
					"7890", "67890", "567890", 
					"4567890", "34567890", "234567890", 
					"1234567890", "Z1234567890", "YZ1234567890", 
					"XYZ1234567890", "WXYZ1234567890", "VWXYZ1234567890", 
					"UVWXYZ1234567890", "TUVWXYZ1234567890", "STUVWXYZ1234567890", 
					"RSTUVWXYZ1234567890", "QRSTUVWXYZ1234567890", "PQRSTUVWXYZ1234567890", 
					"OPQRSTUVWXYZ1234567890", "NOPQRSTUVWXYZ1234567890", "MNOPQRSTUVWXYZ1234567890", 
					"LMNOPQRSTUVWXYZ1234567890", "KLMNOPQRSTUVWXYZ1234567890", "JKLMNOPQRSTUVWXYZ1234567890", 
					"IJKLMNOPQRSTUVWXYZ1234567890", "HIJKLMNOPQRSTUVWXYZ1234567890", "GHIJKLMNOPQRSTUVWXYZ1234567890", 
					"FGHIJKLMNOPQRSTUVWXYZ1234567890", "EFGHIJKLMNOPQRSTUVWXYZ1234567890", "DEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"CDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "BCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"zABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "yzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "xyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"wxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "vwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "uvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"tuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "stuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "rstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"qrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "pqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "opqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"nopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "mnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "lmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"klmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "jklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"hijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "fghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"efghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "defghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "cdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"bcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
				}, {
					"", 
					"a", "oa", "zoa", 
					"zzoa", "dzzoa", "zdzzoa", 
					"dzdzzoa", "zdzdzzoa", "xzdzdzzoa", 
					"zxzdzdzzoa", "szxzdzdzzoa", "oszxzdzdzzoa", 
					"zoszxzdzdzzoa", "dzoszxzdzdzzoa", "ldzoszxzdzdzzoa", 
					"zldzoszxzdzdzzoa", "mzldzoszxzdzdzzoa", "xmzldzoszxzdzdzzoa", 
					"lxmzldzoszxzdzdzzoa", "zlxmzldzoszxzdzdzzoa", "azlxmzldzoszxzdzdzzoa", 
					"zazlxmzldzoszxzdzdzzoa", "azazlxmzldzoszxzdzdzzoa", "qazazlxmzldzoszxzdzdzzoa", 
					"aqazazlxmzldzoszxzdzdzzoa", "naqazazlxmzldzoszxzdzdzzoa", "qnaqazazlxmzldzoszxzdzdzzoa", 
					"jqnaqazazlxmzldzoszxzdzdzzoa", "kjqnaqazazlxmzldzoszxzdzdzzoa", "nkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jnkjqnaqazazlxmzldzoszxzdzdzzoa", "njnkjqnaqazazlxmzldzoszxzdzdzzoa", "knjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"nknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "njnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "zojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"izojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "rizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "zrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"zzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"lejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "klejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"ljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"sfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "lsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "klsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "lfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"klfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "dfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"fdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa"
				}, {
					"", 
					"h", "hh", "hhh", 
					"xhhh", "gxhhh", "zgxhhh", 
					"xzgxhhh", "rxzgxhhh", "grxzgxhhh", 
					"rgrxzgxhhh", "grgrxzgxhhh", "xgrgrxzgxhhh", 
					"gxgrgrxzgxhhh", "egxgrgrxzgxhhh", "regxgrgrxzgxhhh", 
					"gregxgrgrxzgxhhh", "rgregxgrgrxzgxhhh", "rrgregxgrgrxzgxhhh", 
					"grrgregxgrgrxzgxhhh", "rgrrgregxgrgrxzgxhhh", "ergrrgregxgrgrxzgxhhh", 
					"gergrrgregxgrgrxzgxhhh", "egergrrgregxgrgrxzgxhhh", "xegergrrgregxgrgrxzgxhhh", 
					"gxegergrrgregxgrgrxzgxhhh", "qgxegergrrgregxgrgrxzgxhhh", "xqgxegergrrgregxgrgrxzgxhhh", 
					"gxqgxegergrrgregxgrgrxzgxhhh", "qgxqgxegergrrgregxgrgrxzgxhhh", "gqgxqgxegergrrgregxgrgrxzgxhhh", 
					"qgqgxqgxegergrrgregxgrgrxzgxhhh", "fqgqgxqgxegergrrgregxgrgrxzgxhhh", "xfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"dxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "edxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"ddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "xdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"exdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "zexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"zdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "xdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"lxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "ndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"zndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "qzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"kdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "adkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"xadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "nxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "jnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"kjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "skjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "askjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"aaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"	
				}
		};
		System.out.println("\nTest de debutRec :");
	 */
	/*
		for (int i=0; i<=s3.length()+3; i++) {
			System.out.print("\""+Chaines.finRec(s3, i)+"\", ");
			if (i%3==0) {
				System.out.println();
			}
		}*/
	/*	int k=0;
		int jeu=0;
		boolean ok=true;
		while (jeu<resDeb.length && ok) {

			k=0;
			while (k<resDeb[jeu].length && ok) {
				ok = (Chaines.debutRec(jeux[jeu], k).equals(resDeb[jeu][k]));
				if (!ok) {
					System.out.println("   Aie... debutRec(\""+jeux[jeu]+"\", "+k+") retourne \""+Chaines.debutRec(jeux[jeu], k)+"\" au lieu de \""+resDeb[jeu][k]+"\"");
					note=0;
				}
				k++;
			}
			jeu++;
		}
		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Chaines.java"),"debutRec").substring(2);
			//System.out.println(met);
			if (!met.contains("debutRec")) {
				System.out.println("   Aie... votre implementation de debutRec doit etre RECURSIVE");
				note=0;
			}
		} 


		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}





	public static int testFinRec() {
		int note=100;
		String[] jeux = {"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890",
				"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa",
		"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"};

		String[][] resDeb= {{"", 
			"a", "ab", "abc", 
			"abcd", "abcde", "abcdef", 
			"abcdefg", "abcdefgh", "abcdefghi", 
			"abcdefghij", "abcdefghijk", "abcdefghijkl", 
			"abcdefghijklm", "abcdefghijklmn", "abcdefghijklmno", 
			"abcdefghijklmnop", "abcdefghijklmnopq", "abcdefghijklmnopqr", 
			"abcdefghijklmnopqrs", "abcdefghijklmnopqrst", "abcdefghijklmnopqrstu", 
			"abcdefghijklmnopqrstuv", "abcdefghijklmnopqrstuvw", "abcdefghijklmnopqrstuvwx", 
			"abcdefghijklmnopqrstuvwxy", "abcdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyzA", 
			"abcdefghijklmnopqrstuvwxyzAB", "abcdefghijklmnopqrstuvwxyzABC", "abcdefghijklmnopqrstuvwxyzABCD", 
			"abcdefghijklmnopqrstuvwxyzABCDE", "abcdefghijklmnopqrstuvwxyzABCDEF", "abcdefghijklmnopqrstuvwxyzABCDEFG", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGH", "abcdefghijklmnopqrstuvwxyzABCDEFGHI", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJ", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJK", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKL", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLM", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNO", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOP", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQ", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRS", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRST", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTU", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUV", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVW", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWX", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"}
		,{
			"", 
			"f", "ff", "ffd", 
			"ffdf", "ffdfk", "ffdfkl", 
			"ffdfklf", "ffdfklfj", "ffdfklfjk", 
			"ffdfklfjkl", "ffdfklfjkls", "ffdfklfjklsf", 
			"ffdfklfjklsfj", "ffdfklfjklsfjl", "ffdfklfjklsfjlj", 
			"ffdfklfjklsfjljk", "ffdfklfjklsfjljkl", "ffdfklfjklsfjljkle", 
			"ffdfklfjklsfjljklej", "ffdfklfjklsfjljklejz", "ffdfklfjklsfjljklejzz", 
			"ffdfklfjklsfjljklejzzr", "ffdfklfjklsfjljklejzzri", "ffdfklfjklsfjljklejzzriz", 
			"ffdfklfjklsfjljklejzzrizo", "ffdfklfjklsfjljklejzzrizoj", "ffdfklfjklsfjljklejzzrizojn", 
			"ffdfklfjklsfjljklejzzrizojnj", "ffdfklfjklsfjljklejzzrizojnjn", "ffdfklfjklsfjljklejzzrizojnjnk", 
			"ffdfklfjklsfjljklejzzrizojnjnkn", "ffdfklfjklsfjljklejzzrizojnjnknj", "ffdfklfjklsfjljklejzzrizojnjnknjn", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnk", "ffdfklfjklsfjljklejzzrizojnjnknjnkj", "ffdfklfjklsfjljklejzzrizojnjnknjnkjq", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqn", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqna", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaq", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqaz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqaza", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazaz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazl", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlx", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxm", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzl", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzld", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzo", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzos", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzosz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszx", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzd", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdz", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzd", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzz", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzo", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa"
		},{
			"", 
			"a", "aa", "aaa", 
			"aaas", "aaask", "aaaskj", 
			"aaaskjn", "aaaskjnx", "aaaskjnxa", 
			"aaaskjnxad", "aaaskjnxadk", "aaaskjnxadkd", 
			"aaaskjnxadkdq", "aaaskjnxadkdqz", "aaaskjnxadkdqzn", 
			"aaaskjnxadkdqznd", "aaaskjnxadkdqzndl", "aaaskjnxadkdqzndlx", 
			"aaaskjnxadkdqzndlxd", "aaaskjnxadkdqzndlxdz", "aaaskjnxadkdqzndlxdzd", 
			"aaaskjnxadkdqzndlxdzdz", "aaaskjnxadkdqzndlxdzdze", "aaaskjnxadkdqzndlxdzdzex", 
			"aaaskjnxadkdqzndlxdzdzexd", "aaaskjnxadkdqzndlxdzdzexdd", "aaaskjnxadkdqzndlxdzdzexddd", 
			"aaaskjnxadkdqzndlxdzdzexddde", "aaaskjnxadkdqzndlxdzdzexddded", "aaaskjnxadkdqzndlxdzdzexdddedx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxf", "aaaskjnxadkdqzndlxdzdzexdddedxfq", "aaaskjnxadkdqzndlxdzdzexdddedxfqg", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgq", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxq", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxe", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxeg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxege", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxeger", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegerg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergr", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgr", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgre", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgreg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrg", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrx", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxz", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgx", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxh", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"		
		}};
		String[][] resFin= {
				{
					"", 
					"0", "90", "890", 
					"7890", "67890", "567890", 
					"4567890", "34567890", "234567890", 
					"1234567890", "Z1234567890", "YZ1234567890", 
					"XYZ1234567890", "WXYZ1234567890", "VWXYZ1234567890", 
					"UVWXYZ1234567890", "TUVWXYZ1234567890", "STUVWXYZ1234567890", 
					"RSTUVWXYZ1234567890", "QRSTUVWXYZ1234567890", "PQRSTUVWXYZ1234567890", 
					"OPQRSTUVWXYZ1234567890", "NOPQRSTUVWXYZ1234567890", "MNOPQRSTUVWXYZ1234567890", 
					"LMNOPQRSTUVWXYZ1234567890", "KLMNOPQRSTUVWXYZ1234567890", "JKLMNOPQRSTUVWXYZ1234567890", 
					"IJKLMNOPQRSTUVWXYZ1234567890", "HIJKLMNOPQRSTUVWXYZ1234567890", "GHIJKLMNOPQRSTUVWXYZ1234567890", 
					"FGHIJKLMNOPQRSTUVWXYZ1234567890", "EFGHIJKLMNOPQRSTUVWXYZ1234567890", "DEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"CDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "BCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"zABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "yzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "xyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"wxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "vwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "uvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"tuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "stuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "rstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"qrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "pqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "opqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"nopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "mnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "lmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"klmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "jklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"hijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "fghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"efghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "defghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "cdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"bcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
				}, {
					"", 
					"a", "oa", "zoa", 
					"zzoa", "dzzoa", "zdzzoa", 
					"dzdzzoa", "zdzdzzoa", "xzdzdzzoa", 
					"zxzdzdzzoa", "szxzdzdzzoa", "oszxzdzdzzoa", 
					"zoszxzdzdzzoa", "dzoszxzdzdzzoa", "ldzoszxzdzdzzoa", 
					"zldzoszxzdzdzzoa", "mzldzoszxzdzdzzoa", "xmzldzoszxzdzdzzoa", 
					"lxmzldzoszxzdzdzzoa", "zlxmzldzoszxzdzdzzoa", "azlxmzldzoszxzdzdzzoa", 
					"zazlxmzldzoszxzdzdzzoa", "azazlxmzldzoszxzdzdzzoa", "qazazlxmzldzoszxzdzdzzoa", 
					"aqazazlxmzldzoszxzdzdzzoa", "naqazazlxmzldzoszxzdzdzzoa", "qnaqazazlxmzldzoszxzdzdzzoa", 
					"jqnaqazazlxmzldzoszxzdzdzzoa", "kjqnaqazazlxmzldzoszxzdzdzzoa", "nkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jnkjqnaqazazlxmzldzoszxzdzdzzoa", "njnkjqnaqazazlxmzldzoszxzdzdzzoa", "knjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"nknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "njnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "zojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"izojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "rizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "zrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"zzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"lejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "klejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"ljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"sfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "lsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "klsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "lfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"klfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "dfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"fdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa"
				}, {
					"", 
					"h", "hh", "hhh", 
					"xhhh", "gxhhh", "zgxhhh", 
					"xzgxhhh", "rxzgxhhh", "grxzgxhhh", 
					"rgrxzgxhhh", "grgrxzgxhhh", "xgrgrxzgxhhh", 
					"gxgrgrxzgxhhh", "egxgrgrxzgxhhh", "regxgrgrxzgxhhh", 
					"gregxgrgrxzgxhhh", "rgregxgrgrxzgxhhh", "rrgregxgrgrxzgxhhh", 
					"grrgregxgrgrxzgxhhh", "rgrrgregxgrgrxzgxhhh", "ergrrgregxgrgrxzgxhhh", 
					"gergrrgregxgrgrxzgxhhh", "egergrrgregxgrgrxzgxhhh", "xegergrrgregxgrgrxzgxhhh", 
					"gxegergrrgregxgrgrxzgxhhh", "qgxegergrrgregxgrgrxzgxhhh", "xqgxegergrrgregxgrgrxzgxhhh", 
					"gxqgxegergrrgregxgrgrxzgxhhh", "qgxqgxegergrrgregxgrgrxzgxhhh", "gqgxqgxegergrrgregxgrgrxzgxhhh", 
					"qgqgxqgxegergrrgregxgrgrxzgxhhh", "fqgqgxqgxegergrrgregxgrgrxzgxhhh", "xfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"dxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "edxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"ddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "xdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"exdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "zexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"zdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "xdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"lxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "ndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"zndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "qzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"kdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "adkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"xadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "nxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "jnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"kjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "skjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "askjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"aaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"	
				}
		};
		System.out.println("\nTest de finRec :");
	 */
	/*
		for (int i=0; i<=s3.length()+3; i++) {
			System.out.print("\""+Chaines.finRec(s3, i)+"\", ");
			if (i%3==0) {
				System.out.println();
			}
		}*/
	/*	int k=0;
		int jeu=0;
		boolean ok=true;
		while (jeu<resFin.length && ok) {

			k=0;
			while (k<resFin[jeu].length && ok) {
				ok = (Chaines.finRec(jeux[jeu], k).equals(resFin[jeu][k]));
				if (!ok) {
					System.out.println("   Aie... finRec(\""+jeux[jeu]+"\", "+k+") retourne \""+Chaines.finRec(jeux[jeu], k)+"\" au lieu de \""+resFin[jeu][k]+"\"");
					note=0;
				}
				k++;
			}
			jeu++;
		}
		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Chaines.java"),"finRec").substring(2);
			//System.out.println(met);
			if (!met.contains("finRec")) {
				System.out.println("   Aie... votre implementation de finRec doit etre RECURSIVE");
				note=0;
			}
		} 


		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}

	public static int testDebutIte() {
		int note=100;
		String[] jeux = {"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890",
				"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa",
		"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"};

		String[][] resDeb= {{"", 
			"a", "ab", "abc", 
			"abcd", "abcde", "abcdef", 
			"abcdefg", "abcdefgh", "abcdefghi", 
			"abcdefghij", "abcdefghijk", "abcdefghijkl", 
			"abcdefghijklm", "abcdefghijklmn", "abcdefghijklmno", 
			"abcdefghijklmnop", "abcdefghijklmnopq", "abcdefghijklmnopqr", 
			"abcdefghijklmnopqrs", "abcdefghijklmnopqrst", "abcdefghijklmnopqrstu", 
			"abcdefghijklmnopqrstuv", "abcdefghijklmnopqrstuvw", "abcdefghijklmnopqrstuvwx", 
			"abcdefghijklmnopqrstuvwxy", "abcdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyzA", 
			"abcdefghijklmnopqrstuvwxyzAB", "abcdefghijklmnopqrstuvwxyzABC", "abcdefghijklmnopqrstuvwxyzABCD", 
			"abcdefghijklmnopqrstuvwxyzABCDE", "abcdefghijklmnopqrstuvwxyzABCDEF", "abcdefghijklmnopqrstuvwxyzABCDEFG", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGH", "abcdefghijklmnopqrstuvwxyzABCDEFGHI", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJ", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJK", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKL", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLM", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNO", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOP", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQ", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRS", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRST", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTU", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUV", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVW", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWX", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"}
		,{
			"", 
			"f", "ff", "ffd", 
			"ffdf", "ffdfk", "ffdfkl", 
			"ffdfklf", "ffdfklfj", "ffdfklfjk", 
			"ffdfklfjkl", "ffdfklfjkls", "ffdfklfjklsf", 
			"ffdfklfjklsfj", "ffdfklfjklsfjl", "ffdfklfjklsfjlj", 
			"ffdfklfjklsfjljk", "ffdfklfjklsfjljkl", "ffdfklfjklsfjljkle", 
			"ffdfklfjklsfjljklej", "ffdfklfjklsfjljklejz", "ffdfklfjklsfjljklejzz", 
			"ffdfklfjklsfjljklejzzr", "ffdfklfjklsfjljklejzzri", "ffdfklfjklsfjljklejzzriz", 
			"ffdfklfjklsfjljklejzzrizo", "ffdfklfjklsfjljklejzzrizoj", "ffdfklfjklsfjljklejzzrizojn", 
			"ffdfklfjklsfjljklejzzrizojnj", "ffdfklfjklsfjljklejzzrizojnjn", "ffdfklfjklsfjljklejzzrizojnjnk", 
			"ffdfklfjklsfjljklejzzrizojnjnkn", "ffdfklfjklsfjljklejzzrizojnjnknj", "ffdfklfjklsfjljklejzzrizojnjnknjn", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnk", "ffdfklfjklsfjljklejzzrizojnjnknjnkj", "ffdfklfjklsfjljklejzzrizojnjnknjnkjq", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqn", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqna", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaq", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqaz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqaza", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazaz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazl", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlx", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxm", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzl", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzld", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzo", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzos", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzosz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszx", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzd", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdz", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzd", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzz", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzo", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa"
		},{
			"", 
			"a", "aa", "aaa", 
			"aaas", "aaask", "aaaskj", 
			"aaaskjn", "aaaskjnx", "aaaskjnxa", 
			"aaaskjnxad", "aaaskjnxadk", "aaaskjnxadkd", 
			"aaaskjnxadkdq", "aaaskjnxadkdqz", "aaaskjnxadkdqzn", 
			"aaaskjnxadkdqznd", "aaaskjnxadkdqzndl", "aaaskjnxadkdqzndlx", 
			"aaaskjnxadkdqzndlxd", "aaaskjnxadkdqzndlxdz", "aaaskjnxadkdqzndlxdzd", 
			"aaaskjnxadkdqzndlxdzdz", "aaaskjnxadkdqzndlxdzdze", "aaaskjnxadkdqzndlxdzdzex", 
			"aaaskjnxadkdqzndlxdzdzexd", "aaaskjnxadkdqzndlxdzdzexdd", "aaaskjnxadkdqzndlxdzdzexddd", 
			"aaaskjnxadkdqzndlxdzdzexddde", "aaaskjnxadkdqzndlxdzdzexddded", "aaaskjnxadkdqzndlxdzdzexdddedx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxf", "aaaskjnxadkdqzndlxdzdzexdddedxfq", "aaaskjnxadkdqzndlxdzdzexdddedxfqg", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgq", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxq", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxe", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxeg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxege", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxeger", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegerg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergr", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgr", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgre", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgreg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrg", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrx", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxz", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgx", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxh", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"		
		}};
		String[][] resFin= {
				{
					"", 
					"0", "90", "890", 
					"7890", "67890", "567890", 
					"4567890", "34567890", "234567890", 
					"1234567890", "Z1234567890", "YZ1234567890", 
					"XYZ1234567890", "WXYZ1234567890", "VWXYZ1234567890", 
					"UVWXYZ1234567890", "TUVWXYZ1234567890", "STUVWXYZ1234567890", 
					"RSTUVWXYZ1234567890", "QRSTUVWXYZ1234567890", "PQRSTUVWXYZ1234567890", 
					"OPQRSTUVWXYZ1234567890", "NOPQRSTUVWXYZ1234567890", "MNOPQRSTUVWXYZ1234567890", 
					"LMNOPQRSTUVWXYZ1234567890", "KLMNOPQRSTUVWXYZ1234567890", "JKLMNOPQRSTUVWXYZ1234567890", 
					"IJKLMNOPQRSTUVWXYZ1234567890", "HIJKLMNOPQRSTUVWXYZ1234567890", "GHIJKLMNOPQRSTUVWXYZ1234567890", 
					"FGHIJKLMNOPQRSTUVWXYZ1234567890", "EFGHIJKLMNOPQRSTUVWXYZ1234567890", "DEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"CDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "BCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"zABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "yzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "xyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"wxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "vwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "uvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"tuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "stuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "rstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"qrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "pqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "opqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"nopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "mnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "lmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"klmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "jklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"hijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "fghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"efghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "defghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "cdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"bcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
				}, {
					"", 
					"a", "oa", "zoa", 
					"zzoa", "dzzoa", "zdzzoa", 
					"dzdzzoa", "zdzdzzoa", "xzdzdzzoa", 
					"zxzdzdzzoa", "szxzdzdzzoa", "oszxzdzdzzoa", 
					"zoszxzdzdzzoa", "dzoszxzdzdzzoa", "ldzoszxzdzdzzoa", 
					"zldzoszxzdzdzzoa", "mzldzoszxzdzdzzoa", "xmzldzoszxzdzdzzoa", 
					"lxmzldzoszxzdzdzzoa", "zlxmzldzoszxzdzdzzoa", "azlxmzldzoszxzdzdzzoa", 
					"zazlxmzldzoszxzdzdzzoa", "azazlxmzldzoszxzdzdzzoa", "qazazlxmzldzoszxzdzdzzoa", 
					"aqazazlxmzldzoszxzdzdzzoa", "naqazazlxmzldzoszxzdzdzzoa", "qnaqazazlxmzldzoszxzdzdzzoa", 
					"jqnaqazazlxmzldzoszxzdzdzzoa", "kjqnaqazazlxmzldzoszxzdzdzzoa", "nkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jnkjqnaqazazlxmzldzoszxzdzdzzoa", "njnkjqnaqazazlxmzldzoszxzdzdzzoa", "knjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"nknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "njnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "zojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"izojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "rizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "zrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"zzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"lejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "klejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"ljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"sfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "lsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "klsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "lfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"klfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "dfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"fdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa"
				}, {
					"", 
					"h", "hh", "hhh", 
					"xhhh", "gxhhh", "zgxhhh", 
					"xzgxhhh", "rxzgxhhh", "grxzgxhhh", 
					"rgrxzgxhhh", "grgrxzgxhhh", "xgrgrxzgxhhh", 
					"gxgrgrxzgxhhh", "egxgrgrxzgxhhh", "regxgrgrxzgxhhh", 
					"gregxgrgrxzgxhhh", "rgregxgrgrxzgxhhh", "rrgregxgrgrxzgxhhh", 
					"grrgregxgrgrxzgxhhh", "rgrrgregxgrgrxzgxhhh", "ergrrgregxgrgrxzgxhhh", 
					"gergrrgregxgrgrxzgxhhh", "egergrrgregxgrgrxzgxhhh", "xegergrrgregxgrgrxzgxhhh", 
					"gxegergrrgregxgrgrxzgxhhh", "qgxegergrrgregxgrgrxzgxhhh", "xqgxegergrrgregxgrgrxzgxhhh", 
					"gxqgxegergrrgregxgrgrxzgxhhh", "qgxqgxegergrrgregxgrgrxzgxhhh", "gqgxqgxegergrrgregxgrgrxzgxhhh", 
					"qgqgxqgxegergrrgregxgrgrxzgxhhh", "fqgqgxqgxegergrrgregxgrgrxzgxhhh", "xfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"dxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "edxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"ddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "xdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"exdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "zexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"zdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "xdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"lxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "ndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"zndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "qzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"kdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "adkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"xadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "nxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "jnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"kjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "skjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "askjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"aaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"	
				}
		};
		System.out.println("\nTest de debutIte :");
	 */
	/*
		for (int i=0; i<=s3.length()+3; i++) {
			System.out.print("\""+Chaines.finRec(s3, i)+"\", ");
			if (i%3==0) {
				System.out.println();
			}
		}*/
	/*	int k=0;
		int jeu=0;
		boolean ok=true;
		while (jeu<resDeb.length && ok) {

			k=0;
			while (k<resDeb[jeu].length && ok) {
				ok = (Chaines.debutIte(jeux[jeu], k).equals(resDeb[jeu][k]));
				if (!ok) {
					System.out.println("   Aie... debutIte(\""+jeux[jeu]+"\", "+k+") retourne \""+Chaines.debutIte(jeux[jeu], k)+"\" au lieu de \""+resDeb[jeu][k]+"\"");
					note=0;
				}
				k++;
			}
			jeu++;
		}
		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Chaines.java"),"debutIte").substring(2);
			//System.out.println(met);
			if (met.contains("debutIte")||met.contains("debutRec")) {
				System.out.println("   Aie... votre implementation de debutIte NE doit PAS etre RECURSIVE");
				note=0;
			}
		} 


		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}





	public static int testFinIte() {
		int note=100;
		String[] jeux = {"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890",
				"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa",
		"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"};

		String[][] resDeb= {{"", 
			"a", "ab", "abc", 
			"abcd", "abcde", "abcdef", 
			"abcdefg", "abcdefgh", "abcdefghi", 
			"abcdefghij", "abcdefghijk", "abcdefghijkl", 
			"abcdefghijklm", "abcdefghijklmn", "abcdefghijklmno", 
			"abcdefghijklmnop", "abcdefghijklmnopq", "abcdefghijklmnopqr", 
			"abcdefghijklmnopqrs", "abcdefghijklmnopqrst", "abcdefghijklmnopqrstu", 
			"abcdefghijklmnopqrstuv", "abcdefghijklmnopqrstuvw", "abcdefghijklmnopqrstuvwx", 
			"abcdefghijklmnopqrstuvwxy", "abcdefghijklmnopqrstuvwxyz", "abcdefghijklmnopqrstuvwxyzA", 
			"abcdefghijklmnopqrstuvwxyzAB", "abcdefghijklmnopqrstuvwxyzABC", "abcdefghijklmnopqrstuvwxyzABCD", 
			"abcdefghijklmnopqrstuvwxyzABCDE", "abcdefghijklmnopqrstuvwxyzABCDEF", "abcdefghijklmnopqrstuvwxyzABCDEFG", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGH", "abcdefghijklmnopqrstuvwxyzABCDEFGHI", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJ", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJK", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKL", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLM", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMN", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNO", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOP", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQ", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQR", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRS", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRST", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTU", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUV", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVW", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWX", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXY", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ12345678", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
			"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"}
		,{
			"", 
			"f", "ff", "ffd", 
			"ffdf", "ffdfk", "ffdfkl", 
			"ffdfklf", "ffdfklfj", "ffdfklfjk", 
			"ffdfklfjkl", "ffdfklfjkls", "ffdfklfjklsf", 
			"ffdfklfjklsfj", "ffdfklfjklsfjl", "ffdfklfjklsfjlj", 
			"ffdfklfjklsfjljk", "ffdfklfjklsfjljkl", "ffdfklfjklsfjljkle", 
			"ffdfklfjklsfjljklej", "ffdfklfjklsfjljklejz", "ffdfklfjklsfjljklejzz", 
			"ffdfklfjklsfjljklejzzr", "ffdfklfjklsfjljklejzzri", "ffdfklfjklsfjljklejzzriz", 
			"ffdfklfjklsfjljklejzzrizo", "ffdfklfjklsfjljklejzzrizoj", "ffdfklfjklsfjljklejzzrizojn", 
			"ffdfklfjklsfjljklejzzrizojnj", "ffdfklfjklsfjljklejzzrizojnjn", "ffdfklfjklsfjljklejzzrizojnjnk", 
			"ffdfklfjklsfjljklejzzrizojnjnkn", "ffdfklfjklsfjljklejzzrizojnjnknj", "ffdfklfjklsfjljklejzzrizojnjnknjn", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnk", "ffdfklfjklsfjljklejzzrizojnjnknjnkj", "ffdfklfjklsfjljklejzzrizojnjnknjnkjq", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqn", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqna", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaq", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqaz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqaza", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazaz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazl", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlx", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxm", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzl", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzld", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzo", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzos", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzosz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszx", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzd", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdz", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzd", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdz", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzz", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzo", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
			"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa"
		},{
			"", 
			"a", "aa", "aaa", 
			"aaas", "aaask", "aaaskj", 
			"aaaskjn", "aaaskjnx", "aaaskjnxa", 
			"aaaskjnxad", "aaaskjnxadk", "aaaskjnxadkd", 
			"aaaskjnxadkdq", "aaaskjnxadkdqz", "aaaskjnxadkdqzn", 
			"aaaskjnxadkdqznd", "aaaskjnxadkdqzndl", "aaaskjnxadkdqzndlx", 
			"aaaskjnxadkdqzndlxd", "aaaskjnxadkdqzndlxdz", "aaaskjnxadkdqzndlxdzd", 
			"aaaskjnxadkdqzndlxdzdz", "aaaskjnxadkdqzndlxdzdze", "aaaskjnxadkdqzndlxdzdzex", 
			"aaaskjnxadkdqzndlxdzdzexd", "aaaskjnxadkdqzndlxdzdzexdd", "aaaskjnxadkdqzndlxdzdzexddd", 
			"aaaskjnxadkdqzndlxdzdzexddde", "aaaskjnxadkdqzndlxdzdzexddded", "aaaskjnxadkdqzndlxdzdzexdddedx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxf", "aaaskjnxadkdqzndlxdzdzexdddedxfq", "aaaskjnxadkdqzndlxdzdzexdddedxfqg", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgq", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxq", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxe", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxeg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxege", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxeger", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegerg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergr", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgr", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgre", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgreg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregx", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrg", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgr", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrx", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxz", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzg", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgx", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxh", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
			"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"		
		}};
		String[][] resFin= {
				{
					"", 
					"0", "90", "890", 
					"7890", "67890", "567890", 
					"4567890", "34567890", "234567890", 
					"1234567890", "Z1234567890", "YZ1234567890", 
					"XYZ1234567890", "WXYZ1234567890", "VWXYZ1234567890", 
					"UVWXYZ1234567890", "TUVWXYZ1234567890", "STUVWXYZ1234567890", 
					"RSTUVWXYZ1234567890", "QRSTUVWXYZ1234567890", "PQRSTUVWXYZ1234567890", 
					"OPQRSTUVWXYZ1234567890", "NOPQRSTUVWXYZ1234567890", "MNOPQRSTUVWXYZ1234567890", 
					"LMNOPQRSTUVWXYZ1234567890", "KLMNOPQRSTUVWXYZ1234567890", "JKLMNOPQRSTUVWXYZ1234567890", 
					"IJKLMNOPQRSTUVWXYZ1234567890", "HIJKLMNOPQRSTUVWXYZ1234567890", "GHIJKLMNOPQRSTUVWXYZ1234567890", 
					"FGHIJKLMNOPQRSTUVWXYZ1234567890", "EFGHIJKLMNOPQRSTUVWXYZ1234567890", "DEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"CDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "BCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"zABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "yzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "xyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"wxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "vwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "uvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"tuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "stuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "rstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"qrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "pqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "opqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"nopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "mnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "lmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"klmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "jklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"hijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "ghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "fghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"efghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "defghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "cdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"bcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", 
					"abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890", "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890"
				}, {
					"", 
					"a", "oa", "zoa", 
					"zzoa", "dzzoa", "zdzzoa", 
					"dzdzzoa", "zdzdzzoa", "xzdzdzzoa", 
					"zxzdzdzzoa", "szxzdzdzzoa", "oszxzdzdzzoa", 
					"zoszxzdzdzzoa", "dzoszxzdzdzzoa", "ldzoszxzdzdzzoa", 
					"zldzoszxzdzdzzoa", "mzldzoszxzdzdzzoa", "xmzldzoszxzdzdzzoa", 
					"lxmzldzoszxzdzdzzoa", "zlxmzldzoszxzdzdzzoa", "azlxmzldzoszxzdzdzzoa", 
					"zazlxmzldzoszxzdzdzzoa", "azazlxmzldzoszxzdzdzzoa", "qazazlxmzldzoszxzdzdzzoa", 
					"aqazazlxmzldzoszxzdzdzzoa", "naqazazlxmzldzoszxzdzdzzoa", "qnaqazazlxmzldzoszxzdzdzzoa", 
					"jqnaqazazlxmzldzoszxzdzdzzoa", "kjqnaqazazlxmzldzoszxzdzdzzoa", "nkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jnkjqnaqazazlxmzldzoszxzdzdzzoa", "njnkjqnaqazazlxmzldzoszxzdzdzzoa", "knjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"nknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "njnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "zojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"izojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "rizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "zrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"zzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"lejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "klejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"ljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "jljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"sfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "lsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "klsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"jklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "lfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"klfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "fklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "dfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"fdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", 
					"ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa", "ffdfklfjklsfjljklejzzrizojnjnknjnkjqnaqazazlxmzldzoszxzdzdzzoa"
				}, {
					"", 
					"h", "hh", "hhh", 
					"xhhh", "gxhhh", "zgxhhh", 
					"xzgxhhh", "rxzgxhhh", "grxzgxhhh", 
					"rgrxzgxhhh", "grgrxzgxhhh", "xgrgrxzgxhhh", 
					"gxgrgrxzgxhhh", "egxgrgrxzgxhhh", "regxgrgrxzgxhhh", 
					"gregxgrgrxzgxhhh", "rgregxgrgrxzgxhhh", "rrgregxgrgrxzgxhhh", 
					"grrgregxgrgrxzgxhhh", "rgrrgregxgrgrxzgxhhh", "ergrrgregxgrgrxzgxhhh", 
					"gergrrgregxgrgrxzgxhhh", "egergrrgregxgrgrxzgxhhh", "xegergrrgregxgrgrxzgxhhh", 
					"gxegergrrgregxgrgrxzgxhhh", "qgxegergrrgregxgrgrxzgxhhh", "xqgxegergrrgregxgrgrxzgxhhh", 
					"gxqgxegergrrgregxgrgrxzgxhhh", "qgxqgxegergrrgregxgrgrxzgxhhh", "gqgxqgxegergrrgregxgrgrxzgxhhh", 
					"qgqgxqgxegergrrgregxgrgrxzgxhhh", "fqgqgxqgxegergrrgregxgrgrxzgxhhh", "xfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"dxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "edxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"ddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "xdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"exdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "zexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"zdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "xdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"lxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "ndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"zndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "qzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"kdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "dkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "adkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"xadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "nxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "jnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"kjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "skjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "askjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"aaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", 
					"aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh", "aaaskjnxadkdqzndlxdzdzexdddedxfqgqgxqgxegergrrgregxgrgrxzgxhhh"	
				}
		};
		System.out.println("\nTest de finIte :");
	 */
	/*
		for (int i=0; i<=s3.length()+3; i++) {
			System.out.print("\""+Chaines.finRec(s3, i)+"\", ");
			if (i%3==0) {
				System.out.println();
			}
		}*/
	/*		int k=0;
		int jeu=0;
		boolean ok=true;
		while (jeu<resFin.length && ok) {

			k=0;
			while (k<resFin[jeu].length && ok) {
				ok = (Chaines.finIte(jeux[jeu], k).equals(resFin[jeu][k]));
				if (!ok) {
					System.out.println("   Aie... finIte(\""+jeux[jeu]+"\", "+k+") retourne \""+Chaines.finIte(jeux[jeu], k)+"\" au lieu de \""+resFin[jeu][k]+"\"");
					note=0;
				}
				k++;
			}
			jeu++;
		}
		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Chaines.java"),"finIte").substring(2);
			//System.out.println(met);
			if (met.contains("finIte")||met.contains("finRec")) {
				System.out.println("   Aie... votre implementation de finIte NE doit PAS etre RECURSIVE");
				note=0;
			}
		} 


		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}
	public static int testSinusRec() {
		int note=100;

		System.out.println("\nTest de sinusRec : ");
		boolean ok=true;
		for (double angle=0.0; ok && angle<3.0; angle+=0.01) {
			ok = Math.abs(Trigonometrie.sinusRec(angle)-Math.sin(angle))<0.01;
			if (!ok) {
				System.out.println("   Aie... sinusRec("+angle+") retourne "+Trigonometrie.sinusRec(angle)+" au lieu de "+Math.sin(angle));
				note=0;
			}
		}
		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Trigonometrie.java"),"sinusRec").substring(2);
			if (!met.contains("sinusRec")) {
				System.out.println("   Aie... votre implementation de sinusRec doit etre RECURSIVE");
				note=0;
			} else if (met.contains("Math.")) {
				System.out.println("   Aie... vous ne devez pas utiliser les methodes de Math");
				note=0;
			}
		} 

		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}
	public static int testCosinusRec() {
		int note=100;

		System.out.println("\nTest de cosinusRec : ");
		boolean ok=true;
		for (double angle=0.0; ok && angle<3.0; angle+=0.01) {
			ok = Math.abs(Trigonometrie.cosinusRec(angle)-Math.cos(angle))<0.01;
			if (!ok) {
				System.out.println("   Aie... cosinusRec("+angle+") retourne "+Trigonometrie.cosinusRec(angle)+" au lieu de "+Math.cos(angle));
				note=0;
			}
		}
		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Trigonometrie.java"),"cosinusRec").substring(2);
			if (!met.contains("cosinusRec")) {
				System.out.println("   Aie... votre implementation de cosinusRec doit etre RECURSIVE");
				note=0;
			} else if (met.contains("Math.")) {
				System.out.println("   Aie... vous ne devez pas utiliser les methodes de Math");
				note=0;
			}
		} 
		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}

	public static int testLastIndexOf() {
		int note=100;
		int[][] t = {{1, 2, 3, 1, 2, 3, 1, 2, 3}, 
				{1, 2, 3, 4, 5, 6, 7, 8, 9, 0},
				{0, 9, 8, 7, 6, 5, 4, 3, 2, 1},
				{2, 2, 2, 2, 3, 3, 3, 3, 4, 1, 2, 3, 4}
		};
		int[] resL = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 3, 1, 2, -1, -1, -1, -1, -1, -1, -1, 3, 4, 2, -1, -1, -1, -1, -1, -1, -1, 3, 4, 5, -1, -1, -1, -1, -1, -1, -1, 6, 4, 5, -1, -1, -1, -1, -1, -1, -1, 6, 7, 5, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, -1, -1, -1, -1, -1, -1, -1, 2, 1, 0, -1, -1, -1, -1, -1, -1, 3, 2, 1, 0, -1, -1, -1, -1, -1, 4, 3, 2, 1, 0, -1, -1, -1, -1, 5, 4, 3, 2, 1, 0, -1, -1, -1, 6, 5, 4, 3, 2, 1, 0, -1, -1, 7, 6, 5, 4, 3, 2, 1, 0, -1, 8, 7, 6, 5, 4, 3, 2, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3, -1, -1, -1, -1, -1, -1, -1, -1, -1, 3, 4, -1, -1, -1, -1, -1, -1, -1, -1, 3, 5, -1, -1, -1, -1, -1, -1, -1, -1, 3, 6, -1, -1, -1, -1, -1, -1, -1, -1, 3, 7, -1, -1, -1, -1, -1, -1, -1, -1, 3, 7, 8, -1, -1, -1, -1, -1, -1, 9, 3, 7, 8, -1, -1, -1, -1, -1, -1, 9, 10, 7, 8, -1, -1, -1, -1, -1, -1, 9, 10, 11, 8, -1, -1, -1, -1, -1};
	 */
	/*int[] res = {0, 
				1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 105, 120, 136, 153, 171, 190, 210, 
				231, 253, 276, 300, 325, 351, 378, 406, 435, 465, 496, 528, 561, 595, 630, 666, 703, 741, 780, 820, 
				861, 903, 946, 990, 1035, 1081, 1128, 1176, 1225, 1275, 1326, 1378, 1431, 1485, 1540, 1596, 1653, 1711, 1770, 1830, 
				1891, 1953, 2016, 2080, 2145, 2211, 2278, 2346, 2415, 2485, 2556, 2628, 2701, 2775, 2850, 2926, 3003, 3081, 3160, 3240, 
				3321, 3403, 3486, 3570, 3655, 3741, 3828, 3916, 4005, 4095, 4186, 4278, 4371, 4465, 4560, 4656, 4753, 4851, 4950, 5050, 
				5151, 5253, 5356, 5460, 5565, 5671, 5778, 5886, 5995, 6105, 6216, 6328, 6441, 6555, 6670, 6786, 6903, 7021, 7140};*/
	//		System.out.println("\nTest de lastIndexOf :");
	/*	for (int i=0; i<t.length; i++) {
			for (int pos=0; pos<t[i].length; pos++) {
				for (int valeur=0; valeur<10; valeur++) {
					System.out.print(Index.lastIndexOf(t[i], pos, valeur)+", ");
				}
			}
		}*/
	/*		boolean ok=true;
		int ires=0;
		for (int i=0; ok && i<t.length; i++) {
			for (int pos=0; ok &&  pos<t[i].length; pos++) {
				for (int valeur=0; ok && valeur<10; valeur++) {
					//System.out.print(Index.lastIndexOf(t[i], pos, valeur)+", ");
					ok = Index.lastIndexOf(t[i], pos, valeur)==resL[ires];
					if (!ok) {
						System.out.println("   Aie... lastIndexOf("+Arrays.toString(t[i])+", "+pos+", "+valeur+") retourne "+Index.lastIndexOf(t[i], pos, valeur)+" au lieu de "+resL[ires]);
						note=0;
					}
					ires++;
				}
			}
		}

	 */
	/*	for (int i=0; i<120; i++) {
			System.out.print(NombresTriangulaires.triangulaire(i)+", ");
			if (i%20==0) {
				System.out.println();
			}
		}*/
	// lastIndexOf(int[] t, int pos, int valeur) {
	/*	int k=0;
		boolean ok=true;
		while (k<res.length && ok) {
			ok = (NombresTriangulaires.triangulaire(k)==res[k]);
			if (!ok) {
				System.out.println("   Aie... triangulaire("+k+") retourne "+NombresTriangulaires.triangulaire(k)+" au lieu de "+res[k]);
				note=0;
			}
			k++;
		}*/
	/*	if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Index.java"),"lastIndexOf").substring(2);
			//System.out.println(met);
			if (!met.contains("lastIndexOf")) {
				System.out.println("   Aie... votre implementation de lastIndexOf doit etre RECURSIVE");
				note=0;
			}
		} 


		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}


	public static int testFirstIndexOf() {
		int note=100;
		int[][] t = {{1, 2, 3, 1, 2, 3, 1, 2, 3}, 
				{1, 2, 3, 4, 5, 6, 7, 8, 9, 0},
				{0, 9, 8, 7, 6, 5, 4, 3, 2, 1},
				{2, 2, 2, 2, 3, 3, 3, 3, 4, 1, 2, 3, 4}
		};
		int[] resL = { -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, -1, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, -1, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, -1, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, -1, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, -1, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, -1, -1, 0, 1, 2, 3, 4, 5, 6, 7, 8, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, 1, 0, -1, -1, -1, -1, -1, -1, -1, 2, 1, 0, -1, -1, -1, -1, -1, -1, 3, 2, 1, 0, -1, -1, -1, -1, -1, 4, 3, 2, 1, 0, -1, -1, -1, -1, 5, 4, 3, 2, 1, 0, -1, -1, -1, 6, 5, 4, 3, 2, 1, 0, -1, -1, 7, 6, 5, 4, 3, 2, 1, 0, -1, 8, 7, 6, 5, 4, 3, 2, 1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, -1, -1, -1, -1, -1, -1, -1, -1, -1, 0, 4, -1, -1, -1, -1, -1, -1, -1, -1, 0, 4, -1, -1, -1, -1, -1, -1, -1, -1, 0, 4, -1, -1, -1, -1, -1, -1, -1, -1, 0, 4, -1, -1, -1, -1, -1, -1, -1, -1, 0, 4, 8, -1, -1, -1, -1, -1, -1, 9, 0, 4, 8, -1, -1, -1, -1, -1, -1, 9, 0, 4, 8, -1, -1, -1, -1, -1, -1, 9, 0, 4, 8, -1, -1, -1, -1, -1};
		/*int[] res = {0, 
				1, 3, 6, 10, 15, 21, 28, 36, 45, 55, 66, 78, 91, 105, 120, 136, 153, 171, 190, 210, 
				231, 253, 276, 300, 325, 351, 378, 406, 435, 465, 496, 528, 561, 595, 630, 666, 703, 741, 780, 820, 
				861, 903, 946, 990, 1035, 1081, 1128, 1176, 1225, 1275, 1326, 1378, 1431, 1485, 1540, 1596, 1653, 1711, 1770, 1830, 
				1891, 1953, 2016, 2080, 2145, 2211, 2278, 2346, 2415, 2485, 2556, 2628, 2701, 2775, 2850, 2926, 3003, 3081, 3160, 3240, 
				3321, 3403, 3486, 3570, 3655, 3741, 3828, 3916, 4005, 4095, 4186, 4278, 4371, 4465, 4560, 4656, 4753, 4851, 4950, 5050, 
				5151, 5253, 5356, 5460, 5565, 5671, 5778, 5886, 5995, 6105, 6216, 6328, 6441, 6555, 6670, 6786, 6903, 7021, 7140};*/
	//	System.out.println("\nTest de firstIndexOf :");
	/*
		for (int i=0; i<t.length; i++) {
			for (int pos=0; pos<t[i].length; pos++) {
				for (int valeur=0; valeur<10; valeur++) {
					System.out.print(Index.firstIndexOf(t[i], pos, valeur)+", ");
				}
			}
		}//*/
	//*	
	/*	boolean ok=true;
		int ires=0;
		for (int i=0; ok && i<t.length; i++) {
			for (int pos=0; ok &&  pos<t[i].length; pos++) {
				for (int valeur=0; ok && valeur<10; valeur++) {
					//System.out.print(Index.lastIndexOf(t[i], pos, valeur)+", ");
					ok = Index.firstIndexOf(t[i], pos, valeur)==resL[ires];
					if (!ok) {
						System.out.println("   Aie... firstIndexOf("+Arrays.toString(t[i])+", "+pos+", "+valeur+") retourne "+Index.firstIndexOf(t[i], pos, valeur)+" au lieu de "+resL[ires]);
						note=0;
					}
					ires++;
				}
			}
		}

		//*/	
	/*	for (int i=0; i<120; i++) {
			System.out.print(NombresTriangulaires.triangulaire(i)+", ");
			if (i%20==0) {
				System.out.println();
			}
		}*/
	// lastIndexOf(int[] t, int pos, int valeur) {
	/*	int k=0;
		boolean ok=true;
		while (k<res.length && ok) {
			ok = (NombresTriangulaires.triangulaire(k)==res[k]);
			if (!ok) {
				System.out.println("   Aie... triangulaire("+k+") retourne "+NombresTriangulaires.triangulaire(k)+" au lieu de "+res[k]);
				note=0;
			}
			k++;
		}*/
	///*	
	/*		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Index.java"),"firstIndexOf").substring(2);
			//System.out.println(met);
			if (!met.contains("firstIndexOf")) {
				System.out.println("   Aie... votre implementation de firstIndexOf doit etre RECURSIVE");
				note=0;
			}
		} 

		// */
	/*		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}


	public static int testNbVoyelles() {
		int note=100;
		int[] res= {1, 3, 3, 2, 2, 6, 3, 10, 15, 8, 8, 6, 10, 8, 11, 13, 27, 8, 12};
		String[] t = {"NON", "OUI", "BONJOUR", "MATIN", "JOLI", "AUJOURD'HUI", "CE MATIN", "DURANT TOUTE LA JOURNEE", "AEOIUAAUIOJNJNJNIOIUAJ", "HIEUIHIAYSNY", "OIUSOZUOSJKIU", "AZSSWQIUDIHIZUNXKZLNL", "AOIZUOISUXUZOOZ", "AOAZIUOSIU", "OIUOIDOZUOUOAD", "AOIUOUIIDOSZZIOJOSA", "SZON2OSNOOAIOZUOJSAAUIUYIXIUZYIZYIUXJNIEUYGFGHFS", "MLKMZEDEMDZEMODMZSIOEJADLKZ", "AZZZ AIU IZ AOIZ UY ZZYU "};


			System.out.println("Test de nbVoyelles :");
		/*
			for (String s : t) {
			System.out.print(nbVoyelles(s)+", ");
		}
		}*/
	/*		int k=0;
		boolean ok=true;
		while (k<res.length && ok) {
			ok = (Voyelles.nbVoyelles(t[k])==res[k]);
			if (!ok) {
				System.out.println("   Aie... nbVoyelles(\""+t[k]+"\") retourne "+Voyelles.nbVoyelles(t[k])+" au lieu de "+res[k]);
				note=0;
			}
			k++;
		}
		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Voyelles.java"),"nbVoyelles").substring(2);
			//System.out.println(met);
			if (!met.contains("nbVoyelles")) {
				System.out.println("   Aie... votre implementation de nbVoyelles doit etre RECURSIVE");
				note=0;
			}
		} 


		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}

	public static int testPGCDrec() {
		int note=100;

		int[][] res = {
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
				{1, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1},
				{1, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2},
				{1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5},
				{1, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1},
				{1, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2},
				{1, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1},
				{1, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 3, 4, 1, 6, 1, 4, 3, 2, 1, 12, 1, 2, 3, 4, 1, 6, 1, 4, 3, 2, 1, 12, 1, 2, 3, 4, 1, 6, 1, 4, 3, 2, 1, 12, 1, 2, 3, 4, 1, 6, 1, 4, 3, 2, 1, 12, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 13, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 13, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 13, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 2, 1, 2, 7, 2, 1, 2, 1, 2, 1, 14, 1, 2, 1, 2, 1, 2, 7, 2, 1, 2, 1, 2, 1, 14, 1, 2, 1, 2, 1, 2, 7, 2, 1, 2, 1, 2, 1, 14, 1, 2, 1, 2, 1, 2, 7, 2},
				{1, 1, 1, 3, 1, 5, 3, 1, 1, 3, 5, 1, 3, 1, 1, 15, 1, 1, 3, 1, 5, 3, 1, 1, 3, 5, 1, 3, 1, 1, 15, 1, 1, 3, 1, 5, 3, 1, 1, 3, 5, 1, 3, 1, 1, 15, 1, 1, 3, 1, 5},
				{1, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 16, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 16, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 16, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 17, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 17, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 3, 2, 1, 6, 1, 2, 9, 2, 1, 6, 1, 2, 3, 2, 1, 18, 1, 2, 3, 2, 1, 6, 1, 2, 9, 2, 1, 6, 1, 2, 3, 2, 1, 18, 1, 2, 3, 2, 1, 6, 1, 2, 9, 2, 1, 6, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 19, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 19, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 4, 5, 2, 1, 4, 1, 10, 1, 4, 1, 2, 5, 4, 1, 2, 1, 20, 1, 2, 1, 4, 5, 2, 1, 4, 1, 10, 1, 4, 1, 2, 5, 4, 1, 2, 1, 20, 1, 2, 1, 4, 5, 2, 1, 4, 1, 10},
				{1, 1, 1, 3, 1, 1, 3, 7, 1, 3, 1, 1, 3, 1, 7, 3, 1, 1, 3, 1, 1, 21, 1, 1, 3, 1, 1, 3, 7, 1, 3, 1, 1, 3, 1, 7, 3, 1, 1, 3, 1, 1, 21, 1, 1, 3, 1, 1, 3, 7, 1},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 11, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 22, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 11, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 22, 1, 2, 1, 2, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 23, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 23, 1, 1, 1, 1},
				{1, 1, 2, 3, 4, 1, 6, 1, 8, 3, 2, 1, 12, 1, 2, 3, 8, 1, 6, 1, 4, 3, 2, 1, 24, 1, 2, 3, 4, 1, 6, 1, 8, 3, 2, 1, 12, 1, 2, 3, 8, 1, 6, 1, 4, 3, 2, 1, 24, 1, 2},
				{1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 25, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 25},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 13, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 26, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 13, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
				{1, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 27, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1},
				{1, 1, 2, 1, 4, 1, 2, 7, 4, 1, 2, 1, 4, 1, 14, 1, 4, 1, 2, 1, 4, 7, 2, 1, 4, 1, 2, 1, 28, 1, 2, 1, 4, 1, 2, 7, 4, 1, 2, 1, 4, 1, 14, 1, 4, 1, 2, 1, 4, 7, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 29, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 3, 2, 5, 6, 1, 2, 3, 10, 1, 6, 1, 2, 15, 2, 1, 6, 1, 10, 3, 2, 1, 6, 5, 2, 3, 2, 1, 30, 1, 2, 3, 2, 5, 6, 1, 2, 3, 10, 1, 6, 1, 2, 15, 2, 1, 6, 1, 10},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 31, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 16, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 32, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 16, 1, 2},
				{1, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 11, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 11, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 33, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 11, 3, 1, 1, 3, 1, 1},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 17, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 34, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
				{1, 1, 1, 1, 1, 5, 1, 7, 1, 1, 5, 1, 1, 1, 7, 5, 1, 1, 1, 1, 5, 7, 1, 1, 1, 5, 1, 1, 7, 1, 5, 1, 1, 1, 1, 35, 1, 1, 1, 1, 5, 1, 7, 1, 1, 5, 1, 1, 1, 7, 5},
				{1, 1, 2, 3, 4, 1, 6, 1, 4, 9, 2, 1, 12, 1, 2, 3, 4, 1, 18, 1, 4, 3, 2, 1, 12, 1, 2, 9, 4, 1, 6, 1, 4, 3, 2, 1, 36, 1, 2, 3, 4, 1, 6, 1, 4, 9, 2, 1, 12, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 37, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 19, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 38, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
				{1, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 13, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 13, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 39, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1},
				{1, 1, 2, 1, 4, 5, 2, 1, 8, 1, 10, 1, 4, 1, 2, 5, 8, 1, 2, 1, 20, 1, 2, 1, 8, 5, 2, 1, 4, 1, 10, 1, 8, 1, 2, 5, 4, 1, 2, 1, 40, 1, 2, 1, 4, 5, 2, 1, 8, 1, 10},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 41, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 3, 2, 1, 6, 7, 2, 3, 2, 1, 6, 1, 14, 3, 2, 1, 6, 1, 2, 21, 2, 1, 6, 1, 2, 3, 14, 1, 6, 1, 2, 3, 2, 7, 6, 1, 2, 3, 2, 1, 42, 1, 2, 3, 2, 1, 6, 7, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 43, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 11, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 22, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 11, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 44, 1, 2, 1, 4, 1, 2},
				{1, 1, 1, 3, 1, 5, 3, 1, 1, 9, 5, 1, 3, 1, 1, 15, 1, 1, 9, 1, 5, 3, 1, 1, 3, 5, 1, 9, 1, 1, 15, 1, 1, 3, 1, 5, 9, 1, 1, 3, 5, 1, 3, 1, 1, 45, 1, 1, 3, 1, 5},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 23, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 46, 1, 2, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 47, 1, 1, 1},
				{1, 1, 2, 3, 4, 1, 6, 1, 8, 3, 2, 1, 12, 1, 2, 3, 16, 1, 6, 1, 4, 3, 2, 1, 24, 1, 2, 3, 4, 1, 6, 1, 16, 3, 2, 1, 12, 1, 2, 3, 8, 1, 6, 1, 4, 3, 2, 1, 48, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 49, 1},
				{1, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 25, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 50}
		};
		System.out.println("Test de pgcdRec : ");
		boolean ok=true;
		int i=1, j=1;
		while (ok && i<50) {
			j=1; 
			while (ok && j<=50) {
				ok = PGCD.pgcdRec(i,j)==res[i][j];
				if (!ok) {
					System.out.println("   Aie... pgcdRec("+i+", "+j+") retourne "+PGCD.pgcdRec(i,j)+" au lieu de "+res[i][j]);
					note=0;
				}
				j++;
			}
			i++;
		}

		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"PGCD.java"),"pgcdRec").substring(2);
			//System.out.println(met);
			if (!met.contains("pgcdRec")) {
				System.out.println("   Aie... votre implementation de pgcdRec doit etre RECURSIVE");
				note=0;
			}
		} 

		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}
	public static int testPGCDite() {
		int note=100;

		int[][] res = {
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
				{1, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1},
				{1, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2},
				{1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5},
				{1, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2, 3, 2, 1, 6, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1},
				{1, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2},
				{1, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1},
				{1, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 11, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 3, 4, 1, 6, 1, 4, 3, 2, 1, 12, 1, 2, 3, 4, 1, 6, 1, 4, 3, 2, 1, 12, 1, 2, 3, 4, 1, 6, 1, 4, 3, 2, 1, 12, 1, 2, 3, 4, 1, 6, 1, 4, 3, 2, 1, 12, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 13, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 13, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 13, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 2, 1, 2, 7, 2, 1, 2, 1, 2, 1, 14, 1, 2, 1, 2, 1, 2, 7, 2, 1, 2, 1, 2, 1, 14, 1, 2, 1, 2, 1, 2, 7, 2, 1, 2, 1, 2, 1, 14, 1, 2, 1, 2, 1, 2, 7, 2},
				{1, 1, 1, 3, 1, 5, 3, 1, 1, 3, 5, 1, 3, 1, 1, 15, 1, 1, 3, 1, 5, 3, 1, 1, 3, 5, 1, 3, 1, 1, 15, 1, 1, 3, 1, 5, 3, 1, 1, 3, 5, 1, 3, 1, 1, 15, 1, 1, 3, 1, 5},
				{1, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 16, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 16, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 16, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 17, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 17, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 3, 2, 1, 6, 1, 2, 9, 2, 1, 6, 1, 2, 3, 2, 1, 18, 1, 2, 3, 2, 1, 6, 1, 2, 9, 2, 1, 6, 1, 2, 3, 2, 1, 18, 1, 2, 3, 2, 1, 6, 1, 2, 9, 2, 1, 6, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 19, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 19, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 4, 5, 2, 1, 4, 1, 10, 1, 4, 1, 2, 5, 4, 1, 2, 1, 20, 1, 2, 1, 4, 5, 2, 1, 4, 1, 10, 1, 4, 1, 2, 5, 4, 1, 2, 1, 20, 1, 2, 1, 4, 5, 2, 1, 4, 1, 10},
				{1, 1, 1, 3, 1, 1, 3, 7, 1, 3, 1, 1, 3, 1, 7, 3, 1, 1, 3, 1, 1, 21, 1, 1, 3, 1, 1, 3, 7, 1, 3, 1, 1, 3, 1, 7, 3, 1, 1, 3, 1, 1, 21, 1, 1, 3, 1, 1, 3, 7, 1},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 11, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 22, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 11, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 22, 1, 2, 1, 2, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 23, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 23, 1, 1, 1, 1},
				{1, 1, 2, 3, 4, 1, 6, 1, 8, 3, 2, 1, 12, 1, 2, 3, 8, 1, 6, 1, 4, 3, 2, 1, 24, 1, 2, 3, 4, 1, 6, 1, 8, 3, 2, 1, 12, 1, 2, 3, 8, 1, 6, 1, 4, 3, 2, 1, 24, 1, 2},
				{1, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 25, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 5, 1, 1, 1, 1, 25},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 13, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 26, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 13, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
				{1, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 27, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1, 3, 1, 1, 9, 1, 1, 3, 1, 1},
				{1, 1, 2, 1, 4, 1, 2, 7, 4, 1, 2, 1, 4, 1, 14, 1, 4, 1, 2, 1, 4, 7, 2, 1, 4, 1, 2, 1, 28, 1, 2, 1, 4, 1, 2, 7, 4, 1, 2, 1, 4, 1, 14, 1, 4, 1, 2, 1, 4, 7, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 29, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 3, 2, 5, 6, 1, 2, 3, 10, 1, 6, 1, 2, 15, 2, 1, 6, 1, 10, 3, 2, 1, 6, 5, 2, 3, 2, 1, 30, 1, 2, 3, 2, 5, 6, 1, 2, 3, 10, 1, 6, 1, 2, 15, 2, 1, 6, 1, 10},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 31, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 16, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 32, 1, 2, 1, 4, 1, 2, 1, 8, 1, 2, 1, 4, 1, 2, 1, 16, 1, 2},
				{1, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 11, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 11, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 33, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 11, 3, 1, 1, 3, 1, 1},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 17, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 34, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
				{1, 1, 1, 1, 1, 5, 1, 7, 1, 1, 5, 1, 1, 1, 7, 5, 1, 1, 1, 1, 5, 7, 1, 1, 1, 5, 1, 1, 7, 1, 5, 1, 1, 1, 1, 35, 1, 1, 1, 1, 5, 1, 7, 1, 1, 5, 1, 1, 1, 7, 5},
				{1, 1, 2, 3, 4, 1, 6, 1, 4, 9, 2, 1, 12, 1, 2, 3, 4, 1, 18, 1, 4, 3, 2, 1, 12, 1, 2, 9, 4, 1, 6, 1, 4, 3, 2, 1, 36, 1, 2, 3, 4, 1, 6, 1, 4, 9, 2, 1, 12, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 37, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 19, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 38, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2},
				{1, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 13, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 13, 3, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1, 39, 1, 1, 3, 1, 1, 3, 1, 1, 3, 1, 1},
				{1, 1, 2, 1, 4, 5, 2, 1, 8, 1, 10, 1, 4, 1, 2, 5, 8, 1, 2, 1, 20, 1, 2, 1, 8, 5, 2, 1, 4, 1, 10, 1, 8, 1, 2, 5, 4, 1, 2, 1, 40, 1, 2, 1, 4, 5, 2, 1, 8, 1, 10},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 41, 1, 1, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 3, 2, 1, 6, 7, 2, 3, 2, 1, 6, 1, 14, 3, 2, 1, 6, 1, 2, 21, 2, 1, 6, 1, 2, 3, 14, 1, 6, 1, 2, 3, 2, 7, 6, 1, 2, 3, 2, 1, 42, 1, 2, 3, 2, 1, 6, 7, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 43, 1, 1, 1, 1, 1, 1, 1},
				{1, 1, 2, 1, 4, 1, 2, 1, 4, 1, 2, 11, 4, 1, 2, 1, 4, 1, 2, 1, 4, 1, 22, 1, 4, 1, 2, 1, 4, 1, 2, 1, 4, 11, 2, 1, 4, 1, 2, 1, 4, 1, 2, 1, 44, 1, 2, 1, 4, 1, 2},
				{1, 1, 1, 3, 1, 5, 3, 1, 1, 9, 5, 1, 3, 1, 1, 15, 1, 1, 9, 1, 5, 3, 1, 1, 3, 5, 1, 9, 1, 1, 15, 1, 1, 3, 1, 5, 9, 1, 1, 3, 5, 1, 3, 1, 1, 45, 1, 1, 3, 1, 5},
				{1, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 23, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 2, 1, 46, 1, 2, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 47, 1, 1, 1},
				{1, 1, 2, 3, 4, 1, 6, 1, 8, 3, 2, 1, 12, 1, 2, 3, 16, 1, 6, 1, 4, 3, 2, 1, 24, 1, 2, 3, 4, 1, 6, 1, 16, 3, 2, 1, 12, 1, 2, 3, 8, 1, 6, 1, 4, 3, 2, 1, 48, 1, 2},
				{1, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 7, 1, 1, 1, 1, 1, 1, 49, 1},
				{1, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 25, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 10, 1, 2, 1, 2, 5, 2, 1, 2, 1, 50}
		};
		System.out.println("\nTest de pgcdIte : ");
		boolean ok=true;
		int i=1, j=1;
		while (ok && i<50) {
			j=1; 
			while (ok && j<=50) {
				ok = PGCD.pgcdIte(i,j)==res[i][j];
				if (!ok) {
					System.out.println("   Aie... pgcdIte("+i+", "+j+") retourne "+PGCD.pgcdIte(i,j)+" au lieu de "+res[i][j]);
					note=0;
				}
				j++;
			}
			i++;
		}


		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"PGCD.java"),"pgcdIte").substring(2);
			if (met.contains("pgcdRec")|| met.contains("pgcdIte")) {
				System.out.println("   Aie... votre implementation de pgcdIte NE doit PAS etre RECURSIVE");
				note=0;
			}
		} 

		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}

	 */

	public static int testLogRec() {
		int note=100;
		int[]res= {0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2};
		System.out.println("\nTest de logRec :");
		/*
		for (int b=2; b<11; b++) {
			for (int n=0; n<101; n++) {
				System.out.print(Log.logRec(b,  n)+", ");
			}
		}
		 */
		boolean ok=true;
		int ires=0;

		for (int b=2; ok && b<11; b++) {
			for (int n=0; ok &&  n<101; n++) {
				ok = Log.logRec(b,  n)== res[ires];

				if (!ok) {
					System.out.println("   Aie... logRec("+b+", "+n+") retourne "+Log.logRec(b,  n)+" au lieu de "+res[ires]);
					note=0;
				}
				ires++;
			}

		}
		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Log.java"),"logRec").substring(2);
			//System.out.println(met);
			if (!met.contains("logRec")) {
				System.out.println("   Aie... votre implementation de logRec doit etre RECURSIVE");
				note=0;
			}
		} 
		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}

	public static int testLogIte() {
		int note=100;
		int[]res= {0, 0, 1, 1, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 5, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 6, 0, 0, 0, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 4, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 3, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2};
		System.out.println("\nTest de logIte :");
		/*
		for (int b=2; b<11; b++) {
			for (int n=0; n<101; n++) {
				System.out.print(Log.logRec(b,  n)+", ");
			}
		}
		 */
		boolean ok=true;
		int ires=0;

		for (int b=2; ok && b<11; b++) {
			for (int n=0; ok &&  n<101; n++) {
				ok = Log.logIte(b,  n)== res[ires];

				if (!ok) {
					System.out.println("   Aie... logIte("+b+", "+n+") retourne "+Log.logIte(b,  n)+" au lieu de "+res[ires]);
					note=0;
				}
				ires++;
			}

		}
		if (note>0) {
			String met = methode(sansCommentaires("src"+File.separator+"Log.java"),"logIte").substring(2);
			//System.out.println(met);
			if (met.contains("logIte")) {
				System.out.println("   Aie... votre implementation de logIte doit etre RECURSIVE");
				note=0;
			} else if (met.contains("logRec")) {
				System.out.println("   Aie... votre implementation de logIte ne doit pas appeler logRec");
				note=0;
			}
		} 
		if (note==100) {		
			System.out.println("  Ok. Votre code passe le test");
		}
		return note;
	}


	public static void main(String[] args) {
		int ntestLogRec = 0;
		int ntestLogIte = 0;
		int ntestFinRec =0;
		int ntestDebutIte=0;
		int ntestFinIte=0;


		try {
			ntestLogRec = testLogRec();//testDeclarationNatEtConst();//FEDeclaration   testFEDeclarationEtConstructeur();
			System.out.println("--> "+ntestLogRec+"/100");
		} catch(Throwable e) {
			afficheThrowable(e, "testLogRec");
		}
		finally {

			try {
				ntestLogIte = testLogIte();//testN0ConstructeursSans();// FEConstructeurSans  testFEConstructeursSans();
				System.out.println("--> "+ntestLogIte+"/100");
			} catch(Throwable e) {
				afficheThrowable(e, "testLogIte()");
			}
			finally {
				/*		try {
					ntestFinRec = testFinRec();
					System.out.println("--> "+ntestFinRec+"/100");

				} catch(Throwable e) {
					afficheThrowable(e, "testFinRec()");
				}
				finally {
					try {
						ntestDebutIte = testDebutIte();
						System.out.println("--> "+ntestDebutIte+"/100");
					} catch(Throwable e) {
						afficheThrowable(e, "testDebutIte");
					}
					finally {

						try {
							ntestFinIte = testFinIte();
							System.out.println("--> "+ntestFinIte+"/100");
						} catch(Throwable e) {
							afficheThrowable(e, "testFinIte");
						}
						finally {
							suite( ntestTriangulaire, 	ntestDebutRec,  ntestFinRec,  ntestDebutIte,  ntestFinIte );
						}
					}
				}*/
			}
		}
	}
	/*
	public static void suite(int ntestTriangulaire, int	ntestDebutRec, int ntestFinRec, int ntestDebutIte, int ntestFinIte ) {
		int ntestSinusRec=0;
		int ntestCosinusRec=0;
		int ntestLastIndexOf=0;
		int ntestFirstIndexOf=0;
		int ntestNbVoyelles=0;

		try {
			ntestSinusRec = testSinusRec();
			System.out.println("--> "+ntestSinusRec+"/100");
		} catch(Throwable e) {
			afficheThrowable(e, "testSinusRec");
		}
		finally {
			try {
				ntestCosinusRec = testCosinusRec();
				System.out.println("--> "+ntestCosinusRec+"/100");
			} catch(Throwable e) {
				afficheThrowable(e, "testCosinusRec");
			}
			finally {
				try {
					ntestLastIndexOf = testLastIndexOf();
					System.out.println("--> "+ntestLastIndexOf+"/100");
				} catch(Throwable e) {
					afficheThrowable(e, "testLastIndexOf");
				}
				finally {
					try {
						ntestFirstIndexOf = testFirstIndexOf();
						System.out.println("--> "+ntestFirstIndexOf+"/100");
					} catch(Throwable e) {
						afficheThrowable(e, "testFirstIndexOf");
					}
					finally {
						try {
							ntestNbVoyelles = testNbVoyelles();
							System.out.println("--> "+ntestNbVoyelles+"/100");
						} catch(Throwable e) {
							afficheThrowable(e, "testNbVoyelles");
						}
						finally {
							suite2( ntestTriangulaire, 	ntestDebutRec,  ntestFinRec,  ntestDebutIte,  ntestFinIte,	 ntestSinusRec,
									 ntestCosinusRec,  ntestLastIndexOf,  ntestFirstIndexOf,  ntestNbVoyelles);
					}
				}
			}
		}
		}


	}

	public static void suite2(int ntestTriangulaire, int	ntestDebutRec, int ntestFinRec, int ntestDebutIte, int ntestFinIte,	int ntestSinusRec,
			int ntestCosinusRec, int ntestLastIndexOf, int ntestFirstIndexOf, int ntestNbVoyelles) {
		int ntestPGCDite=0;
		int ntestPGCDrec=0;
		try {
			ntestPGCDrec = testPGCDrec();
			System.out.println("--> "+ntestPGCDrec+"/100");
		} catch(Throwable e) {
			afficheThrowable(e, "testPGCDrec");
		}
		finally {
			try {
				ntestPGCDite = testPGCDite();
				System.out.println("--> "+ntestPGCDite+"/100");
			} catch(Throwable e) {
				afficheThrowable(e, "testPGCDite");
			}
			finally {
			String res = ntestTriangulaire+"\t"+
					ntestDebutRec+"\t"+
					ntestFinRec+"\t"+
					ntestDebutIte+"\t"+
					ntestFinIte +"\t"+
					ntestSinusRec+"\t"+
					ntestCosinusRec+"\t"+
					ntestLastIndexOf+"\t"+
					ntestFirstIndexOf+"\t"+
					ntestNbVoyelles+"\t"+
					ntestPGCDrec+"\t"+
					ntestPGCDite;

			TestsLog.setClipboardContentsStatic(res);
			System.out.println(res);
			System.out.println("Cette ligne vient d'etre copiee dans le clipboard --> collez la en colonne N");
		}
	}



	}
	 */
	public static String toString(int[] t) {
		String res = "{";
		for (int i=0; i<t.length-1; i++) {
			res += t[i]+", ";
		}
		if (t.length>0) {
			res+=t[t.length-1];
		}
		return res+"}";
	}

	public static boolean equals(int[] t1, int[]t2, int pos) {
		return  (pos==0) ? true : t1[pos-1]==t2[pos-1] && equals(t1, t2, pos-1);
	}
	public static boolean equals(int[]t1, int[]t2) {
		return  t1!=null && t2!=null && t1.length==t2.length && equals(t1, t2, t1.length);
	}
	public void setClipboardContents(String s) {
		StringSelection ss = new StringSelection(s);
		Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
		cb.setContents(ss, this);
	}
	public void lostOwnership(Clipboard arg0, Transferable arg1) {
		System.out.println("<<< copie dans le presse-papier >>>");
	}

	public static void setClipboardContentsStatic(String s) {
		TestsLog t = new TestsLog();
		t.setClipboardContents(s);
	}


}
