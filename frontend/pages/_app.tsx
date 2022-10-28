/* normalize css */
import "normalize.css/normalize.css";
/* blueprint */
import "@blueprintjs/icons/lib/css/blueprint-icons.css";
import "@blueprintjs/core/lib/css/blueprint.css";
/* tailwind */
import "styles/globals.css";

import type { AppProps } from "next/app"
import {
    QueryClient,
    QueryClientProvider,
} from 'react-query'
import NavBar from "components/nav/navbar";
import { Toaster } from "react-hot-toast";
import { IconContext } from "react-icons";
import { useRouter } from "next/router";
import { useState } from "react";
import { Alert } from "@blueprintjs/core";

// Create a client
const queryClient = new QueryClient()

export default function App({ Component, pageProps }: AppProps) {
    const router = useRouter();

    const { alert } = router.query;
    function closeAlert() {
        router.replace("")
    }

    return (
        <QueryClientProvider client={queryClient}>
            <IconContext.Provider
                value={{
                    style: {
                        width: '1.25em',
                        height: '1.25em',
                        textAlign: 'center',
                        verticalAlign: '-.125em',
                        transformOrigin: 'center',
                        overflow: 'visible'
                    }
                }}
            >
                {alert && (
                    <Alert
                        confirmButtonText="Okay"
                        isOpen={alert != null}
                        onClose={closeAlert}
                    >
                        <p>
                            {alert}
                        </p>
                    </Alert>
                )}
                <NavBar />
                <Component {...pageProps} />
                <Toaster
                    position="top-center"
                    reverseOrder={false}
                />
            </IconContext.Provider>
        </QueryClientProvider>
    )
}
