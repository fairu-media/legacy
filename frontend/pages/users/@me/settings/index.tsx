import { Spinner, SpinnerSize } from "@blueprintjs/core";
import Container from "components/container";
import useSession from "lib/hooks/useSession";

export default function User() {
    const { session } = useSession(
        { redirectTo: "/auth/login" }
    );


    if (!session) {
        return (
            <Container>
                <Spinner size={SpinnerSize.LARGE} />
            </Container>
        )
    }

    return "Settings will be here soon."
}
