#!/bin/bash

set -euo pipefail

if [ $# -ne 3 ]
  then
    echo "Usage: $0 old-region new-region environment"
    exit 1
fi

DIR=$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )

OLD_REGION=$1
NEW_REGION=$2
ENVIRONMENT=$3

case $ENVIRONMENT in
  prod)
    STACK_SUFFIX=
    ;;
  *)
    STACK_SUFFIX=-$ENVIRONMENT
    ;;
esac

$DIR/deploy-infrastructure.sh create $NEW_REGION $ENVIRONMENT

aws cloudformation delete-stack \
  --region $OLD_REGION \
  --stack-name liquidity-dns$STACK_SUFFIX

aws cloudformation delete-stack \
  --region $OLD_REGION \
  --stack-name liquidity-service$STACK_SUFFIX

aws cloudformation wait stack-delete-complete \
  --region $OLD_REGION \
  --stack-name liquidity-dns$STACK_SUFFIX

aws cloudformation wait stack-delete-complete \
  --region $OLD_REGION \
  --stack-name liquidity-service$STACK_SUFFIX

$DIR/deploy-dns.sh create $NEW_REGION $ENVIRONMENT

JOURNAL_DIR=$(mktemp --directory)

$DIR/save-journal.sh $OLD_REGION $ENVIRONMENT $JOURNAL_DIR

$DIR/load-journal.sh $NEW_REGION $ENVIRONMENT $JOURNAL_DIR

rm \
  --recursive \
  --force \
  $JOURNAL_DIR

$DIR/../cd-scripts/deploy-service.sh create $NEW_REGION $ENVIRONMENT

aws cloudformation delete-stack \
  --region $OLD_REGION \
  --stack-name liquidity-infrastructure$STACK_SUFFIX

aws cloudformation wait stack-delete-complete \
  --region $OLD_REGION \
  --stack-name liquidity-infrastructure$STACK_SUFFIX
