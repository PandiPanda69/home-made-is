App.Views.CalendarPopup = App.Popup.extend({

	template: _.template($('#calendar-popup-template').html()),
	_currentDate: {
		day: null,
		month: null,
		year: null
	},
	_weight: null,

	_init: function($dialog) {
		this.$el = $dialog;
	},
	_preRender: function() {

		this.$el.html(this.template({
			date: this._currentDate,
			weight: this._weight
		}));
	},
	events: {
		'click #cancel-btn': 'dispose',
		'click #ok-btn': '_validate'
	},
	setDate: function(year, month, day) {
		this._currentDate = {
			day: day,
			month: month,
			year: year
		};
	},
	setWeight: function(weight) {
		this._weight = weight;
	},
	onClose: null,
	_validate: function() {

		var weight = this.$el.find('#weight').val();
		var date = new Date(this._currentDate.year, this._currentDate.month, this._currentDate.day);

		this.onClose(date, weight);
		this.dispose();
	}
});
