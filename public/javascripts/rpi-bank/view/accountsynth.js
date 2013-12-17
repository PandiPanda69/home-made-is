App.Views.AccountSynth = Backbone.View.extend({
	
	DAY_IN_MS: 1000 * 60 * 60 * 24,

	initialize: function() {
		this.$el      = $('#operations-container');
		this.template = _.template($('#accountsynth-template').html());

		this.accountTypes = new App.Models.AccountType;
	},
	render: function(account) {

		if(account.get('category') != 'SAVING') {
			return;
		}

		$.when(this.accountTypes.fetch())
		.fail(function() {
			alert('FAIIIIIL');
		})
		.done($.proxy(function() {

			var accountId = account.get('id');
			$.get(globals.rootUrl + '/accounts/' + accountId + '/months/currentYear/operations', $.proxy(function(data) {
				var theType = this.accountTypes.get(account.get('typeId'));
				var tableContent = this._prepareData(theType.get('taux'), data, account);

				this.$el.html(this.template({content: tableContent}));
			}, this));
		}, this));
	},
	_prepareData: function(rates, operations, account) {

		if(rates.length == 0) {
			return new Array;
		}

		var result = new Array;
		var currentYear = (new Date).getFullYear()

		var initialBalance = null;
		if(operations.length > 0) {
			initialBalance = 0.00;
			var firstOpMonthId = operations[0].moisAnneeId;
			$.ajax({
				url: globals.rootUrl + '/accounts/' + account.get('id') + '/months/' + firstOpMonthId + '/balance', 
				async: false,
				success: function(data) {
					initialBalance = data.solde;
				}
			});
		}
		else {
			initialBalance = account.get('solde');
		}


		var currentOpIndex = 0;
		for(var i = 1; i < rates.length + 1; i++) {
			// First, get the rates for the current year. If none found, then it means
			// the previous one was the last one and must be applied.
			var currentRate  = rates[i];
			var previousRate = rates[i-1];

			var currentRateDate = currentRate != null ? new Date(currentRate.date) : null;
			if(currentRate == null || currentRateDate.getFullYear() == currentYear) {

				var previousRateDate = new Date(previousRate.date);

				var operationAdded = false;
				for(currentOpIndex; currentOpIndex < operations.length; currentOpIndex++) {
					var currentOp = operations[currentOpIndex];

					// Only work with previous rate which covers the current period
					var currentOpDate = this._formatDate(currentOp.date);
					if(currentRateDate == null || 
						(currentOpDate.getMonth() <  currentRateDate.getMonth()) ||
						(currentOpDate.getMonth() == currentRateDate.getMonth() && currentOpDate.getDate() < currentRateDate.getDate())) {

						var value = this._getValueForDate(currentOpDate);

						initialBalance += currentOp.montant;

						result.push({
							value:  value,
							amount: initialBalance,
							rate:   previousRate.rate
						});

						operationAdded = true;
					}
					else {
						// Break in place so can be resume where paused.
						break;
					}
				}

				if(!operationAdded) {
					var value = previousRateDate;
					if(value.getFullYear() < currentYear) {
						value = new Date(currentYear, 0, 1);
					}

					result.push({
						value:  value,
						amount: initialBalance,
						rate:   previousRate.rate
					});
				}
			}
		}

		// Last step, compute interests over the year
		var previousDate = new Date(currentYear, 0, 1);
		var yearDayCount = (new Date(currentYear + 1, 0, 1) - previousDate) / this.DAY_IN_MS;
		for(var i = 0; i < result.length; i++) {

			var days = null;
			if((i + 1) < result.length) {
				days = (result[i+1].value - previousDate) / this.DAY_IN_MS;
			}
			else {
				days = (new Date(currentYear +1, 0, 1) - result[i].value) / this.DAY_IN_MS;
			}
			
			result[i].interests = (result[i].amount * result[i].rate / 100) * days / yearDayCount;

			previousDate = result[i].value;
		}

		return result;
	},
	_formatDate: function(date) {
		var buf = date.split('/');

		return new Date(buf[2], buf[1] - 1, buf[0]);
	},
	_getValueForDate: function(date) {
		var value = null;
		if(date.getDate() > 1 && date.getDate() <= 15) {
			value = new Date(date.getFullYear(), date.getMonth(), 15);
		}
		else if(date.getDate() == 1) {
			value = new Date(date.getFullYear(), date.getMonth(), 1);
		}
		else {
			value = new Date(date.getFullYear(), date.getMonth() + 1, 1);
		}

		return value;
	}
});
