import { Button, ButtonGroup, Callout } from "@blueprintjs/core";
import { UserAuthentication } from "lib/api/types";
import { useState } from "react";
import { FormInput } from "./input";
import { useForm } from "lib/hooks/useForm";
import { goto } from "lib/blueprint";
import { useRouter } from "next/router";
import { concat } from "lib/common/concat";
import { FaTimes } from "react-icons/fa";

type Purpose = "sign-up" | "login";

const purposeMessages: Record<Purpose, string> = {
    "sign-up": "Create an Account",
    login: "Login"
}

export type SubmissionHandler = (auth: UserAuthentication, setAlert: (message: string) => void) => void;

type AccountFormProps = {
    onSubmit: SubmissionHandler;
    purpose: Purpose;
}

export default function AccountForm({
    onSubmit,
    purpose
}: AccountFormProps) {
    const [
        alert,
        setAlert
    ] = useState<string | undefined>();

    const form = useForm<UserAuthentication>({
        fields: [
            {
                name: "username",
                type: "text",
                required: true,
                placeholder: "john_doe69",
                defaultValue: "",
                validate: username => {
                    if (username.length < 3) {
                        return username.length ? "Username must be at-least 3 characters" : "A username is required"
                    } else if (username.length > 20) {
                        return "Username must be at-most 20 characters"
                    }
                }
            },
            {
                name: "password",
                type: "password",
                required: true,
                placeholder: "your very secure password",
                defaultValue: "",
                validate: password => {
                    if (password.length < 8) {
                        return password.length ? "Password must be at-least 8 characters" : "A password is required"
                    }
                }
            }
        ],
        onSubmit: values => onSubmit(values, setAlert),
    })

    const router = useRouter(), isSignup = purpose === "sign-up";
    return (
        <form onSubmit={form.formik.handleSubmit}>
            {alert && (
                <Callout title="Couldn't Create Account" intent="danger"  className="mb-4 relative">
                    <span>{alert}</span>
                    <Button className="!text-red-300 absolute top-[8px] right-[8px]" onClick={() => setAlert(undefined)} icon={<FaTimes />} small minimal />
                </Callout>
            )}
            <section className={"space-y-4"}>
                <FormInput name="username" form={form} />
                <FormInput name="password" form={form} />
                <p className="text-red-300">* Required Field</p>
            </section>
            <div className={concat("mt-4 flex", isSignup ? "" : "justify-between")}>
                <ButtonGroup>
                    <Button intent="success" type="submit">{purposeMessages[purpose]}</Button>
                    <Button minimal onClick={goto(router, isSignup ? "/auth/login" : "/auth/sign-up")}>{purposeMessages[isSignup ? "login" : "sign-up"]}</Button>
                </ButtonGroup>
                {!isSignup && <Button minimal intent="warning" text="Reset Password" onClick={goto(router, "/auth/reset-password")} />}
            </div>
        </form>
    )
}
