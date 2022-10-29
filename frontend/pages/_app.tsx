/* normalize css */
import "normalize.css/normalize.css";
/* blueprint */
import "@blueprintjs/icons/lib/css/blueprint-icons.css";
import "@blueprintjs/core/lib/css/blueprint.css";

import "@blueprintjs/datetime/lib/css/blueprint-datetime.css";
import "@blueprintjs/datetime2/lib/css/blueprint-datetime2.css";
import "@blueprintjs/popover2/lib/css/blueprint-popover2.css";
import "@blueprintjs/select/lib/css/blueprint-select.css";

/* tailwind */
import "styles/globals.scss";

import type { AppProps } from "next/app"
import {
    QueryClient,
    QueryClientProvider,
} from 'react-query'
import NavBar from "components/nav/navbar";
import { Toaster } from "react-hot-toast";
import { IconContext } from "react-icons";
import { useRouter } from "next/router";
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
                        width: '16px',
                        height: '16px',
                    },
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
