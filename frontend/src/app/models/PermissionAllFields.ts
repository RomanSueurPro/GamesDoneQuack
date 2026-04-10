import { RoleWithoutPermissions } from "./RoleWithoutPermissions";

export interface PermissionAllFields{
    id: number;
    name: string;
    roles: RoleWithoutPermissions[];
}