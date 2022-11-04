import { Button, Divider, Label } from "@blueprintjs/core";
import { UserAuthentication } from "lib/api/types";
import { capitalize } from "lib/common/capitalize";
import { HTMLInputTypeAttribute, useState } from "react";
import { FieldPath, RegisterOptions, useForm, UseFormReturn } from "react-hook-form";

const errorMessages: Record<keyof UserAuthentication, Record<string, string>> = {
    // TODO: enforce these server-side as well.
    username: {
        required: "A username is required",
        minLength: "Username must be at-least 3 characters",
        maxLength: "Username must be at-most 20 characters",
    },
    password: {
        required: "A password is required",
        minLength: "Password must be at-least 8 characters",
    }
};

function FormInput<T extends FieldPath<UserAuthentication> = FieldPath<UserAuthentication>>({
    name,
    type,
    placeholder,
    options,
    form: { formState, register } 
}: {
    name: T,
    placeholder?: string,
    error?: string,
    type?: HTMLInputTypeAttribute,
    options?: RegisterOptions<UserAuthentication, T>,
    form: UseFormReturn<UserAuthentication>,
}) {
    const error = formState.errors[name]
        // @ts-expect-error
        , errorMessage = error && errorMessages[name]?.[error.type];

    return (
        <div>
            <Label htmlFor={name}>
                {options?.required && <span className={"text-red-500"}>*</span>}
                {capitalize(name)}
            </Label>

            <div className="bp4-input-group">
                <input
                    {...register(name, options)}
                    type={type}
                    placeholder={placeholder}
                    className="bp4-input"
                />
            </div>

            {errorMessage && <span className={"text-red-500 text-xs lowercase"}>{errorMessage}</span>}
        </div>
    );
}

type Purpose = "sign-up" | "login";

export type SubmissionHandler = (auth: UserAuthentication, setAlert: (message: string) => void) => void;

type AccountFormProps = {
    onSubmit: SubmissionHandler;
    purpose: Purpose;
}

// TODO: add better error/alert handling

export default function AccountForm({
    onSubmit,
    purpose
}: AccountFormProps) {
    const form = useForm<UserAuthentication>();
    const [alert, setAlert] = useState<string | undefined>();

    const isSignup = purpose === "sign-up";

    return (
        <form onSubmit={form.handleSubmit(data => onSubmit(data, setAlert))} className={"space-y-6"}>
            {alert}
            <section className={"space-y-4 pb-2"}>
                <FormInput
                    name="username"
                    type="text"
                    placeholder="john_doe69"
                    options={{ minLength: 3, maxLength: 16, required: true }}
                    form={form} />
                <FormInput
                    name="password"
                    type="password"
                    placeholder="your very secure password"
                    options={{ minLength: 8, required: true }}
                    form={form} />
                <p className="text-red-500">
                    * Required Field
                </p>
            </section>
            <Divider />
            <div className={"space-y-4"}>
                <Button type="submit">
                    {isSignup ? "Create Account" : "Login"}
                </Button>
            </div>
        </form>
    )
}
