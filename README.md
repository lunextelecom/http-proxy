http-proxy
==========
  Aggregate multiple webservice endpoint  
  Loadbalancing  
  Authentication - Basic(TBD)  
  IP Filtering (TBD)  
  Logging + Metric  
  Monitoring  

## Implemention and requirement
  Netty and NIO  
  No storage in sql. Only depends on cassandra on runtime.  
  Simple configuration(yaml).  Configuration should be able to refresh without restart  

## Closing of sockets
  Request and endpoint determine the closing of socket, if neither endpoint are close, the connection will stay open for a period of 60 second.

## Aggregate multiple webservice endpoint
  As as frontend to different webservice.  This does not modify http parameter, body.  It will just passthru to servers behind in the configuration.  There will be default server and


## Loadbalancing
  Simple round robin to server that are alive.  HTTP ping server to determine if they are alive.  Server that does not return http 200 within 2 consecutive ping will be marked as down.

## Logging
  By default all message are log and metric are send to graphite.  
  Specific url pattern can be specific to disable logging.  

## Metric
Metric can be enable. Currently using graphite metric
```
#eg. 25ms get request to catalog1 catalog1.get_request_200 25
```  
## Heath Monitoring
Local accessible port to do the following:  
- connections open per endpoint, url  
- request handled per endpoint, url  
- list of endpoint(alive, down)  + the count of request

## Sample Configuration
```
##Default for route
route_default:	
	logging: req, req_body(POST,PUT), resp_body(POST)
	metric: {server}.{verb}_{name}_{response_code}	
	balancer: rr #this is in the case user do not config server.
	health: None #this is in the case user do not config server.

##Default for server
server_default:
	balancer: rr
	health: GET /health

## Configuration begin here

servers:
	#name: name of servers
	#target: list of server	
	- name: did_server
	  target: [192.168.93.100,192.168.93.101]
	- name: pos_server
	  target: [192.168.93.100:9090,192.168.93.101:9090]

routes:	
	#using
	#name: name of this route
	#url: {verb} {regex of url}
	#target: can be defined server or just ip
	#logging: off, req, req_header, req_body, resp_header, resp_body
	#		can also max in verb, so req(POST,PUT) or req(*) == req
	- name: did
	  url: * /didv2/dids.*
	  target: did_server
	  logging: off

	- name: new_order
	  url: POST /pos/.+/orders/.+
	  target: pos_server
	  metric: {server}.{name}_{response_code}

#match /product, /products, /sku, skus
	- name: catalog
	  url: * /(product(?=s| )|sku(?=s| ))/.*
	  target: 192.168.93.107 #balancer, health is default from route_default

#map all url to old server
#logging all req, req
#target can also point directly to an ip instead of server
	- name: unmapped
	  url: * .*
	  target: 192.168.93.102:8080 	  
	  logging: req

```


