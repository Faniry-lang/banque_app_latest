cd api
mvn clean install
cd ../service-compte
mvn clean install wildfly:deploy
cd ../service-central
mvn clean install wildfly:deploy
cd ../service-change
mvn clean install
docker cp ./ear/target/service-change.ear service-change-container:/opt/jboss/wildfly/standalone/deployments/