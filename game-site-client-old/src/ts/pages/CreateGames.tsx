import { Autocomplete, Box, TextField, Button } from '@mui/material';
import './CreateGames.scss';
import { GameType, validGameTypes } from '../model/SystemConstants';
import { useContext, useRef, useState } from 'react';
import RestService from '../services/RestService';
import { ConfigContext } from '../model/ConfigOptions';
import ErrorDialog from '../components/ErrorDialog';
import { useNavigate } from 'react-router';
import { RestError, RestStatusCode } from '../model/RestProtocolModels';
import { createSearchParams } from 'react-router-dom';
import { restCallOnUnauthorized } from '../services/DevModeService';

export default function CreateGames() {
    const [errorStateRoom, setErrorStateRoom] = useState(false);
    const [errorStateType, setErrorStateType] = useState(false);
    const [errorStateMax, setErrorStateMax] = useState(false);
    const [errorDialog, setDialog] = useState(false);
    const configContext = useContext(ConfigContext);
    const roomNameRef = useRef();
    const gameTypeRef = useRef();
    const maxPlayerRef = useRef();
    const navigate = useNavigate();

    function onSubmit() {
        const accountStr = (roomNameRef.current as any)?.value as string;
        const gameTypeStr = (gameTypeRef.current as any)?.value as string;
        const maxPlayerNum = Number((maxPlayerRef.current as any)?.value);

        if(!accountStr) {
            setErrorStateRoom(true);
        } else {
            setErrorStateRoom(false);
        }

        if(!gameTypeStr || gameTypeStr == GameType.NULL) {
            setErrorStateType(true);
        } else {
            setErrorStateType(false);
        }

        if(Number.isNaN(maxPlayerNum) || maxPlayerNum <= 1) {
            setErrorStateMax(true);
        } else {
            setErrorStateMax(false);
        }

        if(errorStateRoom || errorStateType || errorStateMax) {
            return;
        }

        if(!configContext.Uid || !configContext.PlayerName) {
            console.error("Something went wrong!");
            return;
        }

        RestService.postCreateRoom({
            gameType: GameType[gameTypeStr as keyof typeof GameType],
            host: {
                uuid: configContext.Uid,
                name: configContext.PlayerName,
                isGuest: false
            },
            maxLobbySize: maxPlayerNum
        }, configContext)
        .then(response => {
            if(response.statusCode != RestStatusCode.OK || !response.data.success) {
                setDialog(true);    
                return;
            }
            navigate(
                {
                    pathname: "/play",
                    search: createSearchParams({
                        id: response.data.roomId
                    }).toString()
                },
                {
                    state: {
                        asPlayer: true,
                        asHost: true
                    }
                }
            );
        }).catch((error: RestError) => {
            if (error.statusCode === RestStatusCode.UNAUTHORIZED) {
                restCallOnUnauthorized(configContext, navigate);
                return;
            }
            setDialog(true);
            console.error(error);
        })
    }

    function onDialogClose() {
        setDialog(false);
    }

    return (
        <div className="create-games-container">
            <h1>Create Game</h1>
            <Box>
                <TextField 
                    id="room-name" 
                    className='create-games-textbox' 
                    label="Room Name" 
                    variant='filled' 
                    error={errorStateRoom}
                    inputRef={roomNameRef} 
                    required/>
                {/* <TextField id="room-name" className='create-games-textbox' label="Room Name" variant='filled' required/> */}
                <Autocomplete 
                    id="gametype" 
                    className='create-games-textbox' 
                    autoHighlight
                    options={validGameTypes}
                    renderInput={params => (
                        <TextField {...params} variant='filled' label="Game Type" error={errorStateType} inputRef={gameTypeRef} required/>
                    )}
                    
                />
                <TextField 
                    id="player-count" 
                    className='create-games-textbox' 
                    label="Max Players" 
                    type='number' 
                    defaultValue={10} 
                    variant='filled' 
                    inputRef={maxPlayerRef}
                    error={errorStateMax} 
                    helperText={errorStateMax && "You must have at least 2 players in the room"}
                    required/>
                <Button variant='contained' color="primary" onClick={onSubmit}>Create Room</Button>
            </Box>
            <ErrorDialog open={errorDialog} onOkay={onDialogClose}>Something went wrong. Try again later</ErrorDialog>
        </div>
    );
}
