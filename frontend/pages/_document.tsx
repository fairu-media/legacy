import { Html, Head, Main, NextScript } from 'next/document';
import { Colors } from "@blueprintjs/core";

export default function Document() {
    return (
        <Html lang="en">
            <Head />
            <body className="bp4-dark h-screen" style={{ background: Colors.DARK_GRAY4 }}>
                <Main />
                <NextScript />
            </body>
        </Html>
    );
}