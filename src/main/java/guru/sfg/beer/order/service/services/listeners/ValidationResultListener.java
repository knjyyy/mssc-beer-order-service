package guru.sfg.beer.order.service.services.listeners;

import com.spring.brewery.model.events.ValidateOrderResult;
import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ValidationResultListener {

    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.QUEUE_VALIDATE_ORDER_RESPONSE)
    public void listen (ValidateOrderResult validateOrderResult) {
        final UUID beerOrderId = validateOrderResult.getOrderId();
        log.debug("Validation Result for Order Result for Order Id: " + validateOrderResult);
        beerOrderManager.processValidationResult(beerOrderId, validateOrderResult.isValid());
    }
}
