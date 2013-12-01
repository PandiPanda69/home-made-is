/*******************************
 * Adding a new function to jquery to serialize a form as a JSON object.
 * Author : Sébastien
 * Date : 10/08/2013
 *******************************/
jQuery.fn.serializeObject = function() {
	
	var obj = {};
	
	$.each(this.serializeArray(), function(index, value) {
		obj[value.name] = value.value;
	});
	
	// Get checkboxes to assign them boolean values
	var checkboxes = $(this).find('input[type="checkbox"]');
	
	$.each(checkboxes, function(index, value) {
		var el = $(value);
		obj[el.attr('name')] = el.prop('checked');
	});
	
	return obj;
};