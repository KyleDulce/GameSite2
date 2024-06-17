export const environment = {
  production: false,
  backendUrl: 'http://localhost:8080',
  stompEndpoint: '/socket/stomp',
  restEndpoint: '/api',
  useSessionStorage: true,
  maxChatMessages: 200,
  gameRefreshTokenIntervalSeconds: 1800,
  //autoLogin: "SecretDeveloperUser,SimpleDeveloperPassword",
  autoLogin: null,
  ignoreAuth: false, // set to true to ignore auth
  snackbarTimeMillis: 5000
};
