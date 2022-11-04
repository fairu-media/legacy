import { Spinner, SpinnerSize } from "@blueprintjs/core";
import Container from "components/ui/container";
import WorkInProgress from "components/wip";
import useSession from "lib/hooks/useSession";
import Head from "next/head";

export default function Me() {
    const { session } = useSession(
        { redirectTo: "/auth/login" }
    );

    if (!session) {
        return <Spinner size={SpinnerSize.LARGE} />
    }

    return (
        <Container>
            <Head><title>Fairu &bull; Your Profile</title></Head>
            <WorkInProgress />
        </Container>
    );
}