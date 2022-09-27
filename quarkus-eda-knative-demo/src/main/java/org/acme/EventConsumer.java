package org.acme;

import org.jboss.logging.Logger;

import javax.inject.Singleton;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

@Path("/")
@Singleton
public class EventConsumer {

  private final Logger logger = Logger.getLogger(EventConsumer.class);

  @POST
  public void handleCloudEvent(String cloudEventJson) {
    logger.info("received event: " + cloudEventJson);
  }

}