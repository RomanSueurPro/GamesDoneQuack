import { PermissionWithoutRoles } from "./PermissionWithoutRoles";

export interface RoleAllFields{
    id: number;
    name: string;
    permissions: PermissionWithoutRoles[];
}