import { Divider } from "@blueprintjs/core";
import Container from "components/ui/container";
import { RouterActivatedLink } from "components/ui/link";
import Head from "next/head";
import React, { PropsWithChildren } from "react";
import { FaCog, FaKey, FaUser } from "react-icons/fa";

export type SettingsLayoutProps = PropsWithChildren<{
}>

export function createSettingsPage(title: string, component: (/* session: User */) => React.ReactElement): React.FC {
    return function Page() {
        return (
            <SettingsLayout>
                <Head><title>Fairu &bull; {title}</title></Head>
                {component()}
            </SettingsLayout>
        )
    }
}

export default function SettingsLayout({ children }: SettingsLayoutProps) {

    return (
        <Container>
            <h1 className="text-3xl font-bold">User Settings</h1>
            <Divider className="my-6" />
            <div className="flex space-x-12">
                <div className="w-40 list-none">
                    <h1 className="uppercase text-xs font-semibold text-slate-300 px-[5px] mb-4">Sub Menus</h1>
                    <div className="space-y-1">
                        <RouterActivatedLink href="/me/settings/profile" icon={FaUser}>Profile</RouterActivatedLink>
                        <RouterActivatedLink href="/me/settings/account" icon={FaCog} >Account</RouterActivatedLink>
                        <Divider />
                        <RouterActivatedLink href="/me/settings/tokens" icon={FaKey}>Access Tokens</RouterActivatedLink>
                    </div>
                </div>
                <div className="grow">
                    {children}
                </div>
            </div>
        </Container>
    )
}