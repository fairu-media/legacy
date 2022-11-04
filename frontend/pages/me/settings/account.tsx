import { Spinner, SpinnerSize } from "@blueprintjs/core";
import { createSettingsPage } from "components/layout/settings";
import WorkInProgress from "components/wip";
import useSession from "lib/hooks/useSession";

export default createSettingsPage("Account Settings", () => {
    const { session } = useSession(
        { redirectTo: "/auth/login" }
    );

    if (!session) {
        return <Spinner size={SpinnerSize.LARGE} />
    }

    return <WorkInProgress />;
});
