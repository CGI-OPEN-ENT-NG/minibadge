import {Behaviours, ng, notify} from 'entcore';

import {IBadgeTypeService} from "../services";
import {BadgeType} from "../models/badge-type.model";
import {safeApply} from "../utils/safe-apply.utils";
import {AxiosError} from "axios";
import {MINIBADGE_APP} from "../minibadgeBehaviours";
import {IScope} from "angular";
import {Setting} from "../models/setting.model";
import {Subscription} from "rxjs";


interface ViewModel {
}

interface IMinibadgeScope extends IScope {
    vm: ViewModel;
    setting: Setting;
}

class Controller implements ng.IController, ViewModel {

    subscriptions: Subscription = new Subscription();

    constructor(private $scope: IMinibadgeScope) {
        this.$scope.vm = this;
    }

    $onInit() {
    }



    $onDestroy() {
    }
}

export const badgesGivenController = ng.controller('BadgesGivenController',
    ['$scope', '$route', 'BadgesGivenService', Controller]);