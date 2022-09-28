import {Behaviours, ng, notify} from 'entcore';

import {IBadgesGivenService, IBadgeTypeService} from "../services";
import {BadgeType} from "../models/badge-type.model";
import {safeApply} from "../utils/safe-apply.utils";
import {AxiosError} from "axios";
import {MINIBADGE_APP} from "../minibadgeBehaviours";
import {ILocationService, IScope} from "angular";
import {Setting} from "../models/setting.model";
import {Subscription} from "rxjs";
import {BadgeAssigned} from "../models/badge-assigned.model";


interface ViewModel {
}

interface IMinibadgeScope extends IScope {
    vm: ViewModel;
    setting: Setting;
}

class Controller implements ng.IController, ViewModel {

    subscriptions: Subscription = new Subscription();
    badgeGiven : BadgeAssigned[] = [];
    constructor(private $scope: IMinibadgeScope,
                private $location: ILocationService,
                private iBadgesGivenService:IBadgesGivenService) {
        this.$scope.vm = this;
    }

    $onInit() {
        this.iBadgesGivenService.getBadgeGiven().then(
            (data: BadgeAssigned[]) => {
                if (data && data.length > 0) {
                    this.badgeGiven.push(...data);
                }
                safeApply(this.$scope);
            }
        );
    }



    $onDestroy() {
    }
}

export const badgesGivenController = ng.controller('BadgesGivenController',
    ['$scope', '$location', 'BadgesGivenService', Controller]);