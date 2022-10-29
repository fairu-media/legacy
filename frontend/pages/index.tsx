import { Card, H1, H2, H3, H4, H5, H6, Spinner, SpinnerSize } from "@blueprintjs/core"
import Container from "components/ui/container"
import { fetchFileStatistics } from "lib/api/files"
import { fetchUserStatistics } from "lib/api/users"
import { useQuery } from "react-query"

export default function Home() {
    const { data: file, status: fileStatus } = useQuery("file_stats", fetchFileStatistics)
    const { data: user, status: userStatus } = useQuery("user_stats", fetchUserStatistics)

    return (
        <Container className="justify-center">
            <div className="mx-auto text-center w-3/4 py-12">
                <H1>Fairu, a painless personal image-hosting server.</H1>
                <H4 className="font-normal">With an advanced backend and simple frontend UI Fairu is a great choice for your image hosting needs!</H4>
            </div>

            <div className="mt-12 flex justify-between">
                <Card className="flex flex-col">
                    <H3>{userStatus == "loading" ? <Spinner size={SpinnerSize.SMALL} /> : user}</H3>
                    <H6 className="font-normal">Registered Users</H6>
                </Card>
                <Card className="flex flex-col">
                    <H3>{fileStatus == "loading" ? <Spinner size={SpinnerSize.SMALL} /> : file?.total_files}</H3>
                    <H6 className="font-normal">Files Uploaded</H6>
                </Card>
                <Card className="flex flex-col">
                    <H3>{fileStatus == "loading" ? <Spinner size={SpinnerSize.SMALL} /> : file?.total_hits}</H3>
                    <H6 className="font-normal">Accumulated Hits</H6>
                </Card>
            </div>
        </Container>
    )
}
