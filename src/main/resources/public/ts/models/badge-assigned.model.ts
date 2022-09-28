import {IUserResponse, User} from "./user.model";
import {IBadgeTypeResponse} from "./badge-type.model";
import {MinibadgeModel} from "./model";

export interface IBadgeAssignedPayload {
    ownerIds: string[];
}

export interface IBadgeAssignedResponse {
    id : number;
    assignorId :string;
    badgeId : number;
    acceptedAt ?: string;
    updatedAt?: string;
    createdAt: string;
}

export class BadgeAssigned  extends MinibadgeModel<BadgeAssigned> {
    id : number;
    assignorId :string;
    badgeId : number;
    acceptedAt ?: string;
    updatedAt ?: string;
    createdAt: string;
    constructor(data?: IBadgeAssignedResponse) {
        super();
        if (data) this.build(data);
    }

    build(data: IBadgeAssignedResponse): BadgeAssigned {
        this.id = data.id;
        this.assignorId = data.assignorId;
        this.badgeId = data.badgeId
        this.acceptedAt = data.acceptedAt;
        this.updatedAt = data.updatedAt;
        this.createdAt = data.createdAt;
        return this;
    }

    toModel(model: any): BadgeAssigned {
        return new BadgeAssigned(model)
    }
}