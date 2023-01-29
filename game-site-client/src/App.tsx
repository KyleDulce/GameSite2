import { Suspense } from "react";
import { RouterProvider, createBrowserRouter, createRoutesFromElements, Outlet, Route, Routes } from 'react-router-dom';

import { CircularProgress } from "@mui/material";

import Home from "./pages/Home";
import MainHeader from "./components/MainHeader";

const PageLayout = () => (
  <>
    <MainHeader />
    <Outlet />
  </>
);

const router = createBrowserRouter(
  createRoutesFromElements(
    <Route element={<PageLayout />}>
      <Route path="/" element={<Home />} />
    </Route>
  )
);

function App() {
  return (
    <>
      <Suspense fallback={<CircularProgress />} >
        <RouterProvider router={router} />
      </Suspense>
    </>
  );
}

export default App;
