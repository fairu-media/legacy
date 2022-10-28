import { useRouter } from "next/router";
import { useQuery } from "react-query";
import { me } from "lib/api/users";
import { frontendUrl } from "lib/contants";

export default function useSession({
    redirectTo = "",
    redirectIfFound = false,
} = {}) {
    const router = useRouter();
    const { data, status } = useQuery(
        "@me", 
        me,
        {
            onSuccess(user) {
                if (!redirectTo) return;

                if (user == null || redirectIfFound) {
                    router.replace(frontendUrl + redirectTo)
                }
            }
        }
    );

    return { session: data, status };
}
 