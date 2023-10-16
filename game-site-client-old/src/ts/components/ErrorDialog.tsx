import { Button, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle } from "@mui/material";

export default function ErrorDialog({children, open, onOkay}: any) {

    function handleOnClose() {
        onOkay();
    }

    return (
        <Dialog open={open}>
            <DialogTitle>Opps</DialogTitle>
            <DialogContent>
                <DialogContentText>
                    {children}
                </DialogContentText>
                <DialogActions>
                    <Button onClick={handleOnClose}>Okay</Button>
                </DialogActions>
            </DialogContent>
        </Dialog>
    );
}