import { Button, CircularProgress, FormControl, FormHelperText, IconButton, InputAdornment, InputLabel, OutlinedInput, TextField } from '@mui/material';
import './LoginPage.scss';
import { useRef, useState } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';
import RestService from '../services/RestService';
import { ConfigContext } from '../model/ConfigOptions';
import React from 'react';
import { SHA256 } from 'crypto-js';
import { RestError, RestStatusCode } from '../model/RestProtocolModels';
import ErrorDialog from '../components/ErrorDialog';

export default function LoginPage(props: any) {
    const [shouldShowPassword, setShouldShowPassword] = useState(false);
    const [disableButton, setDisableButton] = useState(false);
    const [errorState, setErrorState] = useState(false);
    const [errorDialog, setDialog] = useState(false);
    const configuration = React.useContext(ConfigContext);
    const navigate = useNavigate();
    const location = useLocation();
    const accountRef = useRef();
    const passwordRef = useRef();

    function handleShowPassword() {
        setShouldShowPassword(!shouldShowPassword);
    }

    function navigateBack() {
        console.log(location.key);
        if(location.key !== 'default') {
            navigate(-1);
        } else {
            navigate('/');
        }
    }
    
    function onFormSubmit() {
        setDisableButton(true);
        const accountStr = (accountRef.current as any)?.value;
        const passwordStr = (passwordRef.current as any)?.value;

        if((!accountStr) || (!passwordStr)) {
            setErrorState(true);
            setDisableButton(false);
            return;
        }

        const hashedPassword = SHA256(passwordStr).toString();
        
        RestService.postAuth({
            login: accountStr,
            passHash: hashedPassword
        }, configuration)
        .then(response => {
            if(response.data.success === true && response.statusCode === RestStatusCode.OK) {
                navigateBack();
            } else {
                setErrorState(true);
                setDisableButton(false);
            }
        }).catch((error: RestError) => {
            if(error.statusCode === RestStatusCode.UNAUTHORIZED) {
                setErrorState(true);
            } else {
                setDialog(true);
            }
            setDisableButton(false);
            console.error(error);
        })
    }

    function onDialogClose() {
        setDialog(false);
    }

    return (
        <div className="login-container">
            <div className="login-box">
                <h2 className='login-title'>Login With Id</h2>
                <hr/>
                <div className='login-form'>
                    <TextField 
                        className='login-field-account' 
                        label='Account Id' 
                        variant='outlined'
                        inputRef={accountRef}
                        error={errorState}
                        required
                        autoComplete='username'
                        />
                    <FormControl variant='outlined' className='login-field-password' error={errorState} required>
                        <InputLabel htmlFor='login-field-password'>Password</InputLabel>
                        <OutlinedInput id='login-field-password' type={shouldShowPassword? 'text' : 'password'} 
                            label="Password"
                            endAdornment={
                                <InputAdornment position='end'>
                                    <IconButton onClick={handleShowPassword} onMouseDown={event => event.preventDefault()} edge='end'>  
                                        {shouldShowPassword ? <VisibilityOff/> : <Visibility/>}
                                    </IconButton>
                                </InputAdornment>
                            }
                            inputRef={passwordRef}
                            autoComplete='current-password'
                        />
                        <FormHelperText hidden={!errorState}>Invalid Account or Password</FormHelperText>
                    </FormControl>
                    <span>
                        <Button className='login-button-submit' variant='contained' onClick={onFormSubmit} disabled={disableButton}>Login</Button>
                        {disableButton &&
                            <CircularProgress />
                        }
                        
                    </span>
                </div>
            </div>
            <ErrorDialog open={errorDialog} onOkay={onDialogClose}>Something went wrong. Try again later</ErrorDialog>
        </div>
    );
}