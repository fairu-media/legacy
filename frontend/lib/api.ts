import { backendUrl } from "./contants";

export type BasicResponse<D = {}> =
    | { success: false, data: { message: string } } 
    | { success: true , data: D }

export async function fetchApiEndpoint<T>(endpoint: `/${string}`, options: RequestInit = {}): Promise<BasicResponse<T>> {
    const response = await fetch(backendUrl + "/v1" + endpoint, {
        ...options,
        credentials: "include"
    });
    
    return response.json();
}
