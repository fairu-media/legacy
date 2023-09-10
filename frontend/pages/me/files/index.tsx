import { Button, ButtonGroup, Divider, Spinner, SpinnerSize } from "@blueprintjs/core";
import Container from "components/ui/container";
import { meFiles } from "lib/api/users";
import useSession from "lib/hooks/useSession"
import { useQuery, useQueryClient } from "react-query";
import { goto } from "lib/blueprint";
import { useRouter } from "next/router";
import { FileViews, View, ViewComponent, views } from "components/ui/files";
import { useState } from "react";

export default function Files() {
    const { session } = useSession(
        { redirectTo: "/auth/login" }
    );

    const [
        currentView, 
        setCurrentView
    ] = useState<View>("Card");

    const { data: files = [], status } = useQuery(
        ["@me", "files"],
        meFiles,
        { enabled: session != null }
    );

    const router = useRouter(), qc = useQueryClient();
    if (!session || status == "loading") {
        return (
            <Container>
                <Spinner size={SpinnerSize.LARGE} />
            </Container>
        )
    }

    const View: ViewComponent = FileViews[currentView];
    return (
        <>
            <Container>
                <div className="flex items-center justify-between">
                    <span className="text-lg font-semibold">Your Uploads</span>
                    <div className="flex items-center space-x-4">
                        <ButtonGroup minimal about="File View">
                            {views.map(view => <Button
                                key={view.key}
                                title={`${view.key} View`}
                                disabled={view.key == currentView}
                                icon={view.icon}
                                onClick={() => setCurrentView(view.key)}
                            />)}
                        </ButtonGroup>
                        <Button intent="success" icon="upload" text="Upload" onClick={goto(router, "/me/files/upload")} />
                    </div>
                </div>
                <span className="text-xs text-slate-300"><span className="font-semibold">Current View:</span> {currentView}</span>
                <Divider className="my-4" />
            </Container>
            <View router={router} qc={qc} files={files ?? []} />
        </>
    )
}
