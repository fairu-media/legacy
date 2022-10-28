import { Button, Card, Divider, Toast } from "@blueprintjs/core";
import toast from "react-hot-toast";

export function copyToClipboard(value: string, doToast = true) {
    navigator.clipboard.writeText(value)
    if (doToast) {
        toast.custom(t => (
            <Toast message="Copied to your clipboard!" onDismiss={() => toast.remove(t.id)} />
        ));
    }
}