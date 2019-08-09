<?php 


class zipTools{
	
	private $filepath;
	public $error = false;
	
	private $AdmCodeAndSerial;
	private $version;
	private $validityPeriod;
	private $files;
	
	function __construct($filepath){
		date_default_timezone_set('Europe/Oslo');
		if(!is_int(zip_open($filepath))){
			$this->filepath = $filepath;
			$this-> setVersion();
			$this-> setAdmCodeAndSerial();
			$this->setFiles();
			$this->setValidityPeriod();
		}else{
			$this->error = true;
		}
	}
	
	function getAdmCodeAndSerial(){
		if(isset($this->AdmCodeAndSerial))
			return $this->AdmCodeAndSerial;
	}
	function getValidityPeriod(){
		if(isset($this->validityPeriod))
			return $this->validityPeriod;
	}
	function getFiles(){
		if(isset($this->files))
			return $this->files;
	}
	function getVersion(){
		if(isset($this->version))
			return $this->version;
	}
	
	public function openZip(){
		return zip_open($this->filepath);
	}
	
	
	private function setAdmCodeAndSerial(){
		$filename = false;
		$zip = $this->openZip();
		while($entry = zip_read($zip)){
			$extension = strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION));
			if($extension == 'tda' || $extension == 'tms')
				$filename = pathinfo(zip_entry_name($entry), PATHINFO_FILENAME);
		}
		zip_close($zip);
		$this->AdmCodeAndSerial = $filename;
	}
	
	
	private function setVersion(){
		$zip = $this->openZip();
		$version = false;
		//		 Let etter fil med versjonsnummer, introdusert i versjon 1.2
		while($entry = zip_read($zip)){
			$extension = strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION));
			if($extension == 'frm'){
				$version = trim(substr(zip_entry_read($entry, zip_entry_filesize($entry)), 4));
			}elseif(!$version && $extension == 'vlp'){ // Let etter vognløpfil, introdusert i versjon 1.1.D*
				$version = '1.1.D*';	#RS-121206 BUG, missed if (!version)!
			}
		}
		zip_close($zip);
		if(!$version)
			$version = '1.1.C eller tidligere';
		$this->version = $version;
	}
	
	
	private function setFiles(){
		if( isset($this->version) && $this->version == '1.2' )	#Hva gjelder for 1.1.D* ??
			$files = array(
				'tix' => 'MISSING',
				'tms' => 'MISSING',
				'hpl' => 'MISSING',
				'dko' => 'MISSING',
				'frm' => 'MISSING'
			);
		else
			$files = array(
				'tix' => 'MISSING',
				'tda' => 'MISSING',
				'hpl' => 'MISSING',
				'dko' => 'MISSING'
			);
		$zip = $this->openZip();
		while($entry = zip_read($zip)){
			//			$extension = strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION));
			if(strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION)))
				$files[strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION))] = 'OK';
		}
		zip_close($zip);
		$this->files =  $files;
	}
	
	public function extractTo($input){
		$zip = new ZipArchive;
		if($zip->open($this->filepath) === TRUE){
			$zip->extractTo($input);
			$zip->close();
			return true;
		}
		return false;
	}
	
	public function countFiles(){
		$zip = $this->openZip();
		$files = array();
		while($entry = zip_read($zip)){
			$fileExtension = strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION));
			if(array_key_exists($fileExtension,$files)){
				$files[$fileExtension]++;
			}else{
				$files[$fileExtension] =  1;
			}
		}
		zip_close($zip);
		foreach($files as $f)
		if($f > 1)
			return false;
		return true;
	}
	
	
	private function setValidityPeriod(){
		$zip = $this->openZip();
		$valid = array();
		$data = false;
		while($entry = zip_read($zip)){
			$extension = strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION));
			if($extension == 'dko')
				$data = zip_entry_read($entry, zip_entry_filesize($entry));
		}
		zip_close($zip);
		if($data){
			$data = array_filter(explode("\n", $data));
			$startDate = mktime(0,0,0, substr($data[0],2,2), substr($data[0],4,2), substr($data[0],0,2)); // m d y
			
			$data = array_slice($data,1);
			
			$data = array_map( function($a){
				$days = substr($a, 8);
				return strrpos($days, "1");
				
			}, $data);
			asort($data);
			
			$stopDate = strtotime('+'.array_pop($data).' day', $startDate);
			
			$valid = array('from' => date('d.m.Y', $startDate), 'to' => date('d.m.Y', $stopDate), 'fromTime' => $startDate, 'toTime' => $stopDate);
		}
		$this->validityPeriod =  $valid;
	}
}

?>