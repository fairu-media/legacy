import { z } from "zod";

export const UserAuthentication = z.object({
    username: z.string(),
    password: z.string(),
});

export const CreateToken = z.object({
    name: z.string(),
    expiration: z.number(),
    scopes: z.array(z.string())
});

export const TOKEN_SCOPES: { id: TokenScope, summary: string }[] = [
    { id: "file.upload", summary: "Upload files on your behalf" },
    { id: "file.delete", summary: "Delete uploaded files" }
]

/* requests */
export type UserAuthentication = z.infer<typeof UserAuthentication>;
export type CreateToken        = z.infer<typeof CreateToken>;

/* documents */
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

export interface Token {
    id: string;
    name: string;
    scopes: TokenScope[];
    user_id: string;
    expires_at: number;
    last_updated: number;
}

export interface CreatedToken {
    access_token: string;
    id: string;
}

export interface CurrentSession {
    logged_in: boolean;
    user: User | null;
}

export type TokenScope = "file.upload" | "file.delete";

/* responses */
export interface GetFiles { total_hits:  number, total_files: number }
export interface GetUsers { total_users: number }


