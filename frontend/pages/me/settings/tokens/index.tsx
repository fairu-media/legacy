import { Alert, Button, ButtonGroup, Card, Divider, NonIdealState, Spinner, SpinnerSize } from "@blueprintjs/core";
import { createSettingsPage } from "components/ui/layout/settings";
import { deleteUserToken, fetchUserTokens } from "lib/api/user-tokens";
import { useQuery, useQueryClient } from "react-query";
import Link from "next/link";
import useSession from "lib/hooks/useSession";
import { useRouter } from "next/router";
import { useState } from "react";
import { Token } from "lib/api/types";

export default createSettingsPage("Access Tokens", () => {
    const { session } = useSession(
        { redirectTo: "/auth/login" }
    );

    const [token, setToken] = useState<Token | null>(null);

    const { data: tokens } = useQuery(
        ["@me", "tokens"],
        fetchUserTokens,
        { enabled: session != null }
    );

    const router = useRouter(), qc = useQueryClient();
    if (!session || !tokens) return (
        <Spinner size={SpinnerSize.LARGE} />
    )

    return (
        <>
            <Alert
                // className={this.props.data.themeName}
                cancelButtonText="Cancel"
                confirmButtonText="Delete"
                icon="trash"
                intent="danger"
                isOpen={token != null}
                onClose={() => setToken(null)}
                onConfirm={async () => { 
                    // @ts-expect-error
                    const resp = await deleteUserToken(token.id)
                    setToken(null);

                    if (typeof resp == "string") {
                        router.push("/me/settings/tokens?alert=" + resp);
                        return;
                    }

                    qc.invalidateQueries([ "@me", "tokens" ])
                    router.push("/me/settings/tokens");
                }}
            >
                <p>
                    Are you sure you want to delete this access token? Any applications using this token will no longer no work.
                </p>
            </Alert>

            <div className="flex justify-between mb-4">
                <span className="text-lg font-semibold">Your access tokens</span>
                <Button icon="add" onClick={() => router.push("/me/settings/tokens/create")}>
                    Create New
                </Button>
            </div>
            <Divider className="mb-4" />
            <div className="space-y-4">
                {!tokens.length && (
                    <NonIdealState
                        icon="key"
                        title="No access tokens"
                        description="It looks like you haven't created any access tokens yet. Use the 'Create New' button to start!"
                    />
                )}

                {tokens.map(token => {
                    return (
                        <Card key={token.id} className="flex items-center rounded justify-between">
                            <div className="flex flex-col">
                                <div className="flex items-center space-x-1">
                                    <Link href={`/settings/tokens/${token.id}`} className="font-bold">{token.name}</Link>
                                    <span className="italic text-slate-400">&mdash; {token.scopes.join(", ")}</span>
                                </div>
                                <span className="text-slate-200 italic">
                                    Expires on
                                    <span className="font-medium ml-1">{new Date(token.expires_at).toDateString()}</span>
                                </span>
                            </div>
                            <ButtonGroup className="space-x-4">
                                <Button icon="trash" intent="danger" onClick={() => setToken(token)}>Delete</Button>
                            </ButtonGroup>
                        </Card>
                    )
                })}
            </div>
            <span className="mt-4 opacity-50 text-xs flex justify-center">
                You may only view access tokens once.
            </span>
        </>
    )
})