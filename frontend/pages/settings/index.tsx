import { Spinner, SpinnerSize } from "@blueprintjs/core";
import Container from "components/ui/container";
import useSession from "lib/hooks/useSession";
import { useRouter } from "next/router";

export default function User() {
    const { session } = useSession(
        { redirectTo: "/auth/login" }
    );

    const router = useRouter()

    if (!session) {
        return (
            <Container>
                <Spinner size={SpinnerSize.LARGE} />
            </Container>
        )
    }

    router.replace("/settings/profile")
}
