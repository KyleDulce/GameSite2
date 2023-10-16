import { Button, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow } from '@mui/material';
import { Link as RouterLink, createSearchParams, useNavigate } from 'react-router-dom';
import { RestStatusCode, RoomListing } from "../model/RestProtocolModels";
import "./ActiveGames.scss";
import { useContext, useEffect, useState } from 'react';
import RestService from '../services/RestService';
import { ConfigContext } from '../model/ConfigOptions';
import { restCallOnUnauthorized } from '../services/DevModeService';
import ErrorDialog from '../components/ErrorDialog';

const ROOM_CAPACITY_WARNING_PERCENT = 0.6;
 
export default function ActiveGames() {
    const [roomList, setRoomList] = useState<RoomListing[]>([]);
    const [errorDialog, setErrorDialog] = useState(false);
    const configContext = useContext(ConfigContext);
    const navigate = useNavigate();

    useEffect(() => {
        loadRooms();
    }, []);

    function loadRooms() {
        RestService.getRoomLists(configContext)
        .then(result => {
            if(!result.data) {
                console.error("Failed to load rooms");
            }
            setRoomList(result.data);
        }).catch(error => {
            if (error.statusCode === RestStatusCode.UNAUTHORIZED) {
                restCallOnUnauthorized(configContext, navigate);
                return;
            }
            console.error("Failed to load rooms");
            console.error(error);
        });
    }

    function getNumPlayersClass(listing: RoomListing) {
        const percent = listing.lobbySize / listing.maxLobbySize;
        if(percent >= 1) {
            return "room-full";
        } else if(percent >= ROOM_CAPACITY_WARNING_PERCENT) {
            return "room-warning";
        } else {
            return "room-empty";
        }
    }

    function onDialogClose() {
        setErrorDialog(false);
    }

    function joinRoom(asSpectator: boolean, roomListing: RoomListing) {
        RestService.postJoinRoom({
            asSpectator: asSpectator,
            roomId: roomListing.roomId
        }, configContext)
        .then((response) => {
            if(response.statusCode !== RestStatusCode.OK) {
                console.warn("Failed to join room per bad request");
                setErrorDialog(true);    
                return;
            }
            if(!response.data.success) {
                console.log("Failed to join room");
                setErrorDialog(true);
                return;
            }
            navigate(
                {
                    pathname: "/play",
                    search: createSearchParams({
                        id: roomListing.roomId
                    }).toString()
                },
                {
                    state: {
                        asPlayer: !asSpectator,
                        asHost: response.data.isHost
                    }
                }
            );
        })
    }

    return (
        <div className="rooms-container">
            <div className="active-games-header-container">
                <div className="active-games-header-title">
                    <h1>Games</h1>
                </div>
                <div className="active-games-header-button">
                    <Button variant="contained" color="primary" onClick={loadRooms}>Refresh</Button>
                    <Button variant="contained" color="primary" component={RouterLink} to="/createroom">Create Room</Button>
                </div>
            </div>
            <TableContainer className="active-games-table-container" component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Room Id</TableCell>
                            <TableCell>Room Name</TableCell>
                            <TableCell>Host</TableCell>
                            <TableCell>Number of Players</TableCell>
                            <TableCell>Game</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Play</TableCell>
                            <TableCell>Spectate</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        { roomList.length > 0 ? 
                            roomList.map((listing) => (
                                <TableRow>
                                    <TableCell>{listing.roomId.substring(0,5)}</TableCell>
                                    <TableCell>{listing.roomName}</TableCell>
                                    <TableCell>{listing.hostName}</TableCell>
                                    <TableCell>
                                        <span className={getNumPlayersClass(listing)}>
                                            {listing.lobbySize} / {listing.maxLobbySize}
                                        </span>
                                    </TableCell>
                                    <TableCell>{listing.gameType}</TableCell>
                                    <TableCell >
                                        <span className={listing.inProgress ? 'game-in-progress' : 'game-not-progress'}>
                                            {listing.inProgress ? "In Progress" : "Waiting"}
                                        </span>
                                    </TableCell>
                                    <TableCell><Button color="primary" variant="contained" onClick={() => joinRoom(false, listing)} disabled={listing.inProgress}>Join Game</Button></TableCell>
                                    <TableCell><Button color="primary" variant="contained" onClick={() => joinRoom(true, listing)}>Spectate Game</Button></TableCell>
                                </TableRow>     
                            )) : (
                                <h1>There are no active rooms</h1>
                            )
                        }
                    </TableBody>
                </Table>
            </TableContainer>
            <ErrorDialog open={errorDialog} onOkay={onDialogClose}>Something went wrong. Try again later</ErrorDialog>
        </div>
    );
}