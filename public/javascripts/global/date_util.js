App.Utils.DateUtil = {

	DAY_IN_MS: 1000 * 60 * 60 * 24,

	checkDaysBetweenDates: function(date1, date2, dayCount) {
		var delta = this.getDaysBetweenDates(date1, date2);
		return (dayCount == delta);
	},
	getDaysBetweenDates: function(date1, date2) {
		return Math.abs(date1 - date2) / this.DAY_IN_MS;
	}
}
