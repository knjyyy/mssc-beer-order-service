package guru.sfg.beer.order.service.services.beer;

import guru.sfg.beer.order.service.services.beer.model.BeerDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;
@Slf4j
@Service
@ConfigurationProperties(prefix = "mssc.brewery", ignoreUnknownFields = false)
public class BeerServiceRestTemplateImpl implements BeerService {
    private final String BEER_PATH = "/api/v1/beer/";
    private final String BEER_UPC_PATH = "/api/v1/beer/upc/";

    private final RestTemplate restTemplate;

    //@Value("${mssc.brewery.beer-service-host}")
    private String beerServiceHost;

    public BeerServiceRestTemplateImpl(RestTemplateBuilder restTemplateBuilder) {
        this.restTemplate = restTemplateBuilder.build();
    }

    @Override
    public Optional<BeerDto> getBeerById(UUID beerId) {
        log.debug("Calling Beer Service - Get Beer By ID");
        return Optional.of(restTemplate.getForObject(beerServiceHost + BEER_PATH + beerId.toString(), BeerDto.class));
    }

    @Override
    public Optional<BeerDto> getBeerByUpc(String upc) {
        log.debug("Calling Beer Service - Get Beer By UPC");
        return Optional.of(restTemplate.getForObject(beerServiceHost + BEER_UPC_PATH + upc, BeerDto.class));
    }

    public void setBeerServiceHost(String beerServiceHost) {
        this.beerServiceHost = beerServiceHost;
    }
}
