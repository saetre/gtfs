<?php 
header('Content-Type: text/html; charset=utf-8');
?>

<h2>Last opp nytt datasett</h2>
<?php #print `whoami`?>
<ol class="instructions">
	<li>Et datasett gjelder for én periode (fra dato A, til dato B).</li>
	<li>Hvert datasett er én ZIP-fil med filer i <a href="http://labs.trafikanten.no/2011/4/13/dokumentasjon-av-regtopp-formatet.aspx">REGTOPP-format</a>, <br />
	 og hver opplastet ZIP-fil skal bare innholdet ett datasett.</li>
	<li>Obligatoriske filer: TIX, TDA (TMS for v1.2), HPL, DKO (og FRM for v1.2).</li>
	<li>Innholdet i datasettet gjennomgår kun en enkel validering ved opplasting.</li>
	<li>Datasettet blir tilgjengelig for bruk umiddelbart etter godkjenning av opplasting.</li>
</ol>

<input type="file" name="file_upload" id="file_upload" />

<ul id="filelist"></ul>

<!-- Template for file upload list -->
<script id="uploadedFile" type="text/html" charset=utf-8>
	<li class="{{>status}}" data-filename="{{>filename}}">
		<p class="head">{{>filename}}</p>
			<ul>
				<li><label>Adm.kode og versjon</label> {{>adm}} ({{>version}})</li>
				<li><label>Gyldig fra og med</label> {{>valid.from}}</li>
				<li><label>Gyldig til og med</label> {{>valid.to}}</li>
				<li><label>Obligatoriske filer</label> {{for files}} <span class="ext {{>status}}" title="{{>status}}">{{>ext}}</span> <span class="dot">&sdot;</span> {{/for}}</li>
			</ul>
		{{if status=='OK'}}
			<a href="#" class="approve">Godkjenn</a>
			Legg ved en kommentar:
			<input type="text" id="inputComment" name="comment"/>
			<p class="overwriteWarning">
				En fil med samme adm.kode og gyldighetsperiode finnes fra før.
				<br />
				<a href="#" class="overwrite">Overskriv</a>
			</p>
		{{else}}
			<p class="error">{{>status}}</p>
		{{/if}}
	</li>
</script>
