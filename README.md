
# Eresources Indexing

## Background

ETL for all Lane Search data sources (FOLIO, PubMed, etc.).

Jobs run on a configurable schedule or on-demand via an [admin interface](http://localhost:8080/).

## Prerequisites

1. **[Install Docker](https://www.docker.com/products/docker)**

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

Please get your personal token from https://gitlab.med.stanford.edu/-/profile/personal_access_tokens
and save the the token to ${HOME}/.gitlab-token file.

```
echo -n <token> > ${HOME}/.gitlab-token
```

_NOTE_: Do not add newline at the end of the token.


#### Setup gitlab pipeline and slack notification

```
$ make gl-setup
```
