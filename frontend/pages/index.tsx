import { Card, Spinner, SpinnerSize } from "@blueprintjs/core"
import Container from "components/ui/container"
import { fetchFileStatistics } from "lib/api/files"
import { fetchUserStatistics } from "lib/api/users"
import { useQuery } from "react-query"

export default function Home() {
    const { data: file, status: fileStatus } = useQuery("file_stats", fetchFileStatistics)
    const { data: user, status: userStatus } = useQuery("user_stats", fetchUserStatistics)

    return (
        <Container className="justify-center">
            <div className="flex flex-col mx-auto text-center w-3/4 py-12">
                <span className="font-semibold text-4xl">Fairu, a painless personal image-hosting server.</span>
                <span className="text-lg">With an advanced backend and simple frontend UI Fairu is a great choice for your image hosting needs!</span>
            </div>

            <div className="mt-12 flex justify-between">
                <Card className="flex flex-col">
                    <span className="font-semibold text-xl">{userStatus == "loading" ? <Spinner size={SpinnerSize.SMALL} /> : user}</span>
                    <span className="text-sm">Registered Users</span>
                </Card>
                <Card className="flex flex-col">
                    <span className="font-semibold text-xl">{fileStatus == "loading" ? <Spinner size={SpinnerSize.SMALL} /> : file?.total_files}</span>
                    <span className="text-sm">Files Uploaded</span>
                </Card>
                <Card className="flex flex-col">
                    <span className="font-semibold text-xl">{fileStatus == "loading" ? <Spinner size={SpinnerSize.SMALL} /> : file?.total_hits}</span>
                    <span className="text-sm">Accumulated Hits</span>
                </Card>
            </div>
        </Container>
    )
}
