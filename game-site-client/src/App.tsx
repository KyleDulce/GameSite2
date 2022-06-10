import React from 'react';
import logo from './logo.svg';
import './App.css';
import {connectAndSubscribeTo, sendTo} from "./socketConnection";
import {CompatClient} from "@stomp/stompjs";

let client: CompatClient;

function App() {
  return (
    <div className="App" onLoad={async () => client = (await connectAndSubscribeTo([
        {
          endpoint: process.env.REACT_APP_STOMP_ENDPOINT,
          callback: function(payload) {
             console.log(payload);
          }
        }]))}>
      <header className="App-header">
        <img src={logo} className="App-logo" alt="logo" />
        <p>
          Edit <code>src/App.tsx</code> and save to reload.
        </p>
        <a
          className="App-link"
          href="https://reactjs.org"
          target="_blank"
          rel="noopener noreferrer"
        >
          Learn React
        </a>
        <button onClick={() => sendTo('/socket/app/test', client,
            {
                user: {
                  uuid: 12,
                  name: 'John',
                  isGuest: false
                },
              gameData: {
                  gameDataIdString: 'test',
                  roomId: 'test',
                  data: {
                    name: 'John'
                  }
              }
            }, {
              myHeader: 'test'
            })}>Hello World</button>
      </header>
    </div>
  );
}

export default App;
