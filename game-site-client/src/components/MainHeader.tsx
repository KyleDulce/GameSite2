import React from "react";
import { AppBar, Toolbar, Container, Box, Menu, MenuItem, Tooltip, Button, IconButton, Avatar } from "@mui/material";
import { Link as RouterLink } from 'react-router-dom';

import AutoFixHighIcon from '@mui/icons-material/AutoFixHigh';
import MenuIcon from '@mui/icons-material/Menu';
import avatarPic from "../resources/temporary/avatar.png";
import "./MainHeader.css";

class NavLink {
    readonly text: string;
    readonly linkValue: string;

    constructor(text: string, value: string) {
        this.text = text;
        this.linkValue = value;
    }
}

const links: NavLink[]  = [
    new NavLink("Games", "/games"),
    new NavLink("Create Game", "/create")
];

export default function MainHeader() {

    const [avatarMenuOpen, setAvatarMenuOpen] = React.useState<null | HTMLElement>();
    const [sideMenuOpen, setSideMenuOpen] = React.useState<null | HTMLElement>();
    const [playerLogged, setPlayerLogged] = React.useState<Boolean>();

    function handleAvatarMenuOpen(event: React.MouseEvent<HTMLElement>) {
        setAvatarMenuOpen(event.currentTarget);
    }

    function handleAvatarMenuClose() {
        setAvatarMenuOpen(null);
    }

    function handleSideMenuOpen(event: React.MouseEvent<HTMLElement>) {
        setSideMenuOpen(event.currentTarget);
    }

    function handleSideMenuClose() {
        setSideMenuOpen(null);
    }

    return (
        <AppBar position="static">
            <Container maxWidth="xl">
                <Toolbar disableGutters>

                    {/* Visible when Normal */}
                    <Box sx={{mr: 1, flexGrow: 1, display: {xs: "none", md:"flex"}}}>
                        <AutoFixHighIcon sx={{mr: 1}} />

                        <Box>
                            {links.map((page) => (
                                <Button sx={{color: 'white'}} component={RouterLink} to={page.linkValue}>
                                    {page.text}
                                </Button>
                            ))}
                        </Box>
                    </Box>

                    {/* Visible when Small */}
                    <Box sx={{mr: 1, flexGrow: 1, display: {xs: "flex", md:"none"}}}>
                        <IconButton onClick={handleSideMenuOpen}>
                             <MenuIcon />
                        </IconButton>
                        <Menu
                            anchorEl={sideMenuOpen}
                            anchorOrigin={{
                                vertical: 'bottom',
                                horizontal: 'left',
                            }}
                            keepMounted
                            transformOrigin={{
                                vertical: 'top',
                                horizontal: 'left',
                            }}
                            open={Boolean(sideMenuOpen)}
                            onClose={handleSideMenuClose}
                            sx={{
                                display: { xs: 'block', md: 'none' },
                            }}
                        >
                            {links.map((page) => (
                                <MenuItem component={RouterLink} to={page.linkValue}>
                                    {page.text}
                                </MenuItem >
                            ))}
                        </Menu>
                        <AutoFixHighIcon />
                    </Box>

                    <Box sx={{mr: 0, flexGrow: 0}}>
                        {
                            playerLogged ? 
                            <>
                                <Tooltip title="Open Profile">
                                    <IconButton onClick={handleAvatarMenuOpen}>
                                        <Avatar alt="temp" src={avatarPic}></Avatar>
                                    </IconButton>
                                </Tooltip>
                                <Menu
                                    sx={{ mt: '45px' }}
                                    anchorEl={avatarMenuOpen}
                                    anchorOrigin={{
                                        vertical: 'top',
                                        horizontal: 'right',
                                    }}
                                    keepMounted
                                    transformOrigin={{
                                        vertical: 'top',
                                        horizontal: 'right',
                                    }}
                                    open={Boolean(avatarMenuOpen)}
                                    onClose={handleAvatarMenuClose}
                                >
                                    <MenuItem>
                                        Logout
                                    </MenuItem >
                                </Menu> 
                            </>
                            :
                            <>
                                <Button sx={{color: 'white'}} component={RouterLink} to="/createAccount">
                                    Create an Account
                                </Button>
                            </>
                        }
                        
                    </Box>
                </Toolbar>
            </Container>
        </AppBar>
    );
}
