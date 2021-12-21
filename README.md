# eresources in Docker

## Prerequisites

1. **[Install Docker](https://www.docker.com/products/docker)**

1. **[Install Drone command line tools](http://readme.drone.io/devs/cli/)**

## Build eresources image

### Clone the eresources repo and build the docker image
    
```
$ cd $HOME/projects/lane
$ git clone git@gitlab.med.stanford.edu:lane/eresources-database.git
$ cd eresources

$ make build
```

## Push to repo

```
$ make push
```

## Pull the latest image from repo

```
$ make pull
```

## CI/CD Support

#### Get and setup personal gitlab access token
Gitlab API requires access token to talk to gitlab server.

Please get your personal token from https://ci.med.stanford.edu/account/token,
and save the the token to ${HOME}/.drone-token file.

_NOTE_: Do not add newline at the end of the token.

#### Setup gitlab pipeline and slack notifcations

```
$ make gl-setup
```