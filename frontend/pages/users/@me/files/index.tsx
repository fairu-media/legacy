import { Button, Card, Divider, H1, Menu, MenuDivider, MenuItem, NonIdealState, NonIdealStateIconSize, Spinner, SpinnerSize, Tag, Toaster } from "@blueprintjs/core";
import Container from "components/ui/container";
import { meFiles } from "lib/api/users";
import useSession from "lib/hooks/useSession"
import { useQuery } from "react-query";
import { Masonry, RenderComponentProps } from "masonic";
import { File } from "lib/api/types";
import { Popover2 } from "@blueprintjs/popover2";
import { copyToClipboard } from "lib/common/clipboard";
import { goto } from "lib/blueprint";
import { useRouter } from "next/router";

function FileCard({ data: file }: RenderComponentProps<File>) {
    const url = process.env.NEXT_PUBLIC_FAIRU_BACKEND_URL + "/" + file.file_name;
    return (
        <Card key={file.id} className="p-3">
            <img alt={file.file_name} src={url} />
            <div className="flex justify-between items-center pt-[10px]">
                <span className="font-mono text-xs">{file.file_name}</span>
                <Popover2 placement="top-end" content={(
                    <Menu>
                        <MenuItem icon="link"     text="Copy Link" onClick={() => copyToClipboard(url, true)} />
                        <MenuItem icon="download" text="Open File" href={url} target="_blank" rel="noopener noreferrer" />
                        <MenuDivider />
                        <MenuItem icon="trash"    text="Delete"    intent="danger" />
                    </Menu>
                )}>
                    <Button icon="more" minimal />
                </Popover2>
            </div>
        </Card>
    )
}

export default function Files() {
    const { session } = useSession(
        { redirectTo: "/auth/login" }
    );
    
    const { data: files, status } = useQuery(
        "@me/files",
        meFiles,
        { enabled: session != null }
    );

    const router = useRouter();
    if (!session || status == "loading") {
        return (
            <Container>
                <Spinner size={SpinnerSize.LARGE} />
            </Container>
        )
    }

    return (
        <Container>
            <div className="flex items-center justify-between">
                <span className="text-lg font-semibold">Your Uploads</span>
                <Button intent="success" icon="upload" text="Upload" onClick={goto(router, "/users/@me/files/upload")} />
            </div>
            <Divider className="my-4" />
            {!files?.length && (
                <NonIdealState
                    icon="upload"
                    iconSize={NonIdealStateIconSize.STANDARD}
                    title={"No files found"}
                    description={"It looks like you haven't uploaded any files. Upload one now to get started!"}
                />
            )}

            <Masonry
                key={"@me/files"}
                items={files ?? []}
                columnGutter={8}
                render={FileCard}
            />
        </Container>
    )
}