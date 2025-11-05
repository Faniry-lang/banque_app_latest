mvn clean install
docker cp ./ear/target/service-change.ear service-change-container:/opt/jboss/wildfly/standalone/deployments/