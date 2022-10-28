import { Alignment, AnchorButton, Navbar, Position, Tag } from "@blueprintjs/core";
import Container from "components/container";
import { frontendUrl } from "lib/contants";
import UserNavigation from "./user";
import { FaDiscord, FaGithub } from "react-icons/fa";
import { Tooltip2 } from "@blueprintjs/popover2";

export default function NavBar() {
    return (
        <Navbar className="mb-6 !px-[0px]">
            {/* <div style={{ margin: '0 auto', width: '1000px' }}> */}
            <Container>
                <Navbar.Group align={Alignment.LEFT}>
                    <Navbar.Heading className="flex items-center space-x-2">
                        <span className="font-bold">Fairu</span>
                        {/* <Tooltip2 content="There may be bugs!" position={Position.BOTTOM} openOnTargetFocus={false}> */}
                            <Tag intent="warning" minimal round>Alpha</Tag>
                        {/* </Tooltip2> */}
                    </Navbar.Heading>
                    <AnchorButton href={frontendUrl} className="bp4-minimal" icon="home" text="Home" />
                </Navbar.Group>
                <Navbar.Group align={Alignment.RIGHT}>
                    <UserNavigation />
                    <Navbar.Divider />
                    <AnchorButton href="https://github.com/melike2d/fairu" className="bp4-minimal" icon={<FaGithub />} />
                    <AnchorButton href="https://2d.gay/discord" className="bp4-minimal" icon={<FaDiscord />} />
                </Navbar.Group>
            </Container>
            {/* </div> */}
        </Navbar>
    )
}