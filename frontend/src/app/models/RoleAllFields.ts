import { PermissionWithoutRoles } from "./PermissionWithoutRoles";

export interface RoleAllFields{
    id: number;
    name: string;
    isAdmin: boolean;
    isDefault: boolean;
    permissions: PermissionWithoutRoles[];

}