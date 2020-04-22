[![Build Status](https://ci.med.stanford.edu/api/badges/lane/eresources-database/status.svg)](https://ci.med.stanford.edu/lane/eresources-database)

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

This repo supports [DroneCI](https://ci.med.stanford.edu/lane/eresources-database).

#### Get and setup personal drone token
Drone CLI requires access token to talk to drone server.

Please get your personal token from https://ci.med.stanford.edu/account/token, 
and save the the token to ${HOME}/.drone-token file. 

_NOTE_: Do not add newline at the end of the token. Use:

```
echo -n <token> > ${HOME}/.drone-token
```


#### To turn on the ci job defined in .drone.yml

```
$ make drone-setup
```

