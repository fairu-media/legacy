#!/usr/bin/env node

import { readFile, readdir, lstat } from "fs/promises";
import { basename } from "path";

const {
    FAIRU_URL   = "https://img.2d.gay",
    FAIRU_TOKEN
} = process.env;

if (!FAIRU_TOKEN?.length) {
    throw new Error("Missing Fairu Token");
}

/* filter out directory paths. */
const files = [];
for (const path of process.argv.slice(2)) {
    const stats = await lstat(path);
    if (stats.isDirectory()) {
        continue
    }

    files.push(path);
}

console.log("Uploading", files.length, "files")

/* upload each file to cdn */
for (const file of files) {
    const file_name = basename(file);
    try {
        const body = new FormData();
        body.append("file", new Blob([ await readFile(file) ]), file);
        body.append("payload_json", JSON.stringify({ file_name }));

        const headers = new Headers();
        headers.append("Authorization", `Bearer ${FAIRU_TOKEN}`);

        const response = await fetch(FAIRU_URL + "/v1/files", {
            method: "PUT",
            body,
            headers
        });

        const result = await response.json();
        if (result.success) {
            console.info(`Successfully uploaded '${file_name}'`);
        } else {
            console.error(`Failed to upload '${file_name}':`, result.data);
        }

    } catch (error) {
        console.error(`Unable to upload '${file}'`, error);
    }
}
