import React from "react";
import CardView from "./card";
import { QueryClient } from "react-query";
import { NextRouter } from "next/router";
import { File } from "lib/api/types";
import { IconName } from "@blueprintjs/core";
import { TableView } from "./table";

export type View = "Card" | "Table" /*  | "Details"*/;

export type ViewProps = { files: File[], router: NextRouter, qc: QueryClient }

export type ViewComponent = React.FC<ViewProps>

export const FileViews: Record<View, ViewComponent> = {
    Card:  CardView,
    Table: TableView,
}

export const views: { key: View, icon: IconName }[] = [
    {
        key: "Card",
        icon: "grid-view"
    },
    {
        key: "Table",
        icon: "panel-table"
    },
]
