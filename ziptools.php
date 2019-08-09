<?php

function getAdmCodeAndSerial($filepath){
	$filename = false;
	$zip = zip_open($filepath);
	while($entry = zip_read($zip))
		if(strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION)) == 'tda' || strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION)) == 'tms')
			$filename = pathinfo(zip_entry_name($entry), PATHINFO_FILENAME);
	return $filename;
}

function getVersion($filepath){
	
	$version = false;
	
	// Let etter fil med versjonsnummer, introdusert i versjon 1.2
	$zip = zip_open($filepath);
	while($entry = zip_read($zip))
		if(strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION)) == 'frm')
			$version = trim(substr(zip_entry_read($entry, zip_entry_filesize($entry)), 4));
	zip_close($zip);
	
	
	// Let etter vognløpfil, introdusert i versjon 1.1.D*
	$zip = zip_open($filepath);
	while(!$version && $entry = zip_read($zip))
		if(strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION)) == 'vlp')
			$version = '1.1.D*';
	zip_close($zip);
	
	// Hvis ingenting ble funnet er det nok en gammel versjon
	if(!$version)
		$version = '1.1.C eller tidligere';
	
	// Return version number
	return $version;
}

function getFiles($filepath, $version){
	if(isset($version) && $version == '1.2')
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
	$zip = zip_open($filepath);
	while($entry = zip_read($zip))
		if(strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION)))
			$files[strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION))] = 'OK';
	zip_close($zip);
	return $files;
}

function countFiles($filepath){
	$zip = zip_open($filepath);
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

function getValidityPeriod($filepath){
	$valid = array();
	$data = false;
	$zip = zip_open($filepath);
	while($entry = zip_read($zip))
		if(strtolower(pathinfo(zip_entry_name($entry), PATHINFO_EXTENSION)) == 'dko')
			$data = zip_entry_read($entry, zip_entry_filesize($entry));
		
	if($data){
		$data = array_filter(explode("\n", $data));
		$startDate = mktime(0,0,0, substr($data[0],2,2), substr($data[0],4,2), substr($data[0],0,2)); // m d y
		
		$data = array_slice($data,1);
		
		$data = array_map(function($a){
			$days = substr($a, 8);
			return strrpos($days, "1");
			
		}, $data);
		asort($data);
		
		$stopDate = strtotime('+'.array_pop($data).' day', $startDate);
		
		$valid = array('from' => date('d.m.Y', $startDate), 'to' => date('d.m.Y', $stopDate), 'fromTime' => $startDate, 'toTime' => $stopDate);
	}
	return $valid;
}
