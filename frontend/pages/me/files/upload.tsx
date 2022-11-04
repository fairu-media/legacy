import { Button, ButtonGroup, Card, Divider, Spinner, SpinnerSize, Toast } from "@blueprintjs/core";
import Container from "components/ui/container";
import { uploadFiles } from "lib/api/files";
import useSession from "lib/hooks/useSession";
import { Masonry } from "masonic";
import Head from "next/head";
import { useRouter } from "next/router";
import { useCallback, useEffect, useState } from "react";
import { ErrorCode, useDropzone } from "react-dropzone";
import toast from "react-hot-toast";

type FilePreview = { file: File, url: string };

export default function FileUpload() {
    const router = useRouter();

    const { session } = useSession(
        { redirectTo: "/auth/login" }
    );

    const [files, setFiles] = useState<File[]>([])
    function removeFile(file: File) {
        const newFiles = [...files]
        newFiles.splice(newFiles.indexOf(file), 1)
        setFiles(newFiles)
    }

    /* setup dropzone. */
    const onDrop = useCallback(
        (accepted: File[]) => setFiles([...files, ...accepted]),
        [files]
    );

    const { getRootProps, getInputProps, isDragActive } = useDropzone({
        // maxFiles: 10,
        onDrop,
        validator: (file: File) => {
            if (!file.name.match(/\.(webp|png|jpe?g)$/m)) {
                return {
                    code: ErrorCode.FileInvalidType,
                    message: "Image must be of type: Webp, png, or jpeg"
                }
            }

            return null;
        },

    });

    /* get image previews */
    const [previews, setPreviews] = useState<FilePreview[]>([]);
    useEffect(() => {
        const fileReaders: FileReader[] = [];

        let isCancel = false;
        if (files.length) {
            const promises = files.map(file => new Promise<FilePreview>((resolve, reject) => {
                const fileReader = new FileReader();

                fileReaders.push(fileReader);

                fileReader.onload = (e) => {
                    const result = e.target?.result;
                    if (result) resolve({ url: result as string, file });
                }

                fileReader.onabort = () => {
                    reject(new Error("File reading aborted"));
                }

                fileReader.onerror = () => {
                    reject(new Error("Failed to read file"));
                }

                fileReader.readAsDataURL(file);
            }));

            Promise
                .all(promises)
                .then(images => {
                    if (!isCancel) {
                        setPreviews(images);
                    }
                })
                .catch(reason => {
                    console.log(reason);
                });
        } else {
            setPreviews([])
        };

        return () => {
            isCancel = true;
            fileReaders.forEach(fileReader => {
                if (fileReader.readyState === 1) fileReader.abort()
            })
        }
    }, [files]);

    if (!session) {
        return (
            <Container>
                <Head><title>Fairu &bull; Upload File</title></Head>
                <Spinner size={SpinnerSize.LARGE} />
            </Container>
        )
    }

    async function handleSubmit() {
        if (!files.length) {
            router.push("?alert=You need to select some files first!");
            return;
        }

        const formData = new FormData();
        files.forEach(file => formData.append(file.name, file));

        const resp = await uploadFiles(formData);
        if (typeof resp == "string") {
            router.push("?alert=" + resp);
            return;
        }

        toast.custom(t => (
            <Toast intent="success" message="Successfully uploaded your files." onDismiss={() => toast.remove(t.id)} />
        ))

        router.push("/me/files");
    }

    return (
        <Container>
            <Head><title>Fairu &bull; Upload File</title></Head>
            <div className="flex items-center justify-between">
                <span className="text-lg font-semibold">Upload a File</span>
                <ButtonGroup>
                    <Button icon="tick"  text="Finish" intent="success"         onClick={handleSubmit} />
                    <Button icon="cross" text="Cancel" intent="none"    minimal onClick={() => router.back()} />
                    <Button icon="reset" text="Reset"  intent="warning" minimal onClick={() => setFiles([])} />
                </ButtonGroup>
            </div>
            <span className="text-xs text-slate-300">Note: File names will be randomized upon upload!</span>
            <Divider className="my-4" />
            <div {...getRootProps({ className: "dropzone" })} className="flex bp4-card h-24">
                <input className="absolute input-zone" {...getInputProps()} />
                <div className="m-auto text-center font-semibold">
                    {isDragActive ? (
                        <p className="dropzone-content">Release to drop the files here.</p>
                    ) : (
                        <p className="dropzone-content">Drop or click to upload files.</p>
                    )}
                </div>
            </div>
            <div className="mt-4">
                <Masonry
                    key={previews.length}
                    items={previews}
                    columnGutter={8}
                    render={({ data }) => (
                        <Card className="p-3">
                            <span className="font-mono text-xs">{data.file.name}</span>
                            <img alt={"file preview"} src={data.url} className="my-[10px]" />
                            <ButtonGroup>
                                <Button icon="cross" intent="danger" text="Remove" onClick={() => removeFile(data.file)} small />
                            </ButtonGroup>
                        </Card>
                    )}
                />
            </div>
        </Container>
    )
}