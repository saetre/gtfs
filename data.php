<?php

	require_once('zipper.php');
?>

<h2>Tilgjengelige datasett</h2>

<p><a href="json.php">Liste over datasett som JSON(P)</a></p>

<table>
	<tr>
		<th>ID</th>
		<th>Gyldighetsperiode</th>
		<th>Oppdatert</th>
		<th>Versjon</th>
		<th>Filer</th>
		<th></th>
	</tr>

	<?php
		$datafolder = 'data';
		$dir = opendir($datafolder);
		$files = array();

		while($file = readdir($dir)){
			if(!is_dir($file) && pathinfo($file, PATHINFO_EXTENSION) == 'zip')
				$files[] = $file;
		}
		arsort($files);
		date_default_timezone_set('Europe/Oslo');

		foreach($files as $file){
				$path = $datafolder.'/'.$file;
				$tool = new zipTools($path);
				$valid = $tool->getValidityPeriod();
				$ffiles = $tool->getFiles();
				ksort($ffiles);

				echo '<tr '.($valid['toTime'] < time() ? 'class="expired"' : '').'>';
				echo '<td>'.$tool->getAdmCodeAndSerial().'</td>';
					echo '<td>'.date('d.m.Y', $valid['fromTime']).'-'.date('d.m.Y', $valid['toTime']).'</td>';
				echo '<td>'.date('d.m.Y H:m', filemtime($path)).'</td>';
				echo '<td>'.$tool->getVersion().'</td>';
				echo '<td>';
				foreach($ffiles as $ext => $f)
					echo $ext.' ';
				echo '</td>';
				echo '<td><a href="'.$path.'">Last ned</a></td>';

				echo '</tr>';
		}

	?>

</table>