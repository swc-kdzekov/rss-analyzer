# rss-analyser

RSS feed analyser

This Rest API service analyses rss feeds (given as Url resources) and returns the top three hot topics based on the matching between the feeds, and the frequency of their appearance.
For that purpose there are two end-points: '*/rss/analyse/new*', and '*/rss/frequency*'. 
The first end-point takes rss feed urls, analyses the content, and stores the results in the in-memory database. In the end, it returns a unique identifier under which the analysed data is stored.
The second end-point gets this unique identifier as argument, and returns the top three hot topics along with it's frequency of appearance, and all the resources (titles and links) where it appeared.

The call for the first end-point:


```
curl --location --request POST 'http://{host}:{port}/rss/analyse/new?urls=url1,url2,..urln'
```

Example:

```
curl --location --request POST 'http://localhost:8080/rss/analyse/new?urls=https://www.theguardian.com/us-news/rss,https://news.google.com/news?cf=all%26hl=en%26pz=1%26ned=us%26output=rss'
```

**The urls should be comma separated and no spaces between!**


As a result of the execution, if there are matchings, the endpoint stores the analysed data and returns a unique identifier (for example: *9ffdf5c0-6512-458b-9b2a-aae0b6617902*). In the following API call this identifier is used as an input argument in order to get the most frequent (or hot) rss topics.


The call for the second end-point:


```
curl --location 'http://{host}:{port}/rss/frequency?id=unique_identifier'
```

Example:

```
curl --location 'http://localhost:8080/rss/frequency?id=9ffdf5c0-6512-458b-9b2a-aae0b6617902'
```
<br/>
The branch *'rss-analyser/threads'* allows you to use Swagger-UI for interacting with the API! For that purpose you can use this reference: http://localhost:8080/swagger-ui.html
