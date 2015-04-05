(function($) {

   var _original_show = $.fn.show;
   var _original_hide = $.fn.hide;


   $.fn.show = function() {
      var args = Array.prototype.slice.call(arguments);

      _original_show.apply(this, args);
      this.trigger('shown');
      return this;
   };

   $.fn.hide = function() {
      var args = Array.prototype.slice.call(arguments);

      _original_hide.apply(this, args);
      this.trigger('hidden');
      return this;
   };

})(jQuery);
