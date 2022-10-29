import { concat } from "lib/common/concat";
import { ButtonHTMLAttributes } from "react";
import { IconType } from "react-icons";

export interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
    minimal?: boolean;
    icon?: IconType;
}

export default function Button({ minimal, icon: Icon, children, className, ...props }: ButtonProps) {
    return (
        <button className={concat(
            "bp4-button",
            minimal ? "bp4-minimal" : "",
            className ?? ""
        )} {...props}>
            {Icon && (<span className="bp4-icon"><Icon /></span>)}
            {children}
        </button>
    )
}