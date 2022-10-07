import {Behaviours, moment, ng,idiom as lang, notify} from 'entcore';

import {IBadgesGivenService, IBadgeTypeService} from "../services";
import {BadgeType} from "../models/badge-type.model";
import {safeApply} from "../utils/safe-apply.utils";
import {AxiosError} from "axios";
import {MINIBADGE_APP} from "../minibadgeBehaviours";
import {ILocationService, IScope} from "angular";
import {Setting} from "../models/setting.model";
import {Subscription} from "rxjs";
import {BadgeAssigned} from "../models/badge-assigned.model";
import {DATE_FORMAT} from "../core/enum/date.enum";


interface ViewModel {
}


interface IMinibadgeScope extends IScope {
    vm: ViewModel;
    setting: Setting;
}

class Controller implements ng.IController, ViewModel {

    subscriptions: Subscription = new Subscription();
    badgesGiven: BadgeAssigned[];
    payload = {
        query: "",
        startDate: "",
        endDate: "",
        sortType: "",
        sortAsc: true,
    };
    startDate: Date;
    endDate: Date;
    labelTo: string
    labelFrom: string
    searchQuery: string;

    constructor(private $scope: IMinibadgeScope,
                private $location: ILocationService,
                private BadgesGivenService: IBadgesGivenService) {
        this.endDate = new Date();
        this.startDate = moment().subtract('months', 1).toDate();
        this.labelTo = "minibadge.periode.date.to";
        this.labelFrom = "minibadge.periode.date.from";
        this.$scope.vm = this;
    }

    filterBadges = async (sortLabel: string, isAsc: boolean) => {
        //need to wait directives changes
        await safeApply(this.$scope);
        this.payload.sortType = lang.translate(sortLabel);
        this.payload.sortAsc = isAsc;
        this.initBadgeGiven();
    }

    $onInit() {
        this.initBadgeGiven();
    }


    private async initBadgeGiven() {
        //need to wait directives changes
        await safeApply(this.$scope)
        this.badgesGiven = [];
        this.payload.query = this.searchQuery;
        if (this.startDate && this.endDate) {
            this.payload.startDate = moment(this.startDate).format(DATE_FORMAT.DAY_MONTH_YEAR_MOMENT);
            this.payload.endDate = moment(this.endDate).format(DATE_FORMAT.DAY_MONTH_YEAR_MOMENT);
        }
        await this.BadgesGivenService.getBadgeGiven(this.payload).then(
            (data: BadgeAssigned[]) => {
                if (data && data.length > 0) {
                    this.badgesGiven.push(...data);
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