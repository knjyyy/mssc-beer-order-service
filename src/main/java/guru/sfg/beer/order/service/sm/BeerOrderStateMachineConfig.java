package guru.sfg.beer.order.service.sm;

import guru.sfg.beer.order.service.domain.BeerOrderEventEnum;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Configuration
@EnableStateMachineFactory
@RequiredArgsConstructor
@Slf4j
public class BeerOrderStateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> {
    private final Action<BeerOrderStatusEnum, BeerOrderEventEnum> validateOrderAction;

    @Override
    public void configure(StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> states) throws Exception {
        states.withStates()
            .initial(BeerOrderStatusEnum.NEW)
            .states(EnumSet.allOf(BeerOrderStatusEnum.class))
            .end(BeerOrderStatusEnum.DELIVERED)
            .end(BeerOrderStatusEnum.PICKED_UP)
            .end(BeerOrderStatusEnum.DELIVERY_EXCEPTION)
            .end(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
            .end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> transitions) throws Exception {
        transitions.withExternal()
                .state(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATION_PENDING).event(BeerOrderEventEnum.VALIDATE_ORDER).action(validateOrderAction)
            .and().withExternal()
                .state(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATED).event(BeerOrderEventEnum.VALIDATION_PASSED)
            .and().withExternal()
                .state(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATION_EXCEPTION).event(BeerOrderEventEnum.VALIDATION_FAILED);
    }

    @Override
    public void configure(StateMachineConfigurationConfigurer<BeerOrderStatusEnum, BeerOrderEventEnum> config) throws Exception {
        StateMachineListenerAdapter<BeerOrderStatusEnum, BeerOrderEventEnum> adapter =new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(State<BeerOrderStatusEnum, BeerOrderEventEnum> from, State<BeerOrderStatusEnum, BeerOrderEventEnum> to) {
                log.info(String.format("stateChanged(from: %s, to: %s)", from, to));
            }
        };
        config.withConfiguration().listener(adapter);
    }
}
