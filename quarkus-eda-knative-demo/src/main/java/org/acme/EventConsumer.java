package org.acme;

import org.jboss.logging.Logger;

import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

@Path("/")
public class EventConsumer {

  private final Logger logger = Logger.getLogger(EventConsumer.class);

  @POST
  public void handleCloudEvent(String cloudEventJson) {
    logger.info("received event: " + cloudEventJson);
  }

}