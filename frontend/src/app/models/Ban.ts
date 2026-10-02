import { LoggedInUser } from "./LoggedInUser";
import {UserNoRelations} from "./UserNoRelations"

export interface Ban{
    id: number;
    user: UserNoRelations;
    moderator: LoggedInUser;
    startDate: Date;
    endDate: Date;
    reason: String;
}