import { Button, Stack, TextField } from '@mui/material';
import './ChatBar.scss';
import { ChatMessage } from '../model/RestProtocolModels';

const chatMessages: ChatMessage[] = [
    {
        messageText: "Message1",
        senderName: "sender1"
    },
    {
        messageText: "Message2 mmmmm mmmmmmm mmmmm",
        senderName: "sender1"
    },
    {
        messageText: "Message3",
        senderName: "sender1"
    }
]

export default function ChatBar() {
    return (
        <div className='chat-bar-container'>
            <div className='chat-bar-box'>
                <Stack className='chat-bar-content' spacing={0} direction="column-reverse">
                    {chatMessages.map(message => (
                        <span><b>{message.senderName}: </b>{message.messageText}</span> 
                    ))}
                </Stack>
                <div className='chat-bar-input'>
                    <TextField className='chat-bar-input-text' size='small' label="Message" variant='outlined' />
                    <Button variant='contained'>Send</Button>
                </div>
            </div>
            
        </div>
    )
}