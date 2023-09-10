import { Alert, Button, Card, Menu, MenuDivider, MenuItem, NonIdealState, NonIdealStateIconSize } from "@blueprintjs/core";
import Container from "components/ui/container";
import { deleteFile } from "lib/api/users";
import { Masonry, RenderComponentProps } from "masonic";
import { File } from "lib/api/types";
import { Popover2 } from "@blueprintjs/popover2";
import { copyToClipboard } from "lib/common/clipboard";
import { useCallback, useState } from "react";
import { ViewProps } from ".";

type FileCardProps = RenderComponentProps<File>;
type FileCardPropsReal = FileCardProps & { submitDeletion: (file: File) => void };

function FileCard({ data: file, submitDeletion }: FileCardPropsReal) {
    const url = process.env.NEXT_PUBLIC_FAIRU_BACKEND_URL + "/" + file.file_name;
    return (
        <Card key={file.id} className="flex flex-col items-center p-3">
            <img alt={file.file_name} src={url} />
            <div className="flex justify-between items-center w-full pt-[10px]">
                <span className="font-mono text-xs">{file.file_name}</span>
                <Popover2 placement="bottom-end" content={(
                    <Menu>
                        <MenuItem icon="link" text="Copy Link" onClick={() => copyToClipboard(url, true)} />
                        <MenuItem icon="download" text="Open File" href={url} target="_blank" rel="noopener noreferrer" />
                        <MenuDivider />
                        <MenuItem icon="trash" text="Delete" intent="danger" onClick={() => submitDeletion(file)} />
                    </Menu>
                )}>
                    <Button icon="more" minimal />
                </Popover2>
            </div>
        </Card>
    )
}

export default function CardView({ files, router, qc }: ViewProps) {
    const [file, setFile] = useState<File | null>(null);

    const FileComponent = useCallback(
        (props: FileCardProps) => <FileCard {...props} submitDeletion={file => setFile(file)} />,
        []
    );

    return (
        <>
            <Alert
                // className={this.props.data.themeName}
                cancelButtonText="Cancel"
                confirmButtonText="Delete"
                icon="trash"
                intent="danger"
                isOpen={file != null}
                onClose={() => setFile(null)}
                onConfirm={async () => {
                    // @ts-expect-error
                    const resp = await deleteFile(file.id)
                    setFile(null);

                    if (typeof resp == "string") {
                        router.push("/me/files?alert=" + resp);
                        return;
                    }

                    qc.invalidateQueries(["@me", "files"])
                    router.push("/me/files");
                }}
            >
                <p>
                    Are you sure you want to delete <span className="font-mono text-xs">{file?.file_name}</span>? This action cannot be undone.
                </p>
            </Alert>

            <Container large>
                {!files?.length && (
                    <NonIdealState
                        icon="upload"
                        iconSize={NonIdealStateIconSize.STANDARD}
                        title={"No files found"}
                        description={"It looks like you haven't uploaded any files. Upload one now to get started!"}
                    />
                )}

                <Masonry
                    key={`/me/files+${files?.length ?? 0}`}
                    items={files ?? []}
                    columnGutter={8}
                    render={FileComponent}
                />
            </Container>
        </>
    )
}
