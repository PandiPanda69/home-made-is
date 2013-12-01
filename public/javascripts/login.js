
var Loading = {};

Loading.toggleLoading = function(show) {
	
	if(show) {
		$('#form').addClass('hide');
		$('#loading').removeClass('hide');
	}
	else {
		$('#loading').addClass('hide');
		$('#form').removeClass('hide');
	}
}

Loading.sendAuthReq = function() {

	Loading.toggleLoading(true);

	var username = $('#username').val();
	var password = $('#password').val();

	$.ajax({
		url: '/login',
		type: 'POST',
		async: false,
		dataType: 'json',
		contentType: 'application/json; charset=utf-8',
		data: JSON.stringify({username: username , password: password}),
		success: function(data) {
			window.location.reload();
		},
		error: function(xhr, code, err) {
			Loading.toggleLoading(false);

			$('#error_div').removeClass('hide');
			$('#error_msg').html('Accès refusé.');
		}			
	});
}


$(document).ready(function() {

	$('#authenticate').click(function() {
		Loading.sendAuthReq();
	});

	$(document).keypress(function(e) {
		if(e.which == 13) {
			Loading.sendAuthReq();
		}
	});
});
