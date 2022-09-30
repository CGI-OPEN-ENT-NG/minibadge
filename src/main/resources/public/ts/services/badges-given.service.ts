import {ng} from 'entcore';
import http, {AxiosResponse} from 'axios';
import {BadgeAssigned, IBadgeGivenPayload} from "../models/badge-assigned.model";
import {IBadgeTypesResponses} from "../models/badge-type.model";

export interface IBadgesGivenService {
    getBadgeGiven(payload:IBadgeGivenPayload): Promise<BadgeAssigned[]>;
}

export const badgesGivenService: IBadgesGivenService = {

    /**
     * Get badge type
     *
     * @param typeId badge type identifier
     */
    getBadgeGiven: async (payload:IBadgeGivenPayload): Promise<BadgeAssigned[]> =>
        http.get(`/minibadge/assigned/given${payload.query ? `?query=${payload.query}` : ''}`)
            .then((res: AxiosResponse) => {
                let badgeTypesResponses: IBadgeTypesResponses = res.data;
                return new BadgeAssigned().toList(badgeTypesResponses ? badgeTypesResponses.all : []);
            })
};

export const BadgesGivenService = ng.service('BadgesGivenService', (): IBadgesGivenService => badgesGivenService);