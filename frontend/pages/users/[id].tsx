import SEO from "components/seo";
import { fetchUser } from "lib/api/users";
import { useRouter } from "next/router"
import { useQuery } from "react-query";

export default function User() {
    const router = useRouter();

    /* get /:id parameter */
    const id = router.query.id?.toString() ?? "";

    /* fetch user */
    const { data, status } = useQuery(['user', id], () => fetchUser(id));

    return (
        <div>
            <SEO title={`Fairu${status === "success" ? ` • ${data?.username}` : ""}`} />
            {status}
            {JSON.stringify(data)}
        </div>
    )
}
