import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.ClipboardOwner;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.io.BufferedReader;
import java.io.FileReader;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TestsGrille  implements ClipboardOwner {
	public static int DELAI = 250; // Temps d'attente entre les affichages lors d'une levee d'exception

	public static final int B = 0;
	public static final int N = -1;

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


	public static int testDebutH() {
		int note=100;
		// Exemple 1 de grille.
		int[][] ex1Grille={ 
				{B,B,B,B,B,B},
				{B,B,B,B,B,B},
				{B,B,N,B,B,B},
				{B,B,B,B,B,B},
				{B,B,B,B,N,B},
				{B,N,B,B,B,B},
				{B,B,B,B,B,B}};
		boolean[][] resH1 = {{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{true, false, false, true, false, false},
				{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{false, false, true, false, false, false},
				{true, false, false, false, false, false}};
		boolean[][] resV1 = {		
				{true, true, true, true, true, true},
				{false, false, false, false, false, false},
				{false, false, false, false, false, false},
				{false, false, true, false, false, false},
				{false, false, false, false, false, false},
				{false, false, false, false, true, false},
				{false, false, false, false, false, false}};
		boolean[][] resH2 ={{true, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, false, true, false, false, true, false, false, false, false},
				{true, false, false, true, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, true, false, false, false, false, false, false, true, false},
				{false, false, false, false, true, false, false, false, false, false},
				{true, false, false, false, false, false, false, true, false, false},
				{true, false, false, false, false, false, false, false, false, false}};
		boolean[][] resV2 = {{true, true, true, true, true, false, true, true, false, true},
				{false, false, false, false, false, false, false, false, false, false},
				{false, false, false, false, false, true, false, false, false, false},
				{false, true, false, false, false, false, false, false, false, false},
				{false, false, true, false, false, false, false, false, false, false},
				{false, false, false, false, true, false, false, false, true, false},
				{false, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, true, false, false},
				{false, true, false, true, false, false, false, false, false, false},
				{false, false, false, false, false, false, false, false, false, false}};

		String[] ex1DefsH={ "Pieces de theatres entierement chantees", 
				"Conserve", "Sentier de grand randonnee", "Travail", "Intonation", "Apres le soir",
				"L'epoque des sapins", "A elle ou a lui"};
		String[] ex1DefsV={"Partes vitales", "Touche", "Agent de liaison", 
				"Descendant", "Animal qui brait", "Perspicace", "Salle de spectacle", "De cela", };
		String[] ex1MotsH = {"OPERA", "RETENU", "GR", "JOB", "ACCENT", "NUIT", "NOEL", "SIENNE"};
		String[] ex1MotsV = {"ORGANES", "PERCU", "ET", "REJETON", "ANON","SUBTILE","CINE",  "EN" };

		// Exemple 2 de grille.
		int[][] ex2Grille={ {B,B,B,B,B,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,N,B},
				{B,N,B,B,N,B,B,B,N,B},
				{B,B,N,B,B,B,B,B,N,B},
				{B,B,B,B,N,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,B,B},
				{N,B,B,B,B,N,B,N,B,B},
				{B,N,B,N,B,B,B,B,B,B},
				{B,B,B,B,N,B,N,B,B,B},
				{B,B,B,B,B,B,B,B,B,B}};
		String[] ex2DefsH={ "Lynx d'afrique", "Rieuse, tachetee ou rayee", "Possede", 
				"Hors de portee", "Antilope", "Adverbe de lieu", "Girafide", "Asile", "Rongeur", 
				"Rois faineants de la savane", "Retrograde", "vient apres l'effort", 
				"Pronom personnel", "Nourriture hebraique", "Reservoir", "Deesse", 
		"Trompeuses africaines"};
		String[] ex2DefsV={"Loup africain", "Champagne", "Debut de reunion", "Prenom masculin",
				"Demonstratif", "Pantheres", "Telle une mort precoce", "Noir ou blanc d'Afrique", 
				"Autobus", "Oiseau sacre en Afrique", "Monnaie de l'ex URSS", "Ne se mouille pas", "Conduit",
				"Mi-mouche africaine", "Recueil amusant", "Succes", "Pronom", "Opus abrege"};
		String[] ex2MotsH = {"CARACALS", "HYENE", "EU", "UT", "COB", "CI", "OKAPI", 
				"ABRI", "RAT", "LIONS", "REAC", "SUEE", "ME", "CASHER", "SILO", "INO", "ELEPHANTES"};
		String[] ex2MotsV = {"CHACAL", "AY", "REU", "ANTOINE", "CE", "LEOPARDS",
				"SUBITE", "RHINOCEROS", "CAR", "IBIS", "ROUBLE", "SEC", "AMENE", "TSE", "ANA", "HIT",
				"IL", "OP"};

		//Grille g=new Grille(ex1Grille, ex1DefsH, ex1DefsV, ex1MotsH, ex1MotsV);
		//Grille.numeroter(ex1Grille);
		//System.out.println(Grille.toString(ex1Grille));
		//System.out.println(Grille.toStringDefinitions(ex1Grille, ex1DefsH, ex1DefsV));
		//System.out.println(Grille.toStringSolution(ex1Grille,ex1MotsH, ex1MotsV));

		//Grille g2=new Grille(ex2Grille, ex2DefsH, ex2DefsV, ex2MotsH, ex2MotsV);
		//Grille.numeroter(ex2Grille);
		//System.out.println(Grille.toString(ex2Grille));
		//System.out.println(Grille.toStringDefinitions(ex2Grille, ex2DefsH,ex2DefsV));
		//System.out.println(Grille.toStringSolution(ex2Grille, ex2MotsH,ex2MotsV));

		System.out.println("Test de estUnDebutDeMotH");
		boolean ok=true; 
		int col,lig;
		lig=0;
		while (ok && lig<ex1Grille.length) {
			col=0;
			while (ok && col<ex1Grille[lig].length) {
				ok=Grille.estUnDebutDeMotH(ex1Grille,lig, col)==resH1[lig][col];
				if (!ok) {
					note=0;
					System.out.println("   Aie... Sur la grille :\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |><|  |  |  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |  |  |><|  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |><|  |  |  |  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+\n");
					System.out.println("  estUnDebutDeMotH("+lig+", "+col+") retourne "+Grille.estUnDebutDeMotH(ex1Grille,lig, col)+" au lieu de  "+resH1[lig][col]);
				}
				col++;
			}
			lig++;
		}
		lig=0;
		while (ok && lig<ex2Grille.length) {
			col=0;
			while (ok && col<ex2Grille[lig].length) {
				ok=Grille.estUnDebutDeMotH(ex2Grille,lig, col)==resH2[lig][col];
				if (!ok) {
					System.out.println("   Aie... Sur la grille : \n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |  |  |><|  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |><|  |  |><|  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |><|  |  |><|  |  |  |><|  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |><|  |  |  |  |  |><|  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |><|  |  |  |><|  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |><|  |  |  |  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|><|  |  |  |  |><|  |><|  |  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |><|  |><|  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |><|  |><|  |  |  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"

							);
					note=0;
					System.out.println("  estUnDebutDeMotH("+lig+", "+col+") retourne "+Grille.estUnDebutDeMotH(ex2Grille,lig, col)+" au lieu de  "+resH2[lig][col]);
				}
				col++;
			}
			lig++;
		}
		if (note==100) {
			System.out.println("  OK. Votre code passe le test");
		}
		return note;
	}

	public static int testDebutV() {
		int note=100;
		// Exemple 1 de grille.
		int[][] ex1Grille={ 
				{B,B,B,B,B,B},
				{B,B,B,B,B,B},
				{B,B,N,B,B,B},
				{B,B,B,B,B,B},
				{B,B,B,B,N,B},
				{B,N,B,B,B,B},
				{B,B,B,B,B,B}};
		boolean[][] resH1 = {{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{true, false, false, true, false, false},
				{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{false, false, true, false, false, false},
				{true, false, false, false, false, false}};
		boolean[][] resV1 = {		
				{true, true, true, true, true, true},
				{false, false, false, false, false, false},
				{false, false, false, false, false, false},
				{false, false, true, false, false, false},
				{false, false, false, false, false, false},
				{false, false, false, false, true, false},
				{false, false, false, false, false, false}};
		boolean[][] resH2 ={{true, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, false, true, false, false, true, false, false, false, false},
				{true, false, false, true, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, true, false, false, false, false, false, false, true, false},
				{false, false, false, false, true, false, false, false, false, false},
				{true, false, false, false, false, false, false, true, false, false},
				{true, false, false, false, false, false, false, false, false, false}};
		boolean[][] resV2 = {{true, true, true, true, true, false, true, true, false, true},
				{false, false, false, false, false, false, false, false, false, false},
				{false, false, false, false, false, true, false, false, false, false},
				{false, true, false, false, false, false, false, false, false, false},
				{false, false, true, false, false, false, false, false, false, false},
				{false, false, false, false, true, false, false, false, true, false},
				{false, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, true, false, false},
				{false, true, false, true, false, false, false, false, false, false},
				{false, false, false, false, false, false, false, false, false, false}};

		String[] ex1DefsH={ "Pieces de theatres entierement chantees", 
				"Conserve", "Sentier de grand randonnee", "Travail", "Intonation", "Apres le soir",
				"L'epoque des sapins", "A elle ou a lui"};
		String[] ex1DefsV={"Partes vitales", "Touche", "Agent de liaison", 
				"Descendant", "Animal qui brait", "Perspicace", "Salle de spectacle", "De cela", };
		String[] ex1MotsH = {"OPERA", "RETENU", "GR", "JOB", "ACCENT", "NUIT", "NOEL", "SIENNE"};
		String[] ex1MotsV = {"ORGANES", "PERCU", "ET", "REJETON", "ANON","SUBTILE","CINE",  "EN" };

		// Exemple 2 de grille.
		int[][] ex2Grille={ {B,B,B,B,B,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,N,B},
				{B,N,B,B,N,B,B,B,N,B},
				{B,B,N,B,B,B,B,B,N,B},
				{B,B,B,B,N,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,B,B},
				{N,B,B,B,B,N,B,N,B,B},
				{B,N,B,N,B,B,B,B,B,B},
				{B,B,B,B,N,B,N,B,B,B},
				{B,B,B,B,B,B,B,B,B,B}};
		String[] ex2DefsH={ "Lynx d'afrique", "Rieuse, tachetee ou rayee", "Possede", 
				"Hors de portee", "Antilope", "Adverbe de lieu", "Girafide", "Asile", "Rongeur", 
				"Rois faineants de la savane", "Retrograde", "vient apres l'effort", 
				"Pronom personnel", "Nourriture hebraique", "Reservoir", "Deesse", 
		"Trompeuses africaines"};
		String[] ex2DefsV={"Loup africain", "Champagne", "Debut de reunion", "Prenom masculin",
				"Demonstratif", "Pantheres", "Telle une mort precoce", "Noir ou blanc d'Afrique", 
				"Autobus", "Oiseau sacre en Afrique", "Monnaie de l'ex URSS", "Ne se mouille pas", "Conduit",
				"Mi-mouche africaine", "Recueil amusant", "Succes", "Pronom", "Opus abrege"};
		String[] ex2MotsH = {"CARACALS", "HYENE", "EU", "UT", "COB", "CI", "OKAPI", 
				"ABRI", "RAT", "LIONS", "REAC", "SUEE", "ME", "CASHER", "SILO", "INO", "ELEPHANTES"};
		String[] ex2MotsV = {"CHACAL", "AY", "REU", "ANTOINE", "CE", "LEOPARDS",
				"SUBITE", "RHINOCEROS", "CAR", "IBIS", "ROUBLE", "SEC", "AMENE", "TSE", "ANA", "HIT",
				"IL", "OP"};

		//Grille g=new Grille(ex1Grille, ex1DefsH, ex1DefsV, ex1MotsH, ex1MotsV);
		//Grille.numeroter(ex1Grille);
		//System.out.println(Grille.toString(ex1Grille));
		//System.out.println(Grille.toStringDefinitions(ex1Grille, ex1DefsH, ex1DefsV));
		//System.out.println(Grille.toStringSolution(ex1Grille,ex1MotsH, ex1MotsV));

		//Grille g2=new Grille(ex2Grille, ex2DefsH, ex2DefsV, ex2MotsH, ex2MotsV);
		//Grille.numeroter(ex2Grille);
		//System.out.println(Grille.toString(ex2Grille));
		//System.out.println(Grille.toStringDefinitions(ex2Grille, ex2DefsH,ex2DefsV));
		//System.out.println(Grille.toStringSolution(ex2Grille, ex2MotsH,ex2MotsV));
		System.out.println("\nTest de estUnDebutDeMotV");
		boolean ok=true;
		int col, lig=0;
		while (ok && lig<ex1Grille.length) {
			col=0;
			while (ok && col<ex1Grille[lig].length) {
				ok=Grille.estUnDebutDeMotV(ex1Grille,lig, col)==resV1[lig][col];
				if (!ok) {
					System.out.println("   Aie... Sur la grille :\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |><|  |  |  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |  |  |><|  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |><|  |  |  |  |\n"+
							"+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+\n");
					note=0;
					System.out.println("  estUnDebutDeMotV("+lig+", "+col+") retourne "+Grille.estUnDebutDeMotV(ex1Grille,lig, col)+" au lieu de  "+resV1[lig][col]);
				}
				col++;
			}
			lig++;
		}
		lig=0;
		while (ok && lig<ex2Grille.length) {
			col=0;
			while (ok && col<ex2Grille[lig].length) {
				ok=Grille.estUnDebutDeMotV(ex2Grille,lig, col)==resV2[lig][col];
				if (!ok) {
					System.out.println("   Aie... Sur la grille : \n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |  |  |><|  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |><|  |  |><|  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |><|  |  |><|  |  |  |><|  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |><|  |  |  |  |  |><|  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |><|  |  |  |><|  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |><|  |  |  |  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|><|  |  |  |  |><|  |><|  |  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |><|  |><|  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |><|  |><|  |  |  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"+
							"|  |  |  |  |  |  |  |  |  |  |\n"+
							"+--+--+--+--+--+--+--+--+--+--+\n"
							);
					System.out.println("  estUnDebutDeMotV("+lig+", "+col+") retourne "+Grille.estUnDebutDeMotV(ex2Grille,lig, col)+" au lieu de  "+resV2[lig][col]);
					note=0;
				}
				col++;
			}
			lig++;
		}
		if (note==100) {		
			System.out.println("   Ok. Votre code passe le test");
		}
		return note;
	}

	public static int testNumeroter() {
		int note=100;
		// Exemple 1 de grille.
		int[][] ex1Grille={ 
				{B,B,B,B,B,B},
				{B,B,B,B,B,B},
				{B,B,N,B,B,B},
				{B,B,B,B,B,B},
				{B,B,B,B,N,B},
				{B,N,B,B,B,B},
				{B,B,B,B,B,B}};
		boolean[][] resH1 = {{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{true, false, false, true, false, false},
				{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{false, false, true, false, false, false},
				{true, false, false, false, false, false}};
		boolean[][] resV1 = {		
				{true, true, true, true, true, true},
				{false, false, false, false, false, false},
				{false, false, false, false, false, false},
				{false, false, true, false, false, false},
				{false, false, false, false, false, false},
				{false, false, false, false, true, false},
				{false, false, false, false, false, false}};
		boolean[][] resH2 ={{true, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, false, true, false, false, true, false, false, false, false},
				{true, false, false, true, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, true, false, false, false, false, false, false, true, false},
				{false, false, false, false, true, false, false, false, false, false},
				{true, false, false, false, false, false, false, true, false, false},
				{true, false, false, false, false, false, false, false, false, false}};
		boolean[][] resV2 = {{true, true, true, true, true, false, true, true, false, true},
				{false, false, false, false, false, false, false, false, false, false},
				{false, false, false, false, false, true, false, false, false, false},
				{false, true, false, false, false, false, false, false, false, false},
				{false, false, true, false, false, false, false, false, false, false},
				{false, false, false, false, true, false, false, false, true, false},
				{false, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, true, false, false},
				{false, true, false, true, false, false, false, false, false, false},
				{false, false, false, false, false, false, false, false, false, false}};

		String[] ex1DefsH={ "Pieces de theatres entierement chantees", 
				"Conserve", "Sentier de grand randonnee", "Travail", "Intonation", "Apres le soir",
				"L'epoque des sapins", "A elle ou a lui"};
		String[] ex1DefsV={"Partes vitales", "Touche", "Agent de liaison", 
				"Descendant", "Animal qui brait", "Perspicace", "Salle de spectacle", "De cela", };
		String[] ex1MotsH = {"OPERA", "RETENU", "GR", "JOB", "ACCENT", "NUIT", "NOEL", "SIENNE"};
		String[] ex1MotsV = {"ORGANES", "PERCU", "ET", "REJETON", "ANON","SUBTILE","CINE",  "EN" };

		// Exemple 2 de grille.
		int[][] ex2Grille={ {B,B,B,B,B,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,N,B},
				{B,N,B,B,N,B,B,B,N,B},
				{B,B,N,B,B,B,B,B,N,B},
				{B,B,B,B,N,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,B,B},
				{N,B,B,B,B,N,B,N,B,B},
				{B,N,B,N,B,B,B,B,B,B},
				{B,B,B,B,N,B,N,B,B,B},
				{B,B,B,B,B,B,B,B,B,B}};
		String[] ex2DefsH={ "Lynx d'afrique", "Rieuse, tachetee ou rayee", "Possede", 
				"Hors de portee", "Antilope", "Adverbe de lieu", "Girafide", "Asile", "Rongeur", 
				"Rois faineants de la savane", "Retrograde", "vient apres l'effort", 
				"Pronom personnel", "Nourriture hebraique", "Reservoir", "Deesse", 
		"Trompeuses africaines"};
		String[] ex2DefsV={"Loup africain", "Champagne", "Debut de reunion", "Prenom masculin",
				"Demonstratif", "Pantheres", "Telle une mort precoce", "Noir ou blanc d'Afrique", 
				"Autobus", "Oiseau sacre en Afrique", "Monnaie de l'ex URSS", "Ne se mouille pas", "Conduit",
				"Mi-mouche africaine", "Recueil amusant", "Succes", "Pronom", "Opus abrege"};
		String[] ex2MotsH = {"CARACALS", "HYENE", "EU", "UT", "COB", "CI", "OKAPI", 
				"ABRI", "RAT", "LIONS", "REAC", "SUEE", "ME", "CASHER", "SILO", "INO", "ELEPHANTES"};
		String[] ex2MotsV = {"CHACAL", "AY", "REU", "ANTOINE", "CE", "LEOPARDS",
				"SUBITE", "RHINOCEROS", "CAR", "IBIS", "ROUBLE", "SEC", "AMENE", "TSE", "ANA", "HIT",
				"IL", "OP"};

		//Grille g=new Grille(ex1Grille, ex1DefsH, ex1DefsV, ex1MotsH, ex1MotsV);
		//Grille.numeroter(ex1Grille);
		//System.out.println(Grille.toString(ex1Grille));
		//System.out.println(Grille.toStringDefinitions(ex1Grille, ex1DefsH, ex1DefsV));
		//System.out.println(Grille.toStringSolution(ex1Grille,ex1MotsH, ex1MotsV));

		//Grille g2=new Grille(ex2Grille, ex2DefsH, ex2DefsV, ex2MotsH, ex2MotsV);
		//Grille.numeroter(ex2Grille);
		//System.out.println(Grille.toString(ex2Grille));
		//System.out.println(Grille.toStringDefinitions(ex2Grille, ex2DefsH,ex2DefsV));
		//System.out.println(Grille.toStringSolution(ex2Grille, ex2MotsH,ex2MotsV));


		int[][] resNum1 = {{1, 2, 3, 4, 5, 6},
				{7, 0, 0, 0, 0, 0},
				{8, 0, -1, 9, 0, 0},
				{10, 0, 11, 0, 0, 0},
				{12, 0, 0, 0, -1, 0},
				{0, -1, 13, 0, 14, 0},
				{15, 0, 0, 0, 0, 0}};
		int[][] resNum2 = {
				{1, 2, 3, 4, 5, 0, 6, 7, -1, 8},
				{9, 0, 0, 0, 0, -1, 10, 0, -1, 0},
				{0, -1, 11, 0, -1, 12, 0, 0, -1, 0},
				{13, 14, -1, 15, 0, 0, 0, 0, -1, 0},
				{16, 0, 17, 0, -1, 18, 0, 0, -1, 0},
				{19, 0, 0, 0, 20, -1, 21, 0, 22, 0},
				{-1, 23, 0, 0, 0, -1, 0, -1, 24, 0},
				{25, -1, 0, -1, 26, 27, 0, 28, 0, 0},
				{29, 30, 0, 31, -1, 0, -1, 32, 0, 0},
				{33, 0, 0, 0, 0, 0, 0, 0, 0, 0}};


		Grille.numeroter(ex1Grille);
		System.out.println("\nTest de numeroter");
		boolean ok=true;
		int col, lig=0;
		while (ok && lig<ex1Grille.length) {
			col=0;
			while (ok && col<ex1Grille[lig].length) {
				ok=ex1Grille[lig][col]==resNum1[lig][col];
				if (!ok) {
					System.out.println("  Aie... numeroter aurait du aboutir a : "+toStringPrivate(resNum1)+"\n mais aboutit a: "+toStringPrivate(ex1Grille));
					note=0;
				}
				col++;
			}
			lig++;
		}
		Grille.numeroter(ex2Grille);
		lig=0;
		while (ok && lig<ex2Grille.length) {
			col=0;
			while (ok && col<ex2Grille[lig].length) {
				ok=ex2Grille[lig][col]==resNum2[lig][col];
				if (!ok) {
					System.out.println("  Aie... numeroter aurait du aboutir a : "+toStringPrivate(resNum2)+"\n mais aboutit a: "+toStringPrivate(ex2Grille));
					note=0;
				}
				col++;
			}
			lig++;
		}
		if (note==100) {		
			System.out.println("   Ok. Votre code passe le test");
		}
		return note;
	}


	public static int testToString() {
		int note=100;
		// Exemple 1 de grille.
		int[][] ex1Grille={ 
				{B,B,B,B,B,B},
				{B,B,B,B,B,B},
				{B,B,N,B,B,B},
				{B,B,B,B,B,B},
				{B,B,B,B,N,B},
				{B,N,B,B,B,B},
				{B,B,B,B,B,B}};
		boolean[][] resH1 = {{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{true, false, false, true, false, false},
				{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{false, false, true, false, false, false},
				{true, false, false, false, false, false}};
		boolean[][] resV1 = {		
				{true, true, true, true, true, true},
				{false, false, false, false, false, false},
				{false, false, false, false, false, false},
				{false, false, true, false, false, false},
				{false, false, false, false, false, false},
				{false, false, false, false, true, false},
				{false, false, false, false, false, false}};
		boolean[][] resH2 ={{true, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, false, true, false, false, true, false, false, false, false},
				{true, false, false, true, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, true, false, false, false, false, false, false, true, false},
				{false, false, false, false, true, false, false, false, false, false},
				{true, false, false, false, false, false, false, true, false, false},
				{true, false, false, false, false, false, false, false, false, false}};
		boolean[][] resV2 = {{true, true, true, true, true, false, true, true, false, true},
				{false, false, false, false, false, false, false, false, false, false},
				{false, false, false, false, false, true, false, false, false, false},
				{false, true, false, false, false, false, false, false, false, false},
				{false, false, true, false, false, false, false, false, false, false},
				{false, false, false, false, true, false, false, false, true, false},
				{false, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, true, false, false},
				{false, true, false, true, false, false, false, false, false, false},
				{false, false, false, false, false, false, false, false, false, false}};

		String[] ex1DefsH={ "Pieces de theatres entierement chantees", 
				"Conserve", "Sentier de grand randonnee", "Travail", "Intonation", "Apres le soir",
				"L'epoque des sapins", "A elle ou a lui"};
		String[] ex1DefsV={"Partes vitales", "Touche", "Agent de liaison", 
				"Descendant", "Animal qui brait", "Perspicace", "Salle de spectacle", "De cela", };
		String[] ex1MotsH = {"OPERA", "RETENU", "GR", "JOB", "ACCENT", "NUIT", "NOEL", "SIENNE"};
		String[] ex1MotsV = {"ORGANES", "PERCU", "ET", "REJETON", "ANON","SUBTILE","CINE",  "EN" };

		// Exemple 2 de grille.
		int[][] ex2Grille={ {B,B,B,B,B,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,N,B},
				{B,N,B,B,N,B,B,B,N,B},
				{B,B,N,B,B,B,B,B,N,B},
				{B,B,B,B,N,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,B,B},
				{N,B,B,B,B,N,B,N,B,B},
				{B,N,B,N,B,B,B,B,B,B},
				{B,B,B,B,N,B,N,B,B,B},
				{B,B,B,B,B,B,B,B,B,B}};
		String[] ex2DefsH={ "Lynx d'afrique", "Rieuse, tachetee ou rayee", "Possede", 
				"Hors de portee", "Antilope", "Adverbe de lieu", "Girafide", "Asile", "Rongeur", 
				"Rois faineants de la savane", "Retrograde", "vient apres l'effort", 
				"Pronom personnel", "Nourriture hebraique", "Reservoir", "Deesse", 
		"Trompeuses africaines"};
		String[] ex2DefsV={"Loup africain", "Champagne", "Debut de reunion", "Prenom masculin",
				"Demonstratif", "Pantheres", "Telle une mort precoce", "Noir ou blanc d'Afrique", 
				"Autobus", "Oiseau sacre en Afrique", "Monnaie de l'ex URSS", "Ne se mouille pas", "Conduit",
				"Mi-mouche africaine", "Recueil amusant", "Succes", "Pronom", "Opus abrege"};
		String[] ex2MotsH = {"CARACALS", "HYENE", "EU", "UT", "COB", "CI", "OKAPI", 
				"ABRI", "RAT", "LIONS", "REAC", "SUEE", "ME", "CASHER", "SILO", "INO", "ELEPHANTES"};
		String[] ex2MotsV = {"CHACAL", "AY", "REU", "ANTOINE", "CE", "LEOPARDS",
				"SUBITE", "RHINOCEROS", "CAR", "IBIS", "ROUBLE", "SEC", "AMENE", "TSE", "ANA", "HIT",
				"IL", "OP"};

		//Grille g=new Grille(ex1Grille, ex1DefsH, ex1DefsV, ex1MotsH, ex1MotsV);
		//Grille.numeroter(ex1Grille);
		//System.out.println(Grille.toString(ex1Grille));
		//System.out.println(Grille.toStringDefinitions(ex1Grille, ex1DefsH, ex1DefsV));
		//System.out.println(Grille.toStringSolution(ex1Grille,ex1MotsH, ex1MotsV));

		//Grille g2=new Grille(ex2Grille, ex2DefsH, ex2DefsV, ex2MotsH, ex2MotsV);
		//Grille.numeroter(ex2Grille);
		//System.out.println(Grille.toString(ex2Grille));
		//System.out.println(Grille.toStringDefinitions(ex2Grille, ex2DefsH,ex2DefsV));
		//System.out.println(Grille.toStringSolution(ex2Grille, ex2MotsH,ex2MotsV));


		int[][] resNum1 = {{1, 2, 3, 4, 5, 6},
				{7, 0, 0, 0, 0, 0},
				{8, 0, -1, 9, 0, 0},
				{10, 0, 11, 0, 0, 0},
				{12, 0, 0, 0, -1, 0},
				{0, -1, 13, 0, 14, 0},
				{15, 0, 0, 0, 0, 0}};
		int[][] resNum2 = {
				{1, 2, 3, 4, 5, 0, 6, 7, -1, 8},
				{9, 0, 0, 0, 0, -1, 10, 0, -1, 0},
				{0, -1, 11, 0, -1, 12, 0, 0, -1, 0},
				{13, 14, -1, 15, 0, 0, 0, 0, -1, 0},
				{16, 0, 17, 0, -1, 18, 0, 0, -1, 0},
				{19, 0, 0, 0, 20, -1, 21, 0, 22, 0},
				{-1, 23, 0, 0, 0, -1, 0, -1, 24, 0},
				{25, -1, 0, -1, 26, 27, 0, 28, 0, 0},
				{29, 30, 0, 31, -1, 0, -1, 32, 0, 0},
				{33, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

		String resToString1="+--+--+--+--+--+--+\n| 1| 2| 3| 4| 5| 6|\n+--+--+--+--+--+--+\n| 7|  |  |  |  |  |\n+--+--+--+--+--+--+\n| 8|  |><| 9|  |  |\n+--+--+--+--+--+--+\n|10|  |11|  |  |  |\n+--+--+--+--+--+--+\n|12|  |  |  |><|  |\n+--+--+--+--+--+--+\n|  |><|13|  |14|  |\n+--+--+--+--+--+--+\n|15|  |  |  |  |  |\n+--+--+--+--+--+--+\n";
		String resToString2="+--+--+--+--+--+--+--+--+--+--+\n| 1| 2| 3| 4| 5|  | 6| 7|><| 8|\n+--+--+--+--+--+--+--+--+--+--+\n| 9|  |  |  |  |><|10|  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|  |><|11|  |><|12|  |  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|13|14|><|15|  |  |  |  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|16|  |17|  |><|18|  |  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|19|  |  |  |20|><|21|  |22|  |\n+--+--+--+--+--+--+--+--+--+--+\n|><|23|  |  |  |><|  |><|24|  |\n+--+--+--+--+--+--+--+--+--+--+\n|25|><|  |><|26|27|  |28|  |  |\n+--+--+--+--+--+--+--+--+--+--+\n|29|30|  |31|><|  |><|32|  |  |\n+--+--+--+--+--+--+--+--+--+--+\n|33|  |  |  |  |  |  |  |  |  |\n+--+--+--+--+--+--+--+--+--+--+\n";

		String s = Grille.toString(resNum1);//ex1Grille);
		//    System.out.println(s);
		System.out.println("\nTest de toString");
//Grille.numeroter(ex1Grille);
//System.out.println(Arrays.deepToString(ex1Grille));
		if (!resToString1.equals(s)) {
			System.out.println("  Aie... toString devrait retourner : \n"+resToString1+"\n mais retourne :\n"+s);
			note=0;
		} else {
			s = Grille.toString(resNum2);//ex2Grille);
			//  System.out.println(s);

			if (!resToString2.equals(s)) {
				System.out.println("  Aie... toString devrait retourner : \n"+resToString2+"\n mais retourne :\n"+s);
				note=0;
			} 
		}

		if (note==100) {		
			System.out.println("   Ok. Votre code passe le test");
		}
		return note;
	}

	public static int testDefinitions() {
		int note=100;
		// Exemple 1 de grille.
		int[][] ex1Grille={ 
				{B,B,B,B,B,B},
				{B,B,B,B,B,B},
				{B,B,N,B,B,B},
				{B,B,B,B,B,B},
				{B,B,B,B,N,B},
				{B,N,B,B,B,B},
				{B,B,B,B,B,B}};
		boolean[][] resH1 = {{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{true, false, false, true, false, false},
				{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{false, false, true, false, false, false},
				{true, false, false, false, false, false}};
		boolean[][] resV1 = {		
				{true, true, true, true, true, true},
				{false, false, false, false, false, false},
				{false, false, false, false, false, false},
				{false, false, true, false, false, false},
				{false, false, false, false, false, false},
				{false, false, false, false, true, false},
				{false, false, false, false, false, false}};
		boolean[][] resH2 ={{true, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, false, true, false, false, true, false, false, false, false},
				{true, false, false, true, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, true, false, false, false, false, false, false, true, false},
				{false, false, false, false, true, false, false, false, false, false},
				{true, false, false, false, false, false, false, true, false, false},
				{true, false, false, false, false, false, false, false, false, false}};
		boolean[][] resV2 = {{true, true, true, true, true, false, true, true, false, true},
				{false, false, false, false, false, false, false, false, false, false},
				{false, false, false, false, false, true, false, false, false, false},
				{false, true, false, false, false, false, false, false, false, false},
				{false, false, true, false, false, false, false, false, false, false},
				{false, false, false, false, true, false, false, false, true, false},
				{false, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, true, false, false},
				{false, true, false, true, false, false, false, false, false, false},
				{false, false, false, false, false, false, false, false, false, false}};

		String[] ex1DefsH={ "Pieces de theatres entierement chantees", 
				"Conserve", "Sentier de grand randonnee", "Travail", "Intonation", "Apres le soir",
				"L'epoque des sapins", "A elle ou a lui"};
		String[] ex1DefsV={"Partes vitales", "Touche", "Agent de liaison", 
				"Descendant", "Animal qui brait", "Perspicace", "Salle de spectacle", "De cela", };
		String[] ex1MotsH = {"OPERA", "RETENU", "GR", "JOB", "ACCENT", "NUIT", "NOEL", "SIENNE"};
		String[] ex1MotsV = {"ORGANES", "PERCU", "ET", "REJETON", "ANON","SUBTILE","CINE",  "EN" };

		// Exemple 2 de grille.
		int[][] ex2Grille={ {B,B,B,B,B,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,N,B},
				{B,N,B,B,N,B,B,B,N,B},
				{B,B,N,B,B,B,B,B,N,B},
				{B,B,B,B,N,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,B,B},
				{N,B,B,B,B,N,B,N,B,B},
				{B,N,B,N,B,B,B,B,B,B},
				{B,B,B,B,N,B,N,B,B,B},
				{B,B,B,B,B,B,B,B,B,B}};
		String[] ex2DefsH={ "Lynx d'afrique", "Rieuse, tachetee ou rayee", "Possede", 
				"Hors de portee", "Antilope", "Adverbe de lieu", "Girafide", "Asile", "Rongeur", 
				"Rois faineants de la savane", "Retrograde", "vient apres l'effort", 
				"Pronom personnel", "Nourriture hebraique", "Reservoir", "Deesse", 
		"Trompeuses africaines"};
		String[] ex2DefsV={"Loup africain", "Champagne", "Debut de reunion", "Prenom masculin",
				"Demonstratif", "Pantheres", "Telle une mort precoce", "Noir ou blanc d'Afrique", 
				"Autobus", "Oiseau sacre en Afrique", "Monnaie de l'ex URSS", "Ne se mouille pas", "Conduit",
				"Mi-mouche africaine", "Recueil amusant", "Succes", "Pronom", "Opus abrege"};
		String[] ex2MotsH = {"CARACALS", "HYENE", "EU", "UT", "COB", "CI", "OKAPI", 
				"ABRI", "RAT", "LIONS", "REAC", "SUEE", "ME", "CASHER", "SILO", "INO", "ELEPHANTES"};
		String[] ex2MotsV = {"CHACAL", "AY", "REU", "ANTOINE", "CE", "LEOPARDS",
				"SUBITE", "RHINOCEROS", "CAR", "IBIS", "ROUBLE", "SEC", "AMENE", "TSE", "ANA", "HIT",
				"IL", "OP"};

		//Grille g=new Grille(ex1Grille, ex1DefsH, ex1DefsV, ex1MotsH, ex1MotsV);
		//Grille.numeroter(ex1Grille);
		//System.out.println(Grille.toString(ex1Grille));
		//System.out.println(Grille.toStringDefinitions(ex1Grille, ex1DefsH, ex1DefsV));
		//System.out.println(Grille.toStringSolution(ex1Grille,ex1MotsH, ex1MotsV));

		//Grille g2=new Grille(ex2Grille, ex2DefsH, ex2DefsV, ex2MotsH, ex2MotsV);
		//Grille.numeroter(ex2Grille);
		//System.out.println(Grille.toString(ex2Grille));
		//System.out.println(Grille.toStringDefinitions(ex2Grille, ex2DefsH,ex2DefsV));
		//System.out.println(Grille.toStringSolution(ex2Grille, ex2MotsH,ex2MotsV));


		int[][] resNum1 = {{1, 2, 3, 4, 5, 6},
				{7, 0, 0, 0, 0, 0},
				{8, 0, -1, 9, 0, 0},
				{10, 0, 11, 0, 0, 0},
				{12, 0, 0, 0, -1, 0},
				{0, -1, 13, 0, 14, 0},
				{15, 0, 0, 0, 0, 0}};
		int[][] resNum2 = {
				{1, 2, 3, 4, 5, 0, 6, 7, -1, 8},
				{9, 0, 0, 0, 0, -1, 10, 0, -1, 0},
				{0, -1, 11, 0, -1, 12, 0, 0, -1, 0},
				{13, 14, -1, 15, 0, 0, 0, 0, -1, 0},
				{16, 0, 17, 0, -1, 18, 0, 0, -1, 0},
				{19, 0, 0, 0, 20, -1, 21, 0, 22, 0},
				{-1, 23, 0, 0, 0, -1, 0, -1, 24, 0},
				{25, -1, 0, -1, 26, 27, 0, 28, 0, 0},
				{29, 30, 0, 31, -1, 0, -1, 32, 0, 0},
				{33, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

		String resToString1="+--+--+--+--+--+--+\n| 1| 2| 3| 4| 5| 6|\n+--+--+--+--+--+--+\n| 7|  |  |  |  |  |\n+--+--+--+--+--+--+\n| 8|  |><| 9|  |  |\n+--+--+--+--+--+--+\n|10|  |11|  |  |  |\n+--+--+--+--+--+--+\n|12|  |  |  |><|  |\n+--+--+--+--+--+--+\n|  |><|13|  |14|  |\n+--+--+--+--+--+--+\n|15|  |  |  |  |  |\n+--+--+--+--+--+--+\n";
		String resToString2="+--+--+--+--+--+--+--+--+--+--+\n| 1| 2| 3| 4| 5|  | 6| 7|><| 8|\n+--+--+--+--+--+--+--+--+--+--+\n| 9|  |  |  |  |><|10|  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|  |><|11|  |><|12|  |  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|13|14|><|15|  |  |  |  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|16|  |17|  |><|18|  |  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|19|  |  |  |20|><|21|  |22|  |\n+--+--+--+--+--+--+--+--+--+--+\n|><|23|  |  |  |><|  |><|24|  |\n+--+--+--+--+--+--+--+--+--+--+\n|25|><|  |><|26|27|  |28|  |  |\n+--+--+--+--+--+--+--+--+--+--+\n|29|30|  |31|><|  |><|32|  |  |\n+--+--+--+--+--+--+--+--+--+--+\n|33|  |  |  |  |  |  |  |  |  |\n+--+--+--+--+--+--+--+--+--+--+\n";

		System.out.println("\nTest de toStringDefinitions");
		String resDef1="HORIZONTALEMENT\n1 Pieces de theatres entierement chantees\n7 Conserve\n8 Sentier de grand randonnee\n9 Travail\n10 Intonation\n12 Apres le soir\n13 L'epoque des sapins\n15 A elle ou a lui\n\nVERTICALEMENT\n1 Partes vitales\n2 Touche\n3 Agent de liaison\n4 Descendant\n5 Animal qui brait\n6 Perspicace\n11 Salle de spectacle\n14 De cela\n";
		String resDef2="HORIZONTALEMENT\n1 Lynx d'afrique\n9 Rieuse, tachetee ou rayee\n10 Possede\n11 Hors de portee\n12 Antilope\n13 Adverbe de lieu\n15 Girafide\n16 Asile\n18 Rongeur\n19 Rois faineants de la savane\n21 Retrograde\n23 vient apres l'effort\n24 Pronom personnel\n26 Nourriture hebraique\n29 Reservoir\n32 Deesse\n33 Trompeuses africaines\n\nVERTICALEMENT\n1 Loup africain\n2 Champagne\n3 Debut de reunion\n4 Prenom masculin\n5 Demonstratif\n6 Pantheres\n7 Telle une mort precoce\n8 Noir ou blanc d'Afrique\n12 Autobus\n14 Oiseau sacre en Afrique\n17 Monnaie de l'ex URSS\n20 Ne se mouille pas\n22 Conduit\n25 Mi-mouche africaine\n27 Recueil amusant\n28 Succes\n30 Pronom\n31 Opus abrege\n";
		String s = Grille.toStringDefinitions(resNum1, ex1DefsH,ex1DefsV);
		if (!resDef1.equals(s)) {
			System.out.println("  Aie... toStringDefinitions devrait retourner : \n"+resDef1+"\n mais retourne :\n"+s);
			note=0;
		} else {
			s = Grille.toStringDefinitions(resNum2,ex2DefsH,ex2DefsV);
			//  System.out.println(s);

			if (!resDef2.equals(s)) {
				System.out.println("  Aie... toStringDefinitions devrait retourner : \n"+resDef2+"\n mais retourne :\n"+s);
				note=0;
			} 
		}

		if (note==100) {		
			System.out.println("   Ok. Votre code passe le test");
		}
		return note;
	}

	public static int testSolution() {
		int note=100;
		// Exemple 1 de grille.
		int[][] ex1Grille={ 
				{B,B,B,B,B,B},
				{B,B,B,B,B,B},
				{B,B,N,B,B,B},
				{B,B,B,B,B,B},
				{B,B,B,B,N,B},
				{B,N,B,B,B,B},
				{B,B,B,B,B,B}};
		boolean[][] resH1 = {{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{true, false, false, true, false, false},
				{true, false, false, false, false, false},
				{true, false, false, false, false, false},
				{false, false, true, false, false, false},
				{true, false, false, false, false, false}};
		boolean[][] resV1 = {		
				{true, true, true, true, true, true},
				{false, false, false, false, false, false},
				{false, false, false, false, false, false},
				{false, false, true, false, false, false},
				{false, false, false, false, false, false},
				{false, false, false, false, true, false},
				{false, false, false, false, false, false}};
		boolean[][] resH2 ={{true, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, false, true, false, false, true, false, false, false, false},
				{true, false, false, true, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, false, false, false},
				{true, false, false, false, false, false, true, false, false, false},
				{false, true, false, false, false, false, false, false, true, false},
				{false, false, false, false, true, false, false, false, false, false},
				{true, false, false, false, false, false, false, true, false, false},
				{true, false, false, false, false, false, false, false, false, false}};
		boolean[][] resV2 = {{true, true, true, true, true, false, true, true, false, true},
				{false, false, false, false, false, false, false, false, false, false},
				{false, false, false, false, false, true, false, false, false, false},
				{false, true, false, false, false, false, false, false, false, false},
				{false, false, true, false, false, false, false, false, false, false},
				{false, false, false, false, true, false, false, false, true, false},
				{false, false, false, false, false, false, false, false, false, false},
				{true, false, false, false, false, true, false, true, false, false},
				{false, true, false, true, false, false, false, false, false, false},
				{false, false, false, false, false, false, false, false, false, false}};

		String[] ex1DefsH={ "Pieces de theatres entierement chantees", 
				"Conserve", "Sentier de grand randonnee", "Travail", "Intonation", "Apres le soir",
				"L'epoque des sapins", "A elle ou a lui"};
		String[] ex1DefsV={"Partes vitales", "Touche", "Agent de liaison", 
				"Descendant", "Animal qui brait", "Perspicace", "Salle de spectacle", "De cela", };
		String[] ex1MotsH = {"OPERAS", "RETENU", "GR", "JOB", "ACCENT", "NUIT", "NOEL", "SIENNE"};
		String[] ex1MotsV = {"ORGANES", "PERCU", "ET", "REJETON", "ANON","SUBTILE","CINE",  "EN" };

		// Exemple 2 de grille.
		int[][] ex2Grille={ {B,B,B,B,B,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,N,B},
				{B,N,B,B,N,B,B,B,N,B},
				{B,B,N,B,B,B,B,B,N,B},
				{B,B,B,B,N,B,B,B,N,B},
				{B,B,B,B,B,N,B,B,B,B},
				{N,B,B,B,B,N,B,N,B,B},
				{B,N,B,N,B,B,B,B,B,B},
				{B,B,B,B,N,B,N,B,B,B},
				{B,B,B,B,B,B,B,B,B,B}};
		String[] ex2DefsH={ "Lynx d'afrique", "Rieuse, tachetee ou rayee", "Possede", 
				"Hors de portee", "Antilope", "Adverbe de lieu", "Girafide", "Asile", "Rongeur", 
				"Rois faineants de la savane", "Retrograde", "vient apres l'effort", 
				"Pronom personnel", "Nourriture hebraique", "Reservoir", "Deesse", 
		"Trompeuses africaines"};
		String[] ex2DefsV={"Loup africain", "Champagne", "Debut de reunion", "Prenom masculin",
				"Demonstratif", "Pantheres", "Telle une mort precoce", "Noir ou blanc d'Afrique", 
				"Autobus", "Oiseau sacre en Afrique", "Monnaie de l'ex URSS", "Ne se mouille pas", "Conduit",
				"Mi-mouche africaine", "Recueil amusant", "Succes", "Pronom", "Opus abrege"};
		String[] ex2MotsH = {"CARACALS", "HYENE", "EU", "UT", "COB", "CI", "OKAPI", 
				"ABRI", "RAT", "LIONS", "REAC", "SUEE", "ME", "CASHER", "SILO", "INO", "ELEPHANTES"};
		String[] ex2MotsV = {"CHACAL", "AY", "REU", "ANTOINE", "CE", "LEOPARDS",
				"SUBITE", "RHINOCEROS", "CAR", "IBIS", "ROUBLE", "SEC", "AMENE", "TSE", "ANA", "HIT",
				"IL", "OP"};

		//Grille g=new Grille(ex1Grille, ex1DefsH, ex1DefsV, ex1MotsH, ex1MotsV);
		//Grille.numeroter(ex1Grille);
		//System.out.println(Grille.toString(ex1Grille));
		//System.out.println(Grille.toStringDefinitions(ex1Grille, ex1DefsH, ex1DefsV));
		//System.out.println(Grille.toStringSolution(ex1Grille,ex1MotsH, ex1MotsV));

		//Grille g2=new Grille(ex2Grille, ex2DefsH, ex2DefsV, ex2MotsH, ex2MotsV);
		//Grille.numeroter(ex2Grille);
		//System.out.println(Grille.toString(ex2Grille));
		//System.out.println(Grille.toStringDefinitions(ex2Grille, ex2DefsH,ex2DefsV));
		//System.out.println(Grille.toStringSolution(ex2Grille, ex2MotsH,ex2MotsV));


		int[][] resNum1 = {{1, 2, 3, 4, 5, 6},
				{7, 0, 0, 0, 0, 0},
				{8, 0, -1, 9, 0, 0},
				{10, 0, 11, 0, 0, 0},
				{12, 0, 0, 0, -1, 0},
				{0, -1, 13, 0, 14, 0},
				{15, 0, 0, 0, 0, 0}};
		int[][] resNum2 = {
				{1, 2, 3, 4, 5, 0, 6, 7, -1, 8},
				{9, 0, 0, 0, 0, -1, 10, 0, -1, 0},
				{0, -1, 11, 0, -1, 12, 0, 0, -1, 0},
				{13, 14, -1, 15, 0, 0, 0, 0, -1, 0},
				{16, 0, 17, 0, -1, 18, 0, 0, -1, 0},
				{19, 0, 0, 0, 20, -1, 21, 0, 22, 0},
				{-1, 23, 0, 0, 0, -1, 0, -1, 24, 0},
				{25, -1, 0, -1, 26, 27, 0, 28, 0, 0},
				{29, 30, 0, 31, -1, 0, -1, 32, 0, 0},
				{33, 0, 0, 0, 0, 0, 0, 0, 0, 0}};

		String resToString1="+--+--+--+--+--+--+\n| 1| 2| 3| 4| 5| 6|\n+--+--+--+--+--+--+\n| 7|  |  |  |  |  |\n+--+--+--+--+--+--+\n| 8|  |><| 9|  |  |\n+--+--+--+--+--+--+\n|10|  |11|  |  |  |\n+--+--+--+--+--+--+\n|12|  |  |  |><|  |\n+--+--+--+--+--+--+\n|  |><|13|  |14|  |\n+--+--+--+--+--+--+\n|15|  |  |  |  |  |\n+--+--+--+--+--+--+\n";
		String resToString2="+--+--+--+--+--+--+--+--+--+--+\n| 1| 2| 3| 4| 5|  | 6| 7|><| 8|\n+--+--+--+--+--+--+--+--+--+--+\n| 9|  |  |  |  |><|10|  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|  |><|11|  |><|12|  |  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|13|14|><|15|  |  |  |  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|16|  |17|  |><|18|  |  |><|  |\n+--+--+--+--+--+--+--+--+--+--+\n|19|  |  |  |20|><|21|  |22|  |\n+--+--+--+--+--+--+--+--+--+--+\n|><|23|  |  |  |><|  |><|24|  |\n+--+--+--+--+--+--+--+--+--+--+\n|25|><|  |><|26|27|  |28|  |  |\n+--+--+--+--+--+--+--+--+--+--+\n|29|30|  |31|><|  |><|32|  |  |\n+--+--+--+--+--+--+--+--+--+--+\n|33|  |  |  |  |  |  |  |  |  |\n+--+--+--+--+--+--+--+--+--+--+\n";

		String resDef1="HORIZONTALEMENT\n1 Pieces de theatres entierement chantees\n7 Conserve\n8 Sentier de grand randonnee\n9 Travail\n10 Intonation\n12 Apres le soir\n13 L'epoque des sapins\n15 A elle ou a lui\n\nVERTICALEMENT\n1 Partes vitales\n2 Touche\n3 Agent de liaison\n4 Descendant\n5 Animal qui brait\n6 Perspicace\n11 Salle de spectacle\n14 De cela\n";
		String resDef2="HORIZONTALEMENT\n1 Lynx d'afrique\n9 Rieuse, tachetee ou rayee\n10 Possede\n11 Hors de portee\n12 Antilope\n13 Adverbe de lieu\n15 Girafide\n16 Asile\n18 Rongeur\n19 Rois faineants de la savane\n21 Retrograde\n23 vient apres l'effort\n24 Pronom personnel\n26 Nourriture hebraique\n29 Reservoir\n32 Deesse\n33 Trompeuses africaines\n\nVERTICALEMENT\n1 Loup africain\n2 Champagne\n3 Debut de reunion\n4 Prenom masculin\n5 Demonstratif\n6 Pantheres\n7 Telle une mort precoce\n8 Noir ou blanc d'Afrique\n12 Autobus\n14 Oiseau sacre en Afrique\n17 Monnaie de l'ex URSS\n20 Ne se mouille pas\n22 Conduit\n25 Mi-mouche africaine\n27 Recueil amusant\n28 Succes\n30 Pronom\n31 Opus abrege\n";


		String resSol1 = "OPERAS\nRETENU\nGR*JOB\nACCENT\nNUIT*I\nE*NOEL\nSIENNE\n";
		String resSol2 =
				"CARACALS*R\nHYENE*EU*H\nA*UT*COB*I\nCI*OKAPI*N\nABRI*RAT*O\nLIONS*REAC\n*SUEE*D*ME\nT*B*CASHER\nSILO*N*INO\nELEPHANTES\n";
		System.out.println("\nTest de toStringSolution");
		String s = Grille.toStringSolution(resNum1,ex1MotsH,ex1MotsV);//ex1Grille
		if (!resSol1.equals(s)) {
			System.out.println("  Aie... toStringSolution devrait retourner : \n"+resSol1+"\n mais retourne :\n"+s);
			note=0;
		} else {
			s = Grille.toStringSolution(resNum2,ex2MotsH,ex2MotsV);//ex2Grille
			//  System.out.println(s);

			if (!resSol2.equals(s)) {
				System.out.println("  Aie... toStringSolution  devrait retourner : \n"+resSol2+"\n mais retourne :\n"+s);
				note=0;
			} 
		}

		if (note==100) {		
			System.out.println("   Ok. Votre code passe le test");
		}
		return note;
	}
	public static void main(String[] args) {
		int ntestDebutH = 0;
		int ntestDebutV = 0;
		int ntestNumeroter = 0;
		int ntestToString = 0;
		int ntestDefinitions=0;
		int ntestSolution=0;
		try {
			ntestDebutH = testDebutH();
			System.out.println("--> "+ntestDebutH+"/100");
		} catch(Throwable e) {
			afficheThrowable(e, "testDebutH");
		}
		finally {

			try {
				ntestDebutV = testDebutV();
				System.out.println("--> "+ntestDebutV+"/100");
			} catch(Throwable e) {
				afficheThrowable(e, "testDebutV()");
			}
			finally {
				try {
					ntestNumeroter = testNumeroter();
					System.out.println("--> "+ntestNumeroter+"/100");
				} catch(Throwable e) {
					afficheThrowable(e, "testNumeroter()");
				}
				finally {
					try {
						ntestToString = testToString();
						System.out.println("--> "+ntestToString+"/100");
					} catch(Throwable e) {
						afficheThrowable(e, "testToString()");
					}
					finally {
						try {
							ntestDefinitions = testDefinitions();
							System.out.println("--> "+ntestDefinitions+"/100");
						} catch(Throwable e) {
							afficheThrowable(e, "testDefinitions()");
						}
						finally {
							try {
								ntestSolution = testSolution();
								System.out.println("--> "+ntestSolution+"/100");
							} catch(Throwable e) {
								afficheThrowable(e, "testSolution()");
							}
							finally {

							}
						}
					}
				}
			}
		}
	}
	public static String toStringPrivate(int[][] tab) {
		String res="{";
		for (int lig=0; lig<tab.length; lig++) {
			res+="{";
			for (int  col=0; col<tab[lig].length-1; col++) {
				res+=tab[lig][ col]+", ";			
			}
			res+=tab[lig][tab[lig].length-1]+"},\n";
		}
		return res+"}";
	}
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
		TestsGrille t = new TestsGrille();
		t.setClipboardContents(s);
	}
}
