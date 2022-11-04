import { Divider, H1, Text } from "@blueprintjs/core";
import AccountForm, { SubmissionHandler } from "components/ui/form/account-form";
import { login } from "lib/api/session";
import { useRouter } from "next/router";
import {useQueryClient} from "react-query";

// TODO: add a "forgot password" link
// TODO: add a "sign up" link

export default function SignUp() {
    const router = useRouter(), qc = useQueryClient();

    const submit: SubmissionHandler = async (data, setAlert) => {
        const message = await login(data);
        if (message != true) {
            return setAlert(message);
        }

        await qc.invalidateQueries("@me");
        await router.replace("/me");
    }

    return (
        <div style={{ margin: '0 auto', width: '450px' }}>
            <div className={"block space-y-3"}>
                <H1>Login</H1>
                <Text className={"text-lg"}>Make sure to complete the required fields.</Text>
            </div>

            <Divider className="my-4" />
            <AccountForm purpose="login" onSubmit={submit} />
        </div>
    )
}
