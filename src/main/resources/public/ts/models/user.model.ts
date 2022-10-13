import {MinibadgeModel} from "./model";
import {IPaginatedResponses, IQueryStringPayload} from "./request.model";

export interface IUserResponse {
    id: string;
    firstName: string;
    lastName: string;
}

export interface IUsersResponses extends IPaginatedResponses<IUserResponse>{}

export interface IUserPayload extends IQueryStringPayload{}

export class User extends MinibadgeModel<User> {
    id: string;
    firstName: string;
    lastName: string;

    constructor(data?: IUserResponse) {
        super();
        if (data) this.build(data);
    }

    build(data: IUserResponse): User {
        this.id = data.id;
        this.firstName = data.firstName;
        this.lastName = data.lastName;
        return this;
    }

    toModel(model: any): User {
        return new User(model)
    };

    displayName = (): string => `${this.firstName} ${this.lastName}`

}