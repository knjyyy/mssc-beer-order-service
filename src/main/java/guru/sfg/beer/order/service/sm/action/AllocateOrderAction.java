package guru.sfg.beer.order.service.sm.action;

import com.spring.brewery.model.events.AllocateOrderRequest;
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

@Slf4j
@RequiredArgsConstructor
@Component
public class AllocateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEventEnum> {
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;
    private final JmsTemplate jmsTemplate;
    @Override
    public void execute(StateContext<BeerOrderStatusEnum, BeerOrderEventEnum> stateContext) {
        System.out.println("Allocate Order Action");
        String beerOrderId = (String) stateContext.getMessageHeaders().get(BeerOrderManagerImpl.HEADER_BEER_ORDER_ID);
        BeerOrder beerOrder = beerOrderRepository.findById(UUID.fromString(beerOrderId)).orElseThrow(NotFoundException::new);

        jmsTemplate.convertAndSend(JmsConfig.QUEUE_ALLOCATE_ORDER, AllocateOrderRequest.builder()
                .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder))
                .build());

        log.debug("Sent allocate request to " + JmsConfig.QUEUE_ALLOCATE_ORDER + " for Order ID: " + beerOrderId);
    }
}
