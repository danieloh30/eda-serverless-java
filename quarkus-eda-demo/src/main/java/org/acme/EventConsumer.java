package org.acme;

import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Incoming;
import org.jboss.logging.Logger;

import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class EventConsumer {

  private final Logger logger = Logger.getLogger(EventConsumer.class);

  @Incoming("my-topic")
  public void receive(Record<String, String> record) {
    logger.infof("Received evnt: " + record.value());
  }

}