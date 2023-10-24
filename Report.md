Assignment 1
------------

# Team Members

# GitHub link to your (forked) repository

...

# Task 1

1. Indicate the time necessary for the SimpleCrawler to work.

Ans: 46 Seconds



# Task 2

1. Is the flipped index smaller or larger than the initial index? What does this depend on?

Ans: it is larger, this is the case since each link can have up to 3 distinct words. It could be the case that with a different set of
webpages the flipped index could be smaller if in more pages the same words are used.

# Task 3

1. Explain your design choices for the API design.

Ans: For the API, we have gone with a delete request for /delete and put for /launch, /regenerate and /update.
One Point of discussion was whether our responses should also have a content, for example the updated index
as part of the 200 response for /updated (UrlArray). But since it is not needed in the exercise, we decided not
to include any content for responses. For /delete and /update we added a second parameter, because we needed
both the url and the index filename as parameters. Since every request used at least one url or index filename
parameters, all of them could have 400 or 404 responses.

# Task 4

1.  Indicate the time necessary for the MultithreadCrawler to work.

Ans: 5 seconds

3. Indicate the ratio of the time for the SimpleCrawler divided by the time for the MultithreadedCrawler to get the increase in speed.

Ans: 9.2 Times faster

Note for Task 4: When running the test, the Crawler finishes its task and creates and fills the index.csv. The MultiCrawlerTest though seems to enter an infinite loop afterwards. 
We could not find out what the issue is, but since the crawler finishes its task and passes the test, we decided to leave it as it is, especially since the loop seems to happen outside of our code.

