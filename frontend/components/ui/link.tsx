import { concat } from "lib/common/concat";
import NextLink, { LinkProps } from "next/link";
import { useRouter } from "next/router";
import { AnchorHTMLAttributes } from "react";
import { IconType } from "react-icons/lib";

export interface MenuLinkItemsProps extends AnchorHTMLAttributes<HTMLAnchorElement> {
    href: string;
    icon?: IconType;
    active?: boolean;
}

export function Link({ children, active, icon: Icon, href, ...props }: MenuLinkItemsProps & LinkProps) {
    return (
        <NextLink 
            {...props} 
            href={href}
            className={concat(
                "fairu-btn fairu-btn-minimal !no-underline",
                children ? "" : "fairu-btn-icon-only",
                active ? "!text-blue-300" : "!text-zinc-300" 
            )}
        >
            {Icon && <Icon className={children ? "mr-[7px]" : ""} />}
            {children}
        </NextLink>
    )
}

export function RouterActivatedLink(props: MenuLinkItemsProps) {
    const router = useRouter();
    return <Link active={router.pathname == props.href} {...props} />
}
