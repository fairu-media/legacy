import { useQueryClient } from "react-query"
import { AnchorButton, Button, Menu, MenuDivider, MenuItem, Spinner, SpinnerSize } from "@blueprintjs/core";
import { Popover2 } from "@blueprintjs/popover2";
import { useRouter } from "next/router";
import { logout } from "lib/api/session";
import useSession from "lib/hooks/useSession";
import { frontendUrl } from "lib/contants";

export default function UserNavigation() {
    const { session, status } = useSession();
    const router = useRouter();
    const qc = useQueryClient();

    if (status == "loading") {
        return <Spinner size={SpinnerSize.SMALL} />
    }

    if (session == null) {
        return <>
            <AnchorButton href={`${frontendUrl}/auth/login`}   className="bp4-minimal" text="Login" />
            <AnchorButton href={`${frontendUrl}/auth/sign-up`} className="bp4-minimal" text="Sign Up" />
        </>
    }

    async function submitLogout() {
        const resp = await logout()
        if (resp != true) {
            router.push("?alert=" + resp);
            return;
        }

        qc.invalidateQueries("@me")
        router.replace(frontendUrl)
    }

    return (
        <Popover2 content={
            <Menu className="bp4-elevation-2">
                <MenuItem icon="mugshot"  text="Profile"  href={frontendUrl + "/users/@me"} />
                <MenuItem icon="document" text="Files"    href={frontendUrl + "/users/@me/files"} />
                <MenuItem icon="cog"      text="Settings" href={frontendUrl + "/users/@me/settings"} />

                <MenuDivider />

                <MenuItem icon="log-out" text="Logout" onClick={submitLogout} intent="danger" />
            </Menu>
        } placement="bottom-end">
            <Button className="bp4-minimal" rightIcon="chevron-down" text={session.username} />
        </Popover2>
    )
}