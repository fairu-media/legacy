import { fetchApiEndpoint } from "lib/api";
import { GetFiles } from "./types";

export async function fetchFileStatistics(): Promise<GetFiles> {
    const resp = await fetchApiEndpoint<GetFiles>("/files")
    return resp.success ? resp.data : { total_files: -1, total_hits: -1 }
}
