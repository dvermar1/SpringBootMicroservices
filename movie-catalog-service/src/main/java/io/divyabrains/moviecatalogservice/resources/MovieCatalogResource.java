package io.divyabrains.moviecatalogservice.resources;

import io.divyabrains.moviecatalogservice.models.CatalogItem;
import io.divyabrains.moviecatalogservice.models.Movie;
import io.divyabrains.moviecatalogservice.models.Rating;
import io.divyabrains.moviecatalogservice.models.UserRating;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/catalog")
public class MovieCatalogResource {

    @Autowired
    private RestTemplate restTemplate;

    @RequestMapping("/{userId}")
    public List<CatalogItem> getCatalog(@PathVariable("userId") String userId) {

        UserRating ratings = restTemplate.getForObject("http://ratings-data-service/ratingsdata/users/" + userId,UserRating.class);

        return ratings.getUserRating().stream().map(rating ->  {
            //For each movie id, call movie info service and get details
            Movie movie = restTemplate.getForObject("http://movie-info-service/movies/" + rating.getMovieId(), Movie.class);
            //Put them all together
            return new CatalogItem(movie.getName(), "Desc", rating.getRating()) ;
        })
                .collect(Collectors.toList());
    }
}
