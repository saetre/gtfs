README:

How to extract from RegTOpp files to BusTUC files:

1) Set the right RegTOpp source folder and BusTUC output folder in 
	src/regtopToBusTUC/ConvertRegTop.java (line 50)

2) Run ConvertRegTop.java
       java  -cp bin regtopToBusTUC.ConvertRegTop


#(re-) compile from busstuc@vm-6114:/var/www/regtopp$ with 
javac -encoding UTF-8 -d bin src/regtopToBusTUC/*.java

# CURRENT FOLDERS
#	private static String GTFS_INPUT_ROOT_FOLDER = "data/tables_GTFS_2019_2";	// RS-180810; //FIXED?: Inneholder zip-filer fra https://www.entur.org/dev/rutedata/
#	private static String BUSTUC_OUTPUT_ROOT_FOLDER = "C:/eclipse/git/busstuc/db/tables"; //U-zippet
