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

######Example: 
```
In this example all incoming request going start with http url /products/ will be loadbalance to catalog1-3 and /login/ to auth1-2.  All remain service goes to default1, default2.

.*		 		= default1, default2
^/products/.*	= catalog1, catalog2, catalog3
^/login/.*		= auth1, auth2
```  

## Loadbalancing
  Simple round robin to server that are alive.  HTTP ping server to determine if they are alive.  Server that does not return http 200 within 2 consecutive ping will be marked as down.

```
default1 		= 192.168.93.100:8080
default1.ping 	= GET /ping
```

## Logging
  By default all message are log and metric are send to graphite.  
  Specific url pattern can be specific to disable logging.  
  graphite metric defaults to 'default' but can be more specific by using regular expression.  

```
#Logging
#URL(regex)	verb	= options
.*	GET				= on #log request only no header and no response
.*					= req, req_header, req_body, resp_body 
^/login/.* 			= off #do not login authentication request


#Metric naming
#URL(regex)	verb = metric
#accessible variable
#server, verb, response_code
.* 					= {server}.{verb}_request_{response_code} 
^/products/.* GET	= {server}.list_products
^/products/.* POST	= {server}.create_product

#eg. 25ms get request to catalog1 catalog1.get_request_200 25

```  
##Monitoring
  Local accessible port to do the following:  
  # connections open per endpoint, url  
  # request handled per endpoint, url  
  # list of endpoint(alive, down)  + the count of request

  


