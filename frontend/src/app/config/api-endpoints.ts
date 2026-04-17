import { environment } from "../../environments/environment";

const BASE = environment.apiBaseUrl;

const ADMIN_BASE = `admin`;

export const API_ENDPOINTS = {

    admin: {
        fetchRoles: `${BASE}/${ADMIN_BASE}/fetchroles`,
        fetchPermissions: `${BASE}/${ADMIN_BASE}/fetchpermissions`,
        
    },

    homePage: {
        home: `${BASE}/home`,
        adminrolename: `${BASE}/adminrolename`,
    },

    auth:{
        me: `${BASE}/api/me`,
        logout: `${BASE}/logout`,
        register: `${BASE}/register`,
        login: `${BASE}/login`,
        csrf: `${BASE}/csrf`
    },

    kaamelott:{
        allData: `${BASE}/api/kaamelott/`,
    },

    steam:{
        allData: `${BASE}/api/steam/`,
    }

}