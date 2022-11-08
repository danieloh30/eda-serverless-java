# Event-driven autoscaling for Serverless Java

## Install KEDA v2

Deploying with Operator Hub - https://keda.sh/docs/2.8/deploy/#operatorhub

## knative-evnenting namespace

export KO_DOCKER_REPO=YOUR_CONTAINER_REGISTRY

For example, `export KO_DOCKER_REPO=quay.io/danieloh30`

ko apply -f quarkus-eda-knative-demo/kube/01-autoscaler-keda.yaml

## KnativeKafka

```
apiVersion: operator.serverless.openshift.io/v1alpha1
kind: KnativeKafka
metadata:
  name: knative-kafka
  namespace: knative-eventing
spec:
  broker:
    enabled: false
  channel:
    enabled: false
  sink:
    enabled: true
  source:
    enabled: true
```