import { useQueryClient } from "react-query"
import { Button, Menu, MenuDivider, MenuItem, Spinner, SpinnerSize } from "@blueprintjs/core";
import { Popover2 } from "@blueprintjs/popover2";
import { useRouter } from "next/router";
import { logout } from "lib/api/session";
import useSession from "lib/hooks/useSession";
import { goto } from "lib/blueprint";

export default function UserNavigation() {
    const { session, status } = useSession();
    const router = useRouter();
    const qc = useQueryClient();

    if (status == "loading") {
        return <Spinner size={SpinnerSize.SMALL} />
    }

    if (session == null) {
        return <>
            <Button onClick={goto(router, `/auth/login`)}   className="bp4-minimal" text="Login" />
            <Button onClick={goto(router, `auth/sign-up`)} className="bp4-minimal" text="Sign Up" />
        </>
    }

    async function submitLogout() {
        const resp = await logout()
        if (resp != true) {
            router.push("?alert=" + resp);
            return;
        }

        qc.invalidateQueries("@me")
        router.push("/")
    }

    return (
        <Popover2 content={
            <Menu className="">
                <MenuItem icon="mugshot"  text="Profile"  onClick={goto(router, "/users/@me")} />
                <MenuItem icon="document" text="Files"    onClick={goto(router, "/users/@me/files")} />
                <MenuItem icon="cog"      text="Settings" onClick={goto(router, "/settings")} />

                <MenuDivider />

                <MenuItem icon="log-out" text="Logout" onClick={submitLogout} intent="danger" />
            </Menu>
        } placement="bottom-end">
            <Button className="bp4-minimal" rightIcon="chevron-down" text={session.username} />
        </Popover2>
    )
}