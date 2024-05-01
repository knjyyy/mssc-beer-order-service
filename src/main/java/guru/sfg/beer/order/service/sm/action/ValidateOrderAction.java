package guru.sfg.beer.order.service.sm.action;

import com.spring.brewery.model.events.ValidateOrderRequest;
import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import guru.sfg.beer.order.service.web.controllers.NotFoundException;
import guru.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class ValidateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final JmsTemplate jmsTemplate;

    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        System.out.println("Validate Order Action");
        String beerOrderId = (String) stateContext.getMessageHeaders().get(BeerOrderManagerImpl.HEADER_BEER_ORDER_ID);
        BeerOrder beerOrder = beerOrderRepository.findById(UUID.fromString(beerOrderId)).orElseThrow(NotFoundException::new);

        jmsTemplate.convertAndSend(JmsConfig.QUEUE_VALIDATE_ORDER, ValidateOrderRequest.builder()
                .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder))
                .build());

        log.debug("Sent validation request to " + JmsConfig.QUEUE_VALIDATE_ORDER + " for Order ID: " + beerOrderId);
    }
}
