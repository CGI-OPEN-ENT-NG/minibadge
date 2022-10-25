import {MinibadgeModel} from "./model";
import {BadgeProtagonistSettingRelation} from "./badge-protagonist-setting.model";

export interface IBadgeSettingResponse {
    is_self_assignable: boolean;
    structureId: string;
    relations: BadgeProtagonistSettingRelation[];
}


export class BadgeSettings extends MinibadgeModel<BadgeSettings> {
    is_self_assignable: boolean;
    structureId: string;
    relations: BadgeProtagonistSettingRelation[];

    constructor(data?: IBadgeSettingResponse) {
        super();
        if (data) this.build(data);
    }

    build(data: IBadgeSettingResponse): BadgeSettings {
        this.is_self_assignable = data.is_self_assignable;
        this.structureId = data.structureId;
        return this;
    }

    toModel(model: any): BadgeSettings {
        return new BadgeSettings(model)
    };
}