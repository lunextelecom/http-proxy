CREATE KEYSPACE http_proxy WITH REPLICATION = { 'class' : 'NetworkTopologyStrategy', 'dc1' : 2 };
CREATE TABLE http_proxy.logging (target text, url text, updateid timeuuid, client text, method text, request_header text, request_body text, response_body text, PRIMARY KEY (target, url, updateid)) WITH CLUSTERING ORDER BY (url ASC, updateid DESC);
CREATE TABLE http_proxy.endpoint (target text, status int, updateid timeuuid, PRIMARY KEY (target));
