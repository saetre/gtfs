
package GTFS_ToBusTUC;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeMap;

//import regtopToBusTUC.ConvertRegTop;

/**
	Class for the prolog predicates passes4 (3?) and ntourstops.<BR>
	Prune away duplicate tdax information. <br>
	Different tdax numbers with equal constants are converted to only one tdax. 
	
	@author Tore Bruland
	@author Rune Sætre	@since 2011.12.19
*/

public class RegpasHolder {

	private static final String OUTPUT_ENCODING = ConvertGTFS.OUTPUT_ENCODING;

	private static final String START_NUMBER = "999";

	/** controls the duplicate tdax numbers	*/		
	public ArrayList<TdaSegment> noDup;

	/** holds record for prolog predicate ntourstops */		
	public ArrayList<String> ntourstops;
	
	/** holds record for prolog predicate passes3 */
	public ArrayList<String> passes3;

	/**	holds tdaRec redords, tdax number point to start in table and nstat indicate how many records are used. 	 */	
	public ArrayList<TdaRec> tdaTab;

	/**	name of the directory */
	public String directory;

	// pointer
	private ReghplHolder theHpl;
	
	/**	Converting table for old tdax numbers */	
	public TreeMap<String,String> tdaxIndex;

	/** @param bustucOutputRootFolder Where to write the regxxx.pl files (for bustuc/db/tables)	 */
	public RegpasHolder( String bustucOutputRootFolder ){
		ntourstops = new ArrayList<String>();
		passes3 = new ArrayList<String>();
		tdaTab = new ArrayList<TdaRec>();	
		directory = bustucOutputRootFolder;
		noDup = new ArrayList<TdaSegment> ();
		tdaxIndex = new TreeMap<String,String>();
	}//CONSTRUCTOR
	
	/**	instance is used to hold several filesets<br> method is called before each new fileset is read 	*/
	public void clear() {
		ntourstops.clear();
		passes3.clear();
		directory="";
		tdaTab.clear();
		noDup.clear();
		tdaxIndex.clear();
	}//clear


	public void debug(int level, String message){
		ConvertGTFS.debug(level, message);
	} // debug
	
	/**	collects data from the tix file. 	*/
	public void setTix( String iTdax, String iNstat ){
		String tdax=Util.trimZero(iTdax);
		String nstat=Util.trimZero(iNstat);	
		if (tdax.equals("3811") || tdax.equals("6765")) {
			System.out.println("skal være like. tdax: "+tdax+"  nstat:"+nstat);
		}
				
		// finn segment: TODO Gjør om første og siste arr/dep til 999.   RS-180812 !!! Gjør om fra absolutt tid, til delay fra start...
		//System.out.println("debug<"+nstat+">");
		int pos = Integer.parseInt( tdax.trim() );
		int antall = Integer.parseInt(nstat.trim());
		TdaSegment temp = new TdaSegment( tdax.trim() ); // new TdaSegment(tdax);  %% TA-110216
		
		String start = START_NUMBER;
		Integer start_minutes = new Integer( START_NUMBER );
		for (int j=0;j<antall;j++) {
			if ( j+pos < 1 ){	// RS-120120
				debug(0, "ERROR: Antall="+antall+", pos="+pos+", size="+tdaTab.size()+", and TDA Segment["+antall+pos+"]:"+tdaTab.get(antall+pos) );
			}else{
				TdaRec leg = tdaTab.get(j+pos-1);
				if ( start.equals( START_NUMBER ) ){
					start = leg.arr;
					Integer start_hours = new Integer( leg.arr ) / 100;
					start_minutes = new Integer( leg.arr ) - start_hours * 100 + start_hours * 60;
					leg.arr = START_NUMBER;
				}else{
					Integer hours = new Integer( leg.arr ) / 100;
					Integer minutes = new Integer( leg.arr ) - hours * 100 + hours * 60;
					Integer arrival = minutes - start_minutes;
					leg.arr = arrival.toString();
				}
				Integer hours = new Integer( leg.dep ) / 100;
				Integer minutes = new Integer( leg.dep ) - hours * 100 + hours * 60;
				Integer departure = minutes - start_minutes;
				leg.dep = departure.toString();
				
				temp.theTab.add( leg );
			}
		}
		
		//System.out.println("debug<"+nstat+">start:"+start+temp.theTab );
		if (tdax.equals("3811") || tdax.equals("6765")) {
			debug(0, "Funker denne?"+temp ); // RS-2019.06.19
		}
		int i=0;
		int dupNr=-1;
		//System.out.println("the size:"+noDup.size());
		while ( i<noDup.size() && dupNr == -1 ) {
			if ( noDup.get(i).equalTo(temp) ) {
				debug(3, "duplicate found. "+temp.tdax+" and "+noDup.get(i).tdax + ", the size:"+noDup.size() );
				dupNr = i;
				tdaxIndex.put(tdax,noDup.get(i).tdax); // lag konverteringstabell for tdax nummer som fjernes
			}
			i++;
		}//while more segments?
		
		// duplikat ?
		TdaRec tempRec;
		String passKey;
		if ( dupNr==-1) {
			for (int j=0; j<temp.theTab.size();j++) {
				tempRec = temp.theTab.get(j);
				passKey = "passes4("+temp.tdax+ "," + tempRec.statnr+"," + theHpl.getStatid(tempRec.statnr)+ ","+(j+1)+","+tempRec.arr+","+tempRec.dep+").";
				// TA-100226
				if ( !passes3.contains(passKey) ) { 
					passes3.add(passKey);
				}//if new stop-key
			}//for each stop
			String key = "ntourstops("+tdax+","+nstat+").";
			if ( !ntourstops.contains(key) ) {
				ntourstops.add(key);
			}
			noDup.add(temp);
		} else {
			String key = "ntourstops("+noDup.get(dupNr).tdax+","+nstat+")."; // get the tdax from orginal
			if ( !ntourstops.contains(key) ) {
				ntourstops.add(key);
			}//if new key for ntourstops
		}//if dup==-1, else dup found?
	}//setTix
	
	/**		collects data from the tda file.	*/		
	public void setTda(String iStatnr, String iArr, String iDep) {
		String arr = Util.trimZero(iArr);
		String dep = Util.trimZero(iDep);
		tdaTab.add( new TdaRec( iStatnr, arr, dep ) );
	}
	
	/**
		Pointer to a ReghplHolder class. getStatid method is used in setTix method 
	*/
	public void setReghplHolder(ReghplHolder iHpl) {
		theHpl=iHpl;
	}
	
	/**
	*	write the records of the passes3 and ntourstops predicates to the proper directory and filename regpas.pl
	*/	
	public void writePredicates() {
		//System.out.println("ntours antall="+ntourstops.size());
		System.out.println( "TimeStamp: "+ new Timestamp(new Date().getTime()) );
		try {
			BufferedWriter outFile = new BufferedWriter(
				new OutputStreamWriter(new FileOutputStream(directory+File.separator+"regpas.pl"), OUTPUT_ENCODING));
			
			if(ConvertGTFS.OUTPUT_ENCODING.equals("utf-8"))
				outFile.write("/* -*- Mode:Prolog; coding:utf-8; -*- */\n");
			else
				outFile.write("/* -*- Mode:Prolog; coding:iso-8859-1; -*- */\n");
			
			outFile.write("%% Generated by regtopToBusTUC.RegpasHolder.java on "+ new Timestamp(new Date().getTime()) +"\n\n");
			outFile.write("%% PASSES4 (TOURTYPE, STATION_NR, STATION, SEQ_NR, ARR_DELAY, DEP_DELAY)\n");
			outFile.write("%%    Example: passes4(17326,16011035,anton_grevskotts_vei,22,15,16).\n\n");
			outFile.write("%% NTOURSTOPS (TOURTYPE,LENGTH). Tourtype almost matches the linenumber in regpas.pl and FILE.TDA or FILE.TMS\n");
			outFile.write("%%    Defines the length (number of stations) in the tourtype, e.g. ntourstops(12713,5).\n");

			outFile.write("\n\n");
			outFile.write("%% PASSES4 (TOURTYPE, STATION NUMBER, STATION, SEQNUMBER, ARR DELAY, DEP DELAY)\n");
			for (Iterator<String> iter=passes3.iterator();iter.hasNext(); ) {
				outFile.write(iter.next()+"\n");
			}				
			for (Iterator<String> iter=ntourstops.iterator();iter.hasNext(); ) {
				outFile.write(iter.next()+"\n");
			}
			outFile.close();
		} catch (IOException e ) {
			System.out.println("********** IOException "+e);
			e.printStackTrace();
		}		
	}//writePredicates
	
}//class RegpasHolder


class TdaRec {
	String statnr, arr, dep;

	public TdaRec(String iStatnr, String iArr, String iDep) {
		statnr=iStatnr;
		arr=iArr;
		dep=iDep;
	}
	public String toString() {
		return statnr+","+arr+","+dep;
	}
	public boolean equalTo(TdaRec iObj) {
		return (statnr.equals(iObj.statnr) && arr.equals(iObj.arr) && dep.equals(iObj.dep) );
	}
}//Class TdaRec

class TdaSegment {
	public TdaSegment(String iTdax) {
		theTab = new ArrayList<TdaRec>();
		tdax = iTdax;
	}//TdaSegment CONSTRUCTOR
	
	public boolean equalTo(TdaSegment iObj) {
		if ( theTab.size() != iObj.theTab.size() ) return false;
		boolean isEqual=true;
		int i=0;
		while (isEqual && i<theTab.size()) {
			isEqual = theTab.get(i).equalTo(iObj.theTab.get(i));
			i++;
		}
		return isEqual;
	}//equalTo
	
	public String toString() {
		String temp = "tdaSegment(\ntdax:"+tdax+"\n";
		for (Iterator<TdaRec> iter=theTab.iterator(); iter.hasNext(); ) {
			temp += iter.next()+"\n";
		}
		temp += ").";
		return temp;
	}
	
	String tdax;
	ArrayList<TdaRec> theTab;
}//class TdaSegment
