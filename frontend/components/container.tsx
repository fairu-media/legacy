import { concat } from "lib/common/concat";
import { PropsWithChildren } from "react";

export default function Container({ children, className = "", addPadding = true }: PropsWithChildren<{ className?: string, addPadding?: boolean }>) {
    return (
        <div
            className={concat(
                "mx-auto",
                "px-6 sm:px-8 md:px-10 lg:px-12 xl:px-14 2xl:px-16",
                "max-w-[1050px]",
                className,
            )}
        >
            {children}
        </div>
    )
}
