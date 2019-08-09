var _button;

$(document).ready( function(){
	ready();
});

function ready(){
	
	$('#filelist').delegate('a.approve', 'click', function(e){
		e.preventDefault();
		approveUpload($(this));
	});
		

	$('#file_upload').uploadify({
		swf      			: 'uploadify/uploadify.swf',
		uploader 			: 'uploadify/uploadify.php',
		width				: 250,
		height 				: 50,
		auto				: true,
		buttonText			: 'Bla gjennom og last opp...',
		fileTypeDesc		: 'ZIP-filer',
		fileTypeExts		: '*.zip',
		removeCompleted		: true,
		removeTimeout		: 0,
		onDialogOpen		: function(){
			$('#file_upload').uploadify('cancel', '*');
		},
		onDialogClose		: function(){
			$('#filelist').empty();
		},
		onUploadSuccess 	: function(file, data, response) {
			validateUpload(file);
		}
	});
}

function validateUpload(file){
	var d = new Date();

	$.ajax({
		url: "./checkzip.php?"+d.getTime(),
		data: {'f': file.name},
		dataType: "json",
		success: function( data ){
			$('#filelist').append( $('#uploadedFile').render( data ) );
		},
		error: function(data){
		}
	});
}

function approveUpload(button, overwrite){
	_button = button;
	overwrite = overwrite || 0;
	var file = button.parent().attr('data-filename');
	//console.log('Approving file: '+file+'. Overwrite: '+overwrite);
	button.unbind().click(function(e){e.preventDefault();}).removeClass().addClass('clicked').html('Vent...');;
	$.ajax({
		url: './approve.php',
		data: {'f': file, 'o': overwrite},
		dataType: 'json',
		type: 'get',
		success: function(data){
			//console.log(data);
			if(data.status == 'SUCCESS'){
				var file = data.file;
				convertToProlog(file);
				button.removeClass().addClass('converting').html('Konverterer filer..');
			} else if(data.status == 'FILE-EXISTS'){
				button.parent().find('.overwriteWarning').show().find('a.overwrite').click(function(e){
					e.preventDefault();
					$(this).parent().remove();
					approveUpload(button, 1);
				});
			} else {
				button.addClass('error').html(data.status);
			}
		},
		error: function(data){
			//console.error(data);
		}
	});
}

function convertToProlog( file ){
	var comment = document.getElementById( "inputComment" ).value;
	$.ajax( {
		url: './converter.php',
		//data: {'f': file, 'comment': encodeURI( comment ) },
		data: {'f': file, 'comment': comment },
		datatype: 'json',
		type: 'get',
		contentType: "application/x-www-form-urlencoded; charset=utf-8",
		success: function(data){
			// endre tekst på knapp til godkjent
			if(data.convert == 'OK' && _button != null){
				_button.removeClass().addClass('approved').html('Godkjent');
			}else{
				_button.removeClass().addClass('error').html( data.convert );
			}
		},
		error: function( data ){
			//console.error(data);
		}
	} );
}//convertToProlog


