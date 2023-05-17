import { Button, FormControl, IconButton, InputAdornment, InputLabel, OutlinedInput, TextField } from '@mui/material';
import './LoginPage.scss';
import { useState } from 'react';
import { useNavigate, useLocation, redirect } from 'react-router-dom';
import Visibility from '@mui/icons-material/Visibility';
import VisibilityOff from '@mui/icons-material/VisibilityOff';

export default function LoginPage(props: any) {
    const [shouldShowPassword, setShouldShowPassword] = useState(false);
    const navigate = useNavigate();
    const location = useLocation();

    function handleShowPassword() {
        setShouldShowPassword(!shouldShowPassword);
    }

    function navigateBack() {
        if(location.key !== 'default') {
            navigate(-1);
        } else {
            navigate('/');
        }
    }

    return (
        <div className="login-container">
            <div className="login-box">
                <h2 className='login-title'>Login With Id</h2>
                <hr/>
                <div className='login-form'>
                    <TextField className='login-field-account' label='Account Id' variant='outlined'/>
                    <FormControl variant='outlined' className='login-field-password'>
                        <InputLabel htmlFor='login-field-password'>Password</InputLabel>
                        <OutlinedInput id='login-field-password' type={shouldShowPassword? 'text' : 'password'} 
                            endAdornment={
                                <InputAdornment position='end'>
                                    <IconButton onClick={handleShowPassword} onMouseDown={event => event.preventDefault()} edge='end'>  
                                        {shouldShowPassword ? <VisibilityOff/> : <Visibility/>}
                                    </IconButton>
                                </InputAdornment>
                            }
                        />
                    </FormControl>
                    <Button className='login-button-submit' variant='contained'>Login</Button>
                </div>
                
            </div>
        </div>
    );
}