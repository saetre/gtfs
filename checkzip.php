<?php

require_once('config.php');
require_once('zipper.php');


// Hente navnet på fila vi skal jobbe med
$filename = trim(isset($_GET['f']) ? $_GET['f'] : false);

// Prøve å finne en matchende fil i mappa
$path = $_SERVER['DOCUMENT_ROOT'] . $regtopp_uploads;

// Starter med null resultater og går gjennom alt i mappa
$filepath = false;
$data = array('status' => 'UNKNOWN', 'filename' => $filename);
if($filename){
	$dir = opendir($path);
	while ($file = readdir($dir))
		if($filename == $file)
			$filepath = $path.'/'.$filename;
	closedir($dir);
	
	if(file_exists($filepath)){
		
		$zip = new zipTools($filepath);
//		$zip = zip_open($filepath);
		if($zip->error){
			// Fila finnes, men er ikke gyldig ZIP
			$data['status'] = 'FILE-NOT-ZIP';
		} else if($zip){
			// Fila fantes og kan prosesseres
			
			$data['version'] = $zip->getVersion();
			$data['adm'] = $zip->getAdmCodeAndSerial();
			$data['files'] = $zip->getFiles();
			$data['valid'] = $zip->getValidityPeriod();

			$missing = array();
			foreach($data['files'] as $f => $status){
				if($status == 'MISSING'){
					$missing[] = $f;
				}
			}
			if(!$zip->countFiles())
				$data['status'] = 'TOO-MANY-DATASETS';
			else if( ! (
					sizeof($missing) || 
					$data['version'] == false || 
					$data['adm'] == false || 
					sizeof($data['files']) == 0 ||  
					( isset ($data['valid']) &&	$data['valid'] == false))
				)
				$data['status'] = 'OK';
			else
				$data['status'] = 'CONTENT-ERROR: missing(redline)/invalid files:';
						#.join($missing, " ")."||".sizeof($data['files'])."||".join(array_keys($data['valid']), " ");
		} else {
			$data['status'] = 'UNKNOWN-FILE';
		}
	} else {
		// Fila eksisterer ikke
		$data['status'] = 'FILE-NOT-FOUND';
	}
	
}

// Rearrange file array to make it JSON friendly
$jsonfiles = array();
if(array_key_exists('files',$data)){
	foreach($data['files'] as $ext => $status)
		$jsonfiles[] = array('ext' => $ext, 'status' => $status);
	$data['files'] = $jsonfiles;
}

// Dump ut JSON-svaret
header("Content-Type: application/json; charset=UTF-8");
echo utf8_decode(json_encode($data));
exit;
