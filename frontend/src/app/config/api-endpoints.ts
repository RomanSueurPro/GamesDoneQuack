import { environment } from "../../environments/environment";

const BASE = environment.apiBaseUrl;

const ADMIN_BASE = `admin`;
const AUTH_BASE = `auth`;
const USERS_BASE = `users`;

export const API_ENDPOINTS = {

    admin: {
        fetchAllRoles: `${BASE}/${ADMIN_BASE}/fetchallroles`,
        fetchAllRolesNoPermissionField: `${BASE}/${ADMIN_BASE}/fetchallrolesnopermissionfield`,
        fetchAllPermissions: `${BASE}/${ADMIN_BASE}/fetchallpermissions`,
        fetchAllPermissionsNoRoleField: `${BASE}/${ADMIN_BASE}/fetchallpermissionsnorolefield`,
        fetchAllUsers: `${BASE}/${ADMIN_BASE}/fetchallusersnorelations`,
        fetchPaginatedUsers: `${BASE}/${ADMIN_BASE}/fetchpaginatedusers`,
        updateRole: `${BASE}/${ADMIN_BASE}/updaterole`,
        createRole: `${BASE}/${ADMIN_BASE}/createrole`,
        deleteRole: `${BASE}/${ADMIN_BASE}/deleterole`,
        updatePermission: `${BASE}/${ADMIN_BASE}/updatepermission`,
        createPermission: `${BASE}/${ADMIN_BASE}/createpermission`,
        deletePermission: `${BASE}/${ADMIN_BASE}/deletepermission`,
        updateUser: `${BASE}/${ADMIN_BASE}/updateuser`,
        searchUserAdmin: `${BASE}/${ADMIN_BASE}/searchusers`,
        fetchUserLastBan: `${BASE}/${ADMIN_BASE}/fetchuserlastban`,
        unbanUser: `${BASE}/${ADMIN_BASE}/unbanuser`,
        banUser: `${BASE}/${ADMIN_BASE}/banuser`,
    },

    homePage: {
        home: `${BASE}/home`,
        adminrolename: `${BASE}/adminrolename`,
        superadmin : `${BASE}/dev-login`,
        csrf: `${BASE}/csrf`,
    },

    auth:{
        me: `${BASE}/${AUTH_BASE}/me`,
        logout: `${BASE}/${AUTH_BASE}/logout`,
        register: `${BASE}/${AUTH_BASE}/register`,
        login: `${BASE}/${AUTH_BASE}/login`,
    },

    kaamelott:{
        allData: `${BASE}/api/kaamelott/`,
    },

    steam:{
        allData: `${BASE}/api/steam/`,
    },

    user:{
        userInfoById: `${BASE}/${USERS_BASE}/getuserprofilebyid`, 
        userInfoByusername: `${BASE}/${USERS_BASE}/getuserprofilebyusername`,
        checkusername: `${BASE}/${USERS_BASE}/check-username-availability`,
        checkemail: `${BASE}/${USERS_BASE}/check-email-availability`,
        avatarUpload: `${BASE}/${USERS_BASE}/uploadAvatar`,
        editProfile: `${BASE}/${USERS_BASE}/editprofile`,
    }

}