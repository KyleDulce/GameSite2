import { Suspense, useContext, } from "react";
import { RouterProvider, Outlet, Route, Navigate, useLocation, createBrowserRouter, createRoutesFromElements } from 'react-router-dom';
import { ConfigContext } from "./ts/model/ConfigOptions";
import { removeAttributeFromRoot, setAttributeToRoot } from "./ts/services/NativeHtmlService";
import { Box, CircularProgress, CssBaseline, createTheme, useMediaQuery } from "@mui/material";
import { DARK_MODE_HTML_ATTRIBUTE } from "./ts/model/SystemConstants";
import { ThemeProvider } from "@emotion/react";
import Home from "./ts/pages/Home";
import MainHeader from "./ts/components/MainHeader";
import React from "react";
import LoginPage from "./ts/pages/LoginPage";
import ActiveGames from "./ts/pages/ActiveGames";
import CreateGames from "./ts/pages/CreateGames";
import PlayGame from "./ts/pages/PlayGame";
import "./App.scss";
import { getThemeOptions } from "./Theme";

export const ResponsiveContext = React.createContext(false);

const PageLayout = () => {
  const location = useLocation();
  const {AuthToken} = useContext(ConfigContext);
  if(location.pathname !== "/login" && process.env.REACT_APP_IGNORE_AUTH === "false" && AuthToken == undefined) {
    return <Navigate to={"/login"} replace={true}/>
  }
  return (
    <Box className="app-enclosing">
      {location.pathname !== "/login" && (<MainHeader/>)}
      <Outlet />
    </Box>
  )};

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route element={<PageLayout />}>
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/rooms" element={<ActiveGames />} />
      <Route path="/createroom" element={<CreateGames />} />
      <Route path="/play" element={<PlayGame />} />
    </Route>
  )
);

export default function App() {
  const {UseLightMode, setUseLightMode} = useContext(ConfigContext);
  const isMobile = useMediaQuery('(max-width:768px)');

  const theme = React.useMemo(
    () => {
      if(UseLightMode) {
        removeAttributeFromRoot(DARK_MODE_HTML_ATTRIBUTE);
      } else {
        setAttributeToRoot(DARK_MODE_HTML_ATTRIBUTE);
      }
      return createTheme(getThemeOptions(UseLightMode ? 'light' : 'dark'))
    },
    [UseLightMode, setUseLightMode]
  );
  
  return (
    <ThemeProvider theme={theme}>
      <CssBaseline />
      <ResponsiveContext.Provider value={isMobile}>
        <Suspense fallback={<CircularProgress />} >
          <RouterProvider router={router} />
        </Suspense>
      </ResponsiveContext.Provider>
    </ThemeProvider>
  );
}
