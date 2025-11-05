# Use the official WildFly image as a base
FROM quay.io/wildfly/wildfly:34.0.1.Final-jdk21

# Copy the built EAR file from the target directory into the WildFly deployments folder
COPY ./ear/target/service-change.ear /opt/jboss/wildfly/standalone/deployments/

# Add application user
RUN /opt/jboss/wildfly/bin/add-user.sh -a \
    -u applicationAdmin \
    -p admin123 \
    --silent

# Add management user
RUN /opt/jboss/wildfly/bin/add-user.sh \
    -u managementAdmin \
    -p admin123 \
    -g admin \
    -r ManagementRealm \
    --silent

# Expose HTTP and management ports
EXPOSE 8080 9990 4447

# Start WildFly server and bind it to all network interfaces to be accessible from outside the container
CMD ["/opt/jboss/wildfly/bin/standalone.sh", "-b", "0.0.0.0", "-bmanagement", "0.0.0.0"]
