import { H1, Text } from "@blueprintjs/core";
import AccountForm, { SubmissionHandler } from "components/form/account-form";
import { createUser, me } from "lib/api/users";
import { useRouter } from "next/router";
import { useQueryClient } from "react-query";

export default function SignUp() {
    const router = useRouter();
    const qc = useQueryClient()

    const submit: SubmissionHandler = async (data, setAlert) => {
        const message = await createUser(data);
        if (message != true) {
            return setAlert(message);
        }

        await qc.prefetchQuery("@me", me);
        router.replace("/auth/login");
    }

    return (
        <div style={{ margin: '0 auto', width: '500px' }}>
            <div className={"space-y-7 py-6"}>
                {/* {alert && <div className={"flex flex-col font-bold text-white items-center bg-red-400 w-full rounded-lg border-red-500 border py-4 mt-6"}>
                    {alert}
                </div>} */}

                {/* header */}
                <div className={"block space-y-3"}>
                    <H1>Create a new account</H1>

                    <Text className="text-lg">Make sure to complete the required fields.</Text>
                </div>

                {/* form */}
                <AccountForm purpose="sign-up" onSubmit={submit} />
            </div>
        </div>
    )
}
