import {ng} from 'entcore';
import http, {AxiosResponse} from 'axios';
import {BadgeAssigned, IBadgeGivenPayload} from "../models/badge-assigned.model";
import {IBadgeTypesResponses} from "../models/badge-type.model";

export interface IBadgesGivenService {
    getBadgeGiven(payload:IBadgeGivenPayload): Promise<BadgeAssigned[]>;
}

function getParams(payload: IBadgeGivenPayload) {
    let params ="";
    if(payload.query){
        params += `?query=${payload.query}`
        if(payload.startDate && payload.endDate){
            params += `&startDate=${payload.startDate}&endDate=${payload.endDate}`
        }
        if(payload.sortType && payload.sortType !== "" && payload.sortAsc!== undefined){
            params += `&sortBy=${payload.sortType}&sortAsc=${payload.sortAsc}`
        }
    }else{
        if(payload.startDate && payload.endDate){
            params += `?startDate=${payload.startDate}&endDate=${payload.endDate}`
            if(payload.sortType && payload.sortType !== "" && payload.sortAsc!== undefined){
                params += `&sortBy=${payload.sortType}&sortAsc=${payload.sortAsc}`
            }
        }else{
            if(payload.sortType && payload.sortType !== "" && payload.sortAsc!== undefined){
                params += `?sortBy=${payload.sortType}&sortAsc=${payload.sortAsc}`
            }
        }
    }
    return params;
}

export const badgesGivenService: IBadgesGivenService = {

    /**
     * Get badge type
     *
     * @param typeId badge type identifier
     */
    getBadgeGiven: async (payload:IBadgeGivenPayload): Promise<BadgeAssigned[]> =>
        http.get(`/minibadge/assigned/given${getParams(payload)}`)
            .then((res: AxiosResponse) => {
                let badgeTypesResponses: IBadgeTypesResponses = res.data;
                return new BadgeAssigned().toList(badgeTypesResponses ? badgeTypesResponses.all : []);
            })
};

export const BadgesGivenService = ng.service('BadgesGivenService', (): IBadgesGivenService => badgesGivenService);