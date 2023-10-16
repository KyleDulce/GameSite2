import { Avatar, Badge, Button, CircularProgress, Dialog, DialogContent, DialogTitle, IconButton } from '@mui/material';
import ChatIcon from '@mui/icons-material/Chat';
import SettingsIcon from '@mui/icons-material/Settings';
import CloseIcon from '@mui/icons-material/Close';
import { Suspense, useState, useContext, useEffect, createContext, useRef } from 'react';
import SettingBar from 'src/ts/components/SettingBar';
import './PlayGame.scss';
import ChatBar from 'src/ts/components/ChatBar';
import { GameWindow, getGameWindowById } from '../services/GameLoadingService';
import { Navigate, useLocation, useNavigate } from 'react-router';
import { useSearchParams } from 'react-router-dom';
import { ConfigContext } from '../model/ConfigOptions';
import { ErrorBoundary } from 'react-error-boundary';
import GeneralError from '../components/GeneralError';
import RestService from '../services/RestService';
import ErrorDialog from '../components/ErrorDialog';
import { RestStatusCode } from '../model/RestProtocolModels';
import { restCallOnUnauthorized } from '../services/DevModeService';
import SocketService from '../services/SocketService';
import GameSocketService from '../services/GameSocketService';
import { filter, tap } from 'rxjs';
import { CommonGameTypeStrings } from '../model/SocketModel';

export const GameSocketContext = createContext<GameSocketService | null>(null);

enum SideBarState {
    CHAT, SETTINGS, CLOSED
}

export default function PlayGame() {
    const [sidebarState, setSidebarState] = useState(SideBarState.CLOSED);
    const [open, setOpen] = useState(false);
    const [failedJoinOpen, setFailedJoinOpen] = useState(false);
    const [playerSetup, setPlayerSetup] = useState(false);
    const [isPlayer, setIsPlayer] = useState(false); // if a player or spectator
    const [isHost, setIsHost] = useState(false);
    const [gameWindow, setGameWindow] = useState<GameWindow | null>(null);
    const configContext = useContext(ConfigContext);
    const [searchParams, setSearchParams] = useSearchParams();
    const [socketConnection, setSocketConnection] = useState<GameSocketService | null>(null);
    const [genericSocketConnection, setGenericSocketConnection] = useState<SocketService | null>(null);
    const socketRef = useRef<{socketConnection: GameSocketService | null, genericSocketConnection: SocketService | null} | null>(null);
    const location = useLocation();
    const navigate = useNavigate();

    const gameId = searchParams.get("id");
    const asPlayerRaw: boolean | null = location.state.asPlayer;
    const asHostRaw: boolean | null = location.state.asHost;

    useEffect(() => {
        loadGameWindow();

        if(asPlayerRaw == null || asHostRaw == null) {
            setOpen(true);
        } else {
            setPlayerSetup(true);
            setIsPlayer(asPlayerRaw);
            setIsHost(asHostRaw);
            setupSocketConnection()
        }
        const interval = setInterval(() => {
            RestService.getRefreshToken(configContext);
        }, Number(process.env.REACT_APP_GAME_REFRESH_TOKEN_INTERVAL_SECONDS || "1800") * 1000);

        return () => {
            clearInterval(interval);
            console.log("run");
            console.log(socketRef);
            console.log(socketRef.current?.genericSocketConnection)
            console.log(socketRef.current)
            console.log(socketRef)
            if(socketRef.current?.genericSocketConnection) {
                console.log("gen");
                socketRef.current.genericSocketConnection.disconnect();
            }
            if(socketRef.current?.socketConnection) {
                console.log("non");
                socketRef.current.socketConnection.disconnect();
            }
        }
    }, []);

    useEffect(() => {
        console.log("run2")
        socketRef.current = {
            socketConnection: socketConnection,
            genericSocketConnection: genericSocketConnection
        };
    }, [socketConnection, genericSocketConnection]);

    if(gameId == null) {
        return <Navigate to={"/rooms"} replace={true}/>
    }

    function loadGameWindow() {
        if (gameId != null) {
            setGameWindow(getGameWindowById(gameId, configContext));
        }
    }

    function handleButtonPress(state: SideBarState): void {
        if(state === SideBarState.CLOSED || sidebarState === state) {
            setSidebarState(SideBarState.CLOSED);
        } else {
            setSidebarState(state);
        }
    }

    function onTypeSelect(type: boolean) {
        RestService.postJoinRoom({
            asSpectator: !type,
            roomId: gameId as string
        }, configContext)
        .then(response => {
            if(response.statusCode !== RestStatusCode.OK) {
                console.warn("Failed to join room per bad request");
                setFailedJoinOpen(true);    
                return;
            }
            if(!response.data.success) {
                console.log("Failed to join room");
                setFailedJoinOpen(true);
                return;
            }
            setIsPlayer(type);
            setPlayerSetup(true);
            setIsHost(response.data.isHost);
            setupSocketConnection()
        })
        .catch(error => {
            if (error.statusCode === RestStatusCode.UNAUTHORIZED) {
                restCallOnUnauthorized(configContext, navigate);
                return;
            }
            console.error("Failed to join room " + error);
            setFailedJoinOpen(true);
        });
    }

    function setupSocketConnection() {
        const socketService = SocketService.createConnection();
        const gameSocketService = GameSocketService.createGameConnection(gameId as string, socketService);
        console.log("created");
        setSocketConnection(gameSocketService);
        setGenericSocketConnection(socketService);
        // React strict mode messes with states so they do not trigger the effect
        socketRef.current = {
            socketConnection: socketConnection,
            genericSocketConnection: genericSocketConnection
        };
        console.log(socketRef);
        console.log(socketRef.current);
        console.log("end")
        if(!isHost && isPlayer) {
            gameSocketService.privateMessages.pipe(
                filter(message => message.gameDataIdString === CommonGameTypeStrings.HOST_CHANGE_DATA ||
                    message.gameDataIdString === CommonGameTypeStrings.FORCE_KICK_DATA),
                tap(message => {
                    if(message.gameDataIdString === CommonGameTypeStrings.HOST_CHANGE_DATA) {
                        setIsHost(true);
                    } else if(message.gameDataIdString === CommonGameTypeStrings.FORCE_KICK_DATA) {
                        navigate("/rooms");
                    }
                })
            ).subscribe();
        }
        gameSocketService.unauthorizedMessages
        .pipe(
            tap(() => {
                restCallOnUnauthorized(configContext, navigate);
            })
        ).subscribe();
    }

    function onFailedOkay() {
        navigate("/rooms");
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
                <GameSocketContext.Provider value={socketConnection}>
                    <div className='play-games-content'>
                        <div className='play-games-outlet'>
                            {
                                playerSetup && gameWindow && (
                                    <ErrorBoundary fallback={<GeneralError />}>
                                        <Suspense fallback={<CircularProgress />} >
                                            <gameWindow.page isPlayer={isPlayer}/>
                                        </Suspense>
                                    </ErrorBoundary>
                                )
                            }
                            
                        </div>
                        {sidebarState !== SideBarState.CLOSED && (
                            <div className='play-games-sidebar'>
                                <div className='play-games-sidebar-header'>
                                    <IconButton onClick={() => handleButtonPress(SideBarState.CLOSED)}><CloseIcon/></IconButton>
                                </div>
                                <hr/>
                                {sidebarState === SideBarState.CHAT && playerSetup && (
                                    <ChatBar/>
                                )}
                                {sidebarState === SideBarState.SETTINGS && isHost && playerSetup && gameWindow && (
                                    <ErrorBoundary fallback={<GeneralError />}>
                                        <Suspense fallback={<CircularProgress />} >
                                            <SettingBar><gameWindow.settingPanel/></SettingBar>
                                        </Suspense>
                                    </ErrorBoundary>
                                )}
                            </div>    
                        )}
                    </div>
                </GameSocketContext.Provider>
            <Dialog open={open}>
                <DialogTitle>How do you want to join the game?</DialogTitle>
                <DialogContent>
                    <Button onClick={() => onTypeSelect(true)}>As Player</Button>
                    <Button onClick={() => onTypeSelect(false)}>As Spectator</Button>
                </DialogContent>
            </Dialog>
            <ErrorDialog open={failedJoinOpen} onOkay={onFailedOkay}>Failed To join the room</ErrorDialog>
        </div>
    )
}
