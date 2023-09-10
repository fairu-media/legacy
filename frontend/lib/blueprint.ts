import { NextRouter } from "next/router";

export function goto(router: NextRouter, href: string): () => void {
    return () => router.push(href);
}