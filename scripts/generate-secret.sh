#!/bin/bash

case $1 in
  secret)
    openssl enc -aes-256-cbc -k $2 -P -md sha1 | grep "key=" | cut -d "=" -f 2 | clip
    ;;

  rand)
    secret=$(openssl rand -base64 64) || exit
    echo "${secret//[$'\t\r\n ']}" | clip
    ;;
esac

