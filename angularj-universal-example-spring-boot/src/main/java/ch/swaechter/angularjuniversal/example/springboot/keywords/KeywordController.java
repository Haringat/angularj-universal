package ch.swaechter.angularjuniversal.example.springboot.keywords;

import ch.swaechter.angularjuniversal.keywords.Keyword;
import ch.swaechter.angularjuniversal.keywords.KeywordService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * This class is responsible for providing all keywords.
 *
 * @author Simon Wächter
 */
@RestController
@RequestMapping("/api")
public class KeywordController {

    private final KeywordService keywordservice;

    /**
     * Default constructor.
     */
    public KeywordController() {
        this.keywordservice = new KeywordService();
    }

    /**
     * Get all keywords.
     *
     * @return All keywords
     */
    @GetMapping("/keyword")
    public ResponseEntity<List<Keyword>> getKeywords() {
        return new ResponseEntity<>(keywordservice.getKeywords(), HttpStatus.OK);
    }
}
