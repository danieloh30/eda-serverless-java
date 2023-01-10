Setup files for deploying things prior to starting the demo

1. Open a terminal to this directory
2. Log into the cluster via the `oc` cli
3. `oc apply -f 1-operators.yml`
    - This will install the following operators:
        - Red Hat OpenShift Serverless
        - Custom Metrics Autoscaler
        - Red Hat Integration - AMQ Streams
4. Wait for all the operators to finish provisioning
5. `oc apply -f 2-crs.yml`
    - This will install the following CRs
        - `KnativeServing` into the `knative-serving` namespace
        - `KnativeEventing` into the `knative-eventing` namespace
        - `KnativeKafka` into the `knative-eventing` namespace
        - `KedaController` into the `openshift-keda` namespace
6. Wait for everything to spin up
    - Make sure everything is blue in the `knative-serving`, `knative-eventing`, and `openshift-keda` namespaces
7. `export KO_DOCKER_REPO=quay.io/danieloh30`
8. `ko apply -f ../quarkus-eda-knative-demo/kube/01-autoscaler-keda.yaml`
9. `oc apply -f 3-projects.yml`
    - This will stand up the 4 projects
        - `kafka-hpa` (use case 1)
        - `kafka-keda` (use case 2)
        - `kafka-knative` (use case 3)
        - `kafka-keda-knative` (use case 4)
    - It will also install the `Kafka` and `KafkaTopic` CRs into all of the namespaces