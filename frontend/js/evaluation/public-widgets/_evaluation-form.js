'use strict';

import FormHelper from "./../../common/_form-helper";

export default class Widget {
    /**
     * Filter history
     * @param {String} selector
     */
    constructor(selector) {
        this.$root = $(selector);
        this.locals = this._getDom();
        this.currentStep = 1;
        this.helperStep1 = new FormHelper(this.locals.$controlsStep1);
        this.helperStep2 = new FormHelper(this.locals.$controlsStep2);

        this._setDataFromLocal('step1', this.locals.$controlsStep1);
        this._setDataFromLocal('step2', this.locals.$controlsStep2);
        this._assignEvents();
    }

    _getDom() {
        const $root = this.$root;

        return {
            $controlsStep1: $root.find('.b-evalform__step.type-1 .form-control'),
            $controlsStep2: $root.find('.b-evalform__step.type-2 .form-control'),
            $addressBlock: $root.find('[data-eval-address]'),
            $form: $root.find('[data-eval-form]')
        };
    }

    _assignEvents() {
        this.$root
            .on('click', '[data-eval-next]', this._onClickNextStep.bind(this))
            .on('click', '[data-eval-previous]', this._onClickPreviousStep.bind(this))
            .on('click', '[data-eval-toggle]', this._onClickToggleAddress.bind(this))
            .on('click', '[data-eval-submit]', this._onClickSubmitBtn.bind(this))

        this.locals.$form.on('submit', this._onEventSubmit.bind(this));
    }

    _onClickNextStep(e){
        e && e.preventDefault();

        if (!this.helperStep1.isValidInputs()) return;

        this._saveDataToLocal('step1', this.helperStep1.getFormData());
        this.showStep(2);
    }

    _onClickPreviousStep(e){
        e.preventDefault();

        this._saveDataToLocal('step2', this.helperStep2.getFormData());
        this.showStep(1);
    }

    _onClickToggleAddress(e){
        e.preventDefault();
        
        const $link = $(e.currentTarget);
        const isShowDetail = !$link.hasClass('state_active');

        $link.toggleClass('state_active', isShowDetail);
        this.locals.$addressBlock.slideToggle();
    }

    _onClickSubmitBtn(e){
        e && e.preventDefault();

        if (!this.helperStep2.isValidInputs()) return;
        this._saveDataToLocal('step2', this.helperStep2.getFormData());

        const formData = $.extend({}, this.helperStep1.getFormData(), this.helperStep2.getFormData());
        this._sendEvaluation(formData)
            .done(function(){
                this._resetForm();

                success("Your evaluation has been send successfully")
            })
            .fail(function(response){

            })
    }

    /**
     * Try to submit form on both steps
     * @param {Event} e
     * @private
     */
    _onEventSubmit(e){
        e.preventDefault();

        if (this.currentStep == 1){
            this._onClickNextStep();
        } else {
            this._onClickSubmitBtn();
        }
    }

    /**
     * Show given step
     * @param {Number} number
     */
    showStep(number = 1){
        const $root = this.$root;
        const isShowStep2 = (number == 2) && (!$root.hasClass('b-evalform_state_second'));

        this.currentStep = number;
        $root.toggleClass('b-evalform_state_second', isShowStep2);
    }

    /**
     * Save data into localStorage
     * @param {String} key
     * @param {Object} data
     * @private
     */
    _saveDataToLocal(key, data){
        localStorage.setItem(key, JSON.stringify(data));
    }

    /**
     * Get data from localStorage and values to html controls
     * @param {String} key - unique key for LocalStorage
     * @param {jQuery|null} $controls - list of controls
     * @private
     */
    _setDataFromLocal(key, $controls){
        const item = localStorage.getItem(key);
        if (!item) return false;

        const formData = JSON.parse(item);
        let $control;

        for(let fieldName in formData){
            if (formData.hasOwnProperty(fieldName)){
                $control = $controls.filter(`[name="${fieldName}"]`).first();

                if (!$control.length) continue;
                $control.val(formData[fieldName]);
            }
        }
    }

    _resetForm(){
        this.helperStep1.clearForm();
        this.helperStep1.removeErrors();
        this._saveDataToLocal('step1', null);

        this.helperStep2.clearForm();
        this.helperStep2.removeErrors();
        this._saveDataToLocal('step2', null);
    }


    //transport
    _sendEvaluation(formData){
        const url = this.locals.$form.data('action');
        return $.post(url, formData);
    }

    // static
    static plugin(selector) {
        const $elems = $(selector);
        if (!$elems.length) return;

        return $elems.each(function (index, el) {
            let $element = $(el);
            let data = $element.data('widget');

            if (!data) {
                data = new Widget(el);
                $element.data('widget', data);
            }
        })
    }
}


