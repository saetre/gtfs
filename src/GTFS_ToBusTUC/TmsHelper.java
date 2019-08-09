
package GTFS_ToBusTUC;

import java.util.HashMap;
/**
	Helper class for file tda. 
	getLine take a string as input and then locates the fields.
	
	@author Rune Sætre (RS), 2011
	@version 111218 : Adding .tms functionality to replace .tda functions
*/

public class TmsHelper extends TdaHelper{

	/**	Specify where each field starts and stops */	
	public TmsHelper() {	// .tda is replaced by .tms files... %% RS-2011.12.18... Moved into TdaHelper.java as an alternativ there. %% RS-2018.08.12
		id = new FieldHolder("line",5,12); // Line, Dir, Tour

		statnr = new FieldHolder("statnr",15,23);
		arr = new FieldHolder("arr",23,26);
		dep = new FieldHolder("dep",28,31);
	}//CONSTRUCTOR
	
	/* substring(5,9), (9,10) and (10,12) from input string */
	public FieldHolder id;

	//Number of stops read for current tour
	int currentStopCount = 0;

	//Number of stops, lineNr and DepTime, for each given tour.
	private static HashMap<String,Integer> nStops = new HashMap<String,Integer>();
	private static HashMap<String,Integer> lineNrs = new HashMap<String,Integer>();
	private static HashMap<String,String> depTimes = new HashMap<String,String>();

	//Remember the previously read id.
	private String previousId;

	/**	Locates the fields in a text string
	 * ATB:VehicleJourney:00900001-2018-08-13, NSR:Quay:73125, 0, 15:00:00, 15:00:00, 0, 1, ,	 **/
	public void getLine( String iObj, int lineNr ){
		if ( iObj != null ){
			currentStopCount++;
			String[] parts = iObj.split(",");
			String quayID = parts[1].split(":")[2];
			String[] hourMinSec = parts[4].split(":");
			
			id.value = parts[0].split(":")[2]; // Remove .split(":")[2] to go World-wide ;) RS-180812
			statnr.value = quayID;

			arr.value = parts[3].split(":")[0] + parts[3].split(":")[1]; //TODO Use offsets.
			dep.value = hourMinSec[0]+hourMinSec[1];
		}else{
			id.value = "null";
		}
		//If new chain of stations, store previous count and reset.
		if ( ! id.value.equals( previousId ) ){
			if ( previousId != null ){
				nStops.put( previousId, currentStopCount);
			}
			previousId = id.value;
			lineNrs.put( id.value, lineNr );
			depTimes.put( id.value, dep.value );
			currentStopCount=0;
		}//if new id --> new offset
	}//getLine

	public String getNumberOfStops( String ref ) {
		if ( nStops.get(ref) == null ){
			System.out.println("Couldn't find number of stops for ref: "+ref);
			if ( lineNrs.keySet().iterator().hasNext() ){
				System.out.println(""
						+ "Moving on to next stop instead: "+nStops.keySet().iterator().next());
			}
		}else{
			return nStops.get(ref).toString();
		}//if real TMS-id ref
		//return "null";
		return "0"; 	// RS-150328. If failing...
	}//getNumberOfStops

	public String getLineNrOffset(String ref) {
		if (lineNrs.get(ref) == null){
			System.out.println("Couldn't find ref: "+ref);
			if ( lineNrs.keySet().iterator().hasNext() ){
				System.out.println("moving to next ref: "+lineNrs.keySet().iterator().next());
				System.out.println( "Returning '0' for obsolete field." );
				return "0";
			}
		}else{
			return lineNrs.get(ref).toString();
		}//if real TMS-id ref
		//return "null"; //002300011020011601071899900000000000000  000000
		return "null"; //002300011020011601071899900000000000000  000000
	}//getLineNumber

	/**	Function to display a instance
	public String toString() {
		return "TdaHelper("+statnr+", "+arr+", "+dep+")";
	}

	/**	Display each FieldHolder.
	 *  The start and stop positions and the length are displayed to output. */
	public void printStatus() {
		System.out.print("TMS: ");
		System.out.println(id);
		super.printStatus();
	}

	public String getDepTime(String ref) {
		if ( depTimes.get(ref) == null ){
			System.out.println("Couldn't find number of stops for ref: "+ref);
			if ( lineNrs.keySet().iterator().hasNext() ){
				System.out.println(""
						+ "Moving on to next Time instead: "+depTimes.keySet().iterator().next());
			}
		}else{
			return depTimes.get(ref).toString();
		}//if real TMS-id ref
		return "0"; 	// RS-150328. 		//return "null"; If failing...
	}//getDepTime

	/**
	 * 	the station number substring(1,9) from input string
	public FieldHolder statnr;
		substring(9,12) from input string
	public FieldHolder arr;
		substring(12,15) from input string
	public FieldHolder dep;
	static int MAX=20;
	*/
}//TmsHelper
