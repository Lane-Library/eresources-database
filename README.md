[![Build Status](https://ci.med.stanford.edu/api/badges/irt-lane/docker-eresources/status.svg)](https://ci.med.stanford.edu/irt-lane/docker-eresources)

# eresources in Docker

## Prerequisites

1. **[Install Docker](https://www.docker.com/products/docker)**

1. **[Install Drone command line tools](http://readme.drone.io/devs/cli/)**

## Build eresources image

### Clone the eresources repo and build app jar
    
```
$ cd $HOME/projects/lane
$ git clone git@gitlab.med.stanford.edu:lane/eresources-database.git
$ cd eresources

$ make app
```

### Build docker image
    
```
$ make docker
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

This repo supports [DroneCI](https://ci.med.stanford.edu/lane/eresources-database).

#### Get and setup personal drone token
Drone CLI requires access token to talk to drone server.

Please get your personal token from https://ci.med.stanford.edu/account/token, 
and save the the token to ${HOME}/.drone-token file. 

_NOTE_: Do not add newline at the end of the token.

#### To turn on the ci job defined in .drone.yml

```
$ make drone-setup
```

