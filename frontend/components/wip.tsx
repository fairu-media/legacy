import { NonIdealState, NonIdealStateIconSize } from "@blueprintjs/core";

export default function WorkInProgress() {
    return (
        <NonIdealState
            icon="wrench"
            iconSize={NonIdealStateIconSize.STANDARD}
            title={"Work in Progress"}
            description={"It looks like you you've found a work-in-progress page! Come back later to find out if it has been finished."}
        />
    )
}