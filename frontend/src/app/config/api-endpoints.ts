import { environment } from "../../environments/environment";

const BASE = environment.apiBaseUrl;

const ADMIN_BASE = `admin`;

export const API_ENDPOINTS = {

    admin: {
        fetchAllRoles: `${BASE}/${ADMIN_BASE}/fetchallroles`,
        fetchAllRolesNoPermissionField: `${BASE}/${ADMIN_BASE}/fetchallrolesnopermissionfield`,
        fetchAllPermissions: `${BASE}/${ADMIN_BASE}/fetchallpermissions`,
        fetchAllPermissionsNoRoleField: `${BASE}/${ADMIN_BASE}/fetchallpermissionsnorolefield`,
        updateRole: `${BASE}/${ADMIN_BASE}/updaterole`,
        createRole: `${BASE}/${ADMIN_BASE}/createrole`,
        deleteRole: `${BASE}/${ADMIN_BASE}/deleterole`,
        updatePermission: `${BASE}/${ADMIN_BASE}/updatepermission`,
        createPermission: `${BASE}/${ADMIN_BASE}/createpermission`,
        deletePermission: `${BASE}/${ADMIN_BASE}/deletepermission`,
    },

    homePage: {
        home: `${BASE}/home`,
        adminrolename: `${BASE}/adminrolename`,
        superadmin : `${BASE}/dev-login`,
    },

    auth:{
        me: `${BASE}/api/me`,
        logout: `${BASE}/logout`,
        register: `${BASE}/register`,
        login: `${BASE}/login`,
        csrf: `${BASE}/csrf`,
        checkusername: `${BASE}/api/check-username-availability`,
        checkemail: `${BASE}/api/check-email-availability`,
    },

    kaamelott:{
        allData: `${BASE}/api/kaamelott/`,
    },

    steam:{
        allData: `${BASE}/api/steam/`,
    }

}