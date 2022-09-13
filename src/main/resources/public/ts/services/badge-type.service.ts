import {ng} from 'entcore';
import http, {AxiosPromise, AxiosResponse} from 'axios';
import {BadgeType, IBadgeTypeResponse, IBadgeTypesPayload, IBadgeTypesResponses} from "../models/badge-type.model";

export interface IBadgeTypeService {
    getBadgeTypes(params: IBadgeTypesPayload): Promise<BadgeType[]>;

    getBadgeType(typeId: number): Promise<BadgeType>;

    privatizeBadgeType(typeId: number): Promise<AxiosPromise>;

    refuseBadgeType(typeId: number): Promise<AxiosPromise>;
}

export const badgeTypeService: IBadgeTypeService = {
    /**
     * Get list of general / structure based badge types
     *
     * @param payload params to send to the backend
     */
    getBadgeTypes: async (payload: IBadgeTypesPayload): Promise<BadgeType[]> =>
        http.get(`/minibadge/types?offset=${payload.offset}${payload.query ? `&query=${payload.query}` : ''}`)
            .then((res: AxiosResponse) => {
                let badgeTypesResponses: IBadgeTypesResponses = res.data;
                return new BadgeType().toList(badgeTypesResponses ? badgeTypesResponses.all : []);
            }),

    /**
     * Get badge type
     *
     * @param typeId badge type identifier
     */
    getBadgeType: async (typeId: number): Promise<BadgeType> =>
        http.get(`/minibadge/types/${typeId}`)
            .then((res: AxiosResponse) => new BadgeType(<IBadgeTypeResponse>res.data)),

    /**
     * privatize badge type for current user session
     *
     * @param typeId badge type identifier
     */
    privatizeBadgeType: async (typeId: number): Promise<AxiosPromise> =>
        http.put(`/minibadge/types/${typeId}/privatize`),

    /**
     * refuse badge type for current user session
     *
     * @param typeId badge type identifier
     */
    refuseBadgeType: async (typeId: number): Promise<AxiosPromise> =>
        http.put(`/minibadge/types/${typeId}/refuse`)
};

export const BadgeTypeService = ng.service('BadgeTypeService', (): IBadgeTypeService => badgeTypeService);