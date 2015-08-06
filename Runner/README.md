Cicada Chaingun Runner
======================

A dockerized multi-threaded load test runner

Usage
-----

To experiment locally, launch the server:

    $ lein run
    Load test runner listening on port 3000

Then use curl (or your favorite http client) to POST a load test:

    curl 127.0.0.1:3000/load-test -d @load-test-example-1.edn

Then view the report:

    curl 127.0.0.1:3000/load-test
    [{:request-time 952,
      :status 200,
      :url "http://www.yodle.com",
      :thread 1,
      :run 0}
     {:request-time 952,
      :status 200,
      :url "http://www.yodle.com",
      :thread 0,
      :run 0}
     {:request-time 807,
      :status 200,
      :url "http://www.yodle.com",
      :thread 0,
      :run 1}
     {:request-time 1166,
      :status 200,
      :url "http://www.yodle.com",
      :thread 1,
      :run 1}]

It is possible to get the report before the runner is done, in which case it will return an incomplete snapshot of results.
