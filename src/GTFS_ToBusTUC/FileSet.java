
package GTFS_ToBusTUC;

/**
	Class designed to hold dko, hpl, tda (/tms) and tix file names and the directory for the files<BR>
	The filename without the file type is put in name<br>
	The dko, hpl, tda (/tms) and tix files share the same name.
	Used by the helper classes<BR>
	
	@author Tore Bruland (RegTOpp)
	@author Rune Sætre Since 2011.12.18
	@version 2018.08.11
	
*/

public class FileSet {

	private String dko, hpl, tda, tms, tix,  name;
	@SuppressWarnings("unused")
	private String age, inf; //Agent and Info
	private String directory;

	public FileSet() {
		//dko=""; 		hpl=""; 		tda=""; 		tms=""; 		tix=""; 		age=""; 		inf="";   		name="";
	}//CONSTRUCTOR
	
	/**	Creates the name of the directory where the files are to be stored. */
	public void setFolderDate( String rootFolder, String iStr ) {
		//directory = rootFolder +File.separator+ (name+"_"+iStr).toLowerCase();
		setDirectory(rootFolder + "/" + (name+"_"+iStr).toLowerCase());
	} // setFolderDate
	
	/**	Set the name of the files: calendar,routes,trips,stops,stop_times,agency,feed_info.
	 * 								DKO, 	TMS, 	TIX, HPL,	TDA, 	AGE,	route_info
	 **/
	public void setFilename(String folder, String iName, String iFiletype) {
		//iName = folder+"/"+iName; //RS-180810
		
		if (iName.toLowerCase().equals("calendar")) {
			dko=folder+"/"+iName+"."+iFiletype;
			setName(dko);
		} else if (iName.toLowerCase().equals("stops")) {
			hpl=folder+"/"+iName+"."+iFiletype;
		} else if (iName.toLowerCase().equals("trips")) {
			tix=folder+"/"+iName+"."+iFiletype;  // Try using stop_times instead, for tix as well
		} else if (iName.toLowerCase().equals("routes")) {
			tda=folder+"/"+iName+"."+iFiletype;
		} else if (iName.toLowerCase().equals("stop_times")) {
			tms=folder+"/"+iName+"."+iFiletype;
		} else if (iName.toLowerCase().equals("agency")) {
			age=folder+"/"+iName+"."+iFiletype;
		} else if (iName.toLowerCase().equals("feed_info")) {
			inf=folder+"/"+iName+"."+iFiletype;
		} else {
			ConvertGTFS.debug(1, " setFilename filetype = "+iName+", should be in [calendar,routes,trips,stops,stop_times(,agency,feed_info)?]" );
		}
		ConvertGTFS.debug(2, this.toString() );
	}

	private void setName(String calendarFile_Name) {
		name = "R160"; // TODO: RS-180811 Hard-coded.		
	}

	public String toString() {
		return "dko="+dko+" hpl="+hpl+" tda="+tda+" tms="+tms+" tix="+tix+" directory="+getDirectory()+" name="+name;
	}

	public String getDkoName() {
		return dko;
	}

	public String getTmsName() {
		return tms;
	}

	public String getTdaName() {
		return tda;
	}

	public String getTixFileName() {
		return tix;
	}

	public String getHplFileName() {
		return hpl;
	}

	public void setHplFileName(String newName) {
		hpl = newName;
	}

	public String getDirectory() {
		return directory;
	}

	public void setDirectory(String directory) {
		this.directory = directory;
	}

}//class FileSet
