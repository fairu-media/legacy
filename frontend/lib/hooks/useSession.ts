import { useRouter } from "next/router";
import { useQuery } from "react-query";
import * as session from "lib/api/session";

export default function useSession({
    redirectTo = "",
    redirectIfFound = false,
} = {}) {
    const router = useRouter();

    const { data, status } = useQuery(
        "@me", 
        session.current,
        {
            async onSuccess(resp) {
                if (typeof resp == "string") {
                    await router.replace("?alert" + resp);
                    return
                }

                if (!redirectTo) return;

                if (!resp.logged_in || redirectIfFound) {
                    await router.replace(redirectTo)
                }
            }
        }
    );

    return {
        session: typeof data === "string" ? null : data?.user,
        status
    };
}
 