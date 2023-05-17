import { Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';
import { Link as RouterLink } from 'react-router-dom';
import { RoomListing } from "../model/RestProtocolModels";
import { GameType } from "../model/SystemConstants";
import "./ActiveGames.scss";

const roomListings: RoomListing[] = [
    {
        roomId: "fakeId1",
        lobbySize: 4,
        maxLobbySize: 10,
        spectatorAmount: 2,
        gameType: GameType.JOIN_ROOM, 
        hostName: "Person",
        inProgress: false,
        gameStartTime: -1
    },
    {
        roomId: "fakeId2",
        lobbySize: 6,
        maxLobbySize: 6,
        spectatorAmount: 1,
        gameType: GameType.JOIN_ROOM, 
        hostName: "Person2",
        inProgress: true,
        gameStartTime: 1684003358540
    },
    {
        roomId: "fakeId3",
        lobbySize: 1,
        maxLobbySize: 5,
        spectatorAmount: 0,
        gameType: GameType.JOIN_ROOM, 
        hostName: "Person3",
        inProgress: false,
        gameStartTime: -1
    }
]

const WARNING_START_PERCENT = 0.6;
 
export default function ActiveGames() {
    return (
        <div className="rooms-container">
            <div className="active-games-header-container">
                <div className="active-games-header-title">
                    <h1>Games</h1>
                </div>
                <div className="active-games-header-button">
                    <Button variant="contained" color="primary" component={RouterLink} to="/createroom">Create Room</Button>
                </div>
            </div>
            <TableContainer className="active-games-table-container" component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Room Id</TableCell>
                            <TableCell>Room Name</TableCell>
                            <TableCell>Number of Players</TableCell>
                            <TableCell>Game</TableCell>
                            <TableCell>Play</TableCell>
                            <TableCell>Spectate</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {
                            roomListings.map((listing) => (
                                <TableRow>
                                    <TableCell>{listing.roomId}</TableCell>
                                    <TableCell>{listing.hostName}</TableCell>
                                    <TableCell>{listing.lobbySize} / {listing.maxLobbySize}</TableCell>
                                    <TableCell>{listing.gameType}</TableCell>
                                    <TableCell><Button color="primary" variant="contained">Join Game</Button></TableCell>
                                    <TableCell><Button color="primary" variant="contained">Spectate Game</Button></TableCell>
                                </TableRow>     
                            ))
                        }
                    </TableBody>
                </Table>
            </TableContainer>
        </div>
    );
}