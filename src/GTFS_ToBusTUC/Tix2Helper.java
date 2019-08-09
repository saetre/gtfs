package GTFS_ToBusTUC;

/**
	Helper class for file tix.
	getLine take a string as input and then locates the fields.
	
	@author Rune Sætre
	@version RegTopp 1.2
	@since 111219
*/

public class Tix2Helper extends TixHelper{
	
	// A way to get Number of stops for each tour-pattern, now that TDA is replaced by TMS
	TmsHelper TDA;

	int debugcounter = 0;

	/**		Specify where each field starts and stops	*/		
	public Tix2Helper( TmsHelper tda ) {
		this.TDA = tda;
		line = new FieldHolder("line",5,9);
		tour = new FieldHolder("tour",9,13);
		dayc = new FieldHolder("dayc",16,20);
		dirTms = new FieldHolder("dirTms",41,44); //dir and tms-nr
		deptime = new FieldHolder("deptime",44,48);

		//nstat = new FieldHolder("nstat",48,51); //v1.1d
		nstat = new FieldHolder("nstat",0,0);	// Not available any more? // RS-150328
		tdax = new FieldHolder("tdax",0,0);		// Not available any more? // RS-150328
	}


	/**	Locates the fields in a text string. Gets tdax and nstat from TMS-calc.
16120001000100200052640000000000       11020645000000010000010
16120001000200200052640000000000       11020715000000010000010
16120001000300200052640000000000       11020745000000010000010

route_id, service_id, trip_id, trip_headsign, trip_short_name, direction_id, wheelchair_accessible, shape_id
ATB:Line:0007-2019-01-01, ATB:Timetable:1600018-2019-01-01, ATB:VehicleJourney:00076030-2019-01-01, Sentrum,  , 0, 0,  at (ConvertGTFS.java:400)
	**/
	public void getLine( String iObj ){
		String[] parts = iObj.split(",");
		//String atbTimeIdDayDate = parts[1].split(":")[2];
		String idDay = parts[1].split(":")[2].split("-")[0];
		String[] atbLineID = parts[2].split(":");
		//String[] lineDates = atbLineID[2].split("-");

		if (debugcounter++ % 1000 == 0){
			ConvertGTFS.debug(2, "Doing trips.txt line "+debugcounter+" - "+parts[2] );
			//ConvertGTFS.debug(-1, "OOps! "+lineDates[0] );
		}

		line.value = atbLineID[2].substring(0,4);
		tour.value = atbLineID[2].substring(4,8);
		dayc.value = idDay.substring(4,7);
		dirTms.value = parts[5];

		//Referanse fra TURIX til TURMSTR er feltene Linjenr, Retning og Turmønsternr (GTFS: ATB:VehicleJourney:LineTour-YEAR-MM-DD)
		String ref = atbLineID[2];

		nstat.value = TDA.getNumberOfStops( ref );
		tdax.value = TDA.getLineNrOffset( ref );		//Use offset in file instead, like in version 1.1d
		deptime.value = TDA.getDepTime( ref ); // Sjekk stop_times! parts[4].substring(0, 5); // e.g. 15:00
	}//getLine
	
	/**	Display each FieldHolder.
	 *  The start and stop positions and the length are displayed to output. */
	public void printStatus(){
		System.out.print("TIX v1.2: ");
		super.printStatus();
	}//printStatus

	/**	Function to display a instance
	public String toString() {
		return "Tix2Helper("+line+", "+tour+", "+dayc+", "+deptime+", "+nstat+", "+tdax+")";
	}
	*/		
	/**	From v1.1d
	 * Inherited from TixHelper(1.1d)
	 *	substring(5,9) from input string
			public FieldHolder line;
	 *	substring(9,13) from input string	
			public FieldHolder tour;
	 *	substring(16,20) from input string	
			public FieldHolder dayc;
	 *	substring(44,48) from input string	
			public FieldHolder deptime;
	 *	substring(48,51) from input string	
			public FieldHolder nstat;
	 *	substring(51,58) from input string
			public FieldHolder tdax;
	static int MAX=20;
	**/	

	/**	From v1.2
	 *	substring(41,44) from input string (41 is dir, 42-43 is TurMønsterNr) */
	public FieldHolder dirTms;
	
}//Tix2Helper
