import { Button, Grid } from '@mui/material';
import { User } from '../model/RestProtocolModels';
import './SettingBar.scss';

const playerList: User[] = [
    {
        userid: 100,
        name: 'hello',
        isGuest: false
    },
    {
        userid: 101,
        name: 'hello2',
        isGuest: false
    },
    {
        userid: 102,
        name: 'hello3',
        isGuest: false
    }
]

export default function SettingBar({children}: any) {
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
                                <Button variant='contained'>Kick</Button>
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