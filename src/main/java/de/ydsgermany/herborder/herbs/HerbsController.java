package de.ydsgermany.herborder.herbs;

import java.net.URI;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/herbs")
public class HerbsController {

    HerbsRepository herbsRepository;

    @Autowired
    public HerbsController(HerbsRepository herbsRepository) {
        this.herbsRepository = herbsRepository;
    }

    @GetMapping
    public ResponseEntity<List<Herb>> getHerbs() {
        List<Herb> herbs = herbsRepository.findAll();
        return ResponseEntity.ok()
            .body(herbs);
    }

    //@PostMapping
    //public ResponseEntity<Herb> addHerb(@RequestBody Herb herb) {
    //    Herb savedHerb = herbsRepository.save(herb);
    //    return ResponseEntity
    //        .created(URI.create("http://localhost:8080/herbs/" + savedHerb.getId()))
    //        .body(savedHerb);
    //}

}
