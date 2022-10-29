import { concat } from "lib/common/concat";
import { HTMLDivProps } from "../props";

export interface ContainerProps extends HTMLDivProps {
    padded?: boolean;
}

export default function Container({ children, className = "", padded = true, ...props }: ContainerProps) {
    return (
        <div
            {...props}
            className={concat(
                "mx-auto max-w-[1050px]",
                padded ? "px-6 sm:px-8 md:px-10 lg:px-12 xl:px-14 2xl:px-16" : "",
                className,
            )}
        >
            {children}
        </div>
    )
}
