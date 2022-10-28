import { fetchApiEndpoint } from "lib/api";
import { UserAuthentication } from "./types";

export async function login(body: UserAuthentication): Promise<string | true> {
    const resp = await fetchApiEndpoint("/session", {
        body: JSON.stringify(body),
        headers: { "Content-Type": "application/json" },
        method: "POST"
    });

    return resp.success ? true : resp.data.message;
}

export async function logout(): Promise<string | true> {
    const resp = await fetchApiEndpoint("/session", {
        method: "DELETE"
    });

    return resp.success ? true : resp.data.message;
}
