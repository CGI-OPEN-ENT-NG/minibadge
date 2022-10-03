import {ng} from "entcore";
import {IDirective, ILocationService, IScope, IWindowService} from "angular";
import {RootsConst} from "../../core/constants/roots.const";
import {DATE_FORMAT} from "../../core/enum/date.enum";


interface IViewModel {
    DATE_FORMAT: typeof DATE_FORMAT;
}

interface IMinibadgeScope extends IScope {
    vm: IViewModel;
}

class Controller implements ng.IController, IViewModel {
    DATE_FORMAT: typeof DATE_FORMAT;
    constructor(private $scope: IMinibadgeScope,
                private $location: ILocationService,
                private $window: IWindowService) {
        this.DATE_FORMAT = DATE_FORMAT;
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
        templateUrl: `${RootsConst.directive}/badge-given-array/badge-given-array.html`,
        scope: {
            badgesGiven : '='
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

export const minibadgeBadgeGivenArray = ng.directive('minibadgeBadgeGivenArray', directive);