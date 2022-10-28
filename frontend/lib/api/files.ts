import { fetchApiEndpoint } from "lib/api";
import { GetFiles } from "./types";

export async function fetchFileStatistics(): Promise<GetFiles> {
    const resp = await fetchApiEndpoint<GetFiles>("/files")
    return resp.success ? resp.data : { file_count: -1, total_hits: -1 }
}
