import { PermissionWithoutRoles } from "./PermissionWithoutRoles";

export interface RoleAllFields{
    id: number;
    name: string;
    adminRole: boolean;
    defaultRole: boolean;
    permissions: PermissionWithoutRoles[];

}