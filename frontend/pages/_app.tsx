/* normalize css */
import "normalize.css/normalize.css";
/* blueprint */
import "@blueprintjs/icons/lib/css/blueprint-icons.css";
import "@blueprintjs/core/lib/css/blueprint.css";

import "@blueprintjs/table/lib/css/table.css";
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
import NavBar from "components/ui/nav/navbar";
import { ToastBar, Toaster } from "react-hot-toast";
import { IconContext } from "react-icons";
import { useRouter } from "next/router";
import { Alert } from "@blueprintjs/core";
import SEO from "components/seo";
import Head from "next/head";

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
            <SEO /* default seo */ />
            <Head>
                <meta property="og:site_name" content="Fairu" />
                <meta name="robots" content="follow, index" />
                <meta name="twitter:site" content="Fairu" />
                <meta name="twitter:card" content="summary_large_image" />
            </Head>
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
                >
                    {(t) => (
                        <ToastBar
                            toast={t}
                            style={{
                                ...t.style,
                                animation: t.visible ? 'custom-enter 1s ease' : 'custom-exit 1s ease',
                            }}
                        />
                    )}
                </Toaster>
            </IconContext.Provider>
        </QueryClientProvider>
    )
}
