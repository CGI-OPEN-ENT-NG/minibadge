import {InfiniteScrollService, SnipletBadgeAssignService} from "./services";
import {minibadgeBadgeAssign} from "./sniplets/badge-assign.sniplet";
import {rights} from "./core/constants/rights.const";
import {ChartEventsService} from "./services/chart.events.service";

export const MINIBADGE_APP = "minibadge";

export const minibadgeBehaviours = {
    rights,
    sniplets: {
        'badge-assign': minibadgeBadgeAssign,
    },
    chartEventsService: new ChartEventsService,
    infiniteScrollService: new InfiniteScrollService,
    snipletBadgeAssignService: new SnipletBadgeAssignService,
};