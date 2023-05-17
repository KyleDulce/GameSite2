import { Avatar, Badge, IconButton } from '@mui/material';
import ChatIcon from '@mui/icons-material/Chat';
import SettingsIcon from '@mui/icons-material/Settings';
import CloseIcon from '@mui/icons-material/Close';
import { useState } from 'react';
import SettingBar from 'src/ts/components/SettingBar';
import './PlayGame.scss';
import ChatBar from 'src/ts/components/ChatBar';
import { GameWindow, getGameWindow } from '../services/GameLoadingService';
import { GameType } from '../model/SystemConstants';

const isHost = true;
enum SideBarState {
    CHAT, SETTINGS, CLOSED
}

const gameWindow: GameWindow = getGameWindow(GameType.JOIN_ROOM);

export default function PlayGame() {
    const [sidebarState, setSidebarState] = useState(SideBarState.CLOSED);

    function handleButtonPress(state: SideBarState): void {
        if(state === SideBarState.CLOSED || sidebarState === state) {
            setSidebarState(SideBarState.CLOSED);
        } else {
            setSidebarState(state);
        }
        console.log(sidebarState === SideBarState.CLOSED);
    }

    return (
        <div className="play-games-container">
            <div className="play-games-options">
                {isHost && (
                    <IconButton onClick={() => handleButtonPress(SideBarState.SETTINGS)} className='play-games-option-button' component={Avatar}>
                        <SettingsIcon/>
                    </IconButton>
                )}
                <IconButton onClick={() => handleButtonPress(SideBarState.CHAT)} className='play-games-option-button' component={Avatar}>
                    <Badge badgeContent="5" color='secondary'>
                        <ChatIcon/>
                    </Badge>
                </IconButton>
            </div>
            <div className='play-games-content'>
                <div className='play-games-outlet'>
                    <gameWindow.page/>
                </div>
                {sidebarState !== SideBarState.CLOSED && (
                    <div className='play-games-sidebar'>
                        <div className='play-games-sidebar-header'>
                            <IconButton onClick={() => handleButtonPress(SideBarState.CLOSED)}><CloseIcon/></IconButton>
                        </div>
                        <hr/>
                        {sidebarState === SideBarState.CHAT && (
                            <ChatBar/>
                        )}
                        {sidebarState === SideBarState.SETTINGS && isHost && (
                            <SettingBar><gameWindow.settingPanel/></SettingBar>
                        )}
                    </div>    
                )}
            </div>
        </div>
    )
}
