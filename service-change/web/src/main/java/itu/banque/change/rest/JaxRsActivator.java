package itu.banque.change.rest;

import jakarta.ws.rs.ApplicationPath;
import jakarta.ws.rs.core.Application;

@ApplicationPath("/api")
public class JaxRsActivator extends Application {
    // This class activates JAX-RS.
    // The @ApplicationPath annotation defines the base URI for all JAX-RS resources.
}
