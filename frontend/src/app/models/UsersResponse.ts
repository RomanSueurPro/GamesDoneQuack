import { UserNoRelations } from "./UserNoRelations";


export interface UsersResponse {
  users: UserNoRelations[];
  totalElements: number;
  totalPages: number;
}