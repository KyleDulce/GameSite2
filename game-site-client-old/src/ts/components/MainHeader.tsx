import React, { useEffect } from "react";
import { ResponsiveContext } from "src/App";
import { AppBar, Toolbar, Container, Box, Menu, MenuItem, Tooltip, Button, IconButton, useTheme } from "@mui/material";
import { Link as RouterLink, useNavigate } from 'react-router-dom';

import AutoFixHighIcon from '@mui/icons-material/AutoFixHigh';
import MenuIcon from '@mui/icons-material/Menu';
import "./MainHeader.scss";
import LightModeIcon from '@mui/icons-material/WbSunny';
import DarkModeIcon from '@mui/icons-material/ModeNight'
import { ConfigContext } from "../model/ConfigOptions";
import { saveConfigOptions } from "../services/StorageService";
import ChangeNameDialog from "./ChangeNameDialog";
import RestService from "../services/RestService";

class NavLink {
    readonly text: string;
    readonly linkValue: string;

    constructor(text: string, value: string) {
        this.text = text;
        this.linkValue = value;
    }
}

const links: NavLink[]  = [
    new NavLink("Games", "/rooms"),
    new NavLink("Create Game", "/createroom")
];

export default function MainHeader() {

    const [avatarMenuOpen, setAvatarMenuOpen] = React.useState<null | HTMLElement>();
    const [sideMenuOpen, setSideMenuOpen] = React.useState<null | HTMLElement>();
    const [changeNameDialogOpen, setChangeNameDialogOpen] = React.useState(false);

    const { PlayerName, UseLightMode, setUseLightMode } = React.useContext(ConfigContext);
    const fullConfig = React.useContext(ConfigContext);
    const isMobile = React.useContext(ResponsiveContext);

    const theme = useTheme();
    const navigate = useNavigate();

    useEffect(() => {
        saveConfigOptions(fullConfig);
    }, [UseLightMode, PlayerName])

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

    function handleModeToggle(useLight: boolean) {
        setUseLightMode(useLight);
    }

    function handleNameChange(value: string) {
        setChangeNameDialogOpen(false);
    }

    function handleLogout() {
        RestService.deleteAuthToken(fullConfig);

        navigate("/login");
    }

    return (
        <AppBar className="appbar" color='primary' position="static">
            <Container className="header-container" maxWidth={false}>
                <Toolbar disableGutters>

                    { isMobile ? /* MOBILE */
                    (
                        <Box className="header-router-links">
                            <IconButton className="header-logo-button" onClick={handleSideMenuOpen}>
                                <MenuIcon />
                            </IconButton>
                            <Menu
                                className="header-nav-menu"
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
                            >
                                {links.map((page) => (
                                    <MenuItem component={RouterLink} to={page.linkValue}>
                                        {page.text}
                                    </MenuItem >
                                ))}
                            </Menu>
                        <AutoFixHighIcon className="header-logo-button" />
                    </Box>
                    ) : /*DESKTOP*/(
                        <Box className="header-router-links">
                            <IconButton component={RouterLink} to='/'>
                                <AutoFixHighIcon className="header-logo-button"/>
                            </IconButton>

                            <Box>
                                {links.map((page, i) => (
                                    <Button className="header-nav-button" component={RouterLink} to={page.linkValue} key={`headerNav-${i}`}>
                                        {page.text}
                                    </Button>
                                ))}
                            </Box>
                        </Box>
                    )}
                    
                    <Box className="header-account-box">
                        <Box>
                            {UseLightMode ? (
                                    <Tooltip className="header-theme-tooltip" title="Dark Mode">
                                        <IconButton onClick={() => handleModeToggle(false)}>
                                            <DarkModeIcon sx={{color: theme.palette.primary.contrastText}}/>
                                        </IconButton>
                                    </Tooltip>
                                ) : (
                                    <Tooltip className="header-theme-tooltip" title="Light Mode">
                                        <IconButton onClick={() => handleModeToggle(true)}>
                                            <LightModeIcon sx={{color: theme.palette.primary.contrastText}}/>
                                        </IconButton>
                                    </Tooltip>
                                )
                            }
                            <Tooltip className="header-account-tooltip" title="Open Profile">
                                <Button className="header-nav-button" onClick={event => handleAvatarMenuOpen(event)}>{PlayerName}</Button>
                            </Tooltip>
                        </Box>
                        <Menu
                            className="header-account-menu"
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
                            <MenuItem onClick={() => setChangeNameDialogOpen(true)}>Change Name</MenuItem>
                            <MenuItem onClick={handleLogout}>Logout</MenuItem>
                        </Menu>
                    </Box>
                </Toolbar>
            </Container>
            <ChangeNameDialog open={changeNameDialogOpen} onSubmit={handleNameChange} onCancel={() => setChangeNameDialogOpen(false)} />
        </AppBar>
    );
}
