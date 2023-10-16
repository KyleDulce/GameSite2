import { Button, Grid } from '@mui/material';
import { User } from '../model/RestProtocolModels';
import { ConfigContext } from '../model/ConfigOptions';
import { useContext, useEffect, useState } from 'react';

import './SettingBar.scss';
import { GameSocketContext } from '../pages/PlayGame';
import { filter, tap } from 'rxjs';
import { CommonGameTypeStrings, KickPlayerData, SettingsDataResponse } from '../model/SocketModel';

export default function SettingBar({children}: any) {
    const {Uid} = useContext(ConfigContext);
    const socketConnection = useContext(GameSocketContext);

    const [playerList, setPlayerList] = useState<Array<User>>([]);

    useEffect(() => {
        socketConnection?.privateMessages
        .pipe(
            filter(message => message.gameDataIdString === CommonGameTypeStrings.SETTINGS_DATA_RESPONSE),
            tap(message => {
                const settingsDataResposne: SettingsDataResponse = message.data;
                setPlayerList(settingsDataResposne.players);
            })
        ).subscribe();

        socketConnection?.sendGameMessage(CommonGameTypeStrings.SETTINGS_DATA_REQUEST, null);
    }, []);

    function kickPlayer(player: User) {
        const data: KickPlayerData = {
            player: player.uuid
        };

        socketConnection?.sendGameMessage(CommonGameTypeStrings.KICK_PLAYER_DATA, data);
    }
    
    return (
        <div className='setting-bar-container'>
            <div className='setting-bar-playerList'>
                <h3>Player List</h3>
                <Grid container spacing={1}>
                    {playerList.map(player => (
                        <>
                            <Grid item xs={8} className='setting-bar-player-row-name'>
                            <p>{player.name}</p>
                            </Grid>
                            <Grid item xs={4} className='setting-bar-player-row-button'>
                                <Button variant='contained' 
                                    onClick={() => kickPlayer(player)} 
                                    disabled={Uid === player.uuid}>Kick</Button>
                            </Grid>
                        </>
                    ))}
                </Grid>
            </div>
            <hr/>
            {children}
        </div>
    )
}
