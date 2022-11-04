import type { ViewComponent, ViewProps } from ".";
import Container from "../container";
import { Column, Table2, SelectionModes, Region, CellInterval } from "@blueprintjs/table";
import { Alert, Button, ButtonGroup, HotkeysProvider, ProgressBar, Toast } from "@blueprintjs/core";
import { useState } from "react";
import { format } from "date-fns";
import toast from "react-hot-toast";
import { deleteFile } from "lib/api/users";


export function createRange(range: CellInterval) {
    const [min, max] = range;
    return Array.from({ length: max - min + 1 }, (_, i) => min + i);
}

export const TableView: ViewComponent = ({ router, files, qc }: ViewProps) => {
    const [selected, setSelected] = useState<Set<number>>(new Set());

    const [open, setOpen] = useState(false);

    const [active, setActive] = useState(false);

    function updateSelection(regions: Region[]) {
        const indices = regions
            .map(r => r.rows ?? [])
            .filter(r => r.length > 0)
            .flatMap(r => createRange(r as CellInterval))

        setSelected(new Set(indices));
    }

    function openFileDeletionAlert() {
        if (active) {
            toast.custom(t => (
                <Toast intent="warning" message={"Please wait until the previous files have been deleted."} onDismiss={() => toast.remove(t.id)} />
            ));

            return;
        }

        setOpen(true);
    }

    return (
        <>
            <Alert
                cancelButtonText="Cancel"
                confirmButtonText="Delete"
                icon="trash"
                intent="danger"
                isOpen={open}
                onClose={() => setOpen(false)}
                onConfirm={async () => {
                    const selection = selected;

                    setActive(true);
                    setOpen(false);

                    let amount = 0, cancelled = false, toastId: string | undefined;
                    function show() {
                        const percentage = amount / selection.size
                        toastId = toast.custom(
                            () => <Toast
                                icon="trash"
                                intent="none"
                                className="items-center"
                                onDismiss={() => cancelled = true}
                                isCloseButtonShown={false}
                                action={{ text: "Cancel" }}
                                timeout={0}
                                message={<ProgressBar intent={"danger"} value={percentage} stripes={amount < 100} />}
                            />,
                            { duration: Infinity, id: toastId }
                        )
                    }

                    let error: string | undefined;
                    show();

                    // @ts-expect-error
                    for (const index of [...selection]) {
                        if (cancelled) {
                            break;
                        }

                        const message = await deleteFile(files[index].id);
                        if (typeof message === "string") {
                            error = message;
                            break
                        }


                        amount++;
                        show();
                    }

                    if (error) {
                        router.push("?alert=" + error);
                        toast.remove(toastId)
                    } else {
                        toast.custom(
                            t => <Toast icon="trash" message={cancelled ? "Cancelled File Deletion" : "Successfully deleted all files."} onDismiss={() => toast.remove(t.id)} />,
                            { id: toastId }
                        );
                    }

                    qc.invalidateQueries([ "@me", "files" ])
                    setActive(false);
                }}
            >
                <p>
                    Are you sure you want to delete <span className="font-semibold">{selected.size} files</span>? This action cannot be undone.
                </p>
            </Alert>

            <Container className="flex flex-col">
                <ButtonGroup className="mb-4">
                    <Button icon="trash" intent="danger" text="Delete Selected" disabled={selected.size < 1} onClick={openFileDeletionAlert} />
                </ButtonGroup>
                <HotkeysProvider>
                    <Table2
                        numRows={files.length}
                        enableColumnResizing={false}
                        selectionModes={SelectionModes.ROWS_ONLY}
                        onSelection={regions => updateSelection(regions)}
                    >
                        <Column name="File Name" cellRenderer={i => <span className="!font-mono pl-[10px]">{files[i].file_name}</span>} />
                        <Column name="Content Type" cellRenderer={i => <span className="!font-mono pl-[10px]">{files[i].content_type}</span>} />
                        <Column name="Total Hits" cellRenderer={i => <span className="!font-mono pl-[10px]">{files[i].hits}</span>} />
                        <Column name="Last Updated" cellRenderer={i => <span className="pl-[10px]">{format(new Date(files[i].last_updated_at), "yyyy-MM-dd hh:mm")}</span>} />
                    </Table2>
                </HotkeysProvider>
            </Container>
        </>
    )
}

