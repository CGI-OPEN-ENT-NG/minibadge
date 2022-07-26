import {Behaviours, ng, notify} from 'entcore';

import {IBadgeTypeService} from "../services";
import {BadgeType, IBadgeTypesPayload} from "../models/badge-type.model";
import {safeApply} from "../utils/safe-apply.utils";
import {AxiosError} from "axios";
import {MINIBADGE_APP} from "../minibadgeBehaviours";
import {ILocationService, IScope} from "angular";
import {Setting} from "../models/setting.model";
import {CARD_FOOTER} from "../core/enum/card-footers.enum";


interface ViewModel {
    getBadgeTypes(): Promise<void>;

    initBadgeTypes(): Promise<void>;

    onScroll(): Promise<void>;

    redirectBadgeType(badgeType: BadgeType): void;

    onOpenLightbox(badgeType: BadgeType): void;

    CARD_FOOTER: typeof CARD_FOOTER;
    badgeTypes: BadgeType[];
    searchQuery: string;
}

interface IMinibadgeScope extends IScope {
    vm: ViewModel;
    setting: Setting;
}

class Controller implements ng.IController, ViewModel {
    private payload: IBadgeTypesPayload;

    CARD_FOOTER: typeof CARD_FOOTER;
    badgeTypes: BadgeType[];
    searchQuery: string;
    isMinibadgeAccepted: boolean;

    constructor(private $scope: IMinibadgeScope,
                private $location: ILocationService,
                private badgeTypeService: IBadgeTypeService) {
        this.$scope.vm = this;
        this.CARD_FOOTER = CARD_FOOTER;
        this.payload = {
            offset: 0,
        };
        this.isMinibadgeAccepted = !!this.$scope.setting.userPermissions.acceptAssign
            || !!this.$scope.setting.userPermissions.acceptReceive;
    }

    $onInit() {
        this.initBadgeTypes();
    }

    resetBadgeTypes = (): void => {
        this.payload.offset = 0;
        this.badgeTypes = [];
    }

    getBadgeTypes = async (): Promise<void> => {
        this.badgeTypeService.getBadgeTypes(this.payload)
            .then((data: BadgeType[]) => {
                if (data && data.length > 0) {
                    this.badgeTypes.push(...data);
                    Behaviours.applicationsBehaviours[MINIBADGE_APP].infiniteScrollService
                        .updateScroll();
                }
                safeApply(this.$scope);
            })
            .catch((err: AxiosError) => notify.error('minibadge.error.get.badge.types'))
    }

    onScroll = async (): Promise<void> => {
        this.payload.offset += this.$scope.setting.pageSize;
        await this.getBadgeTypes();
    };

    initBadgeTypes = async (): Promise<void> => {
        this.payload.query = this.searchQuery;
        this.resetBadgeTypes();
        await this.getBadgeTypes();
    }

    redirectBadgeType = (badgeType: BadgeType): void => {
       this.$location.path(badgeType.getDetailPath());
    }

    onOpenLightbox = (badgeType: BadgeType): void => {
        Behaviours.applicationsBehaviours[MINIBADGE_APP].snipletBadgeAssignService
            .sendBadgeType(badgeType);
    }


    $onDestroy() {
    }
}

export const badgeTypesController = ng.controller('BadgeTypesController',
    ['$scope', '$location', 'BadgeTypeService', Controller]);