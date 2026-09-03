import { RoleWithoutPermissions } from "./RoleWithoutPermissions";

export interface UserNoRelations{
    id: number;
    username: string;
    email: string;
    role: RoleWithoutPermissions;
}