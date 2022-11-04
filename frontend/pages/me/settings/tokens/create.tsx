import { Button, ButtonGroup, Checkbox, Classes, Divider, Label, Spinner, SpinnerSize, Toast } from "@blueprintjs/core";
import { createSettingsPage } from "components/layout/settings";
import useSession from "lib/hooks/useSession";
import { useFormik } from "formik";
import { TokenScope, TOKEN_SCOPES } from "lib/api/types";
import { concat } from "lib/common/concat";
import { DateInput2 } from "@blueprintjs/datetime2";
import { format } from "date-fns";
import { useRouter } from "next/router";
import { createUserToken } from "lib/api/user-tokens";
import { copyToClipboard } from "lib/common/clipboard";
import toast from "react-hot-toast";
import Head from "next/head";

export default createSettingsPage("Create Access Token", () => {
    const { session } = useSession(
        { redirectTo: "/auth/login" }
    );

    const router = useRouter();

    const formik = useFormik<{
        name: string;
        scopes: TokenScope[];
        expiration: string | null;
    }>({
        initialValues: {
            name: "My access token",
            scopes: [],
            expiration: null // TODO: how do we let the user select access token expirations?
        },
        onSubmit: async values => {
            const response = await createUserToken({
                name: values.name,
                expiration: new Date(values.expiration as unknown as string).getTime(),
                scopes: values.scopes
            });

            if (typeof response === "string") {
                router.push("/me/settings/tokens?alert=" + response);
                return;
            }

            copyToClipboard(response.access_token, false);
            toast.custom(t => (
                <Toast intent="success" message="Successfully created an access token! It has been copied to your clipboard." onDismiss={() => toast.remove(t.id)} />
            ))

            router.push("/me/settings/tokens");
        },
        validate: values => {
            const errors: NodeJS.Dict<any> = {};
            if (!values.name) {
                errors.name = "Required"
            }

            if (!values.expiration) {
                errors.expiration = "Required"
            }

            return errors;
        }
    })

    if (!session) {
        return <Spinner size={SpinnerSize.LARGE} />
    }

    return (
        <>
            <span className="text-lg font-semibold">Create new access token</span>
            <Divider className="my-4" />
            <form onSubmit={formik.handleSubmit} className="space-y-5">
                <div className="flex flex-col">
                    <Label htmlFor="name" className="!mb-2 space-x-2">
                        <span className="bp4-text-large font-bold">
                            <span className={"text-red-500"}>*</span>
                            Name
                        </span>

                        {formik.errors.name && (
                            <span className={"text-red-500 text-xs lowercase"}>
                                {formik.errors.name}
                            </span>
                        )}
                    </Label>
                    <input
                        id="name"
                        name="name"
                        placeholder="What is this access token for?"
                        className={Classes.INPUT}
                        onChange={formik.handleChange}
                        value={formik.values.name}
                    />
                </div>
                <div>
                    <Label htmlFor="expiration" className="!mb-2 space-x-2">
                        <span className="bp4-text-large font-bold">
                            <span className={"text-red-500"}>*</span>
                            Expiration
                        </span>

                        {formik.errors.expiration && (
                            <span className={"text-red-500 text-xs lowercase"}>
                                {formik.errors.expiration}
                            </span>
                        )}
                    </Label>
                    <DateInput2
                        minDate={new Date()}
                        formatDate={date => format(date, "yyyy-MM-dd")}
                        onChange={date => formik.setFieldValue("expiration", date)}
                        parseDate={date => new Date(date)}
                        placeholder="When should this token expire?"
                        value={formik.values.expiration}
                    />
                </div>

                <div>
                    <Label htmlFor="scopes" className="!mb-2 space-x-2">
                        <span className="bp4-text-large font-bold">
                            Scopes
                        </span>

                        {formik.errors.scopes && <span className={"text-red-500 text-xs lowercase"}>{formik.errors.scopes}</span>}
                    </Label>
                    <div className="mt-2">
                        {TOKEN_SCOPES.map(scope => (
                            <Checkbox
                                key={scope.id}
                                className="flex items-center"
                                name="scopes"
                                value={scope.id}
                                onChange={formik.handleChange}
                                checked={formik.values.scopes.includes(scope.id)}
                            >
                                <span title={scope.summary} className={concat("font-mono", "text-xs")}>{scope.id}</span>
                            </Checkbox>
                        ))}
                    </div>
                </div>


                <p className="text-red-500">
                    * Required Field
                </p>

                <Divider />
                <ButtonGroup>
                    <Button type="submit" text="Create token" intent="success" />
                    <Button text="Cancel" minimal onClick={() => router.back()} />
                </ButtonGroup>
            </form>
        </>
    )
})