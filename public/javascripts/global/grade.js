var jQueryGrade = {
	$star:   $('<img>').attr('src', '/assets/images/star.png'),
	$starHi: $('<img>').attr('src', '/assets/images/star-hi.png')
};

/**
 * Grade function for jQuery
 * Setup a grade system made of stars (home made).
 * @param defaultValue Number of activated stars
 * @param activationHandler Handler called when a star has been clicked
 * @param data Data to be passed to the handler
 */
jQuery.fn.grade = function(defaultValue, activationHandler, data) {

	var $el = $(this[0]);

	var MAX_STAR = 3;

	// Set default activated stars
	var highlightStarCount = defaultValue || 0;
	if(highlightStarCount > MAX_STAR) {
		highlightStarCount = MAX_STAR;
	}

	var previousContext = null;

	// Define setup function, appending star to the container
	// and registering click handler.
	var setupStar = function($img, attachedGrade) {

                var starContext = {
                        $currentStar: $img.clone(),
			$nextStar: null,
			$prevStar: null,
                        index: attachedGrade,
			userHandler: activationHandler,			

			/**
			 * Handler triggered when user clicks on a star
			 */
                        clickHandler: function() {

				// Toggle stars then fired user handler
				this.toggleStars(true, true);
                                if(typeof this.userHandler === 'function') {
                                        this.userHandler(this.index, data);
                                }
                        },
			/**
			 * Reset stars according to user click
			 * @param activate Does the star must be highlighted or not?
			 * @param initialCall Is it the initial call?
			 */
			toggleStars: function(activate, initialCall) {
				
				if(activate) {
					// Reset next stars if current was already highlighted and this is the first call
					// Considering + as the clicked star, current state is * + * ; Target is * * o
					if(this.$currentStar.attr('src') === jQueryGrade.$starHi.attr('src') && initialCall) {
						this._toggleNextStars(false);
					}
					// Current state is * o + ; Target is * * *
					else {
	                                        this.$currentStar.attr('src', jQueryGrade.$starHi.attr('src'));
						this._togglePrevStars(true);
					}
                                }
                                else {
					// Do not highlight current star, so "downlight" next ones.
					this.$currentStar.attr('src', jQueryGrade.$star.attr('src'));
					this._toggleNextStars(false);
                                }
			},
			/**
			 * Reset previous stars
			 * @param activate Does the star must be highlighted or not?
			 */
			_togglePrevStars: function(activate) {
				if(this.$prevStar === null) {
					return;
				}

				this.$prevStar.toggleStars(activate);
			},
			/**
			 * Reset next stars
			 * @param activate Does the star must be highlighted or not?
			 */
			_toggleNextStars: function(activate) {
				if(this.$nextStar === null) {
					return;
				}

				this.$nextStar.toggleStars(activate);
			}
                };

                starContext.$currentStar.click($.proxy(starContext.clickHandler, starContext));

		// Chain stars
		if(previousContext != null) {
			starContext.$prevStar = previousContext;
			starContext.$prevStar.$nextStar = starContext;
		}

		previousContext = starContext;
		return starContext.$currentStar;
	};

	// Setup stars
	var stars = new Array();
	var i = 0;
	for(; i < highlightStarCount; i++) {
		var $theStar = setupStar(jQueryGrade.$starHi, i+1);
		stars.push($theStar);
	}

	for(; i < MAX_STAR; i++) {
		var $theStar = setupStar(jQueryGrade.$star, i+1);
		stars.push($theStar);
	}

	$el.append(stars);	
};
