import { H1, Text } from "@blueprintjs/core";
import AccountForm, { SubmissionHandler } from "components/form/account-form";
import { login } from "lib/api/session";
import { useRouter } from "next/router";

export default function SignUp() {
    const router = useRouter();

    const submit: SubmissionHandler = async (data, setAlert) => {
        const message = await login(data);
        if (message != true) {
            return setAlert(message);
        }

        router.replace("/users/@me");
    }

    return (
        <div style={{ margin: '0 auto', width: '500px' }}>
            <div className={"space-y-7"}>
                {/* {alert && <div className={"flex flex-col font-bold text-white items-center bg-red-400 w-full rounded-lg border-red-500 border py-4 mt-6"}>
                    {alert}
                </div>} */}

                {/* header */}
                <div className={"block space-y-3"}>
                    <H1>Login</H1>
                    <Text className={"text-lg"}>Make sure to complete the required fields.</Text>
                </div>

                {/* form */}
                <AccountForm purpose="login" onSubmit={submit} />
            </div>
        </div>
    )
}
