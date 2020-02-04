/*
Arbeid 2010...
------------------
møte 18 jan: 1 time

prog 19 jan: 4
reprog 20 jan 4
regprog 22 jan 3
regprog 23 jan 4
regprog 24 jan 5 + 3 kveld (comp)
regprog 29 jan 1 feilretting
regprog 30 jan 1 feilretting
regprog 6 feb  1, dokumentasjon
*/

package GTFS_ToBusTUC;

import java.io.*;
import java.util.*;

/**
	Program with Main function. Called from a directory with GTFS files. <BR>
	<P>
	GTFS files can be downloaded from https://www.entur.org/dev/rutedata/
								https://developer.entur.org/content/stops-and-timetable-data
	--> Unzip to tables_GTFS_2019_2
	
	main: Assuming input 'c:/eclipse/regtopp/data/tables_GTFS_2019_2' at (ConvertGTFS.java:99)
	main: Assuming output 'C:/eclipse/git/busstuc/db/tables' at (ConvertGTFS.java:106)
	<P>
	OLD: Program reads the catalog and finds files of type: ["*.dko", "*.hpl", "*.tda", "*.tix"]. <BR>
	NEW: Program reads the catalog and finds files of type: ["calendar.txt", "stops.txt", "trips.txt", "routes.txt", "stop_times.txt", etc.]. <BR>
	The hpl file's xyzæøå is converted into file *_hpl.txt. <BR><BR>

	Creates sub directories in catalog based on calendar files and the records: YYMMDD.<BR>
	A file with name R0021.DKO with a first record of 071224 will result in the directory r0021_071224.<BR>
	All prolog entries are put in this directory.<BR><BR>

	Every input file has its Helper class that take care of position of fields in a the text record.<BR>
	Every prolog predicate file has its Holder class that take care of all the fields that are produced
	 in the prolog predicates.<BR><BR>

	Each set of input files are read and after that the prolog predicates are generated. <BR>
	The previous information is deleted before reading a new set of input files.<BR>

	ReghlpHolder class must be read before regpasHolder, because regpasHolder uses data generated in ReghlpHolder<br>

	Convert input files into prolog predicates.

	@author Tore Bruland	@since 2010.01.01
	@author Tore Amble		@since 2011.01.01
	@author Rune Sætre		@since 2011.12.01
	@author Atsuya Hasegawa	@since 2019.08-09
	@version 20180812 */

public class ConvertGTFS {

	public static final int  DEBUG  = 1;

	public static final String INPUT_ENCODING = "utf-8";	//= "iso-8859-1";
	public static final String OUTPUT_ENCODING = "utf-8";	//= "iso-8859-1";

	//	private static final String GTFS_INPUT_ROOT_FOLDER = "RegTopp AtB";	// RS-120113
	//	private static final String GTFS_INPUT_ROOT_FOLDER = "RegTopp AtB/AtB (12.09.13)"; //Inneholde bare zip
	//	private static final String GTFS_INPUT_ROOT_FOLDER = "RegTopp AtB/Gråkallbanen";
	//	private static String BUSTUC_OUTPUT_ROOT_FOLDER = "C:/eclipse/workspace_prolog/BussTUC/db/tables"; //U-zippet

	//private static String GTFS_INPUT_ROOT_FOLDER = "../data/tables_GTFS_2020.01";	// RS-180810; //FIXED?: Inneholder zip-filer fra https://www.entur.org/dev/rutedata/
	//private static String BUSTUC_OUTPUT_ROOT_FOLDER = "../../busstuc/db/tables"; //U-zippet

//	private static String GTFS_INPUT_ROOT_FOLDER = "C:/cygwin/home/satre/git/busstuc/db/tables_GTFS_20.01";	// RS-180810; //FIXED?: Inneholder zip-filer fra https://www.entur.org/dev/rutedata/
//	private static String BUSTUC_OUTPUT_ROOT_FOLDER = "C:/cygwin/home/satre/git/busstuc/db/tables"; //U-zippet
	
	private static String GTFS_INPUT_ROOT_FOLDER = "C:/Users/satre/git/busstuc/db/tables_GTFS_20.01";	// RS-180810; //FIXED?: Inneholder zip-filer fra https://www.entur.org/dev/rutedata/
	private static String BUSTUC_OUTPUT_ROOT_FOLDER = "C:/Users/satre/git/busstuc/db/tables"; //U-zippet
	

	/** Holds input file sets */
	public TreeMap<String,FileSet> index;

//	private String rmask1; // TA-080220  // AtB always starts on Monday!?
//	private int weekdayvalue;

	/**
	 * Verbosity of the output
	 * @param level		High level => More output
	 * @param message	What do write	 */
	/** Debug method to include the filename, line-number and method of the caller */
	static void debug(int d, String msg) {
		if (DEBUG >= d) {
			StackTraceElement[] st = Thread.currentThread().getStackTrace();
			int stackLevel = 1;
			while ( st[stackLevel].getMethodName().equals("debug") ){
				stackLevel++;
			}
			StackTraceElement e = st[stackLevel];
			if ( d < 0 ){
				System.err.println( e.getMethodName() + ": " + msg + " at (" + e.getFileName()+":"+e.getLineNumber() +")" );
			}else{
				System.out.println( e.getMethodName() + ": " + msg + " at (" + e.getFileName()+":"+e.getLineNumber() +")" );
			}
		}
	} // debug

	public static void main( String[] args ){
		String usage = "USAGE:\n	GTFS_ToBusTUC.ConvertRegTop"
				+ " [INPUT_FOLDER] [OUTPUT_FOLDER]\n"
				+ "Converting the GTFS source in INPUT_FOLDER,\n" +
				" creating the prolog code which is stored in OUTPUT_FOLDER\n";

		if(args.length != 2){
			System.err.println( usage );	//System.exit(1);
			debug(-1, "Assuming input '"+GTFS_INPUT_ROOT_FOLDER+"'" );
			//GTFS_INPUT_ROOT_FOLDER = "data/AtB2015påske";	// RS-121217
			//GTFS_INPUT_ROOT_FOLDER = "data/temp/AtB Nyttårsaften 2012";	// RS-121223
			//	private static final String GTFS_INPUT_ROOT_FOLDER = "RegTopp AtB/Gråkallbanen";

			//BUSTUC_OUTPUT_ROOT_FOLDER = "C:/cygwin/home/satre/git/busstuc/db/tables"; //U-zippet //OLD OUTPUT FOLDER
			debug(-1, "Assuming output '"+BUSTUC_OUTPUT_ROOT_FOLDER+"'" );
		}else{
			ConvertGTFS.GTFS_INPUT_ROOT_FOLDER = args[0];
			ConvertGTFS.BUSTUC_OUTPUT_ROOT_FOLDER = args[1];
		}

		ConvertGTFS pgm = new ConvertGTFS();
		debug(0, "\nConverting all files in sub-folders of "+ConvertGTFS.GTFS_INPUT_ROOT_FOLDER+"\n" );
		pgm.getFileSets( GTFS_INPUT_ROOT_FOLDER ); // find all input file sets
		//pgm.convertHpl(); // convert æøå in hlp files. OBSOLETE?
		pgm.readFiles( ConvertGTFS.BUSTUC_OUTPUT_ROOT_FOLDER ); // create prolog predicates
		System.out.println("\nStored all converted files in "+ConvertGTFS.BUSTUC_OUTPUT_ROOT_FOLDER+"\n");
		if ( GTFS_INPUT_ROOT_FOLDER.endsWith("temp") ){
			pgm.removeFiles(GTFS_INPUT_ROOT_FOLDER);
		}
	}//main	
	

	/**	Get input files sets and put them in a TreeMap<String,FileSet> class.
	 *          index = TreeMap< FileSetName, FileSet >
	 *  Scans all sub-folders, to find more filesets in each sub-folder.  */
	public TreeMap<String,FileSet> getFileSets( String folderName ) {
		if ( index == null ){ index = new TreeMap<String,FileSet>(); }
		try{
			String name, filetype;
			FileSet fileSet;
			// enumerate all files in the directory
			File pathName = new File( folderName );
			String[] fileNames = pathName.list();
			if (fileNames == null){
				String error = new String();
				error += "I couldn't find any files in folder: "+folderName+" !!!\n";
				error += "Please make sure the pathname REGTOP_PARENT_FOLDER is correct";
				error += "   in (ConvertGTFS.java:45), and try again...";
				throw new IOException( error );
			}else{
				for (int i = 0; i < fileNames.length; i++) {
					File f = new File(pathName.getPath(), fileNames[i]);
					//System.out.println("fileName: "+f.getName());
					if (f.isDirectory() ){
						System.out.println( "\n Do folder: "+f.getName() );
						index.putAll( getFileSets( folderName+'/'+f.getName() ) ); // Recursive !!!
					}else{
						//Filetype is [calendar,routes,trips,stops,stop_times(,agency,feed_info)?] (aka DKO, HPL, TIX,  TMS or TDA)

						int pos = f.getName().lastIndexOf('.');
						if (pos > -1) { //if filetype given
							name = f.getName().substring(0,pos); 
							//String fullname = folderName+'/'+name; // RS-120120 Avoid folder clashing !
							String fullname = folderName; // RS-120120 Avoid folder clashing! No, Multiple sets in same folder!
							filetype = f.getName().substring(pos+1);
							if (name.equals("agency") || name.equals("stops") || name.equals("trips") || name.equals("routes") || name.equals("calendar") || name.equals("feed_info") || name.equals("stop_times") ) {
								debug(2, "name("+name+") filetype("+filetype+") ==> "+fullname);
								// find the previous FileSet or create a new.
								if (index.containsKey(fullname)) {
									debug(2, "\n...Using existing fileset for "+fullname+": "+index.get(fullname) );
									fileSet = index.get(fullname);
									fileSet.setFilename( folderName, name, filetype);
								} else {
									fileSet = new FileSet();
									fileSet.setFilename( folderName, name, filetype);
									index.put( fullname, fileSet );
									debug(1, "\n...New fileset created for "+fullname+": "+index.get(fullname) );
								}//if existing name
							}//if correct filetype to add if filetype (.FIL) belongs to RegTopp fileSet.
						}//if pos>-1 : filetype is given
					}//if directory, else file
				}//for each file
			}//if any files...
		}catch(IOException e) {
			e.printStackTrace();
		}//try path REGTOP_PARENTS
		return index;
	}//getFileSets


	/**	Convert æøå in hlp file. */
	public void convertHpl() {
		FileSet temp;
		for ( Iterator<FileSet> iter=index.values().iterator(); iter.hasNext(); ) {
			temp = iter.next();
			debug(1, "open fileset: "+temp);
			File f = new File(temp.getHplFileName());
			String newName = temp.getHplFileName().replace(".HPL" , "_HPL.txt");
			File f1 = new File( newName );
			if( !f.exists() && f.length()<0 ) {
				System.out.println("The specified file is not exist");
			} else {
				try {
					FileInputStream finp=new FileInputStream(f);
					FileOutputStream foutp=new FileOutputStream(f1);
					int b;
					b=finp.read();
					while ( b !=-1 ) {
						foutp.write( Util.strangeAscii(b) );
						b=finp.read();
					}
					finp.close();
					foutp.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			temp.setHplFileName(newName);
			debug(1, temp.getHplFileName() );
		} // for each regtop-folder fileset
	} // convertHpl

	private void removeFiles(String folderName) {
					File pathName = new File( folderName );
					pathName.delete();
	}//removeFiles

	public void readFiles( String bustucOutputRootFolder ) {
		// classes for the files
		DkoHelper theDKO = new DkoHelper();
		//HplHelper theHPL = new HplHelper();	// OBSOLETE IN GTFS? // RS-180811
		TdaHelper theTDA = null;
		TixHelper theTIX = null;
		//TdaHelper theTDA = new TdaHelper(); // Will be replaced by TMS in RegTopp version 1.2 
		//TixHelper theTIX = new TixHelper(); // Was changed from version 1.1D to 1.2

		// classes for the Prolog predicates
		RegbusHolder theRegbus = new RegbusHolder( bustucOutputRootFolder );
		RegdepHolder theRegdep = new RegdepHolder( bustucOutputRootFolder );
		ReghplHolder theReghpl = new ReghplHolder( bustucOutputRootFolder );
		RegcompHolder theRegcomp = new RegcompHolder( bustucOutputRootFolder );
		RegdkoHolder theRegdko = new RegdkoHolder( bustucOutputRootFolder );
		RegpasHolder theRegpas = new RegpasHolder( bustucOutputRootFolder );
		theRegpas.setReghplHolder(theReghpl); // regpas uses statid from ReghplHolder class

//		RegcutHolder theRegcut = new RegcutHolder(); // added TA

		if ( index.size() == 0 ) {
			debug(-1, "******> no file sets found.");
			return;
		}
		FileSet theFileSet;

		try {
			for (Iterator<FileSet> iter=index.values().iterator(); iter.hasNext();) {
				theRegbus.clear();
				theRegdep.clear();
				theReghpl.clear();
				theRegcomp.clear();
				theRegdko.clear();
				theRegpas.clear();

				//theRegcut.clear(); // added TA

				theFileSet = iter.next();

				
				// read DKO file, the first record TO get FIRST FROM YYMMDD
				debug(1, "Read dkoFile "+theFileSet.getDkoName() );
				BufferedReader DkoFile = new BufferedReader( new FileReader( theFileSet.getDkoName() ) );

				String header = DkoFile.readLine(); // RS-180811 Read (SKIP) header-line DO:
				//	service_id,Monday,Tuesday,Wednesday,Thursday,Friday,Saturday,Sunday,start_date,end_date

				String record = DkoFile.readLine(); // TA-110208 RS-200202 First real information line
			
				String[] parts = record.split(","); 	//ATB:Timetable:1600018-2018-08-13,1,0,1,0,0,0,0,20180813,20181220
				header = parts[8]+"1"; // RS-180811 : Is FromDate really always Monday(ie 1)?!
				int count_dko=0;
				theDKO.getLine1( header ); //header is for example = 20150112.1 (Date and dayofweek-number, 1=monday)
				theFileSet.setFolderDate( BUSTUC_OUTPUT_ROOT_FOLDER, theDKO.yymmdd.value );
				debug(0, "Processing FileSet:\n"+theFileSet );

				//is directory already created?
				File newDir = new File( theFileSet.getDirectory() );
				if ( newDir.mkdir() ) {
					System.out.println(theFileSet.getDirectory()+" created ...");
				} else {
					debug(-1, theFileSet.getDirectory()+" NOT CREATED!!! Using Existing folder?...");
					//System.exit(-1);
				}
				theRegbus.directory = theFileSet.getDirectory();
				theRegdep.directory = theFileSet.getDirectory();
				theReghpl.directory = theFileSet.getDirectory();
				theRegcomp.directory = theFileSet.getDirectory();
				theRegdko.directory = theFileSet.getDirectory();
				theRegpas.directory = theFileSet.getDirectory();

				//theRegcut.directory = theFileSet.directory; // added TA
				// Read rest of DKO file from record 1 (line 2) --> END
				while ( record != null ) {
					count_dko++;

//					String[] infoNameDate = parts[0].split(":");
//					String[] nameYearMonthDate = infoNameDate[2].split("-");
//					// RS-150328 len=1 // Monday=1, Tuesday=2, etc.  TODO: GTFS ALWAYS VALID FROM A MONDAY?!?
//					record = nameYearMonthDate[0].substring(0, 3)+"1"+nameYearMonthDate[0].substring(3, 7);
					
					debug(2, "record.length="+record.length()+" "+record );

					theDKO.getLine3( record );  // TA-110208 // RS-200202
					theRegdko.setDko( theDKO.dayc.value, theDKO.x.value, theDKO.y.value, theDKO.mask1.value,
							theDKO.mask2.value,theDKO.mask3.value,theDKO.mask4.value,theDKO.masks.value );

					//AtB always start on Monday? (GTFS)
//					weekdayvalue = new Integer( theDKO.weekday.value ).intValue(); // TA-080220
//					rmask1 = rotatemask( weekdayvalue, theDKO.mask1.value ); 

					theRegdep.setDko( theDKO.dayc.value, theDKO.mask1.value ); // rmask1 ); // TA 081223 // RS-2020.02.02 Always Monday First in AtB!?

					record = DkoFile.readLine(); // TA-110208
//					if (record != null){
//						parts = record.split(","); //service_id,monday,tuesday,wednesday,thursday,friday,saturday,sunday,start_date,end_date
//					}
				}//while more records left to process

				debug(1, count_dko+" CALENDAR records are read from "+theFileSet.getDkoName() );
				DkoFile.close();

				// Read HPL file
				debug(1, "Reading HPL (stops) file: "+theFileSet.getHplFileName() );
				BufferedReader HplFile = null;
				try{
					HplFile = new BufferedReader( new InputStreamReader( new FileInputStream( theFileSet.getHplFileName() ), INPUT_ENCODING) );
					header = HplFile.readLine();
					record = HplFile.readLine();
				}catch (Exception e){
					debug(-1, "Error reading HPL (stops.txt) file: "+theFileSet.getHplFileName() + e.getMessage() );
					System.exit(-1);
				}
				
				if (record == null){
					debug(-1, "No content in HPL (stops.txt) file: "+theFileSet.getHplFileName() );
					System.exit(-1);					
				}
			
				int count_hpl=0;
				while ( record != null  ){
					if ( record.contains("Quay") ){
						count_hpl++;
						parts = HplHelper.getLine( record );

						//theReghpl.setHpl( theHPL.statnr.value, theHPL.statname.value );	// NOT USED BY GTFS // RS-180811
						//theRegcomp.setHpl( theHPL.statnr.value, theHPL.statname.value );
						theReghpl.setHpl( parts[0], parts[1] );	//  statnr - statname	// RS-180811
						theRegcomp.setHpl( parts[0], parts[1] );						
					}else{
						//debug(3, "Skipping (Parent) Stop: "+ record );
					}//if "real" stop (Not parent Stop)
					record = HplFile.readLine();
				}//While more HPL (stops) lines
				debug(1, count_hpl+" HPL (stops) records are read" );
				HplFile.close();

				// Read TDA or TMS file (NOW with GTFS: stop_timess.txt)
				debug(1, "Reading TDA TurData / TMS TurMønster (stop_times.txt) file: "+theFileSet.getTmsName() );
				BufferedReader TdaHelperFile = new BufferedReader( new FileReader( theFileSet.getTmsName() ) );
				theTDA = new TmsHelper();
				theTIX = new Tix2Helper( (TmsHelper)theTDA );
//				if ( DEBUG >1 ) {
//					theDKO.printStatus();
//					theHPL.printStatus();
//					theTDA.printStatus();
					theTIX.printStatus();
//				}//if debug

				//Continue reading TDA / TMS in the right version
				header = TdaHelperFile.readLine();
				record = TdaHelperFile.readLine();
				int count_TdaHelper=0;
				while ( record != null ){
					count_TdaHelper++;
					theTDA.getLine( record, count_TdaHelper );
					theRegpas.setTda( theTDA.statnr.value, theTDA.arr.value, theTDA.dep.value );
					record = TdaHelperFile.readLine();
				}
				theTDA.getLine( record, count_TdaHelper ); //Make sure the last stopCount is stored! // RS-151515?
				debug(0, count_TdaHelper+" regPas (stop_times.txt) records are read");
				TdaHelperFile.close();

				// Read TIX file (NOW: trips.txt)
				debug(1, "Reading TIX (trips.txt) file: "+theFileSet.getTixFileName() );
				BufferedReader TixFile = new BufferedReader( new FileReader( theFileSet.getTixFileName() ) );
				header = TixFile.readLine();
				record = TixFile.readLine();
				debug(1, header +"\n"+ record );
				int count_Tix=0;
				while ( record != null ){
					count_Tix++;
					theTIX.getLine( record );
					theRegbus.setTix( theTIX.line.value, theTIX.tour.value );
					theRegdep.setTix( theTIX.line.value, theTIX.tour.value, theTIX.deptime.value, theTIX.tdax.value, theTIX.dayc.value );
					theRegpas.setTix( theTIX.tdax.value, theTIX.nstat.value );
					record = TixFile.readLine();
				}
				System.out.println(count_Tix+" regDep (TIX? Trips.txt?) records are read");
				TixFile.close();

				theRegbus.writePredicates();
				theRegdep.writePredicates( theRegpas.tdaxIndex ); // bruker konverteringstabell for tdax nummer som fjernes
				theReghpl.writePredicates();
				theRegcomp.writePredicates();
				theRegdko.writePredicates( theDKO.yymmdd.value, theDKO.weekday.value ); // TA-080219
				theRegpas.writePredicates();

				//theRegcut.writePredicates( bustucOutputRootFolder ); // added TA // RS-111219 ToDo this!

			} // end for each file-set iterator
		} catch (IOException e ) {
			System.out.println("********** IOException "+e);
			e.printStackTrace();
		}//try-catch
	}//readFiles

	public String rotatemask(int  wd, String mask) { // rotates string(0:7) wd times. // RS-150328 1 -> 0
		if( wd == 0 ) { // RS-150328
			return  mask;
		}else{
			return ( mask.substring(8-wd,7) + mask.substring(0,8-wd) );
		}
	}

	/*public void testFunc() {
		RegcompHolder theObj = new RegcompHolder();
		theObj.setHpl("007","Ugla veien");
		for (Iterator iter=theObj.composite_stat.iterator();iter.hasNext(); ) {
			System.out.println(iter.next());
		}
		IsGateRec temp = theObj.isGateEnds("stortingsv");
		System.out.println("prefix:"+temp.strPrefix);
		System.out.println("suffix:"+temp.strSuffix);
		System.out.println("boolean:"+temp.bEndswith);
  	}*/

}// class ConvertGTFS
