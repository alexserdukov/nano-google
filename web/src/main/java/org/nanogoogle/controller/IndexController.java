package org.nanogoogle.controller;

import com.google.common.collect.Lists;
import org.nanogoogle.model.SearchDocument;
import org.nanogoogle.service.IndexService;
import org.nanogoogle.service.SearchService;
import org.nanogoogle.model.IndexForm;
import org.nanogoogle.model.SearchForm;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.List;

@Controller
public class IndexController {

    Logger logger = LoggerFactory.getLogger(IndexController.class);
    @Autowired
    IndexService indexService;
    @Autowired
    SearchService searchService;

    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String index(Model model) {
        model.addAttribute("indexForm", new IndexForm());
        return "index";
    }

    @RequestMapping(value = "/index", method = RequestMethod.POST)
    public String index(@ModelAttribute("indexForm") IndexForm indexForm, Model model) {
        String uri = indexForm.getUri();
        logger.debug("Received " + uri + " for indexing");
        try {
            if (validateUrl(uri))
                indexService.index(URI.create(uri), 2);
            else
                model.addAttribute("errorMessage", String.format("URL %s is malformed  ", uri));
        } catch (IOException e) {
            model.addAttribute("errorMessage", "Cannot index uri " + uri);
        }
        model.addAttribute("successMessage", "Uri " + uri + " is indexed");
        return "index";
    }

    @RequestMapping(value = "/", method = RequestMethod.GET)
    public String search(Model model) {
        model.addAttribute("searchForm", new SearchForm());
        return "search";
    }

    @RequestMapping(value = "/search", method = RequestMethod.POST)
    @ModelAttribute("searchResults")
    public List<SearchDocument> search(@ModelAttribute SearchForm searchForm, Model model) {
        try {
            final List<SearchDocument> searchResults = searchService.search(searchForm.getKeyword(), 0, 10);
            logger.debug("Found " + searchResults.size() + " records");
            return searchResults;
        } catch (IOException e) {
            logger.error(e.getLocalizedMessage());
            model.addAttribute("errorMessage", "Failure when searching for keyword " + searchForm.getKeyword());
        }
        return Lists.newArrayList();
    }

    private boolean validateUrl(String uri) {
        try {
            new URL(uri);
            return true;
        } catch (MalformedURLException e) {
            logger.error("URL {} is malformed", uri);
            return false;
        }
    }



}
