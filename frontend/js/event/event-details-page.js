'use strict';

import EventBlock from "./../common/_event-block";
import RequestEventDlg from "./widgets/_evaluation-dlg";

$(function(){
    EventBlock.plugin('.js-event-controls');
    RequestEventDlg.plugin('.js-request-evaluation');

    App.events
        .sub('hmt.event.cancel', function(){
            const brandId = $('#brandId').val();
            window.location.replace(jsRoutes.controllers.Events.index(brandId).url);
        })
});