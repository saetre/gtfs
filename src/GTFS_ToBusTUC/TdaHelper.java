
package GTFS_ToBusTUC;

/**
	Helper class for file tda. 
	getLine take a string as input and then locates the fields.
	
	@author Tore Bruland, 2011
	@author satre
	@version 111218, Rune Sætre (RS): Adding .tms functionality
*/

public class TdaHelper {
	/**	Specify where each field starts and stops */	
	public TdaHelper() {	// Obsolete in case of .tms files... %% RS-111218
		statnr = new FieldHolder("statnr",1,9); //length = 8
		arr = new FieldHolder("arr",9,12);	 //length = 3
		dep = new FieldHolder("dep",12,15);	 //length = 3
	}

	public void setTms(boolean tms) {
		if (tms){
			statnr = new FieldHolder("statnr",15,23);  //length = 8
			arr = new FieldHolder("arr",24,27);	 //length = 3
			dep = new FieldHolder("dep",29,32);	 //length = 3
		}//if .TMS file
	}//setTmsFields

	/**	Locates the fields in a text string
	 * OLD FORMAT: 
1611000110200116010718 999 00 000 000000000  000000
1611000110200216010014 001 00 001 000000000  000475
161100011020031601001300200002000000000  000820
161100011020041601044200300003000000000  001067
161100011020051601019200400004000000000  001381
161100011020061601071300500005000000000  001643
161100011020071601071200600006000000000  002033
161100011020081601071100700007000000000  002684
161100011020091601074000800008000000000  002986
161100011020101601000000900009000000000  003427
161100011020111601070901000010000000000  004226
161100011020121601070801100011000000000  004474
161100011020131601070701300013000000000  005152
161100011020141601070601500015000000000  005831
161100011020151601070501600016000000000  006496
161100011020161601070401700017000000000  006767
161100011020171601070301800018000000000  007251
161100011020181601070201900019000000000  008008
161100011020191601070102000020000000000  008314
1611000110202016010700 021 00 021 000000000  008685
	 * NEW Example: trip_id,			stop_id, stop_sequence, arrival_time, departure_time,pickup_type,drop_off_type,shape_dist_traveled,stop_headsign
			    	ATB:VehicleJourney:00900001-2018-08-13,	NSR:Quay:73125,	0,	15:00:00,	15:00:00,	0,	1,	,
	 **/
	public void getLine( String iObj, int lineNr ) {
		if ( iObj != null ){
//			statnr.value = iObj.substring( statnr.start-1, statnr.stop-1 );
//			arr.value = iObj.substring( arr.start-1, arr.stop-1 );
//			dep.value = iObj.substring( dep.start-1, dep.stop-1 );
			String[] parts = iObj.split(",");
			String[] AgentQuayID = parts[0].split(":");
			
			statnr.value = AgentQuayID[2];
			arr.value = parts[3];
			dep.value = parts[4];
		}else{ //if not null
			System.out.println("Last one?");
		}
	}//getLine

	/**
		Function to display a instance
	*/		
	public String toString() {
		return "TdaHelper("+statnr+", "+arr+", "+dep+")";
	}

	/**	Display each FieldHolder.
	 *  The start and stop positions and the length are displayed to output.	*/
	public void printStatus() {
		System.out.println("TDA/TMS:");
		System.out.println(statnr);
		System.out.println(arr);
		System.out.println(dep);
		System.out.println("MAX:"+MAX);
	}
	
	/**	statnr = the station number substring(1,9) from input string 15-23 for TMS */
	public FieldHolder statnr;

	/** arr = substring(9,12) from TDA input string or 24-27 for TMS */
	public FieldHolder arr;
	
	/**	substring(12,15) from input string or 29-32 for TMS */
	public FieldHolder dep;
	
	//What's this?
	static int MAX=20;
	
}//TdaHelper

