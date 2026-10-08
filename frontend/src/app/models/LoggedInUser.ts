import { RoleWithoutPermissions } from "./RoleWithoutPermissions";

export interface LoggedInUser {
    username: string;
    id: number;
    role: RoleWithoutPermissions;
    banned: boolean;
    unbanDate: Date;
}