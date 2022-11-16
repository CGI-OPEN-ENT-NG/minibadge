import {Behaviours, model, ng, notify, template} from 'entcore';
import {NAVBAR_VIEWS} from "../core/enum/navbar.enum";
import {IChartService, ISettingService} from "../services";
import {Setting} from "../models/setting.model";
import {IScope} from "angular";
import {Chart} from "../models/chart.model";
import {IUserResponse, User} from "../models/user.model";
import {MINIBADGE_APP} from "../minibadgeBehaviours";

interface ViewModel {
    openChartLightbox(): void;

    chartValidate(): Promise<void>;

    resetChartValues(): void;

    navbarViewSelected: NAVBAR_VIEWS;
    isChartLightboxOpened: boolean;
    isChartAccepted: boolean;
    isMinibadgeAccepted: boolean;
}

interface IMinibadgeScope extends IScope {
    vm: ViewModel;
    setting: Setting;
    me: User;
}

/**
 Wrapper controller
 ------------------
 Main controller.
 **/

class Controller implements ng.IController, ViewModel {
    navbarViewSelected: NAVBAR_VIEWS;
    isChartLightboxOpened: boolean;
    isChartAccepted: boolean;
    isMinibadgeAccepted: boolean;

    constructor(private $scope: IMinibadgeScope,
                private $route: any,
                private settingService: ISettingService,
                private chartService: IChartService) {
        this.$scope.vm = this;

        this.$route({
            badgeReceived: async () => {
                await this.initInfos();
                this.navbarViewSelected = NAVBAR_VIEWS.BADGES_RECEIVED;
                template.open('main', `main`);
            },
            badgeTypes: async () => {
                await this.initInfos();
                this.navbarViewSelected = NAVBAR_VIEWS.BADGES_LIBRARY;
                template.open('main', `badge-types`);
            },
            badgeGiven: async () => {
                await this.initInfos();
                this.navbarViewSelected = NAVBAR_VIEWS.BADGES_GIVEN;
                template.open('main', `badges-given`);
            },
            badgeType: async () => {
                await this.initInfos();
                template.open('main', `badge-type`);
            }
        });
    }

    $onInit() {
    }

    openChartLightbox = (): void => {
        this.isChartLightboxOpened = true;
    }

    chartValidate = async (): Promise<void> => {
        this.chartService.saveChart(this.isChartAccepted, this.isMinibadgeAccepted)
            .then(async () => {
                this.$scope.setting.userPermissions = await this.chartService.getChart();
                Behaviours.applicationsBehaviours[MINIBADGE_APP].chartEventsService
                    .validateChart(this.$scope.setting.userPermissions)
                this.resetChartValues();
            })
            .catch(() => notify.error('minibadge.error.chart.validate'));
    }

    resetChartValues = (): void => {
        this.$scope.vm.isChartAccepted = !!this.$scope.setting.userPermissions.acceptChart;
        this.$scope.vm.isMinibadgeAccepted = !!this.$scope.setting.userPermissions.acceptAssign
            || !!this.$scope.setting.userPermissions.acceptReceive;
    }

    private async initInfos() {
        this.$scope.me = new User(<IUserResponse>model.me);
        await Promise.all([this.getSettings(), this.chartService.getUserChart()])
            .then((data: [Setting, Chart]) => {
                let setting: Setting = data[0];
                setting.userPermissions = data[1];
                this.$scope.setting = setting;

                this.isChartLightboxOpened = !this.$scope.setting.userPermissions.acceptChart;
                this.isChartAccepted = !!this.$scope.setting.userPermissions.acceptChart;
                this.isMinibadgeAccepted = !!this.$scope.setting.userPermissions.acceptAssign
                    || !!this.$scope.setting.userPermissions.acceptReceive;
            });
    }

    private async getSettings(): Promise<Setting> {
        return this.settingService.getGlobalSettings()
            .catch(() => new Setting({pageSize: 0}));
    }

    $onDestroy() {
    }
}

export const mainController = ng.controller('MainController',
    ['$scope', 'route', 'SettingService', 'ChartService', Controller]);