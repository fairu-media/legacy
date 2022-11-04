import { Alignment, Button, Navbar, Tag } from "@blueprintjs/core";
import Container from "components/ui/container";
import UserNavigation from "./user";
import { FaDiscord, FaGithub } from "react-icons/fa";
import { goto } from "lib/blueprint";
import { useRouter } from "next/router";
import { Link } from "components/ui/link";

export default function NavBar() {
    const router = useRouter()
    return (
        <Navbar className="mb-6 !px-[0px]">
            <Container>
                <Navbar.Group align={Alignment.LEFT}>
                    <Navbar.Heading className="flex items-center space-x-2">
                        <span className="font-bold">Fairu</span>
                        <Tag intent="warning" minimal round>Alpha</Tag>
                    </Navbar.Heading>
                    <Button onClick={goto(router, "/")} className="bp4-minimal" icon="home" text="Home" />
                </Navbar.Group>
                <Navbar.Group align={Alignment.RIGHT}>
                    <UserNavigation />
                    <Navbar.Divider />
                    <Link href="https://github.com/melike2d/fairu" title="github"  className="bp4-minimal" icon={FaGithub}  />
                    <Link href="https://2d.gay/discord"            title="discord" className="bp4-minimal" icon={FaDiscord} />
                </Navbar.Group>
            </Container>
            {/* </div> */}
        </Navbar>
    )
}