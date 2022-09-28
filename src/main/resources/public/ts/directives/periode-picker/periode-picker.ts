import {ng} from "entcore";
import {IDirective, ILocationService, IScope, IWindowService} from "angular";
import {RootsConst} from "../../core/constants/roots.const";
import {BadgeType} from "../../models/badge-type.model";

interface IViewModel {
}

interface IMinibadgeScope extends IScope {
    vm: IViewModel;
}

class Controller implements ng.IController, IViewModel {
    constructor(private $scope: IMinibadgeScope,
                private $location: ILocationService,
                private $window: IWindowService) {
    }

    $onInit() {
    }

    $onDestroy() {
    }

}

function directive(): IDirective {
    return {
        replace: true,
        restrict: 'E',
        templateUrl: `${RootsConst.directive}/periode-picker/periode-picker.html`,
        scope: {
        },
        controllerAs: 'vm',
        bindToController: true,
        controller: ['$scope', '$location', '$window', Controller],
        /* interaction DOM/element */
        link: function (scope: ng.IScope,
                        element: ng.IAugmentedJQuery,
                        attrs: ng.IAttributes,
                        vm: ng.IController) {
        }
    }
}

export const minibadgePeriodePicker = ng.directive('minibadgePeriodePicker', directive);