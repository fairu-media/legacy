import { z } from "zod";

export const UserAuthentication = z.object({
    username: z.string(),
    password: z.string(),
});

export type UserAuthentication = z.infer<typeof UserAuthentication>;

export interface User {
    id: string;
    last_updated_at: string;
    username: string;
}

export interface File {
    id: string;
    last_updated_at: string;
    file_name: string;
    content_type: string;
    hits: 0;
}

export interface GetFiles { total_hits: number, file_count: number }
export interface GetUsers { total_users: number }
