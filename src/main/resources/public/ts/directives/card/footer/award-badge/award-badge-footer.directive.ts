import {ng} from "entcore";
import {IDirective, IScope, isFunction} from "angular";
import {RootsConst} from "../../../../core/constants/roots.const";
import {BadgeType} from "../../../../models/badge-type.model";

interface IViewModel {
    click(): void;
}

interface IDirectiveProperties {
    onClick?(badgeType: BadgeType): void;

    badgeType: BadgeType;
}

interface IMinibadgeScope extends IScope {
    vm: IDirectiveProperties;
}

class Controller implements ng.IController, IViewModel {
    constructor(private $scope: IMinibadgeScope) {
    }

    $onInit() {
    }

    click() {
        if (isFunction(this.$scope.vm.onClick)) this.$scope.vm.onClick(this.$scope.vm.badgeType)
    }

    $onDestroy() {
    }

}


function directive(): IDirective {
    return {
        replace: true,
        restrict: 'E',
        templateUrl: `${RootsConst.directive}/card/footer/award-badge/award-badge-footer.html`,
        scope: {
            badgeType: '=',
            onClick: '&?',
        },
        controllerAs: 'vm',
        bindToController: true,
        controller: ['$scope', Controller],
        /* interaction DOM/element */
        link: function (scope: ng.IScope,
                        element: ng.IAugmentedJQuery,
                        attrs: ng.IAttributes,
                        vm: ng.IController) {
        }
    }
}

export const awardBadgeFooter = ng.directive('awardBadgeFooter', directive);