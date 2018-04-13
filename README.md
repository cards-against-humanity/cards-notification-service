# Cards Notification Service

A dockerizable notification service for cards against humanity based on socket.io

## How to use it

The project contains all configuration necesary to build a docker image by simply pulling the repo, opening up the main project folder in the command line, and running ```docker build .```

### Settings

| Environment Variable | Required | Default | Description |
| --- | --- | --- | --- |
| API_URL | NO | localhost | Cards API url |
| API_PORT | NO | 80 | Cards API port |
| SOCKET_IO_PORT | NO | 8080 | Port for socket.io server to bind to |

To set an environment variable, the following syntax must be followed: ```[VARIABLE_NAME]=[VALUE]```
