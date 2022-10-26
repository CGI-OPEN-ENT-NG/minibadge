import {ILimitOffsetPayload, IPaginatedResponses} from "./request.model";
import {IUserResponse, User} from "./user.model";
import {MinibadgeModel} from "./model";
import {BadgeSettings} from "./badge-settings.model";

export interface IBadgeTypeResponse {
    id?: number;
    structureId?: string;
    ownerId?: string;
    pictureId?: string;
    label: string;
    description: string;
    createdAt?: string;
    assignableBy?: string[];
    level?: string;
    owner?: User;
    settings?: BadgeSettings[];
}

export interface IBadgeTypesPayload extends ILimitOffsetPayload {
}

export interface IBadgeTypesResponses extends IPaginatedResponses<IBadgeTypeResponse> {
}


export class BadgeType extends MinibadgeModel<BadgeType> {
    id?: number;
    structureId?: string;
    ownerId?: string;
    pictureId?: string;
    label: string;
    description: string;
    createdAt?: string;
    owner?: User;
    settings?: BadgeSettings[];

    constructor(data?: IBadgeTypeResponse) {
        super();
        if (data) this.build(data);
    }

    build(data: IBadgeTypeResponse): BadgeType {
        this.id = data.id;
        this.structureId = data.structureId;
        this.ownerId = data.ownerId
        this.pictureId = data.pictureId;
        this.label = data.label;
        this.description = data.description;
        this.createdAt = data.createdAt;
        this.owner = new User(<IUserResponse>data.owner);
        this.settings = data.settings;
        return this;
    }

    toModel(model: any): BadgeType {
        return new BadgeType(model)
    };

    displayAssignors = (): string => {
        let assigners = [];

        this.settings.forEach(setting => {
            setting.relations.forEach(relation => {
                assigners.push(relation.assignorType.type + " ");
            })
        })
        return assigners.toString()
    }
    displayReceivers = (): string => {
        let receivers = [];

        this.settings.forEach(setting => {
            setting.relations.forEach(relation => {
                receivers.push(relation.receiverType.type + " ");
            })
        })
        return receivers.toString()
    }
    getDetailPath = (): string => `/badge-types/${this.id}`;

}