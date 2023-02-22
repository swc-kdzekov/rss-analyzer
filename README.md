# rss-analyzer

RSS feed analyzer

This Rest API service analyzes rss feeds (given as Url resources) and returns the top three hot topics based on the matching between the feeds.
For this purpose there are two end-points: /rss/analyse/new, and /rss/frequency. 
The first end-point take rss feed urls, analyzes the content, and stores the results in the in-memory database. It returns a unique identifier under which the analyzed data is stored
The second end-point gets this unique identifier as argument, and returns the top three hot topic with it's frequency of appearance and all the resources where it apeared.
