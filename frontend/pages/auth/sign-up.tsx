import { Divider, H1, Text } from "@blueprintjs/core";
import AccountForm from "components/ui/form/account-form";
import { createUser } from "lib/api/users";
import { useRouter } from "next/router";

export default function SignUp() {
    const router = useRouter();

    return (
        <div style={{ margin: '0 auto', width: '450px' }}>
            <div className={"block space-y-3"}>
                <H1>Create a new account</H1>

                <Text className="text-lg">Make sure to complete the required fields.</Text>
            </div>
            <Divider className="my-4" />
            <AccountForm
                purpose="sign-up"
                onSubmit={async (data, setAlert) => {
                    const message = await createUser(data);
                    if (message != true) {
                        return setAlert(message);
                    }

                    await router.replace("/auth/login");
                }} />
        </div>
    )
}
