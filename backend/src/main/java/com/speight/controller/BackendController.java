package com.speight.controller;

import com.speight.domain.User;
import com.speight.exception.UserNotFoundException;
import com.speight.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController()
@RequestMapping("/api")
public class BackendController {

    private static final Logger LOG = LoggerFactory.getLogger(BackendController.class);

    public static final String HELLO_TEXT = "Hello from Spring Boot Backend!";
    public static final String SECURED_TEXT = "Hello from the secured resource!";

    @Autowired
    private UserRepository userRepository;

    @RequestMapping(path = "/hello")
    public @ResponseBody String sayHello() {

        LocalDate start = LocalDate.now();
        LocalDate end = LocalDate.now().plusMonths(1).with(TemporalAdjusters.lastDayOfMonth());






        LocalDateTime Zstart= LocalDateTime.now();
        LocalDateTime lastQuarter = Zstart.truncatedTo(ChronoUnit.HOURS)
                .plusMinutes(((Zstart.getMinute() + 29) / 30) * 30);
        LocalDateTime Zend= LocalDateTime.now().plusDays(5).with(TemporalAdjusters.lastDayOfMonth());

        LocalDateTime date = LocalDateTime.of(2017,12,3,6,30);

        List<LocalDateTime> zdates=Stream.iterate(Zstart, something -> something.plusMinutes(30))
                .limit(ChronoUnit.DAYS.between(Zstart,Zend)*24).
                 filter(test ->test.getDayOfWeek()!= DayOfWeek.FRIDAY).collect(Collectors.toList());



        List<LocalDate> dates = Stream.iterate(start, test -> test.plusDays(1))
                .limit(ChronoUnit.DAYS.between(start, end))
                .collect(Collectors.toList());


        LOG.info("GET called on /hello resource");
        return HELLO_TEXT;
    }

    @RequestMapping(path = "/user/{lastName}/{firstName}", method = RequestMethod.POST)
    @ResponseStatus(HttpStatus.CREATED)
    public @ResponseBody long addNewUser (@PathVariable("lastName") String lastName, @PathVariable("firstName") String firstName) {
        User savedUser = userRepository.save(new User(firstName, lastName));

        LOG.info(savedUser.toString() + " successfully saved into DB");

        return savedUser.getId();
    }

    @GetMapping(path = "/user/{id}")
    public @ResponseBody User getUserById(@PathVariable("id") long id) {

        return userRepository.findById(id).map(user -> {
            LOG.info("Reading user with id " + id + " from database.");
            return user;
        }).orElseThrow(() -> new UserNotFoundException("The user with the id " + id + " couldn't be found in the database."));
    }

    @RequestMapping(path="/secured", method = RequestMethod.GET)
    public @ResponseBody String getSecured() {
        LOG.info("GET successfully called on /secured resource");
        return SECURED_TEXT;
    }

    // Forwards all routes to FrontEnd except: '/', '/index.html', '/api', '/api/**'
    // Required because of 'mode: history' usage in frontend routing, see README for further details
    @RequestMapping(value = "{_:^(?!index\\.html|api).$}")
    public String redirectApi() {
        LOG.info("URL entered directly into the Browser, so we need to redirect...");
        return "forward:/";
    }

}
