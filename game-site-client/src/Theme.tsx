import { PaletteMode } from '@mui/material';
import { ThemeOptions } from '@mui/material/styles';

export const getThemeOptions = (mode: PaletteMode) => ({
  palette: {
    mode,
    ...(
        mode === 'light' ?
        {
            //light
            primary: {
                main: '#053893',
            },
            secondary: {
                main: '#cc9a0e',
            },
            background: {
                paper: '#f5f5f5',
            },
            error: {
                main: '#ce0000',
            },
            warning: {
                main: '#ec6c00',
            },
            success: {
                main: '#007102',
            },
            info: {
                main: '#057d93',
            },
        } : {
            //dark
            primary: {
                main: '#0d52cb',
              },
              secondary: {
                main: '#cc9a0e',
              },
              background: {
                paper: '#272727',
              },
              error: {
                main: '#ce0000',
              },
              warning: {
                main: '#ec6c00',
              },
              success: {
                main: '#007102',
              },
              info: {
                main: '#057d93',
              }
        }
    )
  }
});