export type BasicResponse<D = {}> = 
    | { success: false, data: { message: string } } 
    | { success: true , data: D }

export async function fetchApiEndpoint<T>(endpoint: `/${string}`, options: RequestInit = {}): Promise<BasicResponse<T>> {
    const response = await fetch(process.env.NEXT_PUBLIC_FAIRU_BACKEND_URL + "/v1" + endpoint, {
        ...options,
        credentials: "include"
    });
    
    return response.json();
}
