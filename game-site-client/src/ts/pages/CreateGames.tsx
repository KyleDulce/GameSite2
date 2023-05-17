import { Autocomplete, Box, TextField, Button } from '@mui/material';
import './CreateGames.scss';
import { validGameTypes } from '../model/SystemConstants';

export default function CreateGames() {
    return (
        <div className="create-games-container">
            <h1>Create Game</h1>
            <Box>
                <TextField id="room-name" className='create-games-textbox' label="Room Name" variant='filled' required/>
                {/* <TextField id="room-name" className='create-games-textbox' label="Room Name" variant='filled' required/> */}
                <Autocomplete 
                    id="gametype" 
                    className='create-games-textbox' 
                    autoHighlight
                    options={validGameTypes}
                    renderInput={params => (
                        <TextField {...params} variant='filled' label="Game Type" required/>
                    )}
                />
                <TextField id="player-count" className='create-games-textbox' label="Max Players" type='number' defaultValue={10} variant='filled' required/>
                <Button variant='contained' color="primary">Create Room</Button>
            </Box>
            
        </div>
    );
}
