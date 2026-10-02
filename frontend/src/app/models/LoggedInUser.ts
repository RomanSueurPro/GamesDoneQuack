export interface LoggedInUser {
    username: string;
    userId: number;
    roleName: string;
    banned: boolean;
    unbanDate: Date;
}