import { fetchApiEndpoint } from "lib/api";
import { CreatedToken, CreateToken, Token } from "./types";

export async function fetchUserTokens(): Promise<Token[]> {
    const resp = await fetchApiEndpoint<Token[]>("/users/@me/tokens")
    return resp.success ? resp.data : [];
}

export async function createUserToken(options: CreateToken): Promise<CreatedToken | string> {
    const resp = await fetchApiEndpoint<CreatedToken>("/users/@me/tokens", {
        method: "POST",
        body: JSON.stringify(CreateToken.parse(options)),
        headers: { "content-type": "application/json" }
    });

    return resp.success ? resp.data : resp.data.message;
}

export async function deleteUserToken(id: string): Promise<true | string> {
    const resp = await fetchApiEndpoint<CreatedToken>(`/users/@me/tokens/${id}`, {
        method: "DELETE",
    });

    return resp.success ? true : resp.data.message;
}
