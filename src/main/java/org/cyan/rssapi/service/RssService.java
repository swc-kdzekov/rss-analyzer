package org.cyan.rssapi.service;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.apache.commons.validator.routines.UrlValidator;
import org.cyan.rssapi.callable.FeedCallable;
import org.cyan.rssapi.exceptions.UrlsArgumentException;
import org.cyan.rssapi.model.HotRss;
import org.cyan.rssapi.model.ElementInfo;
import org.cyan.rssapi.model.HotRssDetails;
import org.cyan.rssapi.model.HotRssRespDetail;
import org.cyan.rssapi.model.HotRssResponse;
import org.cyan.rssapi.model.RssFeed;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class RssService {

    private JpaRssRepository jpaRssRepository;
    private JpaRssDetailsRepository jpaRssDetailsRepository;
    private FeedProvider feedProvider;

    public RssService(JpaRssRepository jpaRssRepository, JpaRssDetailsRepository jpaRssDetailsRepository, FeedProvider feedProvider) {
        this.jpaRssRepository = jpaRssRepository;
        this.jpaRssDetailsRepository = jpaRssDetailsRepository;
        this.feedProvider = feedProvider;
    }
    private static final int NUMBER_OF_TOP_NEWS = 3;

    public List<HotRssResponse> getMostFrequentTopics(String id) {
        Pageable sortedByFrequency = PageRequest.of(0, NUMBER_OF_TOP_NEWS, Sort.by("frequency").descending());
        List<HotRss> rssList = jpaRssRepository.findTopRssTopicsById(id, sortedByFrequency);
        return rssList.stream()
                .map(el -> HotRssResponse.builder()
                        .element(el.getElement())
                        .frequency(el.getFrequency())
                        .details(getElementDetails(id, el.getElement()))
                        .build())
                .collect(Collectors.toList());
    }

    private List<HotRssRespDetail> getElementDetails(String id, String element) {
        List<HotRssDetails> rssDetails = jpaRssDetailsRepository.findTopRssTopicDetails(id, element);
        return rssDetails.stream()
                .map(rssD -> new HotRssRespDetail(rssD.getTitle(), rssD.getReference()))
                .collect(Collectors.toList());
    }

    public String analyzeRssFeeds(String[] urls)
            throws IOException, FeedException, ExecutionException, InterruptedException {

        Map<String, ElementInfo> matchedElements = new HashMap<>();

        ExecutorService executor = Executors.newFixedThreadPool(urls.length);

        Future<RssFeed>[] arrayFutures = new Future[urls.length];

        int index = 0;
        for (String url : urls) {

            SyndFeed feed = feedProvider.getFeedFromUrlResource(url);

            FeedCallable fc = new FeedCallable(feed);
            final Future<RssFeed> future = executor.submit(fc);
            arrayFutures[index]=future;

            index++;
        }

        executor.shutdown();
        while (!executor.isTerminated()) {}

        for (int i = 0; i < arrayFutures.length - 1; i++) {
            findMatchingElements(arrayFutures[i], arrayFutures, i + 1, matchedElements);
        }

        return storeMatchingElements(matchedElements);
    }

    private String storeMatchingElements(Map<String, ElementInfo> matchedElements) {
        UUID uuid = UUID.randomUUID();

        matchedElements.entrySet().forEach(me -> {
            HotRss hotRss = new HotRss(uuid.toString(), me.getKey(), me.getValue().getFrequency());
            jpaRssRepository.save(hotRss);
            storeMatchingElementsDetails(
                    uuid.toString(),
                    me.getKey(),
                    me.getValue().getTitles(),
                    me.getValue().getReferences()
            );
        });

        return uuid.toString();
    }

    private void storeMatchingElementsDetails(String rssId, String element, List<String> titles, List<String> links) {

        Iterator<String> titleIterator = titles.iterator();
        Iterator<String> linkIterator = links.iterator();
        List<HotRssDetails> listHotRssDetails = new ArrayList<>();

        while (titleIterator.hasNext() && linkIterator.hasNext()) {
            HotRssDetails hotRssDetails = new HotRssDetails(rssId, element, titleIterator.next(), linkIterator.next());
            listHotRssDetails.add(hotRssDetails);
        }
        jpaRssDetailsRepository.saveAll(listHotRssDetails);
    }

    private void findMatchingElements(
            Future<RssFeed> rssFeedPrev,
            Future<RssFeed>[] arrayRssFeed,
            int index,
            Map<String, ElementInfo> matchedElements
    ) throws ExecutionException, InterruptedException {
        for (String kWord : rssFeedPrev.get().getKeyWordToInfo().keySet()) {

            boolean matched = false;
            int frequency = rssFeedPrev.get().getKeyWordToInfo().get(kWord).getFrequency();
            List<String> titles = new ArrayList<>(rssFeedPrev.get().getKeyWordToInfo().get(kWord).getTitles());
            List<String> references = new ArrayList<>(rssFeedPrev.get().getKeyWordToInfo().get(kWord).getReferences());

            if (matchedElements.get(kWord) != null) {
                continue;
            }
            for (int j = index; j < arrayRssFeed.length; j++) {
                RssFeed rssFeedNext = arrayRssFeed[j].get();
                if (rssFeedNext.getKeyWordToInfo().keySet().contains(kWord)) {
                    matched = true;
                    if (matchedElements.get(kWord) != null) {
                        frequency = matchedElements.get(kWord).getFrequency();
                    }
                    frequency += rssFeedNext.getKeyWordToInfo().get(kWord).getFrequency();
                    titles.addAll(rssFeedNext.getKeyWordToInfo().get(kWord).getTitles());
                    references.addAll(rssFeedNext.getKeyWordToInfo().get(kWord).getReferences());
                }
            }
            if (matched) {
                matchedElements.put(
                        kWord,
                        ElementInfo.builder()
                                .word(kWord)
                                .frequency(frequency)
                                .titles(titles)
                                .references(references)
                                .build()
                );
            }
        }
    }

    public void validateResource(String[] urls) {

        if (urls.length < 2) {
            throw new UrlsArgumentException("Please provide more RSS resources!");
        }

        for (String url : urls) {
            if (!isValidURL(url)) {
                throw new UrlsArgumentException("Not valid Url address: '" + url + "'!");
            }
        }

        for (String url : urls) {
            if (!isValidRssResource(url)) {
                throw new UrlsArgumentException("The provided Url resource: '" + url + "' is not valid RssFeed!");
            }
        }
    }

    private boolean isValidURL(String url) {
        UrlValidator validator = new UrlValidator();
        return validator.isValid(url);
    }

    private boolean isValidRssResource(String feedUrl) {
        SyndFeedInput input = new SyndFeedInput();
        try {
            input.build(new XmlReader(new URL(feedUrl)));
            return true;
        } catch (IOException | FeedException e) {
            return false;
        }
    }
}
