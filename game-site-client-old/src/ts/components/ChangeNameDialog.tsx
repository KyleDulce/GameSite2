import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, TextField, TextFieldProps } from "@mui/material";
import { useRef, useState } from "react";

interface ChangeNameProps {
    open: boolean,
    onSubmit?(providedName: string): void,
    onCancel?(): void
}

export default function ChangeNameDialog({open, onSubmit, onCancel}: ChangeNameProps) {
    const textFieldRef = useRef<TextFieldProps>(null);
    const [hasError, setHasError] = useState(false);

    function handleOnClose() {
        //do nothing
    }
    function handleOnCancel() {
        if(onCancel){
            onCancel();
            setHasError(false);
        }
    }
    function handleOnSubmit() {
        if(textFieldRef.current == null) {
            setHasError(true);
        } else if(textFieldRef.current.value == null) {
            setHasError(true);           
        } else if((textFieldRef.current.value as string).length === 0) {
            setHasError(true);
        } else if(onSubmit) {
            onSubmit(textFieldRef.current?.value as string);
            setHasError(false);
        }
    }
    return (
        <Dialog open={open} onClose={handleOnClose}>
            <DialogTitle>Change Name</DialogTitle>
            <DialogContent>
                <DialogContentText>
                    Change how you appear in game!
                </DialogContentText>
                <TextField
                    autoFocus
                    label="Name"
                    variant="outlined"
                    inputRef={textFieldRef}
                    fullWidth
                    helperText="Name cannot be empty"
                    error={hasError}
                />
                <DialogActions>
                    <Button onClick={handleOnCancel}>Cancel</Button>
                    <Button onClick={handleOnSubmit}>Submit</Button>
                </DialogActions>
            </DialogContent>
        </Dialog>
    )
}