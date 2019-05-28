import java.awt.Toolkit;
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

public class TestsSoundex  implements ClipboardOwner {
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


	public static int testCoderCar() {
		int note=100;
		System.out.println("\nTest de coderCaractere :");
		char[] test = {'V', 'F', 'S', 'Z', 'X', 'J', 'G', 'R', 'M', 'N', 'L', 'D', 'T', 'C', 'K', 'Q', 'B', 'P', 'A', 'E', 'I', 'O', 'U', 'Y', ' ', '-', 'H'
		};
		char[] res = {'9', '9', '8', '8', '8', '7', '7', '6', '5', '5', '4', '3', '3', '2', '2', '2', '1', '1', '0', '0', '0', '0', '0', '0', '0', '0', '0', '0'
		};

		boolean ok = true; 
		int i=0; 
		while (ok && i<test.length) {
			ok = Soundex.coderCaractere(test[i])==res[i];
			if (!ok) {
				System.out.println("   Aie... coderCaractere("+test[i]+") retourne "+Soundex.coderCaractere(test[i])+" au lieu de "+res[i]);
				note=0;
			}
			i++;
		}
		if (ok) {
			System.out.println("   Ok. Votre code passe le test");
		}
		return note;
	}

	public static int testCoder() {
		int note=100;
		String[] noms= {"TREMBLAY", "GAGNON", "ROY", "COTE", "BOUCHARD", "GAUTHIER", "MORIN", "LAVOIE", "FORTIN", "GAGNE", "OUELLET", "PELLETIER", "BELANGER", "LEVESQUE", "BERGERON", "LEBLANC", "PAQUETTE", "GIRARD", "SIMARD", "BOUCHER", "CARON", "BEAULIEU", "CLOUTIER", "DUBE", "POIRIER", "FOURNIER", "LAPOINTE", "LECLERC", "LEFEBVRE", "POULIN", "THIBAULT", "STPIERRE", "NADEAU", "MARTIN", "LANDRY", "MARTEL", "BEDARD", "GRENIER", "LESSARD", "BERNIER", "RICHARD", "MICHAUD", "HEBERT", "DESJARDINS", "COUTURE", "TURCOTTE", "LACHANCE", "PARENT", "BLAIS", "GOSSELIN", "SAVARD", "PROULX", "BEAUDOIN", "DEMERS", "PERREAULT", "BOUDREAU", "LEMIEUX", "CYR", "PERRON", "DUFOUR", "DION", "MERCIER", "BOLDUC", "BERUBE", "BOISVERT", "LANGLOIS", "MENARD", "THERRIEN", "PLANTE", "BILODEAU", "BLANCHETTE", "DUBOIS", "CHAMPAGNE", "PARADIS", "FORTIER", "ARSENAULT", "DUPUIS", "GAUDREAULT", "HAMEL", "HOULE", "VILLENEUVE", "ROUSSEAU", "GRAVEL", "THERIAULT", "LEMAY", "ROBERT", "ALLARD", "DESCHÊNES", "GIROUX", "GUAY", "LEDUC", "BOIVIN", "CHARBONNEAU", "LAMBERT", "RAYMOND", "VACHON", "GILBERT", "AUDET", "JEAN", "LAROUCHE", "LEGAULT", "TRUDEL", "FONTAINE", "PICARD", "LABELLE", "LACROIX", "JACQUES", "MOREAU", "CARRIER", "BERNARD", "DESROSIERS", "GOULET", "RENAUD", "DIONNE", "LAPIERRE", "VAILLANCOURT", "FILLION", "LALONDE", "TESSIER", "BERTRAND", "TARDIF", "LEPAGE", "GINGRAS", "BENOÎT", "RIOUX", "GIGUÈRE", "DROUIN", "HARVEY", "LAUZON", "NGUYEN", "GENDRON", "BOUTIN", "LAFLAMME", "VALLEE", "DUMONT", "BRETON", "PARE", "PAQUIN", "ROBITAILLE", "GELINAS", "DUCHESNE", "LUSSIER", "SEGUIN", "VEILLEUX", "POTVIN", "GERVAIS", "PEPIN", "LAROCHE", "MORISSETTE", "CHARRON", "LAVALLEE", "LAPLANTE", "CHABOT", "BRUNET", "VEZINA", "DESROCHERS", "LABRECQUE", "COULOMBE", "TANGUAY", "CHOUINARD", "NOËL", "POULIOT", "LACASSE", "DAIGLE", "MARCOUX", "LAMONTAGNE", "TURGEON", "LAROCQUE", "ROBERGE", "AUGER", "MASSE", "PILON", "RACINE", "DALLAIRE", "EMOND", "GREGOIRE", "BEAUREGARD", "SMITH", "DENIS", "LEBEL", "BLOUIN", "MARTINEAU", "LABBE", "BEAUCHAMP", "STONGE", "CHARETTE", "DUPONT", "LETOURNEAU", "RODRIGUE", "CORMIER", "RIVARD", "MATHIEU", "ASSELIN", "STJEAN", "PLOURDE", "THIBODEAU", "BELISLE", "STLAURENT", "GODIN", "DESBIENS", "LAVIGNE", "DOUCET", "LABONTE", "MARCHAND", "BRASSARD", "FORGET", "PATEL", "MARCOTTE", "BELAND", "LAROSE", "DUVAL", "ARCHAMBAULT", "MALTAIS", "TREPANIER", "LALIBERTE", "BISSON", "BRISSON", "DUFRESNE", "BEAUDRY", "CHARTRAND", "HOUDE", "FRECHETTE", "LAFONTAINE", "GUILLEMETTE", "DROLET", "VINCENT", "RICHER", "GERMAIN", "LARIVIÈRE", "FERLAND", "TROTTIER", "PICHE", "BOULANGER", "SIROIS", "CHAREST", "PROVOST", "DURAND", "DUMAS", "SOUCY", "LAMOUREUX", "LACHAPELLE", "BEGIN", "BOILY", "CROTEAU", "SAVOIE", "PROVENCHER", "PREVOST", "DUGUAY", "LEMIRE", "DELISLE", "DESMARAIS", "LABERGE", "NAULT", "BOURGEOIS", "LAFRANCE", "LAGACE", "DAOUST", "BRAULT", "CASTONGUAY", "VALLIÈRES", "PELLERIN", "RIVEST", "BROCHU", "SAMSON", "LEPINE", "LEROUX", "LAROCHELLE", "BROUSSEAU", "SAUVE", "LAURIN", "CLEMENT", "BISSONNETTE", "LAJOIE", "AUBIN", "DOYON", "LABRIE", "GRONDIN", "FAUCHER", "CORRIVEAU", "TETREAULT", "BOURQUE", "DAGENAIS", "DUCHARME", "CARRIÈRE", "DUQUETTE", "LAFLEUR", "LANGEVIN", "CORBEIL", "BOURASSA", "PAGE", "TRUDEAU", "GAUDET", "CANTIN", "GOYETTE", "BOYER", "FRANCOEUR", "STLOUIS", "BARRETTE", "VIGNEAULT", "OUIMET", "BARIL", "LAFRENIÈRE", "MEUNIER", "LAPORTE", "JOSEPH", "BRODEUR", "LEGARE", "LAFOND", "ROSS", "MAHEUX", "RONDEAU", "MARQUIS", "BASTIEN", "PLOUFFE", "BOUFFARD", "LACOMBE", "TALBOT", "JULIEN", "ROULEAU", "ROUSSEL", "GUERIN", "BOULIANNE", "BEAUPRE", "ETHIER", "DUSSAULT", "LAMARCHE", "GALLANT", "GAUVIN", "LAMOTHE", "JOLY", "ROBICHAUD", "LEVEILLE", "BELLEMARE", "HUARD", "GARNEAU", "LEVASSEUR", "DUBUC", "MILLETTE", "POITRAS", "ROCHON", "BRIÈRE", "GUIMOND", "HUDON", "AUCLAIR", "BEAUCHEMIN", "BRISEBOIS", "GODBOUT", "GUILBAULT", "CADIEUX", "BROWN", "DUROCHER", "LAMARRE", "RICARD", "MONETTE", "CARDINAL", "TRAN", "STHILAIRE", "JOBIN", "DAIGNEAULT", "CHAMBERLAND", "DESCHAMPS", "BEAUDIN", "HENRY", "BOULET", "COLLIN", "SABOURIN", "DESLAURIERS", "DUMAIS", "GAMACHE", "MESSIER", "BEAUDET", "PILOTE", "BERTHIAUME", "COSSETTE", "HAMELIN", "RHEAUME", "CAMPEAU", "MALLETTE", "FLEURY", "PATRY", "STAMAND", "GARIEPY", "DAVID", "VIENS", "VEILLETTE", "BLANCHARD", "BINETTE", "LEBRUN", "STGERMAIN", "LADOUCEUR", "FISET", "MOISAN", "LOISELLE", "COMEAU", "MAILHOT", "DORE", "DERY", "MAILLOUX", "FOREST", "HUOT", "MORNEAU", "ALLAIRE", "VIAU", "AYOTTE", "MASSICOTTE", "GENEST", "THIVIERGE", "SIMONEAU", "ROBILLARD", "JALBERT", "CHAGNON", "POMERLEAU", "LEBLOND", "FRENETTE", "AUBE", "DESGAGNE", "JUTRAS", "RUEL", "THOMAS", "MURRAY", "BRUNEAU", "BELIVEAU", "COUTU", "LEFRANCOIS", "LHEUREUX", "DESROCHES", "CHARTIER", "COURCHESNE", "VERREAULT", "BRUNELLE", "BOULAY", "QUIRION", "MARCIL", "ALAIN", "DRAPEAU", "MARCEAU", "LIZOTTE", "PIERRE", "BUSSIÈRES", "DAMOURS", "NORMAND", "PRUDHOMME", "LORD", "BAILLARGEON", "LATOUR", "SEVIGNY", "THEBERGE", "PLAMONDON", "MATTE", "COUSINEAU", "CHARLAND", "RANCOURT", "BONNEAU", "ROYER", "PETIT", "LALANCETTE", "LANTHIER", "LEGER", "LEONARD", "STCYR", "CHARLEBOIS", "PAUL", "BUJOLD", "CHOQUETTE", "MCDONALD", "BELAIR", "IMBEAULT", "PIGEON", "CAOUETTE", "GARAND", "BROUILLETTE", "GOBEIL", "PINEAULT", "CHIASSON", "CHEVALIER", "DUGAS", "MOREL", "JONES", "TOUSIGNANT", "BIBEAU", "BLACKBURN", "GIROUARD", "MALO", "MAROIS", "PICHETTE", "COMTOIS", "MORENCY", "LAFOREST", "SARRAZIN", "ISABELLE", "NORMANDIN", "GUENETTE", "JOHNSON", "BORDELEAU", "JODOIN", "GROULX", "BRAZEAU", "SIMON", "BELLEY", "LEBEAU", "LARRIVEE", "MAJOR", "BOISSONNEAULT", "PATENAUDE", "ALARIE", "CARPENTIER", "GRENON", "BOSSE", "BESSETTE", "LAJEUNESSE", "BARBEAU", "MILLER", "MASSON", "COURNOYER", "RATTE", "CHRETIEN", "BOURGAULT", "LEBOEUF", "NOLET", "SYLVESTRE", "RAINVILLE", "SENECAL", "CHAPUT", "METHOT", "DESAULNIERS", "LEMELIN", "REID", "LEE", "JACOB", "MICHEL", "DESILETS", "FALARDEAU", "BUREAU", "GIGNAC", "LORTIE", "MELANCON", "PRIMEAU", "BOURGET", "ROBINSON", "CHENIER", "MALENFANT", "STAMOUR", "LANGLAIS", "WILLIAMS", "LECUYER", "CARBONNEAU", "CHARLES", "CAMPBELL", "PINARD", "GOUDREAU", "RIENDEAU", "STGELAIS", "ROBIDOUX", "WILSON", "HARDY", "LAMPRON", "JETTE", "CLERMONT", "GROLEAU", "BOIS", "GUERTIN", "LECOMPTE", "HEROUX", "SYLVAIN", "HALLE", "DESORMEAUX", "FRASER", "NERON", "STDENIS", "ADAM", "VOYER", "ALBERT", "VENNE", "ROCHETTE", "RODRIGUEZ", "MAYER", "RACICOT", "MIRON", "WHITE", "BROSSEAU", "LECOURS", "NADON", "PELCHAT", "STJACQUES", "STARNAUD", "THEORÊT", "CHASSE", "PAGEAU", "DELORME", "JOLICOEUR", "SAUVAGEAU", "BONIN", "GALARNEAU", "LAPRISE", "MONGRAIN", "THOMPSON", "VALIQUETTE", "CARIGNAN", "CUSSON", "LE", "DUMOULIN", "BABIN", "CHEVRIER", "LATULIPPE", "RIOPEL", "TURMEL", "CLAVEAU", "LAHAIE", "PITRE", "BOURDEAU", "LEMAIRE", "MIGNEAULT", "FECTEAU", "PAYETTE", "POISSON", "GRATTON", "THIFFAULT", "SCOTT", "CAYER", "GARCEAU", "BOISCLAIR", "BELZILE", "BLAIN", "STEMARIE", "BERNATCHEZ", "LARAMEE", "MAINVILLE", "DENEAULT", "BEAUVAIS", "BIGRAS", "CLICHE", "PARENTEAU", "PRINCE", "CLARKE", "LACOSTE", "DESSUREAULT", "ROCH", "BOURGOIN", "SINGH", "BOILEAU", "PELOQUIN", "LESPERANCE", "DESCOTEAUX", "ARBOUR", "ROUX", "JOYAL", "CHICOINE", "DUBEAU", "KELLY", "BEAUCHESNE", "JONCAS", "LOPEZ", "LAFORTUNE", "CHENARD", "ROUTHIER", "BELLAVANCE", "MOORE", "BRIEN", "HUBERT", "MAURICE", "GUINDON", "TOUCHETTE", "DUBREUIL", "SANTERRE", "PRONOVOST", "COURTEMANCHE", "DESHAIES", "CHALIFOUX", "SIGOUIN", "BROUILLARD", "GOYER", "HARRISSON", "LONGPRE", "REMILLARD", "FILIATRAULT", "VERVILLE", "BERARD", "VERMETTE", "ROCHELEAU", "COHEN", "BOURDON", "DUCHESNEAU", "ANCTIL", "AUBRY", "WONG", "GOUPIL", "LAMY", "VERRET", "FAFARD", "MONTPETIT", "DEBLOIS", "BOUTET", "QUESNEL", "GAREAU", "CORBIN", "HACHE", "TAYLOR", "AMYOT", "LALANDE", "BOURDAGES", "BAZINET", "JOLIN", "MARLEAU", "FLAMAND", "GRIMARD", "PERRIER", "NANTEL", "RHEAULT", "STMARTIN", "YOUNG", "COUTURIER", "TOUPIN", "BEAUMONT", "HETU", "GAUVREAU", "DEVEAULT", "FLEURANT", "DESAUTELS", "GUY", "RACETTE", "TOURIGNY", "CHAYER", "DANIS", "DUCLOS", "FOISY", "LOYER", "VALOIS", "COUILLARD", "LAVERDIÈRE", "MEILLEUR", "DORVAL", "KHAN", "MURPHY", "FORGUES", "OTIS", "DORION", "PHANEUF", "AWASHISH", "CHARPENTIER", "CHAMPOUX", "DESMEULES", "MITCHELL", "ARCAND", "TELLIER", "ANDERSON", "ALLEN", "BARON", "BARIBEAU", "CHAPDELAINE", "BACON", "CHAN", "METIVIER", "FRADETTE", "RANGER", "DESPRES", "LESAGE", "POLIQUIN", "GENEREUX", "PAPINEAU", "FRAPPIER", "LATREILLE", "MELOCHE", "GOUIN", "CRÊTE", "PEDNEAULT", "BERGER", "BRIAND", "OLIVIER", "TRUCHON", "SENECHAL", "LAVERGNE", "ROCHEFORT", "LELIÈVRE", "GAUMOND", "ROUSSY", "RENE", "PHAM", "DASILVA", "MCKENZIE", "MARION", "SERGERIE", "DOSTIE", "MADORE", "MONGEAU", "CREVIER", "FAUBERT", "PAIEMENT", "BOUSQUET", "FAVREAU", "GILL", "JUNEAU", "PARIS", "BEAUSOLEIL", "BOILARD", "ADAMS", "BELLEFLEUR", "POISSANT", "GONZALEZ", "LAFRAMBOISE", "RINGUETTE", "GARON", "MARIER", "DESNOYERS", "LI", "PERRAS", "DEZIEL", "GASCON", "CREPEAU", "GALIPEAU", "GARCÍA", "KAUR", "CODERRE", "HUYNH", "MILOT", "TASSE", "HERNANDEZ", "MARIN", "HENAULT", "LEHOUX", "ROBERTSON", "TAILLON", "BISAILLON", "LAPERRIÈRE", "VINET", "CARTIER", "POTHIER", "STGEORGES", "MUNGER", "ANGERS", "AUDY", "DULUDE", "LEDOUX", "PRUNEAU", "BOND", "BEAUSEJOUR", "BIRON", "BANVILLE", "PINETTE", "MARTINEZ", "PEREZ", "DUMOUCHEL", "LABRANCHE", "TRAHAN", "LAVIOLETTE", "BENARD", "HAINS", "GAULIN", "LACOURSIÈRE", "GUERARD", "PRATTE", "DUHAMEL", "DUFORT", "NOLIN", "LUPIEN", "ROUILLARD", "DUPERE", "CHOINIÈRE", "TURBIDE", "VANIER", "AUBUT", "DUPRAS", "BELLEAU", "DAVIS", "LACELLE", "BLONDIN", "HARNOIS", "LAFERRIÈRE", "SURPRENANT", "BOUGIE", "COLLARD", "HAMILTON", "FAUTEUX", "GENDREAU", "CABANA", "GOUGEON", "MAISONNEUVE", "BOUTHILLIER", "QUENNEVILLE", "BOURBONNAIS", "MAILLE", "MORAND", "BECHARD", "BELLEROSE", "NICOLAS", "TAILLEFER", "CAISSY", "LANCTOT", "CADORETTE", "LIRETTE", "DIOTTE", "FERNANDEZ", "ROGER", "LACHAÎNE", "THEROUX", "LAUZIER", "BEAUMIER", "DUHAIME", "GIASSON", "LEWIS", "LIMOGES", "CAMERON", "CANUEL", "MCLEAN", "DASTOUS", "DAVIAULT", "DUNN", "CHEN", "DIAMOND", "STEWART", "JOURDAIN", "POUDRIER", "DOYLE", "NORMANDEAU", "LACERTE", "NICOL", "DECARIE", "LOUIS", "ROBERTS", "PEARSON", "WALKER", "DANSEREAU", "PEREIRA", "LEVY", "MONTREUIL", "DALPE", "LACHARITE", "LETENDRE", "VANDAL", "DANEAU", "MIREAULT", "AHMED", "DESFOSSES", "BELHUMEUR", "GEMME", "JOMPHE", "LANGELIER", "MAGNAN", "SAUCIER", "BRISSETTE", "COLLINS", "LY", "RUEST", "HELIE", "LAPALME", "GORDON", "BOURBEAU", "GONTHIER", "RIVERIN", "TOUSSAINT", "APRIL", "DIAZ", "MANSEAU", "BACHAND", "HURTUBISE", "KING", "ALEXANDRE", "BLEAU", "MARK", "BEAUCAGE", "CAUCHON", "NEVEU", "PAINCHAUD", "QUINTAL", "ROSE", "DUPRE", "MORAIS", "LEGROS", "PHARAND", "BOISJOLI", "FRANCOIS", "CARDIN", "QUEVILLON", "BERGEVIN", "LEVAC", "KIROUAC", "BUSQUE", "CONSTANTINEAU", "DELÂGE", "VALCOURT", "MONTMINY", "DOIRON", "SAURIOL", "SAVAGE", "SOULIÈRES", "DERASPE", "GRANT", "GUERETTE", "LAM", "LORANGER", "HOGUE", "CARLE", "FERRON", "LEMOINE", "FRIGON", "JUTEAU", "FORCIER", "PINSONNEAULT", "HERVIEUX", "DARAICHE", "MCDUFF"}; 
		String[] res = {"T651", "G75 ", "R   ", "C3  ", "B263", "G36 ", "M65 ", "L9  ", "F635", "G75 ", "O43 ", "P436", "B457", "L982", "B676", "L145", "P23 ", "G63 ", "S563", "B26 ", "C65 ", "B4  ", "C436", "D1  ", "P6  ", "F656", "L153", "L246", "L919", "P45 ", "T143", "S316", "N3  ", "M635", "L536", "M634", "B363", "G656", "L863", "B656", "R263", "M23 ", "H163", "D876", "C36 ", "T623", "L252", "P653", "B48 ", "G845", "S963", "P648", "B35 ", "D568", "P643", "B36 ", "L58 ", "C6  ", "P65 ", "D96 ", "D5  ", "M626", "B432", "B61 ", "B896", "L574", "M563", "T65 ", "P453", "B43 ", "B452", "D18 ", "C517", "P638", "F636", "A685", "D18 ", "G364", "H54 ", "H4  ", "V459", "R8  ", "G694", "T643", "L5  ", "R163", "A463", "D825", "G68 ", "G   ", "L32 ", "B95 ", "C615", "L516", "R53 ", "V25 ", "G416", "A3  ", "J5  ", "L62 ", "L743", "T634", "F535", "P263", "L14 ", "L268", "J28 ", "M6  ", "C6  ", "B656", "D868", "G43 ", "R53 ", "D5  ", "L16 ", "V452", "F45 ", "L453", "T86 ", "B636", "T639", "L17 ", "G576", "B53 ", "R8  ", "G76 ", "D65 ", "H69 ", "L85 ", "N75 ", "G536", "B35 ", "L945", "V4  ", "D53 ", "B635", "P6  ", "P25 ", "R134", "G458", "D285", "L86 ", "S75 ", "V48 ", "P395", "G698", "P15 ", "L62 ", "M683", "C65 ", "L94 ", "L145", "C13 ", "B653", "V85 ", "D862", "L162", "C451", "T57 ", "C563", "N4  ", "P43 ", "L28 ", "D74 ", "M628", "L537", "T675", "L62 ", "R167", "A76 ", "M8  ", "P45 ", "R25 ", "D46 ", "E53 ", "G676", "B676", "S53 ", "D58 ", "L14 ", "B45 ", "M635", "L1  ", "B251", "S357", "C63 ", "D153", "L365", "R367", "C656", "R963", "M3  ", "A845", "S375", "P463", "T13 ", "B484", "S346", "G35 ", "D815", "L975", "D23 ", "L153", "M625", "B686", "F673", "P34 ", "M623", "B453", "L68 ", "D94 ", "A625", "M438", "T615", "L416", "B85 ", "B685", "D968", "B36 ", "C636", "H3  ", "F623", "L953", "G453", "D643", "V525", "R26 ", "G65 ", "L696", "F645", "T636", "P2  ", "B457", "S68 ", "C683", "P698", "D653", "D58 ", "S2  ", "L568", "L214", "B75 ", "B4  ", "C63 ", "S9  ", "P695", "P698", "D7  ", "L56 ", "D484", "D856", "L167", "N43 ", "B678", "L965", "L72 ", "D83 ", "B643", "C835", "V468", "P465", "R983", "B62 ", "S585", "L15 ", "L68 ", "L624", "B68 ", "S9  ", "L65 ", "C453", "B853", "L7  ", "A15 ", "D5  ", "L16 ", "G653", "F26 ", "C69 ", "T364", "B62 ", "D758", "D265", "C6  ", "D23 ", "L946", "L579", "C614", "B68 ", "P7  ", "T63 ", "G3  ", "C535", "G3  ", "B6  ", "F652", "S348", "B63 ", "V754", "O53 ", "B64 ", "L965", "M56 ", "L163", "J81 ", "B636", "L76 ", "L953", "R8  ", "M8  ", "R53 ", "M628", "B835", "P49 ", "B963", "L251", "T413", "J45 ", "R4  ", "R84 ", "G65 ", "B45 ", "B16 ", "E36 ", "D843", "L562", "G453", "G95 ", "L53 ", "J4  ", "R123", "L94 ", "B456", "H63 ", "G65 ", "L986", "D12 ", "M43 ", "P368", "R25 ", "B6  ", "G53 ", "H35 ", "A246", "B25 ", "B681", "G313", "G414", "C38 ", "B65 ", "D626", "L56 ", "R263", "M53 ", "C635", "T65 ", "S346", "J15 ", "D754", "C516", "D825", "B35 ", "H56 ", "B43 ", "C45 ", "S165", "D846", "D58 ", "G52 ", "M86 ", "B3  ", "P43 ", "B635", "C83 ", "H545", "R5  ", "C51 ", "M43 ", "F46 ", "P36 ", "S353", "G61 ", "D93 ", "V58 ", "V43 ", "B452", "B53 ", "L165", "S376", "L326", "F83 ", "M85 ", "L84 ", "C5  ", "M43 ", "D6  ", "D6  ", "M48 ", "F683", "H3  ", "M65 ", "A46 ", "V   ", "A3  ", "M823", "G583", "T967", "S5  ", "R146", "J416", "C75 ", "P564", "L145", "F653", "A1  ", "D875", "J368", "R4  ", "T58 ", "M6  ", "B65 ", "B49 ", "C3  ", "L965", "L68 ", "D862", "C636", "C628", "V643", "B654", "B4  ", "Q65 ", "M624", "A45 ", "D61 ", "M62 ", "L83 ", "P6  ", "B868", "D568", "N653", "P635", "L63 ", "B467", "L36 ", "S975", "T167", "P453", "M3  ", "C85 ", "C645", "R526", "B5  ", "R6  ", "P3  ", "L452", "L536", "L76 ", "L563", "S326", "C641", "P4  ", "B743", "C23 ", "M235", "B46 ", "I514", "P75 ", "C3  ", "G653", "B643", "G14 ", "P543", "C85 ", "C946", "D78 ", "M64 ", "J58 ", "T875", "B1  ", "B421", "G63 ", "M4  ", "M68 ", "P23 ", "C538", "M652", "L968", "S685", "I814", "N653", "G53 ", "J585", "B634", "J35 ", "G648", "B68 ", "S5  ", "B4  ", "L1  ", "L69 ", "M76 ", "B854", "P353", "A46 ", "C615", "G65 ", "B8  ", "B83 ", "L758", "B61 ", "M46 ", "M85 ", "C656", "R3  ", "C635", "B674", "L19 ", "N43 ", "S498", "R594", "S524", "C13 ", "M3  ", "D845", "L545", "R3  ", "L   ", "J21 ", "M24 ", "D843", "F463", "B6  ", "G752", "L63 ", "M452", "P65 ", "B673", "R158", "C56 ", "M459", "S356", "L574", "W458", "L26 ", "C615", "C648", "C514", "P563", "G36 ", "R53 ", "S374", "R138", "W485", "H63 ", "L516", "J3  ", "C465", "G64 ", "B8  ", "G635", "L251", "H68 ", "S495", "H4  ", "D865", "F686", "N65 ", "S358", "A35 ", "V6  ", "A416", "V5  ", "R23 ", "R367", "M6  ", "R23 ", "M65 ", "W3  ", "B68 ", "L268", "N35 ", "P423", "S372", "S365", "T63 ", "C8  ", "P7  ", "D465", "J426", "S97 ", "B5  ", "G465", "L168", "M576", "T518", "V423", "C675", "C85 ", "L   ", "D545", "B15 ", "C96 ", "L341", "R14 ", "T654", "C49 ", "L   ", "P36 ", "B63 ", "L56 ", "M754", "F23 ", "P3  ", "P85 ", "G635", "T943", "S23 ", "C6  ", "G62 ", "B824", "B484", "B45 ", "S356", "B653", "L65 ", "M594", "D543", "B98 ", "B768", "C42 ", "P653", "P652", "C462", "L283", "D864", "R2  ", "B675", "S57 ", "B4  ", "P425", "L816", "D823", "A616", "R8  ", "J4  ", "C25 ", "D1  ", "K4  ", "B285", "J528", "L18 ", "L963", "C563", "R36 ", "B495", "M6  ", "B65 ", "H163", "M62 ", "G535", "T23 ", "D164", "S536", "P659", "C635", "D8  ", "C498", "S75 ", "B646", "G6  ", "H685", "L571", "R546", "F436", "V694", "B63 ", "V653", "R24 ", "C5  ", "B635", "D285", "A523", "A16 ", "W57 ", "G14 ", "L5  ", "V63 ", "F963", "M531", "D148", "B3  ", "Q854", "G6  ", "C615", "H2  ", "T46 ", "A53 ", "L453", "B637", "B853", "J45 ", "M64 ", "F453", "G656", "P6  ", "N534", "R43 ", "S356", "Y57 ", "C36 ", "T15 ", "B53 ", "H3  ", "G96 ", "D943", "F465", "D834", "G   ", "R23 ", "T675", "C6  ", "D58 ", "D248", "F8  ", "L6  ", "V48 ", "C463", "L963", "M46 ", "D694", "K5  ", "M61 ", "F678", "O38 ", "D65 ", "P59 ", "A8  ", "C615", "C518", "D854", "M324", "A625", "T46 ", "A536", "A45 ", "B65 ", "B61 ", "C134", "B25 ", "C5  ", "M396", "F63 ", "R576", "D816", "L87 ", "P425", "G568", "P15 ", "F616", "L364", "M42 ", "G5  ", "C63 ", "P354", "B676", "B653", "O496", "T625", "S524", "L967", "R296", "L496", "G53 ", "R8  ", "R5  ", "P5  ", "D849", "M258", "M65 ", "S676", "D83 ", "M36 ", "M57 ", "C696", "F163", "P53 ", "B823", "F96 ", "G4  ", "J5  ", "P68 ", "B84 ", "B463", "A358", "B494", "P853", "G584", "L965", "R573", "G65 ", "M6  ", "D856", "L   ", "P68 ", "D84 ", "G825", "C61 ", "G41 ", "G62 ", "K6  ", "C36 ", "H5  ", "M43 ", "T8  ", "H653", "M65 ", "H543", "L8  ", "R163", "T45 ", "B845", "L16 ", "V53 ", "C636", "P36 ", "S376", "M576", "A576", "A3  ", "D43 ", "L38 ", "P65 ", "B53 ", "B876", "B65 ", "B594", "P53 ", "M635", "P68 ", "D524", "L165", "T65 ", "L943", "B563", "H58 ", "G45 ", "L268", "G63 ", "P63 ", "D54 ", "D963", "N45 ", "L15 ", "R463", "D16 ", "C56 ", "T613", "V56 ", "A13 ", "D168", "B4  ", "D98 ", "L24 ", "B453", "H658", "L96 ", "S616", "B7  ", "C463", "H543", "F38 ", "G536", "C15 ", "G75 ", "M859", "B346", "Q594", "B615", "M4  ", "M653", "B263", "B468", "N248", "T496", "C8  ", "L523", "C363", "L63 ", "D3  ", "F653", "R76 ", "L25 ", "T68 ", "L86 ", "B56 ", "D5  ", "G85 ", "L8  ", "L578", "C565", "C54 ", "M245", "D838", "D943", "D5  ", "C5  ", "D53 ", "S363", "J635", "P36 ", "D4  ", "N653", "L263", "N24 ", "D26 ", "L8  ", "R163", "P685", "W426", "D586", "P6  ", "L9  ", "M536", "D41 ", "L263", "L353", "V534", "D5  ", "M643", "A53 ", "D898", "B456", "G5  ", "J51 ", "L574", "M75 ", "S26 ", "B683", "C458", "L   ", "R83 ", "H4  ", "L145", "G635", "B61 ", "G536", "R965", "T853", "A164", "D8  ", "M58 ", "B253", "H631", "K57 ", "A485", "B4  ", "M62 ", "B27 ", "C25 ", "N9  ", "P523", "Q534", "R8  ", "D16 ", "M68 ", "L768", "P653", "B874", "F652", "C635", "Q945", "B679", "L92 ", "K62 ", "B82 ", "C583", "D47 ", "V426", "M535", "D65 ", "S64 ", "S97 ", "S468", "D681", "G653", "G63 ", "L5  ", "L657", "H7  ", "C64 ", "F65 ", "L5  ", "F675", "J3  ", "F626", "P585", "H698", "D62 ", "M239"
		};
		System.out.println("\nTest de coder :");

		boolean ok=true;
		int ires=0;

		while (ok && ires<noms.length) {
			ok = Soundex.coder(noms[ires]).equals(res[ires]);

			if (!ok) {
				System.out.println("   Aie... coder(\""+noms[ires]+"\") retourne \""+Soundex.coder(noms[ires])+"\" au lieu de \""+res[ires]+"\"");
				note=0;
			}
			ires++;
		}

		if (note==100) {		
			System.out.println("   Ok. Votre code passe le test");
		}
		return note;
	}


	public static void main(String[] args) {
		int ntestCoderCar = 0;
		int ntestCoder = 0;

		try {
			ntestCoderCar = testCoderCar();
			System.out.println("--> "+ntestCoderCar+"/100");
		} catch(Throwable e) {
			afficheThrowable(e, "testCoderCar");
		}
		finally {

			try {
				ntestCoder = testCoder();
				System.out.println("--> "+ntestCoder+"/100");
			} catch(Throwable e) {
				afficheThrowable(e, "testCoder()");
			}
			finally {

			}
		}
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
		TestsLog t = new TestsLog();
		t.setClipboardContents(s);
	}


}
