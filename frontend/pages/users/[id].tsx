import { fetchUser } from "lib/api/users";
import { useRouter } from "next/router"
import { useQuery } from "react-query";

export default function User() {
    const router = useRouter();

    /* get /:id parameter */
    const id = router.query.id?.toString() ?? "";

    /* fetch user */
    const { data, status } = useQuery([ 'user', id ], () => fetchUser(id));

    return (
        <div>
            {status}
            {JSON.stringify(data)}
        </div>
    )
}
