'use strict';


export default class InputChecking {
    /**
     * Validate input through backend url
     */
    constructor(data) {
        this.$root = data.$root;
        this.url = data.url;
        this.locals = this._getDom();

        this._checkValue();
        this._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $content: $root.find('[data-inputcheck-block]'),
            $input: $root.find('input'),
            $error: $root.find('[data-inputcheck-error]'),
            $successText: $root.find('[data-inputcheck-text]')
        };
    }

    _assignEvents() {
        this.locals.$input
            .on('blur', this._checkValue.bind(this))
            .on('focus', this._hideCheckingError.bind(this))
    }

    _checkValue() {
        const self = this;
        const locals = self.locals;
        const valueInput = locals.$input.val();

        if (!locals.$input.val()){
            return;
        }

        this.$root
            .removeClass('b-inputcheck_state_complete b-inputcheck_state_error')
            .addClass('b-inputcheck_state_checking');

        self._sendCheck(valueInput)
            .done(function(response){
                const successText = $.parseJSON(response).message;
                self._completeChecking(successText);
            })
            .fail(function(response){
                const error = $.parseJSON(response.responseText).message;
                self._showCheckingError(error);
            })
    }

    /**
     * Show error when input value is invalid
     * @param {String} error
     * @private
     */
    _showCheckingError(error){
        const locals = this.locals;
        this.valid = false;

        this.$root
            .removeClass('b-inputcheck_state_checking')
            .addClass('b-inputcheck_state_error');

        this.$root.trigger('input_checking.change');
        locals.$error.text(error);
    }

    /**
     * Show success text after checking
     * @param {String} success
     * @private
     */
    _completeChecking(success){
        const locals = this.locals;
        this.valid = true;

        this.$root
            .removeClass('b-inputcheck_state_checking')
            .addClass('b-inputcheck_state_complete');
        
        this.$root.trigger('input_checking.change');
        locals.$successText.text(success);
    }

    _hideCheckingError(){
        this.valid = false;
        this.$root.removeClass('b-inputcheck_state_error');
    }

    isValid() {
        return this.valid;
    }

    //transport
    _sendCheck(value){
        return $.get(this.url(value).url)
    }
}
